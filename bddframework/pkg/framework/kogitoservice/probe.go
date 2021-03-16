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
	}
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
	}
	return &probe
}

// isProbeHandlerEmpty ...
func isProbeHandlerEmpty(handler corev1.Handler) bool {
	return handler.HTTPGet == nil && handler.Exec == nil && handler.TCPSocket == nil
}
