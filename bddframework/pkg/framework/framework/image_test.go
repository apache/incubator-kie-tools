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
	"github.com/kiegroup/kogito-operator/api"
	"reflect"
	"testing"
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
		{"with registry name", args{"quay.io/openshift/myimage:1.0"}, api.Image{Name: "myimage", Tag: "1.0", Namespace: "openshift", Domain: "quay.io"}},
		{"with registry name and port", args{"quay.io:5000/openshift/myimage:1.0"}, api.Image{Name: "myimage", Tag: "1.0", Namespace: "openshift", Domain: "quay.io:5000"}},
		{"full name", args{"openshift/myimage:1.0"}, api.Image{Name: "myimage", Tag: "1.0", Namespace: "openshift"}},
		{"namespace empty", args{"myimage:1.0"}, api.Image{Name: "myimage", Tag: "1.0", Namespace: "", Domain: ""}},
		{"tag empty", args{"myimage"}, api.Image{Name: "myimage", Tag: "latest", Namespace: "", Domain: ""}},
		{"tag empty with a trick", args{"myimage:"}, api.Image{Name: "myimage", Tag: "latest", Namespace: "", Domain: ""}},
		{"just tag", args{":1.0"}, api.Image{Name: "", Tag: "1.0", Namespace: "", Domain: ""}},
		{"localhost domain", args{"localhost:6000/namespace/image"}, api.Image{Name: "image", Tag: "latest", Namespace: "namespace", Domain: "localhost:6000"}},
		{"IP only", args{"10.10.2.1/namespace/image"}, api.Image{Name: "image", Tag: "latest", Namespace: "namespace", Domain: "10.10.2.1"}},
		{"IP and port", args{"10.10.2.1:5000/namespace/image"}, api.Image{Name: "image", Tag: "latest", Namespace: "namespace", Domain: "10.10.2.1:5000"}},
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
		{"with registry name", args{api.Image{Name: "myimage", Tag: "1.0", Namespace: "openshift", Domain: "quay.io"}}, "quay.io/openshift/myimage:1.0"},
		{"with registry name and port", args{api.Image{Name: "myimage", Tag: "1.0", Namespace: "openshift", Domain: "quay.io:5000"}}, "quay.io:5000/openshift/myimage:1.0"},
		{"full name", args{api.Image{Name: "myimage", Tag: "1.0", Namespace: "openshift"}}, "openshift/myimage:1.0"},
		{"namespace empty", args{api.Image{Name: "myimage", Tag: "1.0", Namespace: "", Domain: ""}}, "myimage:1.0"},
		{"tag empty", args{api.Image{Name: "myimage", Tag: "", Namespace: "", Domain: ""}}, "myimage"},
		{"just tag", args{api.Image{Name: "", Tag: "1.0", Namespace: "", Domain: ""}}, ":1.0"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := ConvertImageToImageTag(tt.args.image); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("ConvertImageTagToImage() = %v, want %v", got, tt.want)
			}
		})
	}
}
