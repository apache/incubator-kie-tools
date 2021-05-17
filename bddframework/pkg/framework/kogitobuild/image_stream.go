// Copyright 2020 Red Hat, Inc. and/or its affiliates
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

package kogitobuild

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	buildv1 "github.com/openshift/api/build/v1"
	"os"
	"strings"

	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	imgv1 "github.com/openshift/api/image/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	customKogitoImagePrefix   = "custom-"
	labelKeyVersion           = "version"
	kogitoBuilderImageEnvVar  = "BUILDER_IMAGE"
	kogitoRuntimeJVMEnvVar    = "RUNTIME_IMAGE"
	kogitoRuntimeNativeEnvVar = "RUNTIME_NATIVE_IMAGE"
	// defaultBuilderImage Builder Image for Kogito
	defaultBuilderImage = "kogito-builder"
	// defaultRuntimeJVM Runtime Image for Kogito with  JRE
	defaultRuntimeJVM = "kogito-runtime-jvm"
	//defaultRuntimeNative Runtime Image for Kogito for Native Quarkus Application
	defaultRuntimeNative = "kogito-runtime-native"
)

type kogitoImageType int

const (
	// kogitoBuilderImage Builder Image for Kogito
	kogitoBuilderImage kogitoImageType = iota
	// kogitoRuntimeJVMImage Runtime Image for Kogito with  JRE
	kogitoRuntimeJVMImage
	//kogitoRuntimeNativeImage Runtime Image for Kogito for Native Quarkus Application
	kogitoRuntimeNativeImage
)

var (
	// imageStreamDefaultAnnotations lists the default annotations for ImageStreams
	imageStreamDefaultAnnotations = map[kogitoImageType]map[string]string{
		kogitoRuntimeNativeImage: {
			"openshift.io/provider-display-name": "KIE Group",
			"openshift.io/display-name":          "Runtime image for Kogito based on Quarkus native image",
		},
		kogitoRuntimeJVMImage: {
			"openshift.io/provider-display-name": "KIE Group",
			"openshift.io/display-name":          "Runtime image for Kogito based on Quarkus or Spring Boot JVM image",
		},
		kogitoBuilderImage: {
			"openshift.io/provider-display-name": "KIE Group",
			"openshift.io/display-name":          "Platform for building Kogito based on Quarkus or Spring Boot",
		},
	}

	//tagDefaultAnnotations lists the default annotations for ImageStreamTags
	tagDefaultAnnotations = map[kogitoImageType]map[string]string{
		kogitoRuntimeNativeImage: {
			"iconClass":   "icon-jbpm",
			"description": "Runtime image for Kogito based on Quarkus native image",
			"tags":        "runtime,kogito,quarkus",
			"supports":    "quarkus",
		},
		kogitoRuntimeJVMImage: {
			"iconClass":   "icon-jbpm",
			"description": "Runtime image for Kogito based on Quarkus or Spring Boot JVM image",
			"tags":        "runtime,kogito,quarkus,springboot,jvm",
			"supports":    "quarkus,springboot",
		},
		kogitoBuilderImage: {
			"iconClass":   "icon-jbpm",
			"description": "Platform for building Kogito based on Quarkus or Spring Boot",
			"tags":        "builder,kogito,quarkus,springboot",
			"supports":    "quarkus,springboot",
		},
	}
)

// ImageStreamHandler ...
type ImageStreamHandler interface {
	CreateRequiredKogitoImageStreams(build api.KogitoBuildInterface) (created bool, err error)
	ResolveKogitoImageStreamTagName(build api.KogitoBuildInterface, isBuilder bool) string
	ResolveKogitoImageNameTag(build api.KogitoBuildInterface, isBuilder bool) string
}

type imageStreamHandler struct {
	operator.Context
}

// NewImageSteamHandler ...
func NewImageSteamHandler(context operator.Context) ImageStreamHandler {
	return &imageStreamHandler{
		context,
	}
}

