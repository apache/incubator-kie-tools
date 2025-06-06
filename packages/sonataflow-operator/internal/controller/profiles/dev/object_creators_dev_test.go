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

package dev

import (
	"testing"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/version"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
)

func Test_ensureWorkflowDevServiceIsExposed(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())
	//On Kubernetes we want the service exposed in Dev with NodePort
	service, _ := serviceCreator(workflow)
	service.SetUID("1")
	service.SetResourceVersion("1")

	reflectService := service.(*v1.Service)

	assert.NotNil(t, reflectService)
	assert.NotNil(t, reflectService.Spec.Type)
	assert.NotEmpty(t, reflectService.Spec.Type)
	assert.Equal(t, reflectService.Spec.Type, v1.ServiceTypeNodePort)
	assert.NotNil(t, reflectService.ObjectMeta)
	assert.NotNil(t, reflectService.ObjectMeta.Labels)
	assert.Equal(t, reflectService.ObjectMeta.Labels, map[string]string{
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
