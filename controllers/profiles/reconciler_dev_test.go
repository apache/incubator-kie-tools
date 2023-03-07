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

package profiles

import (
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/intstr"
	clientruntime "sigs.k8s.io/controller-runtime/pkg/client"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"

	"github.com/kiegroup/kogito-serverless-operator/api"

	"github.com/kiegroup/kogito-serverless-operator/test"
)

func Test_recoverFromFailureNoDeployment(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleYamlCR, t.Name())
	workflowID := clientruntime.ObjectKeyFromObject(workflow)

	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, "")
	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()

	reconciler := newDevProfileReconciler(client, &logger)

	// we are in failed state and have no objects
	result, err := reconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// the recover state tried to clear the conditions of our workflow, so we can try reconciling it again
	workflow = test.MustGetWorkflow(t, client, workflowID)
	assert.True(t, workflow.Status.GetTopLevelCondition().IsUnknown())
	result, err = reconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// the deployment should be there
	_ = test.MustGetDeployment(t, client, workflow)

	// we failed again, but now we have the deployment
	workflow = test.MustGetWorkflow(t, client, workflowID)
	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, "")
	err = client.Status().Update(context.TODO(), workflow)
	assert.NoError(t, err)
	// the fake client won't update the deployment status condition since we don't have a deployment controller
	// our state will think that we don't have a deployment available yet, so it will try to reset the pods
	result, err = reconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	workflow = test.MustGetWorkflow(t, client, workflowID)
	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, "")
	assert.Equal(t, 1, workflow.Status.RecoverFailureAttempts)

	deployment := test.MustGetDeployment(t, client, workflow)
	assert.NotEmpty(t, deployment.Spec.Template.ObjectMeta.Annotations["kubectl.kubernetes.io/restartedAt"])
}

func Test_newDevProfile(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleYamlCR, t.Name())
	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()
	devReconciler := newDevProfileReconciler(client, &logger)

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, defaultKogitoServerlessWorkflowDevImage, deployment.Spec.Template.Spec.Containers[0].Image)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].LivenessProbe)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].ReadinessProbe)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].StartupProbe)

	defCM := test.MustGetConfigMap(t, client, workflow)
	assert.NotEmpty(t, defCM.Data[workflow.Name+kogitoWorkflowJSONFileExt])
	assert.Equal(t, configMapWorkflowDefMountPath, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].MountPath)

	propCM := &v1.ConfigMap{}
	_ = client.Get(context.TODO(), types.NamespacedName{Namespace: workflow.Namespace, Name: getWorkflowPropertiesConfigMapName(workflow)}, propCM)
	assert.NotEmpty(t, propCM.Data[applicationPropertiesFileName])
	assert.Equal(t, quarkusDevConfigMountPath, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[1].MountPath)
	assert.Contains(t, propCM.Data[applicationPropertiesFileName], "quarkus.http.port")

	service := test.MustGetService(t, client, workflow)
	assert.Equal(t, int32(defaultHTTPWorkflowPort), service.Spec.Ports[0].TargetPort.IntVal)

	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Status().Update(context.TODO(), workflow)
	assert.NoError(t, err)

	// Mess with the object
	service.Spec.Ports[0].TargetPort = intstr.FromInt(9090)
	err = client.Update(context.TODO(), service)
	assert.NoError(t, err)

	// reconcile again
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the reconciliation ensures the object correctly
	service = test.MustGetService(t, client, workflow)
	assert.Equal(t, int32(defaultHTTPWorkflowPort), service.Spec.Ports[0].TargetPort.IntVal)

	// now with the deployment
	deployment = test.MustGetDeployment(t, client, workflow)
	deployment.Spec.Template.Spec.Containers[0].Image = "default"
	err = client.Update(context.TODO(), deployment)
	assert.NoError(t, err)

	propCM = &v1.ConfigMap{}
	_ = client.Get(context.TODO(), types.NamespacedName{Namespace: workflow.Namespace, Name: getWorkflowPropertiesConfigMapName(workflow)}, propCM)
	assert.NotEmpty(t, propCM.Data[applicationPropertiesFileName])
	assert.Contains(t, propCM.Data[applicationPropertiesFileName], "quarkus.http.port")

	// reconcile
	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, defaultKogitoServerlessWorkflowDevImage, deployment.Spec.Template.Spec.Containers[0].Image)
}
