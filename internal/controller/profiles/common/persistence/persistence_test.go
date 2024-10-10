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

package persistence

import (
	"testing"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/stretchr/testify/assert"
)

func TestResolveWorkflowPersistenceProperties_WithWorkflowPersistence(t *testing.T) {
	workflow := operatorapi.SonataFlow{
		Spec: operatorapi.SonataFlowSpec{
			Persistence: &operatorapi.PersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PersistencePostgreSQL{},
			},
		},
	}
	platform := operatorapi.SonataFlowPlatform{}
	testResolveWorkflowPersistencePropertiesWithPersistence(t, &workflow, &platform)
}

func TestResolveWorkflowPersistenceProperties_WithPlatformPersistence(t *testing.T) {
	workflow := operatorapi.SonataFlow{}
	platform := operatorapi.SonataFlowPlatform{
		Spec: operatorapi.SonataFlowPlatformSpec{
			Persistence: &operatorapi.PlatformPersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PlatformPersistencePostgreSQL{},
			},
		},
	}
	testResolveWorkflowPersistencePropertiesWithPersistence(t, &workflow, &platform)
}

func TestResolveWorkflowPersistenceProperties_WithPlatformPersistenceButBannedInWorkflow(t *testing.T) {
	workflow := operatorapi.SonataFlow{}
	workflow.Spec.Persistence = &operatorapi.PersistenceOptionsSpec{}
	platform := operatorapi.SonataFlowPlatform{
		Spec: operatorapi.SonataFlowPlatformSpec{
			Persistence: &operatorapi.PlatformPersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PlatformPersistencePostgreSQL{},
			},
		},
	}
	props, err := ResolveWorkflowPersistenceProperties(&workflow, &platform)
	assert.NotNil(t, props)
	assert.Nil(t, err)
	assert.Equal(t, 0, props.Len())
}

func TestResolveWorkflowPersistenceProperties_WithNoPersistence(t *testing.T) {
	workflow := operatorapi.SonataFlow{}
	platform := operatorapi.SonataFlowPlatform{}
	props, err := ResolveWorkflowPersistenceProperties(&workflow, &platform)
	assert.NotNil(t, props)
	assert.Nil(t, err)
	assert.Equal(t, 0, props.Len())
}

func testResolveWorkflowPersistencePropertiesWithPersistence(t *testing.T, workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) {
	props, err := ResolveWorkflowPersistenceProperties(workflow, platform)
	assert.Nil(t, err)
	assert.NotNil(t, props)
	assert.Equal(t, 3, props.Len())
	value, _ := props.Get("kogito.persistence.type")
	assert.Equal(t, "jdbc", value)
	value, _ = props.Get("quarkus.datasource.db-kind")
	assert.Equal(t, "postgresql", value)
	value, _ = props.Get("kogito.persistence.proto.marshaller")
	assert.Equal(t, "false", value)
}
