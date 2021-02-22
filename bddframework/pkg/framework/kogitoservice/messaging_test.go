// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package kogitoservice

import (
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	"testing"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func Test_fetchRequiredTopics(t *testing.T) {
	responseWithTopics := `[
   {
      "name":"kogito_incoming_stream",
      "type":"INCOMING",
      "eventsMeta":[
         {
            "type":"travellers",
            "source":"",
            "kind":"CONSUMED"
         }
      ]
   },
   {
      "name":"processedtravellers",
      "type":"OUTGOING",
      "eventsMeta":[
         {
            "type":"process.travelers.processedtravellers",
            "source":"/process/travelers",
            "kind":"PRODUCED"
         }
      ]
   }
]`
	instance := createServiceInstance(t)

	server := mockKogitoSvcReplies(t, serverHandler{Path: topicInfoPath, JSONResponse: responseWithTopics})
	defer server.Close()
	cli := test.NewFakeClientBuilder().AddK8sObjects(createAvailableDeployment(instance)).Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	m := messagingDeployer{Context: context}
	topics, err := m.fetchRequiredTopicsForURL(instance, server.URL)
	assert.NoError(t, err)
	assert.NotEmpty(t, topics)
	m.setCloudEventsStatus(instance, topics)
	assert.NotNil(t, instance.GetStatus().GetCloudEvents())
}

func Test_fetchRequiredTopicsWithEmptyReply(t *testing.T) {
	emptyResponse := "[]"
	instance := createServiceInstance(t)

	server := mockKogitoSvcReplies(t, serverHandler{Path: topicInfoPath, JSONResponse: emptyResponse})
	defer server.Close()
	cli := test.NewFakeClientBuilder().AddK8sObjects(createAvailableDeployment(instance)).Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	m := messagingDeployer{Context: context}
	topics, err := m.fetchRequiredTopicsForURL(instance, server.URL)
	assert.NoError(t, err)
	assert.Empty(t, topics)
}

func createAvailableDeployment(instance api.KogitoService) *v1.Deployment {
	return &v1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: instance.GetName(), Namespace: instance.GetNamespace()},
		Status: v1.DeploymentStatus{
			AvailableReplicas: 1,
		},
	}

}

func createServiceInstance(t *testing.T) api.KogitoService {
	instance := test.CreateFakeDataIndex(t.Name())
	instance.GetStatus().SetDeploymentConditions([]v1.DeploymentCondition{
		{
			Type:           v1.DeploymentAvailable,
			Status:         corev1.ConditionTrue,
			LastUpdateTime: metav1.Now(),
		},
	})
	return instance
}
