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

package quarkus

import (
	"fmt"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"regexp"
	"strconv"
	"strings"
)

type BuildCmdConfig struct {
	Image      string // full image name
	Registry   string // image registry (overrides image name)
	Repository string // image repository (overrides image name)
	ImageName  string // image name (overrides image name)
	Tag        string // image tag (overrides image name)

	// Build strategy options
	Jib       bool // use Jib extension to build the image and push it to a remote registry
	JibPodman bool // use Jib extension to build the image and save it in podman
	Push      bool // choose to push an image to a remote registry or not (Docker only)

	Test bool // choose to run the project tests
}

func NewBuildCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "build",
		Short: "Build a Kogito Serverless Workflow project and generate a container image",
		Long: `
	Builds a Kogito Serverless Workflow project in the current directory 
	resulting in a container image.  
	By default the resultant container image will have the project name. It can be 
	overridden with the --image or with others image options, see help for more information.

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
		PreRunE:    common.BindEnv("image", "image-registry", "image-repository", "image-name", "image-tag", "jib", "jib-podman", "push", "test"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		_, err := runBuild(cmd)
		return err
	}

	cmd.Flags().StringP("image", "i", "", "Full image name in the form of [registry]/[repository]/[name]:[tag]")
	cmd.Flags().String("image-registry", "", "Image registry, ex: quay.io, if the --image flag is in use this option overrides image [registry]")
	cmd.Flags().String("image-repository", "", "Image repository, ex: registry-user or registry-project, if the --image flag is in use, this option overrides image [repository]")
	cmd.Flags().String("image-name", "", "Image name, ex: new-project, if the --image flag is in use, this option overrides the image [name]")
	cmd.Flags().String("image-tag", "", "Image tag, ex: 1.0, if the --image flag is in use, this option overrides the image [tag]")

	cmd.Flags().Bool("jib", false, "Use Jib extension to generate the image (Docker is still required to save the generated image if push is not used)")
	cmd.Flags().Bool("jib-podman", false, "Use Jib extension to generate the image and save it in podman (can't use --push)")
	cmd.Flags().Bool("push", false, "Attempt to push the genereated image after being successfully built")

	cmd.Flags().Bool("test", false, "Run the project tests")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)
	return cmd
}

func runBuild(cmd *cobra.Command) (out string, err error) {
	fmt.Println("🔨 Building your Quarkus Kogito Serverless Workflow project...")

	cfg, err := runBuildCmdConfig(cmd)
	if err != nil {
		err = fmt.Errorf("initializing build config: %w", err)
		return
	}

	if err = common.CheckJavaDependencies(); err != nil {
		return
	}

	if cfg.JibPodman {
		if err = common.CheckPodman(); err != nil {
			return
		}
	} else if (cfg.Jib && !cfg.Push) || (!cfg.Jib) {
		if err = common.CheckDocker(); err != nil {
			return
		}
	}

	if err != nil {
		return
	}

	if err = runAddExtension(cfg); err != nil {
		return
	}

	if out, err = runBuildImage(cfg); err != nil {
		return
	}

	fmt.Println("✅ Quarkus Kogito Serverless Workflow project successfully built")

	return
}

func runBuildCmdConfig(cmd *cobra.Command) (cfg BuildCmdConfig, err error) {
	cfg = BuildCmdConfig{
		Image:      viper.GetString("image"),
		Registry:   viper.GetString("image-registry"),
		Repository: viper.GetString("image-repository"),
		ImageName:  viper.GetString("image-name"),
		Tag:        viper.GetString("image-tag"),

		Jib:       viper.GetBool("jib"),
		JibPodman: viper.GetBool("jib-podman"),
		Push:      viper.GetBool("push"),

		Test: viper.GetBool("test"),
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

// This function removes the extension that is not going to be used (if present)
// and updates the chosen one. The entire operation is handled as an extension addition.
// Therefore the removal is hidden from the user
func runAddExtension(cfg BuildCmdConfig) error {
	if cfg.Jib || cfg.JibPodman {
		fmt.Printf(" - Adding Quarkus Jib extension\n")
		if err := common.RunExtensionCommand(
			"quarkus:remove-extension",
			metadata.QuarkusContainerImageDocker,
		); err != nil {
			return err
		}
		if err := common.RunExtensionCommand(
			"quarkus:add-extension",
			metadata.QuarkusContainerImageJib,
		); err != nil {
			return err
		}
	} else {
		fmt.Printf(" - Adding Quarkus Docker extension\n")
		if err := common.RunExtensionCommand(
			"quarkus:remove-extension",
			metadata.QuarkusContainerImageJib,
		); err != nil {
			return err
		}
		if err := common.RunExtensionCommand(
			"quarkus:add-extension",
			metadata.QuarkusContainerImageDocker,
		); err != nil {
			return err
		}
	}

	fmt.Println("✅ Quarkus extension was successfully added to the project pom.xml")
	return nil
}

func runBuildImage(cfg BuildCmdConfig) (out string, err error) {
	registry, repository, name, tag := getImageConfig(cfg)
	if err = checkImageName(name); err != nil {
		return
	}

	skipTestsConfig := getSkipTestsConfig(cfg)
	builderConfig := getBuilderConfig(cfg)
	executableName := getExecutableNameConfig(cfg)

	build := common.ExecCommand("mvn", "package",
		skipTestsConfig,
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

	if err = common.RunCommand(
		build,
		"build",
	); err != nil {
		if cfg.Push {
			fmt.Println("ERROR: Image build failed.")
			fmt.Println("If you're using a private registry, check if you're authenticated")
		}
		return
	}

	out = getImage(registry, repository, name, tag)
	if cfg.Push {
		fmt.Printf("Created and pushed an image to registry: %s\n", out)
	} else {
		fmt.Printf("Created a local image: %s\n", out)
	}

	fmt.Println("✅ Build success")
	return
}

func checkImageName(name string) (err error) {
	matched, err := regexp.MatchString("^[a-z]([-a-z0-9]*[a-z0-9])?$", name)
	if !matched {
		fmt.Println(`
