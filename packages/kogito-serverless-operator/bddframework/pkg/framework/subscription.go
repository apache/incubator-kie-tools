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
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
)

// getSubscription returns subscription or nil if no subscription is found.
func getSubscription(cli *client.Client, namespace, packageName, catalogSource string) (*olmapiv1alpha1.Subscription, error) {
	log := GetLogger("subscription")
	log.Debug("Trying to fetch Subscription", "namespace", namespace, "Package name", packageName, "CatalogSource", namespace, packageName, catalogSource)

	subs := &olmapiv1alpha1.SubscriptionList{}
	if err := kubernetes.ResourceC(cli).ListWithNamespace(namespace, subs); err != nil {
		return nil, err
	}

	for _, sub := range subs.Items {
		if sub.Spec.Package == packageName &&
			sub.Spec.CatalogSource == catalogSource {
			return &sub, nil
		}
	}

	return nil, nil
}
