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
	"strings"

	"github.com/pb33f/libopenapi"
	libopenapiutils "github.com/pb33f/libopenapi/utils"
	"github.com/santhosh-tekuri/jsonschema/v5"
	"k8s.io/apimachinery/pkg/util/yaml"
)

type ResourceKind int

const (
	OpenApiResource ResourceKind = iota
	AsyncApiResource
	CamelRouteResource
	GenericResource
)

// ParseResourceKind tries to parse the contents of the given resource and find the correct type.
// Async and OpenAPI files are pretty fast to parse (0.00s).
// Camel and generic files can take a fair price from the CPU (0.03s on the i5) since it takes more processing power.
func ParseResourceKind(contents []byte) ResourceKind {
	if len(contents) == 0 {
		return GenericResource
	}
	doc, err := libopenapi.NewDocument(contents)
	if err == nil {
		switch doc.GetSpecInfo().SpecType {
		case libopenapiutils.AsyncApi:
			return AsyncApiResource
		default:
			return OpenApiResource
		}
	}
	if err = validateCamelRoute(contents); err == nil {
		return CamelRouteResource
	}
	return GenericResource
}

func validateCamelRoute(contents []byte) error {
	schema, err := jsonschema.CompileString("camel.json", camelSchema)
	if err != nil {
		return err
	}
	decoder := yaml.NewYAMLOrJSONDecoder(strings.NewReader(string(contents)), 512)
	var v []interface{}
	if err = decoder.Decode(&v); err != nil {
		return err
	}
	return schema.Validate(v)
}
