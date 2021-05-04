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
	"github.com/kiegroup/kogito-operator/api"
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

// test getDefaultHTTPGetAction (Quarkus), getDefaultHTTPPath (Quarkus) and setDefaultProbeValues
func TestGetProbeForKogitoService_EmptyHandler_Quarkus(t *testing.T) {
	service := test.CreateFakeKogitoRuntime(t.Name())
	service.Spec.Runtime = api.QuarkusRuntimeType
	healthCheckProbe := getProbeForKogitoService(service)
	livenessProbe := healthCheckProbe.liveness
	readinessProbe := healthCheckProbe.readiness
	startupProbe := healthCheckProbe.startup

	assert.Nil(t, readinessProbe.Handler.TCPSocket)
	assert.Equal(t, quarkusProbeReadinessPath, readinessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}, readinessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, readinessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(1), readinessProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), readinessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), readinessProbe.SuccessThreshold)
	assert.Equal(t, int32(3), readinessProbe.FailureThreshold)

	assert.Nil(t, livenessProbe.Handler.TCPSocket)
	assert.Equal(t, quarkusProbeLivenessPath, livenessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}, livenessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, livenessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(1), livenessProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), livenessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), livenessProbe.SuccessThreshold)
	assert.Equal(t, int32(3), livenessProbe.FailureThreshold)

	assert.Nil(t, startupProbe.Handler.TCPSocket)
	assert.Equal(t, quarkusProbeLivenessPath, startupProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}, startupProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, startupProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(1), startupProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), startupProbe.PeriodSeconds)
	assert.Equal(t, int32(1), startupProbe.SuccessThreshold)
	assert.Equal(t, int32(3), startupProbe.FailureThreshold)
}

// test setDefaultHTTPGetValues and getDefaultHTTPPath (Spring Boot)
func TestGetProbeForKogitoService_DefaultHTTP_SpringBoot(t *testing.T) {
	customHTTPPortProbe := corev1.Probe{
		Handler: corev1.Handler{
			HTTPGet: &corev1.HTTPGetAction{Port: intstr.IntOrString{IntVal: customProbePort}},
		},
	}
	service := test.CreateFakeKogitoRuntime(t.Name())
	service.Spec.Runtime = api.SpringBootRuntimeType
	service.GetSpec().SetProbes(&v1beta1.KogitoProbe{
		LivenessProbe:  *customHTTPPortProbe.DeepCopy(),
		ReadinessProbe: *customHTTPPortProbe.DeepCopy(),
		StartupProbe:   *customHTTPPortProbe.DeepCopy(),
	})
	healthCheckProbe := getProbeForKogitoService(service)
	livenessProbe := healthCheckProbe.liveness
	readinessProbe := healthCheckProbe.readiness
	startupProbe := healthCheckProbe.startup

	assert.Nil(t, livenessProbe.Handler.TCPSocket)
	assert.Equal(t, springBootProbeLivenessPath, livenessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, livenessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, livenessProbe.Handler.HTTPGet.Scheme)

	assert.Nil(t, readinessProbe.Handler.TCPSocket)
	assert.Equal(t, springBootProbeReadinessPath, readinessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, readinessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, readinessProbe.Handler.HTTPGet.Scheme)

	assert.Nil(t, startupProbe.Handler.TCPSocket)
	assert.Equal(t, springBootProbeLivenessPath, startupProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, startupProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, startupProbe.Handler.HTTPGet.Scheme)
}

func TestGetProbeForKogitoService_CustomHTTP_SpringBoot(t *testing.T) {
	customLivenessProbePath := "/live"
	customReadinessProbePath := "/ready"

	service := test.CreateFakeKogitoRuntime(t.Name())
	service.Spec.Runtime = api.SpringBootRuntimeType
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
			SuccessThreshold:    10,
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
		StartupProbe: corev1.Probe{
			Handler: corev1.Handler{
				HTTPGet: &corev1.HTTPGetAction{
					Path:   customLivenessProbePath,
					Port:   intstr.IntOrString{IntVal: customProbePort},
					Scheme: corev1.URISchemeHTTPS,
				},
			},
			InitialDelaySeconds: 0,
			TimeoutSeconds:      30,
			PeriodSeconds:       30,
			SuccessThreshold:    5,
			FailureThreshold:    10,
		},
	})
	healthCheckProbe := getProbeForKogitoService(service)
	livenessProbe := healthCheckProbe.liveness
	readinessProbe := healthCheckProbe.readiness
	startupProbe := healthCheckProbe.startup

	assert.Nil(t, livenessProbe.Handler.TCPSocket)
	assert.Equal(t, customLivenessProbePath, livenessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, livenessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTPS, livenessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(100), livenessProbe.InitialDelaySeconds)
	assert.Equal(t, int32(10), livenessProbe.TimeoutSeconds)
	assert.Equal(t, int32(5), livenessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), livenessProbe.SuccessThreshold)
	assert.Equal(t, int32(5), livenessProbe.FailureThreshold)

	assert.Nil(t, readinessProbe.Handler.TCPSocket)
	assert.Equal(t, customReadinessProbePath, readinessProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, readinessProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTPS, readinessProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(200), readinessProbe.InitialDelaySeconds)
	assert.Equal(t, int32(20), readinessProbe.TimeoutSeconds)
	assert.Equal(t, int32(25), readinessProbe.PeriodSeconds)
	assert.Equal(t, int32(22), readinessProbe.SuccessThreshold)
	assert.Equal(t, int32(25), readinessProbe.FailureThreshold)

	assert.Nil(t, startupProbe.Handler.TCPSocket)
	assert.Equal(t, customLivenessProbePath, startupProbe.Handler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, startupProbe.Handler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTPS, startupProbe.Handler.HTTPGet.Scheme)
	assert.Equal(t, int32(0), startupProbe.InitialDelaySeconds)
	assert.Equal(t, int32(30), startupProbe.TimeoutSeconds)
	assert.Equal(t, int32(30), startupProbe.PeriodSeconds)
	assert.Equal(t, int32(1), startupProbe.SuccessThreshold)
	assert.Equal(t, int32(10), startupProbe.FailureThreshold)
}

func TestGetProbeForKogitoService_CustomTCP_Quarkus(t *testing.T) {
	service := test.CreateFakeKogitoRuntime(t.Name())
	service.Spec.Runtime = api.QuarkusRuntimeType
	customTCPPortProbe := corev1.Probe{
		Handler: corev1.Handler{
			TCPSocket: &corev1.TCPSocketAction{Port: intstr.IntOrString{IntVal: customProbePort}},
		},
	}
	service.GetSpec().SetProbes(&v1beta1.KogitoProbe{
		LivenessProbe:  *customTCPPortProbe.DeepCopy(),
		ReadinessProbe: *customTCPPortProbe.DeepCopy(),
		StartupProbe:   *customTCPPortProbe.DeepCopy(),
	})
	healthCheckProbe := getProbeForKogitoService(service)

	assert.Nil(t, healthCheckProbe.readiness.Handler.HTTPGet)
	assert.Nil(t, healthCheckProbe.liveness.Handler.HTTPGet)
	assert.Nil(t, healthCheckProbe.startup.Handler.HTTPGet)

	assert.Equal(t, intstr.IntOrString{IntVal: int32(customProbePort)}, healthCheckProbe.readiness.Handler.TCPSocket.Port)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(customProbePort)}, healthCheckProbe.liveness.Handler.TCPSocket.Port)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(customProbePort)}, healthCheckProbe.startup.Handler.TCPSocket.Port)
}
