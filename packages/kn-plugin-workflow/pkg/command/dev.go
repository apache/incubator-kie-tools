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
	"time"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"
	"github.com/docker/go-connections/nat"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/docker"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"golang.org/x/sync/errgroup"
)

type DevCmdConfig struct {
	Build               bool
	Run                 bool
	Tag                 string
	Extensions          string
	Port                string
	DependenciesVersion metadata.DependenciesVersion
}

func NewDevCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "dev",
		Short: "Run a workflow project in development mode",
		Long:  ``,
		Example: `

If you wish, you can run the development container using Docker directly:
	docker container run -it \
	--mount type=bind,source="$(pwd)",target=/tmp/kn-plugin-workflow/src/main/resources \
	-p 8080:8080 quay.io/lmotta/dev		
`,
		SuggestFor: []string{"dve", "start"},
		PreRunE:    common.BindEnv("build", "run", "tag", "extension", "port", "quarkus-platform-group-id", "quarkus-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDev(cmd, args)
	}

	quarkusDepedencies := metadata.ResolveQuarkusDependencies()
	cmd.Flags().BoolP("build", "b", true, "Build dev image.")
	cmd.Flags().BoolP("run", "r", true, "Start the development container.")
	cmd.Flags().StringP("tag", "t", "dev", "Development tag.")
	cmd.Flags().StringP("extension", "e", "", "Project custom Maven extensions, separated with a comma.")
	cmd.Flags().StringP("port", "p", "8080", "Port to be used.")
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDepedencies.QuarkusPlatformGroupId, "Quarkus group id to be set in the project.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDepedencies.QuarkusVersion, "Quarkus version to be set in the project.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runDevCmdConfig(cmd *cobra.Command) (cfg DevCmdConfig, err error) {
	cfg = DevCmdConfig{
		Build:      viper.GetBool("build"),
		Run:        viper.GetBool("run"),
		Tag:        viper.GetString("tag"),
		Extensions: viper.GetString("extension"),
		Port:       viper.GetString("port"),

		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: viper.GetString("quarkus-platform-group-id"),
			QuarkusVersion:         viper.GetString("quarkus-version"),
		},
	}
	return
}

func runDev(cmd *cobra.Command, args []string) (err error) {
	start := time.Now()

	cfg, err := runDevCmdConfig(cmd)
	if err != nil {
		fmt.Println("ERROR: parsing flags")
		return
	}

	if err = common.CheckDocker(); err != nil {
		fmt.Println("ERROR: checking docker")
		return
	}

	if cfg.Build {
		fmt.Println("🔨 Building your development image")
		if err = buildDevImage(cfg, cmd); err != nil {
			fmt.Println("ERROR: building dev image")
			return
		}
	}

	if cfg.Run {
		fmt.Println("🔨 Starting your development container")
		if err = runDevContainer(cfg, cmd); err != nil {
			fmt.Println("ERROR: running dev container")
			return
		}
	}

	fmt.Printf("🚀 Development command took: %s \n", time.Since(start))
	return nil
}

func buildDevImage(cfg DevCmdConfig, cmd *cobra.Command) (err error) {
	ctx := cmd.Context()

	// create docker client
	dockerCli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return
	}

	// creates a session for the dir
	session, err := docker.CreateSession(".", false)
	if err != nil {
		fmt.Println("ERROR: failed to create a new session")
		return
	}
	if session == nil {
		fmt.Println("ERROR: session is not supported")
		return fmt.Errorf("session is not supported")
	}

	// creates a new errgroup
	eg, ctx := errgroup.WithContext(ctx)
	eg.Go(func() error {
		return session.Run(context.TODO(), func(ctx context.Context, proto string, meta map[string][]string) (net.Conn, error) {
			return dockerCli.DialHijack(ctx, common.DOCKER_SESSION_PATH, proto, meta)
		})
	})

	registry, repository, name, tag := common.GetImageConfig("", common.KN_WORKFLOW_DEV_REPOSITORY, "", common.KN_WORKFLOW_DEVELOPMENT, cfg.Tag)
	if err := common.CheckImageName(name); err != nil {
		return err
	}
	buildArgs := docker.GetDockerBuildArgs(cfg.Extensions, registry, repository, name, tag)

	eg.Go(func() error {
		defer session.Close()

		if err := docker.BuildDockerImage(ctx, cfg.DependenciesVersion, dockerCli, types.ImageBuildOptions{
			SessionID:  session.ID(),
			Dockerfile: common.WORKFLOW_DOCKERFILE,
			BuildArgs:  buildArgs,
			Version:    types.BuilderBuildKit,
			Tags:       []string{common.GetImage(registry, repository, name, tag)},
			Target:     common.DOCKER_BUILD_STAGE_DEV,
		}); err != nil {
			fmt.Println("ERROR: generating development image")
			return err
		}
		return nil
	})

	if err := eg.Wait(); err != nil {
		return err
	}

	fmt.Println("✅ Development image build success")
	return
}

func runDevContainer(cfg DevCmdConfig, cmd *cobra.Command) (err error) {
	ctx := cmd.Context()

	// create docker client
	dockerCli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return
	}

	containerPort := nat.Port(common.QUARKUS_DEV_PORT)
	containerConfig := &container.Config{
		Image:        fmt.Sprintf("%s:%s", common.KN_WORKFLOW_DEV_IMAGE, cfg.Tag),
		AttachStdin:  true,
		AttachStdout: true,
		AttachStderr: true,
		Tty:          true,
		ExposedPorts: nat.PortSet{
			containerPort: struct{}{},
		},
	}

	currentPath, err := common.GetCurrentPath()
	if err != nil {
		return
	}

	containerHostConfig := &container.HostConfig{
		Binds: []string{
			fmt.Sprintf("%s:%s", currentPath, common.WORKFLOW_RESOURCES_PATH),
		},
		PortBindings: nat.PortMap{
			containerPort: []nat.PortBinding{{HostIP: "", HostPort: cfg.Port}},
		},
	}

	containerName := fmt.Sprintf("%s-%s", common.KN_WORKFLOW_DEV_CONTAINER, docker.RandString())
	devContainer, err := dockerCli.ContainerCreate(ctx, containerConfig, containerHostConfig, nil, nil, containerName)
	if err != nil {
		fmt.Println("ERROR: failed to create a developement container")
		return
	}

	err = dockerCli.ContainerStart(ctx, devContainer.ID, types.ContainerStartOptions{})
	if err != nil {
		fmt.Println("ERROR: failed to start the developement container")
		return
	}

	fmt.Println("✅ Development container started")
	fmt.Printf("- Container name: %s\n", containerName)
	fmt.Printf("- Listening on: localhost:%s", cfg.Port)
	fmt.Printf(`
- If you wish to stop your development container you can use Docker directly:

docker container stop %s

`, containerName)
	return
}
