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

package kubernetes

import (
	appsv1 "k8s.io/api/apps/v1"
	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"

	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"

	monitoringv1 "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"

	routev1 "github.com/openshift/api/route/v1"
)

// A Collection is a container of Kubernetes resources.
type Collection struct {
	items []ctrl.Object
}

// NewCollection creates a new empty collection.
func NewCollection(objects ...ctrl.Object) *Collection {
	collection := Collection{
		items: make([]ctrl.Object, 0, len(objects)),
	}

	collection.items = append(collection.items, objects...)

	return &collection
}

// Size returns the number of resources belonging to the collection.
func (c *Collection) Size() int {
	return len(c.items)
}

// Items returns all resources belonging to the collection.
func (c *Collection) Items() []ctrl.Object {
	return c.items
}

// AsKubernetesList returns all resources wrapped in a Kubernetes list.
func (c *Collection) AsKubernetesList() *corev1.List {
	lst := corev1.List{
		TypeMeta: metav1.TypeMeta{
			Kind:       "List",
			APIVersion: "v1",
		},
		Items: make([]runtime.RawExtension, 0, len(c.items)),
	}
	for _, res := range c.items {
		raw := runtime.RawExtension{
			Object: res,
		}
		lst.Items = append(lst.Items, raw)
	}
	return &lst
}

// Add adds a resource to the collection.
func (c *Collection) Add(resource ctrl.Object) {
	if resource != nil {
		c.items = append(c.items, resource)
	}
}

// AddFirst adds a resource to the head of the collection.
func (c *Collection) AddFirst(resource ctrl.Object) {
	if resource != nil {
		c.items = append([]ctrl.Object{resource}, c.items...)
	}
}

// AddAll adds all resources to the collection.
func (c *Collection) AddAll(resource []ctrl.Object) {
	c.items = append(c.items, resource...)
}

// VisitDeployment executes the visitor function on all Deployment resources.
func (c *Collection) VisitDeployment(visitor func(*appsv1.Deployment)) {
	c.Visit(func(res runtime.Object) {
		if conv, ok := res.(*appsv1.Deployment); ok {
			visitor(conv)
		}
	})
}

// VisitDeploymentE executes the visitor function on all Deployment resources.
func (c *Collection) VisitDeploymentE(visitor func(*appsv1.Deployment) error) error {
	return c.VisitE(func(res runtime.Object) error {
		if conv, ok := res.(*appsv1.Deployment); ok {
			return visitor(conv)
		}

		return nil
	})
}

// GetDeployment returns a Deployment that matches the given function.
func (c *Collection) GetDeployment(filter func(*appsv1.Deployment) bool) *appsv1.Deployment {
	var retValue *appsv1.Deployment
	c.VisitDeployment(func(re *appsv1.Deployment) {
		if filter(re) {
			retValue = re
		}
	})
	return retValue
}

// GetDeploymentForWorkflow returns a Deployment for the given workflow.
func (c *Collection) GetDeploymentForWorkflow(workflow *operatorapi.KogitoServerlessWorkflow) *appsv1.Deployment {
	if workflow == nil {
		return nil
	}

	return c.GetDeployment(func(d *appsv1.Deployment) bool {
		return d.ObjectMeta.Labels[metadata.Name] == workflow.Name
	})
}

// HasDeployment returns true if a deployment matching the given condition is present.
func (c *Collection) HasDeployment(filter func(*appsv1.Deployment) bool) bool {
	return c.GetDeployment(filter) != nil
}

// RemoveDeployment removes and returns a Deployment that matches the given function.
func (c *Collection) RemoveDeployment(filter func(*appsv1.Deployment) bool) *appsv1.Deployment {
	res := c.Remove(func(res runtime.Object) bool {
		if conv, ok := res.(*appsv1.Deployment); ok {
			return filter(conv)
		}
		return false
	})
	if res == nil {
		return nil
	}
	deploy, ok := res.(*appsv1.Deployment)
	if !ok {
		return nil
	}

	return deploy
}

