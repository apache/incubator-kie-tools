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
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	grafanav1 "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/grafana/v1alpha1"
)

var (
	// grafanaOlmNamespacedInstaller installs Grafana in the namespace using OLM
	grafanaOlmNamespacedInstaller = OlmNamespacedServiceInstaller{
		SubscriptionName:                  grafanaOperatorSubscriptionName,
		Channel:                           grafanaOperatorSubscriptionChannel,
		Catalog:                           framework.GetCommunityCatalog,
		InstallationTimeoutInMinutes:      3,
		GetAllNamespacedOlmCrsInNamespace: getGrafanaCrsInNamespace,
	}

	grafanaOperatorSubscriptionName    = "grafana-operator"
	grafanaOperatorSubscriptionChannel = "v4"
)

// GetGrafanaInstaller returns Grafana installer
func GetGrafanaInstaller() ServiceInstaller {
	return &grafanaOlmNamespacedInstaller
}

func getGrafanaCrsInNamespace(namespace string) ([]client.Object, error) {
	var crs []client.Object

	grafanas := &grafanav1.GrafanaList{}
	if err := framework.GetObjectsInNamespace(namespace, grafanas); err != nil {
		return nil, err
	}
	for i := range grafanas.Items {
		crs = append(crs, &grafanas.Items[i])
	}

	return crs, nil
}
