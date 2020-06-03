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
	"sort"
	"strings"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// registerImageRegistrySteps register all existing image registry steps
func registerImageRegistrySteps(s *godog.Suite, data *Data) {
	s.Step(`^Local example service "([^"]*)" is built by Maven using profile "([^"]*)" and deployed to runtime registry$`, data.localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistry)
}

// Build local service and deploy it to registry if the registry doesn't contain such image already
func (data *Data) localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistry(contextDir, profile string) error {
	projectLocation := data.KogitoExamplesLocation + "/" + contextDir

	imageTag, err := getKogitoImageTag(projectLocation, profile)
	if err != nil {
		return err
	}

	if err := framework.GetContainerEngine(data.Namespace).PullImage(imageTag).GetError(); err != nil {
		// Not found in registry, so we need to build and push the application
		// Build the application
		err = data.localServiceBuiltByMavenWithProfile(contextDir, profile)
		if err != nil {
			return err
		}

		// Create Dockerfile
		dockerfileContent := framework.GetKogitoApplicationDockerfileProvider(projectLocation).GetDockerfileContent()
		if err := framework.CreateFile(projectLocation, "Dockerfile", dockerfileContent); err != nil {
			return err
		}

		// Build and push image
		err := framework.GetContainerEngine(data.Namespace).BuildImage(projectLocation, imageTag).PushImage(imageTag).GetError()
		if err != nil {
			return err
		}
	} else {
		framework.GetLogger(data.Namespace).Infof("Using cached Kogito image from %s", imageTag)
	}

	// Store image tag into scenario context
	kogitoApplicationName := filepath.Base(projectLocation)
	data.ScenarioContext[getBuiltRuntimeImageTagContextKey(kogitoApplicationName)] = imageTag

	return nil
}

// Returns complete Kogito image tag, registry and namespace is retrieved from test configuration
func getKogitoImageTag(projectLocation, mavenProfiles string) (string, error) {
	runtimeApplicationImageRegistry := config.GetRuntimeApplicationImageRegistry()
	if len(runtimeApplicationImageRegistry) == 0 {
		return "", errors.New("Runtime application image registry must be set to build the image")
	}

	runtimeApplicationImageNamespace := config.GetRuntimeApplicationImageNamespace()
	if len(runtimeApplicationImageNamespace) == 0 {
		return "", errors.New("Runtime application image namespace must be set to build the image")
	}

	kogitoImageName := getKogitoImageName(projectLocation, mavenProfiles)
	buildImageTag := fmt.Sprintf("%s/%s/%s:latest", runtimeApplicationImageRegistry, runtimeApplicationImageNamespace, kogitoImageName)
	return buildImageTag, nil
}

// Returns Kogito image name in the form of "<project location base>(-<maven profile>)*"
func getKogitoImageName(projectLocation, mavenProfiles string) string {
	kogitoApplicationName := filepath.Base(projectLocation)

	if len(mavenProfiles) == 0 {
		return kogitoApplicationName
	}

	mavenProfilesSlice := strings.Split(mavenProfiles, ",")

	// Sort profiles to generate consistent image name
	sort.Strings(mavenProfilesSlice)

	joinedMavenProfiles := strings.Join(mavenProfilesSlice, "-")
	return fmt.Sprintf("%s-%s", kogitoApplicationName, joinedMavenProfiles)
}

// Returns context tag used to store built runtime image tag
func getBuiltRuntimeImageTagContextKey(kogitoApplicationName string) string {
	return fmt.Sprintf("built-image-%s", kogitoApplicationName)
}
