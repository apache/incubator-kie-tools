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
	"path"

	corev1 "k8s.io/api/core/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
)

var _ Scheduler = &kanikoScheduler{}

type kanikoScheduler struct {
	schedulerHook         schedulerHook
	kanikoTask            *api.KanikoTask
	info                  ContainerBuilderInfo
	containerBuildContext *containerBuildContext
}

type kanikoSchedulerManager struct {
}

var _ schedulerManager = &kanikoSchedulerManager{}

func (k kanikoSchedulerManager) CreateScheduler(info ContainerBuilderInfo, ctx *containerBuildContext, hook schedulerHook) Scheduler {
	kanikoTask := api.KanikoTask{
		ContainerBuildBaseTask: api.ContainerBuildBaseTask{Name: "KanikoTask"},
		PublishTask: api.PublishTask{
			ContextDir: path.Join("/builder", info.BuildUniqueName, "context"),
			BaseImage:  info.Platform.Spec.BaseImage,
			Image:      info.FinalImageName,
			Registry:   info.Platform.Spec.Registry,
		},
		Cache:               api.KanikoTaskCache{},
		KanikoExecutorImage: info.ContainerBuilderImageTag,
	}

	ctx.containerBuild = &api.ContainerBuild{
		Spec: api.ContainerBuildSpec{
			Tasks:    []api.ContainerBuildTask{{Kaniko: &kanikoTask}},
			Strategy: api.ContainerBuildStrategyPod,
			Timeout:  *info.Platform.Spec.Timeout,
		},
		Status: api.ContainerBuildStatus{},
	}
	ctx.containerBuild.Name = info.BuildUniqueName
	ctx.containerBuild.Namespace = info.Platform.Namespace

	sched := &kanikoScheduler{
		schedulerHook: hook,
		kanikoTask:    &kanikoTask,
	}
	return sched
}

func (k kanikoSchedulerManager) CanHandle(info ContainerBuilderInfo) bool {
	return info.Platform.Spec.BuildStrategy == api.ContainerBuildStrategyPod && info.Platform.Spec.PublishStrategy == api.PlatformBuildPublishStrategyKaniko
}

func (sk *kanikoScheduler) WithProperty(property BuilderProperty, object interface{}) Scheduler {
	if property == KanikoCache {
		sk.kanikoTask.Cache = object.(api.KanikoTaskCache)
	}
	return sk
}

func (sk *kanikoScheduler) WithResourceRequirements(res corev1.ResourceRequirements) Scheduler {
	sk.kanikoTask.Resources = res
	return sk
}

func (sk *kanikoScheduler) WithAdditionalArgs(flags []string) Scheduler {
	sk.kanikoTask.AdditionalFlags = flags
	return sk
}

func (sk *kanikoScheduler) WithBuildArgs(args []corev1.EnvVar) Scheduler {
	sk.kanikoTask.BuildArgs = args
	return sk
}

func (sk *kanikoScheduler) WithEnvs(envs []corev1.EnvVar) Scheduler {
	sk.kanikoTask.Envs = envs
	return sk
}

func (sk *kanikoScheduler) Schedule() (*api.ContainerBuild, error) {
	return sk.schedulerHook()
}
