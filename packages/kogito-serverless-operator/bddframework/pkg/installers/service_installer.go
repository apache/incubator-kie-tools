/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package installers

import (
	"fmt"
	"sync"
	"time"

	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
)

var (
	waitForAllCrsRemovalTimeout = 1 * time.Minute

	// Map of installed services for namespaces, contains slices of service installers installing services in those namespaces
	installedNamespacedServices sync.Map
	// Map of cluster wide installed services
	installedClusterWideServices sync.Map
)

// ServiceInstaller is the API to install services
type ServiceInstaller interface {
	// Install the service into cloud to serve the namespace
	Install(namespace string) error
	// Return all CRs of this service which exists in this namespace
	getAllCrsInNamespace(namespace string) ([]client.Object, error)
	// Returns service name for logging purposes
	getServiceName() string
	// Cleanup the namespace from service CRs. This functionality is needed because of Kogito service KogitoInfra object.
	// KogitoInfra can exists with or without owner and when owner is deleted then KogitoInfra will remain.
	// Other services just remove CRs without owner and that will remove all other CRs.
	// Returns true in case of success, false in case of some error, logging is handled within the function.
	cleanupCrsInNamespace(namespace string) bool
}

// ClusterWideServiceInstaller is the API of cluster wide services
type ClusterWideServiceInstaller interface {
	ServiceInstaller
	// Uninstall service from whole cluster
	uninstallFromCluster() error
}

// NamespacedServiceInstaller is the API of namespaced services
type NamespacedServiceInstaller interface {
	ServiceInstaller
	// Uninstall service from specific namespace
	uninstallFromNamespace(namespace string) error
}

// Generic API for all services

// UninstallServicesFromNamespace uninstalls all services from specific namespace. Returns false in case of any error while uninstalling.
func UninstallServicesFromNamespace(namespace string) (success bool) {
	success = true

	// Delete all CRs without owner
	if ok := executeOnClusterWideServices(func(si ClusterWideServiceInstaller) bool {
		return si.cleanupCrsInNamespace(namespace)
	}); !ok {
		success = false
	}
	if ok := executeOnNamespacedServices(namespace, func(si NamespacedServiceInstaller) bool {
		return si.cleanupCrsInNamespace(namespace)
	}); !ok {
		success = false
	}

	// Wait until all CRs are removed
	if ok := executeOnClusterWideServices(func(si ClusterWideServiceInstaller) bool {
		return waitForAllCrsRemoval(si, namespace, waitForAllCrsRemovalTimeout)
	}); !ok {
		success = false
	}
	if ok := executeOnNamespacedServices(namespace, func(si NamespacedServiceInstaller) bool {
		return waitForAllCrsRemoval(si, namespace, waitForAllCrsRemovalTimeout)
	}); !ok {
		success = false
	}

	// Remove namespaced services
	servicesNotDeleted := []NamespacedServiceInstaller{}
	if ok := executeOnNamespacedServices(namespace, func(si NamespacedServiceInstaller) bool {
		if err := si.uninstallFromNamespace(namespace); err != nil {
			framework.GetLogger(namespace).Error(err, "Error uninstalling service from namespace", "service name", si.getServiceName())
			servicesNotDeleted = append(servicesNotDeleted, si)
			return false
		}
		return true
	}); !ok {
		success = false
	}

	// Add not removed services back to the list
	installedNamespacedServices.Store(namespace, servicesNotDeleted)

	return success
}

// UninstallServicesFromCluster uninstalls the cluster wide service from whole cluster. Returns false in case of any error while uninstalling.
func UninstallServicesFromCluster() (success bool) {
	success = true

	servicesDeleted := []ClusterWideServiceInstaller{}
	if ok := executeOnClusterWideServices(func(si ClusterWideServiceInstaller) bool {
		if err := si.uninstallFromCluster(); err != nil {
			framework.GetMainLogger().Error(err, "Error uninstalling service from cluster", "service name", si.getServiceName())
			return false
		}
		servicesDeleted = append(servicesDeleted, si)
		return true
	}); !ok {
		success = false
	}

	// Remove deleted services from list
	for si := range servicesDeleted {
		installedClusterWideServices.Delete(si)
	}

	return success
}

// YAML namespaced service specification

// Make sure that YamlNamespacedServiceInstaller can be typed to NamespacedServiceInstaller
var _ NamespacedServiceInstaller = &YamlNamespacedServiceInstaller{}

