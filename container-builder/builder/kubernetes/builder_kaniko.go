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
	"path"

	corev1 "k8s.io/api/core/v1"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
)

var _ Scheduler = &kanikoScheduler{}

type kanikoScheduler struct {
	*scheduler
	KanikoTask *api.KanikoTask
}

type kanikoSchedulerHandler struct {
}

var _ schedulerHandler = &kanikoSchedulerHandler{}

func (k kanikoSchedulerHandler) CreateScheduler(info ContainerBuilderInfo, buildCtx containerBuildContext) Scheduler {
	kanikoTask := api.KanikoTask{
		ContainerBuildBaseTask: api.ContainerBuildBaseTask{Name: "KanikoTask"},
		PublishTask: api.PublishTask{
			ContextDir: path.Join("/builder", info.BuildUniqueName, "context"),
			BaseImage:  info.Platform.Spec.BaseImage,
			Image:      info.FinalImageName,
			Registry:   info.Platform.Spec.Registry,
		},
		Cache: api.KanikoTaskCache{},
	}

	buildCtx.ContainerBuild = &api.ContainerBuild{
		Spec: api.ContainerBuildSpec{
			Tasks:    []api.ContainerBuildTask{{Kaniko: &kanikoTask}},
			Strategy: api.ContainerBuildStrategyPod,
			Timeout:  *info.Platform.Spec.Timeout,
		},
		Status: api.ContainerBuildStatus{},
	}
	buildCtx.ContainerBuild.Name = info.BuildUniqueName
	buildCtx.ContainerBuild.Namespace = info.Platform.Namespace

	sched := &kanikoScheduler{
		&scheduler{
			builder: builder{
				Context: buildCtx,
			},
			Resources: make([]resource, 0),
		},
		&kanikoTask,
	}
	return sched
}

func (k kanikoSchedulerHandler) CanHandle(info ContainerBuilderInfo) bool {
	return info.Platform.Spec.BuildStrategy == api.ContainerBuildStrategyPod && info.Platform.Spec.PublishStrategy == api.PlatformBuildPublishStrategyKaniko
}

func (sk *kanikoScheduler) WithProperty(property BuilderProperty, object interface{}) Scheduler {
	if property == KanikoCache {
		sk.KanikoTask.Cache = object.(api.KanikoTaskCache)
	}
	return sk
}

func (sk *kanikoScheduler) WithResourceRequirements(res corev1.ResourceRequirements) Scheduler {
	sk.KanikoTask.Resources = res
	return sk
}

func (sk *kanikoScheduler) WithAdditionalArgs(flags []string) Scheduler {
	sk.KanikoTask.AdditionalFlags = flags
	return sk
}

func (sk *kanikoScheduler) Schedule() (*api.ContainerBuild, error) {
	// verify if we really need this
	for _, task := range sk.builder.Context.ContainerBuild.Spec.Tasks {
		if task.Kaniko != nil {
			task.Kaniko = sk.KanikoTask
			break
		}
	}
	return sk.scheduler.Schedule()
}
