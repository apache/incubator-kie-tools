// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitodataindex/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// DeployKogitoDataIndexService deploy the Kogito Data Index service
func DeployKogitoDataIndexService(namespace string, replicas int) error {
	kogitoDataIndex := &v1alpha1.KogitoDataIndex{
		ObjectMeta: metav1.ObjectMeta{
			Name:      resource.DefaultDataIndexName,
			Namespace: namespace,
		},
		Spec: v1alpha1.KogitoDataIndexSpec{
			Replicas: int32(replicas),
		},
	}

	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(kogitoDataIndex); err != nil {
		return fmt.Errorf("Error creating Kogito Data Index service: %v", err)
	}
	return nil
}
