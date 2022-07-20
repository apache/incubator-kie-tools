/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package command

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
)

type WorkflowStates struct {
	Name    string   `json:"name"`
	Type    string   `json:"type"`
	Actions []string `json:"actions"`
	End     bool     `json:"end"`
}

type Workflow struct {
	Id          string           `json:"id"`
	SpecVersion string           `json:"specVersion"`
	Name        string           `json:"name"`
	Start       string           `json:"start"`
	States      []WorkflowStates `json:"states"`
}

func getWorkflowTemplate() (workflowJsonByte []byte, err error) {
	workflowStates := WorkflowStates{
		Name:    "HelloWorld",
		Type:    "operation",
		Actions: []string{},
		End:     true,
	}

	workflow := Workflow{
		Id:          "hello",
		SpecVersion: "0.8.0",
		Name:        "Hello World",
		Start:       "HelloWorld",
		States:      []WorkflowStates{workflowStates},
	}

	workflowJsonByte, err = json.MarshalIndent(workflow, "", "  ")
	if err != nil {
		fmt.Println("ERROR: marshaling the workflow json file.")
	}
	return
}

func CreateWorkflow(workflowFilePath string) (err error) {
	workflowFileData, err := getWorkflowTemplate()
	if err != nil {
		return err
	}

	err = ioutil.WriteFile(workflowFilePath, workflowFileData, 0644)
	if err != nil {
		fmt.Println("ERROR: writing the workflow json file.")
		return err
	}

	fmt.Printf("Workflow file created on %s \n", workflowFilePath)
	return
}
