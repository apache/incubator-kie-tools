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

package kubernetes

import (
	"context"
	"fmt"

	"github.com/pkg/errors"
	"sigs.k8s.io/controller-runtime/pkg/client"

	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
)

// addResourcesToVolume add to the given volumes the build resources context. The resources reference must be previously created.
func addResourcesToVolume(ctx context.Context, client client.Client, task api.PublishTask, build *api.ContainerBuild, volumes *[]corev1.Volume, volumeMounts *[]corev1.VolumeMount) error {
	// TODO: do it via specialized handlers
	switch build.Status.ResourceVolume.ReferenceType {
	case api.ResourceReferenceTypeConfigMap:
		configMap, err := getResourcesConfigMap(ctx, client, build)
		if err != nil {
			return err
		}
		if configMap == nil {
			return errors.Errorf("can't find configMap for resources context for build %s in ns %s", build.Name, build.Namespace)
		}
		keys := make([]corev1.KeyToPath, len(configMap.Data))
		i := 0
		for fileName := range configMap.Data {
			keys[i].Key = fileName
			keys[i].Path = fileName

			*volumeMounts = append(*volumeMounts, corev1.VolumeMount{
				Name:      "builder-context",
				MountPath: task.ContextDir + "/" + fileName,
				SubPath:   fileName,
				ReadOnly:  true,
			})

			i++
		}
		// mount volumes
		*volumes = append(*volumes, corev1.Volume{
			Name: "builder-context",
			VolumeSource: corev1.VolumeSource{
				ConfigMap: &corev1.ConfigMapVolumeSource{
					LocalObjectReference: corev1.LocalObjectReference{Name: build.Status.ResourceVolume.ReferenceName},
					Items:                keys,
				},
			},
		})

	default:
		return errors.Errorf("unsupported resource mount type for build %s on ns %s", build.Name, build.Namespace)
	}

	return nil
}

// TODO: create an actual handler for resources build context. For PoC level, CM will do
func mountResourcesWithConfigMap(buildContext *containerBuildContext, resources *[]resource) error {
	configMap, err := getOrCreateResourcesConfigMap(buildContext, resources)
	if err != nil {
		return err
	}

	buildContext.ContainerBuild.Status.ResourceVolume = &api.ContainerBuildResourceVolume{
		ReferenceName: configMap.Name,
		ReferenceType: api.ResourceReferenceTypeConfigMap,
	}

	return nil
}

func getResourcesConfigMap(c context.Context, client client.Client, build *api.ContainerBuild) (*corev1.ConfigMap, error) {
	resourcesConfigMap := corev1.ConfigMap{}
	configMapId := types.NamespacedName{Name: buildPodName(build), Namespace: build.Namespace}

	if err := client.Get(c, configMapId, &resourcesConfigMap); err != nil {
		if k8serrors.IsNotFound(err) {
			return nil, nil
		}
		return nil, err
	}

	return &resourcesConfigMap, nil
}

func getOrCreateResourcesConfigMap(buildContext *containerBuildContext, resources *[]resource) (*corev1.ConfigMap, error) {
	// TODO: build an actual configMap builder context handler
	resourcesConfigMap, err := getResourcesConfigMap(buildContext.C, buildContext.Client, buildContext.ContainerBuild)
	if err != nil {
		return nil, err
	}

	if resourcesConfigMap == nil {
		resourcesConfigMap = &corev1.ConfigMap{}
		configMapId := types.NamespacedName{Name: buildPodName(buildContext.ContainerBuild), Namespace: buildContext.ContainerBuild.Namespace}
		resourcesConfigMap.Namespace = configMapId.Namespace
		resourcesConfigMap.Name = configMapId.Name
		addContentToConfigMap(resourcesConfigMap, resources)
		// TODO: every object we create, must pass to a listener for our client code. For example, an operator would like to add their labels/owner refs
		if err := buildContext.Client.Create(buildContext.C, resourcesConfigMap); err != nil {
			return nil, err
		}
	} else {
		addContentToConfigMap(resourcesConfigMap, resources)
		if err := buildContext.Client.Update(buildContext.C, resourcesConfigMap); err != nil {
			return nil, err
		}
	}

	return resourcesConfigMap, nil
}

func addContentToConfigMap(configMap *corev1.ConfigMap, resources *[]resource) {
	configMap.BinaryData = make(map[string][]byte)
	configMap.Data = make(map[string]string)
	for _, resource := range *resources {
		configMap.Data[resource.Target] = fmt.Sprintf("%s", resource.Content)
	}
}
