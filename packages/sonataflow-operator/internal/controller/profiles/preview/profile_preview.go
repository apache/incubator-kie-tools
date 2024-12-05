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

package preview

import (
	"time"

	"k8s.io/client-go/rest"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"

	"k8s.io/client-go/tools/record"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/discovery"

	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common"
)

var _ profiles.ProfileReconciler = &previewProfile{}

type previewProfile struct {
	common.Reconciler
}

const (
	requeueAfterStartingBuild   = 3 * time.Minute
	requeueWhileWaitForBuild    = 1 * time.Minute
	requeueWhileWaitForPlatform = 5 * time.Second

	quarkusProdConfigMountPath = "/deployments/config"
)

// ObjectEnsurers is a struct for the objects that ReconciliationState needs to create in the platform for the preview profile.
// ReconciliationState that needs access to it must include this struct as an attribute and initialize it in the profile builder.
// Use NewObjectEnsurers to facilitate building this struct
type ObjectEnsurers struct {
	// deployment for this ensurer. Don't call it directly, use DeploymentByDeploymentModel instead
	deployment common.ObjectEnsurerWithPlatform
	// kservice Knative Serving deployment for this ensurer. Don't call it directly, use DeploymentByDeploymentModel instead
	kservice common.ObjectEnsurerWithPlatform
	// service for this ensurer. Don't call it directly, use ServiceByDeploymentModel instead
	service common.ObjectEnsurer
	// serviceMonitor for this ensurer. Don't call it directly, use ServiceMonitorByDeploymentModel instead
	serviceMonitor        common.ObjectEnsurer
	userPropsConfigMap    common.ObjectEnsurer
	managedPropsConfigMap common.ObjectEnsurerWithPlatform
}

// DeploymentByDeploymentModel gets the deployment ensurer based on the SonataFlow deployment model
func (o *ObjectEnsurers) DeploymentByDeploymentModel(workflow *v1alpha08.SonataFlow) common.ObjectEnsurerWithPlatform {
	if workflow.IsKnativeDeployment() {
		return o.kservice
	}
	return o.deployment
}

// ServiceByDeploymentModel gets the service ensurer based on the SonataFlow deployment model
func (o *ObjectEnsurers) ServiceByDeploymentModel(workflow *v1alpha08.SonataFlow) common.ObjectEnsurer {
	if workflow.IsKnativeDeployment() {
		// Knative Serving handles the service
		return common.NewNoopObjectEnsurer()
	}
	return o.service
}

// ServiceMonitorByDeploymentModel gets the service monitor ensurer based on the SonataFlow deployment model
func (o *ObjectEnsurers) ServiceMonitorByDeploymentModel(workflow *v1alpha08.SonataFlow) common.ObjectEnsurer {
	if workflow.IsKnativeDeployment() {
		// Do not create service monitor for workflows deployed as Knative service
		return common.NewNoopObjectEnsurer()
	}
	return o.serviceMonitor
}

// NewObjectEnsurers common.ObjectEnsurer(s) for the preview profile.
func NewObjectEnsurers(support *common.StateSupport) *ObjectEnsurers {
	return &ObjectEnsurers{
		deployment:            common.NewObjectEnsurerWithPlatform(support.C, common.DeploymentCreator),
		kservice:              common.NewObjectEnsurerWithPlatform(support.C, common.KServiceCreator),
		service:               common.NewObjectEnsurer(support.C, common.ServiceCreator),
		serviceMonitor:        common.NewObjectEnsurer(support.C, common.ServiceMonitorCreator),
		userPropsConfigMap:    common.NewObjectEnsurer(support.C, common.UserPropsConfigMapCreator),
		managedPropsConfigMap: common.NewObjectEnsurerWithPlatform(support.C, common.ManagedPropsConfigMapCreator),
	}
}

// NewProfileReconciler the default profile builder which includes a build state to run an internal build process
// to have an immutable workflow image deployed
func NewProfileReconciler(client client.Client, cfg *rest.Config, recorder record.EventRecorder) profiles.ProfileReconciler {
	support := &common.StateSupport{
		C:        client,
		Cfg:      cfg,
		Catalog:  discovery.NewServiceCatalogForConfig(client, cfg),
		Recorder: recorder,
	}
	// the reconciliation state machine
	stateMachine := common.NewReconciliationStateMachine(
		&newBuilderState{StateSupport: support, ensurers: NewObjectEnsurers(support)},
		&followBuildStatusState{StateSupport: support},
		&deployWithBuildWorkflowState{StateSupport: support, ensurers: NewObjectEnsurers(support)},
	)
	reconciler := &previewProfile{
		Reconciler: common.NewReconciler(support, stateMachine),
	}

	return reconciler
}

func (p previewProfile) GetProfile() metadata.ProfileType {
	return metadata.PreviewProfile
}
