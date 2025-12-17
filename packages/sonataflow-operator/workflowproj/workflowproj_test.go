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

package workflowproj

import (
	"fmt"
	"io"
	"os"
	"path"
	"sort"
	"strings"
	"testing"

	"github.com/magiconair/properties"

	"k8s.io/apimachinery/pkg/runtime/schema"

	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/client-go/kubernetes/scheme"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
)

func Test_Handler_WorkflowMinimal(t *testing.T) {
	proj, err := New("default").WithWorkflow(getWorkflowMinimal()).AsObjects()
	assert.NoError(t, err)
	assert.NotNil(t, proj)
	assert.Equal(t, "hello", proj.Workflow.Name)
	assert.Equal(t, string(metadata.DevProfile), proj.Workflow.Annotations[metadata.Profile])
}

func Test_Handler_WorkflowMinimalInvalid(t *testing.T) {
	proj, err := New("default").
		WithWorkflow(getWorkflowMinimalInvalid()).
		AsObjects()
	assert.Error(t, err)
	assert.Nil(t, proj)
}

func Test_Handler_WorkflowMinimalAndProps(t *testing.T) {
	proj, err := New("default").
		Named("minimal").
		Profile(metadata.PreviewProfile).
		WithWorkflow(getWorkflowMinimal()).
		WithAppProperties(getWorkflowProperties()).
		AsObjects()
	assert.NoError(t, err)
	assert.NotNil(t, proj.Workflow)
	assert.NotNil(t, proj.Properties)
	assert.Equal(t, "minimal", proj.Workflow.Name)
	assert.Equal(t, "minimal-props", proj.Properties.Name)
	assert.NotEmpty(t, proj.Properties.Data["application.properties"])
	assert.Equal(t, string(metadata.PreviewProfile), proj.Workflow.Annotations[metadata.Profile])
	assert.NotEmpty(t, proj.Properties.Data)
}

func Test_Handler_WorkflowMinimalAndPropsAndSpec(t *testing.T) {
	proj, err := New("default").
		WithWorkflow(getWorkflowMinimal()).
		WithAppProperties(getWorkflowProperties()).
		AddResource("myopenapi.json", getSpecOpenApi()).
		AsObjects()
	assert.NoError(t, err)
	assert.NotNil(t, proj.Workflow)
	assert.NotNil(t, proj.Workflow.ObjectMeta)
	assert.Equal(t, proj.Workflow.ObjectMeta.Labels, map[string]string{
		"app":                               "hello",
		"sonataflow.org/workflow-app":       "hello",
		"sonataflow.org/workflow-namespace": "default",
		"app.kubernetes.io/name":            "hello",
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator",
	})
	assert.NotNil(t, proj.Properties)
	assert.NotEmpty(t, proj.Resources)
	assert.Equal(t, "hello", proj.Workflow.Name)
	assert.Equal(t, "hello-props", proj.Properties.Name)
	assert.NotEmpty(t, proj.Properties.Data)
	assert.Equal(t, 1, len(proj.Resources))
	assert.Equal(t, "01-hello-resources-specs", proj.Resources[0].Name)
	assert.Equal(t, proj.Workflow.Spec.Resources.ConfigMaps[0].ConfigMap.Name, proj.Resources[0].Name)

}

