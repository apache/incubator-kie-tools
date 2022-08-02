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

var imageName = []string{
	"test:latest",
	"docker.io/test:latest",
	"docker.io/repo/test:latest",
	"quay.io/repo/test:0.0.0",
}

var cfg = []BuildCmdConfig{
	{Image: "test"},
	{Image: "docker.io/test:latest"},
	{Image: "docker.io/repo/test:latest"},
	{Image: "quay.io/repo/test:0.0.0"},
}

func fakeExecCommand(helperIndex string) func(command string, args ...string) *exec.Cmd {
	return func(command string, args ...string) *exec.Cmd {
		cs := []string{"-test.run=TestHelperProcess", "--", command}
		cs = append(cs, args...)
		cmd := exec.Command(os.Args[0], cs...)
		cmd.Env = []string{fmt.Sprintf("GO_WANT_HELPER_PROCESS=%s", helperIndex)}
		return cmd
	}

}

func TestRunBuildImage(t *testing.T) {
	common.ExecCommand = fakeExecCommand("0")
	defer func() { common.ExecCommand = exec.Command }()

	out, err := runBuildImage(cfg[0])
	if err != nil {
		t.Errorf("Expected nil error, got %#v", err)
	}

	if string(out) != imageName[0] {
		t.Errorf("Expected %q, got %q", imageName[0], out)
	}
}

func TestRunBuildImage1(t *testing.T) {
	common.ExecCommand = fakeExecCommand("1")
	defer func() { common.ExecCommand = exec.Command }()

	out, err := runBuildImage(cfg[1])
	if err != nil {
		t.Errorf("Expected nil error, got %#v", err)
	}

	if string(out) != imageName[1] {
		t.Errorf("Expected %q, got %q", imageName[1], out)
	}
}

func TestRunBuildImage2(t *testing.T) {
	common.ExecCommand = fakeExecCommand("2")
	defer func() { common.ExecCommand = exec.Command }()

	out, err := runBuildImage(cfg[2])
	if err != nil {
		t.Errorf("Expected nil error, got %#v", err)
	}

	if string(out) != imageName[2] {
		t.Errorf("Expected %q, got %q", imageName[2], out)
	}
}

func TestRunBuildImage3(t *testing.T) {
	common.ExecCommand = fakeExecCommand("3")
	defer func() { common.ExecCommand = exec.Command }()

	out, err := runBuildImage(cfg[3])
	if err != nil {
		t.Errorf("Expected nil error, got %#v", err)
	}

	if string(out) != imageName[3] {
		t.Errorf("Expected %q, got %q", imageName[3], out)
	}
}

func TestHelperProcess(t *testing.T) {
	if os.Getenv("GO_WANT_HELPER_PROCESS") == "0" {
		fmt.Fprintf(os.Stdout, imageName[0])
		os.Exit(0)
	} else if os.Getenv("GO_WANT_HELPER_PROCESS") == "1" {
		fmt.Fprintf(os.Stdout, imageName[1])
		os.Exit(0)
	} else if os.Getenv("GO_WANT_HELPER_PROCESS") == "2" {
		fmt.Fprintf(os.Stdout, imageName[2])
		os.Exit(0)
	} else if os.Getenv("GO_WANT_HELPER_PROCESS") == "3" {
		fmt.Fprintf(os.Stdout, imageName[3])
		os.Exit(0)
	} else {
		return
	}

}
