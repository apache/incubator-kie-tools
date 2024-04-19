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

package kubernetes

import (
	"context"
	"fmt"
	"path"

	"github.com/google/uuid"
	"github.com/pkg/errors"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"

	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
)

type configMapVolumeBuildContext struct {
	VolumeMount []corev1.VolumeMount
	Volume      corev1.Volume
}

// addResourcesToBuilderContextVolume add the build resources to volumes. Usually these volumes are added to a build pod. The resources reference must be previously created.
func addResourcesToBuilderContextVolume(ctx context.Context, client client.Client, task api.PublishTask, build *api.ContainerBuild, volumes *[]corev1.Volume, volumeMounts *[]corev1.VolumeMount) error {
	// TODO: do it via specialized handlers, since we might have multiple volumeMounts types (configMap, Secrets, AWS, GCP, etc).
	// TODO: for now, what we have is a context based on CMs so one projected volume for everything is enough and easier to setup.
	// See https://kubernetes.io/docs/concepts/storage/projected-volumes/
	mounts := make(map[string]configMapVolumeBuildContext, 0)

	for _, resVol := range build.Status.ResourceVolumes {
		switch resVol.ReferenceType {
		case api.ResourceReferenceTypeConfigMap:
			configMap := &corev1.ConfigMap{}
			err := client.Get(ctx, types.NamespacedName{Name: resVol.ReferenceName, Namespace: build.Namespace}, configMap)
			if err != nil {
				klog.ErrorS(err, "Failed to fetch configMap to add to build context", "configMap", resVol.ReferenceName, "Namespace", build.Namespace)
				return err
			}
			entry, ok := mounts[resVol.DestinationDir]
			var volName string
			if ok {
				volName = entry.Volume.Name
			} else {
				volName = uuid.NewString()
				mounts[resVol.DestinationDir] = configMapVolumeBuildContext{
					Volume: corev1.Volume{
						Name: volName,
						VolumeSource: corev1.VolumeSource{
							Projected: &corev1.ProjectedVolumeSource{},
						},
					},
				}
				entry = mounts[resVol.DestinationDir]
			}

			cmMounts := make([]corev1.VolumeMount, len(configMap.Data))
			i := 0
			for fileName := range configMap.Data {
				cmMounts[i] = corev1.VolumeMount{
					Name:      volName,
					MountPath: path.Join(task.ContextDir, resVol.DestinationDir, fileName),
					SubPath:   fileName,
					ReadOnly:  true,
				}
				i++
			}
			entry.VolumeMount = append(entry.VolumeMount, cmMounts...)
			entry.Volume.Projected.Sources = append(entry.Volume.Projected.Sources, corev1.VolumeProjection{
				ConfigMap: &corev1.ConfigMapProjection{
					LocalObjectReference: corev1.LocalObjectReference{Name: configMap.Name},
				},
			})
			mounts[resVol.DestinationDir] = entry
		default:
			return errors.Errorf("unsupported resource mount type for build %s on ns %s", build.Name, build.Namespace)
		}
	}

	for _, cmMount := range mounts {
		*volumeMounts = append(*volumeMounts, cmMount.VolumeMount...)
		*volumes = append(*volumes, cmMount.Volume)
	}

	return nil
}

// Mount the given ConfigMaps to the ContainerBuild that later will be mounted in the build context.
func mountResourcesConfigMapToBuild(buildContext *containerBuildContext, cms *[]resourceConfigMap) {
	if cms == nil || len(*cms) == 0 {
		return
	}
	for _, cm := range *cms {
		buildContext.containerBuild.Status.ResourceVolumes = append(buildContext.containerBuild.Status.ResourceVolumes, api.ContainerBuildResourceVolume{
			ReferenceName:  cm.Ref.Name,
			ReferenceType:  api.ResourceReferenceTypeConfigMap,
			DestinationDir: cm.Path,
		})
	}
}

// Mount the given resource(s) files in a ConfigMap and then add it to the ContainerBuild that later will be mounted in the build context
func mountResourcesBinaryWithConfigMapToBuild(buildContext *containerBuildContext, resources *[]resource) error {
	if resources == nil || len(*resources) == 0 {
		return nil
	}
	configMap, err := getOrCreateResourcesBinaryConfigMap(buildContext, resources)
	if err != nil {
		return err
	}

	buildContext.containerBuild.Status.ResourceVolumes = append(buildContext.containerBuild.Status.ResourceVolumes, api.ContainerBuildResourceVolume{
		ReferenceName:  configMap.Name,
		ReferenceType:  api.ResourceReferenceTypeConfigMap,
		DestinationDir: "",
	})

	return nil
}

func getResourcesBinaryConfigMap(c context.Context, client client.Client, build *api.ContainerBuild) (*corev1.ConfigMap, error) {
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

func getOrCreateResourcesBinaryConfigMap(buildContext *containerBuildContext, resources *[]resource) (*corev1.ConfigMap, error) {
	// TODO: build an actual configMap builder context handler
	resourcesConfigMap, err := getResourcesBinaryConfigMap(buildContext.ctx, buildContext.c, buildContext.containerBuild)
	if err != nil {
		return nil, err
	}

	if resourcesConfigMap == nil {
		resourcesConfigMap = &corev1.ConfigMap{}
		configMapId := types.NamespacedName{Name: buildPodName(buildContext.containerBuild), Namespace: buildContext.containerBuild.Namespace}
		resourcesConfigMap.Namespace = configMapId.Namespace
		resourcesConfigMap.Name = configMapId.Name
		addBinaryContentToConfigMap(resourcesConfigMap, resources)
		// TODO: every object we create, must pass to a listener for our client code. For example, an operator would like to add their labels/owner refs
		if err := buildContext.c.Create(buildContext.ctx, resourcesConfigMap); err != nil {
			return nil, err
		}
	} else {
		addBinaryContentToConfigMap(resourcesConfigMap, resources)
		if err := buildContext.c.Update(buildContext.ctx, resourcesConfigMap); err != nil {
			return nil, err
		}
	}

	return resourcesConfigMap, nil
}

func addBinaryContentToConfigMap(configMap *corev1.ConfigMap, resources *[]resource) {
	configMap.BinaryData = make(map[string][]byte)
	configMap.Data = make(map[string]string)
	for _, resource := range *resources {
		configMap.Data[resource.Target] = fmt.Sprintf("%s", resource.Content)
	}
}