// CreateRequiredKogitoImageStreams creates the ImageStreams required by the BuildConfigs to build a custom Kogito Service.
// These images should not be controlled by a given KogitoBuild instance, but reused across all of them.
// This function checks the existence of any of the required ImageStreams by the given instance, if no ImageStream found, creates.
// If the ImageStream exists, but not the tag, a new tag for that same ImageStream is created.
// This way would be possible to handle different builds with different Kogito versions in the same namespace.
// Returns a flag indicating if one of them were created in the cluster or not.
func (k *imageStreamHandler) CreateRequiredKogitoImageStreams(build api.KogitoBuildInterface) (created bool, err error) {
	buildersCreated := false
	runtimeCreated := false
	if buildersCreated, err = k.createRequiredKogitoImageStreamTag(k.newKogitoImageStreamForBuilders(build)); err != nil {
		return false, err
	}
	if runtimeCreated, err = k.createRequiredKogitoImageStreamTag(k.newKogitoImageStreamForRuntime(build)); err != nil {
		return false, err
	}
	return buildersCreated || runtimeCreated, nil
}

// See CreateRequiredKogitoImageStreams
func (k *imageStreamHandler) createRequiredKogitoImageStreamTag(requiredStream imgv1.ImageStream) (created bool, err error) {
	created = false
	deployedStream := &imgv1.ImageStream{ObjectMeta: metav1.ObjectMeta{Name: requiredStream.Name, Namespace: requiredStream.Namespace}}
	exists, err := kubernetes.ResourceC(k.Client).Fetch(deployedStream)
	tagExists := false
	if err != nil {
		return created, err
	}
	if exists {
		for _, tag := range deployedStream.Spec.Tags {
			if tag.Name == requiredStream.Spec.Tags[0].Name {
				tagExists = true
				break
			}
		}
	}
	// nor tag nor image stream exists, we can safely create a new one for us
	if !tagExists && !exists {
		if err := kubernetes.ResourceC(k.Client).Create(&requiredStream); err != nil {
			// double check since the object could've been created in another thread
			if errors.IsAlreadyExists(err) {
				exists = true
				created = false
			} else {
				return created, err
			}
		} else {
			created = true
		}
	}
	// the required tag is not there, let's just add the required tag and move on
	if !tagExists && exists {
		deployedStream.Spec.Tags = append(deployedStream.Spec.Tags, requiredStream.Spec.Tags...)
		if err := kubernetes.ResourceC(k.Client).Update(deployedStream); err != nil {
			return created, err
		}
		created = true
	}

	return created, nil
}

// newKogitoImageStreamForBuilders same as newKogitoImageStream(build, true)
func (k *imageStreamHandler) newKogitoImageStreamForBuilders(build api.KogitoBuildInterface) imgv1.ImageStream {
	return k.newKogitoImageStream(build, true)
}

// newKogitoImageStreamForRuntime same as newKogitoImageStream(build, false)
func (k *imageStreamHandler) newKogitoImageStreamForRuntime(build api.KogitoBuildInterface) imgv1.ImageStream {
	return k.newKogitoImageStream(build, false)
}

// newKogitoImageStream creates a new OpenShift ImageStream based on the given build and the image purpose
func (k *imageStreamHandler) newKogitoImageStream(build api.KogitoBuildInterface, isBuilder bool) imgv1.ImageStream {
	imageStreamName := resolveKogitoImageStreamName(build, isBuilder)
	imageTag := k.resolveKogitoImageTag(build, isBuilder)
	imageRegistry := resolveKogitoImageRegistryNamespace(build, isBuilder)
	imageType := getKogitoImageType(isBuilder, build.GetSpec().IsNative())
	tagAnnotations := tagDefaultAnnotations[imageType]
	if tagAnnotations == nil { //custom image streams won't have a default tag ;)
		tagAnnotations = map[string]string{}
	}
	tagAnnotations[labelKeyVersion] = imageTag
	return imgv1.ImageStream{
		ObjectMeta: metav1.ObjectMeta{
			Name:        imageStreamName,
			Namespace:   build.GetNamespace(),
			Annotations: imageStreamDefaultAnnotations[imageType],
		},
		Spec: imgv1.ImageStreamSpec{
			Tags: []imgv1.TagReference{
				{
					Name:        imageTag,
					Annotations: tagAnnotations,
					ReferencePolicy: imgv1.TagReferencePolicy{
						Type: imgv1.LocalTagReferencePolicy,
					},
					From: &v1.ObjectReference{
						Kind: "DockerImage",
						Name: fmt.Sprintf("%s/%s:%s",
							imageRegistry, resolveKogitoImageName(build, isBuilder), imageTag),
					},
				},
			},
		},
	}
}

