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
	"bufio"
	"context"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net"
	"path/filepath"
	"strconv"
	"sync"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/archive"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/moby/buildkit/session"
	"github.com/moby/buildkit/session/filesync"
	"github.com/ory/viper"
	"github.com/pkg/errors"
	"github.com/spf13/cobra"
	fsutiltypes "github.com/tonistiigi/fsutil/types"
	"golang.org/x/sync/errgroup"
	"google.golang.org/grpc"
)

type BuildCmdConfig struct {
	Extesions string // List of extensions separated by "," to be add on the Quarkus project

	// Image options
	Image      string // full image name
	Registry   string // image registry (overrides image name)
	Repository string // image repository (overrides image name)
	ImageName  string // image name (overrides image name)
	Tag        string // image tag (overrides image name)
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
		PreRunE:    common.BindEnv("extension", "image", "image-registry", "image-repository", "image-name", "image-tag"),
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

	if err := runBuildImage(cfg, cmd); err != nil {
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
	}
	if len(cfg.Image) == 0 && len(cfg.ImageName) == 0 {
		fmt.Println("ERROR: either --image or --image-name should be used")
		err = fmt.Errorf("missing flags")
	}

	return
}
func runBuildImage(cfg BuildCmdConfig, cmd *cobra.Command) (err error) {
	ctx := cmd.Context()
	dockerCli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return fmt.Errorf(err.Error())
	}

	// creates a session for the dir
	// TODO: path flag?
	session, err := trySession(".", false)
	if err != nil {
		log.Fatal(err, " : failed session")
	}
	if session == nil {
		return fmt.Errorf("buildkit not supported by daemon")
	}

	// Adds the fs methods to the session
	session.Allow(filesync.NewFSSyncProvider([]filesync.SyncedDir{
		{
			Name: "context",
			Dir:  ".",
			Map:  resetUIDAndGID,
		},
		{
			Name: "dockerfile",
			Dir:  ".",
		},
	}))
	session.Allow(filesync.NewFSSyncTargetDir("./kubernets"))

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

	var workflowSwJson string = common.WORKFLOW_SW_JSON

	buildArgs := map[string]*string{
		common.DOCKER_BUILD_ARG_WORKFLOW_FILE:            &workflowSwJson,
		common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_REGISTRY: &registry,
		common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_GROUP:    &repository,
		common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_NAME:     &name,
		common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_TAG:      &tag,
		common.DOCKER_BUILD_ARG_WORKFLOW_NAME:            &cfg.ImageName,
	}

	existExtensions := strconv.FormatBool(len(cfg.Extesions) > 0)
	buildArgs[common.DOCKER_BUILD_ARG_EXTENSIONS] = &existExtensions

	if len(cfg.Extesions) > 0 {
		buildArgs[common.DOCKER_BUILD_ARG_EXTENSIONS_LIST] = &cfg.Extesions
	}
	imageTag := common.GetImage(registry, repository, name, tag)

	eg.Go(func() error {
		// make sure the Status ends cleanly on build errors
		defer func() {
			session.Close()
		}()

		if err := generateOutputFiles(ctx, dockerCli, buildArgs, session.ID()); err != nil {
			return fmt.Errorf("error output: %w", err)
		}

		if err := generateRunnerImage(ctx, dockerCli, buildArgs, imageTag, session.ID()); err != nil {
			return fmt.Errorf("error runner: %w", err)
		}

		return nil
	})

	if err := eg.Wait(); err != nil {
		return err
	}

	fmt.Println("✅ Build success")

	return nil
}

func generateOutputFiles(
	ctx context.Context,
	dockerCli client.CommonAPIClient,
	buildArgs map[string]*string,
	sessionId string,
) (err error) {
	tar, err := createTarFromDockerfile()
	if err != nil {
		return
	}
	defer tar.Close()

	outputFilesOptions := types.ImageBuildOptions{
		SessionID:   sessionId,
		Dockerfile:  "Dockerfile.workflow",
		BuildArgs:   buildArgs,
		Version:     types.BuilderBuildKit,
		Target:      "output-files",
		NetworkMode: "default",
		Outputs: []types.ImageBuildOutput{{
			Type:  "local",
			Attrs: map[string]string{},
		}},
	}

	res, err := dockerCli.ImageBuild(ctx, tar, outputFilesOptions)
	if err != nil {
		return fmt.Errorf("cannot build the app image: %w", err)
	}
	defer res.Body.Close()

	return print(res.Body)
}

func generateRunnerImage(
	ctx context.Context,
	dockerCli client.CommonAPIClient,
	buildArgs map[string]*string,
	imageTag string,
	sessionId string,
) (err error) {
	tar, err := createTarFromDockerfile()
	if err != nil {
		return
	}
	defer tar.Close()

	outputFilesOptions := types.ImageBuildOptions{
		Dockerfile:  "Dockerfile.workflow",
		SessionID:   sessionId,
		Tags:        []string{imageTag},
		BuildArgs:   buildArgs,
		Version:     types.BuilderBuildKit,
		Target:      "runner",
		NetworkMode: "default",
	}

	res, err := dockerCli.ImageBuild(ctx, tar, outputFilesOptions)
	if err != nil {
		return fmt.Errorf("cannot build the app image: %w", err)
	}
	defer res.Body.Close()

	return print(res.Body)
}

func createTarFromDockerfile() (tar io.ReadCloser, err error) {
	// creates a tar with the Dockerfile and the workflow.sw.json
	// TODO: path flag?
	tar, err = archive.TarWithOptions("./", &archive.TarOptions{
		IncludeFiles: []string{"Dockerfile.workflow", "workflow.sw.json"},
	})

	if err != nil {
		err = fmt.Errorf("%w :unable to open Dockerfile", err)
	}

	return
}

// Session is a long running connection between client and a daemon
type Session struct {
	id         string
	name       string
	sharedKey  string
	ctx        context.Context
	cancelCtx  func()
	done       chan struct{}
	grpcServer *grpc.Server
	conn       net.Conn
}

// Creates a new session
// A session is a grpc server that enables the Docker sdk to have a longer connection
func trySession(contextDir string, forStream bool) (*session.Session, error) {
	sessionHash := sha256.Sum256([]byte(fmt.Sprintf("%s", contextDir)))
	sharedKey := hex.EncodeToString(sessionHash[:])
	session, err := session.NewSession(context.Background(), filepath.Base(contextDir), sharedKey)
	if err != nil {
		return nil, errors.Wrap(err, "failed to create session")
	}
	return session, nil
}

func resetUIDAndGID(_ string, s *fsutiltypes.Stat) bool {
	s.Uid = 0
	s.Gid = 0
	return true
}

type ErrorDetail struct {
	Message string `json:"message"`
}

type ErrorLine struct {
	Error       string      `json:"error"`
	ErrorDetail ErrorDetail `json:"errorDetail"`
}

func print(rd io.Reader) error {
	var lastLine string

	scanner := bufio.NewScanner(rd)
	for scanner.Scan() {
		lastLine = scanner.Text()
		fmt.Println(scanner.Text())
	}

	errLine := &ErrorLine{}
	json.Unmarshal([]byte(lastLine), errLine)
	if errLine.Error != "" {
		return errors.New(errLine.Error)
	}

	if err := scanner.Err(); err != nil {
		return err
	}

	return nil
}
