// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package framework

import (
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	monv1 "github.com/coreos/prometheus-operator/pkg/apis/monitoring/v1"
	"github.com/kiegroup/kogito-operator/core/client"
	appsv1 "github.com/openshift/api/apps/v1"
	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	routev1 "github.com/openshift/api/route/v1"
	apps "k8s.io/api/apps/v1"
	"sort"

	v1 "k8s.io/api/core/v1"

	"reflect"
)

// Comparator is a simple struct to encapsulate the complex elements from Operator Utils
type Comparator struct {
	ResourceType reflect.Type
	CompFunc     func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool
}

// ComparatorBuilder creates Comparators to be used during reconciliation phases
type ComparatorBuilder interface {
	// WithCustomComparator it's the custom comparator function that will get called by the Operator Utils
	WithCustomComparator(customComparator func(deployed resource.KubernetesResource, requested resource.KubernetesResource) (equal bool)) ComparatorBuilder
	// WithType defines the comparator resource type
	WithType(resourceType reflect.Type) ComparatorBuilder
	// UseDefaultComparator defines if the comparator will delegate the comparision to inner comparators from Operator Utils
	UseDefaultComparator() ComparatorBuilder
	// Build creates the Comparator in the form of Operator Utils interface
	Build() (reflect.Type, func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool)
}

// NewComparatorBuilder creates a new comparator builder for comparision usages
func NewComparatorBuilder() ComparatorBuilder {
	return &comparatorBuilder{
		comparator: &Comparator{},
	}
}

type comparatorBuilder struct {
	comparator        *Comparator
	customComparator  func(deployed resource.KubernetesResource, requested resource.KubernetesResource) (changed bool)
	defaultComparator func(deployed resource.KubernetesResource, requested resource.KubernetesResource) (changed bool)
	client            *client.Client
}

func (c *comparatorBuilder) WithClient(cli *client.Client) ComparatorBuilder {
	c.client = cli
	return c
}

func (c *comparatorBuilder) WithType(resourceType reflect.Type) ComparatorBuilder {
	c.comparator.ResourceType = resourceType
	return c
}

func (c *comparatorBuilder) WithCustomComparator(customComparator func(deployed resource.KubernetesResource, requested resource.KubernetesResource) (changed bool)) ComparatorBuilder {
	c.customComparator = customComparator
	return c
}

func (c *comparatorBuilder) UseDefaultComparator() ComparatorBuilder {
	c.defaultComparator = compare.DefaultComparator().GetComparator(c.comparator.ResourceType)
	// we don't have a default comparator for the given type, call the generic one
	if c.defaultComparator == nil {
		c.defaultComparator = compare.DefaultComparator().GetDefaultComparator()
	}
	return c
}

func (c *comparatorBuilder) Build() (reflect.Type, func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool) {
	c.comparator.CompFunc = func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		equal := true
		// calls the first comparator defined by the caller
		if c.customComparator != nil {
			equal = c.customComparator(deployed, requested) && equal
		}
		if equal && c.defaultComparator != nil {
			// calls the default comparator from Operator Utils
			equal = c.defaultComparator(deployed, requested) && equal
		}
		return equal
	}
	return c.comparator.ResourceType, c.comparator.CompFunc
}

func containAllLabels(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	deployedLabels := deployed.GetLabels()
	requestedLabels := requested.GetLabels()

	for key, value := range requestedLabels {
		if deployedLabels[key] != value {
			return false
		}
	}

	return true
}

// CreateDeploymentConfigComparator creates a new comparator for DeploymentConfig using Trigger and RollingParams
func CreateDeploymentConfigComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		sortVolumes(&deployed.(*appsv1.DeploymentConfig).Spec.Template.Spec)
		sortVolumes(&requested.(*appsv1.DeploymentConfig).Spec.Template.Spec)
		ignoreInjectedVariables(
			deployed.(*appsv1.DeploymentConfig).Spec.Template,
			requested.(*appsv1.DeploymentConfig).Spec.Template)

		dcDeployed := deployed.(*appsv1.DeploymentConfig)
		dcRequested := requested.(*appsv1.DeploymentConfig).DeepCopy()

		for i := range dcDeployed.Spec.Triggers {
			if len(dcRequested.Spec.Triggers) <= i {
				return false
			}
			triggerDeployed := dcDeployed.Spec.Triggers[i]
			triggerRequested := dcRequested.Spec.Triggers[i]
			if triggerDeployed.ImageChangeParams != nil && triggerRequested.ImageChangeParams != nil && triggerRequested.ImageChangeParams.From.Namespace == "" {
				//This value is generated based on image stream being found in current or openshift project:
				triggerDeployed.ImageChangeParams.From.Namespace = ""
			}
		}

		if dcRequested.Spec.Strategy.RollingParams == nil && dcDeployed.Spec.Strategy.Type == dcRequested.Spec.Strategy.Type {
			dcDeployed.Spec.Strategy.RollingParams = dcRequested.Spec.Strategy.RollingParams
		}
		return true
	}
}

// CreateDeploymentComparator creates a new comparator for Deployment sorting volumes
func CreateDeploymentComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		sortVolumes(&deployed.(*apps.Deployment).Spec.Template.Spec)
		sortVolumes(&requested.(*apps.Deployment).Spec.Template.Spec)
		ignoreInjectedVariables(
			&deployed.(*apps.Deployment).Spec.Template,
			&requested.(*apps.Deployment).Spec.Template)
		return true
	}
}