func getKogitoImageType(isBuilder bool, isNative bool) kogitoImageType {
	if isBuilder {
		return kogitoBuilderImage
	} else if !isNative {
		return kogitoRuntimeJVMImage
	}
	return kogitoRuntimeNativeImage
}

// ResolveKogitoImageNameTag resolves the ImageStreamTag to be used in the given build, e.g. kogito-quarkus-ubi8-s2i:0.11
func (k *imageStreamHandler) ResolveKogitoImageNameTag(build api.KogitoBuildInterface, isBuilder bool) string {
	return strings.Join([]string{
		resolveKogitoImageName(build, isBuilder),
		k.resolveKogitoImageTag(build, isBuilder),
	}, ":")
}

// resolveKogitoImageTag resolves the ImageTag to be used in the given build, e.g. 0.11
func (k *imageStreamHandler) resolveKogitoImageTag(build api.KogitoBuildInterface, isBuilder bool) string {
	image := framework.ConvertImageTagToImage(build.GetSpec().GetRuntimeImage())
	if isBuilder {
		image = framework.ConvertImageTagToImage(build.GetSpec().GetBuildImage())
	}
	if len(image.Tag) > 0 {
		return image.Tag
	}
	return infrastructure.GetKogitoImageVersion(k.Context.Version)
}

// resolveKogitoImageName resolves the ImageName to be used in the given build, e.g. kogito-quarkus-ubi8-s2i
func resolveKogitoImageName(build api.KogitoBuildInterface, isBuilder bool) string {
	if isBuilder {
		image := framework.ConvertImageTagToImage(build.GetSpec().GetBuildImage())
		if len(image.Name) > 0 {
			return image.Name
		}
		return GetDefaultBuilderImage()
	}
	image := framework.ConvertImageTagToImage(build.GetSpec().GetRuntimeImage())
	if len(image.Name) > 0 {
		return image.Name
	}
	if build.GetSpec().IsNative() {
		return GetDefaultRuntimeNativeImage()
	}
	return GetDefaultRuntimeJVMImage()
}

// GetDefaultBuilderImage ...
func GetDefaultBuilderImage() string {
	builderImage := os.Getenv(kogitoBuilderImageEnvVar)
	if len(builderImage) == 0 {
		builderImage = defaultBuilderImage
	}
	return builderImage
}

// GetDefaultRuntimeJVMImage ...
func GetDefaultRuntimeJVMImage() string {
	runtimeImage := os.Getenv(kogitoRuntimeJVMEnvVar)
	if len(runtimeImage) == 0 {
		runtimeImage = defaultRuntimeJVM
	}
	return runtimeImage
}

// GetDefaultRuntimeNativeImage ...
func GetDefaultRuntimeNativeImage() string {
	runtimeImage := os.Getenv(kogitoRuntimeNativeEnvVar)
	if len(runtimeImage) == 0 {
		runtimeImage = defaultRuntimeNative
	}
	return runtimeImage
}

