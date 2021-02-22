// Copyright 2019 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package openshift

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/kiegroup/kogito-cloud-operator/core/client"

	dockerv10 "github.com/openshift/api/image/docker10"
	imgv1 "github.com/openshift/api/image/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	//ImageTagLatest is the default name for the latest image tag
	ImageTagLatest = "latest"
	// ImageLabelForExposeServices is the label defined in images to identify ports that need to be exposed by the container
	ImageLabelForExposeServices = "io.openshift.expose-services"
)

// ImageStreamInterface exposes OpenShift ImageStream operations
type ImageStreamInterface interface {
	// FetchDockerImage fetches a docker image based on a ImageStreamTag with the defined key (namespace and name).
	// Returns nil if not found
	FetchDockerImage(key types.NamespacedName) (*dockerv10.DockerImage, error)
	// FetchTag fetches for a particular ImageStreamTag on OpenShift cluster.
	// If tag is nil or empty, will search for "latest".
	// Returns nil if the object was not found.
	FetchTag(key types.NamespacedName, tag string) (*imgv1.ImageStreamTag, error)
	// CreateTagIfNotExists will create a new ImageStreamTag if not exists
	CreateTagIfNotExists(image *imgv1.ImageStreamTag) (bool, error)
	// CreateImageStream will create a new ImageStream if not exists
	CreateImageStream(is *imgv1.ImageStream) (bool, error)
}

func newImageStream(c *client.Client) ImageStreamInterface {
	return &imageStream{
		client: c,
	}
}

type imageStream struct {
	client *client.Client
}

func (i *imageStream) FetchTag(key types.NamespacedName, tag string) (*imgv1.ImageStreamTag, error) {
	if len(tag) == 0 {
		tag = ImageTagLatest
	}
	tagRefName := fmt.Sprintf("%s:%s", key.Name, tag)
	isTag, err := i.client.ImageCli.ImageStreamTags(key.Namespace).Get(context.TODO(), tagRefName, metav1.GetOptions{})
	if err != nil && errors.IsNotFound(err) {
		log.Debug("Image '%s' not found on namespace %s", tagRefName, key.Namespace)
		return nil, nil
	} else if err != nil {
		return nil, err
	}
	return isTag, err
}

func (i *imageStream) FetchDockerImage(key types.NamespacedName) (*dockerv10.DockerImage, error) {
	dockerImage := &dockerv10.DockerImage{}
	isTag, err := i.FetchTag(key, "")
	if err != nil {
		return nil, err
	} else if isTag == nil {
		return nil, nil
	}
	log.Debug("Found image '%s' in the namespace '%s'", key.Name, key.Namespace)
	// is there any metadata to read from?
	if len(isTag.Image.DockerImageMetadata.Raw) != 0 {
		err = json.Unmarshal(isTag.Image.DockerImageMetadata.Raw, dockerImage)
		if err != nil {
			return nil, err
		}
		return dockerImage, nil
	}

	log.Warn("Can't find any metadata in the docker image for the imagestream '%s' in the namespace '%s'", key.Name, key.Namespace)
	return nil, nil
}

func (i *imageStream) CreateTagIfNotExists(is *imgv1.ImageStreamTag) (bool, error) {
	is, err := i.client.ImageCli.ImageStreamTags(is.Namespace).Create(context.TODO(), is, metav1.CreateOptions{})
	if err != nil && !errors.IsAlreadyExists(err) {
		log.Debug("Error while creating Image Stream Tag '%s' in namespace '%s'", is.Name, is.Namespace)
		return false, err
	} else if errors.IsAlreadyExists(err) {
		log.Debug("Image Stream Tag already exists in the namespace")
		return false, nil
	}
	log.Debug("Image Stream Tag %s created in namespace %s", is.Name, is.Namespace)
	return true, nil
}

func (i *imageStream) CreateImageStream(is *imgv1.ImageStream) (bool, error) {
	is, err := i.client.ImageCli.ImageStreams(is.Namespace).Create(context.TODO(), is, metav1.CreateOptions{})
	if err != nil && !errors.IsAlreadyExists(err) {
		log.Debug("Error while creating Image Stream '%s' in namespace '%s'", is.Name, is.Namespace)
		return false, err
	} else if errors.IsAlreadyExists(err) {
		log.Debug("Image Stream already exists in the namespace")
		return false, nil
	}
	log.Debug("Image Stream %s created in namespace %s", is.Name, is.Namespace)
	return true, nil
}