func Test_Handler_WorkflowMinimalAndPropsAndSpecAndGeneric(t *testing.T) {
	proj, err := New("default").
		WithWorkflow(getWorkflowMinimal()).
		WithAppProperties(getWorkflowProperties()).
		AddResource("myopenapi.json", getSpecOpenApi()).
		AddResource("myopenapi.json", getSpecOpenApi()).
		AddResource("myopenapi2.json", getSpecOpenApi()).
		AddResourceAt("input.json", "files", getSpecGeneric()).
		AsObjects()
	assert.NoError(t, err)
	assert.NotNil(t, proj.Workflow)
	assert.NotNil(t, proj.Properties)
	assert.NotEmpty(t, proj.Resources)
	sort.Slice(proj.Resources, func(i, j int) bool {
		return proj.Resources[i].Name < proj.Resources[j].Name
	})
	sort.Slice(proj.Workflow.Spec.Resources.ConfigMaps, func(i, j int) bool {
		return proj.Workflow.Spec.Resources.ConfigMaps[i].ConfigMap.Name < proj.Workflow.Spec.Resources.ConfigMaps[j].ConfigMap.Name
	})

	assert.Equal(t, "hello", proj.Workflow.Name)
	assert.Equal(t, "hello-props", proj.Properties.Name)
	assert.NotEmpty(t, proj.Properties.Data)
	assert.Equal(t, 2, len(proj.Resources))
	assert.Equal(t, "01-hello-resources-files", proj.Resources[0].Name)
	assert.Equal(t, "02-hello-resources-specs", proj.Resources[1].Name)
	assert.Equal(t, proj.Workflow.Spec.Resources.ConfigMaps[0].ConfigMap.Name, proj.Resources[0].Name)
	assert.Equal(t, proj.Workflow.Spec.Resources.ConfigMaps[1].ConfigMap.Name, proj.Resources[1].Name)
	data, err := getResourceDataWithFileName(proj.Resources, "myopenapi.json")
	assert.NoError(t, err)
	assert.NotEmpty(t, data)
	data, err = getResourceDataWithFileName(proj.Resources, "input.json")
	assert.NoError(t, err)
	assert.NotEmpty(t, data)
}

func Test_Handler_WorkflowMinimalAndSecrets(t *testing.T) {
	type env struct {
		key              string
		secretKeyRefName string
	}
	proj, err := New("default").
		Named("minimal").
		Profile(metadata.PreviewProfile).
		WithWorkflow(getWorkflowMinimal()).
		WithSecretProperties(getWorkflowSecretProperties()).
		AsObjects()
	assert.NoError(t, err)
	assert.NotNil(t, proj.Workflow)
	assert.NotNil(t, proj.SecretProperties)
	assert.Equal(t, "minimal", proj.Workflow.Name)
	assert.Equal(t, "minimal-secrets", proj.SecretProperties.Name)
	assert.NotEmpty(t, proj.SecretProperties.StringData)

	secretPropsContent, err := io.ReadAll(getWorkflowSecretProperties())
	assert.NoError(t, err)
	secrets, err := properties.Load(secretPropsContent, properties.UTF8)
	assert.NoError(t, err)
	envs := map[string]env{}

	for _, value := range proj.Workflow.Spec.PodTemplate.Container.Env {
		envs[value.Name] = env{key: value.ValueFrom.SecretKeyRef.Key, secretKeyRefName: value.ValueFrom.SecretKeyRef.Name}
	}

	for k, v := range secrets.Map() {
		assert.Equal(t, v, proj.SecretProperties.StringData[k])
		normalized, err := normalizeEnvNames(k)
		for _, value := range normalized {
			assert.NoError(t, err)
			env, exists := envs[value]
			assert.True(t, exists)
			assert.Equal(t, k, env.key)
			assert.Equal(t, proj.SecretProperties.Name, env.secretKeyRefName)
		}

	}
}

func getResourceDataWithFileName(cms []*corev1.ConfigMap, fileName string) (string, error) {
	for i := range cms {
		if data, ok := cms[i].Data[fileName]; ok {
			return data, nil
		}
	}
	return "", fmt.Errorf("No configmap found with data containing filename %s", fileName)
}

