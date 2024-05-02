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

package kubernetes

import (
	"context"
	"fmt"

	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/labels"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	containerReasonContainerCreating = "ContainerCreating"
)

var _ DeploymentUnavailabilityReader = &deploymentUnavailabilityReader{}

// DeploymentUnavailabilityReader implementations find the reason behind a deployment failure
type DeploymentUnavailabilityReader interface {
	// ReasonMessage returns the reason message in string format fetched from the culprit resource. For example, a Container status.
	ReasonMessage() (string, error)
}

// DeploymentTroubleshooter creates a new DeploymentUnavailabilityReader for finding out why a deployment failed
func DeploymentTroubleshooter(client client.Client, deployment *v1.Deployment, container string) DeploymentUnavailabilityReader {
	return &deploymentUnavailabilityReader{
		c:          client,
		deployment: deployment,
		container:  container,
	}
}

type deploymentUnavailabilityReader struct {
	c          client.Client
	deployment *v1.Deployment
	container  string
}

// ReasonMessage tries to find a human-readable reason message for why the deployment is not available or in a failed state.
// This implementation fetches the given container status for this information.
// Returning an empty string means that no reason has been found in the underlying pods.
//
// See: https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/#container-states
//
// Future implementations might look in other objects for a more specific reason.
// Additionally, future work might involve returning a typed Reason, so controllers may take actions depending on what have happened.
func (d deploymentUnavailabilityReader) ReasonMessage() (string, error) {
	podList := &corev1.PodList{}
	// ideally we should get the latest replicaset, then the pods.
	// problem is that we don't have a reliable field to get this information,
	// it's in a message within the deployment status
	// since this use case is only to show the deployment problem for user's troubleshooting, it's ok showing all of them.
	// additionally, we are using a unique label identifier for matching
	opts := &client.ListOptions{
		LabelSelector: labels.SelectorFromSet(d.deployment.Spec.Selector.MatchLabels),
		Namespace:     d.deployment.Namespace,
	}

	if err := d.c.List(context.TODO(), podList, opts); err != nil {
		return "", err
	}

	for _, pod := range podList.Items {
		if pod.Status.Phase == corev1.PodRunning || pod.Status.Phase == corev1.PodSucceeded {
			continue
		}
		for _, container := range pod.Status.ContainerStatuses {
			if container.Name == d.container {
				if !container.Ready {
					if container.State.Waiting != nil && container.State.Waiting.Reason != containerReasonContainerCreating {
						return fmt.Sprintf("ContainerNotReady: (%s) %s", container.State.Waiting.Reason, container.State.Waiting.Message), nil
					}
					if container.State.Terminated != nil && container.State.Terminated.ExitCode > 0 {
						return fmt.Sprintf("ContainerNotReady: (%s) %s", container.State.Terminated.Reason, container.State.Terminated.Message), nil
					}
				}
			}
		}
	}

	return "", nil
}
