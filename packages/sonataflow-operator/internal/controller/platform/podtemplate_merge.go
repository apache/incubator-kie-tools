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

package platform      // ← same package as k8s.go

import corev1 "k8s.io/api/core/v1"

// mergePodTemplate copies envFrom, volumes and mounts from def into dst.
func mergePodTemplate(dst *corev1.PodTemplateSpec, def *corev1.PodTemplateSpec) {
	if def == nil ||
		len(dst.Spec.Containers) == 0 ||
		len(def.Spec.Containers) == 0 {
		return
	}
	dstC := &dst.Spec.Containers[0]
	defC := def.Spec.Containers[0]

	dst.Spec.Volumes = append(dst.Spec.Volumes, def.Spec.Volumes...)
	dstC.EnvFrom     = append(dstC.EnvFrom,     defC.EnvFrom...)
	dstC.VolumeMounts = append(dstC.VolumeMounts, defC.VolumeMounts...)
}
