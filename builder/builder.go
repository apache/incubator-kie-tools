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

package builder

import (
	"context"
	"time"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"

	"github.com/kiegroup/container-builder/api"
	builder "github.com/kiegroup/container-builder/builder/kubernetes"
	"github.com/kiegroup/container-builder/client"
)

const (
	resourceDockerfile = "Dockerfile"
)

type Builder struct {
	ctx          context.Context
	commonConfig corev1.ConfigMap
	customConfig map[string]string
}

func NewBuilder(contex context.Context, cm corev1.ConfigMap) Builder {
	return Builder{ctx: contex, commonConfig: cm}
}

func NewBuilderWithConfig(contex context.Context, common corev1.ConfigMap, custom map[string]string) Builder {
	return Builder{ctx: contex, commonConfig: common, customConfig: custom}
}

func (b *Builder) getImageBuilder(workflowID string, imageTag string, workflowDefinition []byte) ImageBuilder {
	containerFile := b.commonConfig.Data[b.commonConfig.Data[configKeyDefaultBuilderResourceName]]
	ib := NewImageBuilder(workflowID, workflowDefinition, []byte(containerFile))
	ib.OnNamespace(b.customConfig[configKeyBuildNamespace])
	ib.WithPodMiddleName(workflowID)
	ib.WithInsecureRegistry(false)
	ib.WithImageName(workflowID + imageTag)
	ib.WithSecret(b.customConfig[configKeyRegistrySecret])
	ib.WithRegistryAddress(b.customConfig[configKeyRegistryAddress])
	return ib
}

func (b *Builder) getImageBuilderForKaniko(workflowID string, imageTag string, workflowDefinition []byte, task *api.KanikoTask) ImageBuilder {
	containerFile := b.commonConfig.Data[b.commonConfig.Data[configKeyDefaultBuilderResourceName]]
	ib := NewImageBuilder(workflowID, workflowDefinition, []byte(containerFile))
	ib.OnNamespace(b.customConfig[configKeyBuildNamespace])
	ib.WithPodMiddleName(workflowID)
	ib.WithInsecureRegistry(false)
	ib.WithImageName(workflowID + imageTag)
	ib.WithSecret(b.customConfig[configKeyRegistrySecret])
	ib.WithRegistryAddress(b.customConfig[configKeyRegistryAddress])
	ib.WithCache(task.Cache)
	ib.WithResources(task.Resources)
	ib.WithAdditionalFlags(task.AdditionalFlags)
	return ib
}

func (b *Builder) ScheduleNewBuildWithTimeout(workflowName string, imageTag string, workflowDefinition []byte, timeout time.Duration) (*api.Build, error) {
	ib := b.getImageBuilder(workflowName, imageTag, workflowDefinition)
	ib.WithTimeout(timeout)
	return b.BuildImage(ib.Build())
}

func (b *Builder) ScheduleNewBuild(workflowName string, imageTag string, workflowDefinition []byte) (*api.Build, error) {
	ib := b.getImageBuilder(workflowName, imageTag, workflowDefinition)
	ib.WithTimeout(5 * time.Minute)
	return b.BuildImage(ib.Build())
}

func (b *Builder) ScheduleNewBuildWithContainerFile(workflowName string, imageTag string, workflowDefinition []byte) (*api.Build, error) {
	ib := b.getImageBuilder(workflowName, imageTag, workflowDefinition)
	ib.WithTimeout(5 * time.Minute)
	return b.BuildImage(ib.Build())
}

func (b *Builder) ScheduleNewKanikoBuildWithContainerFile(workflowName string, imageTag string, workflowDefinition []byte, build api.BuildSpec) (*api.Build, error) {
	task := findKanikoTask(build.Tasks)
	ib := b.getImageBuilderForKaniko(workflowName, imageTag, workflowDefinition, task)
	ib.WithTimeout(5 * time.Minute)
	return b.BuildImage(ib.Build())

}

func findKanikoTask(tasks []api.Task) *api.KanikoTask {
	if tasks != nil && len(tasks) > 0 {
		for _, task := range tasks {
			if task.Kaniko != nil {
				return task.Kaniko
			}
		}
	}
	// if we don't find a defined KanikoTask into the CR, we create a new one and use it with the standard values
	return &api.KanikoTask{}
}

func (b *Builder) ReconcileBuild(build *api.Build, cli client.Client) (*api.Build, error) {
	result, err := builder.FromBuild(build).WithClient(cli).Reconcile()
	return result, err
}

