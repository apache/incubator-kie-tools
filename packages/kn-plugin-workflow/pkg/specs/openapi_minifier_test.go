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

package specs

import (
	"fmt"
	"io"
	"os"
	"path"
	"strings"
	"testing"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/getkin/kin-openapi/openapi3"
	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/util/sets"
	"k8s.io/apimachinery/pkg/util/yaml"
)

type spec struct {
	file     string
	initial  int
	minified int
}

type minifyTest struct {
	workflowFile     string
	openapiSpecFiles []spec
	specsDir         string
	subflowsDir      string
	subflows         []string
}

func TestOpenAPIMinify(t *testing.T) {
	tests := []minifyTest{
		{
			workflowFile:     "testdata/workflow.sw.yaml",                   // 4 functions, 2 of them are ref to the same openapi spec
			openapiSpecFiles: []spec{{"testdata/flink-openapi.yaml", 5, 3}}, // 5 operations, 3 must left
			specsDir:         "specs",
			subflowsDir:      "subflows",
		},
		{
			workflowFile:     "testdata/workflow.sw.json",                   // 4 functions, 2 of them are ref to the same openapi spec
			openapiSpecFiles: []spec{{"testdata/flink-openapi.yaml", 5, 3}}, // 5 operations, 3 must left
			specsDir:         "specs",
			subflowsDir:      "subflows",
		},
		{
			workflowFile:     "testdata/workflow-json-openapi.sw.json",           // 4 functions, 2 of them are ref to the same openapi spec
			openapiSpecFiles: []spec{{"testdata/flink-openapi-json.json", 5, 3}}, // 5 operations, 3 must left
			specsDir:         "specs",
			subflowsDir:      "subflows",
		},
		{
			workflowFile: "testdata/workflow2.sw.yaml", // 4 functions, 1 per openapi spec file
			openapiSpecFiles: []spec{
				{"testdata/flink1-openapi.yaml", 3, 1},
				{"testdata/flink2-openapi.yaml", 3, 1},
				{"testdata/flink3-openapi.yaml", 3, 1},
				{"testdata/flink4-openapi.yaml", 3, 1}},
			specsDir:    "specs",
			subflowsDir: "subflows",
		},
		{
			workflowFile:     "testdata/workflow-empty.sw.yaml", // check don't fail with empty workflow
			openapiSpecFiles: []spec{},
			specsDir:         "specs",
			subflowsDir:      "subflows",
		},
		{
			workflowFile:     "testdata/workflow-empty.sw.yaml", // check all operations are removed
			openapiSpecFiles: []spec{{"testdata/flink-openapi.yaml", 5, 0}},
			specsDir:         "specs",
			subflowsDir:      "subflows",
		},
		{
			workflowFile:     "testdata/workflow-mySpecsDir.sw.yaml", // check all operations are removed, with different specs dir
			openapiSpecFiles: []spec{{"testdata/flink-openapi.yaml", 5, 3}},
			specsDir:         "mySpecsDir",
			subflowsDir:      "subflows",
		},
		{
			workflowFile:     "testdata/workflow-mySpecsDir-one-finction.sw.yaml", // check all operations are removed, with different specs dir
			openapiSpecFiles: []spec{{"testdata/flink-openapi.yaml", 5, 2}},
			specsDir:         "mySpecsDir",
			subflowsDir:      "subflows",
			subflows:         []string{"testdata/subflow-mySpecsDir.sw.yaml"},
		},
		{
			workflowFile:     "testdata/workflow-empty.sw.yaml", // check all operations are removed, with different subflow dir
			openapiSpecFiles: []spec{{"testdata/flink-openapi.yaml", 5, 0}},
			specsDir:         "mySpecsDir",
			subflowsDir:      "subflows",
		},
		{
			workflowFile:     "testdata/workflow-empty2.sw.yaml", // check don't fail with workflow with non openapi functions
			openapiSpecFiles: []spec{},
			specsDir:         "specs",
			subflowsDir:      "subflows",
		},
		{
			workflowFile:     "testdata/workflow-empty2.sw.yaml", // check functions is on subflow
			openapiSpecFiles: []spec{{"testdata/flink-openapi.yaml", 5, 2}},
			specsDir:         "specs",
			subflowsDir:      "subflows",
			subflows:         []string{"testdata/subflow.sw.yaml"},
		},
		{
			workflowFile:     "testdata/workflow-empty2.sw.yaml", // check functions is on subflow, with different subflow and specs dirs
			openapiSpecFiles: []spec{{"testdata/flink-openapi.yaml", 5, 2}},
			specsDir:         "mySpecsDir",
			subflowsDir:      "mySubFlowDir",
			subflows:         []string{"testdata/subflow-mySpecsDir.sw.yaml"},
		},
	}

	current, err := os.Getwd()
	if err != nil {
		t.Fatalf("Error getting current directory: %v", err)
	}

	for _, test := range tests {
		t.Run(test.workflowFile, func(t *testing.T) {
			if err := os.Mkdir(test.specsDir, 0755); err != nil {
				t.Fatalf("Error creating specs directory: %v", err)
			}
			defer os.RemoveAll(test.specsDir)
			if err := copyFile(test.workflowFile, path.Base(test.workflowFile)); err != nil {
				t.Fatalf("Error copying workflow file: %v", err)
			}
			defer os.Remove(path.Base(test.workflowFile))
			if len(test.subflows) > 0 {
				if err := os.Mkdir(test.subflowsDir, 0755); err != nil {
					t.Fatalf("Error creating subflows directory: %v", err)
				}
				defer os.RemoveAll(test.subflowsDir)
				for _, subflow := range test.subflows {
					if err := copyFile(subflow, path.Join(test.subflowsDir, path.Base(subflow))); err != nil {
						t.Fatalf("Error copying subflow file: %v", err)
					}
				}
			}
			for _, openapiSpecFile := range test.openapiSpecFiles {
				if err := copyFile(openapiSpecFile.file, path.Join(test.specsDir, path.Base(openapiSpecFile.file))); err != nil {
					t.Fatalf("Error copying openapi spec file: %v", err)
				}
			}

			minifiedfiles, err := NewMinifier(&OpenApiMinifierOpts{
				SpecsDir:    path.Join(current, test.specsDir),
				SubflowsDir: path.Join(current, test.subflowsDir),
			}).Minify()
			if err != nil {
				t.Fatalf("Error minifying openapi specs: %v", err)
			}
			checkInitial(t, test)
			checkResult(t, test, minifiedfiles)
		})
	}
}

