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
	"testing"

	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/discovery"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/platform/services"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/constants"

	"github.com/magiconair/properties"

	"github.com/stretchr/testify/assert"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
)

const (
	defaultNamespace  = "default-namespace"
	namespace1        = "namespace1"
	myService1        = "my-service1"
	myService1Address = "http://10.110.90.1:80"
	myService2        = "my-service2"
	myService2Address = "http://10.110.90.2:80"
	myService3        = "my-service3"
	myService3Address = "http://10.110.90.3:80"

	myKnService1        = "my-kn-service1"
	myKnService1Address = "http://my-kn-sevice1.namespace1.svc.cluster.local"

	myKnService2        = "my-kn-service2"
	myKnService2Address = "http://my-kn-sevice2.namespace1.svc.cluster.local"

	myKnService3        = "my-kn-service3"
	myKnService3Address = "http://my-kn-sevice3.default-namespace.svc.cluster.local"

	myKnBroker1        = "my-kn-broker1"
	myKnBroker1Address = "http://broker-ingress.knative-eventing.svc.cluster.local/namespace1/my-kn-broker1"

	myKnBroker2        = "my-kn-broker2"
	myKnBroker2Address = "http://broker-ingress.knative-eventing.svc.cluster.local/default-namespace/my-kn-broker2"
)

var (
	enabled                           = true
	disabled                          = false
	jobServiceDevProperties           *properties.Properties
	jobServiceProdProperties          *properties.Properties
	dataIndexDevProperties            *properties.Properties
	dataIndexProdProperties           *properties.Properties
	dataIndexJobServiceDevProperties  *properties.Properties
	dataIndexJobServiceProdProperties *properties.Properties
)

type mockCatalogService struct {
}

func (c *mockCatalogService) Query(ctx context.Context, uri discovery.ResourceUri, outputFormat string) (string, error) {
	if uri.Scheme == discovery.KubernetesScheme && uri.Namespace == namespace1 && uri.Name == myService1 {
		return myService1Address, nil
	}
	if uri.Scheme == discovery.KubernetesScheme && uri.Name == myService2 && uri.Namespace == defaultNamespace {
		return myService2Address, nil
	}
	if uri.Scheme == discovery.KubernetesScheme && uri.Name == myService3 && uri.Namespace == defaultNamespace && uri.GetPort() == "http-port" {
		return myService3Address, nil
	}
	if uri.Scheme == discovery.KnativeScheme && uri.Name == myKnService1 && uri.Namespace == namespace1 {
		return myKnService1Address, nil
	}
	if uri.Scheme == discovery.KnativeScheme && uri.Name == myKnService2 && uri.Namespace == namespace1 {
		return myKnService2Address, nil
	}
	if uri.Scheme == discovery.KnativeScheme && uri.Name == myKnService3 && uri.Namespace == defaultNamespace {
		return myKnService3Address, nil
	}
	if uri.Scheme == discovery.KnativeScheme && uri.Name == myKnBroker1 && uri.Namespace == namespace1 {
		return myKnBroker1Address, nil
	}
	if uri.Scheme == discovery.KnativeScheme && uri.Name == myKnBroker2 && uri.Namespace == defaultNamespace {
		return myKnBroker2Address, nil
	}

	return "", nil
}

func Test_appPropertyHandler_WithKogitoServiceUrl(t *testing.T) {
	workflow := test.GetBaseSonataFlow("default")
	props, err := ApplicationManagedProperties(workflow, nil)
	assert.NoError(t, err)
	assert.Contains(t, props, constants.KogitoServiceURLProperty)
	assert.Contains(t, props, "http://"+workflow.Name+"."+workflow.Namespace)
}

func Test_appPropertyHandler_WithUserPropertiesWithNoUserOverrides(t *testing.T) {
	//just add some user provided properties, no overrides.
	userProperties := "property1=value1\nproperty2=value2"
	workflow := test.GetBaseSonataFlow("default")
	props, err := NewManagedPropertyHandler(workflow, nil)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 7, len(generatedProps.Keys()))
	assert.NotContains(t, "property1", generatedProps.Keys())
	assert.NotContains(t, "property2", generatedProps.Keys())
	assert.Equal(t, "http://greeting.default", generatedProps.GetString("kogito.service.url", ""))
	assert.Equal(t, "8080", generatedProps.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", generatedProps.GetString("quarkus.http.host", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.kogito.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoUserTasksEventsEnabled, ""))
}

