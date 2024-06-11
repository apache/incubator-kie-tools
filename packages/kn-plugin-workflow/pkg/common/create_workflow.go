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

package common

import (
	"encoding/json"
	"fmt"
	"github.com/spf13/afero"
	"gopkg.in/yaml.v2"
)

type WorkflowStates struct {
	Name string            `json:"name"`
	Type string            `json:"type"`
	Data map[string]string `json:"data"`
	End  bool              `json:"end"`
}

type Workflow struct {
	Id          string           `json:"id"`
	Version     string           `json:"version"`
	SpecVersion string           `json:"specVersion" yaml:"specVersion"`
	Name        string           `json:"name"`
	Description string           `json:"description"`
	Start       string           `json:"start"`
	States      []WorkflowStates `json:"states"`
}

func GetWorkflowTemplate(yamlWorkflow bool) (workflowByte []byte, err error) {
	workflowStates := WorkflowStates{
		Name: "HelloWorld",
		Type: "inject",
		Data: map[string]string{
			"message": "Hello World",
		},
		End: true,
	}

	workflow := Workflow{
		Id:          "hello",
		Version:     "1.0",
		SpecVersion: "0.8.0",
		Name:        "Hello World",
		Description: "Description",
		Start:       "HelloWorld",
		States:      []WorkflowStates{workflowStates},
	}

	if yamlWorkflow {
		workflowByte, err = yaml.Marshal(workflow)
		if err != nil {
			return nil, fmt.Errorf("error marshaling the workflow file. %w", err)
		}
	} else {
		workflowByte, err = json.MarshalIndent(workflow, "", "  ")
		if err != nil {
			return nil, fmt.Errorf("error marshaling the workflow file. %w", err)
		}
	}

	return workflowByte, nil
}

func CreateWorkflow(workflowFilePath string, yamlWorkflow bool) (err error) {

	workflowByte, err := GetWorkflowTemplate(yamlWorkflow)
	err = afero.WriteFile(FS, workflowFilePath, workflowByte, 0644)
	if err != nil {
		return fmt.Errorf("error writing the workflow file: %w", err)
	}

	fmt.Printf("Workflow file created at %s \n", workflowFilePath)
	return nil
}
