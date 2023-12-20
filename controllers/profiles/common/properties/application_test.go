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

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/discovery"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform/services"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/constants"

	"github.com/magiconair/properties"

	"github.com/stretchr/testify/assert"

	"github.com/apache/incubator-kie-kogito-serverless-operator/test"
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
	return "", nil
}

func Test_appPropertyHandler_WithKogitoServiceUrl(t *testing.T) {
	workflow := test.GetBaseSonataFlow("default")
	props, err := ImmutableApplicationProperties(workflow, nil)
	assert.NoError(t, err)
	assert.Contains(t, props, constants.KogitoServiceURLProperty)
	assert.Contains(t, props, "http://"+workflow.Name+"."+workflow.Namespace)
}

const (
	defaultNS = "default"
)

func Test_appPropertyHandler_WithUserPropertiesWithNoUserOverrides(t *testing.T) {
	//just add some user provided properties, no overrides.
	userProperties := "property1=value1\nproperty2=value2"
	workflow := test.GetBaseSonataFlow("default")
	props, err := NewAppPropertyHandler(workflow, nil)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 11, len(generatedProps.Keys()))
	assert.Equal(t, "value1", generatedProps.GetString("property1", ""))
	assert.Equal(t, "value2", generatedProps.GetString("property2", ""))
	assert.Equal(t, "http://greeting.default", generatedProps.GetString("kogito.service.url", ""))
	assert.Equal(t, "8080", generatedProps.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", generatedProps.GetString("quarkus.http.host", ""))
	assert.Equal(t, "false", generatedProps.GetString("org.kie.kogito.addons.knative.eventing.health-enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString("quarkus.kogito.devservices.enabled", ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessDefinitionsEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoEventsUserTaskEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoEventsVariablesEnabled, ""))
}

func Test_appPropertyHandler_WithUserPropertiesWithServiceDiscovery(t *testing.T) {
	//just add some user provided properties, no overrides.
	userProperties := "property1=value1\nproperty2=value2\n"
	//add some user properties that requires service discovery
	userProperties = userProperties + "service1=${kubernetes:services.v1/namespace1/my-service1}\n"
	userProperties = userProperties + "service2=${kubernetes:services.v1/my-service2}\n"

	workflow := test.GetBaseSonataFlow(defaultNamespace)
	props, err := NewAppPropertyHandler(workflow, nil)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.
		WithUserProperties(userProperties).
		WithServiceDiscovery(context.TODO(), &mockCatalogService{}).
		Build())
	generatedProps.DisableExpansion = true
	assert.NoError(t, propsErr)
	assert.Equal(t, 15, len(generatedProps.Keys()))
	assertHasProperty(t, generatedProps, "property1", "value1")
	assertHasProperty(t, generatedProps, "property2", "value2")

	assertHasProperty(t, generatedProps, "service1", "${kubernetes:services.v1/namespace1/my-service1}")
	assertHasProperty(t, generatedProps, "service2", "${kubernetes:services.v1/my-service2}")
	//org.kie.kogito.addons.discovery.kubernetes\:services.v1\/usecase1º/my-service1 below we use the unescaped vale because the properties.LoadString removes them.
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.kubernetes:services.v1/namespace1/my-service1", myService1Address)
	//org.kie.kogito.addons.discovery.kubernetes\:services.v1\/my-service2 below we use the unescaped vale because the properties.LoadString removes them.
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.discovery.kubernetes:services.v1/my-service2", myService2Address)

	assertHasProperty(t, generatedProps, "kogito.service.url", fmt.Sprintf("http://greeting.%s", defaultNamespace))
	assertHasProperty(t, generatedProps, "quarkus.http.port", "8080")
	assertHasProperty(t, generatedProps, "quarkus.http.host", "0.0.0.0")
	assertHasProperty(t, generatedProps, "org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	assertHasProperty(t, generatedProps, "quarkus.devservices.enabled", "false")
	assertHasProperty(t, generatedProps, "quarkus.kogito.devservices.enabled", "false")
	assertHasProperty(t, generatedProps, constants.KogitoProcessDefinitionsEnabled, "false")
	assertHasProperty(t, generatedProps, constants.KogitoEventsUserTaskEnabled, "false")
	assertHasProperty(t, generatedProps, constants.KogitoEventsVariablesEnabled, "false")
}

func Test_generateDiscoveryProperties(t *testing.T) {

	catalogService := &mockCatalogService{}

	propertiesContent := "property1=value1\n"
	propertiesContent = propertiesContent + "property2=${value2}\n"
	propertiesContent = propertiesContent + "service1=${kubernetes:services.v1/namespace1/my-service1}\n"
	propertiesContent = propertiesContent + "service2=${kubernetes:services.v1/my-service2}\n"
	propertiesContent = propertiesContent + "service3=${kubernetes:services.v1/my-service3?port=http-port}\n"

	propertiesContent = propertiesContent + "non_service4=${kubernetes:--kaka}"

	props := properties.MustLoadString(propertiesContent)
	result := generateDiscoveryProperties(context.TODO(), catalogService, props, &operatorapi.SonataFlow{
		ObjectMeta: metav1.ObjectMeta{Name: "helloworld", Namespace: defaultNamespace},
	})

	assert.Equal(t, result.Len(), 3)
	assertHasProperty(t, result, "org.kie.kogito.addons.discovery.kubernetes\\:services.v1\\/namespace1\\/my-service1", myService1Address)
	assertHasProperty(t, result, "org.kie.kogito.addons.discovery.kubernetes\\:services.v1\\/my-service2", myService2Address)
	assertHasProperty(t, result, "org.kie.kogito.addons.discovery.kubernetes\\:services.v1\\/my-service3?port\\=http-port", myService3Address)
}

func assertHasProperty(t *testing.T, props *properties.Properties, expectedProperty string, expectedValue string) {
	value, ok := props.Get(expectedProperty)
	assert.True(t, ok, "Property %s, is not present as expected.", expectedProperty)
	assert.Equal(t, expectedValue, value, "Expected value for property: %s, is: %s but current value is: %s", expectedProperty, expectedValue, value)
}

func Test_generateMicroprofileServiceCatalogProperty(t *testing.T) {

	doTestGenerateMicroprofileServiceCatalogProperty(t, "kubernetes:services.v1/namespace1/financial-service",
		"org.kie.kogito.addons.discovery.kubernetes\\:services.v1\\/namespace1\\/financial-service")

	doTestGenerateMicroprofileServiceCatalogProperty(t, "kubernetes:services.v1/financial-service",
		"org.kie.kogito.addons.discovery.kubernetes\\:services.v1\\/financial-service")

	doTestGenerateMicroprofileServiceCatalogProperty(t, "kubernetes:pods.v1/namespace1/financial-service",
		"org.kie.kogito.addons.discovery.kubernetes\\:pods.v1\\/namespace1\\/financial-service")

	doTestGenerateMicroprofileServiceCatalogProperty(t, "kubernetes:pods.v1/financial-service",
		"org.kie.kogito.addons.discovery.kubernetes\\:pods.v1\\/financial-service")

	doTestGenerateMicroprofileServiceCatalogProperty(t, "kubernetes:deployments.v1.apps/namespace1/financial-service",
		"org.kie.kogito.addons.discovery.kubernetes\\:deployments.v1.apps\\/namespace1\\/financial-service")

	doTestGenerateMicroprofileServiceCatalogProperty(t, "kubernetes:deployments.v1.apps/financial-service",
		"org.kie.kogito.addons.discovery.kubernetes\\:deployments.v1.apps\\/financial-service")
}

func doTestGenerateMicroprofileServiceCatalogProperty(t *testing.T, serviceUri string, expectedProperty string) {
	mpProperty := generateMicroprofileServiceCatalogProperty(serviceUri)
	assert.Equal(t, mpProperty, expectedProperty, "expected microprofile service catalog property for serviceUri: %s, is %s, but the returned value was: %s", serviceUri, expectedProperty, mpProperty)
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
		Services: operatorapi.ServicesPlatformSpec{
			DataIndex: &operatorapi.ServiceSpec{
				Enabled: &enabled,
			},
			JobService: &operatorapi.ServiceSpec{
				Enabled: &enabled,
			},
		},
	}

	props, err := NewAppPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr := properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 14, len(generatedProps.Keys()))
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
	assert.Equal(t, "", generatedProps.GetString(constants.DataIndexServiceURLProperty, ""))
	assert.Equal(t, "http://localhost/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoProcessDefinitionsEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoEventsUserTaskEnabled, ""))
	assert.Equal(t, "false", generatedProps.GetString(constants.KogitoEventsVariablesEnabled, ""))

	// prod profile enables config of outgoing events url
	workflow.SetAnnotations(map[string]string{metadata.Profile: string(metadata.ProdProfile)})
	props, err = NewAppPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 15, len(generatedProps.Keys()))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.DataIndexServiceName+"."+platform.Namespace+"/processes", generatedProps.GetString(constants.DataIndexServiceURLProperty, ""))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.JobServiceName+"."+platform.Namespace+"/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceDataSourceReactiveURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEvents, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEventsURL, ""))

	// disabling data index bypasses config of outgoing events url
	platform.Spec.Services.DataIndex.Enabled = nil
	props, err = NewAppPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 14, len(generatedProps.Keys()))
	assert.Equal(t, "", generatedProps.GetString(constants.DataIndexServiceURLProperty, ""))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.JobServiceName+"."+platform.Namespace+"/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEvents, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEventsURL, ""))

	// check that service app properties are being properly set
	js := services.NewJobService(platform)
	p, err := NewServiceAppPropertyHandler(platform, js)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(p.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 9, len(generatedProps.Keys()))
	assert.Equal(t, "false", generatedProps.GetString(constants.JobServiceKafkaSmallRyeHealthProperty, ""))
	assert.Equal(t, "value1", generatedProps.GetString("property1", ""))
	assert.Equal(t, "value2", generatedProps.GetString("property2", ""))
	//quarkus.http.port remains with the default value since it's immutable.
	assert.Equal(t, "8080", generatedProps.GetString("quarkus.http.port", ""))

	// disabling job service bypasses config of outgoing events url
	platform.Spec.Services.JobService.Enabled = nil
	props, err = NewAppPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 14, len(generatedProps.Keys()))
	assert.Equal(t, "", generatedProps.GetString(constants.DataIndexServiceURLProperty, ""))
	assert.Equal(t, "http://localhost/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceDataSourceReactiveURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEvents, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEventsURL, ""))

	// check that the reactive URL is generated from the postgreSQL JDBC URL when not provided
	platform.Spec.Services.JobService = &operatorapi.ServiceSpec{
		Enabled: &enabled,
		Persistence: &operatorapi.PersistenceOptions{
			PostgreSql: &operatorapi.PersistencePostgreSql{
				ServiceRef: &operatorapi.PostgreSqlServiceOptions{
					Name: "jobs-service",
				},
			},
		},
	}
	props, err = NewAppPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 14, len(generatedProps.Keys()))
	assert.Equal(t, "", generatedProps.GetString(constants.DataIndexServiceURLProperty, ""))
	assert.Equal(t, "http://"+platform.Name+"-"+constants.JobServiceName+"."+platform.Namespace+"/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEvents, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEventsURL, ""))

	// check that the reactive URL is generated from the postgreSQL JDBC URL when provided
	platform.Spec.Services.JobService = &operatorapi.ServiceSpec{
		Enabled: &enabled,
		Persistence: &operatorapi.PersistenceOptions{
			PostgreSql: &operatorapi.PersistencePostgreSql{
				JdbcUrl: "jdbc:postgresql://timeouts-showcase-database:5432/postgres?currentSchema=jobs-service",
			},
		},
	}
	props, err = NewAppPropertyHandler(workflow, platform)
	assert.NoError(t, err)
	generatedProps, propsErr = properties.LoadString(props.WithUserProperties(userProperties).Build())
	assert.NoError(t, propsErr)
	assert.Equal(t, 14, len(generatedProps.Keys()))
	assert.Equal(t, "", generatedProps.GetString(constants.DataIndexServiceURLProperty, ""))
	assert.Equal(t, "http://sonataflow-platform-jobs-service.default/v2/jobs/events", generatedProps.GetString(constants.JobServiceRequestEventsURL, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEvents, ""))
	assert.Equal(t, "", generatedProps.GetString(constants.JobServiceStatusChangeEventsURL, ""))

}

