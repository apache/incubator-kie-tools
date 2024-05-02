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
	infinispan "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/infinispan/v1"

	"github.com/cucumber/godog"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps/mappers"
)

/*
	DataTable for Infinispan:
	| username | developer |
	| password | mypass    |
*/

const (
	externalInfinispanSecret = "external-infinispan-secret"
)

var performanceInfinispanContainerSpec = infinispan.InfinispanContainerSpec{
	ExtraJvmOpts: "-Xmx2G",
	Memory:       "3Gi",
	CPU:          "1",
}

func registerInfinispanSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Infinispan Operator is deployed$`, data.infinispanOperatorIsDeployed)
	ctx.Step(`^Infinispan instance "([^"]*)" has (\d+) (?:pod|pods) running within (\d+) (?:minute|minutes)$`, data.infinispanInstanceHasPodsRunningWithinMinutes)
	ctx.Step(`^Infinispan instance "([^"]*)" is deployed with configuration:$`, data.infinispanInstanceIsDeployedWithConfiguration)
	ctx.Step(`^Infinispan instance "([^"]*)" is deployed for performance within (\d+) minute\(s\) with configuration:$`, data.infinispanInstanceIsDeployedForPerformanceWithinMinutesWithConfiguration)
	ctx.Step(`^Scale Infinispan instance "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleInfinispanInstanceToPodsWithinMinutes)
}

func (data *Data) infinispanOperatorIsDeployed() error {
	installer, err := installers.GetInfinispanInstaller()
	if err != nil {
		return err
	}
	return installer.Install(data.Namespace)
}

func (data *Data) infinispanInstanceHasPodsRunningWithinMinutes(name string, numberOfPods, timeOutInMin int) error {
	return framework.WaitForPodsWithLabels(data.Namespace, framework.GetRunningInfinispanPodLabels(name), numberOfPods, timeOutInMin)
}

func (data *Data) infinispanInstanceIsDeployedWithConfiguration(name string, table *godog.Table) error {
	if err := createInfinispanSecret(data.Namespace, externalInfinispanSecret, table); err != nil {
		return err
	}

	infinispan := framework.GetInfinispanStub(data.Namespace, name, externalInfinispanSecret)

	if err := framework.DeployInfinispanInstance(data.Namespace, infinispan); err != nil {
		return err
	}

	return framework.WaitForPodsWithLabels(data.Namespace, framework.GetRunningInfinispanPodLabels(name), 1, 3)
}

func (data *Data) infinispanInstanceIsDeployedForPerformanceWithinMinutesWithConfiguration(name string, timeOutInMin int, table *godog.Table) error {
	if err := createInfinispanSecret(data.Namespace, externalInfinispanSecret, table); err != nil {
		return err
	}

	infinispan := framework.GetInfinispanStub(data.Namespace, name, externalInfinispanSecret)
	// Add performance-specific container spec
	infinispan.Spec.Container = performanceInfinispanContainerSpec

	if err := framework.DeployInfinispanInstance(data.Namespace, infinispan); err != nil {
		return err
	}

	return framework.WaitForInfinispanPodsToBeRunningWithConfig(data.Namespace, performanceInfinispanContainerSpec, 1, timeOutInMin)
}

func (data *Data) scaleInfinispanInstanceToPodsWithinMinutes(name string, nbPods, timeoutInMin int) error {
	err := framework.SetInfinispanReplicas(data.Namespace, name, nbPods)
	if err != nil {
		return err
	}
	return framework.WaitForPodsWithLabels(data.Namespace, framework.GetRunningInfinispanPodLabels(name), nbPods, timeoutInMin)
}

// Misc methods

func createInfinispanSecret(namespace, secretName string, table *godog.Table) error {
	credentials := make(map[string]string)
	credentials["operator"] = "supersecretoperatorpassword" // Credentials required by Infinispan operator

	if username, password, err := mappers.MapInfinispanCredentialsFromTable(table); err != nil {
		return err
	} else if len(username) > 0 {
		// User defined credentials
		credentials[username] = password
	}

	return framework.CreateInfinispanSecret(namespace, secretName, credentials)
}
