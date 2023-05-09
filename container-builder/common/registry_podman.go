/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package common

import (
	"fmt"
	"strings"
	"time"

	typesMapping "github.com/containers/common/libnetwork/types"
	"github.com/containers/podman/v4/libpod/define"
	"github.com/containers/podman/v4/pkg/bindings/containers"
	"github.com/containers/podman/v4/pkg/bindings/images"
	"github.com/containers/podman/v4/pkg/specgen"
	"github.com/sirupsen/logrus"
	"golang.org/x/net/context"
)

func (p PodmanLocalRegistry) getConnection() (context.Context, error) {
	connectionLocal := p.Connection
	if connectionLocal == nil {
		connectionLocal, err := GetRootlessPodmanConnection()
		return connectionLocal, err
	}
	return connectionLocal, nil
}

func (p PodmanLocalRegistry) StartRegistry() string {
	//wait until Docker registry shutdown in the docker tests
	for {
		time.Sleep(1 * time.Second)
		if IsPortAvailable("5000") {
			break
		}
	}
	logrus.Info("Start Registry")
	connection, err := p.getConnection()
	if err != nil {
		logrus.Info(err)
		return ""
	}

	registryID := p.GetRegistryRunningID()
	if len(registryID) > 0 {
		logrus.Info("Registry already running")
		return registryID
	}

	if !p.IsRegistryImagePresent() {
		logrus.Info("Registry Image Pull ")
		_, err := images.Pull(connection, REGISTRY_IMG, nil)
		if err != nil {
			fmt.Println(err)
			return ""
		}
	} else {
		logrus.Info("Registry Image ready")
	}

	logrus.Info("Registry Container Create")
	s := specgen.NewSpecGenerator(REGISTRY_IMG, false)
	s.Terminal = true
	s.PublishExposedPorts = true
	s.PortMappings = []typesMapping.PortMapping{
		{
			HostPort:      5000,
			ContainerPort: 5000,
			Protocol:      "tcp",
		},
	}

	r, err := containers.CreateWithSpec(connection, s, nil)
	if err != nil {
		logrus.Error(err)

		return ""
	}

	err = containers.Start(connection, r.ID, nil)
	if err != nil {
		logrus.Info("error during Start registry")
		logrus.Error(err)
		return ""
	}

	wait := define.ContainerStateRunning
	_, err = containers.Wait(connection, r.ID, new(containers.WaitOptions).WithCondition([]define.ContainerStatus{wait}))
	if err != nil {
		logrus.Error(err)
		return ""
	}
	// give some time to start
	logrus.Info("Waiting 4 sec")
	time.Sleep(4 * time.Second)
	return p.GetRegistryRunningID()
}

func (p PodmanLocalRegistry) StopRegistry() bool {
	logrus.Info("*** StopRegistry ***")
	registryID := p.GetRegistryRunningID()
	if len(registryID) > 0 {
		logrus.Info("StopRegistry Kill registry container.ID " + registryID)
		_ = containers.Kill(p.Connection, registryID, nil)
		logrus.Info("StopRegistry Removing container ID " + registryID)
		_, err := containers.Remove(p.Connection, registryID, nil)
		if err != nil {
			logrus.Info(err)
			return false
		}
	}
	return true
}

func (p PodmanLocalRegistry) StopAndRemoveContainer(containerID string) bool {
	if len(containerID) > 0 {

		killOptions := containers.KillOptions{}
		killOptions.WithSignal("SIGKILL")
		err := containers.Kill(p.Connection, containerID, &killOptions)
		result := false
		if err == nil {
			logrus.Infof("StopAndRemoveContainer Kill registry container container.ID %s", containerID)
		} else {
			logrus.Infof("topAndRemoveContainer Error during Kill registry container with container.ID %s error %s", containerID, err)
		}
		_, err = containers.Remove(p.Connection, containerID, nil)
		if err == nil {
			logrus.Info("Removing container ID " + containerID)
			result = true
		} else {
			logrus.Errorf("Error during Remove registry container with container.ID %s error %s", containerID, err)
			result = false
		}
		return result
	}
	logrus.Info("StopAndRemoveContainer Invalid container ID " + containerID)
	return true
}

func (p PodmanLocalRegistry) GetRegistryRunningID() string {
	containerList, err := containers.List(p.Connection, nil)
	if err != nil {
		logrus.Errorf("%s %s\n", err, "Error during container list")
		return ""
	}
	for _, container := range containerList {
		if container.Image == REGISTRY_IMG_FULL_TAG {
			return container.ID
		}
	}
	return ""
}

func (p PodmanLocalRegistry) IsRegistryImagePresent() bool {
	imageList, err := images.List(p.Connection, nil)
	if err != nil {
		return false
	}
	for _, imagex := range imageList {
		if len(imagex.Names) > 0 && strings.HasPrefix(imagex.Names[0], REGISTRY_IMG_FULL) {
			return true
		}
	}
	return false
}

func (p PodmanLocalRegistry) IsRegistryRunning() bool {
	containersList, err := containers.List(p.Connection, nil)
	if err != nil {
		fmt.Println(err)
	}

	for _, container := range containersList {
		if strings.HasPrefix(container.Image, REGISTRY_IMG_FULL) {
			logrus.Info("Registry container already running...")
			return true
		}
	}
	return false
}

func (p PodmanLocalRegistry) RemoveRegistryContainerAndImage() {
	containerList, _ := containers.List(p.Connection, nil)
	for _, container := range containerList {
		if container.Image == REGISTRY_IMG {
			_ = containers.Stop(context.Background(), container.ID, nil)
			_ = containers.Kill(context.Background(), container.ID, nil)
			_, _ = containers.Remove(context.Background(), container.ID, nil)
		}
	}
}

func SetupPodmanSocket() (PodmanLocalRegistry, string, Podman) {
	podmanSocketConn, err := GetRootlessPodmanConnection()
	if err != nil {
		logrus.Errorf("Can't get Podman socket")
		return PodmanLocalRegistry{}, "", Podman{}
	}
	podmanSock := Podman{Connection: podmanSocketConn}
	podmanSock.PurgeContainer("", REGISTRY_IMG_FULL)

	connectionLocal, err := GetRootlessPodmanConnection()
	if err != nil {
		fmt.Printf("%s \n", err)
	}
	p := PodmanLocalRegistry{Connection: connectionLocal}
	if !p.IsRegistryImagePresent() {
		podmanSock.PullImage(TEST_IMG_TAG)
	}
	registryID := p.GetRegistryRunningID()
	if len(registryID) == 0 {
		registryID = p.StartRegistry()
	} else {
		logrus.Infof("Registry already up and running with ID %s", registryID)
	}
	return p, registryID, podmanSock
}

func PodmanTearDown(plr PodmanLocalRegistry) {
	if len(plr.GetRegistryRunningID()) > 0 {
		registryID := plr.GetRegistryRunningID()
		plr.StopAndRemoveContainer(registryID)
	} else {
		plr.StopRegistry()
	}
}
