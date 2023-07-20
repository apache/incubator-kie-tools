/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
)

type testBuildImage struct {
	input    BuildCmdConfig
	expected string
}

var testsRunBuildImageSuccess = []testBuildImage{
	{input: BuildCmdConfig{Image: "test", Jib: true}, expected: "test:latest"},
	{input: BuildCmdConfig{Image: "docker.io/test:latest", JibPodman: true}, expected: "docker.io/test:latest"},
	{input: BuildCmdConfig{Image: "docker.io/repo/test:latest", Push: true}, expected: "docker.io/repo/test:latest"},
	{input: BuildCmdConfig{Image: "quay.io/repo/test:0.0.0", Test: true}, expected: "quay.io/repo/test:0.0.0"},

	{input: BuildCmdConfig{Image: "test", ImageName: "abcd"}, expected: "abcd:latest"},
	{input: BuildCmdConfig{Image: "docker.io/test", Registry: "quay.io"}, expected: "quay.io/test:latest"},
	{input: BuildCmdConfig{Image: "test", Repository: "myuser"}, expected: "myuser/test:latest"},
	{input: BuildCmdConfig{Image: "quay.io/test", Repository: "myuser"}, expected: "quay.io/myuser/test:latest"},
	{input: BuildCmdConfig{Image: "test:0.0.0", Tag: "0.0.1"}, expected: "test:0.0.1"},
}

var testsRunBuildImageFail = []testBuildImage{
	{input: BuildCmdConfig{Image: ""}, expected: ""},
	{input: BuildCmdConfig{Image: "1"}, expected: ""},
	{input: BuildCmdConfig{Image: "-test"}, expected: ""},
	{input: BuildCmdConfig{Image: "test.abc"}, expected: ""},
	{input: BuildCmdConfig{Image: "myuser/1"}, expected: ""},
	{input: BuildCmdConfig{Image: "test", ImageName: "1"}, expected: ""},
}

func fakeRunBuildImage(testIndex int) func(command string, args ...string) *exec.Cmd {
	return func(command string, args ...string) *exec.Cmd {
		cs := []string{"-test.run=TestHelperRunBuildImage", "--", command}
		cs = append(cs, args...)
		cmd := exec.Command(os.Args[0], cs...)
		cmd.Env = []string{fmt.Sprintf("GO_TEST_HELPER_RUN_BUILDER_IMAGE=%d", testIndex)}
		return cmd
	}
}

func TestHelperRunBuildImage(t *testing.T) {
	testIndex, err := strconv.Atoi(os.Getenv("GO_TEST_HELPER_RUN_BUILDER_IMAGE"))
	if err != nil {
		return
	}
	fmt.Fprintf(os.Stdout, testsRunBuildImageSuccess[testIndex].expected)
	os.Exit(0)
}

func TestRunBuildImage_Success(t *testing.T) {
	for testIndex, test := range testsRunBuildImageSuccess {
		common.ExecCommand = fakeRunBuildImage(testIndex)
		defer func() { common.ExecCommand = exec.Command }()

		out, err := runBuildImage(test.input)
		if err != nil {
			t.Errorf("Expected nil error, got %#v", err)
		}

		if string(out) != test.expected {
			t.Errorf("Expected %q, got %q", test.expected, out)
		}
	}
}

func TestRunBuildImage_Fail(t *testing.T) {
	for testIndex, test := range testsRunBuildImageFail {
		common.ExecCommand = fakeRunBuildImage(testIndex)
		defer func() { common.ExecCommand = exec.Command }()

		out, err := runBuildImage(test.input)
		if err == nil {
			t.Errorf("Expected error, got %#v", out)

		}
	}
}
