/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package common

import (
	"context"
	"fmt"
	"io"
	"k8s.io/client-go/discovery"
	"k8s.io/client-go/dynamic"
	"os"
	"path/filepath"
	"strings"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common/k8sclient"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"gopkg.in/yaml.v2"
	"k8s.io/apimachinery/pkg/api/errors"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime/schema"
)

type Document struct {
	Kind     string `yaml:"kind"`
	Metadata struct {
		Name string `yaml:"name"`
	} `yaml:"metadata"`
}

var namespacesGVR = schema.GroupVersionResource{
	Group:    "",
	Version:  "v1",
	Resource: "namespaces",
}

var sonataflowGVR = schema.GroupVersionResource{
	Group:    "sonataflow.org",
	Version:  "v1alpha08",
	Resource: "sonataflows",
}

var subscriptionsGVR = schema.GroupVersionResource{
	Group:    "operators.coreos.com",
	Version:  "v1alpha1",
	Resource: "subscriptions",
}

var clusterServiceVersionsGVR = schema.GroupVersionResource{
	Group:    "operators.coreos.com",
	Version:  "v1alpha1",
	Resource: "clusterserviceversions",
}

var customResourceDefinitionsGVR = schema.GroupVersionResource{
	Group:    "apiextensions.k8s.io",
	Version:  "v1",
	Resource: "customresourcedefinitions",
}

var catalogSourcesGVR = schema.GroupVersionResource{
	Group:    "operators.coreos.com",
	Version:  "v1alpha1",
	Resource: "catalogsources",
}

type OperatorManager struct {
	namespace     string
	isOpenshift   bool
	dynamicClient dynamic.Interface
}

var openshiftOperatorNamespaces = []string{"openshift-operators", "community-operators"}

func NewOperatorManager(namespace string) *OperatorManager {
	isOpenshift, err := isOpenshift()
	if err != nil {
		fmt.Printf("❌ ERROR: %v\n", err)
		os.Exit(1)
	}

	if namespace == "" {
		namespace, err = guessOperatorNamespace(isOpenshift)
		if err != nil {
			fmt.Printf("❌ ERROR: %v\n", err)
			os.Exit(1)
		}
	}

	dynamicClient, err := k8sclient.DynamicClient()
	if err != nil {
		fmt.Printf("❌ ERROR: %v\n", err)
		os.Exit(1)
	}

	return &OperatorManager{
		namespace:     namespace,
		isOpenshift:   isOpenshift,
		dynamicClient: dynamicClient,
	}
}

func checkOperatorRunning(getPodsOutPut string) bool {
	pods := strings.Split(getPodsOutPut, "\n")
	for _, pod := range pods {
		// Split each line into fields (NAME, READY, STATUS, RESTARTS, AGE)
		fields := strings.Fields(pod)

		// Check if this line contains information about the desired operator manager pod
		if len(fields) > 2 && strings.HasPrefix(fields[0], metadata.OperatorManagerPod) && fields[2] == "Running" {
			return true
		}
	}
	return false
}

func FindServiceFiles(directory string) ([]string, error) {
	var serviceFiles []string

	err := filepath.Walk(directory, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure accessing a path %q: %v\n", path, err)
		}

		if info.IsDir() || filepath.Ext(path) != ".yaml" {
			return nil
		}

		file, err := os.Open(path)
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure opening file %q: %v\n", path, err)
		}
		defer file.Close()

		byteValue, err := io.ReadAll(file)
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure reading file %q: %v\n", path, err)
		}

		var doc Document
		if err := yaml.Unmarshal(byteValue, &doc); err != nil {
			return fmt.Errorf("❌ ERROR: failure unmarshalling YAML from file %q: %v\n", path, err)
		}

		if doc.Kind == metadata.ManifestServiceFilesKind {
			serviceFiles = append(serviceFiles, path)
		}

		return nil
	})

	if err != nil {
		return nil, err
	}

	return serviceFiles, nil
}

func (m OperatorManager) CheckOLMInstalled() error {
	resources, err := m.dynamicClient.Resource(catalogSourcesGVR).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return fmt.Errorf("❌ ERROR: OLM (Operator Lifecycle Manager) isn't installed, install OLM first : %v\n", err)
	}
	var operatorSources []string

	if m.isOpenshift {
		operatorSources = append(operatorSources, openshiftOperatorNamespaces...)
	} else {
		operatorSources = append(operatorSources, "operatorhubio-catalog")
	}

	for _, resource := range resources.Items {
		name, found, err := unstructured.NestedString(resource.Object, "metadata", "name")
		namespace, found, err := unstructured.NestedString(resource.Object, "metadata", "namespace")
		if err != nil || !found {
			continue
		}
		for _, operatorSource := range operatorSources {
			if name == operatorSource && metadata.OLMCatalogSourcesMap[name] == namespace {
				return nil
			}
		}
	}

	return fmt.Errorf("❌ ERROR: OLM (Operator Lifecycle Manager) is not installed. Please install OLM before installing the SonataFlow Operator.")
}

