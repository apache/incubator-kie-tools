// Copyright 2024 Apache Software Foundation (ASF)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package properties

import (
	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/workflowdef"
	"github.com/magiconair/properties"
	cncfmodel "github.com/serverlessworkflow/sdk-go/v2/model"
)

// generateKnativeEventingWorkflowProperties returns the set of application properties required for the workflow to produce or consume
// Knative Events.
// Never nil.
func generateKnativeEventingWorkflowProperties(workflow *operatorapi.SonataFlow) (*properties.Properties, error) {
	props := properties.NewProperties()
	if workflow == nil || workflow.Spec.Sink == nil {
		props.Set(constants.KnativeHealthEnabled, "false")
		return props, nil
	}
	// verify ${K_SINK}
	props.Set(constants.KnativeHealthEnabled, "true")
	if workflowdef.ContainsEventKind(workflow, cncfmodel.EventKindProduced) {
		props.Set(constants.KogitoOutgoingEventsConnector, constants.QuarkusHTTP)
		props.Set(constants.KogitoOutgoingEventsURL, constants.KnativeInjectedEnvVar)
	}
	if workflowdef.ContainsEventKind(workflow, cncfmodel.EventKindConsumed) {
		props.Set(constants.KogitoIncomingEventsConnector, constants.QuarkusHTTP)
		var path = "/"
		if workflow.Spec.Sink.URI != nil {
			path = workflow.Spec.Sink.URI.Path
		}
		props.Set(constants.KogitoIncomingEventsPath, path)
	}
	return props, nil
}
