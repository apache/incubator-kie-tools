// Copyright 2020 Red Hat, Inc. and/or its affiliates
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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// CreateFakeKogitoKafka create fake kogito infra instance for kafka
func CreateFakeKogitoKafka(namespace string) api.KogitoInfraInterface {
	return &v1beta1.KogitoInfra{
		ObjectMeta: v1.ObjectMeta{
			Name:      "kogito-kafka",
			Namespace: namespace,
		},
		Spec: v1beta1.KogitoInfraSpec{
			Resource: v1beta1.Resource{
				Kind:       "Kafka",
				APIVersion: "kafka.strimzi.io/v1beta1",
			},
		},
		Status: v1beta1.KogitoInfraStatus{
			RuntimeProperties: map[api.RuntimeType]v1beta1.RuntimeProperties{
				api.QuarkusRuntimeType: {
					AppProps: map[string]string{
						"kafka.bootstrap.servers": "kogito-kafka-kafka-bootstrap.test.svc:9092",
					},
					Env: []corev1.EnvVar{
						{
							Name:  "ENABLE_EVENTS",
							Value: "true",
						},
					},
				},
			},
			Condition: v1beta1.KogitoInfraCondition{
				Type:   api.SuccessInfraConditionType,
				Status: v1.StatusSuccess,
				Reason: "",
			},
		},
	}
}

// CreateFakeKogitoInfinispan create fake kogito infra instance for Infinispan
func CreateFakeKogitoInfinispan(namespace string) api.KogitoInfraInterface {
	return &v1beta1.KogitoInfra{
		ObjectMeta: v1.ObjectMeta{
			Name:      "kogito-Infinispan",
			Namespace: namespace,
		},
		Spec: v1beta1.KogitoInfraSpec{
			Resource: v1beta1.Resource{
				Kind:       "Infinispan",
				APIVersion: "infinispan.org/v1",
			},
		},
		Status: v1beta1.KogitoInfraStatus{
			RuntimeProperties: map[api.RuntimeType]v1beta1.RuntimeProperties{
				api.QuarkusRuntimeType: {
					AppProps: map[string]string{
						"quarkus.infinispan-client.server-list": "infinispanInstance:11222",
					},
					Env: []corev1.EnvVar{
						{
							Name:  "ENABLE_PERSISTENCE",
							Value: "true",
						},
					},
				},
			},
			Volumes: []v1beta1.KogitoInfraVolume{
				{
					Mount: corev1.VolumeMount{
						Name:      "tls-configuration",
						ReadOnly:  true,
						MountPath: "/home/kogito/certs",
						SubPath:   "truststore.p12",
					},
					NamedVolume: v1beta1.ConfigVolume{
						Name: "tls-configuration",
						ConfigVolumeSource: v1beta1.ConfigVolumeSource{
							Secret: &corev1.SecretVolumeSource{
								SecretName: "infinispan-secret",
								Items: []corev1.KeyToPath{
									{
										Key:  "tls.crt",
										Path: "tls.crt",
									},
								},
							},
						},
					},
				},
			},
			Condition: v1beta1.KogitoInfraCondition{
				Type:   api.SuccessInfraConditionType,
				Status: v1.StatusSuccess,
				Reason: "",
			},
		},
	}
}

// CreateFakeKogitoKnative create fake kogito infra instance for Knative
func CreateFakeKogitoKnative(namespace string) api.KogitoInfraInterface {
	return &v1beta1.KogitoInfra{
		ObjectMeta: v1.ObjectMeta{
			Name:      "kogito-knative",
			Namespace: namespace,
		},
		Spec: v1beta1.KogitoInfraSpec{
			Resource: v1beta1.Resource{
				Kind:       "Broker",
				APIVersion: "eventing.knative.dev/v1",
			},
		},
		Status: v1beta1.KogitoInfraStatus{
			Condition: v1beta1.KogitoInfraCondition{
				Type:   api.SuccessInfraConditionType,
				Status: v1.StatusSuccess,
				Reason: "",
			},
		},
	}
}
