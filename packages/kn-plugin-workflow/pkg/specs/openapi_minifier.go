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
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"os"
	"path"
	"path/filepath"
	"reflect"
	"strings"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/getkin/kin-openapi/openapi3"
	"gopkg.in/yaml.v3"
	"k8s.io/apimachinery/pkg/util/sets"
	yamlk8s "k8s.io/apimachinery/pkg/util/yaml"
)

type OpenApiMinifier struct {
	workflows  []string
	params     *OpenApiMinifierOpts
	operations map[string]sets.Set[string]
}

type OpenApiMinifierOpts struct {
	RootPath    string
	SpecsDir    string
	SubflowsDir string
}

var workflowExtensionsType = []string{metadata.YAMLSWExtension, metadata.YMLSWExtension, metadata.JSONSWExtension}

var minifiedExtensionsType = []string{metadata.YAMLExtension, metadata.YMLExtension, metadata.JSONExtension}

// k8sFileSizeLimit defines the maximum file size allowed (e.g., Kubernetes ConfigMap size limit is 1MB)
const k8sFileSizeLimit = 3145728 // 3MB

func NewMinifier(params *OpenApiMinifierOpts) *OpenApiMinifier {
	return &OpenApiMinifier{params: params, operations: make(map[string]sets.Set[string]), workflows: []string{}}
}

// Minify removes unused operations from OpenAPI specs based on the functions used in workflows.
func (m *OpenApiMinifier) Minify() (map[string]string, error) {
	if err := m.findWorkflowFile(); err != nil {
		return nil, err
	}

	m.findSubflowsFiles(m.params)

	if err := m.fetchSpecFromFunctions(); err != nil {
		return nil, err
	}

	if err := m.validateSpecsFiles(); err != nil {
		return nil, err
	}

	minifySpecsFiles, err := m.minifySpecsFiles()
	if err != nil {
		return nil, err
	}

	return minifySpecsFiles, nil
}

func (m *OpenApiMinifier) fetchSpecFromFunctions() error {
	for _, workflowFile := range m.workflows {
		err := m.fetchSpecFromFunction(workflowFile)
		if err != nil {
			return err
		}
	}
	return nil
}

func (m *OpenApiMinifier) fetchSpecFromFunction(workflowFile string) error {
	workflow, err := m.GetWorkflow(workflowFile)
	if err != nil {
		return err
	}

	relativePath := filepath.Base(m.params.SpecsDir)

	if workflow.Functions == nil {
		return nil
	}

	for _, function := range workflow.Functions {
		if strings.HasPrefix(function.Operation, relativePath) {
			trimmedPrefix := strings.TrimPrefix(function.Operation, relativePath+"/")
			if !strings.Contains(trimmedPrefix, "#") {
				return fmt.Errorf("Invalid operation format in function: %s", function.Operation)
			}
			parts := strings.SplitN(trimmedPrefix, "#", 2)
			if len(parts) != 2 {
				return fmt.Errorf("❌ ERROR: Invalid operation format: %s", function.Operation)
			}
			apiFileName := path.Base(parts[0])
			operation := parts[1]

			if _, ok := m.operations[apiFileName]; !ok {
				m.operations[apiFileName] = sets.Set[string]{}
			}
			m.operations[apiFileName].Insert(operation)
		}
	}
	return nil
}

func (m *OpenApiMinifier) validateSpecsFiles() error {
	for specFile := range m.operations {
		specFileName := filepath.Join(m.params.SpecsDir, specFile)
		if _, err := os.Stat(specFileName); err != nil {
			return fmt.Errorf("❌ ERROR: file %s not found or can't be open", specFileName)
		}
	}
	return nil
}

func (m *OpenApiMinifier) minifySpecsFiles() (map[string]string, error) {
	minifySpecsFiles := map[string]string{}
	for key, value := range m.operations {
		minifiedSpecName, err := m.minifySpecsFile(key, value)
		if err != nil {
			return nil, err
		}
		minifySpecsFiles[key] = minifiedSpecName
	}
	return minifySpecsFiles, nil
}

func (m *OpenApiMinifier) minifySpecsFile(specFileName string, operations sets.Set[string]) (string, error) {
	specFile := filepath.Join(m.params.SpecsDir, specFileName)
	data, err := os.ReadFile(specFile)
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: failed to read OpenAPI document: %w", err)
	}

	doc, err := m.removeUnusedNodes(data, specFile, operations)
	if err != nil {
		return "", err
	}

	minifiedFile, err := m.writeMinifiedFileToDisk(specFile, doc)
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: failed to write minified file of %s : %w", specFile, err)
	}
	finalSize, err := validateSpecsFileSize(minifiedFile)
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: Minification of %s failed: %w", specFile, err)
	}

	initialSize, err := os.Stat(specFile)
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: failed to get file %s info: %w", specFile, err)
	}

	fmt.Printf("✅ Minified file %s created with %d bytes (original size: %d bytes)\n", minifiedFile, finalSize, initialSize.Size())
	return minifiedFile, nil
}