ERROR: Image name should match [a-z]([-a-z0-9]*[a-z0-9])?
The name needs to start with a lower case letter and then it can be composed exclusvely of lower case letters, numbers or dashes ('-')
Example of valid names: "test-0-0-1", "test", "t1"
Example of invalid names: "1-test", "test.1", "test/1"
		`)
		err = fmt.Errorf("invalid image name")
	}
	return
}

// Use the --image-registry, --image-repository, --image-name, --image-tag to override the --image flag
func getImageConfig(cfg BuildCmdConfig) (string, string, string, string) {
	imageTagArray := strings.Split(cfg.Image, ":")
	imageArray := strings.SplitN(imageTagArray[0], "/", 3)

	var registry = ""
	if len(cfg.Registry) > 0 {
		registry = cfg.Registry
	} else if len(imageArray) > 1 {
		registry = imageArray[0]
	}

	var repository = ""
	if len(cfg.Repository) > 0 {
		repository = cfg.Repository
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

	var tag = metadata.DefaultTag
	if len(cfg.Tag) > 0 {
		tag = cfg.Tag
	} else if len(imageTagArray) > 1 && len(imageTagArray[1]) > 0 {
		tag = imageTagArray[1]
	}

	return registry, repository, name, tag
}

func getImage(registry string, repository string, name string, tag string) string {
	if len(registry) == 0 && len(repository) == 0 {
		return fmt.Sprintf("%s:%s", name, tag)
	} else if len(registry) == 0 {
		return fmt.Sprintf("%s/%s:%s", repository, name, tag)
	} else if len(repository) == 0 {
		return fmt.Sprintf("%s/%s:%s", registry, name, tag)
	}
	return fmt.Sprintf("%s/%s/%s:%s", registry, repository, name, tag)
}

func getSkipTestsConfig(cfg BuildCmdConfig) string {
	skipTests := "-DskipTests="
	if cfg.Test {
		skipTests += "false"
	} else {
		skipTests += "true"
	}
	return skipTests
}

func getBuilderConfig(cfg BuildCmdConfig) string {
	builder := "-Dquarkus.container-image.builder="
	if cfg.Jib || cfg.JibPodman {
		builder += "jib"
	} else {
		builder += "docker"
	}
	return builder
}

func getExecutableNameConfig(cfg BuildCmdConfig) string {
	executableName := "-Dquarkus.jib.docker-executable-name="
	if cfg.JibPodman {
		executableName += "podman"
	} else {
		executableName += "docker"
	}
	return executableName
}
