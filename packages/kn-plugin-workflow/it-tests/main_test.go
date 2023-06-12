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
	"os"
	"path/filepath"
	"runtime"
	"strings"
	"testing"
)

var parentPath string
var KnExecutable string

func TestMain(m *testing.M) {
	// Create temp directory for tests and switch inside it
	workingPath, _ := os.Getwd()
	parentPath = filepath.Dir(workingPath)
	KnExecutable = build()

	tempDirName := "temp-tests"
	_, err := os.Stat(tempDirName)
	if os.IsNotExist(err) == false {
		cleanUpTemp(workingPath, tempDirName)
	}
	setUpTempDir(tempDirName)

	// Run tests
	exitCode := m.Run()

	// Cleanup after tests
	cleanUpTemp(workingPath, tempDirName)

	os.Exit(exitCode)
}

func setUpTempDir(tempDirName string) {
	var err error
	err = os.Mkdir(tempDirName, 0750)
	if err != nil {
		fmt.Printf("Failed to create temp directory: %v", err)
		os.Exit(1)
	}
	err = os.Chdir(tempDirName)
	if err != nil {
		fmt.Printf("Failed to change directory to temp: %v", err)
		os.Exit(1)
	}
}

func cleanUpTemp(workingPath string, tempDirName string) {
	var err error
	err = os.Chdir(workingPath)
	if err != nil {
		fmt.Printf("Failed to change directory back from temp: %v", err)
		os.Exit(1)
	}
	err = os.RemoveAll(tempDirName)
	if err != nil {
		fmt.Printf("Failed to remove temp directory: %v", err)
		os.Exit(1)
	}
}

func build() string {
	err := ExecuteCommand("pnpm", "build:dev")
	if err != nil {
		fmt.Println("Failed to build:", err)
		os.Exit(1)
	}

	binaryDirPath := filepath.Join(parentPath, "dist")
	buildOutput := filepath.Join(binaryDirPath, "/kn-workflow-")
	switch osAndArch := strings.ToLower(runtime.GOOS); osAndArch {
	case "darwin":
		switch arch := strings.ToLower(runtime.GOARCH); arch {
		case "amd64":
			buildOutput += "darwin-amd64"
		case "arm64":
			buildOutput += "darwin-arm64"
		default:
			fmt.Println("Unsupported architecture:", arch)
			os.Exit(1)
		}
	case "linux":
		buildOutput += "linux-amd64"
	case "windows":
		switch arch := strings.ToLower(runtime.GOARCH); arch {
		case "amd64":
			buildOutput += "windows-amd64.exe"
		default:
			fmt.Println("Unsupported architecture:", arch)
			os.Exit(1)
		}
	default:
		fmt.Println("Unsupported OS:", osAndArch)
		os.Exit(1)
	}

	executable := buildOutput
	err = ExecuteCommand("chmod", "+x", executable)
	if err != nil {
		fmt.Println("Failed to make the built executable file executable:", err)
		os.Exit(1)
	}

	return executable
}
