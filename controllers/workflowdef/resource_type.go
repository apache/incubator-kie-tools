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

package workflowdef

import (
	"strings"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
)

type ExternalResourceType string

const (
	ExternalResourceCamel    ExternalResourceType = "resource-camel"
	ExternalResourceOpenApi  ExternalResourceType = "resource-openapi"
	ExternalResourceAsyncApi ExternalResourceType = "resource-asyncapi"
	ExternalResourceGeneric  ExternalResourceType = "resource-generic"
	ExternalResourceNone     ExternalResourceType = ""
)

// ExternalResourceCamelMountDir directory where to mount Camel Routes resource files
const ExternalResourceCamelMountDir = "routes"

// ExternalResourceDestinationDir map for special directories within the resource context.
// In dev mode means within the src/main/resources. In build contexts, the actual context dir.
var ExternalResourceDestinationDir = map[ExternalResourceType]string{
	ExternalResourceGeneric:  "",
	ExternalResourceCamel:    ExternalResourceCamelMountDir,
	ExternalResourceOpenApi:  "",
	ExternalResourceAsyncApi: "",
}

const externalResourceAnnotationPrefix = "resource"

// GetAnnotationResourceType gets the resource type for the given annotation.
// Example: Workflows can be annotated with sw.kogito.kie.org/resource-openapi=MyOpenApisConfigMapName.
// This means that a ConfigMap named MyOpenApisConfigMapName is available in the current context, and it will be mounted in the workflow.
// This function returns "resource-openapi" for the given example above.
func GetAnnotationResourceType(annotationKey string) ExternalResourceType {
	stringArray := strings.Split(annotationKey, "/")
	if len(stringArray) == 2 && stringArray[0] == metadata.Domain && strings.HasPrefix(stringArray[1], externalResourceAnnotationPrefix) {
		return ParseExternalResourceType(stringArray[1])
	} else {
		return ""
	}
}

// GetExternalResourceTypeAnnotation gets the key for the ExternalResourceType annotation.
func GetExternalResourceTypeAnnotation(resourceType ExternalResourceType) string {
	return metadata.Domain + "/" + string(resourceType)
}

// ParseExternalResourceType ...
func ParseExternalResourceType(resourceType string) ExternalResourceType {
	switch resourceType {
	case string(ExternalResourceCamel):
		return ExternalResourceCamel
	case string(ExternalResourceOpenApi):
		return ExternalResourceOpenApi
	case string(ExternalResourceAsyncApi):
		return ExternalResourceAsyncApi
	case string(ExternalResourceGeneric):
	default:
		return ExternalResourceNone
	}
	return ExternalResourceNone
}
