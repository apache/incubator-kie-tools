// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package kubernetes

import (
	"context"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"

	policyv1 "k8s.io/api/policy/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
)

// IsEmptyPodDisruptionBudgetSpec returns true if the PodDisruptionBudgetSpec is nil or has no configured values at all,
// false in any other case.
func IsEmptyPodDisruptionBudgetSpec(spec *operatorapi.PodDisruptionBudgetSpec) bool {
	return spec == nil || (spec.MinAvailable == nil && spec.MaxUnavailable == nil)
}

// ApplyPodDisruptionBudgetSpec applies an operatorapi.PodDisruptionBudgetSpec to the PodDisruptionBudget.
func ApplyPodDisruptionBudgetSpec(pdb *policyv1.PodDisruptionBudget, spec *operatorapi.PodDisruptionBudgetSpec) {
	if spec.MinAvailable != nil {
		pdb.Spec.MinAvailable = spec.MinAvailable
		pdb.Spec.MaxUnavailable = nil
	} else {
		pdb.Spec.MaxUnavailable = spec.MaxUnavailable
		pdb.Spec.MinAvailable = nil
	}
}

// SafeDeletePodDisruptionBudget deletes a potentially existing PodDisruptionBudget, ignoring the not existing error.
func SafeDeletePodDisruptionBudget(ctx context.Context, c client.Client, namespace, name string) error {
	err := c.Delete(ctx, &policyv1.PodDisruptionBudget{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
	})
	if err != nil {
		if errors.IsNotFound(err) {
			klog.V(log.D).Infof("PodDisruptionBudget %s/%s was already deleted or never existed.", namespace, name)
			return nil
		} else {
			return err
		}
	}
	return nil
}
