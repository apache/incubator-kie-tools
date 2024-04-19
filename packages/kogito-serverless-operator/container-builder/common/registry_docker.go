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

package common

import (
	"fmt"
	"strings"
	"time"

	"k8s.io/klog/v2"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"
	"github.com/docker/go-connections/nat"
	"golang.org/x/net/context"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/util/log"
)

func GetDockerConnection() (*client.Client, error) {
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		fmt.Println(err)
		return nil, err
	}
	return cli, nil
}

func (d DockerLocalRegistry) getConnection() (*client.Client, error) {
	connectionLocal := d.Connection
	if connectionLocal == nil {
		return GetDockerConnection()
	}
	return connectionLocal, nil
}

func (d DockerLocalRegistry) StartRegistry() string {
	//wait until Podman registry shutdown in the podman tests
	for {
		time.Sleep(1 * time.Second)
		if IsPortAvailable("5000") {
			break
		}
	}

	ctx := context.Background()
	registryID := d.GetRegistryRunningID()

	if len(registryID) > 0 {
		klog.V(log.I).InfoS("Registry ID is already running", "ID", registryID)
		return registryID
	}

	if !d.IsRegistryImagePresent() {
		klog.V(log.I).InfoS("Registry Image Pull")
		_, err := d.Connection.ImagePull(ctx, RegistryImg, types.ImagePullOptions{})
		if err != nil {
			fmt.Println(err)
			return ""
		}
	}

	time.Sleep(2 * time.Second) // needed on CI

	klog.V(log.I).InfoS("Registry Container Create")
	resp, err := d.Connection.ContainerCreate(ctx, &container.Config{
		Image:        RegistryImg,
		ExposedPorts: nat.PortSet{"5000": struct{}{}},
	},
		&container.HostConfig{
			PortBindings: map[nat.Port][]nat.PortBinding{nat.Port("5000"): {{HostIP: "127.0.0.1", HostPort: "5000"}}},
		},
		nil,
		nil,
		RegistryImg)

	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Registry Container Create")
	}

	klog.V(log.I).InfoS("Starting Registry Container")
	if err := d.Connection.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
		klog.V(log.E).ErrorS(err, "error during Start Registry Container")
		return ""
	}

	// give some time to start
	klog.V(log.I).InfoS("Waiting 4 seconds")
	time.Sleep(4 * time.Second)
	return d.GetRegistryRunningID()
}

func (d DockerLocalRegistry) StopRegistry() bool {
	registryID := d.GetRegistryRunningID()
	if len(registryID) > 0 {
		klog.V(log.I).InfoS("StopRegistry Kill Container", "ID", registryID)
		ctx := context.Background()
		_ = d.Connection.ContainerKill(ctx, registryID, "SIGKILL")
		klog.V(log.I).InfoS("StopRegistry Removing Container", "ID", registryID)
		err := d.Connection.ContainerRemove(ctx, registryID, types.ContainerRemoveOptions{})
		if err != nil {
			klog.V(log.E).ErrorS(err, "error during Stop Registry")
			return false
		}
	}
	return true
}

func (d DockerLocalRegistry) StopAndRemoveContainer(containerID string) bool {
	if len(containerID) > 0 {
		ctx := context.Background()
		klog.V(log.I).InfoS("Docker StopAndRemoveContainer Kill registry container", "ID", containerID)
		_ = d.Connection.ContainerKill(ctx, containerID, "SIGKILL")
		klog.V(log.I).InfoS("Docker StopAndRemoveContainer Removing container", "ID", containerID)
		err := d.Connection.ContainerRemove(ctx, containerID, types.ContainerRemoveOptions{})
		return err == nil
	}
	fmt.Println("Docker StopAndRemoveContainer Invalid ID " + containerID)
	return true
}

func (d DockerLocalRegistry) GetRegistryRunningID() string {
	containers, err := d.Connection.ContainerList(context.Background(), types.ContainerListOptions{})
	if err != nil {
		fmt.Println(err)
		return ""
	}

	for _, container := range containers {
		if container.Image == RegistryImg {
			return container.ID
		}
	}
	return ""
}

func (d DockerLocalRegistry) IsRegistryImagePresent() bool {

	imageList, err := d.Connection.ImageList(context.Background(), types.ImageListOptions{})
	if err != nil {
		return false
	}
	for _, imagex := range imageList {
		if (len(imagex.RepoTags) > 0 && imagex.RepoTags[0] == RegistryImg) || (len(imagex.RepoDigests) > 0 && strings.HasPrefix(imagex.RepoDigests[0], RegistryImg)) {
			return true
		}
	}
	return false
}

func (d DockerLocalRegistry) IsImagePresent(name string) bool {

	imageList, err := d.Connection.ImageList(context.Background(), types.ImageListOptions{})
	if err != nil {
		return false
	}
	for _, imagex := range imageList {
		if len(imagex.RepoTags) == 0 || len(imagex.RepoDigests) == 0 {
			continue
		}
		if imagex.RepoTags[0] == name || (imagex.RepoDigests != nil && strings.HasPrefix(imagex.RepoDigests[0], name)) {
			return true
		}
	}
	return false
}

func SetupDockerSocket() (DockerLocalRegistry, string, Docker) {
	dockerSocketConn, err := GetDockerConnection()

	if err != nil {
		klog.V(log.E).ErrorS(err, "Can't get Docker socket")
		return DockerLocalRegistry{}, "", Docker{}
	}
	dockerSock := Docker{Connection: dockerSocketConn}

	if err != nil {
		klog.V(log.E).ErrorS(err, "error during SetupDockerSocket")
	}
	_, err = dockerSock.PurgeContainer("", RegistryImg)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during SetupDockerSocket")
	}

	d := DockerLocalRegistry{Connection: dockerSocketConn}
	klog.V(log.I).InfoS("Check if registry image is present", "isPresent", d.IsRegistryImagePresent())
	if !d.IsRegistryImagePresent() {
		dockerSock.PullImage(registryImgFullTag)
	}
	registryID := d.GetRegistryRunningID()
	if len(registryID) == 0 {
		registryID = d.StartRegistry()
		klog.V(log.I).InfoS("Registry started")
	} else {
		klog.V(log.I).InfoS("Registry already up and running", "ID", registryID)
	}
	return d, registryID, dockerSock

}

func DockerTearDown(dlr DockerLocalRegistry) {
	if len(dlr.GetRegistryRunningID()) > 0 {
		registryID := dlr.GetRegistryRunningID()
		dlr.StopAndRemoveContainer(registryID)
	} else {
		dlr.StopRegistry()
	}
}
