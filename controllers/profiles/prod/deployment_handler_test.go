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

package prod

import (
	"context"
	"testing"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/metadata"
	"github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/test"
	"github.com/apache/incubator-kie-kogito-serverless-operator/workflowproj"
	"github.com/magiconair/properties"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
)

func Test_CheckPodTemplateChangesReflectDeployment(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithProdOpsProfile(t.Name())

	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow).
		Build()
	stateSupport := fakeReconcilerSupport(client)
	handler := newDeploymentReconciler(stateSupport, newObjectEnsurers(stateSupport))

	result, objects, err := handler.reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)

	// Second reconciliation, we do change the image and that must reflect the deployment
	expectedImg := "quay.io/apache/my-new-workflow:1.0.0"
	workflow.Spec.PodTemplate.Container.Image = expectedImg
	utilruntime.Must(client.Update(context.TODO(), workflow))
	result, objects, err = handler.reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)
	for _, o := range objects {
		if _, ok := o.(*v1.Deployment); ok {
			deployment := o.(*v1.Deployment)
			assert.Equal(t, expectedImg, deployment.Spec.Template.Spec.Containers[0].Image)
			assert.Equal(t, v1alpha08.DefaultContainerName, deployment.Spec.Template.Spec.Containers[0].Name)
			break
		}
	}
}

func Test_CheckDeploymentRolloutAfterCMChange(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithProdOpsProfile(t.Name())

	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow).
		Build()
	stateSupport := fakeReconcilerSupport(client)
	handler := newDeploymentReconciler(stateSupport, newObjectEnsurers(stateSupport))

	result, objects, err := handler.reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)

	// Second reconciliation, we do change the configmap and that must rollout the deployment
	var cm *corev1.ConfigMap
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
			cm = o.(*corev1.ConfigMap)
			currentProps := cm.Data[workflowproj.ApplicationPropertiesFileName]
			props, err := properties.LoadString(currentProps)
			assert.Nil(t, err)
			props.MustSet("test.property", "test.value")
			cm.Data[workflowproj.ApplicationPropertiesFileName] = props.String()
		}
	}
	assert.NotNil(t, cm)
	utilruntime.Must(client.Update(context.TODO(), cm))
	result, objects, err = handler.reconcile(context.TODO(), workflow)
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
	workflow := test.GetBaseSonataFlowWithProdOpsProfile(t.Name())

	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow).
		Build()
	stateSupport := fakeReconcilerSupport(client)
	handler := newDeploymentReconciler(stateSupport, newObjectEnsurers(stateSupport))

	result, objects, err := handler.reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)

	// Second reconciliation, we do change the configmap and that must not rollout the deployment
	// because we're not updating the application.properties key
	var cm *corev1.ConfigMap
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
			cm = o.(*corev1.ConfigMap)
			cm.Data["other.key"] = "useless.key = value"
		}
	}
	assert.NotNil(t, cm)
	utilruntime.Must(client.Update(context.TODO(), cm))
	result, objects, err = handler.reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)
	for _, o := range objects {
		if _, ok := o.(*v1.Deployment); ok {
			deployment := o.(*v1.Deployment)
			// Commented while waiting for SRVLOGIC-195 to be addressed
			// assert.NotContains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.RestartedAt)
			assert.Contains(t, deployment.Spec.Template.ObjectMeta.Annotations, metadata.Checksum)
			newChecksum := deployment.Spec.Template.ObjectMeta.Annotations[metadata.Checksum]
			assert.NotEmpty(t, newChecksum)
			// Change to asssert.Equal when SRVLOGIC-195 is addressed
			assert.NotEqual(t, newChecksum, checksum)
			break
		}
	}
}
