// Copyright 2022 Red Hat, Inc. and/or its affiliates
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

package shared

import (
	kogitoservice "github.com/kiegroup/kogito-operator/core/kogitoservice"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"os"
	"testing"
)

func TestCreateProtoBufConfigMap(t *testing.T) {
	ns := t.Name()
	runtimeService := test.CreateFakeKogitoRuntime(ns)

	runtimeDeployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      runtimeService.Name,
			Namespace: runtimeService.Namespace,
		},
		Status: appsv1.DeploymentStatus{
			AvailableReplicas: 1,
		},
	}

	cli := test.NewFakeClientBuilder().AddK8sObjects(runtimeDeployment).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}

	protoBufFilePath := "/persistence/protobuf/list.json"
	responseWithProtoBufData := `[]`
	server := test.MockKogitoSvcReplies(t, test.ServerHandler{Path: protoBufFilePath, JSONResponse: responseWithProtoBufData})
	defer server.Close()
	err := os.Setenv(
		kogitoservice.EnvVarKogitoServiceURL,
		server.URL,
	)
	assert.NoError(t, err)
	os.Setenv(kogitoservice.EnvVarKogitoServiceURL, server.URL)
	protobufConfigMapHandler := NewProtoBufConfigMapHandler(context)
	configMap, err := protobufConfigMapHandler.CreateProtoBufConfigMap(runtimeService)
	assert.NoError(t, err)
	assert.Nil(t, configMap)
}
