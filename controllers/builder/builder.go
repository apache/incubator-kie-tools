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

package builder

import (
	"context"
	"fmt"

	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/client-go/rest"
	"sigs.k8s.io/controller-runtime/pkg/client"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"

	"github.com/kiegroup/kogito-serverless-operator/controllers/workflowdef"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/controllers/platform"
)

type buildManagerContext struct {
	ctx          context.Context
	client       client.Client
	platform     *operatorapi.KogitoServerlessPlatform
	commonConfig *v1.ConfigMap
}

type BuildManager interface {
	Schedule(build *operatorapi.KogitoServerlessBuild) error
	Reconcile(build *operatorapi.KogitoServerlessBuild) error
}

func NewBuildManager(ctx context.Context, client client.Client, cliConfig *rest.Config, targetName, targetNamespace string) (BuildManager, error) {
	logger := ctrllog.FromContext(ctx)
	p, err := platform.GetActivePlatform(ctx, client, targetNamespace)
	if err != nil {
		if errors.IsNotFound(err) {
			return nil, err
		}
		logger.Error(err, fmt.Sprintf("Error retrieving the active platform. Workflow %s build cannot be performed!", targetName))
		return nil, err
	}
	commonConfig, err := GetCommonConfigMap(client, targetNamespace)
	if err != nil {
		logger.Error(err, "Failed to get common configMap for Workflow Builder. Make sure that kogito-serverless-operator-builder-config is present in the operator namespace.")
		return nil, err
	}
	managerContext := buildManagerContext{
		ctx:          ctx,
		client:       client,
		platform:     p,
		commonConfig: commonConfig,
	}
	switch p.Status.Cluster {
	case operatorapi.PlatformClusterOpenShift:
		return newOpenShiftBuilderManager(managerContext, cliConfig)
	case operatorapi.PlatformClusterKubernetes:
		return newContainerBuilderManager(managerContext, cliConfig), nil
	default:
		logger.Info("Impossible to check the Cluster type in the KogitoServerlessPlatform")
		return newContainerBuilderManager(managerContext, cliConfig), nil
	}
}

// fetchWorkflowDefinitionAndImageTag fetches the workflow instance by name and namespace and convert it to JSON bytes.
func (b *buildManagerContext) fetchWorkflowDefinitionAndImageTag(build *operatorapi.KogitoServerlessBuild) (workflowDef []byte, imageTag string, err error) {
	instance, err := b.fetchWorkflowForBuild(build)
	if err != nil {
		return nil, "", err
	}
	if workflowDef, err = workflowdef.GetJSONWorkflow(instance, b.ctx); err != nil {
		return nil, "", err
	}
	imageTag = workflowdef.GetWorkflowAppImageNameTag(instance)
	return
}

// fetchWorkflowForBuild fetches the k8s API for the workflow from the given build
func (b *buildManagerContext) fetchWorkflowForBuild(build *operatorapi.KogitoServerlessBuild) (workflow *operatorapi.KogitoServerlessWorkflow, err error) {
	workflow = &operatorapi.KogitoServerlessWorkflow{}
	if err = b.client.Get(b.ctx, client.ObjectKeyFromObject(build), workflow); err != nil {
		return nil, err
	}
	return
}
