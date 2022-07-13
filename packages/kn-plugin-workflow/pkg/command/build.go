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
	"strconv"
	"strings"
	"time"

	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type BuildConfig struct {
	// Image options
	Image      string // full image name
	Registry   string // image registry (overrides image name)
	Repository string // image repository (overrides image name)
	ImageName  string // image name (overrides image name)
	Tag        string // image tag (overrides image name)

	// Build strategy options
	Jib       bool // use Jib extension to build the image and push it to a remote registry
	JibPodman bool // use Jib extension to build the image and save it in podman
	Push      bool // choose to push an image to a remote registry or not (Docker only)

	// Plugin options
	Verbose bool
}

func NewBuildCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "build",
		Short: "Build a Kogito Serverless Workflow project and generate a container image",
		Long: `
	Builds a Kogito Serverless Workflow project in the current directory 
	resulting in a container image.  
	By default the resultant container image will have the project name. It can be 
	overriten with the --image or with others image options, see help for more information.

	During the build, a knative.yml file will be generated on the ./target/kubernetes folder.
	If your workflow uses eventing, an additional kogito.yml is also generated.
	The deploy command uses both these files.

	Authentication is required if you want to push the resultant image to a private registry.
	To authenticate to your registry, use "docker login" or any other equivalent method.
`,
		Example: `
	# Build from the local directory
	# The full image name will be determined automatically based on the
	# project's directory name
	{{.Name}} build
	
	# Build from the local directory, specifying the full image name
	{{.Name}} build --image quay.io/myuser/myworkflow:1.0
	
	# Build from the local directory, specifying the full image name and pushing
	# it to the remote registry (authentication can be necessary, use docker login)
	# NOTE: If no registry is specfied in the image full name, quay.io will be used.
	{{.Name}} build --image quay.io/mysuer/myworkflow:1.0 --push
	
	# Build from the local directory, passing separately image options
	{{.Name}} build --image-registry docker.io --image-repository myuser --image-name myworkflow --image-tag 1.0

	# Build using Jib instead of Docker. (Read more: https://kiegroup.github.io/kogito-docs/serverlessworkflow/main/cloud/build-workflow-image-with-quarkus-cli.html)
	# Docker is still required to save the image if the push flag is not used
	{{.Name}} build --jib
	
	# Build using Jib and save the image in podman
	# Can't use the "push" or "jib" flag for this build strategy
	{{.Name}} build --jib-podman
	`,
		SuggestFor: []string{"biuld", "buidl", "built"},
		PreRunE:    common.BindEnv("image", "image-registry", "image-repository", "image-name", "image-tag", "jib", "jib-podman", "push"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runBuild(cmd, args)
	}

	cmd.Flags().StringP("image", "i", "", "Full image name in the form of [registry]/[repository]/[name]:[tag]")
	cmd.Flags().String("image-registry", "", "Image registry, ex: quay.io, if the --image flag is in use this option overrides image [registry]")
	cmd.Flags().String("image-repository", "", "Image repository, ex: registry-user or registry-project, if the --image flag is in use, this option overrides image [repository]")
	cmd.Flags().String("image-name", "", "Image name, ex: new-project, if the --image flag is in use, this option overrides the image [name]")
	cmd.Flags().String("image-tag", "", "Image tag, ex: 1.0, if the --image flag is in use, this option overrides the image [tag]")

	cmd.Flags().Bool("jib", false, "Use Jib extension to generate the image (Docker is still required to save the generated image if push is not used)")
	cmd.Flags().Bool("jib-podman", false, "Use Jib extension to generate the image and save it in podman (can't use --push)")
	cmd.Flags().Bool("push", false, "Attempt to push the genereated image after being successfully built")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)
	return cmd
}

func runBuild(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runBuildConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing build config: %w", err)
	}

	if err := common.CheckJavaDependencies(); err != nil {
		return err
	}

	if cfg.JibPodman {
		if err := common.CheckPodman(); err != nil {
			return err
		}
	} else if cfg.Jib && !cfg.Push {
		if err := common.CheckDocker(); err != nil {
			return err
		}
	}

	if err := runAddExtension(cfg); err != nil {
		return err
	}

	if err := runBuildImage(cfg); err != nil {
		return err
	}

	finish := time.Since(start)
	fmt.Printf("🚀 Build took: %s \n", finish)
	return nil
}

func runBuildConfig(cmd *cobra.Command) (cfg BuildConfig, err error) {
	cfg = BuildConfig{
		Image:      viper.GetString("image"),
		Registry:   viper.GetString("registry"),
		Repository: viper.GetString("repository"),
		ImageName:  viper.GetString("name"),
		Tag:        viper.GetString("tag"),

		Jib:       viper.GetBool("jib"),
		JibPodman: viper.GetBool("jib-podman"),
		Push:      viper.GetBool("push"),

		Verbose: viper.GetBool("verbose"),
	}
	if len(cfg.Image) == 0 && len(cfg.ImageName) == 0 {
		fmt.Println("ERROR: either --image or --image-name should be used")
		err = fmt.Errorf("missing flags")
	}
	if cfg.JibPodman && cfg.Push {
		fmt.Println("ERROR: can't use --jib-podman with --push")
		err = fmt.Errorf("invalid flags")
	}
	if cfg.JibPodman && cfg.Jib {
		fmt.Println("ERROR: can't use --jib-podman with --jib")
		err = fmt.Errorf("invalid flags")
	}
	return
}

