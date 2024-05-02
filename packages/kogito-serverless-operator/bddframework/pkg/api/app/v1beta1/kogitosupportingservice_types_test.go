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

package v1beta1

import (
	"testing"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api"
)

func TestGetItems(t *testing.T) {
	kogitoSupportingServiceList := &KogitoSupportingServiceList{
		Items: []KogitoSupportingService{
			{
				ObjectMeta: v1.ObjectMeta{
					Name: "data-index",
				},
				Spec: KogitoSupportingServiceSpec{
					ServiceType: api.DataIndex,
				},
			},
			{
				ObjectMeta: v1.ObjectMeta{
					Name: "mgmt-console",
				},
				Spec: KogitoSupportingServiceSpec{
					ServiceType: api.MgmtConsole,
				},
			},
		},
	}

	kogitoSupportingServiceInterface := kogitoSupportingServiceList.GetItems()
	assert.Equal(t, 2, len(kogitoSupportingServiceInterface))

	assert.Equal(t, "data-index", kogitoSupportingServiceInterface[0].GetName())
	assert.Equal(t, api.DataIndex, kogitoSupportingServiceInterface[0].GetSupportingServiceSpec().GetServiceType())

	assert.Equal(t, "mgmt-console", kogitoSupportingServiceInterface[1].GetName())
	assert.Equal(t, api.MgmtConsole, kogitoSupportingServiceInterface[1].GetSupportingServiceSpec().GetServiceType())
}
