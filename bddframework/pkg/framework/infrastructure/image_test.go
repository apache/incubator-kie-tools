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

package infrastructure

import (
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	"testing"

	"github.com/stretchr/testify/assert"
)

func Test_imageHandler_resolveImageOnOpenShiftWithImageStreamCreated(t *testing.T) {
	ns := t.Name()
	is, tag := test.CreateImageStreams("jobs-service", ns, "my-data-index", GetKogitoImageVersion())
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(is).AddImageObjects(tag).Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	imageHandler := NewImageHandler(context, &api.Image{Name: "jobs-service"}, "jobs-service", "jobs-service", ns, false, false)
	image, err := imageHandler.ResolveImage()
	assert.NoError(t, err)
	// since we have imagestream and tag, we should see them here
	assert.Contains(t, image, "jobs-service")
}

func Test_imageHandler_resolveImageOnOpenShiftNoImageStreamCreated(t *testing.T) {
	ns := t.Name()
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	imageHandler := NewImageHandler(context, &api.Image{Name: "jobs-service"}, "jobs-service", "jobs-service", ns, false, false)
	image, err := imageHandler.ResolveImage()
	assert.NoError(t, err)
	// on OpenShift and no ImageStream? Bye!
	assert.Empty(t, image)
}

func Test_imageHandler_resolveImageOnKubernetes(t *testing.T) {
	ns := t.Name()
	cli := test.NewFakeClientBuilder().Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	imageHandler := NewImageHandler(context, &api.Image{Name: "jobs-service"}, "jobs-service", "jobs-service", ns, false, false)
	image, err := imageHandler.ResolveImage()
	assert.NoError(t, err)
	// we should always have an image available on Kubernetes
	assert.Contains(t, image, "jobs-service")
}

func Test_imageHandler_newImageHandlerInsecureImageRegistry(t *testing.T) {
	ns := t.Name()
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	imageHandler := NewImageHandler(context, &api.Image{Name: "jobs-service"}, "jobs-service", "jobs-service", ns, false, true)
	imageStream, err := imageHandler.CreateImageStreamIfNotExists()
	assert.NoError(t, err)
	assert.Equal(t, 1, len(imageStream.Spec.Tags))
	assert.True(t, imageStream.Spec.Tags[0].ImportPolicy.Insecure)
}

func Test_getRuntimeImageVersion(t *testing.T) {
	type args struct {
		v string
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{"Usual case", args{v: "0.9.0"}, "0.9"},
		{"Micro update case", args{v: "0.9.1"}, "0.9"},
		{"Micro micro update case", args{v: "0.9.1.1"}, "0.9"},
		{"Unusual version", args{v: "0.9"}, "0.9"},
		{"Weird version", args{v: "0"}, "0"},
		{"No version", args{v: ""}, LatestTag},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := getKogitoImageVersion(tt.args.v); got != tt.want {
				t.Errorf("getKogitoImageVersion() = %v, want %v", got, tt.want)
			}
		})
	}
}
