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

package workflowproj

import (
	"os"
	"testing"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
)

func TestParseResourceType(t *testing.T) {
	type args struct {
		contents string
	}
	tests := []struct {
		name string
		args args
		want metadata.ExtResType
	}{
		{name: "valid openapi", args: args{contents: getResourceContents("valid-openapi.yaml")}, want: metadata.ExtResOpenApi},
		{name: "valid asyncapi", args: args{contents: getResourceContents("valid-asyncapi.yaml")}, want: metadata.ExtResAsyncApi},
		{name: "valid camel", args: args{contents: getResourceContents("valid-camelroute.yaml")}, want: metadata.ExtResCamel},
		{name: "valid openapi (JSON)", args: args{contents: getResourceContents("valid-openapi.json")}, want: metadata.ExtResOpenApi},
		{name: "valid asyncapi (JSON)", args: args{contents: getResourceContents("valid-asyncapi.json")}, want: metadata.ExtResAsyncApi},
		{name: "valid camel (JSON)", args: args{contents: getResourceContents("valid-camelroute.json")}, want: metadata.ExtResCamel},
		{name: "generic resource", args: args{contents: getResourceContents("mygeneric.wsdl")}, want: metadata.ExtResGeneric},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := ParseResourceType(tt.args.contents)
			if got != tt.want {
				t.Errorf("ParseResourceType() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func getResourceContents(filename string) string {
	contents, err := os.ReadFile("testdata/" + filename)
	if err != nil {
		panic(err)
	}
	return string(contents)
}