func runAddExtension(cfg BuildConfig) error {
	var addExtension *exec.Cmd

	osCommand := common.GetOsCommand("mvnw")
	if cfg.Jib || cfg.JibPodman {
		fmt.Printf(" - Adding Quarkus Jib extension\n")
		addExtension = exec.Command(osCommand, "quarkus:add-extension",
			"-Dextensions=container-image-jib")
	} else {
		fmt.Printf(" - Adding Quarkus Docker extension\n")
		addExtension = exec.Command(osCommand, "quarkus:add-extension",
			"-Dextensions=container-image-docker")
	}
	if err := common.RunCommand(
		addExtension,
		cfg.Verbose,
		"build",
		getAddExtensionFriendlyMessages(),
	); err != nil {
		return err
	}

	fmt.Println("✅ Quarkus extension was successfully add to the project")
	return nil
}

func runBuildImage(cfg BuildConfig) error {
	registry, repository, name, tag := getImageConfig(cfg)
	builderConfig := getBuilderConfig(cfg)
	executableName := getExecutableNameConfig(cfg)

	osCommand := common.GetOsCommand("mvnw")
	build := exec.Command(osCommand, "package",
		"-Dquarkus.kubernetes.deployment-target=knative",
		fmt.Sprintf("-Dquarkus.knative.name=%s", name),
		"-Dquarkus.container-image.build=true",
		fmt.Sprintf("-Dquarkus.container-image.registry=%s", registry),
		fmt.Sprintf("-Dquarkus.container-image.group=%s", repository),
		fmt.Sprintf("-Dquarkus.container-image.name=%s", name),
		fmt.Sprintf("-Dquarkus.container-image.tag=%s", tag),
		fmt.Sprintf("-Dquarkus.container-image.push=%s", strconv.FormatBool(cfg.Push)),
		builderConfig,
		executableName,
	)

	if err := common.RunCommand(
		build,
		cfg.Verbose,
		"build",
		getBuildFriendlyMessages(),
	); err != nil {
		if cfg.Push {
			fmt.Println("ERROR: Image build failed.")
			fmt.Println("If you're using a private registry, check if you're authenticated")
		}
		fmt.Println("Check the full logs with the -v | --verbose option")
		return err
	}

	if cfg.Push {
		fmt.Printf("Created and pushed an image to registry: %s\n", getImage(registry, repository, name, tag))
	} else {
		fmt.Printf("Created a local image: %s\n", getImage(registry, repository, name, tag))
	}

	fmt.Println("✅ Build success")
	return nil
}

// Use the --image-registry, --image-repository, --image-name, --image-tag to override the --image flag
func getImageConfig(cfg BuildConfig) (string, string, string, string) {
	imageTagArray := strings.Split(cfg.Image, ":")
	imageArray := strings.SplitN(imageTagArray[0], "/", 3)

	var registry = common.DEFAULT_REGISTRY
	if len(cfg.Registry) > 0 {
		registry = cfg.Registry
	} else if len(imageArray) > 2 {
		registry = imageArray[0]
	}

	var repository = ""
	if len(cfg.Repository) > 0 {
		repository = cfg.Repository
	} else if len(imageArray) == 2 {
		repository = imageArray[0]
	} else if len(imageArray) == 3 {
		repository = imageArray[1]
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

	var tag = common.DEFAULT_TAG
	if len(cfg.Tag) > 0 {
		tag = cfg.Tag
	} else if len(imageTagArray) > 1 && len(imageTagArray[1]) > 0 {
		tag = imageTagArray[1]
	}

	return registry, repository, name, tag
}

func getImage(registry string, repository string, name string, tag string) string {
	if len(repository) == 0 {
		return fmt.Sprintf("%s/%s:%s", registry, name, tag)
	}
	return fmt.Sprintf("%s/%s/%s:%s", registry, repository, name, tag)
}

func getBuilderConfig(cfg BuildConfig) string {
	builder := "-Dquarkus.container-image.builder="
	if cfg.Jib || cfg.JibPodman {
		builder += "jib"
	} else {
		builder += "docker"
	}

	return builder
}

func getExecutableNameConfig(cfg BuildConfig) string {
	executableName := "-Dquarkus.jib.docker-executable-name="
	if cfg.JibPodman {
		executableName += "podman"
	} else {
		executableName += "docker"
	}
	return executableName
}

func getAddExtensionFriendlyMessages() []string {
	return []string{
		" Downloading Quarkus extension...",
		" Still downloading Quarkus extension",
		" Still downloading Quarkus extension",
		" Yes, still downloading Quarkus extension",
		" Don't give up on me",
		" Still downloading Quarkus extension",
		" This is taking a while",
	}
}

func getBuildFriendlyMessages() []string {
	return []string{
		" Building...",
		" Still building",
		" Still building",
		" Yes, still building",
		" Don't give up on me",
		" Still building",
		" This is taking a while",
	}
}
