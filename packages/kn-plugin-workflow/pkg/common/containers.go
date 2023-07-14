/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package common

import (
	"context"
	"errors"
	"fmt"
	"io"
	"os"
	"os/exec"
	"os/signal"
	"strings"
	"syscall"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/stdcopy"
	"github.com/docker/go-connections/nat"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
)

const (
	Docker = "docker"
	Podman = "podman"
)

func getDockerClient() (*client.Client, error) {
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return nil, err
	}
	return cli, nil
}

func GetContainerID(containerTool string) (string, error) {

	switch containerTool {
	case Docker:
		return getDockerContainerID()
	case Podman:
		return getPodmanContainerID()
	default:
		return "", fmt.Errorf("no matching container type found")
	}
}

func getPodmanContainerID() (string, error) {
	cmd := exec.Command("podman",
		"ps",
		"-a",
		"--filter",
		fmt.Sprintf("ancestor=%s", metadata.DevModeImage),
		"--format", "{{.ID}}")
	output, err := cmd.CombinedOutput()
	if err != nil {
		return "", fmt.Errorf("error getting container id: %w", err)
	}
	containerID := strings.TrimSpace(string(output))
	return containerID, nil
}

func getDockerContainerID() (string, error) {
	cli, err := getDockerClient()
	if err != nil {
		return "", err
	}

	containers, err := cli.ContainerList(context.Background(), types.ContainerListOptions{})
	if err != nil {
		return "", err
	}

	for _, container := range containers {
		// Check if the container has the expected image name or other identifying information
		if strings.Contains(container.Image, metadata.DevModeImage) {
			return container.ID, nil
		}
	}

	return "", fmt.Errorf("no matching container found")
}

func StopContainer(containerTool string, containerID string) error {
	fmt.Printf("⏳ Stopping %s container.\n", containerID)
	if containerTool == Podman {
		stopCmd := exec.Command(containerTool, "stop", containerID)
		if err := stopCmd.Run(); err != nil {
			fmt.Printf("Unable to stop container %s: %s", containerID, err)
			return err
		}
	} else if containerTool == Docker {
		cli, err := getDockerClient()
		if err != nil {
			fmt.Printf("unable to create client for docker")
			return err
		}
		if err := cli.ContainerStop(context.Background(), containerID, container.StopOptions{}); err != nil {
			fmt.Printf("Unable to stop container %s: %s", containerID, err)
			return err
		}
	} else {
		return errors.New(fmt.Sprintf("The specified containerTool:%s does not exist", containerTool))
	}
	fmt.Printf("🛑 Container %s stopped successfully.\n", containerID)
	return nil
}

func RunContainerCommand(containerTool string, portMapping string, path string) error {
	fmt.Printf("🔎 Warming up SonataFlow containers (%s), this could take some time...\n", metadata.DevModeImage)
	if containerTool == Podman {
		if err := RunCommand(
			exec.Command(
				containerTool,
				"run",
				"--rm",
				"-p",
				fmt.Sprintf("%s:8080", portMapping),
				"-v",
				fmt.Sprintf("%s:%s", path, metadata.VolumeBindPath),
				fmt.Sprintf("%s", metadata.DevModeImage),
			),
			"container run",
		); err != nil {
			return err
		}
	} else if containerTool == Docker {
		if err := runDockerContainer(portMapping, path); err != nil {
			return err
		}
	} else {
		return errors.New(fmt.Sprintf("The specified containerTool:%s does not exist", containerTool))
	}
	return nil
}

func GracefullyStopTheContainerWhenInterrupted(containerTool string) {
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt, syscall.SIGTERM)

	go func() {
		<-c // Wait for the interrupt signal

		containerID, err := GetContainerID(containerTool)
		if err != nil {
			fmt.Printf("\nerror getting container id: %v\n", err)
			os.Exit(1) // Exit the program with error
		}

		fmt.Println("🔨 Stopping the container id: " + containerID)
		if containerID != "" {
			err := StopContainer(containerTool, containerID)
			if err != nil {
				fmt.Println("❌ ERROR: Error stopping container id: " + containerID)
				os.Exit(1)
			} else {
				fmt.Println("🎉 Successfully stopped container id: " + containerID)
			}
		}

		os.Exit(0) // Exit the program gracefully
	}()
}

func runDockerContainer(portMapping string, path string) error {
	ctx := context.Background()
	cli, err := getDockerClient()
	if err != nil {
		return err
	}
	reader, err := cli.ImagePull(ctx, metadata.DevModeImage, types.ImagePullOptions{})
	if err != nil {
		fmt.Printf("\nError pulling image: %s. Error is: %s", metadata.DevModeImage, err)
		return err
	}
	io.Copy(os.Stdout, reader)

	containerConfig := &container.Config{
		Image: metadata.DevModeImage,
	}
	hostConfig := &container.HostConfig{
		AutoRemove: true,
		PortBindings: nat.PortMap{
			metadata.DockerInternalPort: []nat.PortBinding{
				{
					HostIP:   "0.0.0.0",
					HostPort: portMapping,
				},
			},
		},
		Binds: []string{
			fmt.Sprintf("%s:%s", path, metadata.VolumeBindPath),
		},
	}

	resp, err := cli.ContainerCreate(ctx, containerConfig, hostConfig, nil, nil, "")

	if err != nil {
		fmt.Printf("\nUnable to create container %s: %s", metadata.DevModeImage, err)
		return err
	}

	fmt.Printf("\nCreated container with ID %s", resp.ID)

	if err := cli.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
		fmt.Printf("Unable to start container %s", resp.ID)
		return err
	}
	fmt.Printf("\nSuccessfully started the container %s", resp.ID)

	statusCh, errCh := cli.ContainerWait(ctx, resp.ID, container.WaitConditionNotRunning)
	select {
	case err := <-errCh:
		if err != nil {
			fmt.Printf("\nError starting the container %s", resp.ID)
			return err
		}
	case <-statusCh:
	}

	out, err := cli.ContainerLogs(ctx, resp.ID, types.ContainerLogsOptions{ShowStdout: true})
	if err != nil {
		return err
	}

	stdcopy.StdCopy(os.Stdout, os.Stderr, out)

	return nil
}
