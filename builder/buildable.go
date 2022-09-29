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
	apiv08 "github.com/davidesalerno/kogito-serverless-operator/api/v08"
	"github.com/ricardozanini/kogito-builder/util/log"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

type Buildable struct {
	Client client.Client
	Ctx    context.Context
}

func NewBuildable(client client.Client,
	ctx context.Context) Buildable {
	return Buildable{
		Ctx:    ctx,
		Client: client,
	}
}

func (buildable *Buildable) GetWorkflowBuild(req ctrl.Request, workflowID string) (apiv08.KogitoServerlessBuild, error) {
	buildInstance := &apiv08.KogitoServerlessBuild{}
	buildInstance.Spec.WorkflowId = workflowID
	error := buildable.Client.Get(buildable.Ctx, req.NamespacedName, buildInstance)
	return *buildInstance, error
}

func (buildable *Buildable) CreateWorkflowBuild(workflowID string, targetNamespace string) (apiv08.KogitoServerlessBuild, error) {
	buildInstance := &apiv08.KogitoServerlessBuild{}
	buildInstance.Spec.WorkflowId = workflowID
	buildInstance.ObjectMeta.Namespace = targetNamespace
	buildInstance.ObjectMeta.Name = workflowID
	error := buildable.Client.Create(buildable.Ctx, buildInstance)
	return *buildInstance, error
}

func (buildable *Buildable) HandleWorkflowBuild(workflowID string, req ctrl.Request) (apiv08.KogitoServerlessBuild, error) {
	buildInstance, error := buildable.GetWorkflowBuild(req, workflowID)
	if error != nil {
		if k8serrors.IsNotFound(error) {
			return buildable.CreateWorkflowBuild(workflowID, req.Namespace)
		}
		// Error reading the object - requeue the request.
		log.Error(error, "Failed to get KogitoServerlessBuild")
		return buildInstance, error

	} else {
		return buildInstance, nil
	}
}
