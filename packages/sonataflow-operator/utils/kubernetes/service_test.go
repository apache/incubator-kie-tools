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

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
)

func Test_retrievingKubernetesServiceHost(t *testing.T) {
	t.Run("verify that the service host is returned with the default cluster name on default namespace", func(t *testing.T) {
		svc := &v1.Service{}
		svc.Name = "workflow"
		host := retrieveServiceHost(svc)

		assert.NotNil(t, host)
		assert.Equal(t, host, svc.Name+".default.svc.cluster.local")

	})

	t.Run("verify that the service host is returned with the default cluster name on non-default namespace", func(t *testing.T) {
		svc := &v1.Service{}
		svc.Name = "workflow"
		svc.Namespace = "ns"
		host := retrieveServiceHost(svc)

		assert.NotNil(t, host)
		assert.Equal(t, host, svc.Name+"."+svc.Namespace+".svc.cluster.local")

	})
}

func Test_retrievingKubernetesServiceURL(t *testing.T) {
	t.Run("verify that the service URL is returned with the default cluster name on default namespace", func(t *testing.T) {
		svc := &v1.Service{}
		svc.Name = "workflow"
		RetrieveServiceURL(svc)

		url, err := RetrieveServiceURL(svc)

		assert.NoError(t, err)
		assert.NotNil(t, url)
		assert.Equal(t, url.String(), "http://"+svc.Name+".default.svc.cluster.local/"+svc.Name)

	})

	t.Run("verify that the service URL is returned with the default cluster name on non-default namespace", func(t *testing.T) {
		svc := &v1.Service{}
		svc.Name = "workflow"
		svc.Namespace = "ns"
		RetrieveServiceURL(svc)

		url, err := RetrieveServiceURL(svc)

		assert.NoError(t, err)
		assert.NotNil(t, url)
		assert.Equal(t, url.String(), "http://"+svc.Name+"."+svc.Namespace+".svc.cluster.local/"+svc.Name)

	})

}
