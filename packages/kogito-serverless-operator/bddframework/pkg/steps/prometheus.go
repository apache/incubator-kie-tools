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

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
)

func registerPrometheusSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Prometheus Operator is deployed$`, data.prometheusOperatorIsDeployed)
	ctx.Step(`^Prometheus instance is deployed, monitoring services with label name "([^"]*)" and value "([^"]*)"$`, data.prometheusInstanceIsDeployed)
}

func (data *Data) prometheusOperatorIsDeployed() error {
	return installers.GetPrometheusInstaller().Install(data.Namespace)
}

func (data *Data) prometheusInstanceIsDeployed(labelName, labelValue string) error {
	err := framework.DeployPrometheusInstance(data.Namespace, labelName, labelValue)
	if err != nil {
		return err
	}
	return framework.WaitForPodsWithLabel(data.Namespace, "app.kubernetes.io/name", "prometheus", 1, 3)
}
