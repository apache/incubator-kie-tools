/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
	"bufio"
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/distribution/reference"
	"github.com/docker/docker/api/types/image"
	"io"
	"os"
	"os/exec"
	"os/signal"
	"runtime"
	"strings"
	"syscall"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/stdcopy"
	"github.com/docker/go-connections/nat"
)

const (
	Docker = "docker"
	Podman = "podman"
)

type DockerLogMessage struct {
	Status string `json:"status,omitempty"`
	ID     string `json:"id,omitempty"`
}

type DockerClient interface {
	ImageList(ctx context.Context, options image.ListOptions) ([]image.Summary, error)
}

func getDockerClient() (*client.Client, error) {
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		return nil, fmt.Errorf("failed to create Docker client: %s", err)
	}
	return cli, nil
}

func GetContainerID(containerTool string) (string, error) {

	switch containerTool {
	case Podman:
		return getPodmanContainerID()
	case Docker:
		return getDockerContainerID()
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
		"--filter",
		"status=running",
		"--format", "{{.ID}}")
	fmt.Println(cmd)
	output, err := cmd.CombinedOutput()
	if err != nil {
		return "", fmt.Errorf("error getting podman container id: %w", err)
	}
	containerID := strings.TrimSpace(string(output))
	return containerID, nil
}

func getDockerContainerID() (string, error) {
	cli, err := getDockerClient()
	if err != nil {
		return "", err
	}

	containers, err := cli.ContainerList(context.Background(), container.ListOptions{})
	if err != nil {
		return "", err
	}

	for _, c := range containers {
		// Check if the container has the expected image name or other identifying information
		if strings.Contains(c.Image, metadata.DevModeImage) {
			return c.ID, nil
		}
	}

	return "", fmt.Errorf("no matching container found")
}

func StopContainer(containerTool string, containerID string) error {
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
	fmt.Printf("\n🛑 Container %s stopped successfully.\n", containerID)
	return nil
}

func resolveVolumeBindPath(containerTool string) string {
	if containerTool == "podman" && runtime.GOOS == "linux" {
		return metadata.VolumeBindPathSELinux
	}
	return metadata.VolumeBindPath
}

