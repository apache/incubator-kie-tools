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
	"context"
	"testing"
	"time"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/version"

	prometheus "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/client-go/rest"
	clientruntime "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
)

func Test_Reconciler_ProdCustomPod(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithProdProfile(t.Name())
	workflow.Spec.PodTemplate.PodSpec.InitContainers = append(workflow.Spec.PodTemplate.PodSpec.InitContainers, corev1.Container{
		Name:    "check-postgres",
		Image:   "registry.access.redhat.com/ubi9/ubi-micro:latest",
		Command: []string{"sh", "-c", "until (echo 1 > /dev/tcp/postgres.$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace).svc.cluster.local/5432) >/dev/null 2>&1; do echo \"Waiting for postgres server\"; sleep 3; done;"},
	})
	workflow.Status.Manager().MarkTrue(api.BuiltConditionType)
	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	build := test.GetLocalSucceedSonataFlowBuild(workflow.Name, workflow.Namespace)
	platform := test.GetBasePlatformInReadyPhase(workflow.Namespace)
	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow, build, platform).
		WithStatusSubresource(workflow, build, platform).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	_, err := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder()).Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)

	// Let's check for the right creation of the workflow (one CM volume, one container with a custom image)
	deployment := &appsv1.Deployment{}
	err = client.Get(context.TODO(), clientruntime.ObjectKeyFromObject(workflow), deployment)
	assert.NoError(t, err)

	assert.Len(t, deployment.Spec.Template.Spec.Volumes, 1)
	assert.Len(t, deployment.Spec.Template.Spec.Containers, 1)
	assert.Len(t, deployment.Spec.Template.Spec.InitContainers, 1)
	assert.Len(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts, 1)
	assert.NotNil(t, deployment.ObjectMeta)
	assert.NotNil(t, deployment.ObjectMeta.Labels)
	assert.Equal(t, deployment.ObjectMeta.Labels, map[string]string{
		"app":                               "greeting",
		"app.kubernetes.io/instance":        "greeting",
		"test":                              "test",
		"sonataflow.org/workflow-app":       "greeting",
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/name":            "greeting",
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator",
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
	})
}

func Test_reconcilerProdBuildConditions(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	platform := test.GetBasePlatformInReadyPhase(t.Name())
	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow, platform).
		WithStatusSubresource(workflow, platform, &operatorapi.SonataFlowBuild{}).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	result, err := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder()).Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)

	assert.NotNil(t, result.RequeueAfter)
	assert.True(t, workflow.Status.IsBuildRunningOrUnknown())
	assert.False(t, workflow.Status.IsReady())

	// still building
	result, err = NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder()).Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.Equal(t, requeueWhileWaitForBuild, result.RequeueAfter)
	assert.True(t, workflow.Status.IsBuildRunningOrUnknown())
	assert.False(t, workflow.Status.IsReady())

	// let's finish this build
	build := &operatorapi.SonataFlowBuild{}
	assert.NoError(t, client.Get(context.TODO(), clientruntime.ObjectKeyFromObject(workflow), build))
	build.Status.BuildPhase = operatorapi.BuildPhaseSucceeded
	assert.NoError(t, client.Status().Update(context.TODO(), build))

	// last reconciliation cycle waiting for build
	result, err = NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder()).Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.Equal(t, requeueWhileWaitForBuild, result.RequeueAfter)
	assert.False(t, workflow.Status.IsBuildRunningOrUnknown())
	assert.False(t, workflow.Status.IsReady())
	assert.Equal(t, api.WaitingForDeploymentReason, workflow.Status.GetTopLevelCondition().Reason)

	// now we create the objects
	result, err = NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder()).Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.False(t, workflow.Status.IsBuildRunningOrUnknown())
	assert.False(t, workflow.Status.IsReady())
	assert.Equal(t, api.WaitingForDeploymentReason, workflow.Status.GetTopLevelCondition().Reason)

	// now with the objects created, it should be running
	// let's update the deployment status to available == true
	deployment := &appsv1.Deployment{}
	err = client.Get(context.TODO(), clientruntime.ObjectKeyFromObject(workflow), deployment)
	assert.NoError(t, err)
	deployment.Status.Conditions = append(deployment.Status.Conditions, appsv1.DeploymentCondition{
		Type:   appsv1.DeploymentAvailable,
		Status: corev1.ConditionTrue,
	})
	err = client.Status().Update(context.TODO(), deployment)
	assert.NoError(t, err)

	result, err = NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder()).Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.False(t, workflow.Status.IsBuildRunningOrUnknown())
	assert.True(t, workflow.Status.IsReady())
}