func (b *Builder) BuildImage(kb KogitoBuilder) (*api.Build, error) {
	log := ctrllog.FromContext(b.ctx)
	cli, err := client.NewClient(true)
	platform := api.PlatformBuild{
		ObjectReference: api.ObjectReference{
			Namespace: kb.Namespace,
			Name:      kb.PodMiddleName,
		},
		Spec: api.PlatformBuildSpec{
			BuildStrategy:   api.BuildStrategyPod,
			PublishStrategy: api.PlatformBuildPublishStrategyKaniko,
			Registry: api.RegistrySpec{
				Insecure: kb.InsecureRegistry,
				Address:  kb.RegistryAddress,
				Secret:   kb.Secret,
			},
			Timeout: &metav1.Duration{
				Duration: kb.Timeout,
			},
		},
	}

	build, err := newBuild(kb, platform, b, cli)
	if err != nil {
		log.Error(err, err.Error())
		return nil, err
	}
	return build, err
}

func (b *Builder) ScheduleBuild(kb KogitoBuilder) (*api.Build, error) {
	log := ctrllog.FromContext(b.ctx)
	cli, err := client.NewClient(true)
	platform := api.PlatformBuild{
		ObjectReference: api.ObjectReference{
			Namespace: kb.Namespace,
			Name:      kb.PodMiddleName,
		},
		Spec: api.PlatformBuildSpec{
			BuildStrategy:   api.BuildStrategyPod,
			PublishStrategy: api.PlatformBuildPublishStrategyKaniko,
			Registry: api.RegistrySpec{
				Insecure: kb.InsecureRegistry,
				Address:  kb.RegistryAddress,
				Secret:   kb.Secret,
			},
			Timeout: &metav1.Duration{
				Duration: kb.Timeout,
			},
		},
	}

	build, err := newBuild(kb, platform, b, cli)
	if err != nil {
		log.Error(err, err.Error())
		return nil, err
	}
	return build, err
}

// Helper function to create a new container-builder Build and schedule it
func newBuild(kb KogitoBuilder, platform api.PlatformBuild, b *Builder, cli client.Client) (*api.Build, error) {
	buildInfo := builder.BuilderInfo{FinalImageName: kb.ImageName, BuildUniqueName: kb.PodMiddleName, Platform: platform}

	return builder.NewBuild(buildInfo).
		WithResource(resourceDockerfile, kb.ContainerFile).
		WithResource(kb.WorkflowID+b.commonConfig.Data[configKeyDefaultExtension], kb.WorkflowDefinition).
		WithClient(cli).
		Schedule()
}

// Fluent API section

type KogitoBuilder struct {
	WorkflowID           string
	WorkflowDefinition   []byte
	ContainerFile        []byte
	Namespace            string
	InsecureRegistry     bool
	Timeout              time.Duration
	ImageName            string
	PodMiddleName        string
	RegistryAddress      string
	RegistryOrganization string
	Secret               string
	Cache                api.KanikoTaskCache
	Resources            corev1.ResourceRequirements
	AdditionalFlags      []string
}

type ImageBuilder struct {
	KogitoBuilder *KogitoBuilder
}

func NewImageBuilder(sourceSwfName string, sourceSwf []byte, containerFile []byte) ImageBuilder {
	return ImageBuilder{KogitoBuilder: &KogitoBuilder{WorkflowID: sourceSwfName, WorkflowDefinition: sourceSwf, ContainerFile: containerFile}}
}

func (ib *ImageBuilder) OnNamespace(namespace string) *ImageBuilder {
	ib.KogitoBuilder.Namespace = namespace
	return ib
}

func (ib *ImageBuilder) WithTimeout(timeout time.Duration) *ImageBuilder {
	ib.KogitoBuilder.Timeout = timeout
	return ib
}

func (ib *ImageBuilder) WithSecret(secret string) *ImageBuilder {
	ib.KogitoBuilder.Secret = secret
	return ib
}

func (ib *ImageBuilder) WithRegistryAddress(registryAddress string) *ImageBuilder {
	ib.KogitoBuilder.RegistryAddress = registryAddress
	return ib
}

func (ib *ImageBuilder) WithInsecureRegistry(insecureRegistry bool) *ImageBuilder {
	ib.KogitoBuilder.InsecureRegistry = insecureRegistry
	return ib
}

func (ib *ImageBuilder) WithPodMiddleName(buildName string) *ImageBuilder {
	ib.KogitoBuilder.PodMiddleName = buildName
	return ib
}

func (ib *ImageBuilder) WithImageName(imageName string) *ImageBuilder {
	ib.KogitoBuilder.ImageName = imageName
	return ib
}

func (ib *ImageBuilder) WithCache(cache api.KanikoTaskCache) *ImageBuilder {
	ib.KogitoBuilder.Cache = cache
	return ib
}
func (ib *ImageBuilder) WithResources(resources corev1.ResourceRequirements) *ImageBuilder {
	ib.KogitoBuilder.Resources = resources
	return ib
}

func (ib *ImageBuilder) WithAdditionalFlags(flags []string) *ImageBuilder {
	ib.KogitoBuilder.AdditionalFlags = flags
	return ib
}

func (ib *ImageBuilder) Build() KogitoBuilder {
	return *ib.KogitoBuilder
}