// checkInitial checks the initial number of operations in the openapi specs
func checkInitial(t *testing.T, test minifyTest) {
	for _, spec := range test.openapiSpecFiles {
		data, err := os.ReadFile(spec.file)
		if err != nil {
			t.Fatalf("Error reading openapi spec file: %v", err)
		}
		doc, err := openapi3.NewLoader().LoadFromData(data)
		if err != nil {
			t.Fatalf("Error loading openapi spec file: %v", err)
		}
		assert.Equalf(t, spec.initial, len(doc.Paths.Map()), "Initial number of operations in %s is not correct", spec.file)
	}
}

// checkResult checks the number of operations in the minified openapi specs
func checkResult(t *testing.T, test minifyTest, minifiedFiles map[string]string) {
	workflow, err := parseWorkflow(path.Base(test.workflowFile))
	if err != nil {
		t.Fatalf("Error parsing workflow file: %v", err)
	}

	functions := map[string]sets.Set[string]{}
	parseFunctions(t, functions, workflow, test)
	for _, subflow := range test.subflows {
		subflowWorkflow, err := parseWorkflow(subflow)
		if err != nil {
			t.Fatalf("Error parsing subflow file: %v", err)
		}
		parseFunctions(t, functions, subflowWorkflow, test)
	}

	countOfOperationInSpecs := map[string]int{}

	for file, operationSet := range functions {
		minified := minifiedFiles[file]
		data, err := os.ReadFile(minified)
		if err != nil {
			t.Fatalf("Error reading minified file: %v", err)
		}
		doc, err := openapi3.NewLoader().LoadFromData(data)
		for _, value := range doc.Paths.Map() {
			for _, operation := range value.Operations() {
				assert.True(t, operationSet.Has(operation.OperationID), "Operation %s not found in workflow", operation.OperationID)
			}
		}
		countOfOperationInSpecs[file] = len(doc.Paths.Map())
		assert.Equal(t, len(operationSet), len(doc.Paths.Map()))
	}
	for _, spec := range test.openapiSpecFiles {
		assert.Equalf(t, spec.minified, countOfOperationInSpecs[path.Base(spec.file)], "Minified number of operations in %s is not correct", spec.file)
	}

}

func parseFunctions(t *testing.T, functions map[string]sets.Set[string], workflow *v1alpha08.Flow, test minifyTest) {
	for _, function := range workflow.Functions {
		if strings.HasPrefix(function.Operation, test.specsDir) {
			trimmedPrefix := strings.TrimPrefix(function.Operation, test.specsDir+string(os.PathSeparator))
			if !strings.Contains(trimmedPrefix, "#") {
				t.Fatalf("Invalid operation format in function: %s", function.Operation)
			}
			parts := strings.SplitN(trimmedPrefix, "#", 2)
			if len(parts) != 2 {
				t.Fatalf("Invalid operation format: %s", function.Operation)
			}
			apiFileName := parts[0]
			operation := parts[1]

			if _, ok := functions[apiFileName]; !ok {
				functions[apiFileName] = sets.Set[string]{}
			}
			functions[apiFileName].Insert(operation)
		}
	}
}

func parseWorkflow(workflowFile string) (*v1alpha08.Flow, error) {
	workflow := &v1alpha08.Flow{}
	file, err := os.Open(workflowFile)
	if err != nil {
		return workflow, err
	}
	defer file.Close()

	data, err := io.ReadAll(file)
	if err != nil {
		return workflow, fmt.Errorf("failed to read workflow file %s: %w", workflowFile, err)
	}

	if err = yaml.Unmarshal(data, workflow); err != nil {
		return workflow, fmt.Errorf("failed to unmarshal workflow file %s: %w", workflowFile, err)
	}
	return workflow, nil
}

func copyFile(src, dst string) error {
	srcFile, err := os.Open(src)
	if err != nil {
		return err
	}
	defer srcFile.Close()

	dstFile, err := os.Create(dst)
	if err != nil {
		return err
	}
	defer dstFile.Close()

	_, err = io.Copy(dstFile, srcFile)
	if err != nil {
		return err
	}

	return nil
}
