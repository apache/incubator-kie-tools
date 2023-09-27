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

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/log"
)

const (
	ConfigMapWorkflowPropsVolumeName = "workflow-properties"
	kogitoServiceUrlProperty         = "kogito.service.url"
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
	workflow   *operatorapi.SonataFlow
	properties string
}

func (a *appPropertyHandler) WithUserProperties(userProperties string) AppPropertyHandler {
	if len(userProperties) == 0 {
		return a
	}
	props, propErr := properties.LoadString(userProperties)
	if propErr != nil {
		// can't load user's properties, ignore it
		klog.V(log.D).InfoS("Can't load user's property", "workflow", a.workflow.Name, "namespace", a.workflow.Namespace, "properties", userProperties)
		return a
	}
	defaultProps := properties.MustLoadString(a.properties)
	// we overwrite with the defaults
	props.Merge(defaultProps)
	// Disable expansions since it's not our responsibility
	// Property expansion means resolving ${} within the properties and environment context. Quarkus will do that in runtime.
	props.DisableExpansion = true
	a.properties = props.String()
	return a
}

func (a *appPropertyHandler) Build() string {
	return a.properties
}

// withKogitoServiceUrl adds the property kogitoServiceUrlProperty to the application properties.
// See Service Discovery https://kubernetes.io/docs/concepts/services-networking/service/#dns
func (a *appPropertyHandler) withKogitoServiceUrl() AppPropertyHandler {
	if len(a.workflow.Namespace) > 0 {
		a.properties = a.properties +
			fmt.Sprintf("%s=%s.%s\n", kogitoServiceUrlProperty, a.workflow.Name, a.workflow.Namespace)
	} else {
		a.properties = a.properties +
			fmt.Sprintf("%s=%s\n", kogitoServiceUrlProperty, a.workflow.Name)
	}
	return a
}

// NewAppPropertyHandler creates the default workflow configurations property handler
func NewAppPropertyHandler(workflow *operatorapi.SonataFlow) AppPropertyHandler {
	handler := &appPropertyHandler{
		workflow:   workflow,
		properties: immutableApplicationProperties,
	}
	return handler.withKogitoServiceUrl()
}

// ImmutableApplicationProperties immutable default application properties that can be used with any workflow based on Quarkus.
// Alias for NewAppPropertyHandler(workflow).Build()
func ImmutableApplicationProperties(workflow *operatorapi.SonataFlow) string {
	return NewAppPropertyHandler(workflow).Build()
}
