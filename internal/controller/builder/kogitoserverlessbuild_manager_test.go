// Copyright 2024 Apache Software Foundation (ASF)
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

package builder

import (
	"testing"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/cfg"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/profiles/common/persistence"
	"github.com/apache/incubator-kie-kogito-serverless-operator/test"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func TestSonataFlowBuildManager_GetOrCreateBuildWithWorkflowPersistence(t *testing.T) {
	// Current platform with no persistence
	currentPlatform := operatorapi.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{Name: "current-platform"},
		Spec:       operatorapi.SonataFlowPlatformSpec{},
		Status:     operatorapi.SonataFlowPlatformStatus{},
	}
	// Persistence is configured in the workflow
	workflow := operatorapi.SonataFlow{
		ObjectMeta: metav1.ObjectMeta{
			Name: "my-workflow",
		},
		Spec: operatorapi.SonataFlowSpec{
			Persistence: &operatorapi.PersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PersistencePostgreSQL{},
			},
		},
		Status: operatorapi.SonataFlowStatus{},
	}
	testGetOrCreateBuildWithPersistence(t, &currentPlatform, &workflow)
}

func TestSonataFlowBuildManager_GetOrCreateBuildWithPlatformPersistence(t *testing.T) {
	// Persistence is configured in the platform
	currentPlatform := operatorapi.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{Name: "current-platform"},
		Spec: operatorapi.SonataFlowPlatformSpec{
			Persistence: &operatorapi.PlatformPersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PlatformPersistencePostgreSQL{},
			},
		},
		Status: operatorapi.SonataFlowPlatformStatus{},
	}
	// Workflow with no persistence
	workflow := operatorapi.SonataFlow{
		ObjectMeta: metav1.ObjectMeta{
			Name: "my-workflow",
		},
		Status: operatorapi.SonataFlowStatus{},
	}
	testGetOrCreateBuildWithPersistence(t, &currentPlatform, &workflow)
}

func TestSonataFlowBuildManager_GetOrCreateBuildWithNoPersistence(t *testing.T) {
	// Platform has no persistence
	currentPlatform := operatorapi.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{Name: "current-platform"},
		Spec:       operatorapi.SonataFlowPlatformSpec{},
		Status:     operatorapi.SonataFlowPlatformStatus{},
	}
	// Workflow has no persistence
	workflow := operatorapi.SonataFlow{
		ObjectMeta: metav1.ObjectMeta{
			Name: "my-workflow",
		},
		Status: operatorapi.SonataFlowStatus{},
	}
	buildManager := prepareGetOrCreateBuildTest(t, &currentPlatform)
	build, _ := buildManager.GetOrCreateBuild(&workflow)
	assert.Equal(t, 0, len(build.Spec.BuildArgs))
	test.RestoreControllersConfig(t)
}

func testGetOrCreateBuildWithPersistence(t *testing.T, currentPlatform *operatorapi.SonataFlowPlatform, workflow *operatorapi.SonataFlow) {
	buildManager := prepareGetOrCreateBuildTest(t, currentPlatform)
	build, _ := buildManager.GetOrCreateBuild(workflow)
	assert.NotNil(t, build)
	assert.Equal(t, 1, len(build.Spec.BuildArgs))
	assertContainsPersistence(t, build.Spec.BuildArgs, 0)
	test.RestoreControllersConfig(t)
}

func prepareGetOrCreateBuildTest(t *testing.T, currentPlatform *operatorapi.SonataFlowPlatform) sonataFlowBuildManager {
	initializeControllersConfig(t)
	platforms := operatorapi.NewSonataFlowPlatformList()
	platforms.Items = []operatorapi.SonataFlowPlatform{*currentPlatform}
	cli := test.NewSonataFlowClientBuilder().WithRuntimeObjects(&platforms).Build()
	buildManager := sonataFlowBuildManager{
		client: cli,
	}
	return buildManager
}

func Test_addPersistenceExtensionsWithEmptyArgs(t *testing.T) {
	initializeControllersConfig(t)
	buildTemplate := &operatorapi.BuildTemplate{}
	addPersistenceExtensions(buildTemplate)
	assert.Equal(t, 1, len(buildTemplate.BuildArgs))
	assertContainsPersistence(t, buildTemplate.BuildArgs, 0)
	test.RestoreControllersConfig(t)
}

func Test_addPersistenceExtensionsWithNoQuarkusExtensionsArg(t *testing.T) {
	initializeControllersConfig(t)
	buildTemplate := &operatorapi.BuildTemplate{
		BuildArgs: []v1.EnvVar{
			{Name: "VAR1"},
		},
	}
	addPersistenceExtensions(buildTemplate)
	assert.Equal(t, 2, len(buildTemplate.BuildArgs))
	assertContainsPersistence(t, buildTemplate.BuildArgs, 1)
	test.RestoreControllersConfig(t)
}

func Test_addPersistenceExtensionsWithQuarkusExtensionsArgAndNoPersistenceExtensions(t *testing.T) {
	initializeControllersConfig(t)
	buildTemplate := &operatorapi.BuildTemplate{
		BuildArgs: []v1.EnvVar{
			{Name: "VAR1"},
			{Name: "QUARKUS_EXTENSIONS", Value: "org.acme:org.acme.library:1.0.0"},
		},
	}
	addPersistenceExtensions(buildTemplate)
	assert.Equal(t, 2, len(buildTemplate.BuildArgs))
	assertContainsPersistence(t, buildTemplate.BuildArgs, 1)
	test.RestoreControllersConfig(t)
}

func Test_addPersistenceExtensionsWithQuarkusExtensionsArgAndPersistenceExtensions(t *testing.T) {
	initializeControllersConfig(t)
	buildTemplate := &operatorapi.BuildTemplate{
		BuildArgs: []v1.EnvVar{
			{Name: "VAR1", Value: "VALUE1"},
			{Name: "QUARKUS_EXTENSIONS", Value: "org.acme:org.acme.library:1.0.0,io.quarkus:quarkus-jdbc-postgresql:8.8.0.Final"},
		},
	}
	addPersistenceExtensions(buildTemplate)
	assert.Equal(t, 2, len(buildTemplate.BuildArgs))
	assert.Equal(t, v1.EnvVar{Name: "VAR1", Value: "VALUE1"}, buildTemplate.BuildArgs[0])
	assert.Equal(t, v1.EnvVar{Name: "QUARKUS_EXTENSIONS", Value: "org.acme:org.acme.library:1.0.0,io.quarkus:quarkus-jdbc-postgresql:8.8.0.Final"}, buildTemplate.BuildArgs[1])
	test.RestoreControllersConfig(t)
}

func initializeControllersConfig(t *testing.T) {
	// emulate the controllers config initialization
	cfg, err := cfg.InitializeControllersCfgAt("../cfg/testdata/controllers-cfg-test.yaml")
	assert.NoError(t, err)
	assert.NotNil(t, cfg)
	assert.Equal(t, 3, len(cfg.PostgreSQLPersistenceExtensions))
}

func assertContainsPersistence(t *testing.T, buildArgs []v1.EnvVar, position int) {
	assert.GreaterOrEqual(t, len(buildArgs), position)
	envVar := buildArgs[position]
	assert.Equal(t, QuarkusExtensionsBuildArg, envVar.Name)
	for _, extension := range persistence.GetPostgreSQLExtensions() {
		assert.Contains(t, envVar.Value, extension.String())
	}
}
