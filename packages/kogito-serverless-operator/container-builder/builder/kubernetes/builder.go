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

	"k8s.io/klog/v2"

	corev1 "k8s.io/api/core/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/util/log"
)

type BuilderProperty string

const KanikoCache BuilderProperty = "kaniko-cache"

type ContainerBuilderInfo struct {
	FinalImageName  string
	BuildUniqueName string
	Platform        api.PlatformContainerBuild
	// ContainerBuilderImageTag the image tag used internally to create the pod builder (e.g. Kaniko Executor Builder image)
	ContainerBuilderImageTag string
}

type resource struct {
	Target  string
	Content []byte
	Path    string
}

type resourceConfigMap struct {
	Ref  corev1.LocalObjectReference
	Path string
}

type containerBuildContext struct {
	c              client.Client
	ctx            context.Context
	containerBuild *api.ContainerBuild
	baseImage      string
}

type reconciler struct {
	containerBuildContext *containerBuildContext
}

type mountHandler struct {
	containerBuildContext *containerBuildContext
	reconciler            *reconciler
	info                  ContainerBuilderInfo
	resources             []resource
	resourceConfigMaps    []resourceConfigMap
}

type schedulerHook func() (*api.ContainerBuild, error)

var _ Reconciler = &reconciler{}
var _ MountHandler = &mountHandler{}

// available schedulers, add them in priority order
var schedulers = map[string]schedulerManager{
	"kaniko": &kanikoSchedulerManager{},
}

// Scheduler provides an interface to add resources and schedule a new build
type Scheduler interface {
	// WithResourceRequirements Kubernetes resource requirements to be passed to the underlying builder if necessary. For example, a builder pod might require specific resources underneath.
	WithResourceRequirements(res corev1.ResourceRequirements) Scheduler
	// WithAdditionalArgs array of strings to pass to the underlying builder. For example "--myarg=myvalue" or "MY_ENV=MY_VALUE". The args are passed separated by spaces.
	WithAdditionalArgs(args []string) Scheduler
	// WithProperty specialized property known by inner implementations for additional properties to configure the underlying builder
	WithProperty(property BuilderProperty, object interface{}) Scheduler
	WithBuildArgs(args []corev1.EnvVar) Scheduler
	WithEnvs(envs []corev1.EnvVar) Scheduler
	Schedule() (*api.ContainerBuild, error)
}

type Reconciler interface {
	WithClient(client client.Client) Reconciler
	CancelBuild() (*api.ContainerBuild, error)
	Reconcile() (*api.ContainerBuild, error)
}

type MountHandler interface {
	// AddResource the actual file/resource to add to the build context in the relative root path. Might be called multiple times.
	AddResource(target string, content []byte) MountHandler
	// AddConfigMapResource the configMap to add to the build context. Might be called multiple times.
	// This ConfigMap is a Kubernetes LocalObjectReference, meaning that must be within the Platform namespace.
	AddConfigMapResource(configMap corev1.LocalObjectReference, path string) MountHandler
	WithClient(client client.Client) MountHandler
	Scheduler() Scheduler
}

type schedulerManager interface {
	CreateScheduler(info ContainerBuilderInfo, ctx *containerBuildContext, hook schedulerHook) Scheduler
	CanHandle(info ContainerBuilderInfo) bool
}

// NewBuild is the API entry for the Reconciler. Create a new ContainerBuild instance based on PlatformContainerBuild.
func NewBuild(info ContainerBuilderInfo) MountHandler {
	buildContext := &containerBuildContext{
		baseImage: info.Platform.Spec.BaseImage,
		ctx:       context.TODO(),
	}
	return &mountHandler{
		containerBuildContext: buildContext,
		reconciler:            &reconciler{containerBuildContext: buildContext},
		info:                  info,
		resources:             make([]resource, 0),
		resourceConfigMaps:    make([]resourceConfigMap, 0),
	}
}

func (m *mountHandler) newContainerBuild() (*api.ContainerBuild, error) {
	// TODO: create a handler to mount the resources according to the platform/context options, for now only CM
	if err := mountResourcesBinaryWithConfigMapToBuild(m.containerBuildContext, &m.resources); err != nil {
		return nil, err
	}
	// Add the CMs to the build volume
	mountResourcesConfigMapToBuild(m.containerBuildContext, &m.resourceConfigMaps)
	return m.reconciler.Reconcile()
}

func (m *mountHandler) WithClient(client client.Client) MountHandler {
	m.containerBuildContext.c = client
	return m
}

func (m *mountHandler) AddResource(target string, content []byte) MountHandler {
	m.resources = append(m.resources, resource{target, content, ""})
	return m
}

func (m *mountHandler) AddConfigMapResource(configMap corev1.LocalObjectReference, path string) MountHandler {
	m.resourceConfigMaps = append(m.resourceConfigMaps, resourceConfigMap{configMap, path})
	return m
}

func (m *mountHandler) Scheduler() Scheduler {
	for _, v := range schedulers {
		if v.CanHandle(m.info) {
			return v.CreateScheduler(m.info, m.containerBuildContext, m.newContainerBuild)
		}
	}
	panic(fmt.Errorf("ContainerBuildStrategy %s with PublishStrategy %s is not supported",
		m.info.Platform.Spec.BuildStrategy,
		m.info.Platform.Spec.PublishStrategy))
}

func FromBuild(build *api.ContainerBuild) Reconciler {
	return &reconciler{
		containerBuildContext: &containerBuildContext{
			containerBuild: build,
			ctx:            context.TODO(),
		},
	}
}

func (b *reconciler) WithClient(client client.Client) Reconciler {
	b.containerBuildContext.c = client
	return b
}

// Reconcile idempotent build flow control.
// Can be called many times to check/update the current status of the build instance, indexed by the Platform and ContainerBuild Name.
func (b *reconciler) Reconcile() (*api.ContainerBuild, error) {
	var actions []Action
	switch b.containerBuildContext.containerBuild.Spec.Strategy {
	case api.ContainerBuildStrategyPod:
		// build the action flow:
		actions = []Action{
			newInitializePodAction(),
			newScheduleAction(),
			newMonitorPodAction(),
			newErrorRecoveryAction(),
		}
	}

	target := b.containerBuildContext.containerBuild.DeepCopy()

	for _, a := range actions {
		a.InjectClient(b.containerBuildContext.c)

		if a.CanHandle(target) {
			klog.V(log.I).InfoS("Invoking action", "buildAction", a.Name())
			newTarget, err := a.Handle(b.containerBuildContext.ctx, target)
			if err != nil {
				klog.V(log.E).ErrorS(err, "Failed to invoke action", "buildAction", a.Name())
				return nil, err
			}

			if newTarget != nil {
				if newTarget.Status.Phase != target.Status.Phase {
					klog.V(log.I).InfoS(
						"state transition",
						"phase-from", target.Status.Phase,
						"phase-to", newTarget.Status.Phase,
					)
				}

				target = newTarget
			}
			break
		}
	}

	return target, nil
}

func (b *reconciler) CancelBuild() (*api.ContainerBuild, error) {
	// TODO: do the actual implementation if that makes sense
	panic("CancelBuild: Operation Not Supported")
}
