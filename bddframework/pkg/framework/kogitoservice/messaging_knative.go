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

package kogitoservice

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/client/openshift"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/framework/util"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1alpha1 "knative.dev/eventing/pkg/apis/sources/v1alpha1"
	duckv1 "knative.dev/pkg/apis/duck/v1"
	alpha1 "knative.dev/pkg/apis/duck/v1alpha1"
	"knative.dev/pkg/tracker"
)

const topicIdentifier = "kogito.kie.org/messageEventId"

// knativeMessagingDeployer implementation of messagingHandler
type knativeMessagingDeployer struct {
	messagingDeployer
}

func (k *knativeMessagingDeployer) CreateRequiredResources(service api.KogitoService) error {
	infra, err := k.fetchInfraDependency(service, isKnativeEventingResource)
	if err != nil || infra == nil {
		return err
	}

	// since we depend on Knative, let's bind a SinkBinding object to our deployment
	sinkBinding := k.newSinkBinding(service, infra)
	if err := kubernetes.ResourceC(k.Client).CreateIfNotExistsForOwner(sinkBinding, service, k.Scheme); err != nil {
		return err
	}

	// fetch for incoming topics to create our triggers
	topics, err := k.fetchTopicsAndSetCloudEventsStatus(service)
	if err != nil {
		return err
	}
	for _, topic := range topics {
		if err := k.createKnativeTriggerIfNotExists(topic, service, infra); err != nil {
			return err
		}
	}
	return nil
}

func (k *knativeMessagingDeployer) createKnativeTriggerIfNotExists(topic messagingTopic, service api.KogitoService, infra api.KogitoInfraInterface) error {
	if topic.Kind == incoming {
		if exists, err := k.triggerExists(topic, service); err != nil {
			return err
		} else if !exists {
			knativeRes := k.newTrigger(topic, service, infra)
			if err := kubernetes.ResourceC(k.Client).CreateForOwner(knativeRes, service, k.Scheme); err != nil {
				return err
			}
		}
	}
	return nil
}

// newTrigger creates a new Knative Eventing Trigger reference for the given Topic
func (k *knativeMessagingDeployer) newTrigger(t messagingTopic, service api.KogitoService, infra api.KogitoInfraInterface) *eventingv1.Trigger {
	return &eventingv1.Trigger{
		ObjectMeta: metav1.ObjectMeta{
			Name:      fmt.Sprintf("%s-listener-%s", service.GetName(), util.RandomSuffix()),
			Namespace: service.GetNamespace(),
			Labels: map[string]string{
				framework.LabelAppKey: service.GetName(),
				topicIdentifier:       t.Name,
			},
		},
		Spec: eventingv1.TriggerSpec{
			Broker: infra.GetSpec().GetResource().GetName(),
			Filter: &eventingv1.TriggerFilter{Attributes: eventingv1.TriggerFilterAttributes{"type": t.Name}},
			Subscriber: duckv1.Destination{
				Ref: &duckv1.KReference{
					Name:       service.GetName(),
					Namespace:  service.GetNamespace(),
					Kind:       openshift.KindService.Name,
					APIVersion: openshift.KindService.GroupVersion.Version,
				},
			},
		},
	}
}

// newSinkBinding creates a new SinkBinding object targeting the given KogitoInfra resource and binding the
// deployment resource owned by the given KogitoService
func (k *knativeMessagingDeployer) newSinkBinding(service api.KogitoService, infra api.KogitoInfraInterface) *sourcesv1alpha1.SinkBinding {
	ns := infra.GetSpec().GetResource().GetNamespace()
	name := infra.GetSpec().GetResource().GetName()
	if len(ns) == 0 {
		ns = service.GetNamespace()
	}
	if len(name) == 0 {
		name = service.GetName()
	}
	return &sourcesv1alpha1.SinkBinding{
		ObjectMeta: metav1.ObjectMeta{
			Name:      fmt.Sprintf("%s-publisher", service.GetName()),
			Namespace: service.GetNamespace(),
			Labels: map[string]string{
				framework.LabelAppKey: service.GetName(),
			},
		},
		Spec: sourcesv1alpha1.SinkBindingSpec{
			SourceSpec: duckv1.SourceSpec{
				Sink: duckv1.Destination{
					Ref: &duckv1.KReference{
						Name:       name,
						Namespace:  ns,
						Kind:       infrastructure.KnativeEventingBrokerKind,
						APIVersion: eventingv1.SchemeGroupVersion.String(),
					},
				},
			},
			BindingSpec: alpha1.BindingSpec{
				Subject: tracker.Reference{
					APIVersion: openshift.KindDeployment.GroupVersion.String(),
					Kind:       openshift.KindDeployment.Name,
					Namespace:  service.GetNamespace(),
					Name:       service.GetName(),
				},
			},
		},
	}
}

func (k *knativeMessagingDeployer) triggerExists(t messagingTopic, service api.KogitoService) (bool, error) {
	triggers := &eventingv1.TriggerList{}
	labels := map[string]string{
		framework.LabelAppKey: service.GetName(),
		topicIdentifier:       t.Name,
	}
	if err := kubernetes.ResourceC(k.Client).ListWithNamespaceAndLabel(service.GetNamespace(), triggers, labels); err != nil {
		return false, err
	}
	for _, trigger := range triggers.Items {
		if framework.IsOwner(&trigger, service) {
			return true, nil
		}
	}
	return false, nil
}

// IsKnativeEventingResource checks if provided KogitoInfra instance is for Knative eventing resource
func isKnativeEventingResource(instance api.KogitoInfraInterface) bool {
	return infrastructure.IsKnativeEventingResource(instance.GetSpec().GetResource().GetAPIVersion(), instance.GetSpec().GetResource().GetKind())
}
