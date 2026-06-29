/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package builder

import (
	"context"
	"os"

	"k8s.io/klog/v2"

	"github.com/moby/moby/api/pkg/stdcopy"
	"github.com/moby/moby/api/types/container"
	"github.com/moby/moby/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/util/log"
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

const executorImage = "gcr.io/kaniko-project/executor:latest"

func KanikoBuild(connection *client.Client, config KanikoVanillaConfig) (string, error) {

	ctx := context.Background()

	createOpts := client.ContainerCreateOptions{
		Name: config.ContainerName,
		Config: &container.Config{
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
		},
		HostConfig: &container.HostConfig{
			NetworkMode: "host",
			Binds: []string{
				config.DockerFilePath + ":/workspace",
			},
		},
	}

	resp, err := connection.ContainerCreate(ctx, createOpts)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during KanikoBuild, ContainerCreate")
	}

	if _, err := connection.ContainerStart(ctx, resp.ID, client.ContainerStartOptions{}); err != nil {
		klog.V(log.E).ErrorS(err, "error during KanikoBuild, ContainerStart")
	}

	waitResult := connection.ContainerWait(ctx, resp.ID, client.ContainerWaitOptions{
		Condition: container.WaitConditionNotRunning,
	})

	select {
	case err := <-waitResult.Error:
		if err != nil {
			klog.V(log.E).ErrorS(err, "error during KanikoBuild, ContainerWait")
		}
	case <-waitResult.Result:
	}

	logsResult, err := connection.ContainerLogs(ctx, resp.ID, client.ContainerLogsOptions{ShowStdout: true})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during KanikoBuild, ContainerLogs")
	}
	if config.ReadBuildOutput {
		stdcopy.StdCopy(os.Stdout, os.Stderr, logsResult)
	}
	return resp.ID, err
}
