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
	ispn "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

var (
	// infinispanOlmNamespacedInstaller installs Infinispan in the namespace using OLM
	infinispanOlmNamespacedInstaller = OlmNamespacedServiceInstaller{
		subscriptionName:                  infinispanOperatorSubscriptionName,
		channel:                           infinispanOperatorSubscriptionChannel,
		catalog:                           framework.CommunityCatalog,
		installationTimeoutInMinutes:      10,
		getAllNamespacedOlmCrsInNamespace: getInfinispanCrsInNamespace,
	}

	infinispanOperatorSubscriptionName    = "infinispan"
	infinispanOperatorSubscriptionChannel = "2.0.x"
)

// GetInfinispanInstaller returns Infinispan installer
func GetInfinispanInstaller() ServiceInstaller {
	return &infinispanOlmNamespacedInstaller
}

func getInfinispanCrsInNamespace(namespace string) ([]kubernetes.ResourceObject, error) {
	crs := []kubernetes.ResourceObject{}

	infinispans := &ispn.InfinispanList{}
	if err := framework.GetObjectsInNamespace(namespace, infinispans); err != nil {
		return nil, err
	}
	for i := range infinispans.Items {
		crs = append(crs, &infinispans.Items[i])
	}

	return crs, nil
}
