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

package properties

import (
	"context"
	"fmt"

	"regexp"
	"strings"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/discovery"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform/services"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/constants"

	"github.com/magiconair/properties"

	"k8s.io/klog/v2"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
)

const (
	discoveryLikePropertyPattern = "^\\${(kubernetes|knative|openshift):(.*)}$"
)

var (
	immutableApplicationProperties = fmt.Sprintf("quarkus.http.port=%d\n"+
		"quarkus.http.host=0.0.0.0\n"+
		// We disable the Knative health checks to not block the dev pod to run if Knative objects are not available
		// See: https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/eventing/consume-produce-events-with-knative-eventing.html#ref-knative-eventing-add-on-source-configuration
		"org.kie.kogito.addons.knative.eventing.health-enabled=false\n"+
		"quarkus.devservices.enabled=false\n"+
		"quarkus.kogito.devservices.enabled=false\n", constants.DefaultHTTPWorkflowPortInt)

	discoveryLikePropertyExpr                    = regexp.MustCompile(discoveryLikePropertyPattern)
	_                         AppPropertyHandler = &appPropertyHandler{}
)

type AppPropertyHandler interface {
	WithUserProperties(userProperties string) AppPropertyHandler
	WithServiceDiscovery(ctx context.Context, catalog discovery.ServiceCatalog) AppPropertyHandler
	Build() string
}

type appPropertyHandler struct {
	workflow                 *operatorapi.SonataFlow
	platform                 *operatorapi.SonataFlowPlatform
	catalog                  discovery.ServiceCatalog
	ctx                      context.Context
	userProperties           string
	defaultMutableProperties *properties.Properties
	isService                bool
}

func (a *appPropertyHandler) WithUserProperties(properties string) AppPropertyHandler {
	a.userProperties = properties
	return a
}

func (a *appPropertyHandler) WithServiceDiscovery(ctx context.Context, catalog discovery.ServiceCatalog) AppPropertyHandler {
	a.ctx = ctx
	a.catalog = catalog
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
		if a.isService && a.platform != nil {
			klog.V(log.D).InfoS("Can't load user's property", "platform", a.platform.Name, "namespace", a.platform.Namespace, "properties", a.userProperties)
		} else {
			klog.V(log.D).InfoS("Can't load user's property", "workflow", a.workflow.Name, "namespace", a.workflow.Namespace, "properties", a.userProperties)
		}
		props = properties.NewProperties()
	}
	// Disable expansions since it's not our responsibility
	// Property expansion means resolving ${} within the properties and environment context. Quarkus will do that in runtime.
	props.DisableExpansion = true

	removeDiscoveryProperties(props)
	if a.requireServiceDiscovery() {
		// produce the MicroProfileConfigServiceCatalog properties for the service discovery property values if any.
		discoveryProperties := generateDiscoveryProperties(a.ctx, a.catalog, props, a.workflow)
		if discoveryProperties.Len() > 0 {
			props.Merge(discoveryProperties)
		}
	}

	defaultMutableProps := a.defaultMutableProperties
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
	props.Sort()
	return props.String()
}

// withKogitoServiceUrl adds the property kogitoServiceUrlProperty to the application properties.
// See Service Discovery https://kubernetes.io/docs/concepts/services-networking/service/#dns
func (a *appPropertyHandler) withKogitoServiceUrl() AppPropertyHandler {
	var kogitoServiceUrl string
	if len(a.workflow.Namespace) > 0 {
		kogitoServiceUrl = fmt.Sprintf("%s://%s.%s", constants.KogitoServiceURLProtocol, a.workflow.Name, a.workflow.Namespace)
	} else {
		kogitoServiceUrl = fmt.Sprintf("%s://%s", constants.KogitoServiceURLProtocol, a.workflow.Name)
	}
	return a.addDefaultMutableProperty(constants.KogitoServiceURLProperty, kogitoServiceUrl)
}

// withKafkaHealthCheckDisabled adds the property kafkaSmallRyeHealthProperty to the application properties.
// See Service Discovery https://kubernetes.io/docs/concepts/services-networking/service/#dns
func (a *appPropertyHandler) withKafkaHealthCheckDisabled() AppPropertyHandler {
	a.addDefaultMutableProperty(
		constants.DataIndexKafkaSmallRyeHealthProperty,
		"false",
	)
	return a
}

func (a *appPropertyHandler) addDefaultMutableProperty(name string, value string) AppPropertyHandler {
	a.defaultMutableProperties.Set(name, value)
	return a
}

