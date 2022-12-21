/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform

import (
	"context"
	"github.com/kiegroup/container-builder/api"
	"github.com/kiegroup/container-builder/client"
	v08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/builder"
	"github.com/kiegroup/kogito-serverless-operator/constants"
	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// NewInitializeAction returns a action that initializes the platform configuration when not provided by the user.
func NewInitializeAction() Action {
	return &initializeAction{}
}

type initializeAction struct {
	baseAction
}

func (action *initializeAction) Name() string {
	return "initialize"
}

func (action *initializeAction) CanHandle(platform *v08.KogitoServerlessPlatform) bool {
	return platform.Status.Phase == "" || platform.Status.Phase == v08.PlatformPhaseDuplicate
}

func (action *initializeAction) Handle(ctx context.Context, platform *v08.KogitoServerlessPlatform) (*v08.KogitoServerlessPlatform, error) {
	duplicate, err := action.isPrimaryDuplicate(ctx, platform)
	if err != nil {
		return nil, err
	}
	if duplicate {
		// another platform already present in the namespace
		if platform.Status.Phase != v08.PlatformPhaseDuplicate {
			platform := platform.DeepCopy()
			platform.Status.Phase = v08.PlatformPhaseDuplicate

			return platform, nil
		}

		return nil, nil
	}

	if err = ConfigureDefaults(ctx, action.client, platform, true); err != nil {
		return nil, err
	}
	// nolint: staticcheck
	if platform.Status.KogitoServerlessPlatformSpec.BuildPlatform.PublishStrategy == api.PlatformBuildPublishStrategyKaniko {
		//If KanikoCache is enabled
		if platform.Status.KogitoServerlessPlatformSpec.BuildPlatform.IsOptionEnabled(builder.KanikoBuildCacheEnabled) {
			// Create the persistent volume claim used by the Kaniko cache
			action.Logger.Info("Create persistent volume claim")
			err := createPersistentVolumeClaim(ctx, action.client, platform)
			if err != nil {
				return nil, err
			}
			// Create the Kaniko warmer pod that caches the base image into the Kogito Serverless builder volume
			action.Logger.Info("Create Kaniko cache warmer pod")
			err = createKanikoCacheWarmerPod(ctx, action.client, platform)
			if err != nil {
				return nil, err
			}
			platform.Status.Phase = v08.PlatformPhaseWarming
		} else {
			// Skip the warmer pod creation
			platform.Status.Phase = v08.PlatformPhaseCreating
		}
	} else {
		platform.Status.Phase = v08.PlatformPhaseCreating
	}
	platform.Status.Version = constants.VERSION

	return platform, nil
}

func createPersistentVolumeClaim(ctx context.Context, client client.Client, platform *v08.KogitoServerlessPlatform) error {
	volumeSize, err := resource.ParseQuantity(constants.DEFAULT_KAKIKO_PVC_SIZE)
	if err != nil {
		return err
	}
	// nolint: staticcheck
	pvcName := constants.DEFAULT_KANIKOCACHE_PVC_NAME
	if persistentVolumeClaim, found := platform.Status.BuildPlatform.PublishStrategyOptions[builder.KanikoPVCName]; found {
		pvcName = persistentVolumeClaim
	}

	pvc := &corev1.PersistentVolumeClaim{
		TypeMeta: metav1.TypeMeta{
			APIVersion: corev1.SchemeGroupVersion.String(),
			Kind:       "PersistentVolumeClaim",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: platform.Namespace,
			Name:      pvcName,
			Labels: map[string]string{
				"app": "kogito-serverless-operator",
			},
		},
		Spec: corev1.PersistentVolumeClaimSpec{
			AccessModes: []corev1.PersistentVolumeAccessMode{
				corev1.ReadWriteOnce,
			},
			Resources: corev1.ResourceRequirements{
				Requests: corev1.ResourceList{
					corev1.ResourceStorage: volumeSize,
				},
			},
		},
	}

	err = client.Create(ctx, pvc)
	// Skip the error in case the PVC already exists
	if err != nil && !k8serrors.IsAlreadyExists(err) {
		return err
	}

	return nil
}

// Function to double-check if there is already an active platform on the current context (i.e. namespace)
func (action *initializeAction) isPrimaryDuplicate(ctx context.Context, thisPlatform *v08.KogitoServerlessPlatform) (bool, error) {
	if IsSecondary(thisPlatform) {
		// Always reconcile secondary platforms
		return false, nil
	}
	platforms, err := ListPrimaryPlatforms(ctx, action.client, thisPlatform.Namespace)
	if err != nil {
		return false, err
	}
	for _, p := range platforms.Items {
		p := p // pin
		if p.Name != thisPlatform.Name && IsActive(&p) {
			return true, nil
		}
	}

	return false, nil
}
