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
		Long: `
NAME
	{{.Name}} build - Build a Quarkus project and pushes the image to a registry
	
SYNOPSIS
	{{.Name}} build [-r|--registry] [-g|--group] [-i|--image-name]
					[-t|--tag] [-v|--verbose]
	
DESCRIPTION
	Builds a Quarkus workflow project and pushes a image to a remote registry. It doens't require Docker
	
	$ {{.Name}} build
	`,
		PreRunE: common.BindEnv("registry", "group", "image-name", "tag", "no-docker"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runBuild(cmd, args)
	}

	cmd.Flags().StringP("registry", "r", "docker.io", fmt.Sprintf("%s registry URL", cmd.Name()))
	cmd.Flags().StringP("group", "g", "quarkus", fmt.Sprintf("%s registry group", cmd.Name()))
	cmd.Flags().StringP("image-name", "i", "new-project", fmt.Sprintf("%s image name", cmd.Name()))
	cmd.Flags().StringP("tag", "t", "latest", fmt.Sprintf("%s image tag", cmd.Name()))

	cmd.Flags().Bool("no-docker", false, fmt.Sprintf("%s build using Jib extension", cmd.Name()))

	return cmd
}

func runBuild(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runBuildConfig(cmd)
	if err != nil {
		return fmt.Errorf("build config error %w", err)
	}

	if err := common.CheckJavaDependencies(); err != nil {
		return fmt.Errorf("checking dependencies: %w", err)
	}

	if !cfg.NoDocker {
		if err := common.CheckContainerRuntime(); err != nil {
			return fmt.Errorf("\"no-docker\" option is false: %w", err)
		}
	}

	if err := runAddExtension(cfg); err != nil {
		return fmt.Errorf("%w", err)
	}

	if err := runBuildImage(cfg); err != nil {
		return fmt.Errorf("%w", err)
	}

	fmt.Printf("Created and pushed an image to remote registry: %s/%s/%s:%s\n", cfg.Registry, cfg.Group, cfg.ImageName, cfg.Tag)
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

		NoDocker: viper.GetBool("no-docker"),
		Verbose:  viper.GetBool("verbose"),
	}
	return
}

func runAddExtension(cfg BuildConfig) error {
	var addExtension *exec.Cmd
	if cfg.NoDocker {
		fmt.Printf(" - Adding Quarkus Jib extension\n")
		addExtension = exec.Command("./mvnw", "quarkus:add-extension",
			"-Dextensions=container-image-jib")
	} else {
		fmt.Printf(" - Adding Quarkus Docker extension\n")
		addExtension = exec.Command("./mvnw", "quarkus:add-extension",
			"-Dextensions=container-image-docker")
	}

	if err := common.RunCommand(addExtension, cfg.Verbose, "adding quarkus extension failed with error"); err != nil {
		return fmt.Errorf("%w", err)
	}

	fmt.Printf("Quarkus extension was sucessufully add to the project\n")
	return nil
}

func runBuildImage(cfg BuildConfig) error {
	builder := "-Dquarkus.container-image.builder="
	if cfg.NoDocker {
		builder += "jib"
	} else {
		builder += "docker"
	}

	build := exec.Command("./mvnw", "package",
		"-Dquarkus.native.container-build=true",
		fmt.Sprintf("-Dquarkus.container-image.registry=%s", cfg.Registry),
		fmt.Sprintf("-Dquarkus.container-image.group=%s", cfg.Group),
		fmt.Sprintf("-Dquarkus.container-image.name=%s", cfg.ImageName),
		fmt.Sprintf("-Dquarkus.container-image.tag=%s", cfg.Tag),
		builder,
		"-Dquarkus.container-image.push=true",
	)

	if err := common.RunCommand(build, cfg.Verbose, "build command failed with error"); err != nil {
		return fmt.Errorf("%w", err)
	}

	fmt.Printf("Build success\n")
	return nil
}
