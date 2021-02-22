// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package framework

import (
	"fmt"
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/kogitosupportingservice"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

// DecisionsResponse represents the decision response
type DecisionsResponse struct {
	Status    string     `json:"status"`
	Saliences []Salience `json:"saliencies"`
}

// Salience represents the salience response
type Salience struct {
	OutcomeName string `json:"outcomeName"`
}

const (
	salienceSuccess = "SUCCEEDED"
	executionsPath  = "executions"
	decisionsPath   = "executions/decisions/%s/explanations/saliencies"
)

type executionsResponse struct {
	Executions []execution `json:"headers"`
}

type execution struct {
	ExecutionID        string `json:"executionId"`
	ExecutedModelName  string `json:"executedModelName"`
	ExecutionSucceeded bool   `json:"executionSucceeded"`
}

// InstallKogitoTrustyService install the Kogito Trusty service
func InstallKogitoTrustyService(namespace string, installerType InstallerType, trusty *bddtypes.KogitoServiceHolder) error {
	// Persistence is already configured internally by the Trusty service, so we don't need to add any additional persistence step here.
	return InstallService(trusty, installerType, "trusty")
}

// WaitForKogitoTrustyService wait for Kogito Trusty to be deployed
func WaitForKogitoTrustyService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getTrustyServiceName(), replicas, timeoutInMin)
}

func getTrustyServiceName() string {
	return kogitosupportingservice.DefaultTrustyName
}

// GetKogitoTrustyResourceStub Get basic KogitoTrusty stub with all needed fields initialized
func GetKogitoTrustyResourceStub(namespace string, replicas int) *v1beta1.KogitoSupportingService {
	return &v1beta1.KogitoSupportingService{
		ObjectMeta: NewObjectMetadata(namespace, getTrustyServiceName()),
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType:       api.TrustyAI,
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetTrustyImageTag(), kogitosupportingservice.DefaultTrustyImageName),
		},
		Status: v1beta1.KogitoSupportingServiceStatus{
			KogitoServiceStatus: NewKogitoServiceStatus(),
		},
	}
}

// GetKogitoTrustyDecisionsByExecutionName gets the decisions made by a given execution name
func GetKogitoTrustyDecisionsByExecutionName(namespace, executionName string, timeoutInMin int) (*DecisionsResponse, error) {

	executionID, err := getKogitoTrustyExecutionIDByExecutionName(namespace, executionName, timeoutInMin)
	if err != nil {
		return nil, err
	}

	uri, err := WaitAndRetrieveEndpointURI(namespace, getTrustyServiceName())
	if err != nil {
		return nil, err
	}

	// Retrieve explainability result for the given execution ID
	decisionsPathWithExecutionID := fmt.Sprintf(decisionsPath, executionID)
	decisionsResponse := new(DecisionsResponse)
	requestInfo := NewGETHTTPRequestInfo(uri, decisionsPathWithExecutionID)
	err = WaitForOnOpenshift(namespace, fmt.Sprintf("Get decisions by execution ID %s", executionID), timeoutInMin,
		func() (bool, error) {
			if err := ExecuteHTTPRequestWithUnmarshalledResponse(namespace, requestInfo, &decisionsResponse); err != nil {
				return false, err
			}

			if decisionsResponse.Status != salienceSuccess {
				return false, fmt.Errorf("Decision for execution %s was not success", executionName)
			}

			return true, nil
		})

	return decisionsResponse, err
}

func getKogitoTrustyExecutionIDByExecutionName(namespace, executionName string, timeoutInMin int) (string, error) {
	uri, err := WaitAndRetrieveEndpointURI(namespace, getTrustyServiceName())
	if err != nil {
		return "", err
	}

	var executionID string
	executionsResponse := new(executionsResponse)
	requestInfo := NewGETHTTPRequestInfo(uri, executionsPath)
	err = WaitForOnOpenshift(namespace, fmt.Sprintf("Get execution ID by name %s", executionName), timeoutInMin,
		func() (bool, error) {
			if err := ExecuteHTTPRequestWithUnmarshalledResponse(namespace, requestInfo, &executionsResponse); err != nil {
				return false, err
			}

			GetLogger(namespace).Debug("Got execution response", "executionResponse", executionsResponse)

			for _, execution := range executionsResponse.Executions {
				if execution.ExecutedModelName == executionName {
					if !execution.ExecutionSucceeded {
						return false, fmt.Errorf("Execution %s was unsuccessful", executionName)
					}
					executionID = execution.ExecutionID
				}
			}

			if len(executionID) == 0 {
				return false, fmt.Errorf("Execution %s not found yet", executionName)
			}

			return true, nil
		})

	return executionID, err
}
