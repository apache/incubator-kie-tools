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
	"net/url"
	"path/filepath"
	"strings"

	"github.com/cucumber/godog"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test/utils"
)

func registerSonataFlowSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^SonataFlow orderprocessing example is deployed$`, data.sonataFlowOrderProcessingExampleIsDeployed)
	ctx.Step(`^SonataFlow "([^"]*)" has the condition "(Running|Succeed|Built)" set to "(True|False|Unknown)" within (\d+) minutes?$`, data.sonataFlowHasTheConditionSetToWithinMinutes)
	ctx.Step(`^SonataFlow "([^"]*)" is addressable within (\d+) minutes?$`, data.sonataFlowIsAddressableWithinMinutes)
	ctx.Step(`^HTTP POST request as Cloud Event on SonataFlow "([^"]*)" is successful within (\d+) minutes? with path "([^"]*)", headers "([^"]*)" and body:$`, data.httpPostRequestAsCloudEventOnSonataFlowIsSuccessfulWithinMinutesWithPathHeadersAndBody)
}

func (data *Data) sonataFlowOrderProcessingExampleIsDeployed() error {
	projectDir, _ := utils.GetProjectDir()
	projectDir = strings.Replace(projectDir, "/testbdd", "", -1)

	// TODO or kubectl
	out, err := framework.CreateCommand("oc", "apply", "-f", filepath.Join(projectDir,
		test.GetSonataFlowE2eOrderProcessingFolder()), "-n", data.Namespace).Execute()

	if err != nil {
		framework.GetLogger(data.Namespace).Error(err, fmt.Sprintf("Applying SonataFlow failed, output: %s", out))
	}
	return err
}

func (data *Data) sonataFlowHasTheConditionSetToWithinMinutes(name, conditionType, conditionStatus string, timeoutInMin int) error {
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("SonataFlow %s has the condition %s with status %s", name, conditionType, conditionStatus), timeoutInMin,
		func() (bool, error) {
			if sonataFlow, err := getSonataFlow(data.Namespace, name); err != nil {
				return false, err
			} else if sonataFlow == nil {
				return false, nil
			} else {
				condition := sonataFlow.Status.GetCondition(api.ConditionType(conditionType))
				return condition != nil && condition.Status == v1.ConditionStatus(conditionStatus), nil
			}
		})
}

func (data *Data) sonataFlowIsAddressableWithinMinutes(name string, timeoutInMin int) error {
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("SonataFlow %s is addressable", name), timeoutInMin,
		func() (bool, error) {
			sonataFlow, err := getSonataFlow(data.Namespace, name)
			if err != nil {
				return false, err
			} else if sonataFlow == nil {
				return false, fmt.Errorf("No SonataFlow found with name %s in namespace %s", name, data.Namespace)
			}

			if sonataFlow.Status.Address.URL == nil {
				return false, fmt.Errorf("SonataFlow %s does NOT have an address", name)
			}

			if _, err := url.ParseRequestURI(sonataFlow.Status.Address.URL.String()); err != nil {
				return false, fmt.Errorf("SonataFlow %s address '%s' is not valid: %w", name, sonataFlow.Status.Address.URL, err)
			}

			return true, nil
		})
}

func getSonataFlow(namespace, name string) (*v1alpha08.SonataFlow, error) {
	sonataFlow := &v1alpha08.SonataFlow{}
	if exists, err := framework.GetObjectWithKey(types.NamespacedName{Namespace: namespace, Name: name}, sonataFlow); err != nil {
		return nil, fmt.Errorf("Error while trying to look for SonataFlow %s: %w ", name, err)
	} else if !exists {
		return nil, nil
	}
	return sonataFlow, nil
}

func (data *Data) httpPostRequestAsCloudEventOnSonataFlowIsSuccessfulWithinMinutesWithPathHeadersAndBody(name string, timeoutInMin int, path, headersContent string, body *godog.DocString) error {
	path = data.ResolveWithScenarioContext(path)
	bodyContent := data.ResolveWithScenarioContext(body.Content)
	framework.GetLogger(data.Namespace).Debug("httpPostRequestAsCloudEventOnSonataFlowIsSuccessfulWithinMinutesWithPathHeadersAndBody", "sonataflow", name, "path", path, "bodyMediaType", body.MediaType, "bodyContent", bodyContent, "timeout", timeoutInMin)
	sonataFlow, err := getSonataFlow(data.Namespace, name)
	if err != nil {
		return err
	} else if sonataFlow == nil {
		return fmt.Errorf("No SonataFlow found with name %s in namespace %s", name, data.Namespace)
	}
	sonataFlowUri := sonataFlow.Status.Endpoint
	uri := strings.TrimSuffix(sonataFlowUri.String(), sonataFlowUri.Path)
	headers, err := parseHeaders(headersContent)
	if err != nil {
		return err
	}

	requestInfo := framework.NewPOSTHTTPRequestInfoWithHeaders(uri, path, headers, body.MediaType, bodyContent)
	return framework.WaitForSuccessfulHTTPRequest(data.Namespace, requestInfo, timeoutInMin)
}

func parseHeaders(headersContent string) (map[string]string, error) {
	headers := make(map[string]string)

	for _, headerEntry := range strings.Split(headersContent, ",") {
		keyValuePair := strings.Split(headerEntry, "=")

		if len(keyValuePair) == 1 {
			return nil, fmt.Errorf("Header key and value need to be separated by `=`, parsed header: %s", headerEntry)
		}
		if len(keyValuePair) > 2 {
			return nil, fmt.Errorf("Found multiple `=` in parsed header: %s", headerEntry)
		}

		headers[keyValuePair[0]] = strings.TrimSpace(keyValuePair[1])
	}

	return headers, nil
}
