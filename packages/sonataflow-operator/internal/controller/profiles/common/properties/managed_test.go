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

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"

	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/discovery"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"

	"github.com/magiconair/properties"

	"github.com/stretchr/testify/assert"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
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
	enabled  = true
	disabled = false
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
	platform := test.GetBasePlatform()
	props, err := ApplicationManagedProperties(workflow, platform)
	assert.NoError(t, err)
	assert.Contains(t, props, constants.KogitoServiceURLProperty)
	assert.Contains(t, props, "http://"+workflow.Name+"."+workflow.Namespace)
}

func Test_appPropertyHandler_WithUserPropertiesWithNoUserOverrides(t *testing.T) {
	//just add some user provided properties, no overrides.
	userProperties := "property1=value1\nproperty2=value2"
	workflow := test.GetBaseSonataFlow("default")
	platform := test.GetBasePlatform()
	props, err := NewManagedPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 14, len(generatedProps.Keys()))
	assert.NotContains(t, "property1", generatedProps.Keys())
	assert.NotContains(t, "property2", generatedProps.Keys())
	assert.Equal(t, "http://greeting.default", generatedProps.GetString("kogito.service.url", ""))
	assert.Equal(t, "8080", generatedProps.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", generatedProps.GetString("quarkus.http.host", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.kogito.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoUserTasksEventsEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessDefinitionsEventsEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessInstancesEventsEnabled, ""))
	assert.Equal(t, "quarkus-http", generatedProps.GetString("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", ""))
	assert.Equal(t, "http://localhost/v2/jobs/events", generatedProps.GetString("mp.messaging.outgoing.kogito-job-service-job-request-events.url", ""))
	assert.Equal(t, "false", generatedProps.GetString("org.kie.kogito.addons.knative.eventing.health-enabled", ""))
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
	platform := test.GetBasePlatform()
	props, err := NewManagedPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.
		WithUserProperties(userProperties).
		WithServiceDiscovery(context.TODO(), &mockCatalogService{}).
		Build())
	generatedProps.DisableExpansion = true
	assert.NoError(t, propsErr)
	assert.Equal(t, 28, len(generatedProps.Keys()))
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

	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	assertHasProperty(t, generatedProps, "kogito.events.processdefinitions.enabled", "false")
	assertHasProperty(t, generatedProps, "kogito.events.processinstances.enabled", "false")
	assertHasProperty(t, generatedProps, "kogito.events.usertasks.enabled", "false")
	assertHasProperty(t, generatedProps, "mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	assertHasProperty(t, generatedProps, "mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
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
			DataIndex: &operatorapi.DataIndexServiceSpec{
				ServiceSpec: operatorapi.ServiceSpec{
					Enabled: &enabled,
				},
			},
			JobService: &operatorapi.JobServiceServiceSpec{
				ServiceSpec: operatorapi.ServiceSpec{
					Enabled: &enabled,
				},
			},
		},
	}

	services.SetServiceUrlsInWorkflowStatus(platform, workflow)
	assert.Nil(t, workflow.Status.Services)
	props, err := NewManagedPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 12, len(generatedProps.Keys()))
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
	assert.Equal(t, 19, len(generatedProps.Keys()))
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
	assert.Equal(t, 14, len(generatedProps.Keys()))
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
	assert.Equal(t, 13, len(generatedProps.Keys()))
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

type eventsGroupingTestSpec struct {
	kogitoEventsGrouping                bool
	kogitoEventsGroupingBinary          bool
	kogitoEventsGroupingCompress        bool
	shouldContainEventsGrouping         bool
	shouldContainEventsGroupingBinary   bool
	shouldContainEventsGroupingCompress bool
}

func newTestSpec(eventsGrouping bool, eventsGroupingBinary bool, eventsGroupingCompress bool,
	shouldContainEventsGrouping bool, shouldContainEventsGroupingBinary bool, shouldContainEventsGroupingCompress bool) *eventsGroupingTestSpec {
	return &eventsGroupingTestSpec{
		kogitoEventsGrouping:                eventsGrouping,
		kogitoEventsGroupingBinary:          eventsGroupingBinary,
		kogitoEventsGroupingCompress:        eventsGroupingCompress,
		shouldContainEventsGrouping:         shouldContainEventsGrouping,
		shouldContainEventsGroupingBinary:   shouldContainEventsGroupingBinary,
		shouldContainEventsGroupingCompress: shouldContainEventsGroupingCompress,
	}
}
func Test_appPropertyHandler_KogitoEventsGroupingTrueWithDevProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.DevProfile, newTestSpec(false, false, false, false, false, false))
}

func Test_appPropertyHandler_KogitoEventsGroupingTrueWithPreviewProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.PreviewProfile, newTestSpec(true, false, false, true, false, false))
}

func Test_appPropertyHandler_KogitoEventsGroupingTrueWithGitOpsProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.GitOpsProfile, newTestSpec(true, false, false, true, false, false))
}

func Test_appPropertyHandler_KogitoEventsGroupingFalseWithDevProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.DevProfile, newTestSpec(false, false, false, false, false, false))
}

