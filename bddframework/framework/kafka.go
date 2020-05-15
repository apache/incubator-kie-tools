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

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"

	kafkabetav1 "github.com/kiegroup/kogito-cloud-operator/pkg/apis/kafka/v1beta1"
)

// DeployKafkaInstance deploys an instance of Kafka
func DeployKafkaInstance(namespace string, kafka *kafkabetav1.Kafka) error {
	GetLogger(namespace).Infof("Creating Kafka instance %s.", kafka.Name)

	if err := kubernetes.ResourceC(kubeClient).Create(kafka); err != nil {
		return fmt.Errorf("Error while creating Kafka: %v ", err)
	}

	return nil
}
