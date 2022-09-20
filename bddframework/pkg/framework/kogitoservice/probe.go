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
	"github.com/kiegroup/kogito-operator/core/framework"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
)

// ProbeType defines the types of probes supported by K8s
type ProbeType string

const (
	livenessProbeType  ProbeType = "liveness"
	readinessProbeType ProbeType = "readiness"
	startupProbeType   ProbeType = "startup"

	quarkusProbeLivenessPath  = "/q/health/live"
	quarkusProbeReadinessPath = "/q/health/ready"

	springBootProbeLivenessPath  = "/actuator/health/liveness"
	springBootProbeReadinessPath = "/actuator/health/readiness"
)

type healthCheckProbe struct {
	readiness *corev1.Probe
	liveness  *corev1.Probe
	startup   *corev1.Probe
}

var defaultHTTPGetAction = corev1.HTTPGetAction{
	Port:   intstr.IntOrString{IntVal: int32(framework.DefaultExposedPort)},
	Scheme: corev1.URISchemeHTTP,
}

var defaultProbeValues = corev1.Probe{
	TimeoutSeconds:   int32(1),
	PeriodSeconds:    int32(10),
	SuccessThreshold: int32(1),
	FailureThreshold: int32(3),
}

func getProbeForKogitoService(service api.KogitoService) healthCheckProbe {
	runtimeType := service.GetSpec().GetRuntime()
	return healthCheckProbe{
		readiness: getProbe(service.GetSpec().GetProbes().GetReadinessProbe(), runtimeType, readinessProbeType),
		liveness:  getProbe(service.GetSpec().GetProbes().GetLivenessProbe(), runtimeType, livenessProbeType),
		startup:   getProbe(service.GetSpec().GetProbes().GetStartupProbe(), runtimeType, startupProbeType),
	}
}

// getProbe is a catch-all function that sets default values for all missing values
// that have not been set by the user for all the various probe types.
func getProbe(probe corev1.Probe, runtimeType api.RuntimeType, probeType ProbeType) *corev1.Probe {
	if isProbeHandlerEmpty(probe.ProbeHandler) {
		probe.ProbeHandler = corev1.ProbeHandler{HTTPGet: getDefaultHTTPGetAction(runtimeType, probeType)}
	} else if probe.ProbeHandler.HTTPGet != nil {
		setDefaultHTTPGetValues(&probe, runtimeType, probeType)
	}
	// Remaining case is where probe handler is set to TCP by user.
	// Port is required in YAML so need further values need to be set.
	setDefaultProbeValues(&probe)
	if probeType == livenessProbeType || probeType == startupProbeType {
		// Must be 1 for liveness and startup.
		probe.SuccessThreshold = 1
	}
	return &probe
}

// setDefaultHTTPGetValues sets default HTTPGetAction values for the handler if not set already. This prevents reconciliation loops.
func setDefaultHTTPGetValues(probe *corev1.Probe, runtimeType api.RuntimeType, probeType ProbeType) {
	if probe.ProbeHandler.HTTPGet.Path == "" {
		probe.ProbeHandler.HTTPGet.Path = getDefaultHTTPPath(runtimeType, probeType)
	}
	// port not needed to be set since it is a mandatory field for HTTPGetAction enforced at YAML level
	if probe.ProbeHandler.HTTPGet.Scheme == "" {
		probe.ProbeHandler.HTTPGet.Scheme = corev1.URISchemeHTTP
	}
}

func getDefaultHTTPPath(runtimeType api.RuntimeType, probeType ProbeType) string {
	if runtimeType == api.SpringBootRuntimeType {
		if probeType == livenessProbeType || probeType == startupProbeType {
			return springBootProbeLivenessPath
		}
		// must be readiness probe based on available probe types
		return springBootProbeReadinessPath
	}
	// default to Quarkus runtime. also must be Quarkus based on available runtimes
	if probeType == livenessProbeType || probeType == startupProbeType {
		return quarkusProbeLivenessPath
	}
	return quarkusProbeReadinessPath
}

func getDefaultHTTPGetAction(runtimeType api.RuntimeType, probeType ProbeType) *corev1.HTTPGetAction {
	httpGetAction := defaultHTTPGetAction.DeepCopy()
	httpGetAction.Path = getDefaultHTTPPath(runtimeType, probeType)
	return httpGetAction
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
func isProbeHandlerEmpty(handler corev1.ProbeHandler) bool {
	return handler.HTTPGet == nil && handler.Exec == nil && handler.TCPSocket == nil
}
