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

package kogitoservice

import (
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"testing"
)

const (
	customProbePort = 9000
)

func TestGetProbeForKogitoService_DefaultConfiguration(t *testing.T) {
	serviceDefinition := ServiceDefinition{
		HealthCheckProbe: QuarkusHealthCheckProbe,
	}
	service := test.CreateFakeKogitoRuntime(t.Name())
	healthCheckProbe := getProbeForKogitoService(serviceDefinition, service)
	livenessProbe := healthCheckProbe.liveness
	readinessProbe := healthCheckProbe.readiness

	assert.Equal(t, quarkusProbeReadinessPath, readinessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}, readinessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, readinessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(1), readinessProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), readinessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), readinessProbe.SuccessThreshold)
	assert.Equal(t, int32(3), readinessProbe.FailureThreshold)

	assert.Equal(t, quarkusProbeLivenessPath, livenessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}, livenessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, livenessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(1), livenessProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), livenessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), livenessProbe.SuccessThreshold)
	assert.Equal(t, int32(3), livenessProbe.FailureThreshold)
}

func TestGetProbeForKogitoService_CustomConfiguration(t *testing.T) {
	serviceDefinition := ServiceDefinition{
		HealthCheckProbe: QuarkusHealthCheckProbe,
	}
	customLivenessProbePath := "/live"
	customReadinessProbePath := "/ready"

	service := test.CreateFakeKogitoRuntime(t.Name())
	service.GetSpec().SetProbes(&v1beta1.KogitoProbe{
		LivenessProbe: corev1.Probe{
			Handler: corev1.Handler{
				HTTPGet: &corev1.HTTPGetAction{
					Path:   customLivenessProbePath,
					Port:   intstr.IntOrString{IntVal: customProbePort},
					Scheme: corev1.URISchemeHTTPS,
				},
			},
			InitialDelaySeconds: 100,
			TimeoutSeconds:      10,
			PeriodSeconds:       5,
			SuccessThreshold:    1,
			FailureThreshold:    5,
		},
		ReadinessProbe: corev1.Probe{
			Handler: corev1.Handler{
				HTTPGet: &corev1.HTTPGetAction{
					Path:   customReadinessProbePath,
					Port:   intstr.IntOrString{IntVal: customProbePort},
					Scheme: corev1.URISchemeHTTPS,
				},
			},
			InitialDelaySeconds: 200,
			TimeoutSeconds:      20,
			PeriodSeconds:       25,
			SuccessThreshold:    22,
			FailureThreshold:    25,
		},
	})
	healthCheckProbe := getProbeForKogitoService(serviceDefinition, service)
	livenessProbe := healthCheckProbe.liveness
	readinessProbe := healthCheckProbe.readiness

	assert.Equal(t, customLivenessProbePath, livenessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, livenessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTPS, livenessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(100), livenessProbe.InitialDelaySeconds)
	assert.Equal(t, int32(10), livenessProbe.TimeoutSeconds)
	assert.Equal(t, int32(5), livenessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), livenessProbe.SuccessThreshold)
	assert.Equal(t, int32(5), livenessProbe.FailureThreshold)

	assert.Equal(t, customReadinessProbePath, readinessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, readinessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTPS, readinessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(200), readinessProbe.InitialDelaySeconds)
	assert.Equal(t, int32(20), readinessProbe.TimeoutSeconds)
	assert.Equal(t, int32(25), readinessProbe.PeriodSeconds)
	assert.Equal(t, int32(22), readinessProbe.SuccessThreshold)
	assert.Equal(t, int32(25), readinessProbe.FailureThreshold)
}

func TestGetProbeForKogitoService_CustomConfigurationWithoutDefaultFields(t *testing.T) {
	serviceDefinition := ServiceDefinition{
		HealthCheckProbe: QuarkusHealthCheckProbe,
	}

	service := test.CreateFakeKogitoRuntime(t.Name())
	service.GetSpec().SetProbes(&v1beta1.KogitoProbe{
		LivenessProbe: corev1.Probe{
			Handler: corev1.Handler{
				HTTPGet: &corev1.HTTPGetAction{
					Port: intstr.IntOrString{IntVal: customProbePort},
				},
			},
		},
		ReadinessProbe: corev1.Probe{
			Handler: corev1.Handler{
				HTTPGet: &corev1.HTTPGetAction{
					Port: intstr.IntOrString{IntVal: customProbePort},
				},
			},
		},
	})
	healthCheckProbe := getProbeForKogitoService(serviceDefinition, service)
	livenessProbe := healthCheckProbe.liveness
	readinessProbe := healthCheckProbe.readiness

	assert.Equal(t, quarkusProbeLivenessPath, livenessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, livenessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, livenessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(0), livenessProbe.InitialDelaySeconds)
	assert.Equal(t, int32(1), livenessProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), livenessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), livenessProbe.SuccessThreshold)
	assert.Equal(t, int32(3), livenessProbe.FailureThreshold)

	assert.Equal(t, quarkusProbeReadinessPath, readinessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, readinessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, readinessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(0), readinessProbe.InitialDelaySeconds)
	assert.Equal(t, int32(1), readinessProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), readinessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), readinessProbe.SuccessThreshold)
	assert.Equal(t, int32(3), readinessProbe.FailureThreshold)
}
