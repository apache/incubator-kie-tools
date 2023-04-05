// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package platform

import (
	"context"
	"fmt"
	"github.com/pkg/errors"
	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/container-builder/client"
	"github.com/kiegroup/container-builder/util/defaults"

	v08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/builder"
)

func createKanikoCacheWarmerPod(ctx context.Context, client client.Client, platform *v08.KogitoServerlessPlatform) error {
	// The pod will be scheduled to nodes that are selected by the persistent volume
	// node affinity spec, if any, as provisioned by the persistent volume claim storage
	// class provisioner.
	// See:
	// - https://kubernetes.io/docs/concepts/storage/persistent-volumes/#node-affinity
	// - https://kubernetes.io/docs/concepts/storage/volumes/#local
	// nolint: staticcheck
	pvcName := defaultKanikoCachePVCName
	if persistentVolumeClaim, found := platform.Status.BuildPlatform.PublishStrategyOptions[builder.KanikoPVCName]; found {
		pvcName = persistentVolumeClaim
	}

	var warmerImage string
	if image, found := platform.Status.BuildPlatform.PublishStrategyOptions[builder.KanikoWarmerImage]; found {
		warmerImage = image
	} else {
		warmerImage = fmt.Sprintf("%s:v%s", builder.KanikoDefaultWarmerImageName, defaults.KanikoVersion)
	}

	pod := corev1.Pod{
		TypeMeta: metav1.TypeMeta{
			APIVersion: corev1.SchemeGroupVersion.String(),
			Kind:       "Pod",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: platform.Namespace,
			Name:      platform.Name + "-cache",
			Labels: map[string]string{
				"sw.kogito.kie.org/component": "kaniko-warmer",
			},
		},
		Spec: corev1.PodSpec{
			Containers: []corev1.Container{
				{
					Name:  "warm-kaniko-cache",
					Image: warmerImage,
					Args: []string{
						"--force",
						"--cache-dir=" + builder.KanikoCacheDir,
						"--image=" + platform.Status.BuildPlatform.BaseImage,
					},
					VolumeMounts: []corev1.VolumeMount{
						{
							Name:      "kaniko-cache",
							MountPath: builder.KanikoCacheDir,
						},
					},
					/* TODO: enable this test once we apply security enforcement: https://issues.redhat.com/browse/KOGITO-8799
					SecurityContext: kubeutil.SecurityDefaults(),*/
				},
			},
			// Create the cache directory otherwise Kaniko warmer skips caching silently
			InitContainers: []corev1.Container{
				{
					Name:            "create-kaniko-cache",
					Image:           "busybox",
					ImagePullPolicy: corev1.PullIfNotPresent,
					Command:         []string{"/bin/sh", "-c"},
					Args:            []string{"mkdir -p " + builder.KanikoCacheDir + "&& chmod -R a+rwx " + builder.KanikoCacheDir},
					VolumeMounts: []corev1.VolumeMount{
						{
							Name:      "kaniko-cache",
							MountPath: builder.KanikoCacheDir,
						},
					},
					/* TODO: enable this test once we apply security enforcement: https://issues.redhat.com/browse/KOGITO-8799
					SecurityContext: kubeutil.SecurityDefaults(),*/
				},
			},
			RestartPolicy: corev1.RestartPolicyOnFailure,
			Volumes: []corev1.Volume{
				{
					Name: "kaniko-cache",
					VolumeSource: corev1.VolumeSource{
						PersistentVolumeClaim: &corev1.PersistentVolumeClaimVolumeSource{
							ClaimName: pvcName,
						},
					},
				},
			},
		},
	}

	err := client.Delete(ctx, &pod)
	if err != nil && !k8serrors.IsNotFound(err) {
		return errors.Wrap(err, "cannot delete Kaniko warmer pod")
	}

	err = client.Create(ctx, &pod)
	if err != nil {
		return errors.Wrap(err, "cannot create Kaniko warmer pod")
	}

	return nil
}