// resolveKogitoImageName resolves the ImageName to be used in the given build, e.g. kogito-quarkus-ubi8-s2i
func resolveKogitoImageStreamName(build api.KogitoBuildInterface, isBuilder bool) string {
	imageName := resolveKogitoImageName(build, isBuilder)
	image := framework.ConvertImageTagToImage(build.GetSpec().GetRuntimeImage())
	if isBuilder {
		image = framework.ConvertImageTagToImage(build.GetSpec().GetBuildImage())
	}
	if len(image.Name) > 0 { // custom image
		return strings.Join([]string{customKogitoImagePrefix, imageName}, "")
	}
	return imageName
}

// resolveKogitoImageName resolves the ImageName to be used in the given build, e.g. kogito-quarkus-ubi8-s2i
func (k *imageStreamHandler) ResolveKogitoImageStreamTagName(build api.KogitoBuildInterface, isBuilder bool) string {
	imageStream := resolveKogitoImageStreamName(build, isBuilder)
	imageTag := k.resolveKogitoImageTag(build, isBuilder)
	return strings.Join([]string{imageStream, imageTag}, ":")
}

// resolveImageRegistry resolves the registry/namespace name to be used in the given build, e.g. quay.io/kiegroup
func resolveKogitoImageRegistryNamespace(build api.KogitoBuildInterface, isBuilder bool) string {
	namespace := infrastructure.GetDefaultImageNamespace()
	registry := infrastructure.GetDefaultImageRegistry()
	image := framework.ConvertImageTagToImage(build.GetSpec().GetRuntimeImage())
	if isBuilder {
		image = framework.ConvertImageTagToImage(build.GetSpec().GetBuildImage())
	}
	if len(image.Domain) > 0 {
		registry = image.Domain
	}
	if len(image.Namespace) > 0 {
		namespace = image.Namespace
	}
	return strings.Join([]string{registry, namespace}, "/")
}

func getOutputImageStreamNameTag(bc *buildv1.BuildConfig) (name, tag string) {
	imageNameTag := strings.Split(bc.Spec.Output.To.Name, ":")
	name = imageNameTag[0]
	tag = tagLatest
	if len(imageNameTag) > 1 {
		tag = imageNameTag[1]
	}
	return name, tag
}

// newOutputImageStreamForBuilder creates a new output ImageStream for Builder BuildConfigs
func newOutputImageStreamForBuilder(bc *buildv1.BuildConfig) imgv1.ImageStream {
	isName, tag := getOutputImageStreamNameTag(bc)
	return imgv1.ImageStream{
		ObjectMeta: metav1.ObjectMeta{
			Name:      isName,
			Namespace: bc.Namespace,
			Labels: map[string]string{
				framework.LabelAppKey: bc.Labels[framework.LabelAppKey],
			},
		},
		Spec: imgv1.ImageStreamSpec{
			LookupPolicy: imgv1.ImageLookupPolicy{
				Local: true,
			},
			Tags: []imgv1.TagReference{
				{
					Name: tag,
					ReferencePolicy: imgv1.TagReferencePolicy{
						Type: imgv1.LocalTagReferencePolicy,
					},
				},
			},
		},
	}
}

// newOutputImageStreamForRuntime creates a new image stream for the Runtime
// if one image stream is found in the namespace managed by other resources such as KogitoRuntime or other KogitoBuild, we add ourselves in the owner references
func newOutputImageStreamForRuntime(context operator.Context, bc *buildv1.BuildConfig, build api.KogitoBuildInterface) (*imgv1.ImageStream, error) {
	isName, tag := getOutputImageStreamNameTag(bc)
	imageHandler := newImageHandlerForBuiltServices(context, isName, tag, build.GetNamespace())
	imageStream, err := imageHandler.CreateImageStreamIfNotExists()
	if err != nil {
		return nil, err
	}
	return imageStream, nil
}

// NewImageHandlerForBuiltServices creates a new handler for Kogito Services being built
func newImageHandlerForBuiltServices(context operator.Context, isName, tag, namespace string) infrastructure.ImageHandler {
	image := &api.Image{
		Name: isName,
		Tag:  tag,
	}
	return infrastructure.NewImageHandler(context, image, image.Name, image.Name, namespace, false, false)
}
