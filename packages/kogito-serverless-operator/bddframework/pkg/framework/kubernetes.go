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

package framework

import (
	"fmt"
	"strings"
	"sync"
	"time"

	"sigs.k8s.io/controller-runtime/pkg/client"

	rbac "k8s.io/api/rbac/v1"

	apps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	k8sv1beta1 "k8s.io/api/extensions/v1beta1"
	apiextensionsv1beta1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1beta1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/intstr"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	kogitocli "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/meta"
)

var (
	kubeClient *kogitocli.Client
	mux        = &sync.Mutex{}
)

const (
	// LabelAppKey is the default label key to bind resources together in "Application Group"
	LabelAppKey = "app"
)

// podErrorReasons contains all the reasons to state a pod in error.
var podErrorReasons = []string{"InvalidImageName"}

// InitKubeClient initializes the Kubernetes Client
func InitKubeClient(scheme *runtime.Scheme) error {
	mux.Lock()
	defer mux.Unlock()
	if kubeClient == nil {
		newClient, err := kogitocli.NewClientBuilder(scheme).UseControllerDynamicMapper().WithDiscoveryClient().WithBuildClient().WithKubernetesExtensionClient().Build()
		if err != nil {
			return fmt.Errorf("Error initializing kube client: %v", err)
		}
		kubeClient = newClient
	}
	return nil
}

// WaitForPodsWithLabel waits for pods with specific label to be available and running
func WaitForPodsWithLabel(namespace, labelName, labelValue string, numberOfPods, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("%d Pods with label name '%s' and value '%s' available and running", numberOfPods, labelName, labelValue), timeoutInMin,
		func() (bool, error) {
			pods, err := GetPodsWithLabels(namespace, map[string]string{labelName: labelValue})
			if err != nil || (len(pods.Items) != numberOfPods) {
				return false, err
			}

			return CheckPodsAreReady(pods), nil
		}, CheckPodsWithLabelInError(namespace, labelName, labelValue))
}

// WaitForPodsWithLabels waits for pods with specific label to be available and running
func WaitForPodsWithLabels(namespace string, labels map[string]string, numberOfPods, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("%d Pods with labels '%v' available and running", numberOfPods, labels), timeoutInMin,
		func() (bool, error) {
			pods, err := GetPodsWithLabels(namespace, labels)
			if err != nil || (len(pods.Items) != numberOfPods) {
				return false, err
			}

			return CheckPodsAreReady(pods), nil
		}, CheckPodsWithLabelsInError(namespace, labels))
}

// WaitForPodsInNamespace waits for pods in specific namespace to be available and running
func WaitForPodsInNamespace(namespace string, numberOfPods, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, "Pods in namespace available and running", timeoutInMin,
		func() (bool, error) {
			pods, err := GetPods(namespace)
			if err != nil || (len(pods.Items) != numberOfPods) {
				return false, err
			}

			return CheckPodsAreReady(pods), nil
		}, CheckPodsInNamespaceInError(namespace))
}

// GetPods retrieves all pods in namespace
func GetPods(namespace string) (*corev1.PodList, error) {
	pods := &corev1.PodList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespace(namespace, pods); err != nil {
		return nil, err
	}
	return pods, nil
}

// GetPodsByDeploymentConfig retrieves pods with a deploymentconfig label set to <dcName>
func GetPodsByDeploymentConfig(namespace string, dcName string) (*corev1.PodList, error) {
	return GetPodsWithLabels(namespace, map[string]string{"deploymentconfig": dcName})
}

// GetPodsByDeployment retrieves pods belonging to a Deployment
func GetPodsByDeployment(namespace string, dName string) (pods []corev1.Pod, err error) {
	pods = []corev1.Pod{}

	// Get ReplicaSet related to the Deployment
	replicaSet, err := GetActiveReplicaSetByDeployment(namespace, dName)
	if err != nil {
		return nil, err
	}

	// Fetch all pods in namespace
	podList := &corev1.PodList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespace(namespace, podList); err != nil {
		return nil, err
	}

	// Find which pods belong to the ReplicaSet
	for _, pod := range podList.Items {
		for _, ownerReference := range pod.OwnerReferences {
			if ownerReference.Kind == "ReplicaSet" && ownerReference.Name == replicaSet.GetName() {
				pods = append(pods, pod)
			}
		}
	}

	return
}

