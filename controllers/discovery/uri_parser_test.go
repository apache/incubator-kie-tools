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

package discovery

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

var KubernetesServicesTestValues = map[string]*ResourceUri{
	"kubernetes:services.v1": nil,

	"kubernetes:services.v1/": nil,

	"kubernetes:services.v1/my-service": NewResourceUriBuilder(KubernetesScheme).
		Kind("services").
		Version("v1").
		Name("my-service").Build(),

	"kubernetes:services.v1/my-service?": nil,

	"kubernetes:services.v1/my-service?label-a": nil,

	"kubernetes:services.v1/my-service?label-a=": nil,

	"kubernetes:services.v1/my-service?label-a=value-a": NewResourceUriBuilder(KubernetesScheme).
		Kind("services").
		Version("v1").
		Name("my-service").
		WithLabel("label-a", "value-a").Build(),

	"kubernetes:services.v1/my-service?label-a=value-a&": nil,

	"kubernetes:services.v1/my-service?label-a=value-a&label-b": nil,

	"kubernetes:services.v1/my-service?label-a=value-a&label-b=": nil,

	"kubernetes:services.v1/my-service?label-a=value-a&label-b=value-b": NewResourceUriBuilder(KubernetesScheme).
		Kind("services").
		Version("v1").
		Name("my-service").
		WithLabel("label-a", "value-a").
		WithLabel("label-b", "value-b").Build(),

	"kubernetes:services.v1/my-namespace/": nil,

	"kubernetes:services.v1/my-namespace/my-service": NewResourceUriBuilder(KubernetesScheme).
		Kind("services").
		Version("v1").
		Namespace("my-namespace").
		Name("my-service").
		Build(),

	"kubernetes:services.v1/my-namespace/my-service/": nil,

	"kubernetes:services.v1/my-namespace/my-service/another": nil,

	"kubernetes:services.v1/my-namespace/my-service?label-a": nil,

	"kubernetes:services.v1/my-namespace/my-service?label-a=": nil,

	"kubernetes:services.v1/my-namespace/my-service?label-a=value-a": NewResourceUriBuilder(KubernetesScheme).
		Kind("services").
		Version("v1").
		Namespace("my-namespace").
		Name("my-service").
		WithLabel("label-a", "value-a").Build(),

	"kubernetes:services.v1/my-namespace/my-service?label-a=value-a&": nil,

	"kubernetes:services.v1/my-namespace/my-service?label-a=value-a&label-b": nil,

	"kubernetes:services.v1/my-namespace/my-service?label-a=value-a&label-b=": nil,

	"kubernetes:services.v1/my-namespace/my-service?label-a=value-a&label-b=value-b": NewResourceUriBuilder(KubernetesScheme).
		Kind("services").
		Version("v1").
		Namespace("my-namespace").
		Name("my-service").
		WithLabel("label-a", "value-a").
		WithLabel("label-b", "value-b").Build(),
}

func TestParseKubernetesServicesURI(t *testing.T) {
	for k, v := range KubernetesServicesTestValues {
		doTestParseKubernetesServicesURI(t, k, v)
	}
}

func doTestParseKubernetesServicesURI(t *testing.T, url string, expectedUri *ResourceUri) {
	result, err := ParseUri(url)
	if expectedUri == nil {
		if result != nil {
			assert.Nil(t, result, "parsing of url: %s should have failed, but returned: %s", url, result.String())
		}
		assert.Error(t, err, "parsing of url: %s should have failed", url)
	} else {
		assertEquals(t, result, expectedUri)
	}
}

func assertEquals(t *testing.T, uri *ResourceUri, expectedUri *ResourceUri) {
	assert.NotNil(t, uri, "uri can not be nil")
	assert.NotNil(t, expectedUri, "expectedUri can not be nil")
	assert.Equal(t, uri.Scheme, expectedUri.Scheme)
	assert.Equal(t, uri.Namespace, expectedUri.Namespace)
	assert.Equal(t, uri.Name, expectedUri.Name)
	assert.Equal(t, uri.GetPort(), expectedUri.GetPort())
	assert.Equal(t, uri.GVK.Group, expectedUri.GVK.Group)
	assert.Equal(t, uri.GVK.Version, expectedUri.GVK.Version)
	assert.Equal(t, uri.GVK.Kind, expectedUri.GVK.Kind)
	assert.Equal(t, len(uri.CustomLabels), len(expectedUri.CustomLabels))
	for k, v := range uri.CustomLabels {
		assert.True(t, len(expectedUri.CustomLabels[k]) > 0, "label %s is not present in expectedUri: %s", k, expectedUri.String())
		assert.Equal(t, v, expectedUri.CustomLabels[k], "value for label %s in expectedUri should be %s, but is %s", k, v, expectedUri.CustomLabels[k])
	}
}
