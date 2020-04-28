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

package steps

import (
	"fmt"

	infinispan "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"

	"github.com/cucumber/godog"
	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	//DataTable first column
	infinispanUsernameKey     = "username"
	infinispanPasswordNameKey = "password"
)

func registerInfinispanSteps(s *godog.Suite, data *Data) {
	s.Step(`^Infinispan instance "([^"]*)" is deployed with configuration:$`, data.infinispanInstanceIsDeployedWithConfiguration)
}

func (data *Data) infinispanInstanceIsDeployedWithConfiguration(name string, table *messages.PickleStepArgument_PickleTable) error {
	infinispanSecret := "external-infinispan-secret"

	if err := data.createInfinispanSecret(infinispanSecret, table); err != nil {
		return err
	}

	infinispan := &infinispan.Infinispan{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: data.Namespace,
			Name:      name,
		},
		Spec: infinispan.InfinispanSpec{
			Replicas: 1,
			Security: infinispan.InfinispanSecurity{
				EndpointSecretName: infinispanSecret,
			},
		},
	}
	if err := framework.DeployInfinispanInstance(data.Namespace, infinispan); err != nil {
		return err
	}

	return framework.WaitForPodsWithLabel(data.Namespace, "app", "infinispan-pod", 1, 3)
}

func (data *Data) createInfinispanSecret(name string, table *messages.PickleStepArgument_PickleTable) error {
	credentials := make(map[string]string)
	credentials["operator"] = "supersecretoperatorpassword" // Credentials required by Infinispan operator

	if username, password, err := getInfinispanCredentialsFromTable(table); err != nil {
		return err
	} else if len(username) > 0 {
		// User defined credentials
		credentials[username] = password
	}

	return framework.CreateInfinispanSecret(data.Namespace, name, credentials)
}

// Table parsing

func getInfinispanCredentialsFromTable(table *messages.PickleStepArgument_PickleTable) (username, password string, err error) {

	if len(table.Rows) == 0 { // Using default configuration
		return
	}

	if len(table.Rows[0].Cells) != 2 {
		return "", "", fmt.Errorf("expected table to have exactly two columns")
	}

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)
		switch firstColumn {
		case infinispanUsernameKey:
			username = getSecondColumn(row)
		case infinispanPasswordNameKey:
			password = getSecondColumn(row)

		default:
			return "", "", fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}
	}
	return
}