func (m OperatorManager) InstallSonataflowOperator() error {
	var source string
	var sourceNamespace string

	if m.isOpenshift {
		source = "community-operators"
		sourceNamespace = "openshift-marketplace"
	} else {
		source = "operatorhubio-catalog"
		sourceNamespace = "olm"
	}

	subscriptionUnstructured := &unstructured.Unstructured{
		Object: map[string]interface{}{
			"apiVersion": "operators.coreos.com/v1alpha1",
			"kind":       "Subscription",
			"metadata": map[string]interface{}{
				"name":      metadata.SonataFlowOperatorName,
				"namespace": m.namespace,
			},
			"spec": map[string]interface{}{
				"channel":         "alpha",
				"name":            "sonataflow-operator",
				"source":          source,
				"sourceNamespace": sourceNamespace,
			},
		},
	}

	_, err := ExecuteCreate(subscriptionsGVR, subscriptionUnstructured, m.namespace)
	if err != nil {
		return fmt.Errorf("❌ Failed to create subscription: %v", err)
	}
	fmt.Println("✅ Subscription created successfully")

	return nil
}

func (m OperatorManager) GetSonataflowOperatorStats() error {
	subscription, err := ExecuteGet(subscriptionsGVR, metadata.SonataFlowOperatorName, m.namespace)
	if err != nil {
		return fmt.Errorf("❌ SonataFlow Operator is not installed.")
	}

	status, found, err := unstructured.NestedMap(subscription.Object, "status")
	if err != nil {
		return fmt.Errorf("error getting status: %v", err)
	}
	if !found {
		return fmt.Errorf("status not found in subscription")
	}

	currentCSV, _, _ := unstructured.NestedString(status, "currentCSV")
	installedCSV, _, _ := unstructured.NestedString(status, "installedCSV")
	state, _, _ := unstructured.NestedString(status, "state")

	fmt.Println()
	fmt.Println("📊 Subscription Status:")
	fmt.Println()

	fmt.Printf("Current CSV: %s\n", currentCSV)
	fmt.Printf("Installed CSV: %s\n", installedCSV)
	fmt.Printf("State: %s\n", state)

	conditions, exists, _ := unstructured.NestedSlice(status, "conditions")
	if exists {
		fmt.Println()
		fmt.Printf("Conditions:\n")
		for _, c := range conditions {
			condition := c.(map[string]interface{})
			fmt.Printf("Type: %s\n", condition["type"])
			fmt.Printf("Status: %s\n", condition["status"])
			fmt.Printf("Message: %s\n", condition["message"])
			fmt.Printf("Last Transition Time: %s\n\n", condition["lastTransitionTime"])
		}
	}

	return nil
}

func (m OperatorManager) RemoveSubscription() error {
	fmt.Println("🔧 Deleting the SonataFlow Operator subscription...")

	err := ExecuteDeleteGVR(subscriptionsGVR, metadata.SonataFlowOperatorName, m.namespace)
	if err != nil {
		return fmt.Errorf("❌ Failed to delete subscription `sonataflow-operator` in namespace %s: %v\n", m.namespace, err)
	}
	fmt.Printf("✅ Subscription `sonataflow-operator` deleted successfully in namespace %s\n", m.namespace)

	return nil
}

func (m OperatorManager) RemoveCSV() error {
	resources, err := ExecuteList(clusterServiceVersionsGVR, m.namespace)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get CSV resources: %v", err)
	}
	for _, resource := range resources.Items {
		name, found, err := unstructured.NestedString(resource.Object, "metadata", "name")
		if err != nil || !found {
			continue
		}
		if strings.HasPrefix(name, metadata.SonataFlowOperatorName) {
			err := ExecuteDeleteGVR(clusterServiceVersionsGVR, name, m.namespace)
			if err != nil {
				return fmt.Errorf("❌ ERROR: Failed to delete CSV `sonataflow-operator` in namespace %s: %v\n", m.namespace, err)
			}
			fmt.Printf("✅ CSV `sonataflow-operator` deleted successfully in namespace %s\n", m.namespace)
			return nil
		}
	}
	return fmt.Errorf("❌ ERROR: CSV `sonataflow-operator` not found in namespace %s\n", m.namespace)
}