func Test_appPropertyHandler_WithUserPropertiesWithServiceDiscovery(t *testing.T) {
	//just add some user provided properties, no overrides.
	userProperties := "property1=value1\nproperty2=value2\n"
	//add some user properties that requires service discovery
	userProperties = userProperties + "service1=${kubernetes:services.v1/namespace1/my-service1}\n"
	userProperties = userProperties + "service2=${kubernetes:services.v1/my-service2}\n"
	userProperties = userProperties + "service3=${knative:namespace1/my-kn-service1}\n"
	userProperties = userProperties + "service4=${knative:services.v1.serving.knative.dev/namespace1/my-kn-service2}\n"
	userProperties = userProperties + "service5=${knative:services.v1.serving.knative.dev/my-kn-service3}\n"
	userProperties = userProperties + "broker1=${knative:brokers.v1.eventing.knative.dev/namespace1/my-kn-broker1}\n"
	userProperties = userProperties + "broker2=${knative:brokers.v1.eventing.knative.dev/my-kn-broker2}\n"

	workflow := test.GetBaseSonataFlow(defaultNamespace)
	props, err := NewManagedPropertyHandler(workflow, nil)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.
		WithUserProperties(userProperties).
		WithServiceDiscovery(context.TODO(), &mockCatalogService{}).
		Build())
	generatedProps.DisableExpansion = true
	assert.NoError(t, propsErr)
	assert.Equal(t, 21, len(generatedProps.Keys()))
	assert.NotContains(t, "property1", generatedProps.Keys())
	assert.NotContains(t, "property2", generatedProps.Keys())
	assertHasProperty(t, generatedProps, "service1", myService1Address)
	assertHasProperty(t, generatedProps, "service2", myService2Address)
	assertHasProperty(t, generatedProps, "service3", myKnService1Address)
	assertHasProperty(t, generatedProps, "service4", myKnService2Address)
	assertHasProperty(t, generatedProps, "service5", myKnService3Address)
	assertHasProperty(t, generatedProps, "broker1", myKnBroker1Address)
	assertHasProperty(t, generatedProps, "broker2", myKnBroker2Address)

	//org.kie.kogito.addons.discovery.kubernetes\:services.v1\/usecase1º/my-service1 below we use the unescaped vale because the properties.LoadString removes them.
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.kubernetes:services.v1/namespace1/my-service1", myService1Address)
	//org.kie.kogito.addons.discovery.kubernetes\:services.v1\/my-service2 below we use the unescaped vale because the properties.LoadString removes them.
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.kubernetes:services.v1/my-service2", myService2Address)
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.knative:namespace1/my-kn-service1", myKnService1Address)
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.knative:services.v1.serving.knative.dev/namespace1/my-kn-service2", myKnService2Address)
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.knative:services.v1.serving.knative.dev/my-kn-service3", myKnService3Address)
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.knative:brokers.v1.eventing.knative.dev/namespace1/my-kn-broker1", myKnBroker1Address)
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.knative:brokers.v1.eventing.knative.dev/my-kn-broker2", myKnBroker2Address)

	assertHasProperty(t, generatedProps, "kogito.service.url", fmt.Sprintf("http://greeting.%s", defaultNamespace))
	assertHasProperty(t, generatedProps, "quarkus.http.port", "8080")
	assertHasProperty(t, generatedProps, "quarkus.http.host", "0.0.0.0")
	assertHasProperty(t, generatedProps, "quarkus.devservices.enabled", "false")
	assertHasProperty(t, generatedProps, "quarkus.kogito.devservices.enabled", "false")
	assertHasProperty(t, generatedProps, constants.KogitoUserTasksEventsEnabled, "false")
}

