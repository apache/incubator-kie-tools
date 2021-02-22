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

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	grafanav1 "github.com/integr8ly/grafana-operator/v3/pkg/apis/integreatly/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
)

const (
	// Default Grafana service name
	defaultGrafanaService = "grafana-service"
)

// DeployGrafanaInstance deploys an instance of Grafana watching for label with specific name and value
func DeployGrafanaInstance(namespace, labelName, labelValue string) error {
	GetLogger(namespace).Info("Creating Grafana CR to spin up instance.")

	grafanaCR := &grafanav1.Grafana{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "grafana",
			Namespace: namespace,
		},
		Spec: grafanav1.GrafanaSpec{
			Config: grafanav1.GrafanaConfig{
				AuthAnonymous: &grafanav1.GrafanaConfigAuthAnonymous{
					Enabled: getBool(true),
				},
			},
			DashboardLabelSelector: []*metav1.LabelSelector{
				{
					MatchLabels: map[string]string{labelName: labelValue},
				},
			},
			Compat: &grafanav1.GrafanaCompat{},
		},
	}
	if err := kubernetes.ResourceC(kubeClient).Create(grafanaCR); err != nil {
		return fmt.Errorf("Error while creating Grafana CR: %v ", err)
	}

	// Grafana creates HTTPS routes by default, need to create it manually
	if err := createHTTPRoute(namespace, defaultGrafanaService); err != nil {
		return fmt.Errorf("Error while creating Grafana route: %v ", err)
	}

	return nil
}

func getBool(b bool) *bool {
	return &b
}
