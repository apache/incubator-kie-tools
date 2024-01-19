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
	"testing"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/magiconair/properties"
	"github.com/serverlessworkflow/sdk-go/v2/model"
	"github.com/stretchr/testify/assert"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func Test_generateDiscoveryProperties(t *testing.T) {

	catalogService := &mockCatalogService{}

	propertiesContent := "property1=value1\n"
	propertiesContent = propertiesContent + "property2=${value2}\n"
	propertiesContent = propertiesContent + "service1=${kubernetes:services.v1/namespace1/my-service1}\n"
	propertiesContent = propertiesContent + "service2=${kubernetes:services.v1/my-service2}\n"
	propertiesContent = propertiesContent + "service3=${kubernetes:services.v1/my-service3?port=http-port}\n"

	propertiesContent = propertiesContent + "non_service4=${kubernetes:--kaka}"

	workflow := v1alpha08.Flow{
		Functions: []model.Function{
			{
				Name:      "knServiceInvocation1",
				Operation: "knative:services.v1.serving.knative.dev/namespace1/my-kn-service1?path=/knative-function1",
			},
			{
				Name:      "knServiceInvocation2",
				Operation: "knative:services.v1.serving.knative.dev/my-kn-service3?path=/knative-function3",
			},
		},
	}

	props := properties.MustLoadString(propertiesContent)
	result := generateDiscoveryProperties(context.TODO(), catalogService, props, &operatorapi.SonataFlow{
		ObjectMeta: metav1.ObjectMeta{Name: "helloworld", Namespace: defaultNamespace},
		Spec:       v1alpha08.SonataFlowSpec{Flow: workflow},
	})

	assert.Equal(t, result.Len(), 5)
	assertHasProperty(t, result, "org.kie.kogito.addons.discovery.kubernetes\\:services.v1\\/namespace1\\/my-service1", myService1Address)
	assertHasProperty(t, result, "org.kie.kogito.addons.discovery.kubernetes\\:services.v1\\/my-service2", myService2Address)
	assertHasProperty(t, result, "org.kie.kogito.addons.discovery.kubernetes\\:services.v1\\/my-service3?port\\=http-port", myService3Address)
	assertHasProperty(t, result, "org.kie.kogito.addons.discovery.knative\\:services.v1.serving.knative.dev\\/namespace1\\/my-kn-service1", myKnService1Address)
	assertHasProperty(t, result, "org.kie.kogito.addons.discovery.knative\\:services.v1.serving.knative.dev\\/my-kn-service3", myKnService3Address)
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
