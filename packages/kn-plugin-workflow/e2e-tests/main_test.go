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
	"flag"
	"fmt"
	"os"
	"path/filepath"
	"runtime"
	"strings"
	"testing"
	"time"
)

var parentPath string
var TempTestsPath string
var KnExecutable string

var TestPrintCmdOutput = flag.Bool("logs", true, "Print command output during tests")


func TestMain(m *testing.M) {

	// Create temp directory for tests and switch inside it
	workingPath, _ := os.Getwd()
	parentPath = filepath.Dir(workingPath)
	tempDirName := "temp-tests"
	if fileExists(tempDirName) {
		cleanUpTemp(workingPath, tempDirName)
	}
	setUpTempDir(tempDirName)
	TempTestsPath = filepath.Join(workingPath, tempDirName)

	KnExecutable = getPlatformSpecificExecutablePath()

	checkAndBuildExecutable()

	InstallOperator()
	// Run tests
	exitCode := m.Run()

	UninstallOperator()

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

func getPlatformSpecificExecutablePath() string {
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
	return buildOutput
}

func checkAndBuildExecutable() {
	// Check if rebuilding the executable is needed
	executableExists := fileExists(KnExecutable)
	executableIsExecutable := isExecAny(KnExecutable)
	lastBuildTimestamp, errTimestamp := getDistFolderTimestamp(filepath.Join(parentPath, "dist"))
	codeModified, errModified := areSourceFilesModifiedSince(lastBuildTimestamp)
	if executableExists == false || executableIsExecutable == false || codeModified == true || errTimestamp != nil || errModified != nil {
		buildDev() // Build the executable for current platform (`build:dev`)
	}
}

func getDistFolderTimestamp(path string) (time.Time, error) {
	fileInfo, err := os.Stat(path)
	if err != nil {
		return time.Time{}, err
	}

	return fileInfo.ModTime(), nil
}

func areSourceFilesModifiedSince(timestamp time.Time) (bool, error) {
	excludedFolders := []string{"e2e-tests", "dist-tests", "dist"}
	modificationsDetected := false

	err := filepath.Walk(parentPath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}

		if info.IsDir() {
			for _, excludedFolder := range excludedFolders {
				if strings.Contains(path, excludedFolder) {
					return filepath.SkipDir
				}
			}
			return nil
		}

		modifiedTime := info.ModTime()
		if modifiedTime.After(timestamp) {
			modificationsDetected = true
			return filepath.SkipDir
		}

		return nil
	})

	if err != nil {
		return false, err
	}

	return modificationsDetected, nil
}

func buildDev() {
	err := ExecuteCommand("pnpm", "build:dev")
	if err != nil {
		fmt.Println("Failed to build:", err)
		os.Exit(1)
	}

	err = ExecuteCommand("chmod", "+x", KnExecutable)
	if err != nil {
		fmt.Println("Failed to make the built executable file executable:", err)
		os.Exit(1)
	}

	return
}

func fileExists(filePath string) bool {
	_, err := os.Stat(filePath)
	return err == nil
}

func isExecAny(filePath string) bool {
	fileInfo, _ := os.Stat(filePath)
	fileMode := fileInfo.Mode()
	return fileMode&0111 != 0
}
