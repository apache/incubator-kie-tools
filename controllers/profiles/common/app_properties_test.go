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

package common

import (
	"testing"

	"github.com/magiconair/properties"

	"github.com/stretchr/testify/assert"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/metadata"
	"github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/test"
)

func Test_appPropertyHandler_WithKogitoServiceUrl(t *testing.T) {
	workflow := test.GetBaseSonataFlow("default")
	props := ImmutableApplicationProperties(workflow, nil)
	assert.Contains(t, props, kogitoServiceUrlProperty)
	assert.Contains(t, props, "http://"+workflow.Name+"."+workflow.Namespace)
}

func Test_appPropertyHandler_WithUserPropertiesWithNoUserOverrides(t *testing.T) {
	//just add some user provided properties, no overrides.
	userProperties := "property1=value1\nproperty2=value2"
	workflow := test.GetBaseSonataFlow("default")
	props := NewAppPropertyHandler(workflow, nil).WithUserProperties(userProperties).Build()
	generatedProps, propsErr := properties.LoadString(props)
	assert.NoError(t, propsErr)
	assert.Equal(t, 8, len(generatedProps.Keys()))
	assert.Equal(t, "value1", generatedProps.GetString("property1", ""))
	assert.Equal(t, "value2", generatedProps.GetString("property2", ""))
	assert.Equal(t, "http://greeting.default", generatedProps.GetString("kogito.service.url", ""))
	assert.Equal(t, "8080", generatedProps.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", generatedProps.GetString("quarkus.http.host", ""))
	assert.Equal(t, "false", generatedProps.GetString("org.kie.kogito.addons.knative.eventing.health-enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.kogito.devservices.enabled", ""))
}

func Test_appPropertyHandler_WithServicesWithUserOverrides(t *testing.T) {
	//try to override kogito.service.url and quarkus.http.port
	userProperties := "property1=value1\nproperty2=value2\nquarkus.http.port=9090\nkogito.service.url=http://myUrl.override.com\nquarkus.http.port=9090"
	ns := "default"
	workflow := test.GetBaseSonataFlow(ns)
	enabled := true
	platform := test.GetBasePlatform()
	platform.Namespace = ns
	platform.Spec = v1alpha08.SonataFlowPlatformSpec{
		Services: v1alpha08.ServicesPlatformSpec{
			DataIndex: &v1alpha08.ServiceSpec{
				Enabled: &enabled,
			},
		},
	}

	props := NewAppPropertyHandler(workflow, platform).WithUserProperties(userProperties).Build()
	generatedProps, propsErr := properties.LoadString(props)
	assert.NoError(t, propsErr)
	assert.Equal(t, 8, len(generatedProps.Keys()))
	assert.Equal(t, "value1", generatedProps.GetString("property1", ""))
	assert.Equal(t, "value2", generatedProps.GetString("property2", ""))
	//kogito.service.url takes the user provided value since it's a default mutable property.
	assert.Equal(t, "http://myUrl.override.com", generatedProps.GetString("kogito.service.url", ""))
	//quarkus.http.port remains with the default value since it's immutable.
	assert.Equal(t, "8080", generatedProps.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", generatedProps.GetString("quarkus.http.host", ""))
	assert.Equal(t, "false", generatedProps.GetString("org.kie.kogito.addons.knative.eventing.health-enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.kogito.devservices.enabled", ""))
	assert.Equal(t, "", generatedProps.GetString(dataIndexServiceUrlProperty, ""))

	// prod profile enables config of outgoing events url
	workflow.SetAnnotations(map[string]string{metadata.Profile: string(metadata.ProdProfile)})
	props = NewAppPropertyHandler(workflow, platform).WithUserProperties(userProperties).Build()
	generatedProps, propsErr = properties.LoadString(props)
	assert.NoError(t, propsErr)
	assert.Equal(t, 9, len(generatedProps.Keys()))
	assert.Equal(t, "http://"+platform.Name+"-data-index-service."+platform.Namespace+"/processes", generatedProps.GetString(dataIndexServiceUrlProperty, ""))

	// disabling data index bypasses config of outgoing events url
	platform.Spec.Services.DataIndex.Enabled = nil
	props = NewAppPropertyHandler(workflow, platform).WithUserProperties(userProperties).Build()
	generatedProps, propsErr = properties.LoadString(props)
	assert.NoError(t, propsErr)
	assert.Equal(t, 8, len(generatedProps.Keys()))
	assert.Equal(t, "", generatedProps.GetString(dataIndexServiceUrlProperty, ""))
}