func (m OperatorManager) ListOperatorResources() ([]unstructured.Unstructured, error) {
	resources, err := m.dynamicClient.Resource(clusterServiceVersionsGVR).List(context.Background(), v1.ListOptions{})
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to list resources: %v", err)
	}

	if len(resources.Items) == 0 {
		return nil, fmt.Errorf("❌ No resources found")
	}
	var result []unstructured.Unstructured
	for _, csv := range resources.Items {
		if strings.HasPrefix(csv.GetName(), metadata.SonataFlowOperatorName) {
			result = append(result, csv)
		}
	}
	return result, nil
}

func (m OperatorManager) RemoveCRD() error {
	subscription, err := ExecuteGet(subscriptionsGVR, metadata.SonataFlowOperatorName, m.namespace)
	if err != nil {
		return fmt.Errorf("failed to get subscription %s: %v", metadata.SonataFlowOperatorName, err)
	}

	installedCSV, found, err := unstructured.NestedString(subscription.Object, "status", "installedCSV")
	if err != nil || !found {
		return fmt.Errorf("failed to extract installedCSV from subscription: %v", err)
	}

	csvObj, err := ExecuteGet(clusterServiceVersionsGVR, installedCSV, m.namespace)
	if err != nil {
		return fmt.Errorf("❌ Failed to get CSV %q in namespace %q: %v", installedCSV, m.namespace, err)
	}

	ownedCRDs, found, err := unstructured.NestedSlice(csvObj.Object,
		"spec", "customresourcedefinitions", "owned")
	if err != nil {
		return fmt.Errorf("❌ Failed to extract owned CRDs: %v", err)
	}
	if !found {
		fmt.Println("✅ No owned CRDs found in CSV")
		return nil
	}

	for _, crdRaw := range ownedCRDs {
		crdMap, ok := crdRaw.(map[string]interface{})
		if !ok {
			continue
		}
		crdName, _, _ := unstructured.NestedString(crdMap, "name")
		crdKind, _, _ := unstructured.NestedString(crdMap, "kind")
		crdVersion, _, _ := unstructured.NestedString(crdMap, "version")

		fmt.Printf("✅ CRD Name: %s, Kind: %s, Version: %s\n", crdName, crdKind, crdVersion)

		err := ExecuteDeleteGVR(customResourceDefinitionsGVR, crdName, "")
		if err != nil {
			return fmt.Errorf("❌ ERROR: Failed to delete CRD %q: %v\n", crdName, err)
		}
		fmt.Printf("✅ CRD %q deleted\n", crdName)
	}
	return nil
}

func (m OperatorManager) RemoveCR() error {
	namespaces, err := ExecuteList(namespacesGVR, "")
	if err != nil {
		return fmt.Errorf("❌ Failed to list resources in namespace: %v", err)
	}

	for _, ns := range namespaces.Items {
		namespace := ns.GetName()

		sonataflows, err := ExecuteList(sonataflowGVR, namespace)
		if err != nil {
			if !errors.IsNotFound(err) && !errors.IsForbidden(err) {
				return fmt.Errorf("❌ Error listing sonataflows in namespace %s: %v\n", namespace, err)
			}
			continue
		}

		if len(sonataflows.Items) > 0 {
			for _, deployment := range sonataflows.Items {
				name := deployment.GetName()
				err := ExecuteDeleteGVR(sonataflowGVR, name, namespace)
				if err != nil {
					return fmt.Errorf("❌ Failed to delete %s: %v\n", name, err)
				} else {
					fmt.Printf("✅ Deleted %s successfully\n", name)
				}
			}
		}
	}

	return nil
}

func isOpenshift() (bool, error) {
	dynamicConfig, err := k8sclient.KubeRestConfig()
	if err != nil {
		return false, fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}

	discoveryClient, err := discovery.NewDiscoveryClientForConfig(dynamicConfig)
	if err != nil {
		return false, err
	}

	apiGroups, err := discoveryClient.ServerGroups()
	if err != nil {
		return false, fmt.Errorf("❌ ERROR: Failed to discover server groups: %v", err)
	}

	for _, group := range apiGroups.Groups {
		if group.Name == "route.openshift.io" {
			return true, nil
		}
	}
	return false, nil
}

var possibleOpenshiftNamespaces = []string{"openshift-operators", "community-operators"}

func guessOperatorNamespace(isOpenshift bool) (string, error) {
	if isOpenshift {
		for _, ns := range possibleOpenshiftNamespaces {
			if _, err := GetNamespace(ns); err == nil {
				return ns, nil
			}
		}
	} else {
		// In case of Minikube or Kind, with default OLM installation, the namespace is "operators"
		return "operators", nil
	}
	return "", fmt.Errorf("❌ ERROR: No valid namespace found for the Operator, please provide a namespace with the --namespace flag")
}
