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
	"bufio"
	"fmt"
	"os/exec"
	"time"

	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewBuildCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "build",
		Short: "Build a Quarkus workflow project",
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runBuild(cmd, args)
	}

	cmd.Flags().StringP("registry", "i", "docker.io", fmt.Sprintf("%s registry URL", cmd.Name()))
	cmd.Flags().StringP("group", "i", "quarkus", fmt.Sprintf("%s registry group", cmd.Name()))
	cmd.Flags().StringP("image-name", "i", "new-project", fmt.Sprintf("%s image name", cmd.Name()))
	cmd.Flags().StringP("tag", "i", "latest", fmt.Sprintf("%s image tag", cmd.Name()))

	cmd.Flags().Bool("no-docker", false, fmt.Sprintf("%s build using JIB extension", cmd.Name()))

	return cmd
}

func runBuild(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runBuildConfig(cmd)
	if err != nil {
		return fmt.Errorf("build config error %w", err)
	}

	if err := common.CheckPreRequisitions(); err != nil {
		return fmt.Errorf("checking dependencies: %w", err)
	}

	fmt.Printf("Building Serverless Workflow project\n")

	// build with jib
	// ./mvnw quarkus:add-extension -Dextensions="container-image-jib"

	// build with docker
	// check docker
	// ./mvnw quarkus:add-extension -Dextensions="container-image-docker"

	// local image?

	var build *exec.Cmd
	build = exec.Command("./mvnw", "package",
		"-Dquarkus.native.container-build=true",
		"-Dquarkus.container-image.push=true",
		fmt.Sprintf("-Dquarkus.container-image.registry=%s", cfg.Registry),
		fmt.Sprintf("-Dquarkus.container-image.group=%s", cfg.Group),
		fmt.Sprintf("-Dquarkus.container-image.name=%s", cfg.ImageName),
		fmt.Sprintf("-Dquarkus.container-image.tag=%s", cfg.Tag),
	)

	stdout, _ := build.StdoutPipe()
	stderr, _ := build.StderrPipe()

	if err := build.Start(); err != nil {
		return fmt.Errorf("Build command failed with error: %w", err)
	}

	stdoutScanner := bufio.NewScanner(stdout)
	for stdoutScanner.Scan() {
		m := stdoutScanner.Text()
		fmt.Println(m)
	}

	stderrScanner := bufio.NewScanner(stderr)
	for stderrScanner.Scan() {
		m := stderrScanner.Text()
		fmt.Println(m)
	}

	if err := build.Wait(); err != nil {
		return fmt.Errorf("Build command failed with error: %w", err)
	}
	fmt.Printf("Created and pushed an image to remote registry: %s/%s/%s:%s\n", registry, registryGroup, imageName, imageTag)

	finish := time.Since(start)
	fmt.Printf("🚀 Build took: %s \n", finish)
	return nil
}

type BuildConfig struct {
	// Image options
	Registry  string // registry to be uploaded
	Group     string // group from registry
	ImageName string // image name
	Tag       string // tag

	// Build strategy options
	NoDocker bool

	// Plugin options
	Verbose bool
}

func runBuildConfig(cmd *cobra.Command) (cfg BuildConfig, err error) {
	cfg = BuildConfig{
		Registry:  viper.GetString("registry"),
		Group:     viper.GetString("group"),
		ImageName: viper.GetString("image-name"),
		Tag:       viper.GetString("tag"),

		NoDocker: viper.GetBool("verbose"),
		Verbose:  viper.GetBool("verbose"),
	}
	return
}

func runAddDockerExtension(cfg BuildConfig) {
	var build *exec.Cmd
	build = exec.Command("./mvnw", "package",
		"-Dquarkus.native.container-build=true",
		"-Dquarkus.container-image.push=true",
		fmt.Sprintf("-Dquarkus.container-image.registry=%s", cfg.Registry),
		fmt.Sprintf("-Dquarkus.container-image.group=%s", cfg.Group),
		fmt.Sprintf("-Dquarkus.container-image.name=%s", cfg.ImageName),
		fmt.Sprintf("-Dquarkus.container-image.tag=%s", cfg.Tag),
	)

	stdout, _ := build.StdoutPipe()
	stderr, _ := build.StderrPipe()

	if err := build.Start(); err != nil {
		return fmt.Errorf("Build command failed with error: %w", err)
	}

	stdoutScanner := bufio.NewScanner(stdout)
	for stdoutScanner.Scan() {
		m := stdoutScanner.Text()
		fmt.Println(m)
	}

	stderrScanner := bufio.NewScanner(stderr)
	for stderrScanner.Scan() {
		m := stderrScanner.Text()
		fmt.Println(m)
	}

	if err := build.Wait(); err != nil {
		return fmt.Errorf("Build command failed with error: %w", err)
	}
}
