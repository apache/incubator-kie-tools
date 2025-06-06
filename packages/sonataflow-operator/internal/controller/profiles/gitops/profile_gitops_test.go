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
	"testing"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/version"

	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/client-go/rest"
	clientruntime "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
)

func Test_Reconciler_ProdOps(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithPreviewProfile(t.Name())
	workflow.Spec.PodTemplate.PodSpec.InitContainers = append(workflow.Spec.PodTemplate.PodSpec.InitContainers, corev1.Container{
		Name:    "check-postgres",
		Image:   "registry.access.redhat.com/ubi9/ubi-micro:latest",
		Command: []string{"sh", "-c", "until (echo 1 > /dev/tcp/postgres.$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace).svc.cluster.local/5432) >/dev/null 2>&1; do echo \"Waiting for postgres server\"; sleep 3; done;"},
	})
	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow, &operatorapi.SonataFlowBuild{}).Build()

	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	result, err := NewProfileForOpsReconciler(client, &rest.Config{}, test.NewFakeRecorder()).Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)

	assert.NotNil(t, result.RequeueAfter)
	assert.True(t, workflow.Status.GetCondition(api.BuiltConditionType).IsFalse())
	assert.Equal(t, api.BuildSkippedReason, workflow.Status.GetCondition(api.BuiltConditionType).Reason)
	// We need the deployment controller to tell us that the workflow is ready
	// Since we don't have it in a mocked env, the result must be ready == false
	assert.False(t, workflow.Status.IsReady())

	// Reconcile again to run the deployment handler
	result, err = NewProfileForOpsReconciler(client, &rest.Config{}, test.NewFakeRecorder()).Reconcile(context.TODO(), workflow)
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
		"app":                               "simple",
		"app.kubernetes.io/instance":        "simple",
		"test":                              "test",
		"sonataflow.org/workflow-app":       "simple",
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/name":            "simple",
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator",
		"app.kubernetes.io/part-of":         "sonataflow-platform",
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
	})
}
