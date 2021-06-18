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
	"k8s.io/apimachinery/pkg/types"

	grafanav1 "github.com/integr8ly/grafana-operator/v3/pkg/apis/integreatly/v1alpha1"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
)

const (
	// Default Grafana service name
	defaultGrafanaService = "grafana-service"
	// Default Grafana deployment name
	defaultGrafanaDeployment = "grafana-deployment"
)

var grafanaImages = map[string]string{
	"docker.io/grafana/grafana:7.3.10": "docker.io/grafana/grafana@sha256:5aa6a6493ef5f03be209f4cb9cef07a5cec745f892670c6aaff3e41229becaa8",
}

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
		},
	}
	if err := kubernetes.ResourceC(kubeClient).Create(grafanaCR); err != nil {
		return fmt.Errorf("Error while creating Grafana CR: %v ", err)
	}

	// Patch Grafana CR with image based on digest to allow image mirroring. Can be removed when Garafana image is moved out of docker.io registry.
	if deployment, err := GetDeploymentWaiting(namespace, defaultGrafanaDeployment, 2); err != nil {
		return fmt.Errorf("Error while waiting for Grafana Deployment to become available: %v ", err)
	} else if imageShaTag, imageEntryPresent := grafanaImages[deployment.Spec.Template.Spec.Containers[0].Image]; imageEntryPresent {
		err := patchGrafanaCR(grafanaCR.GetNamespace(), grafanaCR.GetName(), func(grafana *grafanav1.Grafana) {
			grafana.Spec.BaseImage = imageShaTag
		})
		if err != nil {
			return err
		}
	} else {
		return fmt.Errorf("Grafana image %s wasn't found in the list of Grafana images with defined digest. In case this is new Grafana image please add it into the list with proper digest", deployment.Spec.Template.Spec.Containers[0].Image)
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

func patchGrafanaCR(namespace, name string, patch func(grafana *grafanav1.Grafana)) error {
	var err error
	// Try patching for several times
	for i := 0; i < 3; i++ {
		// Fetch Grafana CR
		grafana := &grafanav1.Grafana{}
		if exists, err := GetObjectWithKey(types.NamespacedName{Namespace: namespace, Name: name}, grafana); err != nil {
			return fmt.Errorf("Error fetching Grafana %s in namespace %s: %v", name, namespace, err)
		} else if !exists {
			return fmt.Errorf("Grafana %s in namespace %s doesn't exist", name, namespace)
		}

		// Patch Grafana CR
		patch(grafana)

		// Update Grafana CR
		if err = UpdateObject(grafana); err == nil {
			return nil
		}
	}
	return fmt.Errorf("Error patching Grafana %s in namespace %s: %v", name, namespace, err)
}
