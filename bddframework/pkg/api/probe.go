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

package api

import corev1 "k8s.io/api/core/v1"

// KogitoProbeInterface ...
type KogitoProbeInterface interface {
	GetLivenessProbe() corev1.Probe
	SetLivenessProbe(livenessProbe corev1.Probe)
	GetReadinessProbe() corev1.Probe
	SetReadinessProbe(readinessProbe corev1.Probe)
	GetStartupProbe() corev1.Probe
	SetStartupProbe(startupProbe corev1.Probe)
}