// sortVolumes sorts the volumes of a given PodSpec (can be used either by a Deployment or DeploymentConfig objects)
// TODO: open a PR to operatorutils fixing this once we verify KOGITO-2797
func sortVolumes(pod *v1.PodSpec) {
	sort.SliceStable(pod.Volumes, func(i, j int) bool {
		return pod.Volumes[i].Name < pod.Volumes[j].Name
	})
	for _, c := range pod.Containers {
		sort.SliceStable(c.VolumeMounts, func(i, j int) bool {
			return c.VolumeMounts[i].MountPath < c.VolumeMounts[j].MountPath
		})
	}
}

// CreateBuildConfigComparator creates a new comparator for BuildConfig using Label, Trigger and SourceStrategy
func CreateBuildConfigComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		bcDeployed := deployed.(*buildv1.BuildConfig)
		bcRequested := requested.(*buildv1.BuildConfig).DeepCopy()

		if !containAllLabels(bcDeployed, bcRequested) {
			return false
		}
		if len(bcDeployed.Spec.Triggers) > 0 && len(bcRequested.Spec.Triggers) == 0 {
			//Triggers are generated based on provided github repo
			bcDeployed.Spec.Triggers = bcRequested.Spec.Triggers
		}
		if len(bcRequested.Spec.Triggers) == 0 {
			// see: https://issues.redhat.com/browse/KOGITO-2872
			// setting both to nil for the sake of the comparator
			// on OCP 4.5, empty triggers are set to nil
			// on previous versions empty triggers were empty arrays. Yes, it's different.
			requested.(*buildv1.BuildConfig).Spec.Triggers = nil
			bcDeployed.Spec.Triggers = nil
		}
		return true
	}
}

// CreateServiceComparator creates a new comparator for Service using Label
func CreateServiceComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		svcDeployed := deployed.(*v1.Service)
		svcRequested := requested.(*v1.Service).DeepCopy()

		return containAllLabels(svcDeployed, svcRequested)
	}
}

// CreateRouteComparator creates a new comparator for Route using Label
func CreateRouteComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		rtDeployed := deployed.(*routev1.Route)
		rtRequested := requested.(*routev1.Route).DeepCopy()

		return containAllLabels(rtDeployed, rtRequested)
	}
}

// CreateConfigMapComparator creates a new comparator for ConfigMap using Label
func CreateConfigMapComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		cmDeployed := deployed.(*v1.ConfigMap)
		cmRequested := requested.(*v1.ConfigMap).DeepCopy()

		if !containAllLabels(cmDeployed, cmRequested) {
			return false
		}

		return reflect.DeepEqual(cmDeployed.Data, cmRequested.Data)
	}
}

// CreateImageStreamComparator creates a new ImageStream comparator
func CreateImageStreamComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		img1 := deployed.(*imgv1.ImageStream)
		img2 := requested.(*imgv1.ImageStream)

		// lets check if the tag is presented in the deployed stream
		for i := range img1.Spec.Tags {
			img1.Spec.Tags[i].Generation = nil
		}
		for i := range img2.Spec.Tags {
			img2.Spec.Tags[i].Generation = nil
		}
		// there's no tag!
		return compare.Equals(img1.Spec.Tags, img2.Spec.Tags)
	}
}

// CreateSharedImageStreamComparator creates a new Shared ImageStream comparator that verifies if the OwnerReferences are equal between them
// Also incorporates the `CreateImageStreamComparator` logic
func CreateSharedImageStreamComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		return CreateImageStreamComparator()(deployed, requested) &&
			reflect.DeepEqual(deployed.GetOwnerReferences(), requested.GetOwnerReferences())
	}
}

// ignoreInjectedVariables will fetch in deployed PodSpec for injected variables,
// if found, these variable will be copied to the requested one.
// Pods with containers with different sizes will be ignored.
// For an example for such scenario, see: https://knative.dev/docs/eventing/samples/sinkbinding/ which is another operator injecting
// variables in a given Deployment object. That object could be us.
func ignoreInjectedVariables(deployed *v1.PodTemplateSpec, requested *v1.PodTemplateSpec) {
	if len(deployed.Spec.Containers) != len(requested.Spec.Containers) {
		return
	}
	sortContainersByName(deployed)
	sortContainersByName(requested)
	for i := range deployed.Spec.Containers {
		// there's more envs in the deployed object, let's take them to the requested one.
		// all other scenarios (requested with more envs or equal elements are ignored since the equality will consider them not equal anyway)
		if len(deployed.Spec.Containers[i].Env) > len(requested.Spec.Containers[i].Env) {
			diff := DiffEnvVar(deployed.Spec.Containers[i].Env, requested.Spec.Containers[i].Env)
			if len(diff) > 0 {
				requested.Spec.Containers[i].Env = append(requested.Spec.Containers[i].Env, diff...)
			}
		}
	}
}

func sortContainersByName(pod *v1.PodTemplateSpec) {
	sort.Slice(pod.Spec.Containers, func(i, j int) bool {
		return pod.Spec.Containers[i].Name < pod.Spec.Containers[j].Name
	})
}

// CreateServiceMonitorComparator creates a new comparator for ServiceMonitor using Label
func CreateServiceMonitorComparator() func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
	return func(deployed resource.KubernetesResource, requested resource.KubernetesResource) bool {
		smDeployed := deployed.(*monv1.ServiceMonitor)
		smRequested := requested.(*monv1.ServiceMonitor).DeepCopy()

		return containAllLabels(smDeployed, smRequested)
	}
}