// NewAppPropertyHandler creates the default workflow configurations property handler
// The set of properties is initialized with the operator provided immutable properties.
// The set of defaultMutableProperties is initialized with the operator provided properties that the user might override.
func NewAppPropertyHandler(workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) (AppPropertyHandler, error) {
	handler := &appPropertyHandler{
		workflow: workflow,
		platform: platform,
	}
	props := properties.NewProperties()
	props.Set(constants.KogitoEventsUserTaskEnabled, "false")
	props.Set(constants.KogitoEventsVariablesEnabled, "false")
	props.Set(constants.KogitoProcessDefinitionsEnabled, "false")
	if platform != nil {
		p, err := services.GenerateDataIndexWorkflowProperties(workflow, platform)
		if err != nil {
			return nil, err
		}
		props.Merge(p)
		p, err = services.GenerateJobServiceWorkflowProperties(workflow, platform)
		if err != nil {
			return nil, err
		}
		props.Merge(p)
		props.Sort()
	}
	handler.defaultMutableProperties = props
	return handler.withKogitoServiceUrl(), nil
}

// NewServicePropertyHandler creates the default service configurations property handler
// The set of properties is initialized with the operator provided immutable properties.
// The set of defaultMutableProperties is initialized with the operator provided properties that the user might override.
func NewServiceAppPropertyHandler(platform *operatorapi.SonataFlowPlatform, ps services.Platform) (AppPropertyHandler, error) {
	handler := &appPropertyHandler{
		platform:  platform,
		isService: true,
	}
	props, err := ps.GenerateServiceProperties()
	if err != nil {
		return nil, err
	}
	handler.defaultMutableProperties = props
	return handler, nil
}

// ImmutableApplicationProperties immutable default application properties that can be used with any workflow based on Quarkus.
// Alias for NewAppPropertyHandler(workflow).Build()
func ImmutableApplicationProperties(workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) (string, error) {
	p, err := NewAppPropertyHandler(workflow, platform)
	if err != nil {
		return "", err
	}
	return p.Build(), nil

}

func (a *appPropertyHandler) requireServiceDiscovery() bool {
	return a.ctx != nil && a.catalog != nil
}

// generateDiscoveryProperties Given a user configured properties set, generates the MicroProfileConfigServiceCatalog
// required properties to resolve the corresponding service addresses base on these properties.
// e.g.
// Given a user configured property like this:
//
//	quarkus.rest-client.acme_financial_service_yml.url=${kubernetes:services.v1/usecase1/financial-service?port=http-port}
//
// generates the following property:
//
//	org.kie.kogito.addons.discovery.kubernetes\:services.v1\/usecase1\/financial-service?port\=http-port=http://10.5.9.1:8080
//
// where http://10.5.9.1:8080 is the corresponding k8s cloud address for the service financial-service in the namespace usecase1.
func generateDiscoveryProperties(ctx context.Context, catalog discovery.ServiceCatalog, props *properties.Properties,
	workflow *operatorapi.SonataFlow) *properties.Properties {
	klog.V(log.I).Infof("Generating service discovery properties for workflow: %s, and namespace: %s.", workflow.Name, workflow.Namespace)
	result := properties.NewProperties()
	props.DisableExpansion = true
	for _, k := range props.Keys() {
		value, _ := props.Get(k)
		klog.V(log.I).Infof("Scanning property %s=%s for service discovery configuration.", k, value)
		if !discoveryLikePropertyExpr.MatchString(value) {
			klog.V(log.I).Infof("Skipping property %s=%s since it does not look like a service discovery configuration.", k, value)
		} else {
			klog.V(log.I).Infof("Property %s=%s looks like a service discovery configuration.", k, value)
			plainUri := value[2 : len(value)-1]
			if uri, err := discovery.ParseUri(plainUri); err != nil {
				klog.V(log.I).Infof("Property %s=%s not correspond to a valid service discovery configuration, it will be excluded from service discovery.", k, value)
			} else {
				if len(uri.Namespace) == 0 {
					klog.V(log.I).Infof("Current service discovery configuration has no configured namespace, workflow namespace: %s will be used instead.", workflow.Namespace)
					uri.Namespace = workflow.Namespace
				}
				if address, err := catalog.Query(ctx, *uri, discovery.KubernetesDNSAddress); err != nil {
					klog.V(log.E).ErrorS(err, "An error was produced during service address resolution.", "serviceUri", plainUri)
				} else {
					klog.V(log.I).Infof("Service: %s was resolved into the following address: %s.", plainUri, address)
					mpProperty := generateMicroprofileServiceCatalogProperty(plainUri)
					klog.V(log.I).Infof("Generating microprofile service catalog property %s=%s.", mpProperty, address)
					result.MustSet(mpProperty, address)
				}
			}
		}
	}
	return result
}

func removeDiscoveryProperties(props *properties.Properties) {
	for _, k := range props.Keys() {
		if strings.HasPrefix(k, constants.MicroprofileServiceCatalogPropertyPrefix) {
			props.Delete(k)
		}
	}
}

func generateMicroprofileServiceCatalogProperty(serviceUri string) string {
	escapedServiceUri := escapeValue(serviceUri, ":")
	escapedServiceUri = escapeValue(escapedServiceUri, "/")
	escapedServiceUri = escapeValue(escapedServiceUri, "=")
	property := constants.MicroprofileServiceCatalogPropertyPrefix + escapedServiceUri
	return property
}

func escapeValue(unescaped string, value string) string {
	return strings.Replace(unescaped, value, fmt.Sprintf("\\%s", value), -1)
}