func Test_appPropertyHandler_KogitoEventsGroupingFalseWithPreviewProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.PreviewProfile, newTestSpec(false, false, false, false, false, false))
}

func Test_appPropertyHandler_KogitoEventsGroupingTrueBinaryTrueCompressFalseWithPreviewProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.PreviewProfile, newTestSpec(true, true, false, true, true, false))
}

func Test_appPropertyHandler_KogitoEventsGroupingTrueBinaryTrueCompressTrueWithPreviewProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.PreviewProfile, newTestSpec(true, true, true, true, true, true))
}

func Test_appPropertyHandler_KogitoEventsGroupingFalseWithGitOpsProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.GitOpsProfile, newTestSpec(false, false, false, false, false, false))
}

func Test_appPropertyHandler_KogitoEventsGroupingTrueBinaryTrueCompressFalseWithGitOpsProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.GitOpsProfile, newTestSpec(true, true, false, true, true, false))
}

func Test_appPropertyHandler_KogitoEventsGroupingTrueBinaryTrueCompressTrueWithGitOpsProfile(t *testing.T) {
	doTestManagedPropsForKogitoEventsGrouping(t, metadata.GitOpsProfile, newTestSpec(true, true, true, true, true, true))
}

func doTestManagedPropsForKogitoEventsGrouping(t *testing.T, profile metadata.ProfileType, testSpec *eventsGroupingTestSpec) {
	currentKogitoEventGroupingValue := cfg.GetCfg().KogitoEventsGrouping
	currentKogitoEventGroupingBinaryValue := cfg.GetCfg().KogitoEventsGroupingBinary
	currentKogitoEventGroupingCompressValue := cfg.GetCfg().KogitoEventsGroupingCompress
	cfg.GetCfg().KogitoEventsGrouping = testSpec.kogitoEventsGrouping
	cfg.GetCfg().KogitoEventsGroupingBinary = testSpec.kogitoEventsGroupingBinary
	cfg.GetCfg().KogitoEventsGroupingCompress = testSpec.kogitoEventsGroupingCompress
	workflow := test.GetBaseSonataFlow("default")
	setProfileInFlow(profile)(workflow)
	platform := test.GetBasePlatform()
	handler, err := NewManagedPropertyHandler(workflow, platform)
	cfg.GetCfg().KogitoEventsGrouping = currentKogitoEventGroupingValue
	cfg.GetCfg().KogitoEventsGroupingBinary = currentKogitoEventGroupingBinaryValue
	cfg.GetCfg().KogitoEventsGroupingCompress = currentKogitoEventGroupingCompressValue
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(handler.Build())
	assert.NoError(t, propsErr)
	if testSpec.shouldContainEventsGrouping {
		assertHasProperty(t, generatedProps, "kogito.events.grouping", "true")
	} else {
		assert.NotContains(t, generatedProps.Keys(), "kogito.events.grouping")
	}
	if testSpec.shouldContainEventsGroupingBinary {
		assertHasProperty(t, generatedProps, "kogito.events.grouping.binary", "true")
	} else {
		assert.NotContains(t, generatedProps.Keys(), "kogito.events.grouping.binary", "true")
	}
	if testSpec.shouldContainEventsGroupingCompress {
		assertHasProperty(t, generatedProps, "kogito.events.grouping.compress", "true")
	} else {
		assert.NotContains(t, generatedProps.Keys(), "kogito.events.grouping.compress", "true")
	}
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
					generateJobServiceWorkflowProductionWithJobServiceDisabled()),
				Entry("has enabled field undefined and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowProductionWithJobServiceDisabled()),
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
					generateJobServiceWorkflowProductionWithJobServiceDisabled()),
				Entry("has enabled field undefined and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceWorkflowProductionWithJobServiceDisabled()),
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
					generateDataIndexWorkflowProductionPropertiesWithDataIndexDisabled()),
				Entry("has enabled field undefined and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowProductionPropertiesWithDataIndexDisabled()),
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
					generateDataIndexWorkflowProductionPropertiesWithDataIndexDisabled()),
				Entry("has enabled field set to true and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&enabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowProductionProperties()),
				Entry("has enabled field undefined and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowProductionPropertiesWithDataIndexDisabled()),
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
					generateDataIndexAndJobServiceWorkflowProductionDataIndexAndJobsServiceDisabled()),
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
					generateDataIndexAndJobServiceWorkflowProductionDataIndexAndJobsServiceDisabled()),
				Entry("both have enabled field set to false and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to false and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.PreviewProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowProductionDataIndexAndJobsServiceDisabled()),
				Entry("both have enabled field set to false and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowProductionDataIndexAndJobsServiceDisabled()),
				Entry("both have enabled field set to true and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowProductionProperties()),
				Entry("both have enabled field undefined and workflow with no profile",
					generateFlow(setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowProductionDataIndexAndJobsServiceDisabled()),
			)
		})
	})

})

