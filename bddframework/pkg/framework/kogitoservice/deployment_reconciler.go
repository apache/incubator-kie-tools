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
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	appsv1 "k8s.io/api/apps/v1"
)

// DeploymentReconciler ...
type DeploymentReconciler interface {
	Reconcile() error
}

type deploymentReconciler struct {
	operator.Context
	instance                api.KogitoService
	definition              ServiceDefinition
	imageHandler            infrastructure.ImageHandler
	kogitoDeploymentHandler KogitoDeploymentHandler
	deploymentHandler       infrastructure.DeploymentHandler
	deltaProcessor          infrastructure.DeltaProcessor
}

func newDeploymentReconciler(context operator.Context, instance api.KogitoService, definition ServiceDefinition, imageHandler infrastructure.ImageHandler) DeploymentReconciler {
	return &deploymentReconciler{
		Context:                 context,
		instance:                instance,
		imageHandler:            imageHandler,
		definition:              definition,
		kogitoDeploymentHandler: NewKogitoDeploymentHandler(context),
		deploymentHandler:       infrastructure.NewDeploymentHandler(context),
		deltaProcessor:          infrastructure.NewDeltaProcessor(context),
	}
}

func (d *deploymentReconciler) Reconcile() error {

	imageName, err := d.imageHandler.ResolveImage()
	// we only create the rest of the resources once we have a resolvable image
	if err != nil {
		return err
	} else if len(imageName) == 0 {
		return infrastructure.ErrorForImageNotFound()
	}

	// Create Required resource
	requestedResources, err := d.createRequiredResources(imageName)
	if err != nil {
		return err
	}

	// Get Deployed resource
	deployedResources, err := d.getDeployedResources()
	if err != nil {
		return err
	}

	// Process Delta
	if err = d.processDelta(requestedResources, deployedResources); err != nil {
		return err
	}

	return nil
}

func (d *deploymentReconciler) createRequiredResources(imageName string) (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	deployment := d.kogitoDeploymentHandler.CreateDeployment(d.instance, imageName, d.definition)
	if err := d.onDeploymentCreate(deployment); err != nil {
		return resources, err
	}

	d.mountEnvsOnDeployment(deployment)
	if err := d.mountConfigMapReferencesOnDeployment(deployment); err != nil {
		return resources, err
	}
	if err := d.mountSecretReferencesOnDeployment(deployment); err != nil {
		return resources, err
	}
	if err := framework.SetOwner(d.instance, d.Scheme, deployment); err != nil {
		return nil, err
	}
	resources[reflect.TypeOf(appsv1.Deployment{})] = []client.Object{deployment}
	return resources, nil
}

func (d *deploymentReconciler) getDeployedResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	deployment, err := d.deploymentHandler.FetchDeployment(types.NamespacedName{Name: d.instance.GetName(), Namespace: d.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if deployment != nil {
		resources[reflect.TypeOf(appsv1.Deployment{})] = []client.Object{deployment}
	}
	return resources, nil
}

func (d *deploymentReconciler) processDelta(requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (err error) {
	comparator := d.deploymentHandler.GetComparator()
	_, err = d.deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return
}

func (d *deploymentReconciler) onDeploymentCreate(deployment *appsv1.Deployment) error {
	if d.Client.IsOpenshift() {
		key, value := d.imageHandler.ResolveImageStreamTriggerAnnotation(d.instance.GetName())
		deployment.Annotations = map[string]string{key: value}
	}
	if d.definition.OnDeploymentCreate != nil {
		if err := d.definition.OnDeploymentCreate(deployment); err != nil {
			return err
		}
	}
	return nil
}

func (d *deploymentReconciler) mountConfigMapReferencesOnDeployment(deployment *appsv1.Deployment) error {
	configMapHandler := infrastructure.NewConfigMapHandler(d.Context)
	for _, configMapEnvFromReference := range d.definition.ConfigMapEnvFromReferences {
		configMapHandler.MountAsEnvFrom(deployment, configMapEnvFromReference)
	}

	for _, configMapVolumeReference := range d.definition.ConfigMapVolumeReferences {
		if err := configMapHandler.MountAsVolume(deployment, configMapVolumeReference); err != nil {
			return err
		}
	}
	return nil
}

func (d *deploymentReconciler) mountSecretReferencesOnDeployment(deployment *appsv1.Deployment) error {
	secretHandler := infrastructure.NewSecretHandler(d.Context)
	for _, secretEnvFromReference := range d.definition.SecretEnvFromReferences {
		secretHandler.MountAsEnvFrom(deployment, secretEnvFromReference)
	}

	for _, secretVolumeReference := range d.definition.SecretVolumeReferences {
		if err := secretHandler.MountAsVolume(deployment, secretVolumeReference); err != nil {
			return err
		}
	}
	return nil
}

func (d *deploymentReconciler) mountEnvsOnDeployment(deployment *appsv1.Deployment) {
	deployment.Spec.Template.Spec.Containers[0].Env = framework.EnvOverride(deployment.Spec.Template.Spec.Containers[0].Env, framework.CreateEnvVar(infrastructure.RuntimeTypeKey, string(d.instance.GetSpec().GetRuntime())))
	deployment.Spec.Template.Spec.Containers[0].Env = framework.EnvOverride(deployment.Spec.Template.Spec.Containers[0].Env, d.definition.Envs...)
}
