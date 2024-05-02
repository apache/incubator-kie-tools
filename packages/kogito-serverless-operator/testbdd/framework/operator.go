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

	v1 "k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"

	framework "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/operator"
)

const (
	sonataFlowOperatorTimeoutInMin = 5

	sonataFlowOperatorName                  = "sonataflow-operator"
	sonataFlowOperatorDeploymentName        = sonataFlowOperatorName + "-controller-manager"
	sonataFlowOperatorPullImageSecretPrefix = sonataFlowOperatorName + "-dockercfg"
)

// WaitForSonataFlowOperatorRunning waits for Kogito operator running
func WaitForSonataFlowOperatorRunning(namespace string) error {
	return framework.WaitForOnOpenshift(namespace, "SonataFlow operator running", sonataFlowOperatorTimeoutInMin,
		func() (bool, error) {
			running, err := IsSonataFlowOperatorRunning(namespace)
			if err != nil {
				return false, err
			}

			// If not running, make sure the image pull secret is present in pod
			// If not present, delete the pod to allow its reconstruction with correct pull secret
			// Note that this is specific to Openshift
			if !running && framework.IsOpenshift() {
				podList, err := framework.GetPodsWithLabels(namespace, map[string]string{"name": sonataFlowOperatorName})
				if err != nil {
					framework.GetLogger(namespace).Error(err, "Error while trying to retrieve Kogito Operator pods")
					return false, nil
				}
				for _, pod := range podList.Items {
					if !framework.CheckPodHasImagePullSecretWithPrefix(&pod, sonataFlowOperatorPullImageSecretPrefix) {
						// Delete pod as it has been misconfigured (missing pull secret)
						framework.GetLogger(namespace).Info("Kogito Operator pod does not have the image pull secret needed. Deleting it to renew it.")
						err := framework.DeleteObject(&pod)
						if err != nil {
							framework.GetLogger(namespace).Error(err, "Error while trying to delete Kogito Operator pod")
							return false, nil
						}
					}
				}
			}
			return running, nil
		})
}

// IsSonataFlowOperatorRunning returns whether SonataFlow operator is running
func IsSonataFlowOperatorRunning(namespace string) (bool, error) {
	exists, err := SonataFlowOperatorExists(namespace)
	if err != nil {
		if exists {
			return false, nil
		}
		return false, err
	}

	return exists, nil
}

// SonataFlowOperatorExists returns whether SonataFlow operator exists and is running. If it is existing but not running, it returns true and an error
func SonataFlowOperatorExists(namespace string) (bool, error) {
	framework.GetLogger(namespace).Debug("Checking Operator", "Deployment", sonataFlowOperatorDeploymentName, "Namespace", namespace)

	operatorDeployment := &v1.Deployment{}
	namespacedName := types.NamespacedName{Namespace: namespace, Name: sonataFlowOperatorDeploymentName} // done to reuse the framework function
	if exists, err := framework.GetObjectWithKey(namespacedName, operatorDeployment); err != nil {
		return false, fmt.Errorf("Error while trying to look for Deploment %s: %v ", sonataFlowOperatorDeploymentName, err)
	} else if !exists {
		return false, nil
	}

	if operatorDeployment.Status.AvailableReplicas == 0 {
		return true, fmt.Errorf("%s Operator seems to be created in the namespace '%s', but there's no available pods replicas deployed ", operator.Name, namespace)
	}

	if operatorDeployment.Status.AvailableReplicas != 1 {
		return false, fmt.Errorf("Unexpected number of pods for %s Operator. Expected %d but got %d ", operator.Name, 1, operatorDeployment.Status.AvailableReplicas)
	}

	return true, nil
}
