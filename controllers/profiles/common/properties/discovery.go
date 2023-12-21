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

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/discovery"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
	"github.com/magiconair/properties"
	"k8s.io/klog/v2"
)

const (
	microprofileServiceCatalogPropertyPrefix = "org.kie.kogito.addons.discovery."
	discoveryLikePropertyPattern             = "^\\${(kubernetes|knative|openshift):(.*)}$"
	knativeServiceOperationPrefix            = "knative:services.v1.serving.knative.dev"
)

var discoveryLikePropertyExpr = regexp.MustCompile(discoveryLikePropertyPattern)

func removeDiscoveryProperties(props *properties.Properties) {
	for _, k := range props.Keys() {
		if strings.HasPrefix(k, microprofileServiceCatalogPropertyPrefix) {
			props.Delete(k)
		}
	}
}

func generateMicroprofileServiceCatalogProperty(serviceUri string) string {
	escapedServiceUri := escapeValue(serviceUri, ":")
	escapedServiceUri = escapeValue(escapedServiceUri, "/")
	escapedServiceUri = escapeValue(escapedServiceUri, "=")
	property := microprofileServiceCatalogPropertyPrefix + escapedServiceUri
	return property
}

func escapeValue(unescaped string, value string) string {
	return strings.Replace(unescaped, value, fmt.Sprintf("\\%s", value), -1)
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

	for _, function := range workflow.Spec.Flow.Functions {
		klog.V(log.I).Infof("Scanning function: %s for service discovery configuration.", function.Name)
		if strings.HasPrefix(function.Operation, knativeServiceOperationPrefix) {
			klog.V(log.I).Infof("Function %s looks to be a knative service invocation on service: %s.", function.Name, function.Operation)
			if uri, err := discovery.ParseUri(function.Operation); err != nil {
				klog.V(log.I).Infof("Operation: %s not correspond to a valid service discovery configuration, it will be excluded from service discovery.", function.Operation)
			} else {
				if len(uri.Namespace) == 0 {
					klog.V(log.I).Infof("Current operation has no configured namespace, workflow namespace: %s will be used instead.", workflow.Namespace)
					uri.Namespace = workflow.Namespace
				}
				if address, err := catalog.Query(ctx, *uri, ""); err != nil {
					klog.V(log.E).ErrorS(err, "An error was produced during service address resolution.", "serviceUri", function.Operation)
				} else {
					// when the knative service is invoked from the workflow as an Operation, the query params are not
					// used for the microprofile property generation.
					trimmedUri := function.Operation
					if questionMarkIndex := strings.Index(trimmedUri, "?"); questionMarkIndex > 0 {
						trimmedUri = function.Operation[:questionMarkIndex]
					}
					klog.V(log.I).Infof("Service: %s was resolved into the following address: %s.", function.Operation, address)
					mpProperty := generateMicroprofileServiceCatalogProperty(trimmedUri)
					klog.V(log.I).Infof("Generating microprofile service catalog property %s=%s.", mpProperty, address)
					result.MustSet(mpProperty, address)
				}
			}
		}
	}
	return result
}