func generateJobServiceWorkflowDevProperties() *properties.Properties {
	props := properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("kogito.events.processdefinitions.enabled", "false")
	props.Set("kogito.events.processinstances.enabled", "false")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("quarkus.dev-ui.cors.enabled", "false")
	props.Sort()
	return props
}

func generateJobServiceWorkflowProductionWithJobServiceDisabled() *properties.Properties {
	props := properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("kogito.events.processdefinitions.enabled", "false")
	props.Set("kogito.events.processinstances.enabled", "false")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("kogito.events.grouping", "true")
	props.Set("kogito.events.grouping.binary", "true")
	props.Sort()
	return props
}

func generateJobServiceWorkflowProductionProperties() *properties.Properties {
	props := properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("kogito.jobs-service.url", "http://foo-jobs-service.default")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://foo-jobs-service.default/v2/jobs/events")
	props.Set("kogito.events.processdefinitions.enabled", "false")
	props.Set("kogito.events.processinstances.enabled", "false")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("kogito.events.grouping", "true")
	props.Set("kogito.events.grouping.binary", "true")
	props.Sort()
	return props
}

func generateDataIndexWorkflowDevProperties() *properties.Properties {
	props := properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
	props.Set("kogito.events.processdefinitions.enabled", "false")
	props.Set("kogito.events.processinstances.enabled", "false")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("quarkus.dev-ui.cors.enabled", "false")
	props.Sort()
	return props
}

func generateDataIndexWorkflowProductionPropertiesWithDataIndexDisabled() *properties.Properties {
	props := properties.NewProperties()
	props = properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
	props.Set("kogito.events.processdefinitions.enabled", "false")
	props.Set("kogito.events.processinstances.enabled", "false")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("kogito.events.grouping", "true")
	props.Set("kogito.events.grouping.binary", "true")
	props.Sort()
	return props
}

func generateDataIndexWorkflowProductionProperties() *properties.Properties {
	props := properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("kogito.data-index.url", "http://foo-data-index-service.default")
	props.Set("kogito.data-index.health-enabled", "true")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
	props.Set("mp.messaging.outgoing.kogito-processdefinitions-events.url", "http://foo-data-index-service.default/definitions")
	props.Set("mp.messaging.outgoing.kogito-processinstances-events.url", "http://foo-data-index-service.default/processes")
	props.Set("kogito.events.processdefinitions.enabled", "true")
	props.Set("kogito.events.processdefinitions.errors.propagate", "true")
	props.Set("kogito.events.processinstances.enabled", "true")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("kogito.events.grouping", "true")
	props.Set("kogito.events.grouping.binary", "true")
	props.Sort()
	return props
}

func generateDataIndexAndJobServiceWorkflowDevProperties() *properties.Properties {
	props := properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
	props.Set("kogito.events.processdefinitions.enabled", "false")
	props.Set("kogito.events.processinstances.enabled", "false")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("quarkus.dev-ui.cors.enabled", "false")
	props.Sort()
	return props
}

func generateDataIndexAndJobServiceWorkflowProductionDataIndexAndJobsServiceDisabled() *properties.Properties {
	props := properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
	props.Set("kogito.events.processdefinitions.enabled", "false")
	props.Set("kogito.events.processinstances.enabled", "false")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("kogito.events.grouping", "true")
	props.Set("kogito.events.grouping.binary", "true")
	props.Sort()
	return props
}

func generateDataIndexAndJobServiceWorkflowProductionProperties() *properties.Properties {
	props := properties.NewProperties()
	props.Set("kogito.service.url", "http://foo.default")
	props.Set("kogito.data-index.url", "http://foo-data-index-service.default")
	props.Set("kogito.data-index.health-enabled", "true")
	props.Set("kogito.jobs-service.url", "http://foo-jobs-service.default")
	props.Set("quarkus.http.host", "0.0.0.0")
	props.Set("quarkus.http.port", "8080")
	props.Set("quarkus.kogito.devservices.enabled", "false")
	props.Set("quarkus.devservices.enabled", "false")
	props.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
	props.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://foo-jobs-service.default/v2/jobs/events")
	props.Set("kogito.events.processdefinitions.enabled", "true")
	props.Set("kogito.events.processdefinitions.errors.propagate", "true")
	props.Set("kogito.events.processinstances.enabled", "true")
	props.Set("kogito.events.usertasks.enabled", "false")
	props.Set("mp.messaging.outgoing.kogito-processdefinitions-events.url", "http://foo-data-index-service.default/definitions")
	props.Set("mp.messaging.outgoing.kogito-processinstances-events.url", "http://foo-data-index-service.default/processes")
	props.Set("kogito.events.grouping", "true")
	props.Set("kogito.events.grouping.binary", "true")
	props.Sort()
	return props

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
			p.Spec.Services.JobService = &operatorapi.JobServiceServiceSpec{}
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
			p.Spec.Services.DataIndex = &operatorapi.DataIndexServiceSpec{}
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
			p.Spec.Services.JobService = &operatorapi.JobServiceServiceSpec{}
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
