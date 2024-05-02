// Copyright 2024 Apache Software Foundation (ASF)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package properties

import (
	"testing"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func Test_resolvePlatformWorkflowProperties(t *testing.T) {
	secret := &v1.Secret{
		ObjectMeta: metav1.ObjectMeta{Name: "secretPlatformTest", Namespace: t.Name()},
		Data:       map[string][]byte{"my-key": []byte("secret")},
	}
	cm := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{Name: "configMapPlatformTest", Namespace: t.Name()},
		Data:       map[string]string{"my-key": "value"},
	}

	platform := test.GetBasePlatform()
	platform.Namespace = t.Name()
	platform.Spec.Properties = &v1alpha08.PropertyPlatformSpec{
		Flow: []v1alpha08.PropertyVar{
			{
				Name:  "quarkus.log.category",
				Value: "DEBUG",
			},
			{
				Name: "quarkus.custom.property",
				ValueFrom: &v1alpha08.PropertyVarSource{
					ConfigMapKeyRef: &v1.ConfigMapKeySelector{
						Key:                  "my-key",
						LocalObjectReference: v1.LocalObjectReference{Name: "configMapPlatformTest"},
					},
				},
			},
			{
				Name: "quarkus.custom.secret",
				ValueFrom: &v1alpha08.PropertyVarSource{
					SecretKeyRef: &v1.SecretKeySelector{
						Key:                  "my-key",
						LocalObjectReference: v1.LocalObjectReference{Name: "secretPlatformTest"},
					},
				},
			},
		},
	}

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(platform, secret, cm).WithStatusSubresource(platform).Build()
	utils.SetClient(client)

	props, err := resolvePlatformWorkflowProperties(platform)
	assert.NoError(t, err)
	assert.NotNil(t, props)

	assertHasProperty(t, props, "quarkus.log.category", "DEBUG")
	assertHasProperty(t, props, "quarkus.custom.property", "value")
	assertHasProperty(t, props, "quarkus.custom.secret", "secret")
}

func Test_resolvePlatformWorkflowProperties_RefNotFound(t *testing.T) {
	platform := test.GetBasePlatform()
	platform.Namespace = t.Name()
	platform.Spec.Properties = &v1alpha08.PropertyPlatformSpec{
		Flow: []v1alpha08.PropertyVar{
			{
				Name:  "quarkus.log.category",
				Value: "DEBUG",
			},
			{
				Name: "quarkus.custom.property",
				ValueFrom: &v1alpha08.PropertyVarSource{
					ConfigMapKeyRef: &v1.ConfigMapKeySelector{
						Key:                  "my-key",
						LocalObjectReference: v1.LocalObjectReference{Name: "configMapPlatformTest"},
					},
				},
			},
			{
				Name: "quarkus.custom.secret",
				ValueFrom: &v1alpha08.PropertyVarSource{
					SecretKeyRef: &v1.SecretKeySelector{
						Key:                  "my-key",
						LocalObjectReference: v1.LocalObjectReference{Name: "secretPlatformTest"},
					},
				},
			},
		},
	}

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(platform).WithStatusSubresource(platform).Build()
	utils.SetClient(client)

	props, err := resolvePlatformWorkflowProperties(platform)
	assert.NoError(t, err)
	assert.NotNil(t, props)

	assertHasProperty(t, props, "quarkus.log.category", "DEBUG")
	assertHasProperty(t, props, "quarkus.custom.property", "")
	assertHasProperty(t, props, "quarkus.custom.secret", "")
}