func Test_appPropertyHandler_WithServicesWithUserOverrides(t *testing.T) {
	//try to override kogito.service.url and quarkus.http.port
	userProperties := "property1=value1\nproperty2=value2\nquarkus.http.port=9090\nkogito.service.url=http://myUrl.override.com\nquarkus.http.port=9090"
	ns := "default"
	workflow := test.GetBaseSonataFlow(ns)
	workflow.SetAnnotations(map[string]string{metadata.Profile: string(metadata.DevProfile)})
	enabled := true
	platform := test.GetBasePlatform()
	platform.Namespace = ns
	platform.Spec = operatorapi.SonataFlowPlatformSpec{
		Services: &operatorapi.ServicesPlatformSpec{
			DataIndex: &operatorapi.ServiceSpec{
				Enabled: &enabled,
			},
			JobService: &operatorapi.ServiceSpec{
				Enabled: &enabled,
			},
		},
	}

	services.SetServiceUrlsInWorkflowStatus(platform, workflow)
	assert.Nil(t, workflow.Status.Services)
	props, err := NewManagedPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 11, len(generatedProps.Keys()))
	assert.NotContains(t, "property1", generatedProps.Keys())
	assert.NotContains(t, "property2", generatedProps.Keys())

	//kogito.service.url is a default immutable property.
	assert.Equal(t, "http://greeting.default", generatedProps.GetString("kogito.service.url", ""))
	//quarkus.http.port remains with the default value since it's immutable.
	assert.Equal(t, "8080", generatedProps.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", generatedProps.GetString("quarkus.http.host", ""))
	assert.Equal(t, "false", generatedProps.GetString("org.kie.kogito.addons.knative.eventing.health-enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.kogito.devservices.enabled", ""))
	assert.Equal(t, "http://localhost/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsURL, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsEnabled, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.KogitoProcessInstancesEventsURL, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessInstancesEventsEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoUserTasksEventsEnabled, ""))

	// prod profile enables config of outgoing events url
	workflow.SetAnnotations(map[string]string{metadata.Profile: string(metadata.PreviewProfile)})
	services.SetServiceUrlsInWorkflowStatus(platform, workflow)
	assert.NotNil(t, workflow.Status.Services)
	assert.NotNil(t, workflow.Status.Services.JobServiceRef)
	assert.NotNil(t, workflow.Status.Services.DataIndexRef)
	props, err = NewManagedPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 17, len(generatedProps.Keys()))
	assert.NotContains(t, "property1", generatedProps.Keys())
	assert.NotContains(t, "property2", generatedProps.Keys())
	assert.Equal(t, "http://"+platform.Name+"-"+constants.DataIndexServiceName+"."+platform.Namespace+"/definitions", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsURL, ""))
	assert.Equal(t, "true", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsEnabled, ""))
	assert.Equal(t, "true", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsErrorsEnabled, ""))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.DataIndexServiceName+"."+platform.Namespace+"/processes", generatedProps.GetString(constants.KogitoProcessInstancesEventsURL, ""))
	assert.Equal(t, "true", generatedProps.GetString(constants.KogitoProcessInstancesEventsEnabled, ""))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.JobServiceName+"."+platform.Namespace+"/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoUserTasksEventsEnabled, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceDataSourceReactiveURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEvents, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEventsURL, ""))
	assert.Equal(t, "true", generatedProps.GetString(constants.KogitoDataIndexHealthCheckEnabled, ""))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.DataIndexServiceName+"."+platform.Namespace, generatedProps.GetString(constants.KogitoDataIndexURL, ""))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.JobServiceName+"."+platform.Namespace, generatedProps.GetString(constants.KogitoJobServiceURL, ""))

	// disabling data index bypasses config of outgoing events url
	platform.Spec.Services.DataIndex.Enabled = nil
	services.SetServiceUrlsInWorkflowStatus(platform, workflow)
	assert.NotNil(t, workflow.Status.Services)
	assert.NotNil(t, workflow.Status.Services.JobServiceRef)
	assert.Nil(t, workflow.Status.Services.DataIndexRef)
	props, err = NewManagedPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 12, len(generatedProps.Keys()))
	assert.NotContains(t, "property1", generatedProps.Keys())
	assert.NotContains(t, "property2", generatedProps.Keys())
	assert.Equal(t, "", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsURL, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsEnabled, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.KogitoProcessInstancesEventsURL, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessInstancesEventsEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoUserTasksEventsEnabled, ""))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.JobServiceName+"."+platform.Namespace+"/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEvents, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEventsURL, ""))

	// disabling job service bypasses config of outgoing events url
	platform.Spec.Services.JobService.Enabled = nil
	services.SetServiceUrlsInWorkflowStatus(platform, workflow)
	assert.Nil(t, workflow.Status.Services)
	props, err = NewManagedPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 11, len(generatedProps.Keys()))
	assert.NotContains(t, "property1", generatedProps.Keys())
	assert.NotContains(t, "property2", generatedProps.Keys())
	assert.Equal(t, "", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsURL, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsEnabled, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.KogitoProcessInstancesEventsURL, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessInstancesEventsEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoUserTasksEventsEnabled, ""))
	assert.Equal(t, "http://localhost/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceDataSourceReactiveURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEvents, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEventsURL, ""))
}

