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

	"k8s.io/apimachinery/pkg/runtime/schema"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/client-go/kubernetes/scheme"
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
	assert.Equal(t, proj.Workflow.ObjectMeta.Labels, map[string]string{"app": "hello", "sonataflow.org/workflow-app": "hello"})
	assert.NotNil(t, proj.Properties)
	assert.NotEmpty(t, proj.Resources)
	assert.Equal(t, "hello", proj.Workflow.Name)
	assert.Equal(t, "hello-props", proj.Properties.Name)
	assert.NotEmpty(t, proj.Properties.Data)
	assert.Equal(t, 1, len(proj.Resources))
	assert.Equal(t, "01-hello-resources", proj.Resources[0].Name)
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
	assert.Equal(t, "01-hello-resources", proj.Resources[0].Name)
	assert.Equal(t, "02-hello-resources", proj.Resources[1].Name)
	assert.Equal(t, proj.Workflow.Spec.Resources.ConfigMaps[0].ConfigMap.Name, proj.Resources[0].Name)
	assert.Equal(t, proj.Workflow.Spec.Resources.ConfigMaps[1].ConfigMap.Name, proj.Resources[1].Name)
	data, err := getResourceDataWithFileName(proj.Resources, "myopenapi.json")
	assert.NoError(t, err)
	assert.NotEmpty(t, data)
	data, err = getResourceDataWithFileName(proj.Resources, "input.json")
	assert.NoError(t, err)
	assert.NotEmpty(t, data)
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
		"02-configmap_01-service-resources.yaml",
		"03-configmap_02-service-resources.yaml",
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

func mustGetFile(filepath string) io.Reader {
	file, err := os.OpenFile(filepath, os.O_RDONLY, os.ModePerm)
	if err != nil {
		panic(err)
	}
	return file
}