func RunContainerCommand(containerTool string, portMapping string, path string) error {
	volumeBindPath := resolveVolumeBindPath(containerTool)
	fmt.Printf("🔎 Warming up SonataFlow containers (%s), this could take some time...\n", metadata.DevModeImage)
	if containerTool == Podman {
		c := exec.Command(
			containerTool,
			"run",
			"--rm",
			"-p",
			fmt.Sprintf("%s:8080", portMapping),
			"-v",
			fmt.Sprintf("%s:%s", path, volumeBindPath),
			fmt.Sprintf("%s", metadata.DevModeImage),
		)
		if err := RunCommand(
			c,
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

		fmt.Println("\n🔨 Stopping the container id: " + containerID)
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

func pullDockerImage(cli *client.Client, ctx context.Context) error {
	// Check if the image exists locally.
	exists, err := CheckImageExists(cli, ctx, metadata.DevModeImage)
	if err != nil {
		return fmt.Errorf("error listing images: %s", err)
	}

	// If the image is not found locally, pull it from the remote registry
	if !exists {
		fmt.Printf("\n⏳ Retrieving (%s), this could take some time...\n", metadata.DevModeImage)

		ctx, cancel := context.WithTimeout(ctx, 1*time.Minute)
		defer cancel()

		reader, writer := io.Pipe()
		defer writer.Close()

		var stderr bytes.Buffer

		go func() {
			scanner := bufio.NewScanner(reader)
			for scanner.Scan() {
				fmt.Print(".")
			}
		}()

		// we use local docker client to pull the image
		cmd := exec.CommandContext(ctx, "docker", "pull", metadata.DevModeImage)
		cmd.Stdout = writer
		cmd.Stderr = &stderr

		if err := cmd.Start(); err != nil {
			return fmt.Errorf("\nError pulling image: %s. Error is: %s", metadata.DevModeImage, err)
		}

		if err := cmd.Wait(); err != nil {
			return fmt.Errorf("\nError pulling image: %s. Error is: %s", metadata.DevModeImage, stderr.String())
		}
		fmt.Println("\n🎉 Successfully pulled the image")
	}

	return nil
}

func CheckImageExists(cli DockerClient, ctx context.Context, imageName string) (bool, error) {
	named, err := reference.ParseNormalizedNamed(imageName)

	if tagged, ok := named.(reference.Tagged); ok {
		imageName = fmt.Sprintf("%s:%s", reference.Path(named), tagged.Tag())
	} else {
		imageName = fmt.Sprintf("%s:%s", reference.Path(named), "latest")
	}
	images, err := cli.ImageList(ctx, image.ListOptions{All: true})
	if err != nil {
		return false, fmt.Errorf("error listing images: %s", err)
	}

	for _, i := range images {
		for _, tag := range i.RepoTags {
			if strings.HasSuffix(tag, imageName) {
				return true, nil
			}
		}
	}
	return false, nil
}

func processDockerImagePullLogs(reader io.ReadCloser) error {
	for {
		err := waitToImageBeReady(reader)
		if err == io.EOF {
			break
		} else if err != nil {
			return fmt.Errorf("error decoding ImagePull JSON: %s", err)
		}
	}
	return nil
}

func waitToImageBeReady(reader io.ReadCloser) error {
	var message DockerLogMessage
	decoder := json.NewDecoder(reader)
	if err := decoder.Decode(&message); err != nil {
		return err
	}
	if message.Status != "" {
		fmt.Print(".")
	}

	return nil
}

func createDockerContainer(cli *client.Client, ctx context.Context, portMapping string, path string) (container.CreateResponse, error) {
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
		return resp, fmt.Errorf("\nUnable to create container %s: %s", metadata.DevModeImage, err)
	}
	return resp, nil
}

func startDockerContainer(cli *client.Client, ctx context.Context, resp container.CreateResponse) error {
	fmt.Printf("\nCreated container with ID %s", resp.ID)
	fmt.Println("\n⏳ Starting your container and SonataFlow project...")

	if err := cli.ContainerStart(ctx, resp.ID, container.StartOptions{}); err != nil {
		return fmt.Errorf("\nUnable to start container %s", resp.ID)
	}

	return nil
}

func runDockerContainer(portMapping string, path string) error {
	ctx, cancel := context.WithTimeout(context.Background(), 1*time.Minute)
	defer cancel()

	cli, err := getDockerClient()
	if err != nil {
		return err
	}
	err = pullDockerImage(cli, ctx)
	if err != nil {
		return err
	}

	resp, err := createDockerContainer(cli, ctx, portMapping, path)
	if err != nil {
		return err
	}

	if err := startDockerContainer(cli, ctx, resp); err != nil {
		return err
	}

	if err := processOutputDuringContainerExecution(cli, ctx, resp); err != nil {
		return err
	}

	return nil
}
func processOutputDuringContainerExecution(cli *client.Client, ctx context.Context, resp container.CreateResponse) error {
	statusCh, errCh := cli.ContainerWait(ctx, resp.ID, container.WaitConditionNotRunning)

	//Print all container logs
	out, err := cli.ContainerLogs(ctx, resp.ID, container.LogsOptions{ShowStdout: false, ShowStderr: true, Follow: true})
	if err != nil {
		return fmt.Errorf("\nError getting container logs: %s", err)
	}

	go func() {
		_, err := stdcopy.StdCopy(os.Stdout, os.Stderr, out)
		if err != nil {
			fmt.Errorf("\nError copying container logs to stdout: %s", err)
		}
	}()

	select {
	case err := <-errCh:
		if err != nil {
			return fmt.Errorf("\nError starting the container %s: %s", resp.ID, err)
		}
	case <-statusCh:
		//state of the container matches the condition, in our case WaitConditionNotRunning
	}

	return nil
}

func PollContainerStoppedCheck(containerID string, interval time.Duration, ready chan<- bool) {
	for {
		running, err := IsContainerRunning(containerID)
		if err != nil {
			fmt.Printf("Error checking if container %s is running: %s", containerID, err)
			ready <- false
			return
		}
		if !running {
			ready <- true
			return
		}
		time.Sleep(interval)
	}
}

func IsContainerRunning(containerID string) (bool, error) {
	if errDocker := CheckDocker(); errDocker == nil {
		cli, err := getDockerClient()
		if err != nil {
			return false, fmt.Errorf("unable to create docker client: %w", err)
		}
		containerJSON, err := cli.ContainerInspect(context.Background(), containerID)
		if err != nil {
			if client.IsErrNotFound(err) {
				return false, nil
			}
			return false, fmt.Errorf("unable to inspect container %s with docker: %w", containerID, err)
		}
		return containerJSON.State.Running, nil

	} else if errPodman := CheckPodman(); errPodman == nil {
		cmd := exec.Command("podman", "inspect", containerID, "--format", "{{.State.Running}}")
		output, err := cmd.Output()
		if err != nil {
			return false, fmt.Errorf("unable to inspect container %s with podman: %w", containerID, err)
		}
		return strings.TrimSpace(string(output)) == "true", nil
	}

	return false, fmt.Errorf("there is no docker or podman available")
}
