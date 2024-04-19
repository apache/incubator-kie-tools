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

package builder

import (
	"time"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/cfg"
	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/workflowdef"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/rest"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	clientr "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/platform"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
	builder "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/builder/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
)

const (
	resourceDockerfile = "Dockerfile"
)

var _ BuildManager = &containerBuilderManager{}

type kanikoBuildInput struct {
	name               string
	task               *api.KanikoTask
	workflowDefinition []byte
	workflow           *operatorapi.SonataFlow
	dockerfile         string
	imageTag           string
}

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
		ContainerBuildBaseTask: api.ContainerBuildBaseTask{
			Name:      "kaniko",
			BuildArgs: build.Spec.BuildArgs,
			Envs:      build.Spec.Envs,
			Resources: build.Spec.Resources,
		},
		PublishTask:         api.PublishTask{},
		Cache:               kanikoTaskCache,
		AdditionalFlags:     build.Spec.Arguments,
		KanikoExecutorImage: cfg.GetCfg().KanikoExecutorImageTag,
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
	if len(build.Status.BuildPhase) == 0 {
		build.Status.BuildPhase = operatorapi.BuildPhaseInitialization
	}
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

func (c *containerBuilderManager) scheduleNewKanikoBuildWithContainerFile(build *operatorapi.SonataFlowBuild, task *api.KanikoTask) (*api.ContainerBuild, error) {
	workflow, err := c.fetchWorkflowForBuild(build)
	if err != nil {
		return nil, err
	}
	workflowDef, err := workflowdef.GetJSONWorkflow(workflow, c.ctx)
	if err != nil {
		return nil, err
	}

	buildInput := kanikoBuildInput{
		name:               workflow.Name,
		task:               task,
		workflowDefinition: workflowDef,
		workflow:           workflow,
		dockerfile:         platform.GetCustomizedBuilderDockerfile(c.builderConfigMap.Data[defaultBuilderResourceName], *c.platform),
		imageTag:           buildNamespacedImageTag(workflow),
	}

	if c.platform.Spec.Build.Config.Timeout == nil {
		c.platform.Spec.Build.Config.Timeout = &metav1.Duration{Duration: 5 * time.Minute}
	}
	return c.buildImage(buildInput)
}

func (c *containerBuilderManager) reconcileBuild(build *api.ContainerBuild, cli client.Client) (*api.ContainerBuild, error) {
	result, err := builder.FromBuild(build).WithClient(cli).Reconcile()
	return result, err
}

func (c *containerBuilderManager) buildImage(buildInput kanikoBuildInput) (*api.ContainerBuild, error) {
	cli, err := client.FromCtrlClientSchemeAndConfig(c.client, c.client.Scheme(), c.restConfig)
	plat := api.PlatformContainerBuild{
		ObjectReference: api.ObjectReference{
			Namespace: c.platform.Namespace,
			Name:      buildInput.name,
		},
		Spec: api.PlatformContainerBuildSpec{
			BuildStrategy:   api.ContainerBuildStrategyPod,
			PublishStrategy: api.PlatformBuildPublishStrategyKaniko,
			Registry: api.ContainerRegistrySpec{
				Insecure: c.platform.Spec.Build.Config.Registry.Insecure,
				Address:  c.platform.Spec.Build.Config.Registry.Address,
				Secret:   c.platform.Spec.Build.Config.Registry.Secret,
			},
			Timeout: &metav1.Duration{
				Duration: c.platform.Spec.Build.Config.Timeout.Duration,
			},
		},
	}

	build, err := newBuild(buildInput, plat, c.builderConfigMap.Data[configKeyDefaultExtension], cli)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during build Image")
		return nil, err
	}
	return build, err
}

// Helper function to create a new container-builder build and schedule it
func newBuild(buildInput kanikoBuildInput, platform api.PlatformContainerBuild, defaultExtension string, cli client.Client) (*api.ContainerBuild, error) {
	buildInfo := builder.ContainerBuilderInfo{
		FinalImageName:           buildInput.imageTag,
		BuildUniqueName:          buildInput.name,
		Platform:                 platform,
		ContainerBuilderImageTag: buildInput.task.KanikoExecutorImage,
	}

	newBuilder := builder.NewBuild(buildInfo).
		WithClient(cli).
		AddResource(resourceDockerfile, []byte(buildInput.dockerfile)).
		AddResource(buildInput.name+defaultExtension, buildInput.workflowDefinition)
	for _, res := range buildInput.workflow.Spec.Resources.ConfigMaps {
		newBuilder.AddConfigMapResource(res.ConfigMap, res.WorkflowPath)
	}

	return newBuilder.Scheduler().
		WithAdditionalArgs(buildInput.task.AdditionalFlags).
		WithResourceRequirements(buildInput.task.Resources).
		WithBuildArgs(buildInput.task.BuildArgs).
		WithEnvs(buildInput.task.Envs).Schedule()
}

// buildNamespacedImageTag For the kaniko build we prepend the namespace to the calculated image name/tag to avoid potential
// collisions if the same workflows is deployed in a different namespace. In OpenShift this last is not needed since the
// ImageStreams are already namespaced.
func buildNamespacedImageTag(workflow *operatorapi.SonataFlow) string {
	return workflow.Namespace + "/" + workflowdef.GetWorkflowAppImageNameTag(workflow)
}
