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
