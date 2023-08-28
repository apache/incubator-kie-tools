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
	"time"

	"k8s.io/klog/v2"

	"github.com/kiegroup/kogito-serverless-operator/controllers/workflowdef"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/rest"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	clientr "github.com/kiegroup/kogito-serverless-operator/container-builder/client"
	"github.com/kiegroup/kogito-serverless-operator/controllers/platform"
	"github.com/kiegroup/kogito-serverless-operator/utils"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
	builder "github.com/kiegroup/kogito-serverless-operator/container-builder/builder/kubernetes"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/client"
	"github.com/kiegroup/kogito-serverless-operator/log"
)

const (
	resourceDockerfile = "Dockerfile"
)

var _ BuildManager = &containerBuilderManager{}

type containerBuilderManager struct {
	buildManagerContext
	// needed for the internal container-builder
	restConfig *rest.Config
}

func (c *containerBuilderManager) Schedule(build *operatorapi.SonataFlowBuild) error {
	kanikoTaskCache := api.KanikoTaskCache{}
	if platform.IsKanikoCacheEnabled(c.platform) {
		kanikoTaskCache.Enabled = utils.Pbool(true)
	}
	kanikoTask := &api.KanikoTask{
		ContainerBuildBaseTask: api.ContainerBuildBaseTask{Name: "kaniko"},
		PublishTask:            api.PublishTask{},
		Cache:                  kanikoTaskCache,
		Resources:              build.Spec.Resources,
		AdditionalFlags:        build.Spec.Arguments,
	}
	var containerBuilder *api.ContainerBuild
	var err error
	if containerBuilder, err = c.scheduleNewKanikoBuildWithContainerFile(build, kanikoTask); err != nil {
		return err
	}
	if containerBuilder == nil {
		return nil
	}
	if err = build.Status.SetInnerBuild(containerBuilder); err != nil {
		return err
	}
	build.Status.BuildPhase = operatorapi.BuildPhase(containerBuilder.Status.Phase)
	build.Status.Error = containerBuilder.Status.Error
	return nil
}

func (c *containerBuilderManager) Reconcile(build *operatorapi.SonataFlowBuild) error {
	containerBuild := &api.ContainerBuild{}
	if err := build.Status.GetInnerBuild(containerBuild); err != nil {
		return err
	}
	containerCli, _ := clientr.FromCtrlClientSchemeAndConfig(c.client, c.client.Scheme(), c.restConfig)
	containerBuild, err := c.reconcileBuild(containerBuild, containerCli)
	if err != nil {
		return err
	}
	build.Status.BuildPhase = operatorapi.BuildPhase(containerBuild.Status.Phase)
	build.Status.Error = containerBuild.Status.Error
	build.Status.ImageTag = containerBuild.Status.RepositoryImageTag
	if err = build.Status.SetInnerBuild(containerBuild); err != nil {
		return err
	}
	return nil
}

func newContainerBuilderManager(managerContext buildManagerContext, config *rest.Config) BuildManager {
	return &containerBuilderManager{
		buildManagerContext: managerContext,
		restConfig:          config,
	}
}

func (c *containerBuilderManager) getImageBuilderForKaniko(workflow *operatorapi.SonataFlow, imageNameTag string, workflowDefinition []byte, task *api.KanikoTask) imageBuilder {
	containerFile := platform.GetCustomizedDockerfile(c.commonConfig.Data[c.commonConfig.Data[configKeyDefaultBuilderResourceName]], *c.platform)
	ib := newImageBuilder(workflow, workflowDefinition, []byte(containerFile))
	ib.OnNamespace(c.platform.Namespace)
	ib.WithPodMiddleName(workflow.Name)
	ib.WithInsecureRegistry(false)
	ib.WithImageNameTag(imageNameTag)
	ib.WithSecret(c.platform.Spec.Build.Config.Registry.Secret)
	ib.WithRegistryAddress(c.platform.Spec.Build.Config.Registry.Address)
	ib.WithCache(task.Cache)
	ib.WithResourcesReqs(task.Resources)
	ib.WithAdditionalFlags(task.AdditionalFlags)
	return ib
}

func (c *containerBuilderManager) scheduleNewKanikoBuildWithContainerFile(build *operatorapi.SonataFlowBuild, task *api.KanikoTask) (*api.ContainerBuild, error) {
	workflow, err := c.fetchWorkflowForBuild(build)
	if err != nil {
		return nil, err
	}
	workflowDef, err := workflowdef.GetJSONWorkflow(workflow, c.ctx)
	if err != nil {
		return nil, err
	}
	imageTag := workflowdef.GetWorkflowAppImageNameTag(workflow)
	ib := c.getImageBuilderForKaniko(workflow, imageTag, workflowDef, task)
	ib.WithTimeout(5 * time.Minute)
	return c.buildImage(ib.Build())
}

