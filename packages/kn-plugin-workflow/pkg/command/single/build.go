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
	"context"
	"crypto/rand"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io"
	"io/ioutil"
	"log"
	"net"
	"os"
	"path/filepath"
	"strings"
	"sync"

	cliconfig "github.com/docker/cli/cli/config"
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/archive"
	grpc_middleware "github.com/grpc-ecosystem/go-grpc-middleware"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/moby/buildkit/identity"
	"github.com/moby/buildkit/session/filesync"
	"github.com/moby/buildkit/util/bklog"
	"github.com/moby/buildkit/util/grpcerrors"
	"github.com/ory/viper"
	"github.com/pkg/errors"
	"github.com/spf13/cobra"
	"go.opentelemetry.io/contrib/instrumentation/google.golang.org/grpc/otelgrpc"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/trace"
	"golang.org/x/net/http2"
	"golang.org/x/sync/errgroup"
	"google.golang.org/grpc"
	"google.golang.org/grpc/health"
	"google.golang.org/grpc/health/grpc_health_v1"
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

// Client that panics when used after Close()
type closeGuardingClient struct {
	pimpl  client.CommonAPIClient
	m      sync.RWMutex
	closed bool
}

func (c *closeGuardingClient) Close() error {
	c.m.Lock()
	defer c.m.Unlock()
	c.closed = true
	return c.pimpl.Close()
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

		Verbose: viper.GetBool("verbose"),
	}
	if len(cfg.Image) == 0 && len(cfg.ImageName) == 0 {
		fmt.Println("ERROR: either --image or --image-name should be used")
		err = fmt.Errorf("missing flags")
	}

	return
}
func runBuildImage(cfg BuildCmdConfig, cmd *cobra.Command) (err error) {
	ctx := cmd.Context()
	var dockerClient client.CommonAPIClient
	dockerClient, err = client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		fmt.Println(err.Error())
		return nil
	}
	cli := &closeGuardingClient{pimpl: dockerClient}
	defer cli.Close()

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

	if len(cfg.Extesions) > 0 {
		buildArgs[common.DOCKER_BUILD_ARG_EXTENSIONS] = &cfg.Extesions
	}

	tar, err := archive.TarWithOptions("./docker", &archive.TarOptions{})
	if err != nil {
		log.Fatal(err, " :unable to open Dockerfile")
	}
	defer tar.Close()

	session, err := trySession(dockerClient, "./docker", false)
	if err != nil {
		log.Fatal(err, " : failed session")
	}
	defer func() { // make sure the Status ends cleanly on build errors
		session.Close()
	}()
	session.Allow(filesync.NewFSSyncTargetDir("./kubernetes"))

	eg, ctx := errgroup.WithContext(ctx)

	dialSession := func(ctx context.Context, proto string, meta map[string][]string) (net.Conn, error) {
		return dockerClient.DialHijack(ctx, "/session", proto, meta)
	}
	eg.Go(func() error {
		return session.Run(context.TODO(), dialSession)
	})

	imageTag := common.GetImage(registry, repository, name, tag)
	opts := types.ImageBuildOptions{
		RemoteContext: "client-session",
		SessionID:     session.ID(),
		Dockerfile:    "Dockerfile.workflow",
		Tags:          []string{imageTag},
		BuildArgs:     buildArgs,
		Version:       types.BuilderBuildKit,
		Target:        "kubernetes",
		NetworkMode:   "default",
		Outputs: []types.ImageBuildOutput{{
			Type:  "local",
			Attrs: map[string]string{},
		}},
	}

	res, err := dockerClient.ImageBuild(ctx, tar, opts)
	if err != nil {
		return fmt.Errorf("cannot build the app image: %w", err)
	}
	defer res.Body.Close()

	_, err = io.Copy(os.Stdout, res.Body)
	if err != nil {
		log.Fatal(err, " :unable to read image build response")
	}

	fmt.Println("✅ Build success")
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

func trySession(dockerCli client.CommonAPIClient, contextDir string, forStream bool) (*Session, error) {
	sharedKey := getBuildSharedKey(contextDir)
	s, err := newSession(context.Background(), filepath.Base(contextDir), sharedKey)
	if err != nil {
		return nil, errors.Wrap(err, "failed to create session")
	}
	return s, nil
}

func getBuildSharedKey(dir string) string {
	// build session is hash of build dir with node based randomness
	s := sha256.Sum256([]byte(fmt.Sprintf("%s:%s", tryNodeIdentifier(), dir)))
	return hex.EncodeToString(s[:])
}

func tryNodeIdentifier() string {
	out := cliconfig.Dir() // return config dir as default on permission error
	if err := os.MkdirAll(cliconfig.Dir(), 0700); err == nil {
		sessionFile := filepath.Join(cliconfig.Dir(), ".buildNodeID")
		if _, err := os.Lstat(sessionFile); err != nil {
			if os.IsNotExist(err) { // create a new file with stored randomness
				b := make([]byte, 32)
				if _, err := rand.Read(b); err != nil {
					return out
				}
				if err := ioutil.WriteFile(sessionFile, []byte(hex.EncodeToString(b)), 0600); err != nil {
					return out
				}
			}
		}

		dt, err := ioutil.ReadFile(sessionFile)
		if err == nil {
			return string(dt)
		}
	}
	return out
}