// VisitConfigMap executes the visitor function on all ConfigMap resources.
func (c *Collection) VisitConfigMap(visitor func(*corev1.ConfigMap)) {
	c.Visit(func(res runtime.Object) {
		if conv, ok := res.(*corev1.ConfigMap); ok {
			visitor(conv)
		}
	})
}

// GetConfigMap returns a ConfigMap that matches the given function.
func (c *Collection) GetConfigMap(filter func(*corev1.ConfigMap) bool) *corev1.ConfigMap {
	var retValue *corev1.ConfigMap
	c.VisitConfigMap(func(re *corev1.ConfigMap) {
		if filter(re) {
			retValue = re
		}
	})
	return retValue
}

// RemoveConfigMap removes and returns a ConfigMap that matches the given function.
func (c *Collection) RemoveConfigMap(filter func(*corev1.ConfigMap) bool) *corev1.ConfigMap {
	res := c.Remove(func(res runtime.Object) bool {
		if conv, ok := res.(*corev1.ConfigMap); ok {
			return filter(conv)
		}
		return false
	})
	if res == nil {
		return nil
	}
	cm, ok := res.(*corev1.ConfigMap)
	if !ok {
		return nil
	}

	return cm
}

// VisitService executes the visitor function on all Service resources.
func (c *Collection) VisitService(visitor func(*corev1.Service)) {
	c.Visit(func(res runtime.Object) {
		if conv, ok := res.(*corev1.Service); ok {
			visitor(conv)
		}
	})
}

// GetService returns a Service that matches the given function.
func (c *Collection) GetService(filter func(*corev1.Service) bool) *corev1.Service {
	var retValue *corev1.Service
	c.VisitService(func(re *corev1.Service) {
		if filter(re) {
			retValue = re
		}
	})
	return retValue
}

// GetUserServiceForWorkflow returns a user Service for the given workflow.
func (c *Collection) GetUserServiceForWorkflow(workflow *operatorapi.KogitoServerlessWorkflow) *corev1.Service {
	if workflow == nil {
		return nil
	}
	return c.GetService(func(s *corev1.Service) bool {
		return s.ObjectMeta.Labels != nil &&
			s.ObjectMeta.Labels[metadata.Label] == workflow.Name &&
			s.ObjectMeta.Labels[metadata.ServiceType] == operatorapi.ServiceTypeUser
	})
}

// GetServiceForWorkflow returns a user Service for the given workflow.
func (c *Collection) GetServiceForWorkflow(workflow *operatorapi.KogitoServerlessWorkflow) *corev1.Service {
	if workflow == nil {
		return nil
	}
	return c.GetService(func(s *corev1.Service) bool {
		return s.ObjectMeta.Labels != nil && s.ObjectMeta.Labels[metadata.Label] == workflow.Name
	})
}

// VisitRoute executes the visitor function on all Route resources.
func (c *Collection) VisitRoute(visitor func(*routev1.Route)) {
	c.Visit(func(res runtime.Object) {
		if conv, ok := res.(*routev1.Route); ok {
			visitor(conv)
		}
	})
}

// GetRoute returns a Route that matches the given function.
func (c *Collection) GetRoute(filter func(*routev1.Route) bool) *routev1.Route {
	var retValue *routev1.Route
	c.VisitRoute(func(re *routev1.Route) {
		if filter(re) {
			retValue = re
		}
	})
	return retValue
}

// GetCronJob returns a CronJob that matches the given function.
func (c *Collection) GetCronJob(filter func(job *batchv1.CronJob) bool) *batchv1.CronJob {
	var retValue *batchv1.CronJob
	c.VisitCronJob(func(re *batchv1.CronJob) {
		if filter(re) {
			retValue = re
		}
	})
	return retValue
}

// VisitCronJob executes the visitor function on all CronJob resources.
func (c *Collection) VisitCronJob(visitor func(*batchv1.CronJob)) {
	c.Visit(func(res runtime.Object) {
		if conv, ok := res.(*batchv1.CronJob); ok {
			visitor(conv)
		}
	})
}

