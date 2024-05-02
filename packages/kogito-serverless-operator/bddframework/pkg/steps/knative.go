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
	"github.com/cucumber/godog"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
)

func registerKnativeSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install Knative eventing$`, data.installKnativeEventing)
	ctx.Step(`^Deploy Knative Broker "([^"]*)"$`, data.deployKnativeBroker)
	ctx.Step(`^Create Knative Trigger "([^"]*)" receiving events from Broker "([^"]*)" delivering to Service "([^"]*)"$`, data.createKnativeTrigger)
	ctx.Step(`^Deploy Event display "([^"]*)"$`, data.deployEventDisplay)
}

func (data *Data) installKnativeEventing() error {
	return installers.GetKnativeEventingInstaller().Install(data.Namespace)
}

func (data *Data) deployKnativeBroker(name string) error {
	if err := framework.DeployBroker(data.Namespace, name); err != nil {
		return err
	}

	return framework.WaitForBrokerResource(data.Namespace, name, 3)
}

func (data *Data) createKnativeTrigger(name, brokerName, serviceName string) error {
	if err := framework.CreateTrigger(data.Namespace, name, brokerName, serviceName); err != nil {
		return err
	}

	return framework.WaitForTrigger(data.Namespace, name, 3)
}

func (data *Data) deployEventDisplay(name string) error {
	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: data.Namespace,
		},
		Spec: appsv1.DeploymentSpec{
			Selector: &metav1.LabelSelector{MatchLabels: map[string]string{"app": name}},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: map[string]string{"app": name},
				},
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{
						{
							Name:  name,
							Image: "gcr.io/knative-releases/knative.dev/eventing-contrib/cmd/event_display",
						},
					},
				},
			},
		},
	}

	if err := framework.CreateObject(deployment); err != nil {
		return err
	}

	service := &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: data.Namespace,
		},
		Spec: corev1.ServiceSpec{
			Ports: []corev1.ServicePort{
				{
					Protocol:   "TCP",
					Port:       80,
					TargetPort: intstr.FromInt(8080),
				},
			},
			Selector: deployment.Spec.Selector.MatchLabels,
		},
	}

	if err := framework.CreateObject(service); err != nil {
		return err
	}
	return framework.WaitForPodsWithLabel(data.Namespace, "app", name, 1, 3)
}
