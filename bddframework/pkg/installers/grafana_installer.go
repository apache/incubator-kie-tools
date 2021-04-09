// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package installers

import (
	grafanav1 "github.com/integr8ly/grafana-operator/v3/pkg/apis/integreatly/v1alpha1"

	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
)

var (
	// grafanaOlmNamespacedInstaller installs Grafana in the namespace using OLM
	grafanaOlmNamespacedInstaller = OlmNamespacedServiceInstaller{
		SubscriptionName:                  grafanaOperatorSubscriptionName,
		Channel:                           grafanaOperatorSubscriptionChannel,
		Catalog:                           framework.CommunityCatalog,
		InstallationTimeoutInMinutes:      3,
		GetAllNamespacedOlmCrsInNamespace: getGrafanaCrsInNamespace,
	}

	grafanaOperatorSubscriptionName    = "grafana-operator"
	grafanaOperatorSubscriptionChannel = "alpha"
)

// GetGrafanaInstaller returns Grafana installer
func GetGrafanaInstaller() ServiceInstaller {
	return &grafanaOlmNamespacedInstaller
}

func getGrafanaCrsInNamespace(namespace string) ([]kubernetes.ResourceObject, error) {
	crs := []kubernetes.ResourceObject{}

	grafanas := &grafanav1.GrafanaList{}
	if err := framework.GetObjectsInNamespace(namespace, grafanas); err != nil {
		return nil, err
	}
	for i := range grafanas.Items {
		crs = append(crs, &grafanas.Items[i])
	}

	return crs, nil
}
