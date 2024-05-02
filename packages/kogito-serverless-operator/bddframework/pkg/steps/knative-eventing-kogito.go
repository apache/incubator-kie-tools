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
	"fmt"

	"github.com/cucumber/godog"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
)

func registerKnativeEventingKogitoSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install Knative eventing KogitoSource$`, data.installKnativeEventingKogitoSource)
	ctx.Step(`^Create (quarkus|springboot) KogitoSource "([^"]*)" sinking events to Broker "([^"]*)" from runtime registry$`, data.createKogitoSource)
}

func (data *Data) installKnativeEventingKogitoSource() error {
	return installers.GetKnativeEventingKogitoInstaller().Install(data.Namespace)
}

func (data *Data) createKogitoSource(runtimeType, kogitoSourceName, brokerName string) error {
	imageTag := data.ScenarioContext[GetBuiltRuntimeImageTagContextKey(kogitoSourceName)]

	// TODO: Quick workaround, needs to be refactored once KogitoSource API is moved to separate module
	kogitoSourceContent := `apiVersion: kogito.knative.dev/v1alpha1
kind: KogitoSource
metadata:
  name: process-knative-quickstart-quarkus
spec:
  image: %s
  sink:
    ref:
      apiVersion: eventing.knative.dev/v1
      kind: Broker
      name: default`

	filteredKogitoSourceContent := fmt.Sprintf(kogitoSourceContent, imageTag)
	tempFilePath, err := framework.CreateTemporaryFile("kogito-source*.yaml", filteredKogitoSourceContent)
	if err != nil {
		return err
	}
	output, err := framework.CreateCommand("oc", "apply", "-n", data.Namespace, "-f", tempFilePath).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Applying KogitoSource failed, output: %s", output))
	}

	return err
}
