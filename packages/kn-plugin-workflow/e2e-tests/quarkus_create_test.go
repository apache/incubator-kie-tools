//go:build e2e_tests

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package e2e_tests

import (
	"fmt"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/require"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command/quarkus"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
)

type CfgTestInputQuarkusCreate struct {
	input quarkus.CreateQuarkusProjectConfig
}

var cfgTestInputQuarkusCreate_Success = []CfgTestInputQuarkusCreate{
	{input: quarkus.CreateQuarkusProjectConfig{}},
	{input: quarkus.CreateQuarkusProjectConfig{
		ProjectName: "my-project",
	}},
	{input: quarkus.CreateQuarkusProjectConfig{
		Extensions: "quarkus-jsonp,quarkus-smallrye-openapi",
	}},
	{input: quarkus.CreateQuarkusProjectConfig{
		ProjectName: "serverless-workflow-hello-world",
		Extensions:  "quarkus-jsonp,quarkus-smallrye-openapi",
		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: "io.quarkus.platform",
			QuarkusVersion:         "3.2.10.Final",
		},
	}},
}

var cfgTestInputQuarkusCreate_Fail = []CfgTestInputQuarkusCreate{
	{input: quarkus.CreateQuarkusProjectConfig{
		ProjectName: "wrong/project-name",
	}},
	{input: quarkus.CreateQuarkusProjectConfig{
		Extensions: "nonexistent-extension",
	}},
}

func transformQuarkusCreateCmdCfgToArgs(cfg quarkus.CreateQuarkusProjectConfig) []string {
	args := []string{"create"}
	if cfg.ProjectName != "" {
		args = append(args, "--name", cfg.ProjectName)
	}
	if cfg.Extensions != "" {
		args = append(args, "--extension", cfg.Extensions)
	}
	if cfg.DependenciesVersion.QuarkusPlatformGroupId != "" {
		args = append(args, "--quarkus-platform-group-id", cfg.DependenciesVersion.QuarkusPlatformGroupId)
	}
	if cfg.DependenciesVersion.QuarkusVersion != "" {
		args = append(args, "--quarkus-version", cfg.DependenciesVersion.QuarkusVersion)
	}
	return args
}

func GetQuarkusCreateProjectName(t *testing.T, config CfgTestInputQuarkusCreate) string {
	if config.input.ProjectName != "" {
		return config.input.ProjectName
	} else {
		projectDefaultName, err := LookupFlagDefaultValue("name", quarkus.NewCreateCommand())
		require.NoErrorf(t, err, "Error: %v", err)
		return projectDefaultName
	}
}

func TestQuarkusCreateProjectSuccess(t *testing.T) {
	for testIndex, test := range cfgTestInputQuarkusCreate_Success {
		t.Run(fmt.Sprintf("Test quarkus create project success index: %d", testIndex), func(t *testing.T) {
			defer CleanUpAndChdirTemp(t)
			RunQuarkusCreateTest(t, test)
		})
	}
}

func RunQuarkusCreateTest(t *testing.T, test CfgTestInputQuarkusCreate) string {
	var err error

	projectName := GetQuarkusCreateProjectName(t, test)
	projectDir := filepath.Join(TempTestsPath, projectName)

	// Run `quarkus create` command
	_, err = ExecuteKnWorkflowQuarkus(transformQuarkusCreateCmdCfgToArgs(test.input)...)
	require.NoErrorf(t, err, "Expected nil error, got: %v", err)

	// Check if the project directory was created
	require.DirExistsf(t, projectDir, "Expected project directory '%s' to be created", projectDir)

	// Check if the expected directories and files are present
	expectedDirectories := []string{
		"src/main/java",
		"src/main/resources",
		"src/main/docker",
		"src/main",
		"src",
		".mvn/wrapper",
		".mvn",
	}
	VerifyDirectoriesExist(t, projectDir, expectedDirectories)
	expectedFiles := []string{
		"src/main/resources/application.properties",
		"src/main/resources/workflow.sw.json",
		"src/main/docker/Dockerfile.legacy-jar",
		"src/main/docker/Dockerfile.jvm",
		"src/main/docker/Dockerfile.native",
		"src/main/docker/Dockerfile.native-micro",
		".mvn/wrapper/.gitignore",
		".mvn/wrapper/MavenWrapperDownloader.java",
		".mvn/wrapper/maven-wrapper.properties",
		".gitignore",
		"pom.xml",
		"README.md",
		".dockerignore",
		"mvnw.cmd",
		"mvnw",
	}
	VerifyFilesExist(t, projectDir, expectedFiles)

	// Verify the content of the file `workflow.sw.json`
	workflowFilePath := filepath.Join(projectDir, "src/main/resources/workflow.sw.json")
	workflowFileData, err := common.GetWorkflowTemplate(false)
	require.NoErrorf(t, err, "Error reading workflow template: %v", err)
	expectedFileContent := string(workflowFileData)
	VerifyFileContent(t, workflowFilePath, expectedFileContent)
	return projectName
}

func TestQuarkusCreateProjectFail(t *testing.T) {
	for testIndex, test := range cfgTestInputQuarkusCreate_Fail {
		t.Run(fmt.Sprintf("Test quarkus create project fail index: %d", testIndex), func(t *testing.T) {
			defer CleanUpAndChdirTemp(t)

			projectName := GetQuarkusCreateProjectName(t, test)
			projectDir := filepath.Join(TempTestsPath, projectName)

			// Run `quarkus create` command
			_, err := ExecuteKnWorkflowQuarkus(transformQuarkusCreateCmdCfgToArgs(test.input)...)
			require.Errorf(t, err, "Expected error, got nil")

			// Check if the project directory was not created
			require.NoDirExistsf(t, projectDir, "Expected project directory '%s' not to be created", projectDir)
		})
	}
}
