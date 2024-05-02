// Copyright 2024 Apache Software Foundation (ASF)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package kubernetes

import (
	"testing"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
)

func TestGetImagePullPolicy(t *testing.T) {
	type args struct {
		imageTag string
	}
	tests := []struct {
		name string
		args args
		want v1.PullPolicy
	}{
		{"Short name with latest", args{"ubi9-micro:latest"}, v1.PullAlways},
		{"Long name with latest", args{"gcr.io/knative-releases/knative.dev/eventing/cmd/event_display:latest"}, v1.PullAlways},
		{"No tag specified", args{"ubi9-micro"}, v1.PullAlways},
		{"Short name with tag", args{"ubi9-micro:9.3.1-2"}, ""},
		{"Long name with tag", args{"gcr.io/knative-releases/knative.dev/eventing/cmd/event_display:1.2"}, ""},
		{"Empty tag", args{""}, ""},
		{"Messy tag", args{":"}, ""},
		{"Sha tag", args{"ubuntu@sha256:3235326357dfb65f1781dbc4df3b834546d8bf914e82cce58e6e6b676e23ce8f"}, ""},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, GetImagePullPolicy(tt.args.imageTag), "GetImagePullPolicy(%v)", tt.args.imageTag)
		})
	}
}
