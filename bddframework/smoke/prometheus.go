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

package main

import (
	"fmt"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	monv1 "github.com/coreos/prometheus-operator/pkg/apis/monitoring/v1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
)

const (
	// Default Prometheus service name
	defaultPrometheusService = "prometheus-operated"
)

func deployPrometheusInstance(namespace, labelName, labelValue string) error {
	GetLogger(namespace).Info("Creating Prometheus CR to spin up instance.")

	replicas := int32(1)
	prometheusCR := &monv1.Prometheus{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "prometheus",
			Namespace: namespace,
		},
		Spec: monv1.PrometheusSpec{
			Replicas: &replicas,
			// Default service account for Prometheus is created
			ServiceAccountName: "prometheus-k8s",
			ServiceMonitorSelector: &metav1.LabelSelector{
				MatchLabels: map[string]string{labelName: labelValue},
			},
		},
	}
	if err := kubernetes.ResourceC(kubeClient).Create(prometheusCR); err != nil {
		return fmt.Errorf("Error while creating Prometheus CR: %v ", err)
	}

	// Prometheus doesn't create route by default, need to create it manually
	if err := createHTTPRoute(namespace, defaultPrometheusService); err != nil {
		return fmt.Errorf("Error while creating Prometheus route: %v ", err)
	}

	return nil
}
