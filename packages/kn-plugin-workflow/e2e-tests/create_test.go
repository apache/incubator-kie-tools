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

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
)

type CfgTestInputCreate struct {
	input command.CreateCmdConfig
}

var CfgTestInputCreate_Success = []CfgTestInputCreate{
	{input: command.CreateCmdConfig{}},
	{input: command.CreateCmdConfig{
		ProjectName: "my-project",
	}},
}

var CfgTestInputCreate_Fail = []CfgTestInputCreate{
	{input: command.CreateCmdConfig{
		ProjectName: "wrong/project-name",
	}},
}

func transformCreateCmdCfgToArgs(cfg command.CreateCmdConfig) []string {
	args := []string{"create"}
	if cfg.ProjectName != "" {
		args = append(args, "--name", cfg.ProjectName)
	}
	return args
}

func GetCreateProjectName(t *testing.T, config CfgTestInputCreate) string {
	if config.input.ProjectName != "" {
		return config.input.ProjectName
	} else {
		projectDefaultName, err := LookupFlagDefaultValue("name", command.NewCreateCommand())
		require.NoErrorf(t, err, "Error: %v", err)
		return projectDefaultName
	}
}

func TestCreateProjectSuccess(t *testing.T) {
	for testIndex, test := range CfgTestInputCreate_Success {
		t.Run(fmt.Sprintf("Test create project success index: %d", testIndex), func(t *testing.T) {
			defer CleanUpAndChdirTemp(t)
			RunCreateTest(t, test)
		})
	}
}

func RunCreateTest(t *testing.T, test CfgTestInputCreate) string {
	var err error

	projectName := GetCreateProjectName(t, test)
	projectDir := filepath.Join(TempTestsPath, projectName)

	// Run `create` command
	_, err = ExecuteKnWorkflow(transformCreateCmdCfgToArgs(test.input)...)
	require.NoErrorf(t, err, "Expected nil error, got: %v", err)

	// Check if the project directory was created
	require.DirExistsf(t, projectDir, "Expected project directory '%s' to be created", projectDir)

	expectedFiles := []string{"workflow.sw.json"}
	VerifyFilesExist(t, projectDir, expectedFiles)

	// Verify the content of the file `workflow.sw.json`
	workflowFileData, err := common.GetWorkflowTemplate(false)
	expectedFileContent := string(workflowFileData)
	VerifyFileContent(t, filepath.Join(projectDir, "workflow.sw.json"), expectedFileContent)

	return projectName
}

func TestCreateProjectFail(t *testing.T) {
	for testIndex, test := range CfgTestInputCreate_Fail {
		t.Run(fmt.Sprintf("Test create project fail index: %d", testIndex), func(t *testing.T) {
			defer CleanUpAndChdirTemp(t)
			projectName := test.input.ProjectName
			projectDir := filepath.Join(TempTestsPath, projectName)

			_, err := ExecuteKnWorkflow(transformCreateCmdCfgToArgs(test.input)...)
			require.Errorf(t, err, "Expected error, got nil")

			// Check if the project directory was not created
			require.NoDirExistsf(t, projectDir, "Expected project directory '%s' not to be created", projectDir)

			// Cleanup (if necessary)
			common.DeleteFolderStructure(t, projectDir)
		})
	}
}