// NewSession returns a new long running session
func newSession(ctx context.Context, name, sharedKey string) (*Session, error) {
	id := identity.NewID()

	var unary []grpc.UnaryServerInterceptor
	var stream []grpc.StreamServerInterceptor

	serverOpts := []grpc.ServerOption{}

	if span := trace.SpanFromContext(ctx); span.SpanContext().IsValid() {
		unary = append(unary, filterServer(otelgrpc.UnaryServerInterceptor(otelgrpc.WithTracerProvider(span.TracerProvider()), otelgrpc.WithPropagators(propagators))))
		stream = append(stream, otelgrpc.StreamServerInterceptor(otelgrpc.WithTracerProvider(span.TracerProvider()), otelgrpc.WithPropagators(propagators)))
	}

	unary = append(unary, grpcerrors.UnaryServerInterceptor)
	stream = append(stream, grpcerrors.StreamServerInterceptor)

	if len(unary) == 1 {
		serverOpts = append(serverOpts, grpc.UnaryInterceptor(unary[0]))
	} else if len(unary) > 1 {
		serverOpts = append(serverOpts, grpc.UnaryInterceptor(grpc_middleware.ChainUnaryServer(unary...)))
	}

	if len(stream) == 1 {
		serverOpts = append(serverOpts, grpc.StreamInterceptor(stream[0]))
	} else if len(stream) > 1 {
		serverOpts = append(serverOpts, grpc.StreamInterceptor(grpc_middleware.ChainStreamServer(stream...)))
	}

	s := &Session{
		id:         id,
		name:       name,
		sharedKey:  sharedKey,
		grpcServer: grpc.NewServer(serverOpts...),
	}

	grpc_health_v1.RegisterHealthServer(s.grpcServer, health.NewServer())

	return s, nil
}

const (
	headerSessionID        = "X-Docker-Expose-Session-Uuid"
	headerSessionName      = "X-Docker-Expose-Session-Name"
	headerSessionSharedKey = "X-Docker-Expose-Session-Sharedkey"
	headerSessionMethod    = "X-Docker-Expose-Session-Grpc-Method"
)

var propagators = propagation.NewCompositeTextMapPropagator(propagation.TraceContext{}, propagation.Baggage{})

// Dialer returns a connection that can be used by the session
type Dialer func(ctx context.Context, proto string, meta map[string][]string) (net.Conn, error)

// Attachable defines a feature that can be exposed on a session
type Attachable interface {
	Register(*grpc.Server)
}

// ID returns unique identifier for the session
func (s *Session) ID() string {
	return s.id
}

// Run activates the session
func (s *Session) Run(ctx context.Context, dialer Dialer) error {
	ctx, cancel := context.WithCancel(ctx)
	s.cancelCtx = cancel
	s.done = make(chan struct{})

	defer cancel()
	defer close(s.done)

	meta := make(map[string][]string)
	meta[headerSessionID] = []string{s.id}
	meta[headerSessionName] = []string{s.name}
	meta[headerSessionSharedKey] = []string{s.sharedKey}

	for name, svc := range s.grpcServer.GetServiceInfo() {
		for _, method := range svc.Methods {
			meta[headerSessionMethod] = append(meta[headerSessionMethod], MethodURL(name, method.Name))
		}
	}
	conn, err := dialer(ctx, "h2c", meta)
	if err != nil {
		return errors.Wrap(err, "failed to dial gRPC")
	}
	s.conn = conn
	serve(ctx, s.grpcServer, conn)
	return nil
}

// Close closes the session
func (s *Session) Close() error {
	if s.cancelCtx != nil && s.done != nil {
		if s.conn != nil {
			s.conn.Close()
		}
		s.grpcServer.Stop()
		<-s.done
	}
	return nil
}

// Allow enables a given service to be reachable through the grpc session
func (s *Session) Allow(a Attachable) {
	a.Register(s.grpcServer)
}

// updates needed in opentelemetry-contrib to avoid this
func filterServer(intercept grpc.UnaryServerInterceptor) grpc.UnaryServerInterceptor {
	return func(ctx context.Context, req interface{}, info *grpc.UnaryServerInfo, handler grpc.UnaryHandler) (resp interface{}, err error) {
		if strings.HasSuffix(info.FullMethod, "Health/Check") {
			return handler(ctx, req)
		}
		return intercept(ctx, req, info, handler)
	}
}

// MethodURL returns a gRPC method URL for service and method name
func MethodURL(s, m string) string {
	return "/" + s + "/" + m
}

func serve(ctx context.Context, grpcServer *grpc.Server, conn net.Conn) {
	go func() {
		<-ctx.Done()
		conn.Close()
	}()
	bklog.G(ctx).Debugf("serving grpc connection")
	(&http2.Server{}).ServeConn(conn, &http2.ServeConnOpts{Handler: grpcServer})
}
