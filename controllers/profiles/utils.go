// Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package profiles

import (
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
)

// rolloutDeploymentIfCMChangedMutateVisitor forces a pod refresh if the workflow definition suffered any changes.
// This method can be used as an alternative to the Kubernetes ConfigMap refresher.
//
// See: https://kubernetes.io/docs/concepts/configuration/configmap/#mounted-configmaps-are-updated-automatically
func rolloutDeploymentIfCMChangedMutateVisitor(cmOperationResult controllerutil.OperationResult) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if cmOperationResult == controllerutil.OperationResultUpdated {
				deployment := object.(*appsv1.Deployment)
				err := kubeutil.MarkDeploymentToRollout(deployment)
				return err
			}
			return nil
		}
	}
}

// findNodePortFromPorts returns the first Port in an array of ServicePort
func findNodePortFromPorts(ports []v1.ServicePort) int {
	if ports != nil && len(ports) > 0 {
		for _, p := range ports {
			if p.NodePort != 0 {
				return int(p.NodePort)
			}
		}
	}
	//If we are not able to find a NodePort let's return the zero value
	return 0
}

// IsDevProfile detects if the workflow is using the Dev profile or not
func IsDevProfile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	profile := workflow.Annotations[metadata.Profile]
	if len(profile) == 0 {
		return false
	}
	return Profile(profile) == Development
}
