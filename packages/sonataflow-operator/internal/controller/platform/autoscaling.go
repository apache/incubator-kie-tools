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

	v1 "k8s.io/api/core/v1"
	"k8s.io/klog/v2"

	autoscalingv2 "k8s.io/api/autoscaling/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
)

// findHPAForDeployment returns the HorizontalPodAutoscaler targeting a deployment in a given namespace, or nil if it
// doesn't exist.
// Note: By k8s definition, the HorizontalPodAutoscaler must belong to the same namespace as the managed deployment.
func findHPAForDeployment(ctx context.Context, c client.Client, namespace string, name string) (*autoscalingv2.HorizontalPodAutoscaler, error) {
	klog.V(log.D).Infof("Querying HorizontalPodAutoscalers in namespace: %s", namespace)
	var hpaList autoscalingv2.HorizontalPodAutoscalerList
	if err := c.List(ctx, &hpaList, client.InNamespace(namespace)); err != nil {
		return nil, err
	}
	klog.V(log.D).Infof("Total number of returned HorizontalPodAutoscalers is: %d", len(hpaList.Items))
	for _, hpa := range hpaList.Items {
		ref := hpa.Spec.ScaleTargetRef
		klog.V(log.D).Infof("Checking if HorizontalPodAutoscaler name: %s, ref.Kind: %s, ref.Name: %s, ref.APIVersion: %s, targets deployment: %s.", hpa.Name, ref.Kind, ref.Name, ref.APIVersion, name)
		if ref.Kind == "Deployment" && ref.Name == name && ref.APIVersion == "apps/v1" {
			klog.V(log.D).Infof("HorizontalPodAutoscaler name: %s targets deployment: %s.", hpa.Name, name)
			return &hpa, nil
		}
	}
	klog.V(log.D).Infof("No HorizontalPodAutoscaler targets deployment: %s in namespace: %s.", name, namespace)
	return nil, nil
}

// hpaIsActive returns true if the HorizontalPodAutoscaler is active.
func hpaIsActive(hpa *autoscalingv2.HorizontalPodAutoscaler) bool {
	klog.V(log.D).Infof("Checking if HorizontalPodAutoscaler is Active.")
	for _, cond := range hpa.Status.Conditions {
		klog.V(log.D).Infof("Checking Status condition type: %s, %s.", cond.Type, cond.Status)
		if cond.Type == autoscalingv2.ScalingActive {
			return cond.Status == v1.ConditionTrue
		}
	}
	return false
}

// hpaIsWorking returns true if the HorizontalPodAutoscaler has started to take care of the scaling for the
// corresponding target ref. At this point, our controllers must transfer the control to the HorizontalPodAutoscaler
// and let it manage the replicas.
func hpaIsWorking(hpa *autoscalingv2.HorizontalPodAutoscaler) bool {
	return hpaIsActive(hpa) || hpa.Status.DesiredReplicas > 0
}
