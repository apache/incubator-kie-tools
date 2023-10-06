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
	"bytes"
	"fmt"
	"io"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"syscall"
	"testing"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/command/quarkus"
	"github.com/spf13/cobra"
	"github.com/stretchr/testify/require"
)

// ExecuteCommand executes a command with the given arguments and returns an error if the command fails.
func ExecuteCommand(command string, args ...string) error {
	cmd := exec.Command(command, args...)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}

// ExecuteKnWorkflow executes the 'kn-workflow' CLI tool with the given arguments and returns the command's output and possible error message.
func ExecuteKnWorkflow(args ...string) (string, error) {
	cmd := exec.Command(KnExecutable)
	return executeCommandWithOutput(cmd, args...)
}

// ExecuteKnWorkflowQuarkus executes the 'kn-workflow' CLI tool with 'quarkus' command with the given arguments and returns the command's output and possible error message.
func ExecuteKnWorkflowQuarkus(args ...string) (string, error) {
	newArgs := append([]string{"quarkus"}, args...)
	cmd := exec.Command(KnExecutable)
	return executeCommandWithOutput(cmd, newArgs...)
}

// ExecuteKnWorkflowWithCmd executes the 'kn-workflow' CLI tool with the given arguments using the provided command and returns the command's output and possible error message.
func ExecuteKnWorkflowWithCmd(cmd *exec.Cmd, args ...string) (string, error) {
	return executeCommandWithOutput(cmd, args...)
}

// ExecuteKnWorkflowQuarkusWithCmd executes the 'kn-workflow' CLI tool with 'quarkus' command with the given arguments using the provided command and returns the command's output and possible error message.
func ExecuteKnWorkflowQuarkusWithCmd(cmd *exec.Cmd, args ...string) (string, error) {
	newArgs := append([]string{"quarkus"}, args...)
	return executeCommandWithOutput(cmd, newArgs...)
}

// executeCommandWithOutput executes a command with the given arguments using the provided command and captures its standard output and error streams.
// It returns the combined standard output as a string and an error if the command fails.
// It also prints out the standard output to the console if 'e2e_tests.testPrintCmdOutput' is set to 'true'.
func executeCommandWithOutput(cmd *exec.Cmd, args ...string) (string, error) {
	cmd.Args = append([]string{cmd.Path}, args...)
	var stdout bytes.Buffer
	var stderr bytes.Buffer
	if *TestPrintCmdOutput {
		cmd.Stdout = io.MultiWriter(os.Stdout, &stdout)
	} else {
		cmd.Stdout = &stdout
	}
	cmd.Stderr = &stderr
	err := cmd.Run()
	if err != nil {
		return stdout.String(), err
	}
	return stdout.String(), nil
}

// VerifyFileContent verifies that the content of a file matches the expected content.
func VerifyFileContent(t *testing.T, filePath string, expected string) {
	actual, err := os.ReadFile(filePath)
	require.NoErrorf(t, err, "Failed to read file: %s", filePath)
	require.Equalf(t, expected, string(actual), "The content of the file '%s' is different than expected", filePath)
}

// VerifyDirectoriesExist verifies that the specified directories exist within the given base directory.
func VerifyDirectoriesExist(t *testing.T, baseDir string, directories []string) {
	for _, dir := range directories {
		dirPath := filepath.Join(baseDir, dir)
		require.DirExistsf(t, dirPath, "Expected directory '%s' to be present", dirPath)
	}
}

// VerifyFilesExist verifies that the specified files exist within the given base directory.
func VerifyFilesExist(t *testing.T, baseDir string, files []string) {
	for _, file := range files {
		filePath := filepath.Join(baseDir, file)
		require.FileExistsf(t, filePath, "Expected file '%s' to be present", filePath)
	}
}

// ExpectedImageName returns the expected image name based on the provided quarkus.BuildCmdConfig.
func ExpectedImageName(cfg quarkus.BuildCmdConfig) string {
	var outputName string
	if cfg.Repository != "" {
		splitter := func(r rune) bool {
			return r == '/' || r == ':'
		}
		inputName := strings.FieldsFunc(cfg.Image, splitter)
		outputNameArray := []string{inputName[0], cfg.Repository, inputName[1]}
		outputName = strings.Join(outputNameArray, "/")
	} else {
		outputName = cfg.Image
	}
	if cfg.Tag != "" {
		outputName = outputName + ":" + cfg.Tag
	}
	return outputName
}

// LookupFlagDefaultValue looks up the default value of a flag within a given command.
func LookupFlagDefaultValue(flagName string, createCmd *cobra.Command) (string, error) {
	flag := createCmd.Flags().Lookup(flagName)
	if flag == nil {
		return "", fmt.Errorf("flag '%s' not found", flagName)
	}
	return flag.DefValue, nil
}

// IsSignalInterrupt checks if the given error is caused by a signal interrupt.
func IsSignalInterrupt(err error) bool {
	if exitErr, ok := err.(*exec.ExitError); ok {
		if status, ok := exitErr.Sys().(syscall.WaitStatus); ok {
			return status.Signaled() && status.Signal() == os.Interrupt
		}
	}
	return false
}

// cleanUpFolder removes the folder at the given path and recreates it with permissions set to 0750.
func cleanUpFolder(t *testing.T, path string) {
	var err error
	err = os.RemoveAll(path)
	if err != nil {
		t.Errorf("failed to delete '%s' folder: %v", path, err)
	}
	err = os.Mkdir(path, 0750)
	if err != nil {
		t.Errorf("failed to create '%s' create: %v", path, err)
	}
}

// CleanUpAndChdirTemp cleans up the 'temp-tests' folder by removing its contents and changes the current directory to it.
func CleanUpAndChdirTemp(t *testing.T) {
	cleanUpFolder(t, TempTestsPath)
	err := os.Chdir(TempTestsPath)
	if err != nil {
		t.Errorf("failed to change directory to temp: %v", err)
		os.Exit(1)
	}
}
