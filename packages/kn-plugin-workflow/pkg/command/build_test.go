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

package command

import (
	"fmt"
	"os"
	"os/exec"
	"strconv"
	"testing"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
)

type testBuildImage struct {
	expected string
	cfg      BuildCmdConfig
}

var tests = []testBuildImage{
	{expected: "test:latest", cfg: BuildCmdConfig{Image: "test"}},
	{expected: "docker.io/test:latest", cfg: BuildCmdConfig{Image: "docker.io/test:latest"}},
	{expected: "docker.io/repo/test:latest", cfg: BuildCmdConfig{Image: "docker.io/repo/test:latest"}},
	{expected: "quay.io/repo/test:0.0.0", cfg: BuildCmdConfig{Image: "quay.io/repo/test:0.0.0"}},
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
	fmt.Fprintf(os.Stdout, tests[testIndex].expected)
	os.Exit(0)
}

func TestRunBuildImage(t *testing.T) {
	for testIndex, test := range tests {
		common.ExecCommand = fakeRunBuildImage(testIndex)
		defer func() { common.ExecCommand = exec.Command }()

		out, err := runBuildImage(test.cfg)
		if err != nil {
			t.Errorf("Expected nil error, got %#v", err)
		}

		if string(out) != test.expected {
			t.Errorf("Expected %q, got %q", test.expected, out)
		}
	}
}
