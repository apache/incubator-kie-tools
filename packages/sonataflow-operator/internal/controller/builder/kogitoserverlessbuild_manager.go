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
	"context"
	"strings"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"

	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/persistence"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
)

const QuarkusExtensionsBuildArg = "QUARKUS_EXTENSIONS"

var _ SonataFlowBuildManager = &sonataFlowBuildManager{}

type sonataFlowBuildManager struct {
	client client.Client
	ctx    context.Context
}

func (k *sonataFlowBuildManager) MarkToRestart(build *operatorapi.SonataFlowBuild) error {
	build.Status.BuildPhase = operatorapi.BuildPhaseNone
	return k.client.Status().Update(k.ctx, build)
}

func (k *sonataFlowBuildManager) GetOrCreateBuild(workflow *operatorapi.SonataFlow) (*operatorapi.SonataFlowBuild, error) {
	buildInstance := &operatorapi.SonataFlowBuild{}
	buildInstance.ObjectMeta.Namespace = workflow.Namespace
	buildInstance.ObjectMeta.Name = workflow.Name

	if err := k.client.Get(k.ctx, client.ObjectKeyFromObject(workflow), buildInstance); err != nil {
		if errors.IsNotFound(err) {
			plat := &operatorapi.SonataFlowPlatform{}
			if plat, err = platform.GetActivePlatform(k.ctx, k.client, workflow.Namespace, true); err != nil {
				return nil, err
			}
			workflowBuildTemplate := plat.Spec.Build.Template.DeepCopy()
			if persistence.UsesPostgreSQLPersistence(workflow, plat) {
				addPersistenceExtensions(workflowBuildTemplate)
			}
			buildInstance.Spec.BuildTemplate = *workflowBuildTemplate
			if err = controllerutil.SetControllerReference(workflow, buildInstance, k.client.Scheme()); err != nil {
				return nil, err
			}
			if err = k.client.Create(k.ctx, buildInstance); err != nil {
				return nil, err
			}
			return buildInstance, nil
		}
		return nil, err
	}

	return buildInstance, nil
}

type SonataFlowBuildManager interface {
	// GetOrCreateBuild gets or creates a new instance of SonataFlowBuild for the given SonataFlow.
	//
	// Only one build is allowed per workflow instance.
	GetOrCreateBuild(workflow *operatorapi.SonataFlow) (*operatorapi.SonataFlowBuild, error)
	// MarkToRestart tell the controller to restart this build in the next iteration
	MarkToRestart(build *operatorapi.SonataFlowBuild) error
}

// NewSonataFlowBuildManager entry point to manage SonataFlowBuild instances.
// Won't start a build, but once it creates a new instance, the controller will take place and start the build in the cluster context.
func NewSonataFlowBuildManager(ctx context.Context, client client.Client) SonataFlowBuildManager {
	return &sonataFlowBuildManager{
		client: client,
		ctx:    ctx,
	}
}

// addPersistenceExtensions Adds the persistence related extensions to the current BuildTemplate if none of them is
// already provided. If any of them is detected, its assumed that users might already have provided them in the
// SonataFlowPlatform, so we just let the provided configuration.
func addPersistenceExtensions(template *operatorapi.BuildTemplate) {
	quarkusExtensions := getBuildArg(template.BuildArgs, QuarkusExtensionsBuildArg)
	if quarkusExtensions == nil {
		template.BuildArgs = append(template.BuildArgs, v1.EnvVar{Name: QuarkusExtensionsBuildArg})
		quarkusExtensions = &template.BuildArgs[len(template.BuildArgs)-1]
	}
	if !hasAnyExtensionPresent(quarkusExtensions, persistence.GetPostgreSQLExtensions()) {
		for _, extension := range persistence.GetPostgreSQLExtensions() {
			if len(quarkusExtensions.Value) > 0 {
				quarkusExtensions.Value = quarkusExtensions.Value + ","
			}
			quarkusExtensions.Value = quarkusExtensions.Value + extension.String()
		}
	}
}

func getBuildArg(buildArgs []v1.EnvVar, name string) *v1.EnvVar {
	for i := 0; i < len(buildArgs); i++ {
		if buildArgs[i].Name == name {
			return &buildArgs[i]
		}
	}
	return nil
}

func hasAnyExtensionPresent(buildArg *v1.EnvVar, extensions []cfg.GroupArtifactId) bool {
	for _, extension := range extensions {
		if isExtensionPresent(buildArg, extension) {
			return true
		}
	}
	return false
}

func isExtensionPresent(buildArg *v1.EnvVar, extension cfg.GroupArtifactId) bool {
	return strings.Contains(buildArg.Value, extension.String())
}
