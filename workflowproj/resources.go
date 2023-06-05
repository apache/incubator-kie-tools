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
	"strings"

	"github.com/pb33f/libopenapi"
	libopenapiutils "github.com/pb33f/libopenapi/utils"
	"github.com/santhosh-tekuri/jsonschema/v5"
	"k8s.io/apimachinery/pkg/util/yaml"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
)

// ParseResourceType tries to parse the contents of the given resource and find the correct type.
// Async and OpenAPI files are pretty fast to parse (0.00s).
// Camel and generic files can take a fair price from the CPU (0.03s on the i5) since it takes more processing power.
func ParseResourceType(contents string) metadata.ExtResType {
	if len(contents) == 0 {
		return metadata.ExtResGeneric
	}
	doc, err := libopenapi.NewDocument([]byte(contents))
	if err == nil {
		switch doc.GetSpecInfo().SpecType {
		case libopenapiutils.AsyncApi:
			return metadata.ExtResAsyncApi
		default:
			return metadata.ExtResOpenApi
		}
	}
	if err = validateCamelRoute(contents); err == nil {
		return metadata.ExtResCamel
	}
	return metadata.ExtResGeneric
}

func validateCamelRoute(contents string) error {
	schema, err := jsonschema.CompileString("camel.json", camelSchema)
	if err != nil {
		return err
	}
	decoder := yaml.NewYAMLOrJSONDecoder(strings.NewReader(contents), 512)
	var v []interface{}
	if err = decoder.Decode(&v); err != nil {
		return err
	}
	return schema.Validate(v)
}
