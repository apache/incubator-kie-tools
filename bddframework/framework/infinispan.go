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

	"gopkg.in/yaml.v2"

	infinispan "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"

	infinispaninfra "github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoinfra/infinispan"
)

// DeployInfinispanInstance deploys an instance of Infinispan
func DeployInfinispanInstance(namespace string, infinispan *infinispan.Infinispan) error {
	GetLogger(namespace).Infof("Creating Infinispan instance %s.", infinispan.Name)

	if err := kubernetes.ResourceC(kubeClient).Create(infinispan); err != nil {
		return fmt.Errorf("Error while creating Infinispan: %v ", err)
	}

	return nil
}

// CreateInfinispanSecret creates a new secret for Infinispan instance
func CreateInfinispanSecret(namespace, name string, credentialsMap map[string]string) error {
	GetLogger(namespace).Infof("Create Infinispan Secret %s", name)

	credentialsFileData, err := convertInfinispanCredentialsToYaml(credentialsMap)
	if err != nil {
		return err
	}

	return CreateSecret(namespace, name, map[string]string{infinispaninfra.IdentityFileName: credentialsFileData})
}

func convertInfinispanCredentialsToYaml(credentialsMap map[string]string) (string, error) {
	credentials := []infinispaninfra.Credential{}
	for username, password := range credentialsMap {
		credentials = append(credentials, infinispaninfra.Credential{Username: username, Password: password})
	}

	identity := infinispaninfra.Identity{Credentials: credentials}

	data, err := yaml.Marshal(&identity)
	if err != nil {
		return "", err
	}
	return string(data), nil
}
