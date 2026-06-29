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

	"k8s.io/klog/v2"

	"github.com/moby/moby/api/types/image"
	"github.com/moby/moby/api/types/registry"
	"github.com/moby/moby/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/util"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/util/log"
)

type Docker struct {
	Connection *client.Client
}

// https://docs.docker.com/engine/api/latest/
func (d Docker) GetClient() (*client.Client, error) {
	return client.New(client.FromEnv)
}

func (d Docker) GetClientRemoteFromEnv(host string) (*client.Client, error) {
	return client.New(client.FromEnv, client.WithHost(host))
}

func (d Docker) GetClientRemote(host string, cacertPath string, certPath string, keyPath string) (*client.Client, error) {
	return client.New(client.WithHost(host), client.WithTLSClientConfig(cacertPath, certPath, keyPath))
}

func (d Docker) GetImages(args client.ImageListOptions) ([]image.Summary, error) {
	result, err := d.Connection.ImageList(context.Background(), args)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Get Images")
		return nil, err
	}
	return result.Items, nil
}

// RemoveImagesUntagged removes the images with tag <none>:<none>
func (d Docker) RemoveImagesUntagged() (bool, error) {
	images, err := d.GetImages(client.ImageListOptions{All: true})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Remove Images Untagged")
		return false, err
	}
	for _, img := range images {
		if img.RepoTags != nil && img.RepoTags[0] == "<none>:<none>" && img.RepoDigests[0] == "<none>@<none>" {
			result, err := d.Connection.ImageRemove(context.Background(), img.ID, client.ImageRemoveOptions{PruneChildren: true, Force: true})
			if err != nil {
				klog.V(log.E).ErrorS(err, "error during Remove Images Untagged", "result", result)
				return false, err
			}
		}
	}
	return true, nil
}

// RemoveDanglingImages removes the images with the filter dangling true
func (d Docker) RemoveDanglingImages() (bool, error) {
	filterArgs := client.Filters{}
	filterArgs = filterArgs.Add("dangling", "true")
	images, err := d.GetImages(client.ImageListOptions{Filters: filterArgs})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Remove Dangling Images")
		return false, err
	}
	for _, img := range images {
		result, err := d.Connection.ImageRemove(context.Background(), img.ID, client.ImageRemoveOptions{PruneChildren: true, Force: true})
		if err != nil {
			klog.V(log.E).ErrorS(err, "error during Remove Dangling Images", "result", result)
			return false, err
		}
	}
	return true, nil
}

// Purge images with dangling true
func (d Docker) PurgeImages() (bool, error) {
	filterArgs := client.Filters{}
	filterArgs = filterArgs.Add("dangling", "true")
	_, err := d.Connection.ImagePrune(context.Background(), client.ImagePruneOptions{Filters: filterArgs})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Purge Images")
		return false, err
	}

	klog.V(log.I).InfoS("Images pruned successfully")
	return true, nil
}

func (d Docker) PurgeContainer(id string, imageName string) (bool, error) {
	result, err := d.Connection.ContainerList(context.Background(), client.ContainerListOptions{All: true})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Purge Container")
		return false, err
	}

	for _, ctr := range result.Items {
		if ctr.Image == imageName || ctr.ID == id {
			d.ContainerKill(ctr.ID)
			d.ContainerRemove(ctr.ID)
			klog.V(log.I).InfoS("Purged container", "ID", ctr.ID)
		}
	}
	return true, nil
}

// remove all the images found using the repo name, with or without tag
func (d Docker) RemoveImagesFiltered(repo string, tag string) (bool, error) {
	filterArgs := client.Filters{}
	if len(repo) > 0 && len(tag) > 0 {
		filterArgs = filterArgs.Add("reference", repo+":"+tag)
	}
	if len(repo) > 0 {
		filterArgs = filterArgs.Add("reference", repo)
	}

	images, err := d.GetImages(client.ImageListOptions{Filters: filterArgs})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Remove Images Filtered")
		return false, err
	}
	for _, img := range images {
		result, err := d.Connection.ImageRemove(context.Background(), img.ID, client.ImageRemoveOptions{PruneChildren: true, Force: true})
		if err != nil {
			klog.V(log.E).ErrorS(err, "error during Remove Images Filtered", "result", result)
			return false, err
		}
	}
	return true, nil
}

func (d Docker) TagImage(imageSource string, imageTag string) error {
	opts := client.ImageTagOptions{
		Source: imageSource,
		Target: imageTag,
	}
	_, err := d.Connection.ImageTag(context.Background(), opts)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Tag Image")
	}
	return err
}

func (d Docker) PushImage(imageName string, url string, username string, password string) error {
	var authConfig = registry.AuthConfig{
		Username:      username,
		Password:      password,
		ServerAddress: url,
	}
	authConfigBytes, _ := json.Marshal(authConfig)
	authConfigEncoded := base64.URLEncoding.EncodeToString(authConfigBytes)

	opts := client.ImagePushOptions{RegistryAuth: authConfigEncoded}
	result, err := d.Connection.ImagePush(context.Background(), imageName, opts)
	if err != nil {
		// Note: result contains io.ReadCloser in .Body field for v0.5.0
		klog.V(log.E).ErrorS(err, "error during Push Image")
	}
	_ = result // result would need to be read and closed
	return err
}

func (d Docker) PullImage(imageName string) error {
	result, err := d.Connection.ImagePull(context.Background(), imageName, client.ImagePullOptions{})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Pull Image")
		return err
	}
	// Close the result to avoid resource leaks (result implements io.ReadCloser)
	defer result.Close()
	// Wait for the pull to complete
	return result.Wait(context.Background())
}

func (d Docker) GetContainerID(imageName string) (string, error) {
	result, err := d.Connection.ContainerList(context.Background(), client.ContainerListOptions{})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Get Container ID")
		return "", err
	}

	for _, ctr := range result.Items {
		if ctr.Image == imageName {
			return ctr.ID, nil
		}
	}
	return "", nil
}

func (d Docker) ContainerStop(containerID string) error {
	stopOptions := client.ContainerStopOptions{
		Timeout: util.Pint(10),
	}
	_, err := d.Connection.ContainerStop(context.Background(), containerID, stopOptions)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Container Stop")
	}
	return err
}

func (d Docker) ContainerKill(containerID string) error {
	killOptions := client.ContainerKillOptions{
		Signal: "SIGKILL",
	}
	_, err := d.Connection.ContainerKill(context.Background(), containerID, killOptions)
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Container Kill")
	}
	return err
}

func (d Docker) ContainerRemove(containerID string) error {
	_, err := d.Connection.ContainerRemove(context.Background(), containerID, client.ContainerRemoveOptions{})
	if err != nil {
		klog.V(log.E).ErrorS(err, "error during Container Remove")
	}
	return err
}