// YamlNamespacedServiceInstaller installs service using YAML files applied to specific namespace
type YamlNamespacedServiceInstaller struct {
	// Install service for specific namespaces
	InstallNamespacedYaml func(namespace string) error
	// Wait until the service is up and running
	WaitForNamespacedServiceRunning func(namespace string) error
	// Return all CRs of this service which exists in this namespace
	GetAllNamespaceYamlCrs func(string) ([]client.Object, error)
	// Uninstall service from namespace
	UninstallNamespaceYaml func(namespace string) error
	// Service name
	NamespacedYamlServiceName string
	// Cleanup functionality, will delete CRs without owner if not defined
	CleanupNamespaceYamlCrs func(namespace string) bool
}

// Install the namespaced service using YAML files into cloud
func (installer *YamlNamespacedServiceInstaller) Install(namespace string) error {
	// Store service installer for namespace to use for uninstalling purposes
	if sis, loaded := installedNamespacedServices.LoadOrStore(namespace, []NamespacedServiceInstaller{installer}); loaded {
		installedNamespacedServices.Store(namespace, append(sis.([]NamespacedServiceInstaller), installer))
	}

	if err := installer.InstallNamespacedYaml(namespace); err != nil {
		return err
	}

	return installer.WaitForNamespacedServiceRunning(namespace)
}

func (installer *YamlNamespacedServiceInstaller) getAllCrsInNamespace(namespace string) ([]client.Object, error) {
	return installer.GetAllNamespaceYamlCrs(namespace)
}

func (installer *YamlNamespacedServiceInstaller) uninstallFromNamespace(namespace string) error {
	return installer.UninstallNamespaceYaml(namespace)
}

func (installer *YamlNamespacedServiceInstaller) getServiceName() string {
	return installer.NamespacedYamlServiceName
}

func (installer *YamlNamespacedServiceInstaller) cleanupCrsInNamespace(namespace string) bool {
	if installer.CleanupNamespaceYamlCrs == nil {
		return deleteCrsWithoutOwner(installer, namespace)
	}
	return installer.CleanupNamespaceYamlCrs(namespace)
}

// YAML cluster wide service specification

// Make sure that YamlClusterWideServiceInstaller can be typed to ClusterWideServiceInstaller
var _ ClusterWideServiceInstaller = &YamlClusterWideServiceInstaller{}

// YamlClusterWideServiceInstaller installs service using YAML files applied to specific namespace
type YamlClusterWideServiceInstaller struct {
	// Install service for all namespaces
	InstallClusterYaml func() error
	// Namespace used for cluster wide service installation.
	InstallationNamespace string
	// Wait until the service is up and running
	WaitForClusterYamlServiceRunning func() error
	// Return all CRs of this service which exists in this namespace
	GetAllClusterYamlCrsInNamespace func(string) ([]client.Object, error)
	// Uninstall service from whole cluster
	UninstallClusterYaml func() error
	// Service name
	ClusterYamlServiceName string
	// Cleanup functionality, will delete CRs without owner if not defined
	CleanupClusterYamlCrsInNamespace func(namespace string) bool
}

// Install the cluster wide service using YAML files into cloud
func (installer *YamlClusterWideServiceInstaller) Install(namespace string) error {
	// Store cluster wide service installer to use for uninstalling purposes
	if _, loaded := installedClusterWideServices.LoadOrStore(installer, true); loaded {
		// Should be running already, wait until it is up
		return installer.WaitForClusterYamlServiceRunning()
	}

	monitorNamespace(installer.InstallationNamespace)

	if err := installer.InstallClusterYaml(); err != nil {
		return err
	}

	framework.OnNamespacePostCreated(installer.InstallationNamespace)

	return installer.WaitForClusterYamlServiceRunning()
}

func (installer *YamlClusterWideServiceInstaller) getAllCrsInNamespace(namespace string) ([]client.Object, error) {
	return installer.GetAllClusterYamlCrsInNamespace(namespace)
}

func (installer *YamlClusterWideServiceInstaller) uninstallFromCluster() error {
	stopNamespaceMonitoring(installer.InstallationNamespace)
	if err := installer.UninstallClusterYaml(); err != nil {
		return err
	}
	framework.OnNamespacePostDeleted(installer.InstallationNamespace)
	return nil
}

