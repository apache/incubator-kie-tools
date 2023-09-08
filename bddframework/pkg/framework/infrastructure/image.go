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

package infrastructure

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/operator"
	imgv1 "github.com/openshift/api/image/v1"
	"k8s.io/apimachinery/pkg/types"
	"os"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"strings"
)

const (
	annotationKeyImageTriggers         = "image.openshift.io/triggers"
	annotationValueImageTriggersFormat = "[{\"from\":{\"kind\":\"ImageStreamTag\",\"name\":\"%s\"},\"fieldPath\":\"spec.template.spec.containers[?(@.name==\\\"%s\\\")].image\"}]"

	versionSeparator = "."
	// LatestTag the default name for latest image tag
	LatestTag = "latest"
	// imageRegistryEnvVar ...
	imageRegistryEnvVar = "IMAGE_REGISTRY"
	// defaultImageRegistry the default services image repository
	defaultImageRegistry = "quay.io/kiegroup"
	//RuntimeTypeKey Env key to switch between the runtime
	RuntimeTypeKey = "RUNTIME_TYPE"
)

// ImageHandler describes the handler structure to handle Kogito Services Images
type ImageHandler interface {
	ResolveImage() (string, error)
	ResolveImageNameTag() string
	ResolveImageStreamTriggerAnnotation(containerName string) (key, value string)
	CreateImageStreamIfNotExists() (*imgv1.ImageStream, error)
	ReconcileImageStream(owner client.Object) error
}

// imageHandler defines the base structure for images in either OpenShift or Kubernetes clusters
type imageHandler struct {
	operator.Context
	// image is the CR structure attribute given by the user
	image *api.Image
	// defaultImageName is the default image name for this service. Used to resolve the image from the Kogito Team registry when no custom image is given.
	defaultImageName string
	// imageStreamName name for the image stream that will handle image tags for the given instance
	imageStreamName string
	// namespace to fetch/create objects
	namespace             string
	addFromReference      bool
	insecureImageRegistry bool
}

// NewImageHandler ...
func NewImageHandler(context operator.Context, image *api.Image, defaultImageName, imageStreamName, namespace string, addFromReference, insecureImageRegistry bool) ImageHandler {
	return &imageHandler{
		Context:               context,
		image:                 image,
		defaultImageName:      defaultImageName,
		imageStreamName:       imageStreamName,
		namespace:             namespace,
		addFromReference:      addFromReference,
		insecureImageRegistry: insecureImageRegistry,
	}
}

func (i *imageHandler) CreateImageStreamIfNotExists() (*imgv1.ImageStream, error) {
	if i.Client.IsOpenshift() {
		imageStreamHandler := NewImageStreamHandler(i.Context)
		imageStream, err := imageStreamHandler.CreateImageStreamIfNotExists(types.NamespacedName{Name: i.imageStreamName, Namespace: i.namespace}, i.resolveTag(), i.addFromReference, i.resolveRegistryImage(), i.insecureImageRegistry)
		if err != nil {
			return nil, err
		}
		return imageStream, nil
	}
	return nil, nil
}

func (i *imageHandler) ReconcileImageStream(owner client.Object) error {
	if i.Client.IsOpenshift() {
		imageStreamReconciler := NewImageStreamReconciler(i.Context, types.NamespacedName{Name: i.imageStreamName, Namespace: i.namespace}, i.resolveTag(), i.addFromReference, i.resolveRegistryImage(), i.insecureImageRegistry, owner)
		return imageStreamReconciler.Reconcile()
	}
	return nil
}

// resolveImage resolves images like "quay.io/kiegroup/kogito-jobs-service:latest" or "internal-registry/namespace/image:hash".
// Can be empty if on OpenShift and the ImageStream is not ready.
// In case of Openshift, image name is resolved using Image Stream tag to support kogito build.
func (i *imageHandler) ResolveImage() (string, error) {
	i.Log.Debug("Going to resolve image...")
	if i.Client.IsOpenshift() {
		i.Log.Debug("Openshift environment found.")
		imageStreamHandler := NewImageStreamHandler(i.Context)
		return imageStreamHandler.ResolveImage(types.NamespacedName{Name: i.imageStreamName, Namespace: i.namespace}, i.resolveTag())
	}

	// in k8s environment image name is resolved to image name provided by user through CRD.
	return i.resolveRegistryImage(), nil
}

// resolveRegistryImage resolves images like "quay.io/kiegroup/kogito-jobs-service:latest", as informed by user.
func (i *imageHandler) resolveRegistryImage() string {
	domain := i.image.Domain
	if len(domain) == 0 {
		domain = GetDefaultImageRegistry()
	}
	return fmt.Sprintf("%s/%s", domain, i.ResolveImageNameTag())
}

// resolves like "kogito-jobs-service:latest"
func (i *imageHandler) ResolveImageNameTag() string {
	name := i.image.Name
	if len(name) == 0 {
		name = i.defaultImageName
	}
	return fmt.Sprintf("%s:%s", name, i.resolveTag())
}

// resolves like "latest", 0.8.0, and so on
func (i *imageHandler) resolveTag() string {
	if len(i.image.Tag) == 0 {
		return GetKogitoImageVersion(i.Context.Version)
	}
	return i.image.Tag
}

// ResolveImageStreamTriggerAnnotation creates a key and value combination for the ImageStream trigger to be linked with a Kubernetes Deployment
// this way, a Deployment resource can be attached to a ImageStream, like the DeploymentConfigs are.
// See: https://docs.openshift.com/container-platform/3.11/dev_guide/managing_images.html#image-stream-kubernetes-resources
// imageNameTag should be set in the format image-name:version
func (i *imageHandler) ResolveImageStreamTriggerAnnotation(containerName string) (key, value string) {
	imageNameTag := i.ResolveImageNameTag()
	key = annotationKeyImageTriggers
	value = fmt.Sprintf(annotationValueImageTriggersFormat, imageNameTag, containerName)
	return
}

// GetKogitoImageVersion gets the Kogito Runtime latest micro version based on the given version
// E.g. Operator version is 0.9.0, the latest image version is 0.9.x-latest
// unit test friendly unexported function
// in this case we are considering only micro updates, that's 0.9.0 -> 0.9, thus for 1.0.0 => 1.0
// in the future this should be managed with carefully if we desire a behavior like 1.0.0 => 1, that's minor upgrades
func GetKogitoImageVersion(v string) string {
	if len(v) == 0 {
		return LatestTag
	}

	versionPrefix := strings.Split(v, versionSeparator)
	length := len(versionPrefix)
	if length > 0 {
		lastIndex := 2   // micro updates
		if length <= 2 { // guard against unusual cases
			lastIndex = length
		}
		return strings.Join(versionPrefix[:lastIndex], versionSeparator)
	}
	return LatestTag
}

// GetDefaultImageRegistry ...
func GetDefaultImageRegistry() string {
	registry := os.Getenv(imageRegistryEnvVar)
	if len(registry) == 0 {
		registry = defaultImageRegistry
	}
	return registry
}
