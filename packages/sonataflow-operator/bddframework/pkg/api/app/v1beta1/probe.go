/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package v1beta1

import corev1 "k8s.io/api/core/v1"

// KogitoProbe configure liveness, readiness and startup probes for containers
type KogitoProbe struct {
	// LivenessProbe describes how the Kogito container liveness probe should work
	// +
	// +optional
	LivenessProbe corev1.Probe `json:"livenessProbe,omitempty"`

	// ReadinessProbe describes how the Kogito container readiness probe should work
	// +
	// +optional
	ReadinessProbe corev1.Probe `json:"readinessProbe,omitempty"`

	// StartupProbe describes how the Kogito container startup probe should work
	// +
	// +optional
	StartupProbe corev1.Probe `json:"startupProbe,omitempty"`
}

// GetLivenessProbe ...
func (p *KogitoProbe) GetLivenessProbe() corev1.Probe {
	return p.LivenessProbe
}

// SetLivenessProbe ...
func (p *KogitoProbe) SetLivenessProbe(livenessProbe corev1.Probe) {
	p.LivenessProbe = livenessProbe
}

// GetReadinessProbe ...
func (p *KogitoProbe) GetReadinessProbe() corev1.Probe {
	return p.ReadinessProbe
}

// SetReadinessProbe ...
func (p *KogitoProbe) SetReadinessProbe(readinessProbe corev1.Probe) {
	p.ReadinessProbe = readinessProbe
}

// GetStartupProbe ...
func (p *KogitoProbe) GetStartupProbe() corev1.Probe {
	return p.StartupProbe
}

// SetStartupProbe ...
func (p *KogitoProbe) SetStartupProbe(startupProbe corev1.Probe) {
	p.StartupProbe = startupProbe
}
