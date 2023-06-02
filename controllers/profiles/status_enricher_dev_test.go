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

package profiles

import (
	"context"
	"strings"
	"testing"

	openshiftv1 "github.com/openshift/api/route/v1"
	"github.com/stretchr/testify/assert"
	"knative.dev/pkg/apis"

	apiv08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/test"
)

func Test_enrichmentStatusOnK8s(t *testing.T) {
	t.Run("verify that the service URL is returned with the default cluster name on default namespace", func(t *testing.T) {

		workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeYamlCR, t.Name())
		workflow.Namespace = toK8SNamespace(t.Name())
		service, err := defaultServiceCreator(workflow)
		client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow, service).Build()
		obj, err := defaultDevStatusEnricher(context.TODO(), client, workflow)

		reflectWorkflow := obj.(*apiv08.KogitoServerlessWorkflow)
		assert.NoError(t, err)
		assert.NotNil(t, obj)
		assert.NotNil(t, reflectWorkflow.Status.Address)
		assert.Equal(t, reflectWorkflow.Status.Address.URL.String(), "http://"+workflow.Name+"."+workflow.Namespace+".svc.cluster.local/"+workflow.Name)

	})

	t.Run("verify that the service URL won't be generated if an invalid namespace is used", func(t *testing.T) {

		workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeYamlCR, t.Name())
		workflow.Namespace = t.Name()
		service, err := defaultServiceCreator(workflow)
		client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow, service).Build()
		_, err = defaultDevStatusEnricher(context.TODO(), client, workflow)
		assert.Error(t, err)

	})
}

func Test_enrichmentStatusOnOCP(t *testing.T) {
	t.Run("verify that the service URL is returned with the default cluster name on default namespace", func(t *testing.T) {
		workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeYamlCR, t.Name())
		workflow.Namespace = toK8SNamespace(t.Name())
		service, err := defaultServiceCreator(workflow)
		route := &openshiftv1.Route{}
		route.Name = workflow.Name
		route.Namespace = workflow.Namespace
		route.Spec.Host = workflow.Name + "." + workflow.Namespace + ".apps-crc.testing"
		client := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(workflow, service, route).Build()
		obj, err := devStatusEnricherForOpenShift(context.TODO(), client, workflow)

		reflectWorkflow := obj.(*apiv08.KogitoServerlessWorkflow)
		assert.NoError(t, err)
		assert.NotNil(t, obj)
		assert.NotNil(t, reflectWorkflow.Status.Address)
		expectedURL := apis.HTTP(route.Spec.Host)
		expectedURL.Path = workflow.Name
		assert.Equal(t, reflectWorkflow.Status.Address.URL.String(), expectedURL.String())

	})
}

func toK8SNamespace(testName string) string {
	return strings.ToLower(strings.Replace(strings.Split(testName, "/")[0], "_", "-", 1))
}
