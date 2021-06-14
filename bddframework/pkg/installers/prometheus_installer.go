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
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
	monv1 "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"
)

var (
	// prometheusOlmNamespacedInstaller installs Prometheus in the namespace using OLM
	prometheusOlmNamespacedInstaller = OlmNamespacedServiceInstaller{
		SubscriptionName:                  prometheusOperatorSubscriptionName,
		Channel:                           prometheusOperatorSubscriptionChannel,
		Catalog:                           framework.CommunityCatalog,
		InstallationTimeoutInMinutes:      3,
		GetAllNamespacedOlmCrsInNamespace: getPrometheusCrsInNamespace,
	}

	prometheusOperatorSubscriptionName    = "prometheus"
	prometheusOperatorSubscriptionChannel = "beta"
)

// GetPrometheusInstaller returns Prometheus installer
func GetPrometheusInstaller() ServiceInstaller {
	return &prometheusOlmNamespacedInstaller
}

func getPrometheusCrsInNamespace(namespace string) ([]kubernetes.ResourceObject, error) {
	crs := []kubernetes.ResourceObject{}

	prometheuses := &monv1.PrometheusList{}
	if err := framework.GetObjectsInNamespace(namespace, prometheuses); err != nil {
		return nil, err
	}
	for i := range prometheuses.Items {
		crs = append(crs, prometheuses.Items[i])
	}

	return crs, nil
}
