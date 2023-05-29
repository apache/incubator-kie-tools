// Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package kubernetes

import (
	"context"
	"fmt"

	corev1 "k8s.io/api/core/v1"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/client"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/log"
)

type BuilderProperty string

const KanikoCache BuilderProperty = "kaniko-cache"

type ContainerBuilderInfo struct {
	FinalImageName  string
	BuildUniqueName string
	Platform        api.PlatformContainerBuild
}

type resource struct {
	Target  string
	Content []byte
}

type containerBuildContext struct {
	client.Client
	C              context.Context
	ContainerBuild *api.ContainerBuild
	BaseImage      string
}

type builder struct {
	L       log.Logger
	Context containerBuildContext
}

type scheduler struct {
	Scheduler
	builder   builder
	Resources []resource
}

var _ Scheduler = &scheduler{}
var _ ContainerBuilder = &builder{}

// available schedulers, add them in priority order
var schedulers = map[string]schedulerHandler{
	"kaniko": &kanikoSchedulerHandler{},
}

// Scheduler provides an interface to add resources and schedule a new build
type Scheduler interface {
	// WithResource the actual file/resource to add to the builder. Might be called multiple times.
	WithResource(target string, content []byte) Scheduler
	WithClient(client client.Client) Scheduler
	// WithResourceRequirements Kubernetes resource requirements to be passed to the underlying builder if necessary. For example, a builder pod might require specific resources underneath.
	WithResourceRequirements(res corev1.ResourceRequirements) Scheduler
	// WithAdditionalArgs array of strings to pass to the underlying builder. For example "--myarg=myvalue" or "MY_ENV=MY_VALUE". The args are passed separated by spaces.
	WithAdditionalArgs(args []string) Scheduler
	// WithProperty specialized property known by inner implementations for additional properties to configure the underlying builder
	WithProperty(property BuilderProperty, object interface{}) Scheduler
	Schedule() (*api.ContainerBuild, error)
}

type ContainerBuilder interface {
	WithClient(client client.Client) ContainerBuilder
	CancelBuild() (*api.ContainerBuild, error)
	Reconcile() (*api.ContainerBuild, error)
}

type schedulerHandler interface {
	CreateScheduler(info ContainerBuilderInfo, buildCtx containerBuildContext) Scheduler
	CanHandle(info ContainerBuilderInfo) bool
}

func FromBuild(build *api.ContainerBuild) ContainerBuilder {
	return &builder{
		L: log.WithName(util.ComponentName),
		Context: containerBuildContext{
			ContainerBuild: build,
			C:              context.TODO(),
		},
	}
}

// NewBuild is the API entry for the ContainerBuilder. Create a new ContainerBuild instance based on PlatformContainerBuild.
func NewBuild(info ContainerBuilderInfo) Scheduler {
	ctx := containerBuildContext{
		BaseImage: info.Platform.Spec.BaseImage,
		C:         context.TODO(),
	}

	for _, v := range schedulers {
		if v.CanHandle(info) {
			return v.CreateScheduler(info, ctx)
		}
	}
	panic(fmt.Errorf("ContainerBuildStrategy %s with PublishStrategy %s is not supported", info.Platform.Spec.BuildStrategy, info.Platform.Spec.PublishStrategy))
}

func (s *scheduler) WithClient(client client.Client) Scheduler {
	s.builder.WithClient(client)
	return s.Scheduler
}

func (s *scheduler) WithResource(target string, content []byte) Scheduler {
	s.Resources = append(s.Resources, resource{target, content})
	return s.Scheduler
}

func (s *scheduler) WithResourceRequirements(res corev1.ResourceRequirements) Scheduler {
	// no default implementation.
	return s.Scheduler
}

func (s *scheduler) WithAdditionalArgs(args []string) Scheduler {
	// no default implementation.
	return s.Scheduler
}

func (s *scheduler) WithProperty(property BuilderProperty, object interface{}) Scheduler {
	// no default implementation
	return s.Scheduler
}

// Schedule schedules a new build in the platform
func (s *scheduler) Schedule() (*api.ContainerBuild, error) {
	// TODO: create a handler to mount the resources according to the platform/context options (for now we only have CM, PoC level)
	if err := mountResourcesWithConfigMap(&s.builder.Context, &s.Resources); err != nil {
		return nil, err
	}
	return s.builder.Reconcile()
}

func (b *builder) WithClient(client client.Client) ContainerBuilder {
	b.Context.Client = client
	return b
}

// Reconcile idempotent build flow control.
// Can be called many times to check/update the current status of the build instance, indexed by the Platform and ContainerBuild Name.
func (b *builder) Reconcile() (*api.ContainerBuild, error) {
	var actions []Action
	switch b.Context.ContainerBuild.Spec.Strategy {
	case api.ContainerBuildStrategyPod:
		// build the action flow:
		actions = []Action{
			newInitializePodAction(),
			newScheduleAction(),
			newMonitorPodAction(),
			newErrorRecoveryAction(),
		}
	}

	target := b.Context.ContainerBuild.DeepCopy()

	for _, a := range actions {
		a.InjectLogger(b.L)
		a.InjectClient(b.Context.Client)

		if a.CanHandle(target) {
			b.L.Infof("Invoking action %s", a.Name())
			newTarget, err := a.Handle(b.Context.C, target)
			if err != nil {
				b.L.Errorf(err, "Failed to invoke action %s", a.Name())
				return nil, err
			}

			if newTarget != nil {
				if newTarget.Status.Phase != target.Status.Phase {
					b.L.Info(
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

func (b *builder) CancelBuild() (*api.ContainerBuild, error) {
	//TODO implement me
	panic("implement me")
}
