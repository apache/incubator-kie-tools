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
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/require"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command/quarkus"
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
	//{input: quarkus.BuildCmdConfig{
	//	Image:     "dev.local/my-project",
	//	JibPodman: true,
	//}},
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
			defer CleanUpAndChdirTemp(t)
			RunQuarkusBuildTest(t, cfgTestInputPrepareQuarkusCreateBuild, test, true)
		})
	}
}

func RunQuarkusBuildTest(t *testing.T, cfgTestInputQuarkusCreate CfgTestInputQuarkusCreate, test CfgTestInputQuarkusBuild, cleanUp bool) string {
	var err error

	// Create the project
	projectName := RunQuarkusCreateTest(t, cfgTestInputQuarkusCreate)
	projectDir := filepath.Join(TempTestsPath, projectName)

	err = os.Chdir(projectDir)
	require.NoErrorf(t, err, "Expected nil error, got %v", err)

	// Run `quarkus build` command
	args := transformQuarkusBuildCmdCfgToArgs(test.input)
	_, err = ExecuteKnWorkflowQuarkus(args...)
	require.NoErrorf(t, err, "Expected nil error, got %v", err)

	require.FileExists(t, filepath.Join("target", "kubernetes", "knative.yml"))

	// Clean up images from docker and podman
	if cleanUp {
		CleanUpDockerPodman(t, test)
	}

	return projectName
}

func CleanUpDockerPodman(t *testing.T, test CfgTestInputQuarkusBuild) {
	var err error
	expectedImageName := ExpectedImageName(test.input)
	var removeCmd *exec.Cmd
	if test.input.JibPodman {
		// Remove built image from podman
		removeCmd = exec.Command("podman", "image", "rm", expectedImageName) // podman only takes `rm` for removing images
	} else {
		// Remove built image from docker
		removeCmd = exec.Command("docker", "image", "rm", expectedImageName) // docker takes both `rm` and `remove` for removing images
	}
	t.Log("Removing image:", removeCmd.Args)
	out, err := removeCmd.Output()
	fmt.Print(string(out))
	require.NoErrorf(t, err, "Error when removing image: %s. Expected nil error, got %v", expectedImageName, err)
}