var (
	enabled  = true
	disabled = false
)

var _ = Describe("Platform properties", func() {

	var _ = Context("for service properties", func() {

		var _ = Context("defining the application properties generated for the deployment of the", func() {

			DescribeTable("Job Service",
				func(plfm *operatorapi.SonataFlowPlatform, expectedProperties *properties.Properties) {
					js := services.NewJobService(plfm)
					handler, err := NewServiceAppPropertyHandler(plfm, js)
					Expect(err).NotTo(HaveOccurred())
					p, err := properties.LoadString(handler.Build())
					Expect(err).NotTo(HaveOccurred())
					p.Sort()
					Expect(p).To(Equal(expectedProperties))
				},
				Entry("with an empty spec", generatePlatform(emtpyJobServiceSpec()),
					generateJobServiceDeploymentDevProperties()),
				Entry("with enabled field undefined and with ephemeral persistence",
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentDevProperties()),
				Entry("with enabled field undefined and with postgreSQL persistence",
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateJobServiceDeploymentWithPostgreSQLProperties()),
				Entry("with enabled field set to false and with ephemeral persistence",
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentDevProperties()),
				Entry("with enabled field set to false and with postgreSQL persistence",
					generatePlatform(setJobServiceEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateJobServiceDeploymentWithPostgreSQLProperties()),
				Entry("with enabled field set to true and with ephemeral persistence",
					generatePlatform(setJobServiceEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentDevProperties()),
				Entry("with enabled field set to true and with postgreSQL persistence",
					generatePlatform(setJobServiceEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateJobServiceDeploymentWithPostgreSQLProperties()),
				Entry("with both services with enabled field set to true and with ephemeral persistence",
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentWithDataIndexAndEphemeralProperties()),
				Entry("with both services with enabled field set to true and postgreSQL persistence for both",
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema"), setDataIndexJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateJobServiceDeploymentWithDataIndexAndPostgreSQLProperties()),
			)

			DescribeTable("Data Index", func(plfm *operatorapi.SonataFlowPlatform, expectedProperties *properties.Properties) {
				di := services.NewDataIndexService(plfm)
				handler, err := NewServiceAppPropertyHandler(plfm, di)
				Expect(err).NotTo(HaveOccurred())
				p, err := properties.LoadString(handler.Build())
				Expect(err).NotTo(HaveOccurred())
				p.Sort()
				Expect(p).To(Equal(expectedProperties))
			},
				Entry("with ephemeral persistence", generatePlatform(emtpyDataIndexServiceSpec()), generateDataIndexDeploymentProperties()),
				Entry("with postgreSQL persistence", generatePlatform(emtpyDataIndexServiceSpec(), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexDeploymentProperties()),
			)
		})

		var _ = Context("defining the workflow properties generated from", func() {

			DescribeTable("only job services when the spec",
				func(wf *operatorapi.SonataFlow, plfm *operatorapi.SonataFlowPlatform, expectedProperties *properties.Properties) {
					handler, err := NewAppPropertyHandler(wf, plfm)
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
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
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
					handler, err := NewAppPropertyHandler(wf, plfm)
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
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&disabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field undefined and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(nil), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setDataIndexEnabledValue(&enabled), setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexWorkflowDevProperties()),
				Entry("has enabled field set to true and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
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
				handler, err := NewAppPropertyHandler(wf, plfm)
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
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setPlatformNamespace("default"), setPlatformName("foo")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to true and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to true and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateDataIndexAndJobServiceWorkflowProductionProperties()),
				Entry("both have enabled field undefined and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setDataIndexEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field undefined and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(nil), setDataIndexEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to false and workflow with dev profile",
					generateFlow(setProfileInFlow(metadata.DevProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
					generatePlatform(setJobServiceEnabledValue(&disabled), setDataIndexEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexAndJobServiceWorkflowDevProperties()),
				Entry("both have enabled field set to false and workflow with production profile",
					generateFlow(setProfileInFlow(metadata.ProdProfile), setWorkflowName("foo"), setWorkflowNamespace("default")),
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

func generateJobServiceDeploymentDevProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`, "false")
	p.Sort()
	return p
}

func generateDataIndexDeploymentProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set("quarkus.smallrye-health.check.\"io.quarkus.kafka.client.health.KafkaHealthCheck\".enabled", "false")
	p.Sort()
	return p
}

func generateJobServiceDeploymentWithPostgreSQLProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`, "false")
	p.Set("quarkus.datasource.reactive.url", "postgresql://postgres:5432/sonataflow?search_path=myschema")
	p.Sort()
	return p
}

func generateJobServiceDeploymentWithDataIndexAndEphemeralProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("kogito.jobs-service.http.job-status-change-events", "true")
	p.Set("mp.messaging.outgoing.kogito-job-service-job-status-events-http.url", "http://foo-data-index-service.default/jobs")
	p.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`, "false")
	p.Sort()
	return p
}

func generateJobServiceDeploymentWithDataIndexAndPostgreSQLProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("kogito.jobs-service.http.job-status-change-events", "true")
	p.Set("mp.messaging.outgoing.kogito-job-service-job-status-events-http.url", "http://foo-data-index-service.default/jobs")
	p.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`, "false")
	p.Set("quarkus.datasource.reactive.url", "postgresql://postgres:5432/sonataflow?search_path=myschema")
	p.Sort()
	return p
}

var (
	jobServiceDevProperties           *properties.Properties
	jobServiceProdProperties          *properties.Properties
	dataIndexDevProperties            *properties.Properties
	dataIndexProdProperties           *properties.Properties
	dataIndexJobServiceDevProperties  *properties.Properties
	dataIndexJobServiceProdProperties *properties.Properties
)

func generateJobServiceWorkflowDevProperties() *properties.Properties {
	if jobServiceDevProperties == nil {
		jobServiceDevProperties = properties.NewProperties()
		jobServiceDevProperties.Set("kogito.events.processdefinitions.enabled", "false")
		jobServiceDevProperties.Set("quarkus.devservices.enabled", "false")
		jobServiceDevProperties.Set("kogito.service.url", "http://foo.default")
		jobServiceDevProperties.Set("quarkus.kogito.devservices.enabled", "false")
		jobServiceDevProperties.Set("quarkus.http.host", "0.0.0.0")
		jobServiceDevProperties.Set("quarkus.http.port", "8080")
		jobServiceDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
		jobServiceDevProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		jobServiceDevProperties.Set("kogito.events.processinstances.enabled", "false")
		jobServiceDevProperties.Set("kogito.events.usertasks.enabled", "false")
		jobServiceDevProperties.Set("kogito.events.variables.enabled", "false")
		jobServiceDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		jobServiceDevProperties.Sort()
	}
	return jobServiceDevProperties
}

func generateJobServiceWorkflowProductionProperties() *properties.Properties {
	if jobServiceProdProperties == nil {
		jobServiceProdProperties = properties.NewProperties()
		jobServiceProdProperties.Set("quarkus.kogito.devservices.enabled", "false")
		jobServiceProdProperties.Set("kogito.events.processdefinitions.enabled", "false")
		jobServiceProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		jobServiceProdProperties.Set("quarkus.http.host", "0.0.0.0")
		jobServiceProdProperties.Set("quarkus.http.port", "8080")
		jobServiceProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://foo-jobs-service.default/v2/jobs/events")
		jobServiceProdProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		jobServiceProdProperties.Set("quarkus.devservices.enabled", "false")
		jobServiceProdProperties.Set("kogito.events.processinstances.enabled", "false")
		jobServiceProdProperties.Set("kogito.events.usertasks.enabled", "false")
		jobServiceProdProperties.Set("kogito.events.variables.enabled", "false")
		jobServiceProdProperties.Set("kogito.service.url", "http://foo.default")
		jobServiceProdProperties.Sort()
	}
	return jobServiceProdProperties
}
func generateDataIndexWorkflowDevProperties() *properties.Properties {
	if dataIndexDevProperties == nil {
		dataIndexDevProperties = properties.NewProperties()
		dataIndexDevProperties.Set("kogito.events.variables.enabled", "false")
		dataIndexDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		dataIndexDevProperties.Set("quarkus.devservices.enabled", "false")
		dataIndexDevProperties.Set("quarkus.kogito.devservices.enabled", "false")
		dataIndexDevProperties.Set("kogito.service.url", "http://foo.default")
		dataIndexDevProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		dataIndexDevProperties.Set("quarkus.http.port", "8080")
		dataIndexDevProperties.Set("kogito.events.processdefinitions.enabled", "false")
		dataIndexDevProperties.Set("kogito.events.processinstances.enabled", "false")
		dataIndexDevProperties.Set("kogito.events.usertasks.enabled", "false")
		dataIndexDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
		dataIndexDevProperties.Set("quarkus.http.host", "0.0.0.0")
		dataIndexDevProperties.Sort()
	}
	return dataIndexDevProperties
}

func generateDataIndexWorkflowProductionProperties() *properties.Properties {
	if dataIndexProdProperties == nil {
		dataIndexProdProperties = properties.NewProperties()
		dataIndexProdProperties.Set("kogito.events.variables.enabled", "false")
		dataIndexProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		dataIndexProdProperties.Set("quarkus.devservices.enabled", "false")
		dataIndexProdProperties.Set("quarkus.kogito.devservices.enabled", "false")
		dataIndexProdProperties.Set("kogito.service.url", "http://foo.default")
		dataIndexProdProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		dataIndexProdProperties.Set("mp.messaging.outgoing.kogito-processinstances-events.url", "http://foo-data-index-service.default/processes")
		dataIndexProdProperties.Set("quarkus.http.port", "8080")
		dataIndexProdProperties.Set("kogito.events.processdefinitions.enabled", "false")
		dataIndexProdProperties.Set("kogito.events.processinstances.enabled", "true")
		dataIndexProdProperties.Set("kogito.events.usertasks.enabled", "false")
		dataIndexProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
		dataIndexProdProperties.Set("quarkus.http.host", "0.0.0.0")
		dataIndexProdProperties.Sort()
	}
	return dataIndexProdProperties
}

func generateDataIndexAndJobServiceWorkflowDevProperties() *properties.Properties {
	if dataIndexJobServiceDevProperties == nil {
		dataIndexJobServiceDevProperties = properties.NewProperties()
		dataIndexJobServiceDevProperties.Set("quarkus.kogito.devservices.enabled", "false")
		dataIndexJobServiceDevProperties.Set("kogito.events.processdefinitions.enabled", "false")
		dataIndexJobServiceDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		dataIndexJobServiceDevProperties.Set("quarkus.http.host", "0.0.0.0")
		dataIndexJobServiceDevProperties.Set("quarkus.http.port", "8080")
		dataIndexJobServiceDevProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://localhost/v2/jobs/events")
		dataIndexJobServiceDevProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		dataIndexJobServiceDevProperties.Set("quarkus.devservices.enabled", "false")
		dataIndexJobServiceDevProperties.Set("kogito.events.processinstances.enabled", "false")
		dataIndexJobServiceDevProperties.Set("kogito.events.usertasks.enabled", "false")
		dataIndexJobServiceDevProperties.Set("kogito.events.variables.enabled", "false")
		dataIndexJobServiceDevProperties.Set("kogito.service.url", "http://foo.default")
		dataIndexJobServiceDevProperties.Sort()
	}
	return dataIndexJobServiceDevProperties
}

func generateDataIndexAndJobServiceWorkflowProductionProperties() *properties.Properties {
	if dataIndexJobServiceProdProperties == nil {
		dataIndexJobServiceProdProperties = properties.NewProperties()
		dataIndexJobServiceProdProperties.Set("quarkus.kogito.devservices.enabled", "false")
		dataIndexJobServiceProdProperties.Set("kogito.events.processdefinitions.enabled", "false")
		dataIndexJobServiceProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.connector", "quarkus-http")
		dataIndexJobServiceProdProperties.Set("quarkus.http.host", "0.0.0.0")
		dataIndexJobServiceProdProperties.Set("quarkus.http.port", "8080")
		dataIndexJobServiceProdProperties.Set("mp.messaging.outgoing.kogito-job-service-job-request-events.url", "http://foo-jobs-service.default/v2/jobs/events")
		dataIndexJobServiceProdProperties.Set("org.kie.kogito.addons.knative.eventing.health-enabled", "false")
		dataIndexJobServiceProdProperties.Set("quarkus.devservices.enabled", "false")
		dataIndexJobServiceProdProperties.Set("kogito.events.processinstances.enabled", "true")
		dataIndexJobServiceProdProperties.Set("kogito.events.usertasks.enabled", "false")
		dataIndexJobServiceProdProperties.Set("kogito.events.variables.enabled", "false")
		dataIndexJobServiceProdProperties.Set("kogito.service.url", "http://foo.default")
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
		if p.Spec.Services.JobService == nil {
			p.Spec.Services.JobService = &operatorapi.ServiceSpec{}
		}
		p.Spec.Services.JobService.Enabled = v
	}
}

func setDataIndexEnabledValue(v *bool) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services.DataIndex == nil {
			p.Spec.Services.DataIndex = &operatorapi.ServiceSpec{}
		}
		p.Spec.Services.DataIndex.Enabled = v
	}
}

func emtpyDataIndexServiceSpec() plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services.DataIndex == nil {
			p.Spec.Services.DataIndex = &operatorapi.ServiceSpec{}
		}
	}
}

func emtpyJobServiceSpec() plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services.JobService == nil {
			p.Spec.Services.JobService = &operatorapi.ServiceSpec{}
		}
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
		if p.Spec.Services.JobService == nil {
			p.Spec.Services.JobService = &operatorapi.ServiceSpec{}
		}
		if p.Spec.Services.JobService.Persistence == nil {
			p.Spec.Services.JobService.Persistence = &operatorapi.PersistenceOptions{}
		}
		if p.Spec.Services.JobService.Persistence.PostgreSql == nil {
			p.Spec.Services.JobService.Persistence.PostgreSql = &operatorapi.PersistencePostgreSql{}
		}
		p.Spec.Services.JobService.Persistence.PostgreSql.JdbcUrl = jdbc
	}
}

func setDataIndexJDBC(jdbc string) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services.DataIndex == nil {
			p.Spec.Services.DataIndex = &operatorapi.ServiceSpec{}
		}
		if p.Spec.Services.DataIndex.Persistence == nil {
			p.Spec.Services.DataIndex.Persistence = &operatorapi.PersistenceOptions{}
		}
		if p.Spec.Services.DataIndex.Persistence.PostgreSql == nil {
			p.Spec.Services.DataIndex.Persistence.PostgreSql = &operatorapi.PersistencePostgreSql{}
		}
		p.Spec.Services.DataIndex.Persistence.PostgreSql.JdbcUrl = jdbc
	}
}