// GetActiveReplicaSetByDeployment retrieves active ReplicaSet belonging to a Deployment
func GetActiveReplicaSetByDeployment(namespace string, dName string) (*apps.ReplicaSet, error) {
	replicaSets := &apps.ReplicaSetList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespace(namespace, replicaSets); err != nil {
		return nil, err
	}

	// Find ReplicaSet owned by Deployment with active Pods
	for _, replicaSet := range replicaSets.Items {
		for _, ownerReference := range replicaSet.OwnerReferences {
			if ownerReference.Kind == "Deployment" && ownerReference.Name == dName && replicaSet.Status.AvailableReplicas > 0 {
				return &replicaSet, nil
			}
		}
	}

	return nil, fmt.Errorf("No ReplicaSet belonging to Deployment %s found", dName)
}

// GetPodsWithLabels retrieves pods based on label name and value
func GetPodsWithLabels(namespace string, labels map[string]string) (*corev1.PodList, error) {
	pods := &corev1.PodList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespaceAndLabel(namespace, pods, labels); err != nil {
		return nil, err
	}
	return pods, nil
}

// CheckPodsAreReady returns true if all pods are ready
func CheckPodsAreReady(pods *corev1.PodList) bool {
	for _, pod := range pods.Items {
		if !IsPodStatusConditionReady(&pod) {
			return false
		}
	}
	return true
}

// IsPodRunning returns true if pod is running
func IsPodRunning(pod *corev1.Pod) bool {
	return pod.Status.Phase == corev1.PodRunning
}

// IsPodStatusConditionReady returns true if all pod's containers are ready (really running)
func IsPodStatusConditionReady(pod *corev1.Pod) bool {
	for _, condition := range pod.Status.Conditions {
		if condition.Type == corev1.ContainersReady {
			return condition.Status == corev1.ConditionTrue
		}
	}
	return false
}

// WaitForDeploymentRunning waits for a deployment to be running, with a specific number of pod
func WaitForDeploymentRunning(namespace, dName string, podNb int, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Deployment %s running", dName), timeoutInMin,
		func() (bool, error) {
			if dc, err := GetDeployment(namespace, dName); err != nil {
				return false, err
			} else if dc == nil {
				return false, nil
			} else {
				GetLogger(namespace).Debug("Deployment has", "available replicas", dc.Status.AvailableReplicas)
				return dc.Status.Replicas == int32(podNb) && dc.Status.AvailableReplicas == int32(podNb), nil
			}
		})
}

// GetDeployment retrieves deployment with specified name in namespace
func GetDeployment(namespace, deploymentName string) (*apps.Deployment, error) {
	deployment := &apps.Deployment{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: deploymentName, Namespace: namespace}, deployment); err != nil {
		return nil, err
	} else if !exists {
		return nil, nil
	}
	return deployment, nil
}

// GetDeploymentWaiting waits for a deployment to be available, then returns it
func GetDeploymentWaiting(namespace, deploymentName string, timeoutInMin int) (deployment *apps.Deployment, err error) {
	err = WaitForOnOpenshift(namespace, fmt.Sprintf("Deployment %s is available", deploymentName), timeoutInMin,
		func() (bool, error) {
			if dc, err := GetDeployment(namespace, deploymentName); err != nil {
				return false, err
			} else if dc == nil {
				return false, nil
			}
			return true, nil
		})
	if err != nil {
		return
	}
	return GetDeployment(namespace, deploymentName)
}

// LoadResource loads the resource from provided URI and creates it in the cluster
func LoadResource(namespace, uri string, resourceRef client.Object, beforeCreate func(object interface{})) error {
	GetLogger(namespace).Debug("loadResource", "uri", uri)

	data, err := ReadFromURI(uri)
	if err != nil {
		return fmt.Errorf("Unable to read from URI %s: %v", uri, err)
	}

	if err = kubernetes.ResourceC(kubeClient).CreateFromYamlContent(data, namespace, resourceRef, beforeCreate); err != nil {
		return fmt.Errorf("Error while creating resources from file '%s': %v ", uri, err)
	}
	return nil
}

// WaitForAllPodsByDeploymentConfigToContainTextInLog waits for pods of specified deployment config to contain specified text in log
func WaitForAllPodsByDeploymentConfigToContainTextInLog(namespace, dcName, logText string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Pods for deployment config '%s' contain text '%s'", dcName, logText), timeoutInMin,
		func() (bool, error) {
			pods, err := GetPodsByDeploymentConfig(namespace, dcName)
			if err != nil {
				return false, err
			}

			// Container name is equal to deployment config name
			return checkAllPodsContainingTextInLog(namespace, pods.Items, dcName, logText)
		}, CheckPodsByDeploymentConfigInError(namespace, dcName))
}

