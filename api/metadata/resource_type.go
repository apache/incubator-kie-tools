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

package metadata

import (
	"strings"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

type ExtResType string

const (
	ExtResCamel    ExtResType = "resource-camel"
	ExtResOpenApi  ExtResType = "resource-openapi"
	ExtResAsyncApi ExtResType = "resource-asyncapi"
	ExtResGeneric  ExtResType = "resource-generic"
	ExtResNone     ExtResType = ""
)

const extResAnnotationPrefix = "resource"

// GetAnnotationExtResType gets the resource type for the given annotation.
// Example: Workflows can be annotated with sw.kogito.kie.org/resource-openapi=MyOpenApisConfigMapName.
// This means that a ConfigMap named MyOpenApisConfigMapName is available in the current context, and it will be mounted in the workflow.
// This function returns "resource-openapi" for the given example above.
func GetAnnotationExtResType(workflowAnnonKey string) ExtResType {
	stringArray := strings.Split(workflowAnnonKey, "/")
	if len(stringArray) == 2 && stringArray[0] == Domain && strings.HasPrefix(stringArray[1], extResAnnotationPrefix) {
		return ParseExtResType(stringArray[1])
	} else {
		return ""
	}
}

// AddAnnotationExtResType adds the ExtResType annotation targeting the configMapName into the given workflow.
func AddAnnotationExtResType(workflow metav1.Object, resourceType ExtResType, configMapName string) {
	annotations := workflow.GetAnnotations()
	if annotations == nil {
		annotations = map[string]string{}
	}
	annotations[Domain+"/"+string(resourceType)] = configMapName
	workflow.SetAnnotations(annotations)
}

// GetExtResTypeAnnotation gets the key for the ExtResType annotation.
func GetExtResTypeAnnotation(resourceType ExtResType) string {
	return Domain + "/" + string(resourceType)
}

// ParseExtResType ...
func ParseExtResType(resourceType string) ExtResType {
	switch resourceType {
	case string(ExtResCamel):
		return ExtResCamel
	case string(ExtResOpenApi):
		return ExtResOpenApi
	case string(ExtResAsyncApi):
		return ExtResAsyncApi
	case string(ExtResGeneric):
	default:
		return ExtResNone
	}
	return ExtResNone
}
