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

package framework

import (
	"reflect"
	"testing"

	"github.com/kiegroup/kogito-operator/api"
)

func TestFromStringToImage(t *testing.T) {
	type args struct {
		imageTag string
	}
	tests := []struct {
		name string
		args args
		want api.Image
	}{
		{"empty", args{""}, api.Image{}},
		{"with registry name and namespace", args{"quay.io/openshift/myimage:1.0"}, api.Image{Name: "myimage", Tag: "1.0", Domain: "quay.io/openshift"}},
		{"with registry name, namespace and port", args{"quay.io:5000/openshift/myimage:1.0"}, api.Image{Name: "myimage", Tag: "1.0", Domain: "quay.io:5000/openshift"}},
		{"with registry name and image", args{"quay.io/myimage:1.0"}, api.Image{Name: "myimage", Tag: "1.0", Domain: "quay.io"}},
		{"domain empty", args{"myimage:1.0"}, api.Image{Name: "myimage", Tag: "1.0", Domain: ""}},
		{"tag empty", args{"myimage"}, api.Image{Name: "myimage", Tag: "latest", Domain: ""}},
		{"tag empty with a trick", args{"myimage:"}, api.Image{Name: "myimage", Tag: "latest", Domain: ""}},
		{"just tag", args{":1.0"}, api.Image{Name: "", Tag: "1.0", Domain: ""}},
		{"localhost domain", args{"localhost:6000/namespace/image"}, api.Image{Name: "image", Tag: "latest", Domain: "localhost:6000/namespace"}},
		{"IP only", args{"10.10.2.1/namespace/image"}, api.Image{Name: "image", Tag: "latest", Domain: "10.10.2.1/namespace"}},
		{"IP and port", args{"10.10.2.1:5000/namespace/image"}, api.Image{Name: "image", Tag: "latest", Domain: "10.10.2.1:5000/namespace"}},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := ConvertImageTagToImage(tt.args.imageTag); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("ConvertImageTagToImage() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestFromImageToString(t *testing.T) {
	type args struct {
		image api.Image
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{"empty", args{api.Image{}}, ""},
		{"with registry name", args{api.Image{Name: "myimage", Tag: "1.0", Domain: "quay.io/openshift"}}, "quay.io/openshift/myimage:1.0"},
		{"with registry name and port", args{api.Image{Name: "myimage", Tag: "1.0", Domain: "quay.io:5000/openshift"}}, "quay.io:5000/openshift/myimage:1.0"},
		{"full name", args{api.Image{Name: "myimage", Tag: "1.0", Domain: "openshift"}}, "openshift/myimage:1.0"},
		{"namespace empty", args{api.Image{Name: "myimage", Tag: "1.0", Domain: ""}}, "myimage:1.0"},
		{"tag empty", args{api.Image{Name: "myimage", Tag: "", Domain: ""}}, "myimage"},
		{"just tag", args{api.Image{Name: "", Tag: "1.0", Domain: ""}}, ":1.0"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := ConvertImageToImageTag(tt.args.image); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("ConvertImageTagToImage() = %v, want %v", got, tt.want)
			}
		})
	}
}
