// Copyright 2019 Red Hat, Inc. and/or its affiliates
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
	utilsres "github.com/RHsyseng/operator-utils/pkg/resource"
	"k8s.io/apimachinery/pkg/api/meta"
	"reflect"
)

// IsNoKindMatchError verify if the given error is NoKindMatchError for the given group
func IsNoKindMatchError(group string, err error) bool {
	if kindErr, ok := err.(*meta.NoKindMatchError); ok {
		return kindErr.GroupKind.Group == group
	}
	return false
}

// GetResource walks on KubernetesResource map and returns the object for the given name and type
func GetResource(resourceType reflect.Type, name string, resources map[reflect.Type][]utilsres.KubernetesResource) utilsres.KubernetesResource {
	for _, res := range resources[resourceType] {
		if res.GetName() == name {
			return res
		}
	}
	return nil
}