// WaitForAllPodsByDeploymentToContainTextInLog waits for pods of specified deployment to contain specified text in log
func WaitForAllPodsByDeploymentToContainTextInLog(namespace, dName, logText string, timeoutInMin int) error {
	return waitForPodsByDeploymentToContainTextInLog(namespace, dName, logText, timeoutInMin, checkAllPodsContainingTextInLog)
}

// WaitForAnyPodsByDeploymentToContainTextInLog waits for pods of specified deployment to contain specified text in log
func WaitForAnyPodsByDeploymentToContainTextInLog(namespace, dName, logText string, timeoutInMin int) error {
	return waitForPodsByDeploymentToContainTextInLog(namespace, dName, logText, timeoutInMin, checkAnyPodsContainingTextInLog)
}

func waitForPodsByDeploymentToContainTextInLog(namespace, dName, logText string, timeoutInMin int, predicate func(string, []corev1.Pod, string, string) (bool, error)) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Pods for deployment '%s' contain text '%s'", dName, logText), timeoutInMin,
		func() (bool, error) {
			pods, err := GetPodsByDeployment(namespace, dName)
			if err != nil {
				return false, err
			}

			// Container name is equal to deployment config name
			return predicate(namespace, pods, dName, logText)
		}, CheckPodsByDeploymentInError(namespace, dName))
}

func checkAnyPodsContainingTextInLog(namespace string, pods []corev1.Pod, containerName, text string) (bool, error) {
	for _, pod := range pods {
		containsText, err := isPodContainingTextInLog(namespace, &pod, containerName, text)
		if err != nil {
			return false, err
		} else if containsText {
			return true, nil
		}
	}

	return false, nil
}

func checkAllPodsContainingTextInLog(namespace string, pods []corev1.Pod, containerName, text string) (bool, error) {
	for _, pod := range pods {
		containsText, err := isPodContainingTextInLog(namespace, &pod, containerName, text)
		if err != nil || !containsText {
			return false, err
		}
	}
	return true, nil
}

func isPodContainingTextInLog(namespace string, pod *corev1.Pod, containerName, text string) (bool, error) {
	log, err := kubernetes.PodC(kubeClient).GetLogs(namespace, pod.GetName(), containerName)
	return strings.Contains(log, text), err
}

// GetStatefulSet returns the given StatefulSet
func GetStatefulSet(namespace, name string) (*apps.StatefulSet, error) {
	statefulset := &apps.StatefulSet{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, statefulset); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for Infinispan %s: %v ", name, err)
	} else if errors.IsNotFound(err) || !exists {
		return nil, nil
	}
	return statefulset, nil
}

// IsCrdAvailable returns whether the crd is available on cluster
func IsCrdAvailable(crdName string) (bool, error) {
	crdEntity := &apiextensionsv1beta1.CustomResourceDefinition{
		ObjectMeta: metav1.ObjectMeta{
			Name: crdName,
		},
	}
	return kubernetes.ResourceC(kubeClient).Fetch(crdEntity)
}

// CreateObject creates object
func CreateObject(o client.Object) error {
	return kubernetes.ResourceC(kubeClient).Create(o)
}

// GetObjectsInNamespace returns list of objects in specific namespace based on type
func GetObjectsInNamespace(namespace string, list client.ObjectList) error {
	return kubernetes.ResourceC(kubeClient).ListWithNamespace(namespace, list)
}

// GetObjectWithKey returns object matching provided key
func GetObjectWithKey(key types.NamespacedName, o client.Object) (exists bool, err error) {
	return kubernetes.ResourceC(kubeClient).FetchWithKey(key, o)
}

// UpdateObject updates object
func UpdateObject(o client.Object) error {
	return kubernetes.ResourceC(kubeClient).Update(o)
}

// DeleteObject deletes object
func DeleteObject(o client.Object) error {
	return kubernetes.ResourceC(kubeClient).Delete(o)
}

// CreateSecret creates a new secret
func CreateSecret(namespace, name string, secretContent map[string]string) error {
	GetLogger(namespace).Info("Create Secret %s", "name", name)

	secret := &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Type:       corev1.SecretTypeOpaque,
		StringData: secretContent,
	}

	return kubernetes.ResourceC(kubeClient).Create(secret)
}

// CheckPodHasImagePullSecretWithPrefix checks that a pod has an image pull secret starting with the given prefix
func CheckPodHasImagePullSecretWithPrefix(pod *corev1.Pod, imagePullSecretPrefix string) bool {
	for _, secretRef := range pod.Spec.ImagePullSecrets {
		if strings.HasPrefix(secretRef.Name, imagePullSecretPrefix) {
			return true
		}
	}
	return false
}

