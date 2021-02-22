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
	ispn "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	"github.com/stretchr/testify/assert"
	"io/ioutil"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"testing"
)

func Test_Reconcile_Infinispan(t *testing.T) {
	kogitoInfra := &v1beta1.KogitoInfra{
		ObjectMeta: v1.ObjectMeta{Name: "kogito-infinispan", Namespace: t.Name()},
		Spec: v1beta1.KogitoInfraSpec{
			Resource: v1beta1.Resource{
				APIVersion: infrastructure.InfinispanAPIVersion,
				Kind:       infrastructure.InfinispanKind,
				Name:       "kogito-infinispan",
				Namespace:  t.Name(),
			},
		},
	}
	crtFile, err := ioutil.ReadFile("./testdata/tls.crt")
	assert.NoError(t, err)
	tlsSecret := &corev1.Secret{
		ObjectMeta: v1.ObjectMeta{
			Name:      "secret-with-truststore",
			Namespace: t.Name(),
		},
		Data: map[string][]byte{truststoreSecretKey: crtFile},
	}
	deployedInfinispan := &ispn.Infinispan{
		ObjectMeta: v1.ObjectMeta{Name: "kogito-infinispan", Namespace: t.Name()},
		Status: ispn.InfinispanStatus{
			Security: ispn.InfinispanSecurity{
				EndpointEncryption: ispn.EndpointEncryption{
					CertSecretName: tlsSecret.Name,
				},
			},
			Conditions: []ispn.InfinispanCondition{
				{
					Status: string(v1.ConditionTrue),
				},
			},
		},
	}

	deployedCustomSecret := &corev1.Secret{
		ObjectMeta: v1.ObjectMeta{Name: "kogito-infinispan-credential", Namespace: t.Name()},
	}

	infinispanService := &corev1.Service{
		ObjectMeta: v1.ObjectMeta{Name: "kogito-infinispan", Namespace: t.Name()},
		Spec: corev1.ServiceSpec{
			Ports: []corev1.ServicePort{
				{
					TargetPort: intstr.FromInt(11222),
				},
			},
		},
	}

	client := test.NewFakeClientBuilder().
		AddK8sObjects(kogitoInfra, deployedInfinispan, deployedCustomSecret, infinispanService, tlsSecret).
		Build()

	context := &operator.Context{
		Client: client,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	r := &infinispanInfraReconciler{
		infraContext: infraContext{
			Context:  context,
			instance: kogitoInfra,
		},
	}

	requeue, err := r.Reconcile()
	assert.NoError(t, err)
	assert.False(t, requeue)

	exists, err := kubernetes.ResourceC(client).Fetch(kogitoInfra)
	assert.NoError(t, err)
	assert.True(t, exists)

}
