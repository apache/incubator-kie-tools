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
	"archive/tar"
	"bytes"
	"context"
	"fmt"
	"io/ioutil"
	"net"
	"os"
	"path/filepath"
	"sync"
	"time"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/imdario/mergo"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
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
		Use:        "build",
		Short:      "Build a workflow project",
		Long:       ``,
		Example:    ``,
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
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDepedencies.QuarkusPlatformGroupId, "Quarkus group id to be set in the project.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDepedencies.QuarkusVersion, "Quarkus version to be set in the project.")
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
	session, err := CreateSession(cfg.Path, false)
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
			Map:  ResetUIDAndGID,
		},
		{
			Name: "dockerfile",
			Dir:  GetDockerfilePath(cfg.DependenciesVersion),
		},
	}))
	session.Allow(filesync.NewFSSyncTargetDir("./kubernetes"))

	// creates a new errgroup
	eg, ctx := errgroup.WithContext(ctx)
	eg.Go(func() error {
		return session.Run(context.TODO(), func(ctx context.Context, proto string, meta map[string][]string) (net.Conn, error) {
			return dockerCli.DialHijack(ctx, "/session", proto, meta)
		})
	})

	registry, repository, name, tag := common.GetImageConfig(cfg.Image, cfg.Registry, cfg.Repository, cfg.ImageName, cfg.Tag)
	if err := common.CheckImageName(name); err != nil {
		return err
	}
	buildArgs := GetDockerBuildArgs(cfg, registry, repository, name, tag)

	commomImageBuildOptions := types.ImageBuildOptions{
		SessionID:   session.ID(),
		Dockerfile:  common.WORKFLOW_DOCKERFILE,
		BuildArgs:   buildArgs,
		Version:     types.BuilderBuildKit,
		NetworkMode: "default",
	}

	eg.Go(func() error {
		defer session.Close()

		outputBuildOptions := types.ImageBuildOptions{
			Target: "output-files",
			Outputs: []types.ImageBuildOutput{{
				Type:  "local",
				Attrs: map[string]string{},
			}},
		}
		mergo.Merge(&outputBuildOptions, commomImageBuildOptions)
		if err := runDockerImageBuild(ctx, cfg, dockerCli, outputBuildOptions); err != nil {
			fmt.Println("ERROR: generating output files")
			return err
		}
		runnerBuildOptions := types.ImageBuildOptions{
			Tags:   []string{common.GetImage(registry, repository, name, tag)},
			Target: "runner",
		}
		mergo.Merge(&runnerBuildOptions, commomImageBuildOptions)
		if err := runDockerImageBuild(ctx, cfg, dockerCli, runnerBuildOptions); err != nil {
			fmt.Println("ERROR: generating runner image")
			return err
		}
		return nil
	})

	if err := eg.Wait(); err != nil {
		return err
	}

	fmt.Println("✅ Build success")
	return nil
}

func runDockerImageBuild(
	ctx context.Context,
	cfg BuildCmdConfig,
	dockerCli client.CommonAPIClient,
	imageBuildOptions types.ImageBuildOptions,
) (err error) {
	// creates a tar with the Dockerfile and the workflow.sw.json
	buf := new(bytes.Buffer)
	tw := tar.NewWriter(buf)
	defer tw.Close()

	// adds dockerfile to tar
	err = addFileToTar(tw, GetDockerfilePath(cfg.DependenciesVersion), common.WORKFLOW_DOCKERFILE)
	if err != nil {
		return
	}
	currentPath, err := os.Getwd()
	if err != nil {
		fmt.Println("ERROR: error getting current path")
		return
	}

	// adds workflow.sw.json
	err = addFileToTar(tw, filepath.Join(currentPath, common.WORKFLOW_SW_JSON), common.WORKFLOW_SW_JSON)
	if err != nil {
		return
	}
	dockerTar := bytes.NewReader(buf.Bytes())

	// builds using options
	res, err := dockerCli.ImageBuild(ctx, dockerTar, imageBuildOptions)
	if err != nil {
		fmt.Println("ERROR: error building, image options: %s", imageBuildOptions)
		return
	}
	defer res.Body.Close()

	return Log(res.Body)
}

func addFileToTar(tw *tar.Writer, path string, fileName string) (err error) {
	fileReader, err := os.Open(path)
	if err != nil {
		fmt.Printf("ERROR: opening %s\n", path)
		return
	}
	readFile, err := ioutil.ReadAll(fileReader)
	if err != nil {
		fmt.Printf("ERROR: reading %s\n", path)
		return
	}
	tarHeader := &tar.Header{
		Name: fileName,
		Size: int64(len(readFile)),
	}
	err = tw.WriteHeader(tarHeader)
	if err != nil {
		fmt.Printf("ERROR: unable to write tar header %s\n", path)
		return
	}
	_, err = tw.Write(readFile)
	if err != nil {
		fmt.Printf("ERROR: unable to write tar body %s\n", path)
		return
	}
	return
}