func (c *containerBuilderManager) reconcileBuild(build *api.ContainerBuild, cli client.Client) (*api.ContainerBuild, error) {
	result, err := builder.FromBuild(build).WithClient(cli).Reconcile()
	return result, err
}

func (c *containerBuilderManager) buildImage(kb internalBuilder) (*api.ContainerBuild, error) {
	cli, err := client.FromCtrlClientSchemeAndConfig(c.client, c.client.Scheme(), c.restConfig)
	plat := api.PlatformContainerBuild{
		ObjectReference: api.ObjectReference{
			Namespace: kb.Namespace,
			Name:      kb.PodMiddleName,
		},
		Spec: api.PlatformContainerBuildSpec{
			BuildStrategy:   api.ContainerBuildStrategyPod,
			PublishStrategy: api.PlatformBuildPublishStrategyKaniko,
			Registry: api.ContainerRegistrySpec{
				Insecure: kb.InsecureRegistry,
				Address:  kb.RegistryAddress,
				Secret:   kb.Secret,
			},
			Timeout: &metav1.Duration{
				Duration: kb.Timeout,
			},
		},
	}

	build, err := newBuild(kb, plat, c.commonConfig.Data[configKeyDefaultExtension], cli)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during build Image")
		return nil, err
	}
	return build, err
}

// Helper function to create a new container-builder build and schedule it
func newBuild(kb internalBuilder, platform api.PlatformContainerBuild, defaultExtension string, cli client.Client) (*api.ContainerBuild, error) {
	buildInfo := builder.ContainerBuilderInfo{FinalImageName: kb.ImageName, BuildUniqueName: kb.PodMiddleName, Platform: platform}

	scheduler := builder.NewBuild(buildInfo).
		WithResource(resourceDockerfile, kb.ContainerFile).
		WithResource(kb.Workflow.Name+defaultExtension, kb.WorkflowDefinition).
		WithAdditionalArgs(kb.AdditionalFlags).
		WithResourceRequirements(kb.ResourceReqs).
		WithClient(cli)

	for _, res := range kb.Workflow.Spec.Resources.ConfigMaps {
		scheduler.WithConfigMapResource(res.ConfigMap, res.WorkflowPath)
	}

	return scheduler.Schedule()
}

// Fluent API section

type internalBuilder struct {
	Workflow             *operatorapi.SonataFlow
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
	ResourceReqs         corev1.ResourceRequirements
	AdditionalFlags      []string
}

type imageBuilder struct {
	builder *internalBuilder
}

func newImageBuilder(workflow *operatorapi.SonataFlow, sourceSwf []byte, containerFile []byte) imageBuilder {
	return imageBuilder{builder: &internalBuilder{
		Workflow:           workflow,
		WorkflowDefinition: sourceSwf,
		ContainerFile:      containerFile,
	}}
}

func (ib *imageBuilder) OnNamespace(namespace string) *imageBuilder {
	ib.builder.Namespace = namespace
	return ib
}

func (ib *imageBuilder) WithTimeout(timeout time.Duration) *imageBuilder {
	ib.builder.Timeout = timeout
	return ib
}

func (ib *imageBuilder) WithSecret(secret string) *imageBuilder {
	ib.builder.Secret = secret
	return ib
}

func (ib *imageBuilder) WithRegistryAddress(registryAddress string) *imageBuilder {
	ib.builder.RegistryAddress = registryAddress
	return ib
}

func (ib *imageBuilder) WithInsecureRegistry(insecureRegistry bool) *imageBuilder {
	ib.builder.InsecureRegistry = insecureRegistry
	return ib
}

func (ib *imageBuilder) WithPodMiddleName(buildName string) *imageBuilder {
	ib.builder.PodMiddleName = buildName
	return ib
}

func (ib *imageBuilder) WithImageNameTag(imageName string) *imageBuilder {
	ib.builder.ImageName = imageName
	return ib
}

func (ib *imageBuilder) WithCache(cache api.KanikoTaskCache) *imageBuilder {
	ib.builder.Cache = cache
	return ib
}
func (ib *imageBuilder) WithResourcesReqs(resources corev1.ResourceRequirements) *imageBuilder {
	ib.builder.ResourceReqs = resources
	return ib
}

func (ib *imageBuilder) WithAdditionalFlags(flags []string) *imageBuilder {
	ib.builder.AdditionalFlags = flags
	return ib
}

func (ib *imageBuilder) Build() internalBuilder {
	return *ib.builder
}
