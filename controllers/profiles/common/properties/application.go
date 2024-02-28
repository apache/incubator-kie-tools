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

	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/discovery"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform/services"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/constants"

	"github.com/magiconair/properties"

	"k8s.io/klog/v2"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
)

var (
	immutableApplicationProperties = fmt.Sprintf("quarkus.http.port=%d\n"+
		"quarkus.http.host=0.0.0.0\n"+
		"quarkus.devservices.enabled=false\n"+
		"quarkus.kogito.devservices.enabled=false\n", constants.DefaultHTTPWorkflowPortInt)
	_ AppPropertyHandler = &appPropertyHandler{}
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
	var userProps *properties.Properties
	var propErr error = nil
	if len(a.userProperties) == 0 {
		userProps = properties.NewProperties()
	} else {
		userProps, propErr = properties.LoadString(a.userProperties)
	}
	if propErr != nil {
		klog.V(log.D).InfoS("Can't load user's property", "workflow", a.workflow.Name, "namespace", a.workflow.Namespace, "properties", a.userProperties)
		userProps = properties.NewProperties()
	}
	// Disable expansions since it's not our responsibility
	// Property expansion means resolving ${} within the properties and environment context. Quarkus will do that in runtime.
	userProps.DisableExpansion = true

	removeDiscoveryProperties(userProps)
	discoveryProps := properties.NewProperties()
	if a.requireServiceDiscovery() {
		// produce the MicroProfileConfigServiceCatalog properties for the service discovery property values if any.
		discoveryProps.Merge(generateDiscoveryProperties(a.ctx, a.catalog, userProps, a.workflow))
	}
	userProps = utils.NewApplicationPropertiesBuilder().
		WithInitialProperties(discoveryProps).
		WithImmutableProperties(properties.MustLoadString(immutableApplicationProperties)).
		WithDefaultMutableProperties(a.defaultMutableProperties).
		Build()

	return userProps.String()
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

// NewAppPropertyHandler creates a property handler for a given workflow to execute in the provided platform.
// This handler is intended to build the managed application properties required by the workflow to execute properly together with
// the user properties defined in the user-managed ConfigMap.
// Note that the produced properties might vary depending on the platfom, for example, if the job service managed by the platform
// a particular set of properties will be added, etc.
// By default, the following properties are incorporated:
// The set of immutable properties provided by the operator. (user can never change)
// The set of defaultMutableProperties that are provided by the operator, and that the user cannot overwrite even if it changes
// the user-managed ConfigMap. This set includes for example the required properties to connect with the data index and the
// job service when any of these services are managed by the platform.
func NewAppPropertyHandler(workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) (AppPropertyHandler, error) {
	handler := &appPropertyHandler{
		workflow: workflow,
		platform: platform,
	}
	props := properties.NewProperties()
	props.Set(constants.KogitoUserTasksEventsEnabled, "false")
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
		p, err = generateKnativeEventingWorkflowProperties(workflow)
		if err != nil {
			return nil, err
		}
		props.Merge(p)
		props.Sort()
	}
	handler.defaultMutableProperties = props
	return handler.withKogitoServiceUrl(), nil
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