func (installer *YamlClusterWideServiceInstaller) getServiceName() string {
	return installer.ClusterYamlServiceName
}

func (installer *YamlClusterWideServiceInstaller) cleanupCrsInNamespace(namespace string) bool {
	if installer.CleanupClusterYamlCrsInNamespace == nil {
		return deleteCrsWithoutOwner(installer, namespace)
	}
	return installer.CleanupClusterYamlCrsInNamespace(namespace)
}

// OLM namespaced service specification

// Make sure that OlmNamespacedServiceInstaller can be typed to NamespacedServiceInstaller
var _ NamespacedServiceInstaller = &OlmNamespacedServiceInstaller{}

// OlmNamespacedServiceInstaller installs service using OLM, installed to specific namespace
type OlmNamespacedServiceInstaller struct {
	SubscriptionName             string
	Channel                      string
	StartingCSV                  string
	Catalog                      func() framework.OperatorCatalog
	InstallationTimeoutInMinutes int
	// Return all CRs of this service which exists in this namespace
	GetAllNamespacedOlmCrsInNamespace func(string) ([]client.Object, error)
	// Cleanup functionality, will delete CRs without owner if not defined
	CleanupNamespacedOlmCrsInNamespace func(namespace string) bool
}

// Install the namespaced service using OLM into cloud
func (installer *OlmNamespacedServiceInstaller) Install(namespace string) error {
	// Store service installer for namespace to use for uninstalling purposes
	if sis, loaded := installedNamespacedServices.LoadOrStore(namespace, []NamespacedServiceInstaller{installer}); loaded {
		installedNamespacedServices.Store(namespace, append(sis.([]NamespacedServiceInstaller), installer))
	}

	if err := framework.InstallOperator(namespace, installer.SubscriptionName, installer.Channel, installer.StartingCSV, installer.Catalog()); err != nil {
		return err
	}

	return framework.WaitForOperatorRunning(namespace, installer.SubscriptionName, installer.Catalog(), installer.InstallationTimeoutInMinutes)
}

func (installer *OlmNamespacedServiceInstaller) getAllCrsInNamespace(namespace string) ([]client.Object, error) {
	return installer.GetAllNamespacedOlmCrsInNamespace(namespace)
}

func (installer *OlmNamespacedServiceInstaller) uninstallFromNamespace(namespace string) error {
	subscription, err := framework.GetSubscription(namespace, installer.SubscriptionName, installer.Catalog())
	if err != nil {
		return err
	}

	return framework.DeleteSubscription(subscription)
}

func (installer *OlmNamespacedServiceInstaller) getServiceName() string {
	return installer.SubscriptionName
}

func (installer *OlmNamespacedServiceInstaller) cleanupCrsInNamespace(namespace string) bool {
	if installer.CleanupNamespacedOlmCrsInNamespace == nil {
		return deleteCrsWithoutOwner(installer, namespace)
	}
	return installer.CleanupNamespacedOlmCrsInNamespace(namespace)
}

// OLM cluster wide service specification

// Make sure that OlmClusterWideServiceInstaller can be typed to ClusterWideServiceInstaller
var _ ClusterWideServiceInstaller = &OlmClusterWideServiceInstaller{}

// OlmClusterWideServiceInstaller installs service using OLM, installed cluster wide
type OlmClusterWideServiceInstaller struct {
	SubscriptionName             string
	Channel                      string
	StartingCSV                  string
	Catalog                      func() framework.OperatorCatalog
	InstallationTimeoutInMinutes int
	// Return all CRs of this service which exists in this namespace
	GetAllClusterWideOlmCrsInNamespace func(string) ([]client.Object, error)
	// Cleanup functionality, will delete CRs without owner if not defined
	CleanupClusterWideOlmCrsInNamespace func(namespace string) bool
}

// Install the cluster wide service using OLM into cloud
func (installer *OlmClusterWideServiceInstaller) Install(namespace string) error {
	// Store cluster wide service installer to use for uninstalling purposes
	if _, loaded := installedClusterWideServices.LoadOrStore(installer, true); loaded {
		// Should be running already, wait until it is up
		return framework.WaitForClusterWideOperatorRunning(installer.SubscriptionName, installer.Catalog(), installer.InstallationTimeoutInMinutes)
	}

	if err := framework.InstallClusterWideOperator(installer.SubscriptionName, installer.Channel, installer.StartingCSV, installer.Catalog()); err != nil {
		return err
	}

	return framework.WaitForClusterWideOperatorRunning(installer.SubscriptionName, installer.Catalog(), installer.InstallationTimeoutInMinutes)
}

