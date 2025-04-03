// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package eventing

import (
	"context"
	"fmt"

	duckv1 "knative.dev/pkg/apis/duck/v1"

	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
)

// GetWorkflowDefinitionEventsTargetURL returns the target url that must be used to send the workflow definition events.
func GetWorkflowDefinitionEventsTargetURL(cli client.Client, workflow *operatorapi.SonataFlow) (string, error) {
	var err error
	var sfp *operatorapi.SonataFlowPlatform
	var sink *duckv1.Destination
	var uri string

	if sfp, err = platform.GetActivePlatform(context.Background(), cli, workflow.Namespace, false); err != nil {
		return "", fmt.Errorf("failed to get active platform for workflow: %s, namespace: %s : %v", workflow.Name, workflow.Namespace, err)
	}
	if sfp == nil {
		klog.V(log.D).Infof("No active platform was found to calculate the workflow definition events target url for workflow: %s, namespace: %s.", workflow.Name, workflow.Namespace)
		return "", err
	}
	diHandler := services.NewDataIndexHandler(sfp)
	if !diHandler.IsServiceEnabled() {
		klog.V(log.D).Infof("DataIndex is not enabled for current workflow: %s, namespace: %s, neither in current platform: %s, or by a cluster platform reference.", workflow.Name, workflow.Namespace, sfp.Name)
		return "", nil
	}

	// First check if the workflow is connected with the knative eventing system.
	if sink, err = knative.GetWorkflowSink(workflow, sfp); err != nil {
		return "", fmt.Errorf("failed to look for a potential sink configuration for workflow: %s, namespace: %s : %v", workflow.Name, workflow.Namespace, err)
	}
	if sink != nil {
		// Workflow is connected via with knative eventing by using an operator managed SinkBinding.
		if sinkURI, err := knative.GetSinkURI(*sink); err != nil {
			return "", err
		} else {
			uri = sinkURI.String()
		}
	} else {
		// Workflow is connected via direct http invocation with the DI.
		uri = diHandler.GetServiceBaseUrl() + constants.KogitoProcessDefinitionsEventsPath
	}
	return uri, nil
}
