// Copyright 2021 Red Hat, Inc. and/or its affiliates
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
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	imgv1 "github.com/openshift/api/image/v1"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	dockerImageKind      = "DockerImage"
	annotationKeyVersion = "version"
)

var imageStreamTagAnnotations = map[string]string{
	"iconClass":   "icon-jbpm",
	"description": "Runtime image for Kogito Service",
	"tags":        "kogito,services",
}

var imageStreamAnnotations = map[string]string{
	"openshift.io/provider-display-name": "KIE Group",
	"openshift.io/display-name":          "Kogito Service image",
}

// ImageStreamHandler ...
type ImageStreamHandler interface {
	FetchImageStream(key types.NamespacedName) (*imgv1.ImageStream, error)
	MustFetchImageStream(key types.NamespacedName) (*imgv1.ImageStream, error)
	CreateImageStream(name, namespace, imageName, tag string, addFromReference, insecureImageRegistry bool) *imgv1.ImageStream
}

type imageStreamHandler struct {
	operator.Context
}

// NewImageStreamHandler ...
func NewImageStreamHandler(context operator.Context) ImageStreamHandler {
	return &imageStreamHandler{
		context,
	}
}

// FetchImageStream gets the deployed ImageStream shared among Kogito Custom Resources
func (i *imageStreamHandler) FetchImageStream(key types.NamespacedName) (*imgv1.ImageStream, error) {
	imageStream := &imgv1.ImageStream{}
	if exists, err := kubernetes.ResourceC(i.Client).FetchWithKey(key, imageStream); err != nil {
		return nil, err
	} else if !exists {
		return nil, nil
	} else {
		i.Log.Debug("Successfully fetch deployed kogito infra reference")
		return imageStream, nil
	}
}

// MustFetchImageStream gets the deployed ImageStream shared among Kogito Custom Resources. If not found then return error.
func (i *imageStreamHandler) MustFetchImageStream(key types.NamespacedName) (*imgv1.ImageStream, error) {
	if imageStream, err := i.FetchImageStream(key); err != nil {
		return nil, err
	} else if imageStream == nil {
		return nil, fmt.Errorf("image stream with name %s not found in namespace %s", key.Name, key.Namespace)
	} else {
		i.Log.Debug("Successfully fetch deployed kogito infra reference")
		return imageStream, nil
	}
}

// CreateImageStream creates the ImageStream referencing the given namespace.
// Adds a docker image in the "From" reference based on the given image if `addFromReference` is set to `true`
func (i *imageStreamHandler) CreateImageStream(name, namespace, imageName, tag string, addFromReference, insecureImageRegistry bool) *imgv1.ImageStream {
	if i.Client.IsOpenshift() {
		imageStreamTagAnnotations[annotationKeyVersion] = tag
		imageStream := &imgv1.ImageStream{
			ObjectMeta: v1.ObjectMeta{Name: name, Namespace: namespace, Annotations: imageStreamAnnotations},
			Spec: imgv1.ImageStreamSpec{
				LookupPolicy: imgv1.ImageLookupPolicy{Local: true},
				Tags: []imgv1.TagReference{
					{
						Name:            tag,
						Annotations:     imageStreamTagAnnotations,
						ReferencePolicy: imgv1.TagReferencePolicy{Type: imgv1.LocalTagReferencePolicy},
						ImportPolicy:    imgv1.TagImportPolicy{Insecure: insecureImageRegistry},
					},
				},
			},
		}
		if addFromReference {
			imageStream.Spec.Tags[0].From = &corev1.ObjectReference{
				Kind: dockerImageKind,
				Name: imageName,
			}
		}
		return imageStream
	}
	return nil
}