func (installer *OlmClusterWideServiceInstaller) getAllCrsInNamespace(namespace string) ([]client.Object, error) {
	return installer.GetAllClusterWideOlmCrsInNamespace(namespace)
}

func (installer *OlmClusterWideServiceInstaller) uninstallFromCluster() error {
	subscription, err := framework.GetClusterWideSubscription(installer.SubscriptionName, installer.Catalog())
	if err != nil {
		return err
	}

	return framework.DeleteSubscription(subscription)
}

func (installer *OlmClusterWideServiceInstaller) getServiceName() string {
	return installer.SubscriptionName
}

func (installer *OlmClusterWideServiceInstaller) cleanupCrsInNamespace(namespace string) bool {
	if installer.CleanupClusterWideOlmCrsInNamespace == nil {
		return deleteCrsWithoutOwner(installer, namespace)
	}
	return installer.CleanupClusterWideOlmCrsInNamespace(namespace)
}

// Helper methods

// Execute a function on all deployed cluster wide services
func executeOnClusterWideServices(execute func(si ClusterWideServiceInstaller) bool) bool {
	success := true
	installedClusterWideServices.Range(func(si, _ interface{}) bool {
		if ok := execute(si.(ClusterWideServiceInstaller)); !ok {
			success = false
		}
		return true
	})
	return success
}

// Execute a function on all deployed namespaced services
func executeOnNamespacedServices(namespace string, execute func(si NamespacedServiceInstaller) bool) bool {
	success := true
	if sis, ok := installedNamespacedServices.Load(namespace); ok {
		for _, si := range sis.([]NamespacedServiceInstaller) {
			if ok := execute(si); !ok {
				success = false
			}
		}
	}
	return success
}

// Delete all CRs of the service which don't have owner
func deleteCrsWithoutOwner(installer ServiceInstaller, namespace string) bool {
	crs, err := getCrsWithoutOwner(installer, namespace)
	if err != nil {
		framework.GetLogger(namespace).Error(err, "Error getting CRs without owner.", "service name", installer.getServiceName())
		return false
	}

	for _, cr := range crs {
		if err := framework.DeleteObject(cr); err != nil {
			framework.GetLogger(namespace).Error(err, "Error deleting CR.", "service name", installer.getServiceName(), "CR name", cr.GetName())
			return false
		}
	}
	return true
}

// Get all CRs of the service which don't have owner
func getCrsWithoutOwner(installer ServiceInstaller, namespace string) (crsWithoutOwner []client.Object, err error) {
	crs, err := installer.getAllCrsInNamespace(namespace)
	if err != nil {
		return nil, err
	}

	crsWithoutOwner = []client.Object{}
	for _, cr := range crs {
		if len(cr.GetOwnerReferences()) == 0 {
			crsWithoutOwner = append(crsWithoutOwner, cr)
		}
	}
	return crsWithoutOwner, nil
}

// Wait until all CRs of the service are removed from namespace
func waitForAllCrsRemoval(installer ServiceInstaller, namespace string, timeout time.Duration) bool {
	success := true
	err := framework.WaitFor(namespace, fmt.Sprintf("Removal of all related CRs for service %s", installer.getServiceName()), timeout, func() (bool, error) {
		crs, err := installer.getAllCrsInNamespace(namespace)
		if err != nil {
			return false, err
		}

		return len(crs) == 0, nil
	})
	if err != nil {
		framework.GetLogger(namespace).Error(err, "Error waiting on removal of all related CRs", "service name", installer.getServiceName())
		success = false
	}
	return success
}

func monitorNamespace(namespace string) {
	go func() {
		err := framework.StartPodLogCollector(namespace)
		if err != nil {
			framework.GetLogger(namespace).Error(err, "Error starting log collector", "namespace", namespace)
		}
	}()
}

func stopNamespaceMonitoring(namespace string) {
	if err := framework.StopPodLogCollector(namespace); err != nil {
		framework.GetMainLogger().Error(err, "Error stopping log collector", "namespace", namespace)
	}
	if err := framework.BumpEvents(namespace); err != nil {
		framework.GetMainLogger().Error(err, "Error bumping events", "namespace", namespace)
	}
}
