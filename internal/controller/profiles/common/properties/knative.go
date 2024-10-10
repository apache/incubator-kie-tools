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

package properties

import (
	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/workflowdef"
	"github.com/magiconair/properties"
	cncfmodel "github.com/serverlessworkflow/sdk-go/v2/model"
)

// generateKnativeEventingWorkflowProperties returns the set of application properties required for the workflow to produce or consume
// Knative Events.
// Never nil.
func generateKnativeEventingWorkflowProperties(workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) (*properties.Properties, error) {
	props := properties.NewProperties()
	props.Set(constants.KnativeHealthEnabled, "false")
	sink, err := knative.GetWorkflowSink(workflow, platform)
	if err != nil {
		return nil, err
	}
	if workflow == nil || sink == nil {
		props.Set(constants.KnativeHealthEnabled, "false")
		return props, nil
	}
	props.Set(constants.KnativeHealthEnabled, "true")
	if workflowdef.ContainsEventKind(workflow, cncfmodel.EventKindProduced) {
		props.Set(constants.KogitoOutgoingEventsConnector, constants.QuarkusHTTP)
		props.Set(constants.KogitoOutgoingEventsURL, constants.KnativeInjectedEnvVar)
	}
	if workflowdef.ContainsEventKind(workflow, cncfmodel.EventKindConsumed) {
		props.Set(constants.KogitoIncomingEventsConnector, constants.QuarkusHTTP)
		props.Set(constants.KogitoIncomingEventsPath, "/")
	}
	return props, nil
}
