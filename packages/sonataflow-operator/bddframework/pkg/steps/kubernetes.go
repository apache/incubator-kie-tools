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

package steps

import (
	"github.com/cucumber/godog"
	v1 "k8s.io/api/core/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/bddframework/pkg/steps/mappers"
)

/*
	DataTable for Deployment runtime resources:
	| runtime-request  | cpu/memory     | value  |
	| runtime-limit    | cpu/memory     | value  |
*/

func registerKubernetesSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Namespace is created$`, data.namespaceIsCreated)
	ctx.Step(`^Namespace is deleted$`, data.namespaceIsDeleted)

	ctx.Step(`^CLI create namespace$`, data.cliCreateNamespace)
	ctx.Step(`^CLI use namespace$`, data.cliUseNamespace)

	ctx.Step(`^Deployment "([^"]*)" has (\d+) pods with runtime resources within (\d+) minutes:$`, data.deploymentHasResourcesWithinMinutes)

	// Logging steps
	ctx.Step(`^Deployment "([^"]*)" pods log contains text "([^"]*)" within (\d+) minutes$`, data.deploymentPodsLogContainsTextWithinMinutes)
}

func (data *Data) namespaceIsCreated() error {
	return framework.CreateNamespace(data.Namespace)
}

func (data *Data) namespaceIsDeleted() error {
	if exists, err := framework.IsNamespace(data.Namespace); err != nil {
		return err
	} else if exists {
		err := framework.DeleteNamespace(data.Namespace)
		if err != nil {
			return err
		}
		// wait for deletion complete
		err = framework.WaitForOnOpenshift(data.Namespace, "namespace is deleted", 2,
			func() (bool, error) {
				exists, err := framework.IsNamespace(data.Namespace)
				return !exists, err
			})
		if err != nil {
			return err
		}
	}
	return nil
}

func (data *Data) cliCreateNamespace() error {
	_, err := framework.ExecuteCliCommand(data.Namespace, "new-project", data.Namespace)
	return err
}

func (data *Data) cliUseNamespace() error {
	_, err := framework.ExecuteCliCommand(data.Namespace, "use-project", data.Namespace)
	return err
}

func (data *Data) deploymentHasResourcesWithinMinutes(dName string, podNb, timeoutInMin int, table *godog.Table) error {
	if err := framework.WaitForDeploymentRunning(data.Namespace, dName, podNb, timeoutInMin); err != nil {
		return err
	}

	runtime := &v1.ResourceRequirements{Limits: v1.ResourceList{}, Requests: v1.ResourceList{}}
	err := mappers.MapRuntimeResourceRequirementsTable(table, runtime)

	if err != nil {
		return err
	}

	return framework.WaitForPodsByDeploymentToHaveResources(data.Namespace, dName, *runtime, timeoutInMin)
}

func (data *Data) deploymentPodsLogContainsTextWithinMinutes(dName, logText string, timeoutInMin int) error {
	return framework.WaitForAllPodsByDeploymentToContainTextInLog(data.Namespace, dName, logText, timeoutInMin)
}
