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
	"github.com/kiegroup/kogito-operator/core/manager"
	"reflect"

	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	imgv1 "github.com/openshift/api/image/v1"
	routev1 "github.com/openshift/api/route/v1"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/runtime"
)

// createRequiredResources creates the required resources given the KogitoService instance
func (s *serviceDeployer) createRequiredResources(image string) (resources map[reflect.Type][]resource.KubernetesResource, err error) {
	resources = make(map[reflect.Type][]resource.KubernetesResource)

	// TODO: refactor this entire file

	// we only create the rest of the resources once we have a resolvable image
	// or if the deployment is already there, we don't want to delete it :)
	deploymentHandler := NewDeploymentHandler(s.Context)
	deployment := deploymentHandler.CreateRequiredDeployment(s.instance, image, s.definition)
	if err = s.onDeploymentCreate(deployment); err != nil {
		return resources, err
	}

	serviceHandler := infrastructure.NewServiceHandler(s.Context)
	service := serviceHandler.CreateService(s.instance, deployment)

	var infraVolumes []api.KogitoInfraVolumeInterface

	if len(s.instance.GetSpec().GetInfra()) > 0 {
		s.Log.Debug("Infra references are provided")
		var infraEnvProp []corev1.EnvVar
		infraManager := manager.NewKogitoInfraManager(s.Context, s.infraHandler)
		_, infraEnvProp, infraVolumes, err = infraManager.FetchKogitoInfraProperties(s.instance.GetSpec().GetRuntime(), s.instance.GetNamespace(), s.instance.GetSpec().GetInfra()...)
		if err != nil {
			return resources, err
		}
		deployment.Spec.Template.Spec.Containers[0].Env = append(deployment.Spec.Template.Spec.Containers[0].Env, infraEnvProp...)
	}

	deployment.Spec.Template.Spec.Containers[0].Env = append(deployment.Spec.Template.Spec.Containers[0].Env, framework.CreateEnvVar(infrastructure.RuntimeTypeKey, string(s.instance.GetSpec().GetRuntime())))

	s.mountKogitoInfraVolumes(infraVolumes, deployment)

	if err = NewTrustStoreHandler(s.Context).MountTrustStore(deployment, s.instance); err != nil {
		return resources, err
	}

	if err = s.mountConfigMapOnDeployment(deployment); err != nil {
		return resources, err
	}

	resources[reflect.TypeOf(appsv1.Deployment{})] = []resource.KubernetesResource{deployment}
	resources[reflect.TypeOf(corev1.Service{})] = []resource.KubernetesResource{service}
	if s.Client.IsOpenshift() {
		routeHandler := infrastructure.NewRouteHandler(s.Context)
		resources[reflect.TypeOf(routev1.Route{})] = []resource.KubernetesResource{routeHandler.CreateRoute(service)}
	}

	if err := s.onObjectsCreate(resources); err != nil {
		return resources, err
	}
	if err := s.setOwner(resources); err != nil {
		return resources, err
	}
	return
}

func (s *serviceDeployer) onDeploymentCreate(deployment *appsv1.Deployment) error {
	if s.Client.IsOpenshift() {
		imageHandler := s.newImageHandler()
		key, value := imageHandler.ResolveImageStreamTriggerAnnotation(s.instance.GetName())
		deployment.Annotations = map[string]string{key: value}
	}
	if s.definition.OnDeploymentCreate != nil {
		if err := s.definition.OnDeploymentCreate(deployment); err != nil {
			return err
		}
	}
	return nil
}

// onObjectsCreate calls the OnObjectsCreate hook for clients to add their custom objects/logic to the service
func (s *serviceDeployer) onObjectsCreate(resources map[reflect.Type][]resource.KubernetesResource) error {
	if s.definition.OnObjectsCreate != nil {
		var additionalRes map[reflect.Type][]resource.KubernetesResource
		var err error
		additionalRes, s.definition.extraManagedObjectLists, err = s.definition.OnObjectsCreate(s.instance)
		if err != nil {
			return err
		}
		for resType, res := range additionalRes {
			resources[resType] = append(resources[resType], res...)
		}
	}
	return nil
}