// VisitCronJobE executes the visitor function on all CronJob resources.
func (c *Collection) VisitCronJobE(visitor func(*batchv1.CronJob) error) error {
	return c.VisitE(func(res runtime.Object) error {
		if conv, ok := res.(*batchv1.CronJob); ok {
			return visitor(conv)
		}

		return nil
	})
}

// GetContainer --.
func (c *Collection) GetContainer(filter func(container *corev1.Container) bool) *corev1.Container {
	var retValue *corev1.Container

	c.VisitContainer(func(container *corev1.Container) {
		if filter(container) {
			retValue = container
		}
	})

	return retValue
}

// GetContainerByName --.
func (c *Collection) GetContainerByName(name string) *corev1.Container {
	return c.GetContainer(func(c *corev1.Container) bool {
		return c.Name == name
	})
}

// VisitContainer executes the visitor function on all Containers inside deployments or other resources.
func (c *Collection) VisitContainer(visitor func(container *corev1.Container)) {
	c.VisitDeployment(func(d *appsv1.Deployment) {
		for idx := range d.Spec.Template.Spec.Containers {
			cntref := &d.Spec.Template.Spec.Containers[idx]
			visitor(cntref)
		}
	})
}

// GetController returns the controller associated with the workflow (e.g. Deployment).
func (c *Collection) GetController(filter func(object ctrl.Object) bool) ctrl.Object {
	d := c.GetDeployment(func(deployment *appsv1.Deployment) bool {
		return filter(deployment)
	})
	if d != nil {
		return d
	}
	return nil
}

// VisitPodSpec executes the visitor function on all PodSpec inside deployments or other resources.
func (c *Collection) VisitPodSpec(visitor func(container *corev1.PodSpec)) {
	c.VisitDeployment(func(d *appsv1.Deployment) {
		visitor(&d.Spec.Template.Spec)
	})
}

// VisitPodTemplateMeta executes the visitor function on all PodTemplate metadata inside deployments or other resources.
func (c *Collection) VisitPodTemplateMeta(visitor func(meta *metav1.ObjectMeta)) {
	c.VisitDeployment(func(d *appsv1.Deployment) {
		visitor(&d.Spec.Template.ObjectMeta)
	})
}

// VisitMetaObject executes the visitor function on all meta.Object resources.
func (c *Collection) VisitMetaObject(visitor func(metav1.Object)) {
	c.Visit(func(res runtime.Object) {
		if conv, ok := res.(metav1.Object); ok {
			visitor(conv)
		}
	})
}

// Visit executes the visitor function on all resources.
func (c *Collection) Visit(visitor func(runtime.Object)) {
	for _, res := range c.items {
		visitor(res)
	}
}

// VisitE executes the visitor function on all resources breaking if the visitor function
// returns an error.
func (c *Collection) VisitE(visitor func(runtime.Object) error) error {
	for _, res := range c.items {
		if err := visitor(res); err != nil {
			return err
		}
	}

	return nil
}

// Remove removes the given element from the collection and returns it.
func (c *Collection) Remove(selector func(runtime.Object) bool) runtime.Object {
	for idx, res := range c.items {
		if selector(res) {
			c.items = append(c.items[0:idx], c.items[idx+1:]...)
			return res
		}
	}
	return nil
}

func (c *Collection) VisitPodMonitor(visitor func(*monitoringv1.PodMonitor)) {
	c.Visit(func(res runtime.Object) {
		if conv, ok := res.(*monitoringv1.PodMonitor); ok {
			visitor(conv)
		}
	})
}

func (c *Collection) GetPodMonitor(filter func(*monitoringv1.PodMonitor) bool) *monitoringv1.PodMonitor {
	var retValue *monitoringv1.PodMonitor
	c.VisitPodMonitor(func(podMonitor *monitoringv1.PodMonitor) {
		if filter(podMonitor) {
			retValue = podMonitor
		}
	})
	return retValue
}
