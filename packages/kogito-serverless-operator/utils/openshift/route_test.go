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

package openshift

import (
	"testing"

	v1 "github.com/openshift/api/route/v1"
	"github.com/stretchr/testify/assert"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
)

const (
	WorkflowName      = "helloworld"
	WorkflowNamespace = "usecase1"
)

func TestRouteForWorkflow(t *testing.T) {
	workflow := &operatorapi.SonataFlow{
		ObjectMeta: metav1.ObjectMeta{
			Name:      WorkflowName,
			Namespace: WorkflowNamespace,
		},
	}
	route, err := RouteForWorkflow(workflow)
	assert.NotNil(t, route)
	assert.Nil(t, err)
	assert.Equal(t, WorkflowName, route.ObjectMeta.Name)
	assert.Equal(t, WorkflowNamespace, route.ObjectMeta.Namespace)
	assert.Equal(t, "Service", route.Spec.To.Kind)
	assert.Equal(t, WorkflowName, route.Spec.To.Name)
	assert.Equal(t, v1.TLSTerminationEdge, route.Spec.TLS.Termination)
}
