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

package preview

import (
	"context"
	"testing"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/manager"

	"github.com/magiconair/properties"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	servingv1 "knative.dev/serving/pkg/apis/serving/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
)

type fakeDeploymentReconciler struct {
	DeploymentReconciler
}

func Test_CheckDeploymentModelIsKnative(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithPreviewProfile(t.Name())
	workflow.Spec.PodTemplate.DeploymentModel = v1alpha08.KnativeDeploymentModel

	cli := test.NewSonataFlowClientBuilderWithKnative().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow).
		Build()
	stateSupport := fakeReconcilerSupport(cli)
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	handler := NewDeploymentReconciler(stateSupport, NewObjectEnsurers(stateSupport))

	result, objects, err := handler.ensureObjects(context.TODO(), workflow, "")
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)

	var ksvc *servingv1.Service
	for _, o := range objects {
		if _, ok := o.(*servingv1.Service); ok {
			ksvc = o.(*servingv1.Service)
			assert.Equal(t, v1alpha08.DefaultContainerName, ksvc.Spec.Template.Spec.Containers[0].Name)
			break
		}
	}
	assert.NotNil(t, ksvc)
}

func Test_CheckPodTemplateChangesReflectDeployment(t *testing.T) {
	manager.InitializeSFCWorker(manager.SonataFlowControllerWorkerSize)
	manager.SetOperatorStartTime()
	workflow := test.GetBaseSonataFlowWithPreviewProfile(t.Name())

	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow).
		Build()
	stateSupport := fakeReconcilerSupport(client)
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	handler := NewDeploymentReconciler(stateSupport, NewObjectEnsurers(stateSupport))

	result, objects, err := handler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)

	// Second reconciliation, we do change the image and that must reflect the deployment
	expectedImg := test.CommonImageTag
	workflow.Spec.PodTemplate.Container.Image = expectedImg
	utilruntime.Must(client.Update(context.TODO(), workflow))
	result, objects, err = handler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)
	var deployment *v1.Deployment
	for _, o := range objects {
		if _, ok := o.(*v1.Deployment); ok {
			deployment = o.(*v1.Deployment)
			assert.Equal(t, expectedImg, deployment.Spec.Template.Spec.Containers[0].Image)
			assert.Equal(t, v1alpha08.DefaultContainerName, deployment.Spec.Template.Spec.Containers[0].Name)
			break
		}
	}
	assert.NotNil(t, deployment)
}

func Test_CheckDeploymentRolloutAfterCMChange(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithPreviewProfile(t.Name())

	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow).
		Build()
	stateSupport := fakeReconcilerSupport(client)
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	handler := NewDeploymentReconciler(stateSupport, NewObjectEnsurers(stateSupport))

	result, objects, err := handler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)

	userPropsCM := &corev1.ConfigMap{}
	err = client.Get(context.TODO(), types.NamespacedName{Name: workflowproj.GetWorkflowUserPropertiesConfigMapName(workflow), Namespace: t.Name()}, userPropsCM)
	assert.NoError(t, err)

	// Second reconciliation, we do change the configmap and that must rollout the deployment
	var managedPropsCM *corev1.ConfigMap
	var checksum string
	for _, o := range objects {
		if _, ok := o.(*v1.Deployment); ok {
			deployment := o.(*v1.Deployment)
			assert.NotNil(t, deployment.Spec.Template.ObjectMeta.Annotations)
			assert.Contains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.Checksum)
			checksum = deployment.Spec.Template.ObjectMeta.Annotations[metadata.Checksum]
			assert.NotContains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.RestartedAt)
		}
		if _, ok := o.(*corev1.ConfigMap); ok {
			cm := o.(*corev1.ConfigMap)
			if cm.Name == workflowproj.GetWorkflowManagedPropertiesConfigMapName(workflow) {
				managedPropsCM = cm
			}
		}
	}
	assert.NotNil(t, managedPropsCM)

	currentProps := userPropsCM.Data[workflowproj.ApplicationPropertiesFileName]
	props, err := properties.LoadString(currentProps)
	assert.Nil(t, err)
	props.MustSet("test.property", "test.value")
	userPropsCM.Data[workflowproj.ApplicationPropertiesFileName] = props.String()
	utilruntime.Must(client.Update(context.TODO(), userPropsCM))
	result, objects, err = handler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)
	for _, o := range objects {
		if _, ok := o.(*v1.Deployment); ok {
			deployment := o.(*v1.Deployment)
			assert.Contains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.RestartedAt)
			assert.Contains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.Checksum)
			newChecksum := deployment.Spec.Template.ObjectMeta.Annotations[metadata.Checksum]
			assert.NotEmpty(t, newChecksum)
			assert.NotEqual(t, newChecksum, checksum)
			break
		}
	}
}

func Test_CheckDeploymentUnchangedAfterCMChangeOtherKeys(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithPreviewProfile(t.Name())

	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow).
		Build()
	stateSupport := fakeReconcilerSupport(client)
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	handler := NewDeploymentReconciler(stateSupport, NewObjectEnsurers(stateSupport))

	result, objects, err := handler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)

	userPropsCM := &corev1.ConfigMap{}
	err = client.Get(context.TODO(), types.NamespacedName{Name: workflowproj.GetWorkflowUserPropertiesConfigMapName(workflow), Namespace: t.Name()}, userPropsCM)
	assert.NoError(t, err)

	// Second reconciliation, we do change the configmap and that must not rollout the deployment
	// because we're not updating the application.properties key
	var managedPropsCM *corev1.ConfigMap
	var checksum string
	for _, o := range objects {
		if _, ok := o.(*v1.Deployment); ok {
			deployment := o.(*v1.Deployment)
			assert.NotNil(t, deployment.Spec.Template.ObjectMeta.Annotations)
			assert.Contains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.Checksum)
			checksum = deployment.Spec.Template.ObjectMeta.Annotations[metadata.Checksum]
			assert.NotContains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.RestartedAt)
		}
		if _, ok := o.(*corev1.ConfigMap); ok {
			cm := o.(*corev1.ConfigMap)
			if cm.Name == workflowproj.GetWorkflowManagedPropertiesConfigMapName(workflow) {
				managedPropsCM = cm
			}
		}
	}
	assert.NotNil(t, managedPropsCM)

	userPropsCM.Data["other.key"] = "useless.key = value"
	utilruntime.Must(client.Update(context.TODO(), userPropsCM))
	result, objects, err = handler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)
	for _, o := range objects {
		if _, ok := o.(*v1.Deployment); ok {
			deployment := o.(*v1.Deployment)
			assert.NotContains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.RestartedAt)
			newChecksum := deployment.Spec.Template.ObjectMeta.Annotations[metadata.Checksum]
			assert.NotEmpty(t, newChecksum)
			assert.Equal(t, newChecksum, checksum)
			break
		}
	}
}
