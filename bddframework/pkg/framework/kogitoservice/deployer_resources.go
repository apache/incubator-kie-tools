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
	"fmt"
	"reflect"

	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/framework/util"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/manager"
	imgv1 "github.com/openshift/api/image/v1"
	routev1 "github.com/openshift/api/route/v1"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
)

// createRequiredResources creates the required resources given the KogitoService instance
func (s *serviceDeployer) createRequiredResources(image string) (resources map[reflect.Type][]resource.KubernetesResource, err error) {
	resources = make(map[reflect.Type][]resource.KubernetesResource)

	appProps := map[string]string{}
	appPropsConfigMapHandler := NewAppPropsConfigMapHandler(s.Context)
	if len(s.instance.GetSpec().GetPropertiesConfigMap()) > 0 {
		s.Log.Debug("custom app properties are provided in custom properties ConfigMap", "PropertiesConfigMap", s.instance.GetSpec().GetPropertiesConfigMap())
		propertiesConfigMap := &corev1.ConfigMap{
			ObjectMeta: metav1.ObjectMeta{
				Namespace: s.instance.GetNamespace(),
				Name:      s.instance.GetSpec().GetPropertiesConfigMap(),
			},
		}
		if exists, err := kubernetes.ResourceC(s.Client).Fetch(propertiesConfigMap); err != nil {
			return resources, err
		} else if !exists {
			return resources, fmt.Errorf("propertiesConfigMap %s not found", s.instance.GetSpec().GetPropertiesConfigMap())
		} else {
			util.AppendToStringMap(getAppPropsFromConfigMap(propertiesConfigMap), appProps)
		}
	}

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
		var infraAppProps map[string]string
		var infraEnvProp []corev1.EnvVar
		infraAppProps, infraEnvProp, infraVolumes, err = s.fetchKogitoInfraProperties()
		if err != nil {
			return resources, err
		}
		util.AppendToStringMap(infraAppProps, appProps)
		deployment.Spec.Template.Spec.Containers[0].Env = append(deployment.Spec.Template.Spec.Containers[0].Env, infraEnvProp...)
	}

	deployment.Spec.Template.Spec.Containers[0].Env = append(deployment.Spec.Template.Spec.Containers[0].Env, framework.CreateEnvVar(infrastructure.RuntimeTypeKey, string(s.instance.GetSpec().GetRuntime())))

	if len(s.instance.GetSpec().GetConfig()) > 0 {
		s.Log.Debug("custom app properties are provided in custom Config")
		util.AppendToStringMap(s.instance.GetSpec().GetConfig(), appProps)
	}

	var contentHash string
	contentHash, configMap, err := appPropsConfigMapHandler.GetAppPropConfigMapContentHash(s.instance, appProps)
	if err != nil {
		return resources, err
	}

	s.applyApplicationPropertiesAnnotations(contentHash, deployment)

	s.mountKogitoInfraVolumes(infraVolumes, deployment)

	if err = NewTrustStoreHandler(s.Context).MountTrustStore(deployment, s.instance); err != nil {
		return resources, err
	}

	resources[reflect.TypeOf(appsv1.Deployment{})] = []resource.KubernetesResource{deployment}
	resources[reflect.TypeOf(corev1.Service{})] = []resource.KubernetesResource{service}
	if s.Client.IsOpenshift() {
		routeHandler := infrastructure.NewRouteHandler(s.Context)
		resources[reflect.TypeOf(routev1.Route{})] = []resource.KubernetesResource{routeHandler.CreateRoute(service)}
	}

	if configMap != nil {
		resources[reflect.TypeOf(corev1.ConfigMap{})] = []resource.KubernetesResource{configMap}
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

func (s *serviceDeployer) applyApplicationPropertiesAnnotations(contentHash string, deployment *appsv1.Deployment) {
	if deployment.Spec.Template.Annotations == nil {
		deployment.Spec.Template.Annotations = map[string]string{AppPropContentHashKey: contentHash}
	} else {
		deployment.Spec.Template.Annotations[AppPropContentHashKey] = contentHash
	}
}

// getDeployedResources gets the deployed resources in the cluster owned by the given instance
func (s *serviceDeployer) getDeployedResources() (resources map[reflect.Type][]resource.KubernetesResource, err error) {
	var objectTypes []runtime.Object
	if s.Client.IsOpenshift() {
		objectTypes = []runtime.Object{&appsv1.DeploymentList{}, &corev1.ServiceList{}, &corev1.ConfigMapList{}, &routev1.RouteList{}}
	} else {
		objectTypes = []runtime.Object{&appsv1.DeploymentList{}, &corev1.ServiceList{}, &corev1.ConfigMapList{}}
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

func (s *serviceDeployer) fetchKogitoInfraProperties() (map[string]string, []corev1.EnvVar, []api.KogitoInfraVolumeInterface, error) {
	kogitoInfraReferences := s.instance.GetSpec().GetInfra()
	s.Log.Debug("Going to fetch kogito infra properties", "infra", kogitoInfraReferences)
	consolidateAppProperties := map[string]string{}
	var consolidateEnvProperties []corev1.EnvVar
	var volumes []api.KogitoInfraVolumeInterface
	for _, kogitoInfraName := range kogitoInfraReferences {
		// load infra resource
		infraManager := manager.NewKogitoInfraManager(s.Context, s.infraHandler)
		kogitoInfraInstance, err := infraManager.MustFetchKogitoInfraInstance(types.NamespacedName{Name: kogitoInfraName, Namespace: s.instance.GetNamespace()})
		if err != nil {
			return nil, nil, nil, err
		}

		runtimeType := s.instance.GetSpec().GetRuntime()

		// fetch app properties from Kogito infra instance
		runtimeProperties := kogitoInfraInstance.GetStatus().GetRuntimeProperties()[runtimeType]
		if runtimeProperties != nil {
			appProp := runtimeProperties.GetAppProps()
			util.AppendToStringMap(appProp, consolidateAppProperties)

			// fetch env properties from Kogito infra instance
			envProp := runtimeProperties.GetEnv()
			consolidateEnvProperties = append(consolidateEnvProperties, envProp...)
		}
		// fetch volume from Kogito infra instance
		volumes = append(volumes, kogitoInfraInstance.GetStatus().GetVolumes()...)
	}
	return consolidateAppProperties, consolidateEnvProperties, volumes, nil
}

func (s *serviceDeployer) mountKogitoInfraVolumes(kogitoInfraVolumes []api.KogitoInfraVolumeInterface, deployment *appsv1.Deployment) {
	appPropsVolumeHandler := NewAppPropsVolumeHandler()
	framework.AddVolumeToDeployment(deployment, appPropsVolumeHandler.CreateAppPropVolumeMount(), appPropsVolumeHandler.CreateAppPropVolume(s.instance))
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
