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

package steps

import (
	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// RegisterNamespaceSteps register Kubernetes object steps existing
func registerKubernetesSteps(s *godog.Suite, data *Data) {
	s.Step(`^Namespace is created$`, data.namespaceIsCreated)
	s.Step(`^Namespace is deleted$`, data.namespaceIsDeleted)

	s.Step(`^CLI create namespace$`, data.cliCreateNamespace)
	s.Step(`^CLI use namespace$`, data.cliUseNamespace)
}

func (data *Data) namespaceIsCreated() error {
	return framework.CreateNamespace(data.Namespace)
}

func (data *Data) namespaceIsDeleted() error {
	if exists, err := framework.IsNamespace(data.Namespace); err != nil {
		return err
	} else if exists {
		err := framework.DeleteNamespace(data.Namespace)
		if err != nil {
			return err
		}
		// wait for deletion complete
		err = framework.WaitForOnOpenshift(data.Namespace, "namespace is deleted", 2,
			func() (bool, error) {
				exists, err := framework.IsNamespace(data.Namespace)
				return !exists, err
			})
	}
	return nil
}

func (data *Data) cliCreateNamespace() error {
	_, err := framework.ExecuteCliCommand(data.Namespace, "new-project", data.Namespace)
	return err
}

func (data *Data) cliUseNamespace() error {
	_, err := framework.ExecuteCliCommand(data.Namespace, "use-project", data.Namespace)
	return err
}
