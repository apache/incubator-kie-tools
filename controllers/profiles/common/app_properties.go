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
	"fmt"

	"github.com/magiconair/properties"
	"k8s.io/klog/v2"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
)

const (
	ConfigMapWorkflowPropsVolumeName = "workflow-properties"
	kogitoServiceUrlProperty         = "kogito.service.url"
	kogitoServiceUrlProtocol         = "http"
)

var immutableApplicationProperties = "quarkus.http.port=" + defaultHTTPWorkflowPortIntStr.String() + "\n" +
	"quarkus.http.host=0.0.0.0\n" +
	// We disable the Knative health checks to not block the dev pod to run if Knative objects are not available
	// See: https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/eventing/consume-produce-events-with-knative-eventing.html#ref-knative-eventing-add-on-source-configuration
	"org.kie.kogito.addons.knative.eventing.health-enabled=false\n" +
	"quarkus.devservices.enabled=false\n" +
	"quarkus.kogito.devservices.enabled=false\n"

var _ AppPropertyHandler = &appPropertyHandler{}

type AppPropertyHandler interface {
	WithUserProperties(userProperties string) AppPropertyHandler
	Build() string
}

type appPropertyHandler struct {
	workflow                 *operatorapi.SonataFlow
	userProperties           string
	defaultMutableProperties string
}

func (a *appPropertyHandler) WithUserProperties(properties string) AppPropertyHandler {
	a.userProperties = properties
	return a
}

func (a *appPropertyHandler) Build() string {
	var props *properties.Properties
	var propErr error = nil
	if len(a.userProperties) == 0 {
		props = properties.NewProperties()
	} else {
		props, propErr = properties.LoadString(a.userProperties)
	}
	if propErr != nil {
		// can't load user's properties, ignore it
		klog.V(log.D).InfoS("Can't load user's property", "workflow", a.workflow.Name, "namespace", a.workflow.Namespace, "properties", a.userProperties)
		props = properties.NewProperties()
	}
	// Disable expansions since it's not our responsibility
	// Property expansion means resolving ${} within the properties and environment context. Quarkus will do that in runtime.
	props.DisableExpansion = true
	defaultMutableProps := properties.MustLoadString(a.defaultMutableProperties)
	for _, k := range defaultMutableProps.Keys() {
		if _, ok := props.Get(k); ok {
			defaultMutableProps.Delete(k)
		}
	}
	// overwrite with the default mutable properties provided by the operator that are not set by the user.
	props.Merge(defaultMutableProps)
	defaultImmutableProps := properties.MustLoadString(immutableApplicationProperties)
	// finally overwrite with the defaults immutable properties.
	props.Merge(defaultImmutableProps)
	return props.String()
}

// withKogitoServiceUrl adds the property kogitoServiceUrlProperty to the application properties.
// See Service Discovery https://kubernetes.io/docs/concepts/services-networking/service/#dns
func (a *appPropertyHandler) withKogitoServiceUrl() AppPropertyHandler {
	var kogitoServiceUrl string
	if len(a.workflow.Namespace) > 0 {
		kogitoServiceUrl = fmt.Sprintf("%s://%s.%s", kogitoServiceUrlProtocol, a.workflow.Name, a.workflow.Namespace)
	} else {
		kogitoServiceUrl = fmt.Sprintf("%s://%s", kogitoServiceUrlProtocol, a.workflow.Name)
	}
	return a.addDefaultMutableProperty(kogitoServiceUrlProperty, kogitoServiceUrl)
}

func (a *appPropertyHandler) addDefaultMutableProperty(name string, value string) AppPropertyHandler {
	a.defaultMutableProperties = a.defaultMutableProperties + fmt.Sprintf("%s=%s\n", name, value)
	return a
}

// NewAppPropertyHandler creates the default workflow configurations property handler
// The set of properties is initialized with the operator provided immutable properties.
// The set of defaultMutableProperties is initialized with the operator provided properties that the user might override.
func NewAppPropertyHandler(workflow *operatorapi.SonataFlow) AppPropertyHandler {
	handler := &appPropertyHandler{
		workflow: workflow,
	}
	return handler.withKogitoServiceUrl()
}

// ImmutableApplicationProperties immutable default application properties that can be used with any workflow based on Quarkus.
// Alias for NewAppPropertyHandler(workflow).Build()
func ImmutableApplicationProperties(workflow *operatorapi.SonataFlow) string {
	return NewAppPropertyHandler(workflow).Build()
}
