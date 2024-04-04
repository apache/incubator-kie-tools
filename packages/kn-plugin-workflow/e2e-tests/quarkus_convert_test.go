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
	"os"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/require"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command/quarkus"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
)

var cfgTestInputCreateConvert = CfgTestInputCreate{
	input: command.CreateCmdConfig{},
}

var cfgTestInputQuarkusCreateConvert = CfgTestInputQuarkusCreate{
	input: quarkus.CreateQuarkusProjectConfig{},
}

type CfgTestInputQuarkusConvert struct {
	input quarkus.CreateQuarkusProjectConfig
}

var cfgTestInputQuarkusConvert_Success = []CfgTestInputQuarkusConvert{
	{input: quarkus.CreateQuarkusProjectConfig{}},
	{input: quarkus.CreateQuarkusProjectConfig{
		Extensions: "quarkus-jsonp,quarkus-smallrye-openapi",
	}},
	{input: quarkus.CreateQuarkusProjectConfig{
		Extensions: "quarkus-jsonp,quarkus-smallrye-openapi",
		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: "io.quarkus.platform",
			QuarkusVersion:         "3.2.10.Final",
		},
	}},
}

var cfgTestInputQuarkusConvert_Failed = []CfgTestInputQuarkusConvert{
	{input: quarkus.CreateQuarkusProjectConfig{
		Extensions: "nonexistent-extension",
	}},
}

var cfgTestInputQuarkusConvert_FailedAlreadyQuarkus = []CfgTestInputQuarkusConvert{
	{input: quarkus.CreateQuarkusProjectConfig{}},
}

func transformQuarkusConvertCmdCfgToArgs(t *testing.T, cfg quarkus.CreateQuarkusProjectConfig) []string {
	args := []string{"convert"}
	require.Empty(t, cfg.ProjectName, "The project name can not be set in the test of `quarkus convert`.")
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

func TestQuarkusConvertProjectSuccess(t *testing.T) {
	for testIndex, test := range cfgTestInputQuarkusConvert_Success {
		t.Run(fmt.Sprintf("Test quarkus convert project success index: %d", testIndex), func(t *testing.T) {
			defer CleanUpAndChdirTemp(t)
			RunQuarkusConvertTest(t, cfgTestInputCreateConvert, test)
		})
	}
}

func RunQuarkusConvertTest(t *testing.T, cfgTestInputCreateConvert CfgTestInputCreate, test CfgTestInputQuarkusConvert) string {
	var err error

	projectName := GetCreateProjectName(t, cfgTestInputCreateConvert)
	projectDir := filepath.Join(TempTestsPath, projectName)

	// Create the project
	RunCreateTest(t, cfgTestInputCreateConvert)

	err = os.Chdir(projectDir)
	require.NoErrorf(t, err, "Expected nil error, got %v", err)

	// Run `quarkus convert` command
	_, err = ExecuteKnWorkflowQuarkus(transformQuarkusConvertCmdCfgToArgs(t, test.input)...)
	require.NoErrorf(t, err, "Expected nil error, got %v", err)

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

func TestQuarkusConvertProjectFailed(t *testing.T) {
	for testIndex, test := range cfgTestInputQuarkusConvert_Failed {
		t.Run(fmt.Sprintf("Test quarkus convert project fail index: %d", testIndex), func(t *testing.T) {
			defer CleanUpAndChdirTemp(t)

			var err error
			projectName := GetCreateProjectName(t, cfgTestInputCreateConvert)
			projectDir := filepath.Join(TempTestsPath, projectName)

			// Create the project
			RunCreateTest(t, cfgTestInputCreateConvert)

			err = os.Chdir(projectDir)
			require.NoErrorf(t, err, "Expected nil error, got %v", err)

			// Run `quarkus convert` command
			_, err = ExecuteKnWorkflowQuarkus(transformQuarkusConvertCmdCfgToArgs(t, test.input)...)
			require.Errorf(t, err, "Expected error, got nil")

			common.DeleteFolderStructure(t, projectDir)
		})
	}
}

func TestQuarkusConvertProjectFailedAlreadyQuarkus(t *testing.T) {
	for testIndex, test := range cfgTestInputQuarkusConvert_Failed {
		t.Run(fmt.Sprintf("Test quarkus convert project fail already quarkus index: %d", testIndex), func(t *testing.T) {
			defer CleanUpAndChdirTemp(t)

			var err error
			projectName := GetQuarkusCreateProjectName(t, cfgTestInputQuarkusCreateConvert)
			projectDir := filepath.Join(TempTestsPath, projectName)

			// Create the project
			RunQuarkusCreateTest(t, cfgTestInputQuarkusCreateConvert)

			err = os.Chdir(projectDir)
			require.NoErrorf(t, err, "Expected nil error, got %v", err)

			// Run `quarkus convert` command
			_, err = ExecuteKnWorkflowQuarkus(transformQuarkusConvertCmdCfgToArgs(t, test.input)...)
			require.Errorf(t, err, "Expected error, got nil")
		})
	}
}
