// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package v1beta1

import (
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/stretchr/testify/assert"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestKogitoInfra_Spec(t *testing.T) {
	instance := &KogitoInfra{
		Spec: KogitoInfraSpec{
			Resource: &InfraResource{
				APIVersion: "infinispan.org/v1",
				Kind:       "Infinispan",
				Name:       "test-infinispan",
				Namespace:  t.Name(),
			},
			InfraProperties: map[string]string{
				"key1": "value1",
				"key2": "value2",
			},
		},
	}

	spec := instance.GetSpec()
	assert.Equal(t, "test-infinispan", spec.GetResource().GetName())
	assert.Equal(t, "infinispan.org/v1", spec.GetResource().GetAPIVersion())
	assert.Equal(t, "Infinispan", spec.GetResource().GetKind())
	assert.Equal(t, t.Name(), spec.GetResource().GetNamespace())
	assert.Equal(t, 2, len(spec.GetInfraProperties()))
	assert.Equal(t, "value1", spec.GetInfraProperties()["key1"])
	assert.Equal(t, "value2", spec.GetInfraProperties()["key2"])
}

func TestKogitoInfra_Status(t *testing.T) {
	instance1 := &KogitoInfra{
		Status: KogitoInfraStatus{
			Conditions: &[]metav1.Condition{
				{
					Type:    string(api.KogitoInfraConfigured),
					Status:  metav1.ConditionTrue,
					Reason:  string(api.ReconciliationFailure),
					Message: "Infra success",
				},
			},
			ConfigMapVolumeReferences: []VolumeReference{
				{
					Name: "configMap1",
				},
				{
					Name: "configMap2",
				},
			},
		},
	}

	configMapReferences := instance1.GetStatus().GetConfigMapVolumeReferences()
	assert.Equal(t, 2, len(configMapReferences))
	assert.Equal(t, "configMap1", configMapReferences[0].GetName())
	assert.Equal(t, "configMap2", configMapReferences[1].GetName())
}