var _ = Describe("Platform properties", func() {

	var _ = Context("for workflow properties", func() {

		var _ = Context("defining the workflow properties generated from", func() {

			DescribeTable("only job services when the spec",
				func(wf *operatorapi.SonataFlow, plfm *operatorapi.SonataFlowPlatform, expectedProperties *properties.Properties) {
					services.SetServiceUrlsInWorkflowStatus(plfm, wf)
					handler, err := NewManagedPropertyHandler(wf, plfm)
					Expect(err).NotTo(HaveOccurred())
					p, err := properties.LoadString(handler.Build())
					Expect(err).NotTo(HaveOccurred())
					p.Sort()
					Expect(p).To(Equal(expectedProperties))
				},
				Entry("has enabled field set to false and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field set to false and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceWorkflowProductionProperties()),
				Entry("has enabled field set to true and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceWorkflowProductionProperties()),
				Entry("has enabled field set to false and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceWorkflowDevProperties()),
			)

			DescribeTable("only data index service when the spec",
				func(wf *operatorapi.SonataFlow, plfm *operatorapi.SonataFlowPlatform, expectedProperties *properties.Properties) {
					services.SetServiceUrlsInWorkflowStatus(plfm, wf)
					handler, err := NewManagedPropertyHandler(wf, plfm)
					Expect(err).NotTo(HaveOccurred())
					p, err := properties.LoadString(handler.Build())
					Expect(err).NotTo(HaveOccurred())
					p.Sort()
					Expect(p).To(Equal(expectedProperties))
				},
				Entry("has enabled field set to false and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&disabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field set to false and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&disabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&enabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&enabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowProductionProperties()),
				Entry("has enabled field set to false and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&disabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&enabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowProductionProperties()),
				Entry("has enabled field undefined and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
			)

			DescribeTable("both Data Index and Job Services are available and", func(wf *operatorapi.SonataFlow, plfm *operatorapi.SonataFlowPlatform, expectedProperties *properties.Properties) {
				services.SetServiceUrlsInWorkflowStatus(plfm, wf)
				handler, err := NewManagedPropertyHandler(wf, plfm)
				Expect(err).NotTo(HaveOccurred())
				p, err := properties.LoadString(handler.Build())
				Expect(err).NotTo(HaveOccurred())
				p.Sort()
				Expect(p).To(Equal(expectedProperties))
			},
				Entry("both are undefined and workflow in dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both are undefined and workflow in prod profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to true and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to true and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateDataIndexAndJobServiceWorkflowProductionProperties()),
				Entry("both have enabled field undefined and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setDataIndexEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field undefined and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setDataIndexEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to false and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to false and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to false and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to true and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowProductionProperties()),
				Entry("both have enabled field undefined and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
			)
		})
	})

})

func generateJobServiceWorkflowDevProperties() *properties.Properties {
	if jobServiceDevProperties == nil {
		jobServiceDevProperties = properties.NewProperties()
		jobServiceDevProperties.Set("kogito.service.url", "http://foo.default")
		jobServiceDevProperties.Set("quarkus.http.host", "0.0.0.0")
		jobServiceDevProperties.Set("quarkus.http.port", "8080")
		jobServiceDevProperties.Set("quarkus.devservices.enabled", "false")
		jobServiceDevProperties.Set("quarkus.kogito.devservices.enabled", "false")
		jobServiceDevProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		jobServiceDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
		jobServiceDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		jobServiceDevProperties.Set("kogito.events.processdefinitions.enabled", "false")
		jobServiceDevProperties.Set("kogito.events.processinstances.enabled", "false")
		jobServiceDevProperties.Set("kogito.events.usertasks.enabled", "false")
		jobServiceDevProperties.Sort()
	}
	return jobServiceDevProperties
}

func generateJobServiceWorkflowProductionProperties() *properties.Properties {
	if jobServiceProdProperties == nil {
		jobServiceProdProperties = properties.NewProperties()
		jobServiceProdProperties.Set("kogito.service.url", "http://foo.default")
		jobServiceProdProperties.Set("kogito.jobs-service.url", "http://foo-jobs-service.default")
		jobServiceProdProperties.Set("quarkus.http.host", "0.0.0.0")
		jobServiceProdProperties.Set("quarkus.http.port", "8080")
		jobServiceProdProperties.Set("quarkus.kogito.devservices.enabled", "false")
		jobServiceProdProperties.Set("quarkus.devservices.enabled", "false")
		jobServiceProdProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		jobServiceProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		jobServiceProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://foo-jobs-service.default/v2/jobs/events")
		jobServiceProdProperties.Set("kogito.events.processdefinitions.enabled", "false")
		jobServiceProdProperties.Set("kogito.events.processinstances.enabled", "false")
		jobServiceProdProperties.Set("kogito.events.usertasks.enabled", "false")
		jobServiceProdProperties.Sort()
	}
	return jobServiceProdProperties
}

func generateDataIndexWorkflowDevProperties() *properties.Properties {
	if dataIndexDevProperties == nil {
		dataIndexDevProperties = properties.NewProperties()
		dataIndexDevProperties.Set("kogito.service.url", "http://foo.default")
		dataIndexDevProperties.Set("quarkus.http.host", "0.0.0.0")
		dataIndexDevProperties.Set("quarkus.http.port", "8080")
		dataIndexDevProperties.Set("quarkus.devservices.enabled", "false")
		dataIndexDevProperties.Set("quarkus.kogito.devservices.enabled", "false")
		dataIndexDevProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		dataIndexDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		dataIndexDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
		dataIndexDevProperties.Set("kogito.events.processdefinitions.enabled", "false")
		dataIndexDevProperties.Set("kogito.events.processinstances.enabled", "false")
		dataIndexDevProperties.Set("kogito.events.usertasks.enabled", "false")
		dataIndexDevProperties.Sort()
	}
	return dataIndexDevProperties
}

func generateDataIndexWorkflowProductionProperties() *properties.Properties {
	if dataIndexProdProperties == nil {
		dataIndexProdProperties = properties.NewProperties()
		dataIndexProdProperties.Set("kogito.service.url", "http://foo.default")
		dataIndexProdProperties.Set("kogito.data-index.url", "http://foo-data-index-service.default")
		dataIndexProdProperties.Set("kogito.data-index.health-enabled", "true")
		dataIndexProdProperties.Set("quarkus.http.host", "0.0.0.0")
		dataIndexProdProperties.Set("quarkus.http.port", "8080")
		dataIndexProdProperties.Set("quarkus.devservices.enabled", "false")
		dataIndexProdProperties.Set("quarkus.kogito.devservices.enabled", "false")
		dataIndexProdProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		dataIndexProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		dataIndexProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
		dataIndexProdProperties.Set("mp.messaging.outgoing.kogito-processdefinitions-events.url", "http://foo-data-index-service.default/definitions")
		dataIndexProdProperties.Set("mp.messaging.outgoing.kogito-processinstances-events.url", "http://foo-data-index-service.default/processes")
		dataIndexProdProperties.Set("kogito.events.processdefinitions.enabled", "true")
		dataIndexProdProperties.Set("kogito.events.processdefinitions.errors.propagate", "true")
		dataIndexProdProperties.Set("kogito.events.processinstances.enabled", "true")
		dataIndexProdProperties.Set("kogito.events.usertasks.enabled", "false")
		dataIndexProdProperties.Sort()
	}
	return dataIndexProdProperties
}

func generateDataIndexAndJobServiceWorkflowDevProperties() *properties.Properties {
	if dataIndexJobServiceDevProperties == nil {
		dataIndexJobServiceDevProperties = properties.NewProperties()
		dataIndexJobServiceDevProperties.Set("kogito.service.url", "http://foo.default")
		dataIndexJobServiceDevProperties.Set("quarkus.http.host", "0.0.0.0")
		dataIndexJobServiceDevProperties.Set("quarkus.http.port", "8080")
		dataIndexJobServiceDevProperties.Set("quarkus.kogito.devservices.enabled", "false")
		dataIndexJobServiceDevProperties.Set("quarkus.devservices.enabled", "false")
		dataIndexJobServiceDevProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		dataIndexJobServiceDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		dataIndexJobServiceDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
		dataIndexJobServiceDevProperties.Set("kogito.events.processdefinitions.enabled", "false")
		dataIndexJobServiceDevProperties.Set("kogito.events.processinstances.enabled", "false")
		dataIndexJobServiceDevProperties.Set("kogito.events.usertasks.enabled", "false")
		dataIndexJobServiceDevProperties.Sort()
	}
	return dataIndexJobServiceDevProperties
}

func generateDataIndexAndJobServiceWorkflowProductionProperties() *properties.Properties {
	if dataIndexJobServiceProdProperties == nil {
		dataIndexJobServiceProdProperties = properties.NewProperties()
		dataIndexJobServiceProdProperties.Set("kogito.service.url", "http://foo.default")
		dataIndexJobServiceProdProperties.Set("kogito.data-index.url", "http://foo-data-index-service.default")
		dataIndexJobServiceProdProperties.Set("kogito.data-index.health-enabled", "true")
		dataIndexJobServiceProdProperties.Set("kogito.jobs-service.url", "http://foo-jobs-service.default")
		dataIndexJobServiceProdProperties.Set("quarkus.http.host", "0.0.0.0")
		dataIndexJobServiceProdProperties.Set("quarkus.http.port", "8080")
		dataIndexJobServiceProdProperties.Set("quarkus.kogito.devservices.enabled", "false")
		dataIndexJobServiceProdProperties.Set("quarkus.devservices.enabled", "false")
		dataIndexJobServiceProdProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		dataIndexJobServiceProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		dataIndexJobServiceProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://foo-jobs-service.default/v2/jobs/events")
		dataIndexJobServiceProdProperties.Set("kogito.events.processdefinitions.enabled", "true")
		dataIndexJobServiceProdProperties.Set("kogito.events.processdefinitions.errors.propagate", "true")
		dataIndexJobServiceProdProperties.Set("kogito.events.processinstances.enabled", "true")
		dataIndexJobServiceProdProperties.Set("kogito.events.usertasks.enabled", "false")
		dataIndexJobServiceProdProperties.Set("mp.messaging.outgoing.kogito-processdefinitions-events.url", "http://foo-data-index-service.default/definitions")
		dataIndexJobServiceProdProperties.Set("mp.messaging.outgoing.kogito-processinstances-events.url", "http://foo-data-index-service.default/processes")
		dataIndexJobServiceProdProperties.Sort()
	}
	return dataIndexJobServiceProdProperties
}

type wfOptionFn func(wf *operatorapi.SonataFlow)

func generateFlow(opts ...wfOptionFn) *operatorapi.SonataFlow {
	wf := &operatorapi.SonataFlow{}
	for _, f := range opts {
		f(wf)
	}
	return wf
}

func setProfileInFlow(p metadata.ProfileType) wfOptionFn {
	return func(wf *operatorapi.SonataFlow) {
		if wf.Annotations == nil {
			wf.Annotations = make(map[string]string)
		}
		wf.Annotations[metadata.Profile] = p.String()
	}
}

func setWorkflowName(n string) wfOptionFn {
	return func(wf *operatorapi.SonataFlow) {
		wf.Name = n
	}
}

func setWorkflowNamespace(ns string) wfOptionFn {
	return func(wf *operatorapi.SonataFlow) {
		wf.Namespace = ns
	}
}

type plfmOptionFn func(p *operatorapi.SonataFlowPlatform)

func generatePlatform(opts ...plfmOptionFn) *operatorapi.SonataFlowPlatform {
	plfm := &operatorapi.SonataFlowPlatform{}
	for _, f := range opts {
		f(plfm)
	}
	return plfm
}

func setJobServiceEnabledValue(v *bool) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.JobService == nil {
			p.Spec.Services.JobService = &operatorapi.ServiceSpec{}
		}
		p.Spec.Services.JobService.Enabled = v
	}
}

func setDataIndexEnabledValue(v *bool) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.DataIndex == nil {
			p.Spec.Services.DataIndex = &operatorapi.ServiceSpec{}
		}
		p.Spec.Services.DataIndex.Enabled = v
	}
}

func setPlatformNamespace(namespace string) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		p.Namespace = namespace
	}
}

func setPlatformName(name string) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		p.Name = name
	}
}

func setJobServiceJDBC(jdbc string) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.JobService == nil {
			p.Spec.Services.JobService = &operatorapi.ServiceSpec{}
		}
		if p.Spec.Services.JobService.Persistence == nil {
			p.Spec.Services.JobService.Persistence = &operatorapi.PersistenceOptionsSpec{}
		}
		if p.Spec.Services.JobService.Persistence.PostgreSQL == nil {
			p.Spec.Services.JobService.Persistence.PostgreSQL = &operatorapi.PersistencePostgreSQL{}
		}
		p.Spec.Services.JobService.Persistence.PostgreSQL.JdbcUrl = jdbc
	}
}
