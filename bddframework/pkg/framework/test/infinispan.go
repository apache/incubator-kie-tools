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

package test

import (
	infinispan "github.com/kiegroup/kogito-operator/core/infrastructure/infinispan/v1"
	"io/ioutil"
	v13 "k8s.io/api/core/v1"
	v12 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
)

// CreateFakeInfinispan ...
func CreateFakeInfinispan(namespace string) *infinispan.Infinispan {
	trueValue := true
	return &infinispan.Infinispan{
		ObjectMeta: v12.ObjectMeta{
			Name:      "kogito-infinispan",
			Namespace: namespace,
		},
		Spec: infinispan.InfinispanSpec{
			Security: infinispan.InfinispanSecurity{
				EndpointAuthentication: &trueValue,
				EndpointSecretName:     "kogito-infinispan-generated-secret",
				EndpointEncryption: &infinispan.EndpointEncryption{
					CertSecretName: "infinispan-cert-secret",
				},
			},
		},
		Status: infinispan.InfinispanStatus{
			Conditions: []infinispan.InfinispanCondition{
				{
					Type:   infinispan.ConditionGracefulShutdown,
					Status: v12.ConditionFalse,
				},
				{
					Type:   infinispan.ConditionPrelimChecksPassed,
					Status: v12.ConditionTrue,
				},
				{
					Type:   infinispan.ConditionUpgrade,
					Status: v12.ConditionFalse,
				},
				{
					Type:   infinispan.ConditionStopping,
					Status: v12.ConditionFalse,
				},
				{
					Type:   infinispan.ConditionWellFormed,
					Status: v12.ConditionTrue,
				},
			},
		},
	}
}

// CreateFakeInfinispanService ...
func CreateFakeInfinispanService(namespace string) *v13.Service {
	return &v13.Service{
		ObjectMeta: v12.ObjectMeta{
			Name:      "kogito-infinispan",
			Namespace: namespace,
		},
		Spec: v13.ServiceSpec{
			Ports: []v13.ServicePort{
				{
					TargetPort: intstr.FromInt(11222),
				},
			},
		},
	}
}

// CreateFakeInfinispanCredentialSecret ...
func CreateFakeInfinispanCredentialSecret(namespace string) *v13.Secret {
	return &v13.Secret{
		ObjectMeta: v12.ObjectMeta{
			Name:      "kogito-infinispan-generated-secret",
			Namespace: namespace,
		},
		Data: map[string][]byte{
			"identities.yaml": []byte("credentials:\n- username: developer\n  password: MUMVuROAwrKUYDMV\n  roles:\n  - admin"),
		},
	}
}

// CreateFakeInfinispanCertSecret ...
func CreateFakeInfinispanCertSecret(namespace string) (*v13.Secret, error) {
	crtFile, err := ioutil.ReadFile("./testdata/tls.crt")
	if err != nil {
		return nil, err
	}
	return &v13.Secret{
		ObjectMeta: v12.ObjectMeta{
			Name:      "infinispan-cert-secret",
			Namespace: namespace,
		},
		Data: map[string][]byte{
			"tls.crt": crtFile,
		},
	}, nil
}
