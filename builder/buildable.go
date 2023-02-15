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

	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
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

// GetWorkflowBuild gets the required Build associated with the same name/namespaced as defined in the request, nil if not found
func (buildable *Buildable) GetWorkflowBuild(name string, namespace string) (*operatorapi.KogitoServerlessBuild, error) {
	buildInstance := &operatorapi.KogitoServerlessBuild{}
	err := buildable.Client.Get(buildable.Ctx, types.NamespacedName{Namespace: namespace, Name: name}, buildInstance)
	if err != nil && k8serrors.IsNotFound(err) {
		return nil, nil
	}
	return buildInstance, err
}

func (buildable *Buildable) getWorkflowBuild(req ctrl.Request, workflowID string) (*operatorapi.KogitoServerlessBuild, error) {
	buildInstance := &operatorapi.KogitoServerlessBuild{}
	buildInstance.Spec.WorkflowId = workflowID
	err := buildable.Client.Get(buildable.Ctx, req.NamespacedName, buildInstance)
	return buildInstance, err
}

func (buildable *Buildable) CreateWorkflowBuild(workflowName string, targetNamespace string) (*operatorapi.KogitoServerlessBuild, error) {
	buildInstance := &operatorapi.KogitoServerlessBuild{}
	buildInstance.Spec.WorkflowId = workflowName
	buildInstance.ObjectMeta.Namespace = targetNamespace
	buildInstance.ObjectMeta.Name = workflowName
	err := buildable.Client.Create(buildable.Ctx, buildInstance)
	return buildInstance, err
}
