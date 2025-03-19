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

package platform

import (
	"context"
	"fmt"
	"os"
	"strings"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/klog/v2"

	coordination "k8s.io/api/coordination/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
)

const (
	// DefaultPlatformName is the standard name used for the platform.
	DefaultPlatformName = "kogito-serverless-platform"

	OperatorWatchNamespaceEnvVariable = "WATCH_NAMESPACE"
	operatorNamespaceEnvVariable      = "NAMESPACE"
)

// Copied from https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry

// LocalRegistryHostingV1 describes a local registry that developer tools can
// connect to. A local registry allows clients to load images into the local
// cluster by pushing to this registry.
type LocalRegistryHostingV1 struct {
	// Host documents the host (hostname and port) of the registry, as seen from
	// outside the cluster.
	//
	// This is the registry host that tools outside the cluster should push images
	// to.
	Host string `yaml:"host,omitempty"`

	// HostFromClusterNetwork documents the host (hostname and port) of the
	// registry, as seen from networking inside the container pods.
	//
	// This is the registry host that tools running on pods inside the cluster
	// should push images to. If not set, then tools inside the cluster should
	// assume the local registry is not available to them.
	HostFromClusterNetwork string `yaml:"hostFromClusterNetwork,omitempty"`

	// HostFromContainerRuntime documents the host (hostname and port) of the
	// registry, as seen from the cluster's container runtime.
	//
	// When tools apply Kubernetes objects to the cluster, this host should be
	// used for image name fields. If not set, users of this field should use the
	// value of Host instead.
	//
	// Note that it doesn't make sense semantically to define this field, but not
	// define Host or HostFromClusterNetwork. That would imply a way to pull
	// images without a way to push images.
	HostFromContainerRuntime string `yaml:"hostFromContainerRuntime,omitempty"`

	// Help contains a URL pointing to documentation for users on how to set
	// up and configure a local registry.
	//
	// Tools can use this to nudge users to enable the registry. When possible,
	// the writer should use as permanent a URL as possible to prevent drift
	// (e.g., a version control SHA).
	//
	// When image pushes to a registry host specified in one of the other fields
	// fail, the tool should display this help URL to the user. The help URL
	// should contain instructions on how to diagnose broken or misconfigured
	// registries.
	Help string `yaml:"help,omitempty"`
}

const OperatorLockName = "kogito-serverless-lock"

// IsCurrentOperatorGlobal returns true if the operator is configured to watch all namespaces.
func IsCurrentOperatorGlobal() bool {
	if watchNamespace, envSet := os.LookupEnv(OperatorWatchNamespaceEnvVariable); !envSet || strings.TrimSpace(watchNamespace) == "" {
		return true
	}
	return false
}

// GetOperatorNamespace returns the namespace where the current operator is located (if set).
func GetOperatorNamespace() string {
	if podNamespace, envSet := os.LookupEnv(operatorNamespaceEnvVariable); envSet {
		return podNamespace
	}
	return ""
}

// GetOperatorLockName returns the name of the lock lease that is electing a leader on the particular namepsace.
func GetOperatorLockName(operatorID string) string {
	return fmt.Sprintf("%s-lock", operatorID)
}

// GetActivePlatform returns the currently installed active platform in the local namespace.
// The parameter createIfNotExists determines if such platform must be created when not exists. Never nil when
// createsIfNotExists is true, unless an error.
func GetActivePlatform(ctx context.Context, c ctrl.Client, namespace string, createIfNotExists bool) (*operatorapi.SonataFlowPlatform, error) {
	platform, err := getLocalPlatform(ctx, c, namespace, true)
	if err != nil {
		return nil, err
	}
	if platform != nil {
		return platform, nil
	}
	klog.V(log.I).InfoS("No active SonataFlowPlatform was found in namespace", "Namespace", namespace)
	if createIfNotExists {
		klog.V(log.I).InfoS("Creating a default SonataFlowPlatform", "Namespace", namespace)
		sfp := newEmptySonataFlowPlatform(namespace)
		if err = CreateOrUpdateWithDefaults(ctx, sfp, false); err != nil {
			return nil, err
		}
		return sfp, nil
	}
	return nil, nil
}

// getLocalPlatform returns the currently installed active platform, or any platform, existing in local namespace when no
// active platform exists. When the active parameter is true, only active platforms are considered.
// In other cases, a non-active platform might be returned as a second option.
func getLocalPlatform(ctx context.Context, c ctrl.Client, namespace string, active bool) (*operatorapi.SonataFlowPlatform, error) {
	klog.V(log.D).InfoS("Finding available platforms in namespace", "namespace", namespace)
	lst, err := listPrimaryPlatforms(ctx, c, namespace)
	if err != nil {
		return nil, err
	}
	for _, p := range lst.Items {
		platform := p // pin
		if IsActive(&platform) {
			klog.V(log.D).InfoS("Found active local platform", "platform", platform.Name)
			return &platform, nil
		}
	}

	if !active && len(lst.Items) > 0 {
		// does not require the platform to be active, just return one if present
		res := lst.Items[0]
		klog.V(log.D).InfoS("Found non-active local platform", "platform", res.Name)
		return &res, nil
	}
	return nil, nil
}

