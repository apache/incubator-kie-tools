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

package single

import (
	"fmt"

	"github.com/docker/docker/client"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type BuildCmdConfig struct {
	Extesions string // List of extensions separated by "," to be add on the Quarkus project

	// Image options
	Image      string // full image name
	Registry   string // image registry (overrides image name)
	Repository string // image repository (overrides image name)
	ImageName  string // image name (overrides image name)
	Tag        string // image tag (overrides image name)

	// Plugin options
	Verbose bool
}

func NewBuildCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:        "build",
		Short:      "Build a workflow project",
		Long:       ``,
		Example:    ``,
		SuggestFor: []string{"biuld", "buidl", "built"},
		PreRunE:    common.BindEnv("verbose", "extension", "image", "image-registry", "image-repository", "image-name", "image-tag"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runBuild(cmd, args)
	}

	cmd.Flags().StringP("extension", "e", "", "Project custom Maven extensions, separated with a comma.")
	cmd.Flags().StringP("image", "i", "", "Full image name in the form of [registry]/[repository]/[name]:[tag]")
	cmd.Flags().String("image-registry", "", "Image registry, ex: quay.io, if the --image flag is in use this option overrides image [registry]")
	cmd.Flags().String("image-repository", "", "Image repository, ex: registry-user or registry-project, if the --image flag is in use, this option overrides image [repository]")
	cmd.Flags().String("image-name", "", "Image name, ex: new-project, if the --image flag is in use, this option overrides the image [name]")
	cmd.Flags().String("image-tag", "", "Image tag, ex: 1.0, if the --image flag is in use, this option overrides the image [tag]")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runBuild(cmd *cobra.Command, args []string) error {
	cfg, err := runBuildCmdConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing build config: %w", err)
	}

	if err := common.CheckDocker(); err != nil {
		return err
	}

	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		fmt.Println(err.Error())
		return nil
	}

	if err := runBuildImage(cfg, cli); err != nil {
		return err
	}

	return nil
}

func runBuildCmdConfig(cmd *cobra.Command) (cfg BuildCmdConfig, err error) {
	cfg = BuildCmdConfig{
		Extesions:  viper.GetString("extension"),
		Image:      viper.GetString("image"),
		Registry:   viper.GetString("registry"),
		Repository: viper.GetString("repository"),
		ImageName:  viper.GetString("name"),
		Tag:        viper.GetString("tag"),

		Verbose: viper.GetBool("verbose"),
	}
	if len(cfg.Image) == 0 && len(cfg.ImageName) == 0 {
		fmt.Println("ERROR: either --image or --image-name should be used")
		err = fmt.Errorf("missing flags")
	}

	return
}

func runBuildImage(cfg BuildCmdConfig, dockerClient *client.Client) error {
	registry, repository, name, tag := common.GetImageConfig(cfg.Image, cfg.Registry, cfg.Repository, cfg.ImageName, cfg.Tag)
	if err := common.CheckImageName(name); err != nil {
		return err
	}

	// dockerBuildArgs := getDockerBuildArgs(cfg, registry, repository, name, tag)
	// dockerArgs := []string{
	// 	"build",
	// 	fmt.Sprintf("-f %s", common.WORKFLOW_DOCKERFILE),
	// }

	// dockerKubernetsArgs := dockerArgs
	// dockerKubernetsArgs = append(dockerKubernetsArgs, fmt.Sprintf("--target=%s", "kubernetes"))
	// dockerKubernetsArgs = append(dockerKubernetsArgs, dockerBuildArgs...)
	// dockerKubernetsArgs = append(dockerKubernetsArgs, "--output type=local,dest=kubernetes", ".")

	// // TODO: remove
	// fmt.Printf("args\n")
	// for _, args := range dockerKubernetsArgs {
	// 	fmt.Printf("-- %s --\n", args)
	// }

	// os.Setenv("DOCKER_BUILDKIT", "1")

	// ctx, cancel := context.WithTimeout(context.Background(), time.Second*120)
	// defer cancel()

	// var workflowSwJson string = common.WORKFLOW_SW_JSON

	// buildArgs := map[string]*string{
	// 	common.DOCKER_BUILD_ARG_WORKFLOW_FILE: &workflowSwJson,
	// }

	// opts := types.ImageBuildOptions{
	// 	Dockerfile:     "Dockerfile.workflow",
	// 	SuppressOutput: false,
	// 	Tags:           []string{"test"},
	// 	BuildArgs:      buildArgs,
	// 	Outputs:        []types.ImageBuildOutput{},
	// }

	buildTargetKubernetes := common.ExecCommand(
		"docker",
		"build",
		fmt.Sprintf("-f %s", common.WORKFLOW_DOCKERFILE),
		fmt.Sprintf("--target=%s", "kubernetes"),
		fmt.Sprintf("--build-arg=%s=%s", common.DOCKER_BUILD_ARG_WORKFLOW_FILE, common.WORKFLOW_SW_JSON),
		fmt.Sprintf("--build-arg=%s=%s", common.DOCKER_BUILD_ARG_EXTENSIONS, cfg.Extesions),
		fmt.Sprintf("--build-arg=%s=%s", common.DOCKER_BUILD_ARG_WORKFLOW_NAME, cfg.ImageName),
		fmt.Sprintf("--build-arg=%s=%s", common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_REGISTRY, registry),
		fmt.Sprintf("--build-arg=%s=%s", common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_GROUP, repository),
		fmt.Sprintf("--build-arg=%s=%s", common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_NAME, name),
		fmt.Sprintf("--build-arg=%s=%s", common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_TAG, tag),
		"--output=type=local,dest=kubernetes",
		".",
	)
	if err := common.RunCommand(
		buildTargetKubernetes,
		cfg.Verbose,
		"build",
		common.GetFriendlyMessages("building"),
	); err != nil {
		fmt.Println("Check the full logs with the -v | --verbose option")
		return err
	}

	// dockerRunnerArgs := dockerArgs
	// dockerRunnerArgs = append(dockerRunnerArgs, fmt.Sprintf("--target=%s", "runner"))
	// dockerRunnerArgs = append(dockerKubernetsArgs, dockerBuildArgs...)
	// dockerKubernetsArgs = append(dockerKubernetsArgs, fmt.Sprintf("-t %s/%s/%s:%s", registry, repository, name, tag), ".")

	// buildTargetRunner := exec.Command("docker", dockerRunnerArgs...)

	// if err := common.RunCommand(
	// 	buildTargetRunner,
	// 	cfg.Verbose,
	// 	"build",
	// 	common.GetFriendlyMessages("building"),
	// ); err != nil {
	// 	fmt.Println("Check the full logs with the -v | --verbose option")
	// 	return err
	// }

	fmt.Println("✅ Build success")
	return nil
}
