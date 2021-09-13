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

package kogitoinfra

import (
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v12 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestConfigMapReferenceReconciler_Reconcile(t *testing.T) {
	ns := t.Name()
	infraInstance := &v1beta1.KogitoInfra{
		ObjectMeta: v1.ObjectMeta{
			Name:      "kogito-kafka",
			Namespace: ns,
		},
		Spec: v1beta1.KogitoInfraSpec{
			ConfigMapEnvFromReferences: []string{
				"my-config-1",
				"my-config-2",
			},
			ConfigMapVolumeReferences: []v1beta1.VolumeReference{
				{
					Name:      "volume-reference-1",
					MountPath: "/testPath",
				},
				{
					Name: "volume-reference-2",
				},
			},
		},
	}

	myConfig1 := &v12.ConfigMap{
		ObjectMeta: v1.ObjectMeta{
			Name:      "my-config-1",
			Namespace: ns,
		},
	}
	myConfig2 := &v12.ConfigMap{
		ObjectMeta: v1.ObjectMeta{
			Name:      "my-config-2",
			Namespace: ns,
		},
	}
	volumeReference1 := &v12.ConfigMap{
		ObjectMeta: v1.ObjectMeta{
			Name:      "volume-reference-1",
			Namespace: ns,
		},
	}
	volumeReference2 := &v12.ConfigMap{
		ObjectMeta: v1.ObjectMeta{
			Name:      "volume-reference-2",
			Namespace: ns,
		},
	}

	cli := test.NewFakeClientBuilder().AddK8sObjects(infraInstance, myConfig1, myConfig2, volumeReference1, volumeReference2).Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: infraInstance,
	}

	configMapReferenceReconciler := initConfigMapReferenceReconciler(infraContext)
	err := configMapReferenceReconciler.Reconcile()
	assert.NoError(t, err)
	assert.Equal(t, 2, len(infraInstance.GetStatus().GetConfigMapEnvFromReferences()))
	assert.Equal(t, 2, len(infraInstance.GetStatus().GetConfigMapVolumeReferences()))
}

func TestConfigMapReferenceReconciler_WrongConfigMapName(t *testing.T) {
	ns := t.Name()
	infraInstance := &v1beta1.KogitoInfra{
		ObjectMeta: v1.ObjectMeta{
			Name:      "kogito-kafka",
			Namespace: ns,
		},
		Spec: v1beta1.KogitoInfraSpec{
			ConfigMapEnvFromReferences: []string{
				"my-config-1",
			},
		},
	}

	cli := test.NewFakeClientBuilder().AddK8sObjects().Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: infraInstance,
	}

	configMapReferenceReconciler := initConfigMapReferenceReconciler(infraContext)
	err := configMapReferenceReconciler.Reconcile()
	assert.Errorf(t, err, "Configmap resource(my-config-1) not found in namespace %s", ns)
}
