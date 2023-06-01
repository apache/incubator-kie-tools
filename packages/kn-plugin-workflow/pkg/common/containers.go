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
	"fmt"
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"os"
	"os/exec"
	"os/signal"
	"strings"
	"syscall"
)

const (
	Docker = "docker"
	Podman = "podman"
)

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
		fmt.Sprintf("ancestor=%s", metadata.KogitoImage),
		"--format", "{{.ID}}")
	output, err := cmd.CombinedOutput()
	if err != nil {
		return "", fmt.Errorf("error getting container id: %w", err)
	}
	containerID := strings.TrimSpace(string(output))
	return containerID, nil
}

func getDockerContainerID() (string, error) {
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return "", err
	}

	containers, err := cli.ContainerList(context.Background(), types.ContainerListOptions{})
	if err != nil {
		return "", err
	}

	for _, container := range containers {
		// Check if the container has the expected image name or other identifying information
		if strings.Contains(container.Image, metadata.KogitoImage) {
			return container.ID, nil
		}
	}

	return "", fmt.Errorf("no matching container found")
}

func StopContainer(containerTool string, containerID string) error {
	stopCmd := exec.Command(containerTool, "stop", containerID)
	err := stopCmd.Run()
	if err != nil {
		fmt.Printf("Error stopping container: %v\n", err)
		return err
	}
	fmt.Printf("Container %s stopped successfully.\n", containerID)
	return nil
}

func RunContainerCommand(containerTool string, portMapping string, path string) *exec.Cmd {
	fmt.Println("🕒 Warming up Kogito containers, this could take some time...")
	return exec.Command(
		containerTool,
		"run",
		"--rm",
		"-p",
		fmt.Sprintf("%s:8080", portMapping),
		"-v",
		fmt.Sprintf("%s:/home/kogito/serverless-workflow-project/src/main/resources:z", path),
		fmt.Sprintf("%s", metadata.KogitoImage),
	)
}

func GracefullyStopTheContainerWhenInterrupted(containerTool string) {
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt, syscall.SIGTERM)

	go func() {
		<-c // Wait for the interrupt signal

		containerID, err := GetContainerID(containerTool)
		if err != nil {
			fmt.Printf("error getting container id: %v\n", err)
			os.Exit(1) // Exit the program with error
		}

		fmt.Println("🕒 Stopping the container id: " + containerID)
		if containerID != "" {
			err := StopContainer(containerTool, containerID)
			if err != nil {
				fmt.Println("❌ Error stopping container id: " + containerID)
				os.Exit(1)
			} else {
				fmt.Println("✅ Successfully stopped container id: " + containerID)
			}
		}

		os.Exit(0) // Exit the program gracefully
	}()
}
