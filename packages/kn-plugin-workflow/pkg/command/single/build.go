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

	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type BuildCmdConfig struct {
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
		PreRunE:    common.BindEnv("verbose", "image", "image-registry", "image-repository", "image-name", "image-tag"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runBuild(cmd, args)
	}

	cmd.Flags().StringP("image", "i", "", "Full image name in the form of [registry]/[repository]/[name]:[tag]")
	cmd.Flags().String("image-registry", "", "Image registry, ex: quay.io, if the --image flag is in use this option overrides image [registry]")
	cmd.Flags().String("image-repository", "", "Image repository, ex: registry-user or registry-project, if the --image flag is in use, this option overrides image [repository]")
	cmd.Flags().String("image-name", "", "Image name, ex: new-project, if the --image flag is in use, this option overrides the image [name]")
	cmd.Flags().String("image-tag", "", "Image tag, ex: 1.0, if the --image flag is in use, this option overrides the image [tag]")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runBuild(cmd *cobra.Command, args []string) error {
	// cfg, err := runBuildCmdConfig(cmd)
	// if err != nil {
	// 	return fmt.Errorf("initializing build config: %w", err)
	// }

	if err := common.CheckDocker(); err != nil {
		return err
	}

	// build target -kubernetes
	// build target -runner
	// file := common.WORKFLOW_SW_JSON
	// extensions := "quarkus-jsonp,quarkus-smallrye-openapi"
	// projectName := "test"
	// registry := "quay.io"
	// group := "lmotta"
	// name := "runner"
	// tag := "0.0.1"
	// buildArgs := map[string]*string{
	// 	common.DOCKER_BUILD_ARG_WORKFLOW_FILE:            &file,
	// 	common.DOCKER_BUILD_ARG_EXTENSIONS:               &extensions,
	// 	common.DOCKER_BUILD_ARG_WORKFLOW_NAME:            &projectName,
	// 	common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_REGISTRY: &registry,
	// 	common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_GROUP:    &group,
	// 	common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_NAME:     &name,
	// 	common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_TAG:      &tag,
	// }

	// opts := types.ImageBuildOptions{
	// 	Version:    types.BuilderBuildKit,
	// 	Target:     "kubernetes",
	// 	Dockerfile: "Dockerfile.workflow",
	// 	Tags:       []string{"lmotta" + "/runner"},
	// 	Remove:     true,
	// 	// Outputs:    outputs,
	// 	BuildArgs: buildArgs,
	// }

	// cli, err := client.NewClientWithOpts(client.FromEnv)
	// if err != nil {
	// 	panic(err)
	// }
	// tar, err := archive.TarWithOptions("./", &archive.TarOptions{})
	// if err != nil {
	// 	return err
	// }

	// res, err := cli.ImageBuild(context.Background(), tar, opts)
	// if err != nil {
	// 	return err
	// }

	// defer res.Body.Close()
	// err = print(res.Body)
	// if err != nil {
	// 	return err
	// }

	return nil
}

func runBuildCmdConfig(cmd *cobra.Command) (cfg BuildCmdConfig, err error) {
	cfg = BuildCmdConfig{
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
