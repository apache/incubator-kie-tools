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
package common

import (
	"context"
	"os"

	"github.com/containers/podman/v4/pkg/bindings"
	"github.com/containers/podman/v4/pkg/bindings/containers"
	"github.com/containers/podman/v4/pkg/bindings/images"
	"github.com/containers/podman/v4/pkg/domain/entities"
	"github.com/sirupsen/logrus"
)

/*
 A valid URI connection should be scheme://
 For example tcp://localhost:<port>
 or unix:///run/podman/podman.sock
 or ssh://<user>@<host>[:port]/run/podman/podman.sock?secure=True
*/

func GetRootlessPodmanConnection() (context.Context, error) {
	// ROOTLESS access
	sockDir := os.Getenv("XDG_RUNTIME_DIR")
	socket := "unix:" + sockDir + "/podman/podman.sock"
	conn, err := bindings.NewConnection(context.Background(), socket)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return nil, err
	}
	return conn, err
}

func GetRootPodmanConnection() (context.Context, error) {
	socket := "unix:/run/podman/podman.sock"
	conn, err := bindings.NewConnection(context.Background(), socket)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return nil, err
	}
	return conn, err
}

type Podman struct {
	Connection context.Context
}

func (p Podman) GetRemoteConnection(uri string, identity string) (context.Context, error) {
	conn, err := bindings.NewConnectionWithIdentity(context.Background(), uri, identity, true)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return nil, err
	}
	return conn, err
}

func (p Podman) GetImages(options *images.ListOptions) []*entities.ImageSummary {
	imageList, err := images.List(p.Connection, options)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return nil
	}
	return imageList
}

func (p Podman) PruneImages() (bool, error) {
	options := new(images.PruneOptions).WithAll(true)
	_, err := images.Prune(p.Connection, options)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return false, err
	}
	return true, nil
}

func (p Podman) PruneContainers() (bool, error) {
	options := new(containers.PruneOptions)
	_, err := containers.Prune(p.Connection, options)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return false, err
	}
	return true, nil
}

func (p Podman) PurgeContainer(id string, image string) (bool, error) {
	var filter = new(bool)
	*filter = true
	containers, err := containers.List(p.Connection, &containers.ListOptions{All: filter})
	if err != nil {
		logrus.Errorf("%s %s\n", err, "Error during container list")
	}

	for _, container := range containers {
		if container.Image == image || container.ID == id {
			p.ContainerKill(container.ID)
			p.ContainerRemove(container.ID)
			logrus.Infof("Purged container with ID %s", container.ID)
		}
	}
	return true, nil
}

func (p Podman) RemoveImage(name string, force bool) (*entities.ImageRemoveReport, []error) {
	return images.Remove(
		p.Connection,
		[]string{name},
		new(images.RemoveOptions).WithForce(force))
}

func (p Podman) SearchImagesWithName(term string) ([]entities.ImageSearchReport, error) {
	imageList, err := images.Search(p.Connection, term, nil)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return nil, err
	}
	return imageList, nil
}

func (p Podman) ExistsImage(term string) (bool, error) {
	result, err := images.Exists(p.Connection, term, nil)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return false, err
	}
	return result, nil
}

func (p Podman) GetImagesWithOptions(options images.ListOptions) []*entities.ImageSummary {
	imageList, err := images.List(p.Connection, &options)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return nil
	}
	return imageList
}

func (p Podman) RemoveImagesUntagged() (bool, error) {
	return p.PruneImages()
}

func (p Podman) RemoveDanglingImages() (bool, error) {
	return p.PruneImages()
}

func (p Podman) PurgeImages() (bool, error) {
	return p.PruneImages()

}

func (p Podman) RemoveImagesFiltered(repo string, tag string) (bool, error) {
	_, errs := p.RemoveImage(repo+":"+tag, false)
	if len(errs) == 0 {
		return true, nil
	} else {
		return false, errs[0]
	}

}

func (p Podman) PullImage(image string) ([]string, error) {
	result, err := images.Pull(p.Connection, image, nil)
	if err != nil {
		logrus.Errorf("%s \n", err)
		return nil, err
	}
	return result, nil
}

func (p Podman) TagImage(nameOrId string, tag string, repo string) error {
	err := images.Tag(p.Connection, nameOrId, tag, repo, nil)
	if err != nil {
		logrus.Errorf("%s %s\n", err, "Error during tag image")
	}
	return err
}

func (p Podman) PushImage(image string, url string, username string, password string) error {
	opts := images.PushOptions{}
	opts.WithUsername(username)
	opts.WithPassword(password)
	opts.WithSkipTLSVerify(true)
	err := images.Push(p.Connection, image, url, &opts)
	if err != nil {
		logrus.Errorf("%s %s\n", err, "Error during push image")
	}
	return err
}

func (p Podman) GetContainerID(imageName string) (string, error) {
	containers, err := containers.List(p.Connection, nil)
	if err != nil {
		logrus.Errorf("%s %s\n", err, "Error during container list")
	}

	for _, container := range containers {
		if container.Image == imageName {
			return container.ID, nil
		}
	}
	return "", nil
}

func (p Podman) ContainerStop(containerID string) error {
	err := containers.Stop(p.Connection, containerID, nil)
	if err != nil {
		logrus.Errorf("%s %s\n", err, "Error during container stop")
	}
	return err
}

func (p Podman) ContainerKill(containerID string) error {
	killOptions := containers.KillOptions{}
	killOptions.WithSignal("SIGKILL")
	err := containers.Kill(p.Connection, containerID, &killOptions)
	if err != nil {
		logrus.Errorf("%s %s\n", err, "Error during container kill")
	}
	return err
}

func (p Podman) ContainerRemove(containerID string) error {
	_, err := containers.Remove(p.Connection, containerID, nil)
	if err != nil {
		logrus.Errorf("%s %s\n", err, "Error during container remove")
	}
	return err
}
