/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package workflowproj

import (
	"os"
	"testing"
)

func TestParseResourceType(t *testing.T) {
	type args struct {
		contents []byte
	}
	tests := []struct {
		name string
		args args
		want ResourceKind
	}{
		{name: "valid openapi", args: args{contents: getResourceContents("valid-openapi.yaml")}, want: OpenApiResource},
		{name: "valid asyncapi", args: args{contents: getResourceContents("valid-asyncapi.yaml")}, want: AsyncApiResource},
		{name: "valid camel", args: args{contents: getResourceContents("valid-camelroute.yaml")}, want: CamelRouteResource},
		{name: "valid openapi (JSON)", args: args{contents: getResourceContents("valid-openapi.json")}, want: OpenApiResource},
		{name: "valid asyncapi (JSON)", args: args{contents: getResourceContents("valid-asyncapi.json")}, want: AsyncApiResource},
		{name: "valid camel (JSON)", args: args{contents: getResourceContents("valid-camelroute.json")}, want: CamelRouteResource},
		{name: "generic resource", args: args{contents: getResourceContents("mygeneric.wsdl")}, want: GenericResource},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := ParseResourceKind(tt.args.contents)
			if got != tt.want {
				t.Errorf("ParseResourceKind() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func getResourceContents(filename string) []byte {
	contents, err := os.ReadFile("testdata/" + filename)
	if err != nil {
		panic(err)
	}
	return contents
}
