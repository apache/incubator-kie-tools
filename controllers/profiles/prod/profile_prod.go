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

package prod

import (
	"time"

	"k8s.io/client-go/rest"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/discovery"
	"k8s.io/client-go/tools/record"

	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/metadata"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common"
)

var _ profiles.ProfileReconciler = &prodProfile{}

type prodProfile struct {
	common.Reconciler
}

const (
	requeueAfterStartingBuild   = 3 * time.Minute
	requeueWhileWaitForBuild    = 1 * time.Minute
	requeueWhileWaitForPlatform = 5 * time.Second

	quarkusProdConfigMountPath = "/deployments/config"
)

// objectEnsurers is a struct for the objects that ReconciliationState needs to create in the platform for the Production profile.
// ReconciliationState that needs access to it must include this struct as an attribute and initialize it in the profile builder.
// Use newObjectEnsurers to facilitate building this struct
type objectEnsurers struct {
	deployment          common.ObjectEnsurer
	service             common.ObjectEnsurer
	propertiesConfigMap common.ObjectEnsurer
}

func newObjectEnsurers(support *common.StateSupport) *objectEnsurers {
	return &objectEnsurers{
		deployment:          common.NewObjectEnsurer(support.C, common.DeploymentCreator),
		service:             common.NewObjectEnsurer(support.C, common.ServiceCreator),
		propertiesConfigMap: common.NewObjectEnsurer(support.C, common.WorkflowPropsConfigMapCreator),
	}
}

// NewProfileReconciler the default profile builder which includes a build state to run an internal build process
// to have an immutable workflow image deployed
func NewProfileReconciler(client client.Client, cfg *rest.Config, recorder record.EventRecorder) profiles.ProfileReconciler {
	support := &common.StateSupport{
		C:        client,
		Catalog:  discovery.NewServiceCatalogForConfig(client, cfg),
		Recorder: recorder,
	}
	// the reconciliation state machine
	stateMachine := common.NewReconciliationStateMachine(
		&newBuilderState{StateSupport: support},
		&followBuildStatusState{StateSupport: support},
		&deployWithBuildWorkflowState{StateSupport: support, ensurers: newObjectEnsurers(support)},
	)
	reconciler := &prodProfile{
		Reconciler: common.NewReconciler(support, stateMachine),
	}

	return reconciler
}

// NewProfileForOpsReconciler creates an alternative prod profile that won't require to build the workflow image in order to deploy
// the workflow application. It assumes that the image has been built somewhere else.
func NewProfileForOpsReconciler(client client.Client, cfg *rest.Config, recorder record.EventRecorder) profiles.ProfileReconciler {
	support := &common.StateSupport{
		C:        client,
		Catalog:  discovery.NewServiceCatalogForConfig(client, cfg),
		Recorder: recorder,
	}
	// the reconciliation state machine
	stateMachine := common.NewReconciliationStateMachine(
		&ensureBuildSkipped{StateSupport: support},
		&followDeployWorkflowState{StateSupport: support, ensurers: newObjectEnsurers(support)},
	)
	reconciler := &prodProfile{
		Reconciler: common.NewReconciler(support, stateMachine),
	}

	return reconciler
}

func (p prodProfile) GetProfile() metadata.ProfileType {
	return metadata.ProdProfile
}
