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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
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

	assert.Nil(t, readinessProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, quarkusProbeReadinessPath, readinessProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}, readinessProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, readinessProbe.ProbeHandler.HTTPGet.Scheme)
	assert.Equal(t, int32(1), readinessProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), readinessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), readinessProbe.SuccessThreshold)
	assert.Equal(t, int32(3), readinessProbe.FailureThreshold)

	assert.Nil(t, livenessProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, quarkusProbeLivenessPath, livenessProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}, livenessProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, livenessProbe.ProbeHandler.HTTPGet.Scheme)
	assert.Equal(t, int32(1), livenessProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), livenessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), livenessProbe.SuccessThreshold)
	assert.Equal(t, int32(3), livenessProbe.FailureThreshold)

	assert.Nil(t, startupProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, quarkusProbeLivenessPath, startupProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}, startupProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, startupProbe.ProbeHandler.HTTPGet.Scheme)
	assert.Equal(t, int32(1), startupProbe.TimeoutSeconds)
	assert.Equal(t, int32(10), startupProbe.PeriodSeconds)
	assert.Equal(t, int32(1), startupProbe.SuccessThreshold)
	assert.Equal(t, int32(3), startupProbe.FailureThreshold)
}

// test setDefaultHTTPGetValues and getDefaultHTTPPath (Spring Boot)
func TestGetProbeForKogitoService_DefaultHTTP_SpringBoot(t *testing.T) {
	customHTTPPortProbe := corev1.Probe{
		ProbeHandler: corev1.ProbeHandler{
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

	assert.Nil(t, livenessProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, springBootProbeLivenessPath, livenessProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, livenessProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, livenessProbe.ProbeHandler.HTTPGet.Scheme)

	assert.Nil(t, readinessProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, springBootProbeReadinessPath, readinessProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, readinessProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, readinessProbe.ProbeHandler.HTTPGet.Scheme)

	assert.Nil(t, startupProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, springBootProbeLivenessPath, startupProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, startupProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTP, startupProbe.ProbeHandler.HTTPGet.Scheme)
}

func TestGetProbeForKogitoService_CustomHTTP_SpringBoot(t *testing.T) {
	customLivenessProbePath := "/live"
	customReadinessProbePath := "/ready"

	service := test.CreateFakeKogitoRuntime(t.Name())
	service.Spec.Runtime = api.SpringBootRuntimeType
	service.GetSpec().SetProbes(&v1beta1.KogitoProbe{
		LivenessProbe: corev1.Probe{
			ProbeHandler: corev1.ProbeHandler{
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
			ProbeHandler: corev1.ProbeHandler{
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
			ProbeHandler: corev1.ProbeHandler{
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

	assert.Nil(t, livenessProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, customLivenessProbePath, livenessProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, livenessProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTPS, livenessProbe.ProbeHandler.HTTPGet.Scheme)
	assert.Equal(t, int32(100), livenessProbe.InitialDelaySeconds)
	assert.Equal(t, int32(10), livenessProbe.TimeoutSeconds)
	assert.Equal(t, int32(5), livenessProbe.PeriodSeconds)
	assert.Equal(t, int32(1), livenessProbe.SuccessThreshold)
	assert.Equal(t, int32(5), livenessProbe.FailureThreshold)

	assert.Nil(t, readinessProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, customReadinessProbePath, readinessProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, readinessProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTPS, readinessProbe.ProbeHandler.HTTPGet.Scheme)
	assert.Equal(t, int32(200), readinessProbe.InitialDelaySeconds)
	assert.Equal(t, int32(20), readinessProbe.TimeoutSeconds)
	assert.Equal(t, int32(25), readinessProbe.PeriodSeconds)
	assert.Equal(t, int32(22), readinessProbe.SuccessThreshold)
	assert.Equal(t, int32(25), readinessProbe.FailureThreshold)

	assert.Nil(t, startupProbe.ProbeHandler.TCPSocket)
	assert.Equal(t, customLivenessProbePath, startupProbe.ProbeHandler.HTTPGet.Path)
	assert.Equal(t, intstr.IntOrString{IntVal: customProbePort}, startupProbe.ProbeHandler.HTTPGet.Port)
	assert.Equal(t, corev1.URISchemeHTTPS, startupProbe.ProbeHandler.HTTPGet.Scheme)
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
		ProbeHandler: corev1.ProbeHandler{
			TCPSocket: &corev1.TCPSocketAction{Port: intstr.IntOrString{IntVal: customProbePort}},
		},
	}
	service.GetSpec().SetProbes(&v1beta1.KogitoProbe{
		LivenessProbe:  *customTCPPortProbe.DeepCopy(),
		ReadinessProbe: *customTCPPortProbe.DeepCopy(),
		StartupProbe:   *customTCPPortProbe.DeepCopy(),
	})
	healthCheckProbe := getProbeForKogitoService(service)

	assert.Nil(t, healthCheckProbe.readiness.ProbeHandler.HTTPGet)
	assert.Nil(t, healthCheckProbe.liveness.ProbeHandler.HTTPGet)
	assert.Nil(t, healthCheckProbe.startup.ProbeHandler.HTTPGet)

	assert.Equal(t, intstr.IntOrString{IntVal: int32(customProbePort)}, healthCheckProbe.readiness.ProbeHandler.TCPSocket.Port)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(customProbePort)}, healthCheckProbe.liveness.ProbeHandler.TCPSocket.Port)
	assert.Equal(t, intstr.IntOrString{IntVal: int32(customProbePort)}, healthCheckProbe.startup.ProbeHandler.TCPSocket.Port)
}
