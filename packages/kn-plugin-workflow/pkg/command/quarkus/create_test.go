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

package quarkus

import (
	"fmt"
	"os"
	"os/exec"
	"strconv"
	"testing"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/spf13/afero"
)

type testCreate struct {
	input           CreateQuarkusProjectConfig
	existingProject bool
}

var testRunCreateSuccess = []testCreate{
	{input: CreateQuarkusProjectConfig{ProjectName: "new-project", Extensions: ""}},
	{input: CreateQuarkusProjectConfig{
		ProjectName: "second-project",
		Extensions:  "extension-name",
		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: "io.quarkus.platform",
			QuarkusVersion:         "2.16.0.Final",
		},
	}},
}
var testRunCreateFail = []testCreate{
	{input: CreateQuarkusProjectConfig{ProjectName: "test-data"}, existingProject: true},
	{input: CreateQuarkusProjectConfig{ProjectName: "wrong*project/name"}},
}

func fakeRunCreate(testIndex int) func(command string, args ...string) *exec.Cmd {
	return func(command string, args ...string) *exec.Cmd {
		cs := []string{"-test.run=TestHelperRunCreate", "--", command}
		cs = append(cs, args...)
		cmd := exec.Command(os.Args[0], cs...)
		cmd.Env = []string{fmt.Sprintf("GO_TEST_HELPER_RUN_CREATE_IMAGE=%d", testIndex)}
		return cmd
	}
}

func TestHelperRunCreate(t *testing.T) {
	testIndex, err := strconv.Atoi(os.Getenv("GO_TEST_HELPER_RUN_CREATE_IMAGE"))
	if err != nil {
		return
	}
	fmt.Fprintf(os.Stdout, "%v", testRunCreateSuccess[testIndex].input.ProjectName)
	os.Exit(0)
}

func TestRunCreate_Success(t *testing.T) {
	for testIndex, test := range testRunCreateSuccess {
		common.ExecCommand = fakeRunCreate(testIndex)
		defer func() { common.ExecCommand = exec.Command }()

		err := runCreateProject(test.input)
		if err != nil {
			t.Errorf("Expected nil error, got %#v", err)
		}
	}
}

func TestRunCreate_Fail(t *testing.T) {
	common.FS = afero.NewMemMapFs()
	for testIndex, test := range testRunCreateFail {
		if test.existingProject == true {
			common.CreateFolderStructure(t, test.input.ProjectName)
		}
		common.ExecCommand = fakeRunCreate(testIndex)
		defer func() { common.ExecCommand = exec.Command }()

		err := runCreateProject(test.input)
		if err == nil {
			t.Errorf("Expected error, got pass")
		}
		if test.existingProject == true {
			common.DeleteFolderStructure(t, test.input.ProjectName)
		}
	}
}
