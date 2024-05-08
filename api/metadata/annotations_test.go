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

package metadata

import (
	"testing"
)

func TestGetProfile(t *testing.T) {
	type args struct {
		annotation map[string]string
	}
	tests := []struct {
		name string
		args args
		want ProfileType
	}{
		{"Empty Annotations", args{annotation: nil}, DefaultProfile},
		{"Non-existent Profile", args{annotation: map[string]string{Profile: "IDontExist"}}, DefaultProfile},
		{"Regular Annotation", args{annotation: map[string]string{Profile: GitOpsProfile.String()}}, GitOpsProfile},
		{"Deprecated Annotation", args{annotation: map[string]string{Profile: ProdProfile.String()}}, DefaultProfile},
		{"Dev Annotation", args{annotation: map[string]string{Profile: DevProfile.String()}}, DevProfile},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := GetProfileOrDefault(tt.args.annotation); got != tt.want {
				t.Errorf("GetProfileOrDefault() = %v, want %v", got, tt.want)
			}
		})
	}
}