// CheckPodsByDeploymentConfigInError returns a function that checks the pods error state.
func CheckPodsByDeploymentConfigInError(namespace string, dcName string) func() (bool, error) {
	return func() (bool, error) {
		pods, err := GetPodsByDeploymentConfig(namespace, dcName)
		if err != nil {
			return true, err

		}
		return checkPodsInError(pods.Items)
	}
}

// CheckPodsByDeploymentInError returns a function that checks the pods error state.
func CheckPodsByDeploymentInError(namespace string, dName string) func() (bool, error) {
	return func() (bool, error) {
		pods, err := GetPodsByDeployment(namespace, dName)
		if err != nil {
			return true, err

		}
		return checkPodsInError(pods)
	}
}

// CheckPodsWithLabelInError returns a function that checks the pods error state.
func CheckPodsWithLabelInError(namespace, labelName, labelValue string) func() (bool, error) {
	return func() (bool, error) {
		pods, err := GetPodsWithLabels(namespace, map[string]string{labelName: labelValue})
		if err != nil {
			return true, err

		}
		return checkPodsInError(pods.Items)
	}
}

// CheckPodsWithLabelsInError returns a function that checks the pods error state.
func CheckPodsWithLabelsInError(namespace string, labels map[string]string) func() (bool, error) {
	return func() (bool, error) {
		pods, err := GetPodsWithLabels(namespace, labels)
		if err != nil {
			return true, err

		}
		return checkPodsInError(pods.Items)
	}
}

// CheckPodsInNamespaceInError returns a function that checks the pods error state.
func CheckPodsInNamespaceInError(namespace string) func() (bool, error) {
	return func() (bool, error) {
		pods, err := GetPods(namespace)
		if err != nil {
			return true, err

		}
		return checkPodsInError(pods.Items)
	}
}

func checkPodsInError(pods []corev1.Pod) (bool, error) {
	for _, pod := range pods {
		if hasErrors, err := isPodInError(&pod); hasErrors {
			return true, err
		}
	}

	return false, nil
}

func isPodInError(pod *corev1.Pod) (bool, error) {
	if IsPodRunning(pod) {
		return false, nil
	}

	for _, status := range pod.Status.ContainerStatuses {
		for _, reason := range podErrorReasons {
			if status.State.Waiting != nil && status.State.Waiting.Reason == reason {
				return true, fmt.Errorf("Error in pod, reason: %s", reason)
			}
		}

	}

	return false, nil
}

func checkPodContainerHasEnvVariableWithValue(pod *corev1.Pod, containerName, envVarName, envVarValue string) bool {
	for _, container := range pod.Spec.Containers {
		if container.Name == containerName {
			for _, env := range container.Env {
				if env.Name == envVarName {
					return env.Value == envVarValue
				}
			}
		}
	}
	return false
}

// GetIngressURI returns the ingress URI
func GetIngressURI(namespace, serviceName string) (string, error) {
	ingress := &k8sv1beta1.Ingress{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: serviceName, Namespace: namespace}, ingress); err != nil {
		return "", err
	} else if !exists {
		return "", fmt.Errorf("Ingress %s does not exist in namespace %s", serviceName, namespace)
	} else if len(ingress.Spec.Rules) == 0 {
		return "", fmt.Errorf("Ingress %s does not have any rules", serviceName)
	}

	return fmt.Sprintf("http://%s:80", ingress.Spec.Rules[0].Host), nil
}

// ExposeServiceOnKubernetes adds ingress CR to expose a service
func ExposeServiceOnKubernetes(namespace, serviceName string) error {
	// Needed to retrieve service port to be used
	service, err := GetService(namespace, serviceName)
	if err != nil {
		return err
	}
	if len(service.Spec.Ports) > 1 {
		return fmt.Errorf("Service with name %s contains multiple ports, it is not clear which one should be exposed", serviceName)
	}
	port := service.Spec.Ports[0].Port

	host := serviceName
	if !config.IsLocalCluster() {
		host += fmt.Sprintf(".%s.%s", namespace, config.GetDomainSuffix())
	}

	ingress := k8sv1beta1.Ingress{
		ObjectMeta: metav1.ObjectMeta{
			Name:        serviceName,
			Namespace:   namespace,
			Annotations: map[string]string{"nginx.ingress.kubernetes.io/rewrite-target": "/"},
		},
		Spec: k8sv1beta1.IngressSpec{
			Rules: []k8sv1beta1.IngressRule{
				{
					Host: host,
					IngressRuleValue: k8sv1beta1.IngressRuleValue{
						HTTP: &k8sv1beta1.HTTPIngressRuleValue{
							Paths: []k8sv1beta1.HTTPIngressPath{
								{
									Path: "/",
									Backend: k8sv1beta1.IngressBackend{
										ServiceName: serviceName,
										ServicePort: intstr.FromInt(int(port)),
									},
								},
							},
						},
					},
				},
			},
		},
	}
	return kubernetes.ResourceC(kubeClient).Create(&ingress)
}