func Test_deployWorkflowReconciliationHandler_handleObjects(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	platform := test.GetBasePlatformInReadyPhase(t.Name())
	platform.Spec.Monitoring = &operatorapi.PlatformMonitoringOptionsSpec{Enabled: true}
	build := test.GetLocalSucceedSonataFlowBuild(workflow.Name, workflow.Namespace)
	client := test.NewKogitoClientBuilderWithOpenShift().
		WithRuntimeObjects(workflow, platform, build).
		WithStatusSubresource(workflow, platform, build).
		Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	handler := &deployWithBuildWorkflowState{
		StateSupport: fakeReconcilerSupport(client),
		ensurers:     NewObjectEnsurers(&common.StateSupport{C: client}),
	}
	result, objects, err := handler.Do(context.TODO(), workflow)
	assert.Greater(t, result.RequeueAfter, int64(0))
	assert.NoError(t, err)
	assert.NotNil(t, result)
	assert.Len(t, objects, 4)

	deployment := &appsv1.Deployment{}
	err = client.Get(context.TODO(), clientruntime.ObjectKeyFromObject(workflow), deployment)
	assert.NoError(t, err)
	assert.NotEmpty(t, deployment.Spec.Template.Spec.Containers[0].Image)

	err = client.Get(context.TODO(), clientruntime.ObjectKeyFromObject(workflow), workflow)
	assert.NoError(t, err)
	assert.False(t, workflow.Status.IsReady())
	assert.Equal(t, api.WaitingForDeploymentReason, workflow.Status.GetTopLevelCondition().Reason)

	serviceMonitor := &prometheus.ServiceMonitor{}
	err = client.Get(context.TODO(), clientruntime.ObjectKeyFromObject(workflow), serviceMonitor)
	assert.NoError(t, err)
	assert.NotEmpty(t, serviceMonitor.Spec)
	assert.NotEmpty(t, serviceMonitor.Spec.Selector)
	assert.Equal(t, len(serviceMonitor.Spec.Selector.MatchLabels), 2)
	assert.Equal(t, serviceMonitor.Spec.Selector.MatchLabels[workflowproj.LabelWorkflow], workflow.Name)
	assert.Equal(t, serviceMonitor.Spec.Selector.MatchLabels[workflowproj.LabelWorkflowNamespace], workflow.Namespace)
	assert.Equal(t, len(serviceMonitor.Spec.Endpoints), 1)
	assert.Equal(t, serviceMonitor.Spec.Endpoints[0].Port, "web")
	assert.Equal(t, serviceMonitor.Spec.Endpoints[0].Path, "/q/metrics")
}

func Test_WorkflowChangedCheck(t *testing.T) {
	// we load a workflow with metadata.generation to 0
	workflow := test.GetBaseSonataFlow(t.Name())
	platform := test.GetBasePlatformInReadyPhase(t.Name())
	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow, platform).
		WithStatusSubresource(workflow, platform, &operatorapi.SonataFlowBuild{}).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	handler := &deployWithBuildWorkflowState{
		StateSupport: fakeReconcilerSupport(client),
		ensurers:     NewObjectEnsurers(&common.StateSupport{C: client}),
	}
	result, objects, err := handler.Do(context.TODO(), workflow)
	assert.Greater(t, result.RequeueAfter, int64(time.Second))
	assert.NoError(t, err)
	assert.NotNil(t, result)
	assert.Len(t, objects, 3)

	// then we load the current workflow
	workflowChanged := &operatorapi.SonataFlow{}
	err = client.Get(context.TODO(), clientruntime.ObjectKeyFromObject(workflow), workflowChanged)
	assert.NoError(t, err)
	//we change something within the flow
	workflowChanged.Spec.Flow.AutoRetries = true

	// reconcile -> the one in the k8s DB is different, so there's a change.
	handler = &deployWithBuildWorkflowState{
		StateSupport: fakeReconcilerSupport(client),
		ensurers:     NewObjectEnsurers(&common.StateSupport{C: client}),
	}
	result, objects, err = handler.Do(context.TODO(), workflowChanged)
	assert.NoError(t, err)
	// no requeue, no objects since the workflow has changed
	assert.Equal(t, time.Duration(0), result.RequeueAfter)
	assert.False(t, result.Requeue)
	assert.Len(t, objects, 0)
}

func fakeReconcilerSupport(client clientruntime.Client) *common.StateSupport {
	return &common.StateSupport{
		C:        client,
		Recorder: test.NewFakeRecorder(),
		Cfg:      &rest.Config{},
	}
}
