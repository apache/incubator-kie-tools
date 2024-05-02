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

package framework

import (
	"fmt"

	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	"knative.dev/pkg/apis"
	duckv1 "knative.dev/pkg/apis/duck/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
)

// DeployBroker deploys Knative Broker
func DeployBroker(namespace, name string) error {
	GetLogger(namespace).Info("Creating Knative Broker", "name", name)

	broker := &eventingv1.Broker{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
	}

	if err := kubernetes.ResourceC(kubeClient).Create(broker); err != nil {
		return fmt.Errorf("Error while creating Broker: %v ", err)
	}

	return nil
}

// WaitForBrokerResource waits until the Broker ready status is True
func WaitForBrokerResource(namespace, name string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Broker %s status to be Success", name), timeoutInMin,
		func() (bool, error) {
			broker, err := getBrokerResource(namespace, name)
			if err != nil {
				return false, err
			}
			if broker == nil {
				return false, nil
			}

			for _, condition := range broker.Status.Conditions {
				if condition.Type == apis.ConditionReady && condition.Status == corev1.ConditionTrue {
					return true, nil
				}
			}
			return false, nil
		})
}

// retrieves the Broker resource
func getBrokerResource(namespace, name string) (*eventingv1.Broker, error) {
	broker := &eventingv1.Broker{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, broker); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for Broker %s: %v ", name, err)
	} else if !exists {
		return nil, nil
	}
	return broker, nil
}

// CreateTrigger creates Knative Trigger
func CreateTrigger(namespace, name, brokerName, serviceName string) error {
	GetLogger(namespace).Info("Creating Knative Trigger", "name", name, "Broker name", brokerName, "Service name", serviceName)

	// Check that the service exists
	if _, err := GetService(namespace, serviceName); err != nil {
		return err
	}

	trigger := &eventingv1.Trigger{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
		Spec: eventingv1.TriggerSpec{
			Broker: brokerName,
			Subscriber: duckv1.Destination{
				Ref: &duckv1.KReference{
					APIVersion: corev1.SchemeGroupVersion.Version,
					Kind:       "Service",
					Name:       serviceName,
				},
			},
		},
	}

	if err := kubernetes.ResourceC(kubeClient).Create(trigger); err != nil {
		return fmt.Errorf("Error while creating Trigger: %v ", err)
	}

	return nil
}

// WaitForTrigger waits until the Trigger ready status is True
func WaitForTrigger(namespace, name string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Trigger %s status to be Success", name), timeoutInMin,
		func() (bool, error) {
			trigger, err := getTriggerResource(namespace, name)
			if err != nil {
				return false, err
			}
			if trigger == nil {
				return false, nil
			}

			for _, condition := range trigger.Status.Conditions {
				if condition.Type == apis.ConditionReady && condition.Status == corev1.ConditionTrue {
					return true, nil
				}
			}
			return false, nil
		})
}

// retrieves the Trigger resource
func getTriggerResource(namespace, name string) (*eventingv1.Trigger, error) {
	trigger := &eventingv1.Trigger{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, trigger); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for Trigger %s: %v ", name, err)
	} else if !exists {
		return nil, nil
	}
	return trigger, nil
}
