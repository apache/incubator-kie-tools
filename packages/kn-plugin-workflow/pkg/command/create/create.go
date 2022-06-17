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

package create

import (
	"fmt"
	"io/ioutil"
	"os/exec"
	"strings"
	"time"
)

func Create(name string, extensions string) error {
	start := time.Now()

	// if err := pkg.CheckPreRequisitions(); err != nil {
	// 	return fmt.Errorf("checking dependencies: %w", err)
	// }

	var projectName strings.Builder
	projectName.WriteString("-DprojectArtifactId=")
	projectName.WriteString(name)

	var projectExtensions strings.Builder

	projectExtensions.WriteString("-Dextensions=kogito-quarkus-serverless-workflow,resteasy-reactive-jackson,container-image-jib")
	if len(extensions) > 0 {
		projectExtensions.WriteString(",")
		projectExtensions.WriteString(extensions)
	}

	create := exec.Command(
		"mvn",
		"io.quarkus.platform:quarkus-maven-plugin:2.9.2.Final:create",
		"-DprojectGroupId=org.acme",
		projectName.String(),
		projectExtensions.String())

	if out, err := create.CombinedOutput(); err != nil {
		fmt.Printf("Creating Serverless Workflow project: \n%s\n", string(out))
		return fmt.Errorf("Create command failed with error: %w", err)
	}

	yamlData, err := pkg.GenerateYaml(name, false)
	if err != nil {
		return fmt.Errorf("error generating yaml file")
	}

	var yamlFile strings.Builder
	yamlFile.WriteString("./")
	yamlFile.WriteString(name)
	yamlFile.WriteString("/workflow.yaml")
	err = ioutil.WriteFile(yamlFile.String(), yamlData, 0644)
	if err != nil {
		return fmt.Errorf("error creating yaml file")
	}

	fmt.Printf("Deploy file created on %s \n", yamlFile.String())

	var workflowFile strings.Builder
	workflowFile.WriteString("./")
	workflowFile.WriteString(name)
	workflowFile.WriteString("/src/main/resources/")
	workflowFile.WriteString("workflow.sw.json")
	data := []byte("{}")
	err = ioutil.WriteFile(workflowFile.String(), data, 0644)
	if err != nil {
		return fmt.Errorf("error creating workflow file")
	}

	fmt.Printf("Workflow file created on %s \n", workflowFile.String())

	finish := time.Since(start)
	fmt.Printf("🚀 Create took: %s \n", finish)
	return nil
}
