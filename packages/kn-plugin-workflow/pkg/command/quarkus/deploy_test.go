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
	"github.com/spf13/afero"
)

type testDeploy struct {
	input      DeployCmdConfig
	expected   bool
	createFile string
}

const defaultPath = "./target/kubernetes"

var testRunDeploy = []testDeploy{
	{input: DeployCmdConfig{Path: defaultPath}, expected: true, createFile: "kogito.yml"},
	{input: DeployCmdConfig{Path: "./different/folders"}, expected: true, createFile: "kogito.yml"},
	{input: DeployCmdConfig{Path: "different/folders"}, expected: true, createFile: "kogito.yml"},
	{input: DeployCmdConfig{}, expected: false, createFile: "test"},
	{input: DeployCmdConfig{}, expected: false},
}

func fakeRunDeploy(testIndex int) func(command string, args ...string) *exec.Cmd {
	return func(command string, args ...string) *exec.Cmd {
		cs := []string{"-test.run=TestHelperRunDeploy", "--", command}
		cs = append(cs, args...)
		cmd := exec.Command(os.Args[0], cs...)
		cmd.Env = []string{fmt.Sprintf("GO_TEST_HELPER_RUN_DEPLOY_IMAGE=%d", testIndex)}
		return cmd
	}
}

func TestHelperRunDeploy(t *testing.T) {
	testIndex, err := strconv.Atoi(os.Getenv("GO_TEST_HELPER_RUN_DEPLOY_IMAGE"))
	if err != nil {
		return
	}
	out := []string{"Test", strconv.Itoa(testIndex)}
	if testRunDeploy[testIndex].createFile != "" {
		out = append(out, "with creating", testRunDeploy[testIndex].createFile, "file")
	}
	fmt.Fprintf(os.Stdout, "%v", out)
	os.Exit(0)
}

func TestRunDeploy(t *testing.T) {
	common.FS = afero.NewMemMapFs()
	for testIndex, test := range testRunDeploy {
		common.ExecCommand = fakeRunDeploy(testIndex)
		defer func() { common.ExecCommand = exec.Command }()

		if test.createFile != "" {
			if test.input.Path == "" {
				test.input.Path = defaultPath
			}
			common.CreateFolderStructure(t, test.input.Path)
			common.CreateFileInFolderStructure(t, test.input.Path, test.createFile)
		}

		out, err := deployKnativeServiceAndEventingBindings(test.input)
		if err != nil {
			t.Errorf("Expected nil error, got %#v", err)
		}

		if out != test.expected {
			t.Errorf("Expected %v, got %v", test.expected, out)
		}

		if test.createFile != "" {
			common.DeleteFolderStructure(t, test.input.Path)
		}
	}
}
