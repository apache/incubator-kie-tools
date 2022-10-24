/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package builder

import (
	"context"
	"time"

	"github.com/kiegroup/container-builder/api"
	"github.com/kiegroup/container-builder/builder"
	"github.com/kiegroup/container-builder/client"
	"github.com/kiegroup/kogito-serverless-operator/constants"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"
)

type Builder struct {
	ctx context.Context
	cm  corev1.ConfigMap
}

func NewBuilder(contex context.Context, cm corev1.ConfigMap) Builder {
	return Builder{ctx: contex, cm: cm}
}

func (b *Builder) getImageBuilder(workflowID string, workflowDefinition []byte) ImageBuilder {
	containerFile := b.cm.Data[b.cm.Data[constants.DEFAULT_BUILDER_RESOURCE_NAME_KEY]]
	ib := NewImageBuilder(workflowID, workflowDefinition, []byte(containerFile))
	ib.OnNamespace(constants.BUILDER_NAMESPACE_DEFAULT)
	ib.WithPodMiddleName(constants.BUILDER_IMG_NAME_DEFAULT)
	ib.WithInsecureRegistry(false)
	ib.WithImageName(workflowID + constants.DEFAULT_IMAGES_TAG)
	ib.WithSecret(b.cm.Data[constants.DEFAULT_KANIKO_SECRET_KEY])
	ib.WithRegistryAddress(b.cm.Data[constants.DEFAULT_REGISTRY_REPO_KEY])
	return ib
}

func (b *Builder) ScheduleNewBuildWithTimeout(workflowName string, workflowDefinition []byte, timeout time.Duration) (*api.Build, error) {
	ib := b.getImageBuilder(workflowName, workflowDefinition)
	ib.WithTimeout(timeout)
	return b.BuildImage(ib.Build())
}

func (b *Builder) ScheduleNewBuild(workflowName string, workflowDefinition []byte) (*api.Build, error) {
	ib := b.getImageBuilder(workflowName, workflowDefinition)
	ib.WithTimeout(5 * time.Minute)
	return b.BuildImage(ib.Build())
}

func (b *Builder) ScheduleNewBuildWithContainerFile(workflowName string, workflowDefinition []byte) (*api.Build, error) {
	ib := b.getImageBuilder(workflowName, workflowDefinition)
	ib.WithTimeout(5 * time.Minute)
	return b.BuildImage(ib.Build())
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

	build, err := builder.NewBuild(platform, kb.ImageName, kb.PodMiddleName).
		WithResource(constants.BUILDER_RESOURCE_NAME_DEFAULT, kb.ContainerFile).
		WithResource(kb.WorkflowID+b.cm.Data[constants.DEFAULT_WORKFLOW_EXTENSION_KEY], kb.WorkflowDefinition).
		WithClient(cli).
		Schedule()
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

	build, err := builder.NewBuild(platform, kb.ImageName, kb.PodMiddleName).
		WithResource(constants.BUILDER_RESOURCE_NAME_DEFAULT, kb.ContainerFile).
		WithResource(kb.WorkflowID+b.cm.Data[constants.DEFAULT_WORKFLOW_EXTENSION_KEY], kb.WorkflowDefinition).
		WithClient(cli).
		Schedule()
	if err != nil {
		log.Error(err, err.Error())
		return nil, err
	}
	return build, err
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

func (ib *ImageBuilder) Build() KogitoBuilder {
	return *ib.KogitoBuilder
}
