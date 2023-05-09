/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package builder

import (
	"context"
	"os"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/stdcopy"
	"github.com/sirupsen/logrus"
)

type KanikoVanillaConfig struct {
	DockerFilePath         string
	KanikoExecutorImage    string
	DockerFileName         string
	RegistryFinalImageName string
	VerbosityLevel         string
	ContainerName          string
	ReadBuildOutput        bool
}

const EXECUTOR_IMAGE = "gcr.io/kaniko-project/executor:latest"

func KanikoBuild(connection *client.Client, config KanikoVanillaConfig) (string, error) {

	hostConfig := &container.HostConfig{
		NetworkMode: "host",
		Binds: []string{
			config.DockerFilePath + ":/workspace",
		},
	}

	ctx := context.Background()
	resp, err := connection.ContainerCreate(ctx, &container.Config{
		Image: config.KanikoExecutorImage,
		Cmd: []string{
			"-f", config.DockerFileName,
			"-d", config.RegistryFinalImageName,
			"-c", "/workspace",
			"--force",
			"--verbosity", config.VerbosityLevel,
		},
		Tty:     false,
		Volumes: map[string]struct{}{},
	}, hostConfig, nil, nil, config.ContainerName)

	if err != nil {
		logrus.Error(err)
	}

	if err := connection.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
		logrus.Error(err)
	}
	if err != nil {
		logrus.Error(err)
	}

	statusCh, errCh := connection.ContainerWait(ctx, resp.ID, container.WaitConditionNotRunning)
	select {
	case err := <-errCh:
		if err != nil {
			logrus.Error(err)
		}
	case <-statusCh:
	}

	out, err := connection.ContainerLogs(ctx, resp.ID, types.ContainerLogsOptions{ShowStdout: true})
	if err != nil {
		logrus.Error(err)
	}
	if config.ReadBuildOutput {
		stdcopy.StdCopy(os.Stdout, os.Stderr, out)
	}
	return resp.ID, err
}
