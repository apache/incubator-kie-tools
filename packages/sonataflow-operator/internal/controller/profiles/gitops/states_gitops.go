// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package gitops

import (
	"context"
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"

	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common"
)

type ensureBuildSkipped struct {
	*common.StateSupport
}

func (f *ensureBuildSkipped) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.GetCondition(api.BuiltConditionType).IsUnknown() ||
		workflow.Status.GetCondition(api.BuiltConditionType).IsTrue() ||
		workflow.Status.GetCondition(api.BuiltConditionType).Reason != api.BuildSkippedReason
}

func (f *ensureBuildSkipped) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	// We skip the build, so let's ensure the status reflect that
	workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildSkippedReason, "")
	if _, err := f.PerformStatusUpdate(ctx, workflow); err != nil {
		return ctrl.Result{Requeue: false}, nil, err
	}

	return ctrl.Result{Requeue: true}, nil, nil
}

func (f *ensureBuildSkipped) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	//By default, we don't want to perform anything after the reconciliation, and so we will simply return no error
	return nil
}

type followDeployWorkflowState struct {
	*common.StateSupport
	ensurers *objectEnsurers
}

func (f *followDeployWorkflowState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	// we always reconcile since in this flow we don't mind building anything, just reconcile the deployment state
	return workflow.Status.GetCondition(api.BuiltConditionType).Reason == api.BuildSkippedReason
}

func (f *followDeployWorkflowState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	return newDeploymentReconciler(f.StateSupport, f.ensurers).Reconcile(ctx, workflow)
}

func (f *followDeployWorkflowState) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	// Clean up the outdated Knative revisions, if any
	if err := knative.CleanupOutdatedRevisions(ctx, f.Cfg, workflow); err != nil {
		return fmt.Errorf("failied to cleanup workflow outdated revisions, workflow: %s, namespace: %s - %v", workflow.Name, workflow.Namespace, err)
	}
	return nil
}
