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
	"encoding/base64"
	"encoding/json"
	"io/ioutil"

	"k8s.io/klog/v2"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/api/types/filters"
	"github.com/docker/docker/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/util"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/util/log"
)

type Docker struct {
	Connection *client.Client
}

// https://docs.docker.com/engine/api/latest/
func (d Docker) GetClient() (*client.Client, error) {
	return client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
}

func (d Docker) GetClientRemoteFromEnv(host string) (*client.Client, error) {
	return client.NewClientWithOpts(client.FromEnv, client.WithHost(host))
}

func (d Docker) GetClientRemote(host string, cacertPath string, certPath string, keyPath string) (*client.Client, error) {
	return client.NewClientWithOpts(client.WithHost(host), client.WithAPIVersionNegotiation(), client.WithTLSClientConfig(cacertPath, certPath, keyPath))
}

func (d Docker) GetImages(args types.ImageListOptions) ([]types.ImageSummary, error) {
	images, err := d.Connection.ImageList(context.Background(), args)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Get Images")
		return nil, err
	}
	return images, nil
}

// RemoveImagesUntagged removes the images with tag <none>:<none>
func (d Docker) RemoveImagesUntagged() (bool, error) {
	images, err := d.GetImages(types.ImageListOptions{All: true})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Remove Images Untagged")
		return false, err
	}
	for _, image := range images {
		if image.RepoTags != nil && image.RepoTags[0] == "<none>:<none>" && image.RepoDigests[0] == "<none>@<none>" {
			item, err := d.Connection.ImageRemove(context.Background(), image.ID, types.ImageRemoveOptions{PruneChildren: true, Force: true})
			if err != nil {
				klog.V(log.E).ErrorS(err, "error during Remove Images Untagged", "item", item)
				return false, err
			}
		}
	}
	return true, nil
}

// RemoveDanglingImages removes the images with the filter dangling true
func (d Docker) RemoveDanglingImages() (bool, error) {
	filters := filters.NewArgs()
	filters.Add("dangling", "true")
	images, err := d.GetImages(types.ImageListOptions{Filters: filters})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Remove Dangling Images")
		return false, err
	}
	for _, image := range images {
		item, err := d.Connection.ImageRemove(context.Background(), image.ID, types.ImageRemoveOptions{PruneChildren: true, Force: true})
		if err != nil {
			klog.V(log.E).ErrorS(err, "error during Remove Dangling Images", "item", item)
			return false, err
		}
	}
	return true, nil
}

// Purge images with dangling true
func (d Docker) PurgeImages() (bool, error) {
	filters := filters.NewArgs()
	filters.Add("dangling", "true")
	report, err := d.Connection.ImagesPrune(context.Background(), filters)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Purge Images")
		return false, err
	} else {
		klog.V(log.I).InfoS("Images purged", "images", report.ImagesDeleted)
		return true, nil
	}
}

func (d Docker) PurgeContainer(id string, image string) (bool, error) {
	containers, err := d.Connection.ContainerList(context.Background(), types.ContainerListOptions{All: true})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Purge Container")
		return false, err
	} else {
		for _, container := range containers {
			if container.Image == image || container.ID == id {
				d.ContainerKill(container.ID)
				d.ContainerRemove(container.ID)
				klog.V(log.I).InfoS("Purged container", "ID", container.ID)
			}
		}
		return true, nil
	}
}

// remove all the images found using the repo name, with or without tag
func (d Docker) RemoveImagesFiltered(repo string, tag string) (bool, error) {
	filters := filters.NewArgs()
	if len(repo) > 0 && len(tag) > 0 {
		filters.Add("reference", repo+":"+tag)
	}
	if len(repo) > 0 {
		filters.Add("reference", repo)
	}

	images, err := d.GetImages(types.ImageListOptions{Filters: filters})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Remove Images Filtered")
		return false, err
	}
	for _, image := range images {
		item, err := d.Connection.ImageRemove(context.Background(), image.ID, types.ImageRemoveOptions{PruneChildren: true, Force: true})
		if err != nil {
			klog.V(log.E).ErrorS(err, "error during Remove Images Filtered", "item", item)
			return false, err
		}
	}
	return true, nil
}

func (d Docker) TagImage(imageSource string, imageTag string) error {
	err := d.Connection.ImageTag(context.Background(), imageSource, imageTag)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Tag Image")
	}
	return err
}

func (d Docker) PushImage(image string, url string, username string, password string) error {
	var authConfig = types.AuthConfig{
		Username:      username,
		Password:      password,
		ServerAddress: url,
	}
	authConfigBytes, _ := json.Marshal(authConfig)
	authConfigEncoded := base64.URLEncoding.EncodeToString(authConfigBytes)

	opts := types.ImagePushOptions{RegistryAuth: authConfigEncoded}
	resp, err := d.Connection.ImagePush(context.Background(), image, opts)
	if err != nil {
		body, _ := ioutil.ReadAll(resp)
		klog.V(log.E).ErrorS(err, "error during Push Image", "body", body)
	}
	return err
}

func (d Docker) PullImage(image string) error {
	_, err := d.Connection.ImagePull(context.Background(), image, types.ImagePullOptions{})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Pull Image")
	}
	return err
}

func (d Docker) GetContainerID(imageName string) (string, error) {
	containers, err := d.Connection.ContainerList(context.Background(), types.ContainerListOptions{})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Get Container ID")
	}

	for _, container := range containers {
		if container.Image == imageName {
			return container.ID, nil
		}
	}
	return "", nil
}

func (d Docker) ContainerStop(containerID string) error {
	stopOptions := container.StopOptions{
		Timeout: util.Pint(10),
	}
	err := d.Connection.ContainerStop(context.Background(), containerID, stopOptions)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Container Stop")
	}
	return err
}

func (d Docker) ContainerKill(containerID string) error {
	err := d.Connection.ContainerKill(context.Background(), containerID, "SIGKILL")
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Container Kill")
	}
	return err
}

func (d Docker) ContainerRemove(containerID string) error {
	err := d.Connection.ContainerRemove(context.Background(), containerID, types.ContainerRemoveOptions{})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Container Remove")
	}
	return err
}
