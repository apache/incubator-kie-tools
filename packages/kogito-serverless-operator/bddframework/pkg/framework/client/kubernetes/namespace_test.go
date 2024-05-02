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

package kubernetes

import (
	"testing"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"

	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func Test_CreateNamespaceThatDoesNotExist(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	ns, err := NamespaceC(&client.Client{ControlCli: cli}).CreateIfNotExists("test")
	assert.Nil(t, err)
	assert.NotNil(t, ns)
}

func Test_FetchNamespaceThatDoesNotExist(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	ns, err := NamespaceC(&client.Client{ControlCli: cli}).Fetch("test")
	assert.Nil(t, err)
	assert.Nil(t, ns)
}

func Test_FetchNamespaceThatDExists(t *testing.T) {
	cli := fake.NewClientBuilder().WithRuntimeObjects(&corev1.Namespace{ObjectMeta: metav1.ObjectMeta{Name: "test", CreationTimestamp: metav1.Now()}}).Build()
	ns, err := NamespaceC(&client.Client{ControlCli: cli}).Fetch("test")
	assert.Nil(t, err)
	assert.NotNil(t, ns)
	assert.False(t, ns.CreationTimestamp.IsZero())
}
