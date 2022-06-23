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
	"strings"
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
		PreRunE: common.BindEnv("image", "registry", "group", "name", "tag", "jib", "push"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runBuild(cmd, args)
	}

	cmd.Flags().StringP("image", "i", "", fmt.Sprintf("%s image URL", cmd.Name()))
	cmd.Flags().StringP("registry", "r", "", fmt.Sprintf("%s registry URL", cmd.Name()))
	cmd.Flags().StringP("group", "g", "", fmt.Sprintf("%s registry group", cmd.Name()))
	cmd.Flags().StringP("name", "n", "", fmt.Sprintf("%s image name", cmd.Name()))
	cmd.Flags().StringP("tag", "t", "", fmt.Sprintf("%s image tag", cmd.Name()))

	cmd.Flags().Bool("jib", false, fmt.Sprintf("%s build using Jib extension", cmd.Name()))
	cmd.Flags().Bool("push", false, fmt.Sprintf("%s push", cmd.Name()))

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

	if err := common.CheckContainerRuntime(); err != nil {
		return fmt.Errorf("docker is not available: %w", err)
	}

	if err := runAddExtension(cfg); err != nil {
		return fmt.Errorf("%w", err)
	}

	if err := runBuildImage(cfg); err != nil {
		return fmt.Errorf("%w", err)
	}

	finish := time.Since(start)
	fmt.Printf("🚀 Build took: %s \n", finish)
	return nil
}

type BuildConfig struct {
	// Image options
	Image     string // image
	Registry  string // registry to be uploaded
	Group     string // group from registry
	ImageName string // image name
	Tag       string // tag

	// Build strategy options
	Jib  bool // use Jib extension to build the image and push it to a remote registry
	Push bool // choose to push an image to a remote registry or not (Docker only)

	// Plugin options
	Verbose bool
}

func runBuildConfig(cmd *cobra.Command) (cfg BuildConfig, err error) {
	cfg = BuildConfig{
		Image:     viper.GetString("image"),
		Registry:  viper.GetString("registry"),
		Group:     viper.GetString("group"),
		ImageName: viper.GetString("name"),
		Tag:       viper.GetString("tag"),

		Jib:  viper.GetBool("jib"),
		Push: viper.GetBool("push"),

		Verbose: viper.GetBool("verbose"),
	}
	return
}

func runAddExtension(cfg BuildConfig) error {
	var addExtension *exec.Cmd
	if cfg.Jib {
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

	fmt.Printf("✅ Quarkus extension was sucessufully add to the project\n")
	return nil
}

func runBuildImage(cfg BuildConfig) error {
	registry, group, name, tag := getImageConfig(cfg)
	builderConfig := getBuilderConfig(cfg)
	pushConfig := getPushConfig(cfg)

	build := exec.Command("./mvnw", "package", "-Dquarkus.container-image.build=true",
		fmt.Sprintf("-Dquarkus.container-image.registry=%s", registry),
		fmt.Sprintf("-Dquarkus.container-image.group=%s", group),
		fmt.Sprintf("-Dquarkus.container-image.name=%s", name),
		fmt.Sprintf("-Dquarkus.container-image.tag=%s", tag),
		builderConfig,
		pushConfig,
	)

	if err := common.RunCommand(build, cfg.Verbose, "build command failed with error"); err != nil {
		return fmt.Errorf("%w", err)
	}

	fmt.Printf("Created and pushed an image to remote registry: %s\n", getImageName(registry, group, name, tag))

	fmt.Println("Build success")
	return nil
}

func getImageConfig(cfg BuildConfig) (string, string, string, string) {
	imageTagArray := strings.Split(cfg.Image, ":")
	imageArray := strings.SplitN(imageTagArray[0], "/", 3)

	var registry = "docker.io"
	if len(cfg.Registry) > 0 {
		registry = cfg.Registry
	} else if len(imageArray) > 2 {
		registry = imageArray[0]
	}

	var group = ""
	if len(cfg.Group) > 0 {
		group = cfg.Group
	} else if len(imageArray) == 2 {
		group = imageArray[0]
	} else if len(imageArray) == 3 {
		group = imageArray[1]
	}

	var name = ""
	if len(cfg.ImageName) > 0 {
		name = cfg.ImageName
	} else if len(imageArray) == 1 {
		name = imageArray[0]
	} else if len(imageArray) == 2 {
		name = imageArray[1]
	} else if len(imageArray) == 3 {
		name = imageArray[2]
	}

	var tag = "latest"
	if len(cfg.Tag) > 0 {
		tag = cfg.Tag
	} else if len(imageTagArray) > 1 && len(imageTagArray[1]) > 0 {
		tag = imageTagArray[1]
	}

	return registry, group, name, tag
}

func getImageName(registry string, group string, name string, tag string) string {
	if len(group) == 0 {
		return fmt.Sprintf("%s/%s:%s", registry, name, tag)
	}
	return fmt.Sprintf("%s/%s/%s:%s", registry, group, name, tag)
}

func getBuilderConfig(cfg BuildConfig) string {
	builder := "-Dquarkus.container-image.builder="
	if cfg.Jib {
		builder += "jib"
	} else {
		builder += "docker"
	}

	return builder
}

func getPushConfig(cfg BuildConfig) string {
	push := "-Dquarkus.container-image.push="
	if cfg.Push {
		push += "true"
	} else {
		push += "false"
	}

	return push
}
