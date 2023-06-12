//go:build it_tests

/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it_tests

import (
	"fmt"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/assert"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/command"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
)

type cfgTestInputCreate struct {
	input command.CreateCmdConfig
}

var cfgTestInputCreate_Success = []cfgTestInputCreate{
	{input: command.CreateCmdConfig{}},
	{input: command.CreateCmdConfig{
		ProjectName: "my-project",
	}},
}

var cfgTestInputCreate_Fail = []cfgTestInputCreate{
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

func TestCreateProjectSuccess(t *testing.T) {
	for testIndex, test := range cfgTestInputCreate_Success {
		t.Run(fmt.Sprintf("Test create project success index: %d", testIndex), func(t *testing.T) {
			RunCreateTest(t, test, true)
		})
	}
}

func RunCreateTest(t *testing.T, test cfgTestInputCreate, cleanUp bool) {
	var err error

	projectName := getCreateProjectName(t, test)
	projectDir := filepath.Join(projectName)

	// Run `create` command
	_, err = ExecuteKnWorkflow(transformCreateCmdCfgToArgs(test.input)...)
	assert.NoErrorf(t, err, "Expected nil error, got: %v", err)

	// Check if the project directory was created
	assert.DirExistsf(t, projectDir, "Expected project directory '%s' to be created", projectDir)

	expectedFiles := []string{"workflow.sw.json"}
	VerifyFilesExist(t, projectDir, expectedFiles)

	// Verify the content of the file `workflow.sw.json`
	workflowFileData, err := common.GetWorkflowTemplate()
	expectedFileContent := string(workflowFileData)
	VerifyFileContent(t, filepath.Join(projectDir, "workflow.sw.json"), expectedFileContent)

	if cleanUp {
		common.DeleteFolderStructure(t, projectDir)
	}
}

func TestCreateProjectFail(t *testing.T) {
	for testIndex, test := range cfgTestInputCreate_Fail {
		t.Run(fmt.Sprintf("Test create project fail index: %d", testIndex), func(t *testing.T) {
			projectName := test.input.ProjectName
			projectDir := filepath.Join(projectName)

			_, err := ExecuteKnWorkflow(transformCreateCmdCfgToArgs(test.input)...)
			assert.Errorf(t, err, "Expected error, got nil")

			// Check if the project directory was not created
			assert.NoDirExistsf(t, projectDir, "Expected project directory '%s' not to be created", projectDir)

			// Cleanup (if necessary)
			common.DeleteFolderStructure(t, projectDir)
		})
	}
}

func getCreateProjectName(t *testing.T, config cfgTestInputCreate) string {
	if config.input.ProjectName != "" {
		return config.input.ProjectName
	} else {
		projectDefaultName, err := LookupFlagDefaultValue("name", command.NewCreateCommand())
		assert.NoErrorf(t, err, "Error: %v", err)
		return projectDefaultName
	}
}
