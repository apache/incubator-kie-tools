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
	"testing"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
)

type testBuildImage struct {
	input string
	cfg   BuildCmdConfig
}

var tests = []testBuildImage{
	{input: "test:latest", cfg: BuildCmdConfig{Image: "test"}},
	{input: "docker.io/test:latest", cfg: BuildCmdConfig{Image: "docker.io/test:latest"}},
	{input: "docker.io/repo/test:latest", cfg: BuildCmdConfig{Image: "docker.io/repo/test:latest"}},
	{input: "quay.io/repo/test:0.0.0", cfg: BuildCmdConfig{Image: "quay.io/repo/test:0.0.0"}},
}

func fakeExecCommand(helperIndex int) func(command string, args ...string) *exec.Cmd {
	return func(command string, args ...string) *exec.Cmd {
		cs := []string{"-test.run=TestHelperProcess", "--", command}
		cs = append(cs, args...)
		cmd := exec.Command(os.Args[0], cs...)
		cmd.Env = []string{fmt.Sprintf("GO_WANT_HELPER_PROCESS=%d", helperIndex)}
		return cmd
	}

}

func TestRunBuildImage(t *testing.T) {
	for testIndex, test := range tests {
		common.ExecCommand = fakeExecCommand(testIndex)
		defer func() { common.ExecCommand = exec.Command }()

		out, err := runBuildImage(test.cfg)
		if err != nil {
			t.Errorf("Expected nil error, got %#v", err)
		}

		if string(out) != test.input {
			t.Errorf("Expected %q, got %q", test.input, out)
		}
	}
}

func TestHelperProcess(t *testing.T) {
	if os.Getenv("GO_WANT_HELPER_PROCESS") == "0" {
		fmt.Fprintf(os.Stdout, tests[0].input)
		os.Exit(0)
	} else if os.Getenv("GO_WANT_HELPER_PROCESS") == "1" {
		fmt.Fprintf(os.Stdout, tests[1].input)
		os.Exit(0)
	} else if os.Getenv("GO_WANT_HELPER_PROCESS") == "2" {
		fmt.Fprintf(os.Stdout, tests[2].input)
		os.Exit(0)
	} else if os.Getenv("GO_WANT_HELPER_PROCESS") == "3" {
		fmt.Fprintf(os.Stdout, tests[3].input)
		os.Exit(0)
	} else {
		return
	}
}
