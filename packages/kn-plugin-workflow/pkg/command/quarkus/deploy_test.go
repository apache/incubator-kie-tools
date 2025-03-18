/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package quarkus

import (
	"context"
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common/k8sclient"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/spf13/afero"
	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"strings"
	"testing"
)

type testDeploy struct {
	input          DeployCmdConfig
	expected       bool
	knative        string
	kogito         string
	resourcesCount int
}

const defaultPath = "./target/kubernetes"

var quarkusDependencies = metadata.ResolveQuarkusDependencies()

var testRunDeploy = []testDeploy{
	{input: DeployCmdConfig{Path: defaultPath}, expected: true, knative: "knative.yml", kogito: "kogito-default.yml"},
	{input: DeployCmdConfig{Path: "./different/folders"}, expected: true, knative: "knative.yml", kogito: "kogito-default.yml"},
	{input: DeployCmdConfig{Path: "different/folders"}, expected: true, knative: "knative.yml", kogito: "kogito-complex.yml"},
	{input: DeployCmdConfig{Path: "different/folders"}, expected: true, knative: "knative.yml", kogito: "kogito-complex2.yml"},
	{input: DeployCmdConfig{Path: "./different/folders", Namespace: "mycustom"}, expected: true, knative: "knative.yml", kogito: "kogito-default-custom-namespace.yml"},
	{input: DeployCmdConfig{Path: "different/folders", Namespace: "mycustom"}, expected: true, knative: "knative.yml", kogito: "kogito-complex-custom-namespace.yml"},
	{input: DeployCmdConfig{Path: "different/folders", Namespace: "mycustom"}, expected: true, knative: "knative.yml", kogito: "kogito-complex2-custom-namespace.yml"},
	{input: DeployCmdConfig{Path: "different/folders", Namespace: "mycustom"}, expected: true, knative: "knative.yml", kogito: "kogito-complex3-custom-namespace.yml"},
	{input: DeployCmdConfig{}, expected: false, kogito: ""},
	{input: DeployCmdConfig{}, expected: false},
}

func fakeRunDeploy(testIndex int) func(command string, args ...string) *exec.Cmd {
	return func(command string, args ...string) *exec.Cmd {
		cs := []string{"-test.run=TestHelperRunDeploy", "--", command}
		cs = append(cs, args...)
		cmd := exec.Command(os.Args[0], cs...)
		cmd.Env = []string{fmt.Sprintf("GO_TEST_HELPER_RUN_DEPLOY_IMAGE=%d", testIndex)}
		return cmd
	}
}

func TestHelperRunDeploy(t *testing.T) {
	testIndex, err := strconv.Atoi(os.Getenv("GO_TEST_HELPER_RUN_DEPLOY_IMAGE"))
	if err != nil {
		return
	}
	out := []string{"Test", strconv.Itoa(testIndex)}
	if testRunDeploy[testIndex].kogito != "" {
		out = append(out, "with creating", testRunDeploy[testIndex].kogito, "file")
	}
	fmt.Fprintf(os.Stdout, "%v", out)
	os.Exit(0)
}

func TestRunDeploy(t *testing.T) {
	common.FS = afero.NewMemMapFs()
	originalParseYamlFile := k8sclient.ParseYamlFile
	originalDynamicClient := k8sclient.DynamicClient
	originalGetNamespace := k8sclient.GetCurrentNamespace

	fakeClient := k8sclient.Fake{FS: common.FS}

	defer func() {
		k8sclient.ParseYamlFile = originalParseYamlFile
		k8sclient.DynamicClient = originalDynamicClient
		k8sclient.GetCurrentNamespace = originalGetNamespace
	}()

	k8sclient.ParseYamlFile = fakeClient.FakeParseYamlFile
	k8sclient.DynamicClient = fakeClient.FakeDynamicClient
	k8sclient.GetCurrentNamespace = fakeClient.GetCurrentNamespace

	for _, test := range testRunDeploy {
		checkDeploy(t, test)
	}
}

func checkDeploy(t *testing.T, test testDeploy) {

	expectedResources := []unstructured.Unstructured{}

	prepareFolderAndFiles(t, test)
	populateExpectedResources(t, &expectedResources, test)

	out, err := deployKnativeServiceAndEventingBindings(test.input)
	if err != nil && test.expected {
		assert.True(t, false, "Expected no error, got %v", err)
	}

	assert.Equal(t, out, test.expected, "Expected %v, got %v", test.expected, out)

	checkResourcesCreated(t, &expectedResources, test)

	if test.kogito != "" || test.knative != "" {
		undeploy(t, test, test.input.Namespace)
		checkResourcesDeleted(t, &expectedResources, test)
		common.DeleteFolderStructure(t, test.input.Path)
	}
}