func Test_Handler_WorklflowServiceAndPropsAndSpec_SaveAs(t *testing.T) {
	handler := New("default").
		WithWorkflow(getWorkflowService()).
		WithAppProperties(getWorkflowProperties()).
		AddResource("myopenapi.json", getSpecOpenApi()).
		AddResourceAt("schema.json", "files", getSpecGeneric())
	proj, err := handler.AsObjects()
	assert.NoError(t, err)
	assert.NotNil(t, proj.Workflow)
	assert.NotNil(t, proj.Properties)
	assert.NotEmpty(t, proj.Resources)

	tmpPath, err := os.MkdirTemp("", "*-test")
	assert.NoError(t, err)
	defer os.RemoveAll(tmpPath)

	assert.NoError(t, handler.SaveAsKubernetesManifests(tmpPath))
	files, err := os.ReadDir(tmpPath)
	assert.NoError(t, err)
	assert.Len(t, files, 4)

	expectedFiles := []string{
		"01-configmap_service-props.yaml",
		"02-configmap_01-service-resources-files.yaml",
		"03-configmap_02-service-resources-specs.yaml",
		"04-sonataflow_service.yaml",
	}
	expectedKinds := []schema.GroupVersionKind{
		{Group: "", Version: "v1", Kind: "ConfigMap"},
		{Group: "", Version: "v1", Kind: "ConfigMap"},
		{Group: "", Version: "v1", Kind: "ConfigMap"},
		{Group: "sonataflow.org", Version: "v1alpha08", Kind: "SonataFlow"},
	}

	for i := 0; i < len(files); i++ {
		assert.Equal(t, files[i].Name(), expectedFiles[i])
		assertIsK8sObject(t, tmpPath, files[i].Name(), expectedKinds[i])
	}
}

func assertIsK8sObject(t *testing.T, basePath string, fileName string, gvk schema.GroupVersionKind) {
	contents, err := os.ReadFile(path.Join(basePath, fileName))
	assert.NoError(t, err)
	decode := scheme.Codecs.UniversalDeserializer().Decode
	k8sObj, _, err := decode(contents, nil, nil)
	assert.NoError(t, err)
	assert.NotNil(t, k8sObj)
	assert.NotEmpty(t, k8sObj.GetObjectKind().GroupVersionKind().String())
	assert.Equal(t, gvk, k8sObj.GetObjectKind().GroupVersionKind())
}

func Test_Handler_WorkflowService_SaveAs(t *testing.T) {
	testRun := func(t *testing.T, handler WorkflowProjectHandler) {
		proj, err := handler.AsObjects()
		assert.NoError(t, err)
		assert.NotNil(t, proj.Workflow)

		tmpPath, err := os.MkdirTemp("", "*-test")
		assert.NoError(t, err)
		defer os.RemoveAll(tmpPath)

		assert.NoError(t, handler.SaveAsKubernetesManifests(tmpPath))
		files, err := os.ReadDir(tmpPath)
		assert.NoError(t, err)
		assert.Len(t, files, 1)

		for _, f := range files {
			if strings.HasSuffix(f.Name(), yamlFileExt) {
				// we have only one file produced in these test cases
				prefix := fmt.Sprintf("%02d-", 1)
				assert.True(t, strings.HasPrefix(f.Name(), prefix))
				contents, err := os.ReadFile(path.Join(tmpPath, f.Name()))
				assert.NoError(t, err)
				decode := scheme.Codecs.UniversalDeserializer().Decode
				k8sObj, _, err := decode(contents, nil, nil)
				assert.NoError(t, err)
				assert.NotNil(t, k8sObj)
				assert.NotEmpty(t, k8sObj.GetObjectKind().GroupVersionKind().String())
			}
		}
	}

	t.Run("SaveAs in default namespace", func(t *testing.T) {
		testRun(t, New("default").WithWorkflow(getWorkflowService()))
	})

	t.Run("SaveAs with empty namespace namespace", func(t *testing.T) {
		testRun(t, New("").WithWorkflow(getWorkflowService()))
	})
}

func TestWorkflowProjectHandler_Image(t *testing.T) {
	handler := New("default").WithWorkflow(getWorkflowService())
	proj, err := handler.AsObjects()
	handler.Image("host/namespace/service:latest")
	assert.NoError(t, err)
	assert.Equal(t, "host/namespace/service:latest", proj.Workflow.Spec.PodTemplate.Container.Image)
}

