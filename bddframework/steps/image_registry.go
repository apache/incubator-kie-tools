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
	"github.com/kiegroup/kogito-cloud-operator/test/steps/mappers"
)

/*
	DataTable for Maven:
	| -Dproperty=value |
*/

// registerImageRegistrySteps register all existing image registry steps
func registerImageRegistrySteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Local example service "([^"]*)" is built by Maven using profile "([^"]*)" and deployed to runtime registry$`, data.localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistry)
	ctx.Step(`^Local example service "([^"]*)" is built by Maven using profile "([^"]*)" and deployed to runtime registry with Maven options:$`, data.localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistryWithMavenOptions)
}

// Build local service and deploy it to registry if the registry doesn't contain such image already
func (data *Data) localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistry(contextDir, profile string) error {
	return data.localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistryWithMavenOptions(contextDir, profile, nil)
}

// Build local service and deploy it to registry if the registry doesn't contain such image already
func (data *Data) localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistryWithMavenOptions(contextDir, profile string, table *godog.Table) error {
	var options []string
	if table != nil && len(table.Rows) > 0 {
		err := mappers.MapMavenOptionsTable(table, &options)
		if err != nil {
			return err
		}
	}

	projectLocation := data.KogitoExamplesLocation + "/" + contextDir

	imageTag, err := getKogitoImageTag(projectLocation, profile, options)
	if err != nil {
		return err
	}

	if needToBuildImage(data.Namespace, imageTag) {
		// Not found in registry, so we need to build and push the application
		// Build the application
		err = data.localServiceBuiltByMavenWithProfileWithOptions(contextDir, profile, options)
		if err != nil {
			return err
		}

		// Create Dockerfile
		dockerfileContent, err := framework.GetKogitoApplicationDockerfileProvider(projectLocation).GetDockerfileContent()
		if err != nil {
			return err
		}

		if err := framework.CreateFile(projectLocation, "Dockerfile", dockerfileContent); err != nil {
			return err
		}

		// Build and push image
		err = framework.GetContainerEngine(data.Namespace).BuildImage(projectLocation, imageTag).PushImage(imageTag).GetError()
		if err != nil {
			return err
		}

		//Delete Dockerfile
		err = framework.DeleteFile(projectLocation, "Dockerfile")
		if err != nil {
			return err
		}
	} else {
		framework.GetLogger(data.Namespace).Info("Using cached Kogito image", "imageTag", imageTag)
	}

	// Store image tag into scenario context
	kogitoApplicationName := filepath.Base(projectLocation)
	data.ScenarioContext[getBuiltRuntimeImageTagContextKey(kogitoApplicationName)] = imageTag

	return nil
}

// Returns true if the image has to be built
func needToBuildImage(namespace, imageTag string) bool {
	switch config.GetImageCacheMode() {
	case config.UseImageCacheAlways:
		return false
	case config.UseImageCacheNever:
		return true
	case config.UseImageCacheIfAvailable:
		{
			// Check if image is available in registry, error means it is not available
			err := framework.GetContainerEngine(namespace).PullImage(imageTag).GetError()
			return err != nil
		}
	}
	return true
}

// Returns complete Kogito image tag, registry and namespace is retrieved from test configuration
func getKogitoImageTag(projectLocation, mavenProfiles string, mavenOptions []string) (string, error) {
	runtimeApplicationImageRegistry := config.GetRuntimeApplicationImageRegistry()
	if len(runtimeApplicationImageRegistry) == 0 {
		return "", errors.New("Runtime application image registry must be set to build the image")
	}

	runtimeApplicationImageNamespace := config.GetRuntimeApplicationImageNamespace()
	if len(runtimeApplicationImageNamespace) == 0 {
		return "", errors.New("Runtime application image namespace must be set to build the image")
	}

	runtimeApplicationImageVersion := config.GetRuntimeApplicationImageVersion()
	if len(runtimeApplicationImageVersion) == 0 {
		runtimeApplicationImageVersion = "latest"
	}

	kogitoImageName := getKogitoImageName(projectLocation, mavenProfiles, mavenOptions)
	buildImageTag := fmt.Sprintf("%s/%s/%s:%s", runtimeApplicationImageRegistry, runtimeApplicationImageNamespace, kogitoImageName, runtimeApplicationImageVersion)
	return buildImageTag, nil
}

// Returns Kogito image name in the form of "<project location base>(-<maven profile>)*(-<maven option>)*"
func getKogitoImageName(projectLocation, mavenProfiles string, mavenOptions []string) string {
	kogitoApplicationBaseName := filepath.Base(projectLocation)
	kogitoApplicationNameParts := []string{kogitoApplicationBaseName}

	if len(mavenProfiles) > 0 {
		mavenProfilesSlice := strings.Split(mavenProfiles, ",")

		// Sort profiles to generate consistent image name
		sort.Strings(mavenProfilesSlice)

		kogitoApplicationNameParts = append(kogitoApplicationNameParts, mavenProfilesSlice...)
	}

	if len(mavenOptions) > 0 {
		// Sanitize mavenOptions so they can be used in the image name
		var sanitizedMavenOptions []string
		for _, option := range mavenOptions {
			option = strings.ReplaceAll(option, "=", "-")
			option = strings.ToLower(option)
			sanitizedMavenOptions = append(sanitizedMavenOptions, option)
		}
		// Sort mavenOptions to generate consistent image name
		sort.Strings(sanitizedMavenOptions)

		kogitoApplicationNameParts = append(kogitoApplicationNameParts, sanitizedMavenOptions...)
	}

	if runtimeApplicationImageNameSuffix := config.GetRuntimeApplicationImageNameSuffix(); len(runtimeApplicationImageNameSuffix) > 0 {
		kogitoApplicationNameParts = append(kogitoApplicationNameParts, runtimeApplicationImageNameSuffix)
	}

	return strings.Join(kogitoApplicationNameParts, "-")
}

// Returns context tag used to store built runtime image tag
func getBuiltRuntimeImageTagContextKey(kogitoApplicationName string) string {
	return fmt.Sprintf("built-image-%s", kogitoApplicationName)
}
