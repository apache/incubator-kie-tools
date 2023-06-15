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
	"os/exec"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/assert"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/command/quarkus"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
)

var cfgTestInputPrepareQuarkusCreateBuild = CfgTestInputQuarkusCreate{
	input: quarkus.CreateQuarkusProjectConfig{
		ProjectName: "new-project",
	},
}

type CfgTestInputQuarkusBuild struct {
	input quarkus.BuildCmdConfig
}

var cfgTestInputQuarkusBuild_Success = []CfgTestInputQuarkusBuild{
	{input: quarkus.BuildCmdConfig{
		Image: "dev.local/my-project",
	}},
	{input: quarkus.BuildCmdConfig{
		Image:      "my-user/my-project:1.0.0",
		Repository: "other-user",
		Tag:        "1.0.1",
	}},
	{input: quarkus.BuildCmdConfig{
		Image: "dev.local/my-project",
		Jib:   true,
	}},
	{input: quarkus.BuildCmdConfig{
		Image:     "dev.local/my-project",
		JibPodman: true,
	}},
	// {input: quarkus.BuildCmdConfig{
	// 	Image: "dev.local/my-project",
	// 	Jib:   true,
	// 	Push:  true,
	// }},
	// {input: quarkus.BuildCmdConfig{
	// 	Image: "dev.local/my-project",
	// 	Push:  true,
	// }},
}

func transformQuarkusBuildCmdCfgToArgs(cfg quarkus.BuildCmdConfig) []string {
	args := []string{"build"}
	if cfg.Image != "" {
		args = append(args, "--image", cfg.Image)
	}
	if cfg.ImageName != "" {
		args = append(args, "--image-name", cfg.ImageName)
	}
	if cfg.Registry != "" {
		args = append(args, "--image-registry", cfg.Registry)
	}
	if cfg.Repository != "" {
		args = append(args, "--image-repository", cfg.Repository)
	}
	if cfg.Tag != "" {
		args = append(args, "--image-tag", cfg.Tag)
	}
	if cfg.Jib == true {
		args = append(args, "--jib")
	}
	if cfg.JibPodman == true {
		args = append(args, "--jib-podman")
	}
	if cfg.Push == true {
		args = append(args, "--push")
	}
	if cfg.Test == true {
		args = append(args, "--test")
	}
	return args
}

func TestQuarkusBuildCommand(t *testing.T) {
	for testIndex, test := range cfgTestInputQuarkusBuild_Success {
		t.Run(fmt.Sprintf("Test build project success index: %d", testIndex), func(t *testing.T) {
			RunQuarkusBuildTest(t, cfgTestInputPrepareQuarkusCreateBuild, test, true)
		})
	}
}

func RunQuarkusBuildTest(t *testing.T, cfgTestInputCreate CfgTestInputQuarkusCreate, test CfgTestInputQuarkusBuild, cleanUp bool) {
	var err error

	// Create the project
	RunQuarkusCreateTest(t, cfgTestInputCreate, false)
	projectName := GetQuarkusCreateProjectName(t, cfgTestInputCreate)

	projectPath, err := os.Getwd()
	assert.NoError(t, err, "Expected nil error, got %v", err)
	fmt.Println("Current working directory:", projectPath)

	projectDir := filepath.Join(projectPath, projectName)
	dirExists := assert.DirExists(t, projectDir)

	err = os.Chdir(projectDir)
	assert.NoErrorf(t, err, "Expected nil error, got %v", err)

	// Run `build` command
	_, err = ExecuteKnWorkflowQuarkus(transformQuarkusBuildCmdCfgToArgs(test.input)...)
	assert.NoErrorf(t, err, "Expected nil error, got %v", err)

	assert.FileExists(t, filepath.Join("target", "kubernetes", "knative.yml"))

	expectedImageName := ExpectedImageName(test.input)
	var removeCmd *exec.Cmd
	if test.input.JibPodman {
		// Remove built image from podman
		removeCmd = exec.Command("podman", "image", "rm", expectedImageName) // podman only takes `rm` for removing images
	} else {
		// Remove built image from docker
		removeCmd = exec.Command("docker", "image", "rm", expectedImageName) // docker takes both `rm` and `remove` for removing images
	}
	fmt.Println("Removing image:", removeCmd.Args)
	err = removeCmd.Run()
	assert.NoErrorf(t, err, "Error when removing image: %s. Expected nil error, got %v", expectedImageName, err)

	os.Chdir(projectPath)
	if dirExists && cleanUp {
		common.DeleteFolderStructure(t, projectDir)
	}
}