// WaitForOnKubernetes is a specific method
func WaitForOnKubernetes(namespace, display string, timeoutInMin int, condition func() (bool, error)) error {
	return WaitFor(namespace, display, GetKubernetesDurationFromTimeInMin(timeoutInMin), condition)
}

// GetKubernetesDurationFromTimeInMin will calculate the time depending on the configured cluster load factor
func GetKubernetesDurationFromTimeInMin(timeoutInMin int) time.Duration {
	return time.Duration(timeoutInMin*config.GetLoadFactor()) * time.Minute
}

// IsOpenshift returns whether the cluster is running on Openshift
func IsOpenshift() bool {
	return kubeClient.IsOpenshift()
}

// GetService return Service based on namespace and name
func GetService(namespace, name string) (*corev1.Service, error) {
	service := &corev1.Service{}
	if exits, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, service); err != nil {
		return nil, err
	} else if !exits {
		return nil, fmt.Errorf("Service with name %s doesn't exist in given namespace %s", name, namespace)
	}
	return service, nil
}

// GetClusterRole return ClusterRole based on name
func GetClusterRole(name string) (*rbac.ClusterRole, error) {
	clusterRole := &rbac.ClusterRole{
		ObjectMeta: metav1.ObjectMeta{
			Name: name,
		},
	}
	if exits, err := kubernetes.ResourceC(kubeClient).Fetch(clusterRole); err != nil {
		return nil, err
	} else if !exits {
		return nil, fmt.Errorf("ClusterRole with name %s doesn't exist", name)
	}
	return clusterRole, nil
}

// GetClusterRoleBinding return ClusterRoleBinding based on name
func GetClusterRoleBinding(name string) (*rbac.ClusterRoleBinding, error) {
	clusterRoleBinding := &rbac.ClusterRoleBinding{
		ObjectMeta: metav1.ObjectMeta{
			Name: name,
		},
	}
	if exits, err := kubernetes.ResourceC(kubeClient).Fetch(clusterRoleBinding); err != nil {
		return nil, err
	} else if !exits {
		return nil, fmt.Errorf("ClusterRoleBinding with name %s doesn't exist", name)
	}
	return clusterRoleBinding, nil
}

// CreateServiceAccount creates ServiceAccount
func CreateServiceAccount(namespace, name string) error {
	GetLogger(namespace).Info("Create ServiceAccount", "name", name)

	secret := &corev1.ServiceAccount{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
	}

	return kubernetes.ResourceC(kubeClient).Create(secret)
}

// CreateConfigMap creates ConfigMap
func CreateConfigMap(namespace, name string, data map[string]string, binaryData map[string][]byte) error {
	GetLogger(namespace).Info("Create ConfigMap", "name", name)

	configMap := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
		Data:       data,
		BinaryData: binaryData,
	}

	return kubernetes.ResourceC(kubeClient).Create(configMap)
}

// IsConfigMapExist returns true if ConfigMap exists
func IsConfigMapExist(key types.NamespacedName) (bool, error) {
	exists, err := GetObjectWithKey(key, &corev1.ConfigMap{})
	if err != nil {
		return false, fmt.Errorf("Error fetching ConfigMap %s in namespace %s: %v", key.Name, key.Namespace, err)
	}
	return exists, nil
}

// PruneNamespaces prunes namespaces stored in the "logs/namespace_history.log" file
func PruneNamespaces() error {
	// Create kube client
	if err := InitKubeClient(meta.GetRegisteredSchema()); err != nil {
		return err
	}

	namespaces := GetNamespacesInHistory()
	for _, namespace := range namespaces {
		if len(namespace) > 0 {
			err := DeleteNamespace(namespace)
			if err != nil {
				GetMainLogger().Error(err, "Error in deleting namespace")
			}
		}
	}

	ClearNamespaceHistory()
	return nil
}
