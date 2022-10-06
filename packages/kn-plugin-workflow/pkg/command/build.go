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
	"context"
	"fmt"
	"net"
	"path/filepath"
	"sync"
	"time"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/filters"
	"github.com/docker/docker/client"
	"github.com/imdario/mergo"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/docker"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/moby/buildkit/session/filesync"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"golang.org/x/sync/errgroup"
)

type BuildCmdConfig struct {
	Extesions string // List of extensions separated by "," to be add on the Quarkus project

	// Image options
	Image      string // full image name
	Registry   string // image registry (overrides image name)
	Repository string // image repository (overrides image name)
	ImageName  string // image name (overrides image name)
	Tag        string // image tag (overrides image name)

	Path                string // project path
	DependenciesVersion metadata.DependenciesVersion
}

// Client that panics when used after Close()
type closeGuardingClient struct {
	pimpl  client.CommonAPIClient
	m      sync.RWMutex
	closed bool
}

func NewBuildCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "build",
		Short: "Build a single file Workflow project",
		Long: `
	Builds a single file Workflow project in the current directory 
	resulting in a container image and a kubernetes folder to be used by the deploy command.  
	By default the resultant container image will have the project name. It can be 
	overriten with the --image or with others image options, see help for more information.

	During the build, a knative.yml file will be generated on the ./kubernetes folder.
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

	# Build adding extensions to your generated container image
	{{.Name}} extension=quarkus-jsonp,quarkus-smallrye-openapi
	`,
		SuggestFor: []string{"biuld", "buidl", "built"},
		PreRunE:    common.BindEnv("extension", "image", "image-registry", "image-repository", "image-name", "image-tag", "path", "quarkus-platform-group-id", "quarkus-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runBuild(cmd, args)
	}

	quarkusDepedencies := metadata.ResolveQuarkusDependencies()
	cmd.Flags().StringP("extension", "e", "", "Project custom Maven extensions, separated with a comma.")
	cmd.Flags().StringP("image", "i", "", "Full image name in the form of [registry]/[repository]/[name]:[tag]")
	cmd.Flags().String("image-registry", "", "Image registry, ex: quay.io, if the --image flag is in use this option overrides image [registry]")
	cmd.Flags().String("image-repository", "", "Image repository, ex: registry-user or registry-project, if the --image flag is in use, this option overrides image [repository]")
	cmd.Flags().String("image-name", "", "Image name, ex: new-project, if the --image flag is in use, this option overrides the image [name]")
	cmd.Flags().String("image-tag", "", "Image tag, ex: 1.0, if the --image flag is in use, this option overrides the image [tag]")
	cmd.Flags().StringP("path", "p", ".", "Path of project to be built")
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDepedencies.QuarkusPlatformGroupId, "Quarkus group id to be used in the project build.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDepedencies.QuarkusVersion, "Quarkus version to be used in the project build")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runBuild(cmd *cobra.Command, args []string) (err error) {
	start := time.Now()
	fmt.Println("🔨 Building workflow project")

	cfg, err := runBuildCmdConfig(cmd)
	if err != nil {
		fmt.Println("ERROR: parsing flags")
		return
	}

	if err = common.CheckDocker(); err != nil {
		fmt.Println("ERROR: checking docker")
		return
	}

	if err = runBuildImage(cfg, cmd); err != nil {
		fmt.Println("ERROR: building image")
		return
	}

	fmt.Printf("🚀 Build command took: %s \n", time.Since(start))
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
		Path:       viper.GetString("path"),

		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: viper.GetString("quarkus-platform-group-id"),
			QuarkusVersion:         viper.GetString("quarkus-version"),
		},
	}

	if len(cfg.Image) == 0 && len(cfg.ImageName) == 0 {
		fmt.Println("ERROR: either --image or --image-name should be used")
		err = fmt.Errorf("missing flags")
	}
	return
}

func runBuildImage(cfg BuildCmdConfig, cmd *cobra.Command) (err error) {
	ctx := cmd.Context()

	// create docker client
	dockerCli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return
	}

	// creates a session for the dir
	session, err := docker.CreateSession(cfg.Path, false)
	if err != nil {
		fmt.Println("ERROR: failed to create a new session")
		return
	}
	if session == nil {
		fmt.Println("ERROR: session is not supported")
		return fmt.Errorf("session is not supported")
	}

	// adds fs sync providr to grpc server
	session.Allow(filesync.NewFSSyncProvider([]filesync.SyncedDir{
		{
			Name: "context",
			Dir:  cfg.Path,
			Map:  docker.ResetUIDAndGID,
		},
		{
			Name: "dockerfile",
			Dir:  docker.GetDockerfilePath(cfg.DependenciesVersion),
		},
	}))
	outputFolder := filepath.Join(cfg.Path, metadata.WORKFLOW_OUTPUT_FOLDER)
	session.Allow(filesync.NewFSSyncTargetDir(outputFolder))

	// creates a new errgroup
	eg, ctx := errgroup.WithContext(ctx)
	eg.Go(func() error {
		return session.Run(context.TODO(), func(ctx context.Context, proto string, meta map[string][]string) (net.Conn, error) {
			return dockerCli.DialHijack(ctx, metadata.DOCKER_SESSION_PATH, proto, meta)
		})
	})

	registry, repository, name, tag := common.GetImageConfig(cfg.Image, cfg.Registry, cfg.Repository, cfg.ImageName, cfg.Tag)
	if err := common.CheckImageName(name); err != nil {
		return err
	}
	buildArgs := docker.GetDockerBuildArgs(cfg.Extesions, registry, repository, name, tag)

	commomImageBuildOptions := types.ImageBuildOptions{
		SessionID:  session.ID(),
		Dockerfile: metadata.WORKFLOW_DOCKERFILE,
		BuildArgs:  buildArgs,
		Version:    types.BuilderBuildKit,
	}

	eg.Go(func() error {
		defer session.Close()

		outputBuildOptions := types.ImageBuildOptions{
			Target: metadata.DOCKER_BUILD_STAGE_OUTPUT,
			Outputs: []types.ImageBuildOutput{{
				Type:  "local",
				Attrs: map[string]string{},
			}},
		}
		mergo.Merge(&outputBuildOptions, commomImageBuildOptions)
		if err := docker.BuildDockerImage(ctx, cfg.DependenciesVersion, dockerCli, outputBuildOptions, cfg.Path); err != nil {
			fmt.Println("ERROR: generating output files")
			return err
		}
		fmt.Printf("- Generated outputs at %s folder\n", outputFolder)
		imageName := common.GetImage(registry, repository, name, tag)

		runnerBuildOptions := types.ImageBuildOptions{
			Tags:   []string{imageName},
			Target: metadata.DOCKER_BUILD_STAGE_RUNNER,
			Labels: getCmdBuildLabels(),
		}
		mergo.Merge(&runnerBuildOptions, commomImageBuildOptions)
		if err := docker.BuildDockerImage(ctx, cfg.DependenciesVersion, dockerCli, runnerBuildOptions, cfg.Path); err != nil {
			fmt.Println("ERROR: generating runner image")
			return err
		}
		fmt.Printf("- Generated container image: %s\n", imageName)

		return nil
	})

	if err := eg.Wait(); err != nil {
		return err
	}

	fmt.Println("✅ Build success")
	return nil
}

func getCmdBuildLabels() map[string]string {
	label := docker.GetCmdLabel()
	label["type"] = "runner"
	return label
}

func GetBuildFilter() filters.Args {
	filter := docker.GetFilter()
	filter.Add("label", "type=runner")
	return filter
}
