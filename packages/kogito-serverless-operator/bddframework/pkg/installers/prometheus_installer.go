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

package installers

import (
	monv1 "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
)

var (
	// prometheusOlmNamespacedInstaller installs Prometheus in the namespace using OLM
	prometheusOlmNamespacedInstaller = OlmNamespacedServiceInstaller{
		SubscriptionName:                  prometheusOperatorSubscriptionName,
		Channel:                           prometheusOperatorSubscriptionChannel,
		Catalog:                           framework.GetCommunityCatalog,
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

func getPrometheusCrsInNamespace(namespace string) ([]client.Object, error) {
	var crs []client.Object

	prometheuses := &monv1.PrometheusList{}
	if err := framework.GetObjectsInNamespace(namespace, prometheuses); err != nil {
		return nil, err
	}
	for i := range prometheuses.Items {
		crs = append(crs, prometheuses.Items[i])
	}

	return crs, nil
}
