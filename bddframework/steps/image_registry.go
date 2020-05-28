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
	"errors"
	"fmt"
	"path/filepath"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// registerImageRegistrySteps register all existing image registry steps
func registerImageRegistrySteps(s *godog.Suite, data *Data) {
	s.Step(`^Local example service "([^"]*)" is deployed to image registry, image tag stored as variable "([^"]*)"`, data.localServiceDeployedToImageRegistry)
}

// Build local service
func (data *Data) localServiceDeployedToImageRegistry(contextDir, imageTagContextKey string) error {
	projectLocation := data.KogitoExamplesLocation + "/" + contextDir

	// Create Dockerfile
	dockerfileContent := framework.GetKogitoApplicationDockerfileProvider(projectLocation).GetDockerfileContent()
	if err := framework.CreateFile(projectLocation, "Dockerfile", dockerfileContent); err != nil {
		return err
	}

	// Build image
	builtImageTag, err := buildImage(data.Namespace, projectLocation)
	if err != nil {
		return err
	}

	// Push image
	_, err = framework.CreateCommand("docker", "push", builtImageTag).WithLoggerContext(data.Namespace).Execute()
	if err != nil {
		return err
	}

	// Store image tag into scenario context
	data.ScenarioContext[imageTagContextKey] = builtImageTag
	return nil
}

func buildImage(namespace, projectLocation string) (string, error) {
	runtimeApplicationImageRegistry := config.GetRuntimeApplicationImageRegistry()
	if len(runtimeApplicationImageRegistry) == 0 {
		return "", errors.New("Runtime application image registry must be set to build the image")
	}

	runtimeApplicationImageNamespace := config.GetRuntimeApplicationImageNamespace()
	if len(runtimeApplicationImageNamespace) == 0 {
		return "", errors.New("Runtime application image namespace must be set to build the image")
	}

	kogitoApplicationName := filepath.Base(projectLocation)
	builtImageTag := fmt.Sprintf("%s/%s/%s:%s", runtimeApplicationImageRegistry, runtimeApplicationImageNamespace, kogitoApplicationName, namespace)
	_, err := framework.CreateCommand("docker", "build", "--tag", builtImageTag, ".").InDirectory(projectLocation).WithLoggerContext(namespace).Execute()
	return builtImageTag, err
}