func (m *OpenApiMinifier) removeUnusedNodes(data []byte, specFileName string, operations sets.Set[string]) (*openapi3.T, error) {
	doc, err := openapi3.NewLoader().LoadFromData(data)

	collector, err := newCollector(specFileName)
	if err != nil {
		return nil, err
	}

	keep, err := collector.collect(operations)
	if err != nil {
		return nil, err
	}

	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: failed to load OpenAPI document: %w", err)
	}
	if doc.Paths == nil {
		return nil, fmt.Errorf("OpenAPI document %s has no paths", specFileName)
	}
	for key, value := range doc.Paths.Map() {
		for method, operation := range value.Operations() {
			if !operations.Has(operation.OperationID) {
				value.SetOperation(method, nil)
			}
		}
		if isPathItemEmpty(value) {
			doc.Paths.Delete(key)
		}
	}

	if doc.Components != nil {
		// note we have to skip securitySchemes, because it aren't referenced by operation directly via $ref
		components := map[string]interface{}{
			"schemas":       doc.Components.Schemas,
			"headers":       doc.Components.Headers,
			"parameters":    doc.Components.Parameters,
			"responses":     doc.Components.Responses,
			"requestBodies": doc.Components.RequestBodies,
			"examples":      doc.Components.Examples,
			"links":         doc.Components.Links,
			"callbacks":     doc.Components.Callbacks,
		}

		for key, componentMap := range components {
			if componentMap == nil {
				continue
			}

			componentValue := reflect.ValueOf(componentMap)
			for _, name := range componentValue.MapKeys() {
				nameStr := name.String()
				if !keep["components"][key].Has(nameStr) {
					componentValue.SetMapIndex(name, reflect.Value{})
				}
			}
		}
	}
	return doc, nil
}

func (m *OpenApiMinifier) findWorkflowFile() error {
	file, err := common.FindSonataFlowFile(workflowExtensionsType)
	if err != nil {
		return err
	}
	m.workflows = append(m.workflows, file)
	return nil
}

func (m *OpenApiMinifier) findSubflowsFiles(cfg *OpenApiMinifierOpts) {
	files := common.FindSonataFlowFiles(cfg.SubflowsDir, workflowExtensionsType)
	m.workflows = append(m.workflows, files...)
}

func (m *OpenApiMinifier) GetWorkflow(workflowFile string) (*v1alpha08.Flow, error) {
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

	if err = yamlk8s.Unmarshal(data, workflow); err != nil {
		return workflow, fmt.Errorf("failed to unmarshal workflow file %s: %w", workflowFile, err)
	}
	return workflow, nil
}

func (m *OpenApiMinifier) writeMinifiedFileToDisk(specFile string, doc *openapi3.T) (string, error) {
	var output []byte
	var err error
	if strings.HasSuffix(specFile, metadata.YAMLExtension) || strings.HasSuffix(specFile, metadata.YMLExtension) {
		var buf bytes.Buffer
		encoder := yaml.NewEncoder(&buf)
		encoder.SetIndent(2)
		err = encoder.Encode(doc)
		output = buf.Bytes()
	} else {
		output, err = json.Marshal(doc)
	}

	if err != nil {
		return "", fmt.Errorf("❌ ERROR: failed to marshal OpenAPI document: %w", err)
	}

	minifiedSpecFile, err := MinifiedName(specFile)
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: failed to get minified file name: %w", err)
	}

	err = os.WriteFile(minifiedSpecFile, output, 0644)
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: failed to write OpenAPI document: %w", err)
	}
	return minifiedSpecFile, nil
}

func validateSpecsFileSize(specFile string) (int64, error) {
	file, err := os.Stat(specFile)
	if err != nil {
		return -1, fmt.Errorf("❌ ERROR: failed to get file info: %w", err)
	}
	if file.Size() >= k8sFileSizeLimit {
		return -1, fmt.Errorf("❌ ERROR: Minified file %s exceeds the size limit of %d bytes", specFile, k8sFileSizeLimit)
	}
	return file.Size(), nil
}

func isPathItemEmpty(pathItem *openapi3.PathItem) bool {
	return pathItem.Get == nil &&
		pathItem.Put == nil &&
		pathItem.Post == nil &&
		pathItem.Delete == nil &&
		pathItem.Options == nil &&
		pathItem.Head == nil &&
		pathItem.Patch == nil &&
		pathItem.Trace == nil
}

func MinifiedName(specFile string) (string, error) {
	for _, ext := range minifiedExtensionsType {
		if strings.HasSuffix(specFile, ext) {
			return strings.TrimSuffix(specFile, ext) + ".min" + ext, nil
		}
	}
	return "", fmt.Errorf("❌ ERROR: unknown file extension: %s", specFile)
}