func checkResourcesCreated(t *testing.T, expectedResources *[]unstructured.Unstructured, test testDeploy) {
	for _, resource := range *expectedResources {
		if result, err := checkObjectCreated(resource, test.input.Namespace); err != nil {
			t.Errorf("Error checking if resource was deleted: %v", err)
		} else {
			assert.True(t, result, "Expected resource to be created: %s", resource.GetName())
		}
	}
}

func checkResourcesDeleted(t *testing.T, expectedResources *[]unstructured.Unstructured, test testDeploy) {
	for _, r := range *expectedResources {
		if result, err := checkObjectCreated(r, test.input.Namespace); err != nil {
			t.Errorf("Error checking if resource was deleted: %v", err)
		} else {
			assert.False(t, result, "Expected resource to be deleted: %s", r.GetName())
		}
	}
}

func populateExpectedResources(t *testing.T, resources *[]unstructured.Unstructured, test testDeploy) {
	if test.knative != "" {
		if knativeResources, err := k8sclient.ParseYamlFile(filepath.Join(test.input.Path, "knative.yml")); err == nil {
			*resources = append(*resources, knativeResources...)
		} else {
			t.Errorf("❌ ERROR: Failed to parse Knative resources: %v", err)
		}
	} else {
		fmt.Printf("❌ ERROR: Failed to parse Knative resources: %v", test.knative)
	}
	if test.kogito != "" {
		if kogitoResources, err := k8sclient.ParseYamlFile(filepath.Join(test.input.Path, "kogito.yml")); err == nil {
			*resources = append(*resources, kogitoResources...)
		} else {
			t.Errorf("❌ ERROR: Failed to parse Kogito resources: %v", err)
		}
	} else {
		fmt.Printf("❌ ERROR: Failed to parse Kogito resources: %v", test.kogito)
	}
}

func prepareFolderAndFiles(t *testing.T, test testDeploy) {
	if test.input.Path == "" {
		test.input.Path = defaultPath
	}
	common.CreateFolderStructure(t, test.input.Path)
	knativeFixQuarkusVersionAndWriteToTestFolder(t, test)
	common.CopyFileInFolderStructure(t, test.input.Path, test.kogito, "kogito.yml")
}

func knativeFixQuarkusVersionAndWriteToTestFolder(t *testing.T, test testDeploy) {
	knativeBytes, err := os.ReadFile(filepath.Join("testdata", "knative.yml"))
	if err != nil {
		t.Errorf("❌ ERROR: Failed to read Knative file: %v", err)
	}
	knativeWithQuarkusVersion := strings.ReplaceAll(string(knativeBytes), "QUARKUS_VERSION", quarkusDependencies.QuarkusVersion)

	if err := afero.WriteFile(common.FS, filepath.Join(test.input.Path, "knative.yml"), []byte(knativeWithQuarkusVersion), 0644); err != nil {
		t.Errorf("Error writing to file: %s", filepath.Join(test.input.Path, "knative.yml"))
	}
}

func checkObjectCreated(obj unstructured.Unstructured, namespace string) (bool, error) {
	if namespace == "" {
		currentNamespace, err := common.GetCurrentNamespace()
		if err != nil {
			return false, fmt.Errorf("❌ ERROR: Failed to get current namespace: %v", err)
		}
		namespace = currentNamespace
	}

	client, err := k8sclient.DynamicClient()
	if err != nil {
		return false, fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}

	gvk := obj.GroupVersionKind()
	gvr, _ := meta.UnsafeGuessKindToResource(gvk)

	applyNamespace := namespace
	if obj.GetNamespace() != "" {
		applyNamespace = obj.GetNamespace()
	}

	_, err = client.Resource(gvr).Namespace(applyNamespace).Get(context.Background(), obj.GetName(), metav1.GetOptions{})
	if err != nil {
		if errors.IsNotFound(err) {
			return false, nil
		}
		return false, fmt.Errorf("❌ ERROR: Failed to get resource: %v", err)
	}
	return true, nil
}

func undeploy(t *testing.T, test testDeploy, namespace string) {
	if _, err := common.FS.Stat(filepath.Join(test.input.Path, "knative.yml")); err == nil {
		if err := common.ExecuteDelete(filepath.Join(test.input.Path, "knative.yml"), namespace); err != nil {
			t.Errorf("❌ ERROR: Undeploy failed, Knative service was not created. %v", err)
		}
	}

	if _, err := common.FS.Stat(filepath.Join(test.input.Path, "kogito.yml")); err == nil {
		if err := common.ExecuteDelete(filepath.Join(test.input.Path, "kogito.yml"), namespace); err != nil {
			t.Errorf("❌ ERROR: Undeploy failed, Kogito service was not created. %v", err)
		}
	}
}
