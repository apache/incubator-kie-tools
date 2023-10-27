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

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/test"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/apps/v1"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
)

func Test_CheckPodTemplateChangesReflectDeployment(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithProdOpsProfile(t.Name())

	client := test.NewSonataFlowClientBuilder().
		WithRuntimeObjects(workflow).
		WithStatusSubresource(workflow).
		Build()
	stateSupport := fakeReconcilerSupport(client)
	handler := newDeploymentHandler(stateSupport, newObjectEnsurers(stateSupport))

	result, objects, err := handler.handle(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotEmpty(t, objects)
	assert.True(t, result.Requeue)

	// Second reconciliation, we do change the image and that must reflect the deployment
	expectedImg := "quay.io/apache/my-new-workflow:1.0.0"
	workflow.Spec.PodTemplate.Container.Image = expectedImg
	utilruntime.Must(client.Update(context.TODO(), workflow))
	result, objects, err = handler.handle(context.TODO(), workflow)
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