func TestNormalizeEnvName(t *testing.T) {
	type testCase struct {
		input    string
		expected []string
		error    bool
	}
	tests := []testCase{
		{"my-env", []string{"MY_ENV"}, false},
		{"my.env.1", []string{"MY_ENV_1"}, false},
		{"my.env-1", []string{"MY_ENV_1"}, false},
		{"my-env.1", []string{"MY_ENV_1"}, false},
		{"my-env-1$", []string{""}, true},
		{"my-env-1&&", []string{""}, true},
		{"", []string{""}, true},
		{"$%&*", []string{""}, true},
		{"a", []string{"A"}, false},
		{"1", []string{"1"}, false},
		{"_", []string{""}, true},
		{"my env", []string{"MY_ENV"}, false},
		{"  my env  ", []string{"MY_ENV"}, false},
		{"-", []string{""}, true},
		{".", []string{""}, true},
		{"my-env-1234567890-long-name-with-dashes", []string{"MY_ENV_1234567890_LONG_NAME_WITH_DASHES"}, false},
		{"long-name-with-invalid-characters-@#$%^", []string{""}, true},
		{"my-env-1@name", []string{""}, true},
		{"A", []string{"A"}, false},
		{"a1_b2", []string{"A1_B2"}, false},
		{"a!!@#$b", []string{""}, true},
		{"foo.\"bar\".baz", []string{"FOO__BAR__BAZ"}, false},
		{"quarkus.'my-property'.foo", []string{"QUARKUS__MY_PROPERTY__FOO"}, false},
		{"myProperty[10]", []string{"MYPROPERTY_10"}, false},
		{"my.config[0].value", []string{"MY_CONFIG_0__VALUE"}, false},
		{"quarkus.\"myProperty\"[0].value", []string{"QUARKUS__MYPROPERTY__0__VALUE"}, false},
		{"quarkus.\"my-property\"[1].sub-name", []string{"QUARKUS__MY_PROPERTY__1__SUB_NAME"}, false},
		{"quarkus.myProperty..sub.value", []string{"QUARKUS_MYPROPERTY__SUB_VALUE"}, false},
		{"quarkus.[strange].key", []string{"QUARKUS__STRANGE__KEY"}, false},
		{"quarkus.datasource.\"datasource-name\".jdbc.url", []string{"QUARKUS_DATASOURCE__DATASOURCE_NAME__JDBC_URL"}, false},
		{"%dev.quarkus.http.port", []string{"_DEV_QUARKUS_HTTP_PORT"}, false},
		{"%staging.quarkus.http.test-port", []string{"_STAGING_QUARKUS_HTTP_TEST_PORT"}, false},
		{"%prod,dev.my.prop", []string{"_PROD_MY_PROP", "_DEV_MY_PROP"}, false},
		{"%prod,dev.quarkus.datasource.\"datasource-name\".jdbc.url", []string{"_PROD_QUARKUS_DATASOURCE__DATASOURCE_NAME__JDBC_URL",
			"_DEV_QUARKUS_DATASOURCE__DATASOURCE_NAME__JDBC_URL"}, false},
	}

	for _, test := range tests {
		t.Run(test.input, func(t *testing.T) {
			actual, err := normalizeEnvNames(test.input)
			if test.error {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
				for index, expected := range test.expected {
					assert.Equal(t, expected, actual[index])
				}
			}
		})
	}
}

func getWorkflowMinimalInvalid() io.Reader {
	return mustGetFile("testdata/workflows/workflow-minimal-invalid.sw.json")
}

func getWorkflowMinimal() io.Reader {
	return mustGetFile("testdata/workflows/workflow-minimal.sw.json")
}

func getWorkflowService() io.Reader {
	return mustGetFile("testdata/workflows/workflow-service.sw.json")
}

func getWorkflowProperties() io.Reader {
	return mustGetFile("testdata/workflows/application.properties")
}

func getSpecOpenApi() io.Reader {
	return mustGetFile("testdata/workflows/specs/workflow-service-openapi.json")
}

func getSpecGeneric() io.Reader {
	return mustGetFile("testdata/workflows/specs/workflow-service-schema.json")
}

func getWorkflowSecretProperties() io.Reader {
	return mustGetFile("testdata/workflows/secret.properties")
}

func mustGetFile(filepath string) io.Reader {
	file, err := os.OpenFile(filepath, os.O_RDONLY, os.ModePerm)
	if err != nil {
		panic(err)
	}
	return file
}
