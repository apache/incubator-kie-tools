// Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package utils

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestRemoveFileExtension(t *testing.T) {
	type args struct {
		fileName string
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{name: "Basic", args: struct{ fileName string }{fileName: "myfile.json"}, want: "myfile"},
		{name: "Just the extension", args: struct{ fileName string }{fileName: ".json"}, want: ""},
		{name: "Many extension separators", args: struct{ fileName string }{fileName: "my.file.awesome.json"}, want: "my.file.awesome"},
		{name: "No extension", args: struct{ fileName string }{fileName: "myfileisrad"}, want: "myfileisrad"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, RemoveFileExtension(tt.args.fileName), "RemoveFileExtension(%v)", tt.args.fileName)
		})
	}
}

func TestRemoveKnownExtension(t *testing.T) {
	type args struct {
		fileName  string
		extension string
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{name: "Basic", args: args{fileName: "myworkflow.sw.json", extension: ".sw.json"}, want: "myworkflow"},
		{name: "No Extension", args: args{fileName: "myworkflow", extension: ".sw.json"}, want: "myworkflow"},
		{name: "No Extension Extension", args: args{fileName: "myworkflow.sw.json", extension: ""}, want: "myworkflow.sw.json"},
		{name: "Mess Extension", args: args{fileName: "myworkflow.sw.json", extension: ".json"}, want: "myworkflow.sw"},
		{name: "No filename", args: args{fileName: "", extension: ".json"}, want: ""},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			assert.Equalf(t, tt.want, RemoveKnownExtension(tt.args.fileName, tt.args.extension), "RemoveKnownExtension(%v, %v)", tt.args.fileName, tt.args.extension)
		})
	}
}
