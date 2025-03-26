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

package workflowdef

import (
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"

	cloudevents "github.com/cloudevents/sdk-go/v2"
)

const SonataFlowOperatorSource = "sonataflow.org/operator"

func NewWorkflowDefinitionAvailabilityEvent(workflow *operatorapi.SonataFlow, eventSource string, serviceUrl string, available bool) *cloudevents.Event {
	var status = "unavailable"
	if available {
		status = "available"
	}
	event := cloudevents.NewEvent(cloudevents.VersionV1)
	event.SetType("ProcessDefinitionEvent")
	event.SetSource(eventSource)
	event.SetExtension("kogitoprocid", workflow.Name)
	event.SetExtension("partitionkey", workflow.Name)
	data := make(map[string]interface{})
	data["id"] = workflow.Name
	data["name"] = workflow.Name
	version := workflow.ObjectMeta.Annotations[metadata.Version]
	data["version"] = version
	data["type"] = "SW"
	data["endpoint"] = serviceUrl
	data["metadata"] = map[string]interface{}{
		"status": status,
	}
	data["nodes"] = [0]string{}
	_ = event.SetData(cloudevents.ApplicationJSON, data)
	return &event
}