func newEmptySonataFlowPlatform(namespace string) *operatorapi.SonataFlowPlatform {
	if utils.IsOpenShift() {
		return &operatorapi.SonataFlowPlatform{
			ObjectMeta: metav1.ObjectMeta{Name: defaultSonataFlowPlatformName, Namespace: namespace},
			Spec: operatorapi.SonataFlowPlatformSpec{
				Build: operatorapi.BuildPlatformSpec{
					Config: operatorapi.BuildPlatformConfig{
						BuildStrategy: operatorapi.PlatformBuildStrategy,
					},
				},
			},
		}
	}

	return &operatorapi.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{Name: defaultSonataFlowPlatformName, Namespace: namespace},
		Spec: operatorapi.SonataFlowPlatformSpec{
			Build: operatorapi.BuildPlatformSpec{
				Config: operatorapi.BuildPlatformConfig{
					BuildStrategyOptions: map[string]string{
						kanikoBuildCacheEnabled: "true",
					},
				},
			},
		},
	}
}

// listPrimaryPlatforms returns all non-secondary platforms installed in a given namespace (only one will be active).
func listPrimaryPlatforms(ctx context.Context, c ctrl.Reader, namespace string) (*operatorapi.SonataFlowPlatformList, error) {
	lst, err := listAllPlatforms(ctx, c, namespace)
	if err != nil {
		return nil, err
	}

	filtered := &operatorapi.SonataFlowPlatformList{}
	for i := range lst.Items {
		pl := lst.Items[i]
		if !IsSecondary(&pl) {
			filtered.Items = append(filtered.Items, pl)
		}
	}
	return filtered, nil
}

// listAllPlatforms returns all platforms installed in a given namespace.
func listAllPlatforms(ctx context.Context, c ctrl.Reader, namespace string) (*operatorapi.SonataFlowPlatformList, error) {
	lst := operatorapi.NewSonataFlowPlatformList()
	if err := c.List(ctx, &lst, ctrl.InNamespace(namespace)); err != nil {
		return nil, err
	}
	return &lst, nil
}

// IsActive determines if the given platform is being used.
func IsActive(p *operatorapi.SonataFlowPlatform) bool {
	return !p.Status.IsDuplicated()
}

// IsSecondary determines if the given platform is marked as secondary.
func IsSecondary(p *operatorapi.SonataFlowPlatform) bool {
	if l, ok := p.Annotations[metadata.SecondaryPlatformAnnotation]; ok && l == "true" {
		return true
	}
	return false
}

// IsNamespaceLocked tells if the namespace contains a lock indicating that an operator owns it.
func IsNamespaceLocked(ctx context.Context, c ctrl.Reader, namespace string) (bool, error) {
	if namespace == "" {
		return false, nil
	}

	platforms, err := listPrimaryPlatforms(ctx, c, namespace)
	if err != nil {
		return true, err
	}

	for _, platform := range platforms.Items {
		lease := coordination.Lease{}

		var operatorLockName string
		if platform.Name != "" {
			operatorLockName = GetOperatorLockName(platform.Name)
		} else {
			operatorLockName = OperatorLockName
		}

		if err := c.Get(ctx, ctrl.ObjectKey{Namespace: namespace, Name: operatorLockName}, &lease); err == nil || !k8serrors.IsNotFound(err) {
			return true, err
		}
	}

	return false, nil
}

// IsOperatorAllowedOnNamespace returns true if the current operator is allowed to react on changes in the given namespace.
func IsOperatorAllowedOnNamespace(ctx context.Context, c ctrl.Reader, namespace string) (bool, error) {
	// allow all local operators
	if !IsCurrentOperatorGlobal() {
		return true, nil
	}

	// allow global operators that use a proper operator id
	if utils.OperatorID() != "" {
		return true, nil
	}

	operatorNamespace := GetOperatorNamespace()
	if operatorNamespace == namespace {
		// Global operator is allowed on its own namespace
		return true, nil
	}
	alreadyOwned, err := IsNamespaceLocked(ctx, c, namespace)
	if err != nil {
		return false, err
	}
	return !alreadyOwned, nil
}

// IsOperatorHandler Operators matching the annotation operator id are allowed to reconcile.
// For legacy resources that are missing a proper operator id annotation the default global operator or the local
// operator in this namespace are candidates for reconciliation.
func IsOperatorHandler(object ctrl.Object) bool {
	if object == nil {
		return true
	}
	resourceID := utils.GetOperatorIDAnnotation(object)
	operatorID := utils.OperatorID()

	// allow operator with matching id to handle the resource
	if resourceID == operatorID {
		return true
	}

	// check if we are dealing with resource that is missing a proper operator id annotation
	if resourceID == "" {
		// allow default global operator to handle legacy resources (missing proper operator id annotations)
		if operatorID == DefaultPlatformName {
			return true
		}

		// allow local operators to handle legacy resources (missing proper operator id annotations)
		if !IsCurrentOperatorGlobal() {
			return true
		}
	}

	return false
}

// IsOperatorHandlerConsideringLock uses normal IsOperatorHandler checks and adds additional check for legacy resources
// that are missing a proper operator id annotation. In general two kind of operators race for reconcile these legacy resources.
// The local operator for this namespace and the default global operator instance. Based on the existence of a namespace
// lock the current local operator has precedence. When no lock exists the default global operator should reconcile.
func IsOperatorHandlerConsideringLock(ctx context.Context, c ctrl.Reader, namespace string, object ctrl.Object) bool {
	isHandler := IsOperatorHandler(object)
	if !isHandler {
		return false
	}

	resourceID := utils.GetOperatorIDAnnotation(object)
	// add additional check on resources missing an operator id
	if resourceID == "" {
		operatorNamespace := GetOperatorNamespace()
		if operatorNamespace == namespace {
			// Global operator is allowed on its own namespace
			return true
		}

		if locked, err := IsNamespaceLocked(ctx, c, namespace); err != nil || locked {
			// namespace is locked so local operators do have precedence
			return !IsCurrentOperatorGlobal()
		}
	}

	return true
}
