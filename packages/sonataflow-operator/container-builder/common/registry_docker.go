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
	"context"
	"fmt"
	"net/netip"
	"strings"
	"time"

	"k8s.io/klog/v2"

	"github.com/moby/moby/api/types/container"
	"github.com/moby/moby/api/types/network"
	"github.com/moby/moby/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/util/log"
)

func GetDockerConnection() (*client.Client, error) {
	cli, err := client.New(client.FromEnv)
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
		result, err := d.Connection.ImagePull(ctx, registryImgFullTag, client.ImagePullOptions{})
		if err != nil {
			fmt.Println(err)
			return ""
		}
		defer result.Close()
		if err := result.Wait(ctx); err != nil {
			fmt.Println(err)
			return ""
		}
	}

	time.Sleep(2 * time.Second) // needed on CI

	klog.V(log.I).InfoS("Registry Container Create")

	// Setup port mappings using network types
	port := network.MustParsePort("5000/tcp")
	hostIP := netip.MustParseAddr("127.0.0.1")
	portBindings := network.PortMap{
		port: []network.PortBinding{
			{
				HostIP:   hostIP,
				HostPort: "5000",
			},
		},
	}

	exposedPorts := network.PortSet{
		port: struct{}{},
	}

	createOpts := client.ContainerCreateOptions{
		Name:  RegistryImg,
		Config: &container.Config{
			Image:        registryImgFullTag,
			ExposedPorts: exposedPorts,
		},
		HostConfig: &container.HostConfig{
			PortBindings: portBindings,
		},
	}

	resp, err := d.Connection.ContainerCreate(ctx, createOpts)

	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Registry Container Create")
		return ""
	}

	klog.V(log.I).InfoS("Starting Registry Container")
	if _, err := d.Connection.ContainerStart(ctx, resp.ID, client.ContainerStartOptions{}); err != nil {
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
		killOpts := client.ContainerKillOptions{Signal: "SIGKILL"}
		_, _ = d.Connection.ContainerKill(ctx, registryID, killOpts)
		klog.V(log.I).InfoS("StopRegistry Removing Container", "ID", registryID)
		_, err := d.Connection.ContainerRemove(ctx, registryID, client.ContainerRemoveOptions{})
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
		killOpts := client.ContainerKillOptions{Signal: "SIGKILL"}
		_, _ = d.Connection.ContainerKill(ctx, containerID, killOpts)
		klog.V(log.I).InfoS("Docker StopAndRemoveContainer Removing container", "ID", containerID)
		_, err := d.Connection.ContainerRemove(ctx, containerID, client.ContainerRemoveOptions{})
		return err == nil
	}
	fmt.Println("Docker StopAndRemoveContainer Invalid ID " + containerID)
	return true
}

func (d DockerLocalRegistry) GetRegistryRunningID() string {
	result, err := d.Connection.ContainerList(context.Background(), client.ContainerListOptions{})
	if err != nil {
		fmt.Println(err)
		return ""
	}

	for _, ctr := range result.Items {
		// Check if the container image matches any registry image variant
		if ctr.Image == RegistryImg ||
			ctr.Image == "registry:latest" ||
			ctr.Image == registryImgFullTag ||
			strings.HasPrefix(ctr.Image, "registry:") ||
			strings.HasPrefix(ctr.Image, "docker.io/library/registry:") {
			return ctr.ID
		}
	}
	return ""
}

func (d DockerLocalRegistry) IsRegistryImagePresent() bool {

	result, err := d.Connection.ImageList(context.Background(), client.ImageListOptions{})
	if err != nil {
		return false
	}
	for _, img := range result.Items {
		// Check for any of the registry image tag variants
		for _, tag := range img.RepoTags {
			if tag == RegistryImg || tag == registryImgFullTag || strings.HasPrefix(tag, "registry:") || strings.HasPrefix(tag, "docker.io/library/registry:") {
				return true
			}
		}
		// Also check digests
		if len(img.RepoDigests) > 0 && strings.HasPrefix(img.RepoDigests[0], "registry@") {
			return true
		}
	}
	return false
}

func (d DockerLocalRegistry) IsImagePresent(name string) bool {

	result, err := d.Connection.ImageList(context.Background(), client.ImageListOptions{})
	if err != nil {
		return false
	}
	for _, img := range result.Items {
		if len(img.RepoTags) == 0 || len(img.RepoDigests) == 0 {
			continue
		}
		if img.RepoTags[0] == name || (img.RepoDigests != nil && strings.HasPrefix(img.RepoDigests[0], name)) {
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
