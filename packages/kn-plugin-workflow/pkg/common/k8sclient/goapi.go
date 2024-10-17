package k8sclient

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import (
	"context"
	"fmt"
	"k8s.io/apiextensions-apiserver/pkg/client/clientset/clientset"
	"log"
	"os"
	"path/filepath"
	"strings"

	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/util/yaml"

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/tools/clientcmd/api"
)

type GoAPI struct{}

func (m GoAPI) GetNamespace() (string, error) {
	return GetNamespace()
}

func (m GoAPI) CheckContext() (string, error) {
	config, err := KubeApiConfig()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: No current k8s context found %w", err)
	}
	context := config.CurrentContext
	if context == "" {
		return "", fmt.Errorf("❌ ERROR: No current k8s context found")
	}
	fmt.Printf(" - ✅ k8s current context: %s\n", context)
	return context, nil
}

func (m GoAPI) ExecuteApply(path, namespace string) error {
	client, err := DynamicClient()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}
	fmt.Printf("🔨 Applying YAML file %s\n", path)

	if namespace == "" {
		currentNamespace, err := m.GetNamespace()
		if err != nil {
			return fmt.Errorf("❌ ERROR: Failed to get current namespace: %w", err)
		}
		namespace = currentNamespace
	}

	if resources, err := ParseYamlFile(path); err != nil {
		return fmt.Errorf("❌ ERROR: Failed to parse YAML file: %v", err)
	} else {
		created := make([]unstructured.Unstructured, 0, len(resources))
		for _, resource := range resources {
			gvk := resource.GroupVersionKind()
			gvr, _ := meta.UnsafeGuessKindToResource(gvk)

			if resource.GetNamespace() != "" && namespace != resource.GetNamespace() {
				return fmt.Errorf("❌ ERROR: the namespace from the provided object \"%s\" does not match"+
					" the namespace \"%s\". You must pass '--namespace=%s' to perform this operation.:",
					resource.GetNamespace(), namespace, resource.GetNamespace())
			}

			_, err := client.Resource(gvr).Namespace(namespace).Create(context.Background(), &resource, metav1.CreateOptions{})
			if err != nil {
				if errors.IsAlreadyExists(err) {
					existingResource, err := client.Resource(gvr).Namespace(namespace).Get(context.Background(), resource.GetName(), metav1.GetOptions{})
					if err != nil {
						return fmt.Errorf("❌ ERROR: Failed to get existing resource: %v", err)
					}
					resource.SetResourceVersion(existingResource.GetResourceVersion())
					_, err = client.Resource(gvr).Namespace(namespace).Update(context.Background(), &resource, metav1.UpdateOptions{})
					if err != nil {
						return fmt.Errorf("❌ ERROR: Failed to update resource: %v", err)
					}
				} else {
					// rollback
					if err := doRollback(created, namespace, client); err != nil {
						return fmt.Errorf("❌ ERROR: Failed to rollback resource: %v", err)
					}
					return fmt.Errorf("❌ ERROR: Failed to create resource: %v", err)
				}
			}
			created = append(created, resource)
		}
	}
	return nil
}

func (m GoAPI) ExecuteDelete(path, namespace string) error {
	client, err := DynamicClient()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}

	if namespace == "" {
		currentNamespace, err := m.GetNamespace()
		if err != nil {
			return fmt.Errorf("❌ ERROR: Failed to get current namespace: %w", err)
		}
		namespace = currentNamespace
	}

	if resources, err := ParseYamlFile(path); err != nil {
		return fmt.Errorf("❌ ERROR: Failed to parse YAML file: %v", err)
	} else {
		deletePolicy := metav1.DeletePropagationForeground
		for _, resource := range resources {
			gvk := resource.GroupVersionKind()
			gvr, _ := meta.UnsafeGuessKindToResource(gvk)

			err = client.Resource(gvr).Namespace(namespace).Delete(context.Background(), resource.GetName(), metav1.DeleteOptions{
				PropagationPolicy: &deletePolicy,
			})
			if err != nil {
				return fmt.Errorf("❌ ERROR: Failed to delete Resource: %w", err)
			}
		}
	}
	return nil
}

func (m GoAPI) CheckCrdExists(crd string) error {
	config, err := KubeRestConfig()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create rest config for Kubernetes client: %v", err)
	}

	crdClientSet, err := clientset.NewForConfig(config)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create k8s client: %v", err)
	}

	_, err = crdClientSet.ApiextensionsV1().CustomResourceDefinitions().Get(context.Background(), crd, metav1.GetOptions{})
	if err != nil {
		return err
	}

	return nil
}

func KubeApiConfig() (*api.Config, error) {
	homeDir, err := os.UserHomeDir()
	if err != nil {
		return nil, fmt.Errorf("error getting user home dir: %w", err)
	}
	kubeConfigPath := filepath.Join(homeDir, ".kube", "config")
	fmt.Printf("🔎 Using kubeconfig: %s\n", kubeConfigPath)
	config, err := clientcmd.LoadFromFile(kubeConfigPath)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to load kubeconfig: %w", err)
	}
	return config, nil
}

func KubeRestConfig() (*rest.Config, error) {
	config, err := rest.InClusterConfig()
	if err != nil {
		kubeConfig, err := KubeApiConfig()
		if err != nil {
			return nil, fmt.Errorf("❌ ERROR: Failed to load kubeconfig: %w", err)
		}
		clientConfig := clientcmd.NewDefaultClientConfig(*kubeConfig, &clientcmd.ConfigOverrides{})
		restConfig, err := clientConfig.ClientConfig()
		if err != nil {
			log.Fatalf("Error converting to rest.Config: %v", err)
		}
		return restConfig, nil
	}
	return config, nil
}

var DynamicClient = func() (dynamic.Interface, error) {
	config, err := KubeRestConfig()
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to create rest config for Kubernetes client: %v", err)
	}

	dynamicClient, err := dynamic.NewForConfig(config)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}

	return dynamicClient, nil
}

var ParseYamlFile = func(path string) ([]unstructured.Unstructured, error) {
	data, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to read YAML file: %w", err)
	}
	decoder := yaml.NewYAMLOrJSONDecoder(strings.NewReader(string(data)), 4096)
	result := []unstructured.Unstructured{}
	for {
		rawObj := &unstructured.Unstructured{}
		err := decoder.Decode(rawObj)
		if err != nil {
			break
		}
		result = append(result, *rawObj)
	}
	return result, nil
}

var GetNamespace = func() (string, error) {
	fmt.Println("🔎 Checking current namespace in k8s...")

	config, err := KubeApiConfig()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: Failed to get current k8s namespace: %w", err)
	}
	namespace := config.Contexts[config.CurrentContext].Namespace

	if len(namespace) == 0 {
		namespace = "default"
	}
	fmt.Printf(" - ✅  k8s current namespace: %s\n", namespace)
	return namespace, nil
}

func doRollback(created []unstructured.Unstructured, applyNamespace string, client dynamic.Interface) error {
	for _, r := range created {
		gvk := r.GroupVersionKind()
		gvr, _ := meta.UnsafeGuessKindToResource(gvk)
		if r.GetNamespace() != "" {
			applyNamespace = r.GetNamespace()
		}

		if err := client.Resource(gvr).Namespace(applyNamespace).Delete(context.Background(), r.GetName(), metav1.DeleteOptions{}); err != nil && !errors.IsNotFound(err) {
			return fmt.Errorf("❌ ERROR: Failed to rollback resource: %v", err)
		}
	}
	return nil
}