// setOwner sets this service instance as the owner of each resource.
func (s *serviceDeployer) setOwner(resources map[reflect.Type][]resource.KubernetesResource) error {
	for _, resourceArr := range resources {
		for _, res := range resourceArr {
			if err := framework.SetOwner(s.instance, s.Scheme, res); err != nil {
				return err
			}
		}
	}
	return nil
}

// getDeployedResources gets the deployed resources in the cluster owned by the given instance
func (s *serviceDeployer) getDeployedResources() (resources map[reflect.Type][]resource.KubernetesResource, err error) {
	var objectTypes []runtime.Object
	if s.Client.IsOpenshift() {
		objectTypes = []runtime.Object{&appsv1.DeploymentList{}, &corev1.ServiceList{}, &routev1.RouteList{}}
	} else {
		objectTypes = []runtime.Object{&appsv1.DeploymentList{}, &corev1.ServiceList{}}
	}

	if len(s.definition.extraManagedObjectLists) > 0 {
		objectTypes = append(objectTypes, s.definition.extraManagedObjectLists...)
	}

	resources, err = kubernetes.ResourceC(s.Client).ListAll(objectTypes, s.instance.GetNamespace(), s.instance)
	if err != nil {
		return
	}
	return
}

// getComparator gets the comparator for the owned resources
func (s *serviceDeployer) getComparator() compare.MapComparator {
	resourceComparator := compare.DefaultComparator()

	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(appsv1.Deployment{})).
			UseDefaultComparator().
			WithCustomComparator(framework.CreateDeploymentComparator()).
			Build())

	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(corev1.Service{})).
			UseDefaultComparator().
			WithCustomComparator(framework.CreateServiceComparator()).
			Build())

	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(routev1.Route{})).
			UseDefaultComparator().
			WithCustomComparator(framework.CreateRouteComparator()).
			Build())

	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(imgv1.ImageStream{})).
			UseDefaultComparator().
			WithCustomComparator(framework.CreateSharedImageStreamComparator()).
			Build())

	if s.definition.OnGetComparators != nil {
		s.definition.OnGetComparators(resourceComparator)
	}

	return compare.MapComparator{Comparator: resourceComparator}
}

func (s *serviceDeployer) mountKogitoInfraVolumes(kogitoInfraVolumes []api.KogitoInfraVolumeInterface, deployment *appsv1.Deployment) {
	for _, infraVolume := range kogitoInfraVolumes {
		framework.AddVolumeToDeployment(deployment, infraVolume.GetMount(), infraVolume.GetNamedVolume().ToKubeVolume())
	}
}

func (s *serviceDeployer) newImageHandler() infrastructure.ImageHandler {
	addDockerImageReference := len(s.instance.GetSpec().GetImage()) != 0 || !s.definition.CustomService
	image := s.resolveImage()
	return infrastructure.NewImageHandler(s.Context, image, s.definition.DefaultImageName, image.Name, s.instance.GetNamespace(), addDockerImageReference, s.instance.GetSpec().IsInsecureImageRegistry())
}

func (s *serviceDeployer) resolveImage() *api.Image {
	var image api.Image
	if len(s.instance.GetSpec().GetImage()) == 0 {
		image = api.Image{
			Name: s.definition.DefaultImageName,
			Tag:  s.definition.DefaultImageTag,
		}
	} else {
		image = framework.ConvertImageTagToImage(s.instance.GetSpec().GetImage())
	}
	return &image
}

func (s *serviceDeployer) mountConfigMapOnDeployment(deployment *appsv1.Deployment) error {
	configMapHandler := infrastructure.NewConfigMapHandler(s.Context)
	configMapList, err := configMapHandler.FetchConfigMapForOwner(s.instance)
	if err != nil {
		return err
	}
	for _, configMap := range configMapList {
		if err := configMapHandler.MountConfigMapOnDeployment(deployment, configMap); err != nil {
			return err
		}
	}
	return nil
}
