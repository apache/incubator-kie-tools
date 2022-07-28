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
	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common"
	"github.com/spf13/cobra"
)

func NewBuildCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:        "build",
		Short:      "Build a workflow project",
		Long:       ``,
		Example:    ``,
		SuggestFor: []string{"biuld", "buidl", "built"},
		PreRunE:    common.BindEnv("verbose"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runBuild(cmd, args)
	}

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

// `protobuf:"bytes,3,opt,name=Exporter,proto3" json:"Exporter,omitempty"`
// `protobuf:"bytes,4,rep,name=ExporterAttrs,proto3" json:"ExporterAttrs,omitempty" protobuf_key:"bytes,1,opt,name=key,proto3" protobuf_val:"bytes,2,opt,name=value,proto3"`

func runBuild(cmd *cobra.Command, args []string) error {
	// outputs := []types.ImageBuildOutput{{
	// 	Type:  "local",
	// 	Attrs: map[string]string{"dest": "kubernetes"}},
	// }

	// docker / podman run buildkit
	// generate image
	// build ->

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
