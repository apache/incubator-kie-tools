// Copyright 2023 Apache Software Foundation (ASF)
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

package workflows

import (
	"context"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

var _ WorkflowManager = &workflowManager{}

// WorkflowManager offers a management interface for operations with SonataFlows instances outside the controller's package.
// Meant to be used by other packages that don't have access to a SonataFlow instance coming from a reconciliation cycle.
type WorkflowManager interface {
	SetBuiltStatusToRunning(message string) error
	GetWorkflow() *v1alpha08.SonataFlow
}

type workflowManager struct {
	workflow *v1alpha08.SonataFlow
	client   client.Client
	ctx      context.Context
}

func (w *workflowManager) GetWorkflow() *v1alpha08.SonataFlow {
	return w.workflow
}

func (w *workflowManager) SetBuiltStatusToRunning(message string) error {
	w.workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, message)
	return w.client.Status().Update(w.ctx, w.workflow)
}

func NewManager(client client.Client, ctx context.Context, ns, name string) (WorkflowManager, error) {
	workflow := &v1alpha08.SonataFlow{}
	if err := client.Get(ctx, types.NamespacedName{Name: name, Namespace: ns}, workflow); err != nil {
		return nil, err
	}
	return &workflowManager{
		workflow: workflow,
		client:   client,
		ctx:      ctx,
	}, nil
}
