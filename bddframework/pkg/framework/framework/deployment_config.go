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
	v1 "github.com/openshift/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
)

// IsSafeToRollOutDeploymentConfig checks if the given `DeploymentConfig` has successfully rolled out to the latest version, thus
// it's safe to perform a new roll out
func IsSafeToRollOutDeploymentConfig(dc *v1.DeploymentConfig) bool {
	if dc == nil {
		return false
	}

	for _, condition := range dc.Status.Conditions {
		if condition.Type == v1.DeploymentProgressing &&
			condition.Status == corev1.ConditionTrue &&
			dc.Status.Replicas == dc.Status.ReadyReplicas {
			return true
		}
	}

	return false
}
