// Copyright 2024 Apache Software Foundation (ASF)
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

package gitops

import (
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/discovery"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

var _ profiles.ProfileReconciler = &gitOpsProfile{}

type gitOpsProfile struct {
	common.Reconciler
}

// NewProfileForOpsReconciler creates an alternative prod profile that won't require to build the workflow image in order to deploy
// the workflow application. It assumes that the image has been built somewhere else.
func NewProfileForOpsReconciler(client client.Client, cfg *rest.Config, recorder record.EventRecorder) profiles.ProfileReconciler {
	support := &common.StateSupport{
		C:        client,
		Cfg:      cfg,
		Catalog:  discovery.NewServiceCatalogForConfig(client, cfg),
		Recorder: recorder,
	}
	// the reconciliation state machine
	stateMachine := common.NewReconciliationStateMachine(
		&ensureBuildSkipped{StateSupport: support},
		&followDeployWorkflowState{StateSupport: support, ensurers: newObjectEnsurers(support)},
	)
	reconciler := &gitOpsProfile{
		Reconciler: common.NewReconciler(support, stateMachine),
	}

	return reconciler
}

func (p gitOpsProfile) GetProfile() metadata.ProfileType {
	return metadata.GitOpsProfile
}
