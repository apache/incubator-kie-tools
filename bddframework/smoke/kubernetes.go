// Copyright 2020 Red Hat, Inc. and/or its affiliates
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

package main

import (
	"fmt"
	"time"

	corev1 "k8s.io/api/core/v1"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
)

// waitForPods waits for pods with specific label to be available and running
func waitForPods(namespace, labelName, labelValue string, numberOfPods, timeoutInMin int) error {
	return waitFor(namespace, fmt.Sprintf("Pods with label name '%s' and value '%s' available and running", labelName, labelValue), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		pods, err := getPods(namespace, map[string]string{labelName: labelValue})
		if err != nil || (len(pods.Items) != numberOfPods) {
			return false, err
		}

		return checkPodsAreRunning(pods), nil
	})
}

// getPods retrieves pods based on label name and value
func getPods(namespace string, labels map[string]string) (*corev1.PodList, error) {
	pods := &corev1.PodList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespaceAndLabel(namespace, pods, labels); err != nil {
		return nil, err
	}
	return pods, nil
}

// checkPodsAreRunning returns true if all pods are running
func checkPodsAreRunning(pods *corev1.PodList) bool {
	for _, pod := range pods.Items {
		if pod.Status.Phase != corev1.PodRunning {
			return false
		}
	}
	return true
}
