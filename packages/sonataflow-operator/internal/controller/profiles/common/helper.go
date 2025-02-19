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

package common

import (
	"context"
	"fmt"

	v2 "github.com/cloudevents/sdk-go/v2"
	"k8s.io/klog/v2"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
)

// GetDataIndexPlatform returns the SonataFlowPlatform that declares the DataIndex service currently configured and
// enabled for the given workflow, if any.
func GetDataIndexPlatform(ctx context.Context, cli client.Client, workflow *operatorapi.SonataFlow) (*operatorapi.SonataFlowPlatform, error) {
	var sfp *operatorapi.SonataFlowPlatform
	var err error

	if sfp, err = platform.GetActivePlatform(ctx, cli, workflow.Namespace, false); err != nil {
		return nil, err
	}
	if sfp == nil {
		klog.V(log.I).Infof("No active platform was found for workflow: %s, namespace: %s. ", workflow.Name, workflow.Namespace)
		return nil, nil
	}
	diHandler := services.NewDataIndexHandler(sfp)
	if diHandler.IsServiceEnabledInSpec() {
		return sfp, nil
	} else {
		// No DI enabled in current SFP, look for a potential SFCP
		klog.V(log.I).Infof("DataIndex is not enabled for workflow: %s, platform: %s, namespace: %s. Looking if a cluster platform exists.",
			workflow.Name, sfp.Name, workflow.Namespace)
		if sfp, err = knative.GetRemotePlatform(sfp); err != nil {
			return nil, err
		}
		if sfp == nil {
			klog.V(log.I).Infof("No cluster platform was found for workflow: %s, namespace: %s", workflow.Name, workflow.Namespace)
			return nil, nil
		}
		diHandler = services.NewDataIndexHandler(sfp)
		if diHandler.IsServiceEnabledInSpec() {
			return sfp, nil
		}
		return nil, nil
	}
}

// GetDataIndexBroker returns the broker being used by the DataIndex configured in the given SPF if any.
// Validation is performed to check that the Broker, when configured, is Ready.
func GetDataIndexBroker(sfp *operatorapi.SonataFlowPlatform) (*eventingv1.Broker, error) {
	diHandler := services.NewDataIndexHandler(sfp)
	brokerDest := diHandler.GetServiceSource()
	if brokerDest != nil && len(brokerDest.Ref.Name) > 0 {
		brokerNamespace := brokerDest.Ref.Namespace
		if len(brokerNamespace) == 0 {
			brokerNamespace = sfp.Namespace
		}
		klog.V(log.I).Infof("Broker: %s, namespace: %s is configured for DataIndex: %s in platform: %s, namespace: %s",
			brokerDest.Ref.Name, brokerNamespace, diHandler.GetServiceName(), sfp.Name, sfp.Namespace)
		if broker, err := knative.ValidateBroker(brokerDest.Ref.Name, brokerNamespace); err != nil {
			return nil, err
		} else {
			return broker, nil
		}
	}
	return nil, nil
}

func SendWorkflowDefinitionEvent(ctx context.Context, workflow *operatorapi.SonataFlow, sfp *operatorapi.SonataFlowPlatform, evt *v2.Event) error {
	var broker *eventingv1.Broker
	var err error
	var diHandler services.PlatformServiceHandler
	var url string

	diHandler = services.NewDataIndexHandler(sfp)
	klog.V(log.I).Infof("Using DataIndex: %s for workflow: %s, namespace: %s workflow to send the workflow definition event.",
		diHandler.GetServiceName(), workflow.Name, workflow.Namespace)

	if broker, err = GetDataIndexBroker(sfp); err != nil {
		return err
	}
	if broker != nil {
		if broker.Status.Address != nil && broker.Status.Address.URL != nil {
			url = broker.Status.Address.URL.String()
		} else {
			return fmt.Errorf("no ingress url was found for broker: %s, namespace: %s", broker.Name, broker.Namespace)
		}
	} else {
		klog.V(log.I).Infof("No broker is configured for DataIndex: %s in platform: %s, namespace: %s",
			diHandler.GetServiceName(), sfp.Name, sfp.Namespace)
		url = diHandler.GetLocalServiceBaseUrl() + constants.KogitoProcessDefinitionsEventsPath
	}
	klog.V(log.I).Infof("Using url: %s, to deliver the events.", url)
	if err = utils.SendCloudEventWithContext(evt, ctx, url); err != nil {
		klog.V(log.E).ErrorS(err, "an error was produced while sending workflow definition event.", "workflow", "namespace", "url", "event",
			workflow.Name, workflow.Namespace, url, evt.String())
		return err
	}
	return nil
}
