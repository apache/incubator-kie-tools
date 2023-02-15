// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package utils

import (
	"bufio"
	"fmt"
	"os"
	"os/exec"
	"strconv"
	"strings"

	. "github.com/onsi/ginkgo/v2" //nolint:golint,revive
)

func warnError(err error) {
	fmt.Fprintf(GinkgoWriter, "warning: %v\n", err)
}

func OutputAllPods() error {
	cmd := exec.Command("kubectl", "get", "pods", "-A")
	podsOutput, err := Run(cmd)
	fmt.Println(string(podsOutput))
	return err
}

func OutputAllEvents(namespace string) error {
	cmd := exec.Command("kubectl", "get", "events", "-n", namespace)
	podsOutput, err := Run(cmd)
	fmt.Println(string(podsOutput))
	return err
}

func OutputDeployment(namespace, deployName string) error {
	cmd := exec.Command("kubectl", "get", "deploy", deployName, "-o", "yaml", "-n", namespace)
	podsOutput, err := Run(cmd)
	fmt.Println(string(podsOutput))
	return err
}

// Run executes the provided command within this context
func Run(cmd *exec.Cmd) ([]byte, error) {
	dir, _ := GetProjectDir()
	cmd.Dir = dir
	fmt.Fprintf(GinkgoWriter, "running dir: %s\n", cmd.Dir)

	// To allow make commands be executed from the project directory which is subdir on SDK repo
	// TODO:(user) You might does not need the following code
	if err := os.Chdir(cmd.Dir); err != nil {
		fmt.Fprintf(GinkgoWriter, "chdir dir: %s\n", err)
	}

	cmd.Env = append(os.Environ(), "GO111MODULE=on")
	command := strings.Join(cmd.Args, " ")
	fmt.Fprintf(GinkgoWriter, "running: %s\n", command)
	output, err := cmd.CombinedOutput()
	if err != nil {
		return output, fmt.Errorf("%s failed with error: (%v) %s", command, err, string(output))
	}

	return output, nil
}

// GetNonEmptyLines converts given command output string into individual objects
// according to line breakers, and ignores the empty elements in it.
func GetNonEmptyLines(output string) []string {
	var res []string
	elements := strings.Split(output, "\n")
	for _, element := range elements {
		if element != "" {
			res = append(res, element)
		}
	}

	return res
}

// GetProjectDir will return the directory where the project is
func GetProjectDir() (string, error) {
	wd, err := os.Getwd()
	if err != nil {
		return wd, err
	}
	wd = strings.Replace(wd, "/test/e2e", "", -1)
	return wd, nil
}

// StringToLines read lines from a string
func StringToLines(s string) (lines []string, err error) {
	scanner := bufio.NewScanner(strings.NewReader(s))
	for scanner.Scan() {
		lines = append(lines, scanner.Text())
	}
	err = scanner.Err()
	return
}

// GetOperatorImageName retrieves the operator image name to use
func GetOperatorImageName() (string, error) {
	if v, ok := os.LookupEnv("OPERATOR_IMAGE_NAME"); ok {
		return v, nil
	} else {
		return "", fmt.Errorf("Cannot find `OPERATOR_IMAGE_NAME` env variable needed for the tests")
	}
}

// IsDebugEnabled ...
func IsDebugEnabled() bool {
	if v, ok := os.LookupEnv("DEBUG"); ok {
		if debug, err := strconv.ParseBool(v); err == nil {
			return debug
		}
	}
	return false
}
