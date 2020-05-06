// Copyright 2019 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package framework

import (
	"fmt"
	"strings"

	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/api/core/v1"

	"k8s.io/apimachinery/pkg/api/resource"
)

// WaitForPodsToHaveResources waits for pods to have the expected resources
func WaitForPodsToHaveResources(namespace, dcName string, expected v1.ResourceRequirements, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Pods for deployment config '%s' to have resources", dcName), timeoutInMin,
		func() (bool, error) {
			return checkResourcesInPods(namespace, dcName, expected)
		}, CheckPodsByDeploymentConfigInError(namespace, dcName))
}

// WaitForBuildConfigToHaveResources waits for build config to have the expected resources
func WaitForBuildConfigToHaveResources(namespace, buildConfigName string, expected v1.ResourceRequirements, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("BuildConfig '%s' to have resources", buildConfigName), timeoutInMin,
		func() (bool, error) {
			return checkResourcesInBuildConfig(namespace, buildConfigName, expected)
		})
}

// ToResourceRequirements parses the requests and limits into corev1.ResourceRequirements
func ToResourceRequirements(requests string, limits string) corev1.ResourceRequirements {
	return corev1.ResourceRequirements{Limits: toResourceList(limits), Requests: toResourceList(requests)}
}

func toResourceList(resources string) corev1.ResourceList {
	resourceList := corev1.ResourceList{}
	options := strings.Split(resources, ",")
	for _, option := range options {
		assignment := strings.Split(option, "=")
		resourceList[v1.ResourceName(assignment[0])] = resource.MustParse(assignment[1])
	}

	return resourceList
}

func checkResourcesInBuildConfig(namespace string, buildConfigName string, expected corev1.ResourceRequirements) (bool, error) {
	bc, err := getBuildConfig(namespace, buildConfigName)
	if err != nil {
		return false, err
	} else if bc == nil {
		return false, nil
	}

	return isResourceLimitsAndRequestsEqual(bc.Spec.CommonSpec.Resources, expected), nil
}

func checkResourcesInPods(namespace string, dcName string, expected corev1.ResourceRequirements) (bool, error) {
	pods, err := GetPodsByDeploymentConfig(namespace, dcName)
	if err != nil {
		return false, err
	} else if pods == nil {
		return false, nil
	} else {
		for _, pod := range pods.Items {
			if !checkContainersResources(pod.Spec.Containers, expected) {
				return false, nil
			}
		}

		return true, nil
	}
}

func checkContainersResources(containers []v1.Container, expected corev1.ResourceRequirements) bool {
	for _, container := range containers {
		if !isResourceLimitsAndRequestsEqual(container.Resources, expected) {
			return false
		}
	}

	return true
}

func isResourceLimitsAndRequestsEqual(actual corev1.ResourceRequirements, expected corev1.ResourceRequirements) bool {
	return isResourceRequirementsEqual(actual.Limits, expected.Limits) && isResourceRequirementsEqual(actual.Requests, expected.Requests)
}

func isResourceRequirementsEqual(actual corev1.ResourceList, expected corev1.ResourceList) bool {
	if expected == nil {
		return true
	}

	return actual != nil && isQuantityEqual(actual.Memory(), expected.Memory()) && isQuantityEqual(actual.Cpu(), expected.Cpu())
}

func isQuantityEqual(actual *resource.Quantity, expected *resource.Quantity) bool {
	if expected == nil {
		return true
	}
	expectedValue := expected.Value()
	actualValue := actual.Value()

	return actual != nil && expectedValue == actualValue
}
