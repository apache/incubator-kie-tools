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
	"github.com/kiegroup/kogito-operator/core/framework"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
)

// HealthCheckProbeType defines the supported probes for the ServiceDefinition
type HealthCheckProbeType string

const (
	// QuarkusHealthCheckProbe probe implemented with Quarkus Microprofile Health. See: https://quarkus.io/guides/microprofile-health.
	// the operator will set the probe to the default path /health/live and /health/ready for liveness and readiness probes, respectively.
	QuarkusHealthCheckProbe HealthCheckProbeType = "quarkus"
	// TCPHealthCheckProbe default health check probe that binds to port 8080
	TCPHealthCheckProbe HealthCheckProbeType = "TCP"

	quarkusProbeLivenessPath  = "/q/health/live"
	quarkusProbeReadinessPath = "/q/health/ready"
)

type healthCheckProbe struct {
	readiness *corev1.Probe
	liveness  *corev1.Probe
}

var defaultProbeValues = corev1.Probe{
	TimeoutSeconds:   int32(1),
	PeriodSeconds:    int32(10),
	SuccessThreshold: int32(1),
	FailureThreshold: int32(3),
}

// getProbeForKogitoService gets the appropriate liveness (index 0) and readiness (index 1) probes based on the given service definition
func getProbeForKogitoService(serviceDefinition ServiceDefinition, service api.KogitoService) healthCheckProbe {
	switch serviceDefinition.HealthCheckProbe {
	case QuarkusHealthCheckProbe:
		return healthCheckProbe{
			readiness: getQuarkusHealthCheckReadiness(service.GetSpec().GetProbes().GetReadinessProbe()),
			liveness:  getQuarkusHealthCheckLiveness(service.GetSpec().GetProbes().GetLivenessProbe()),
		}
	case TCPHealthCheckProbe:
		return healthCheckProbe{
			readiness: getTCPHealthCheckProbe(service.GetSpec().GetProbes().GetReadinessProbe()),
			liveness:  getTCPHealthCheckProbe(service.GetSpec().GetProbes().GetLivenessProbe()),
		}
	default:
		return healthCheckProbe{
			readiness: getTCPHealthCheckProbe(service.GetSpec().GetProbes().GetReadinessProbe()),
			liveness:  getTCPHealthCheckProbe(service.GetSpec().GetProbes().GetLivenessProbe()),
		}
	}
}

func getTCPHealthCheckProbe(probe corev1.Probe) *corev1.Probe {
	if isProbeHandlerEmpty(probe.Handler) {
		probe.Handler = corev1.Handler{
			TCPSocket: &corev1.TCPSocketAction{Port: intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)}},
		}
	}
	setDefaultProbeValues(&probe)
	return &probe
}

func getQuarkusHealthCheckLiveness(probe corev1.Probe) *corev1.Probe {
	if isProbeHandlerEmpty(probe.Handler) {
		probe.Handler = corev1.Handler{
			HTTPGet: &corev1.HTTPGetAction{
				Path:   quarkusProbeLivenessPath,
				Port:   intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)},
				Scheme: corev1.URISchemeHTTP,
			},
		}
	} else {
		setDefaultHTTPGetValues(&probe, quarkusProbeLivenessPath)
	}
	setDefaultProbeValues(&probe)
	// Must be 1 for liveness and startup.
	probe.SuccessThreshold = 1
	return &probe
}

func getQuarkusHealthCheckReadiness(probe corev1.Probe) *corev1.Probe {
	if isProbeHandlerEmpty(probe.Handler) {
		probe.Handler = corev1.Handler{
			HTTPGet: &corev1.HTTPGetAction{
				Path:   quarkusProbeReadinessPath,
				Port:   intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)},
				Scheme: corev1.URISchemeHTTP,
			},
		}
	} else {
		setDefaultHTTPGetValues(&probe, quarkusProbeReadinessPath)
	}
	setDefaultProbeValues(&probe)
	return &probe
}

// setDefaultHTTPGetValues sets default HTTPGetAction values for the handler if not set already. This prevents reconciliation loops.
func setDefaultHTTPGetValues(probe *corev1.Probe, defaultPath string) {
	if probe.Handler.HTTPGet.Path == "" {
		probe.Handler.HTTPGet.Path = defaultPath
	}
	// port not needed to be set since it is a mandatory field for HTTPGetAction enforced at YAML level
	if probe.Handler.HTTPGet.Scheme == "" {
		probe.Handler.HTTPGet.Scheme = corev1.URISchemeHTTP
	}
}

// setDefaultProbeValues sets default probe values if not set already. This prevents reconciliation loops.
func setDefaultProbeValues(probe *corev1.Probe) {
	if probe.TimeoutSeconds == 0 {
		probe.TimeoutSeconds = defaultProbeValues.TimeoutSeconds
	}
	if probe.PeriodSeconds == 0 {
		probe.PeriodSeconds = defaultProbeValues.PeriodSeconds
	}
	if probe.SuccessThreshold == 0 {
		probe.SuccessThreshold = defaultProbeValues.SuccessThreshold
	}
	if probe.FailureThreshold == 0 {
		probe.FailureThreshold = defaultProbeValues.FailureThreshold
	}
}

// isProbeHandlerEmpty ...
func isProbeHandlerEmpty(handler corev1.Handler) bool {
	return handler.HTTPGet == nil && handler.Exec == nil && handler.TCPSocket == nil
}
