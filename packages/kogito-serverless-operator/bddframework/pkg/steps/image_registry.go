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

package steps

import (
	"errors"
	"fmt"
	"path/filepath"
	"sort"
	"strings"

	"github.com/cucumber/godog"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps/mappers"
)

/*
	DataTable for Maven:
	| profile | profile        |
	| profile | profile2       |
	| option  | -Doption=true  |
	| option  | -Doption2=true |
	| native  | enabled        |
*/

const (
	builtTagsLogFile              = "logs/built_images.log"
	builtProjectImageNamesLogFile = "logs/built_project_image_names.log"
)

// registerImageRegistrySteps register all existing image registry steps
func registerImageRegistrySteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step("^"+DefaultMavenBuiltExampleRegex+" and deployed to runtime registry$", data.localServiceBuiltByMavenAndDeployedToRuntimeRegistry)
	ctx.Step("^"+DefaultMavenBuiltExampleRegex+" and deployed to runtime registry with Maven configuration:$", data.localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistryWithMavenConfiguration)
}

// Build local service and deploy it to registry if the registry doesn't contain such image already
func (data *Data) localServiceBuiltByMavenAndDeployedToRuntimeRegistry(contextDir string) error {
	return data.localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistryWithMavenConfiguration(contextDir, nil)
}

// Build local service and deploy it to registry if the registry doesn't contain such image already
func (data *Data) localServiceBuiltByMavenWithProfileAndDeployedToRuntimeRegistryWithMavenConfiguration(contextDir string, table *godog.Table) error {
	mavenConfig := &mappers.MavenCommandConfig{}
	if table != nil && len(table.Rows) > 0 {
		err := mappers.MapMavenCommandConfigTable(table, mavenConfig)
		if err != nil {
			return err
		}
	}

	projectLocation := data.KogitoExamplesLocation + "/" + contextDir

	projectImageName := getProjectImageName(projectLocation, mavenConfig)
	runtimeApplicationImageTag, err := getRuntimeApplicationImageTag(projectImageName)
	if err != nil {
		return err
	}

	if needToBuildImage(data.Namespace, runtimeApplicationImageTag) {
		// Not found in registry, so we need to build and push the application
		// Build the application
		err = data.localServiceBuiltByMavenWithProfileAndOptions(contextDir, mavenConfig)
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
		err = framework.GetContainerEngine(data.Namespace).BuildImage(projectLocation, runtimeApplicationImageTag).PushImage(runtimeApplicationImageTag).GetError()
		if err != nil {
			return err
		}

		//Delete Dockerfile
		err = framework.DeleteFile(projectLocation, "Dockerfile")
		if err != nil {
			return err
		}

		onImageBuiltPostCreated(data.Namespace, projectImageName, runtimeApplicationImageTag)
	} else {
		framework.GetLogger(data.Namespace).Info("Using cached Kogito image", "imageTag", runtimeApplicationImageTag)
	}

	// Store image tag into scenario context
	kogitoApplicationName := filepath.Base(projectLocation)
	data.ScenarioContext[GetBuiltRuntimeImageTagContextKey(kogitoApplicationName)] = runtimeApplicationImageTag

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
func getRuntimeApplicationImageTag(projectImageName string) (string, error) {
	runtimeApplicationImageRegistry := config.GetRuntimeApplicationImageRegistry()
	if len(runtimeApplicationImageRegistry) == 0 {
		return "", errors.New("Runtime application image registry must be set to build the image")
	}

	runtimeApplicationImageName := getRuntimeApplicationImageName(projectImageName)

	runtimeApplicationImageVersion := config.GetRuntimeApplicationImageVersion()
	if len(runtimeApplicationImageVersion) == 0 {
		runtimeApplicationImageVersion = "latest"
	}

	buildImageTag := fmt.Sprintf("%s/%s:%s", runtimeApplicationImageRegistry, runtimeApplicationImageName, runtimeApplicationImageVersion)
	return buildImageTag, nil
}

// Retrieve the image name from project, based from test configuration
func getRuntimeApplicationImageName(projectImageName string) string {
	var runtimeApplicationImageNameParts []string

	if runtimeApplicationImageNamePrefix := config.GetRuntimeApplicationImageNamePrefix(); len(runtimeApplicationImageNamePrefix) > 0 {
		runtimeApplicationImageNameParts = append(runtimeApplicationImageNameParts, runtimeApplicationImageNamePrefix)
	}

	runtimeApplicationImageNameParts = append(runtimeApplicationImageNameParts, projectImageName)

	if runtimeApplicationImageNameSuffix := config.GetRuntimeApplicationImageNameSuffix(); len(runtimeApplicationImageNameSuffix) > 0 {
		runtimeApplicationImageNameParts = append(runtimeApplicationImageNameParts, runtimeApplicationImageNameSuffix)
	}

	return strings.Join(runtimeApplicationImageNameParts, "-")
}

// Returns project image name in the form of "<project location base>(-<maven profile>)*(-<maven option>)*"
func getProjectImageName(projectLocation string, mavenConfig *mappers.MavenCommandConfig) string {
	var projectImageNameParts []string

	projectImageBaseName := filepath.Base(projectLocation)
	projectImageNameParts = append(projectImageNameParts, projectImageBaseName)

	if len(mavenConfig.Profiles) > 0 {
		// Sort profiles to generate consistent image name
		sort.Strings(mavenConfig.Profiles)
		projectImageNameParts = append(projectImageNameParts, mavenConfig.Profiles...)
	}

	if mavenConfig.Native {
		projectImageNameParts = append(projectImageNameParts, nativeProfile)
	}

	if len(mavenConfig.Options) > 0 {
		// Sanitize mavenOptions so they can be used in the image name
		var sanitizedMavenOptions []string
		for _, option := range mavenConfig.Options {
			option = strings.ReplaceAll(option, "=", "-")
			option = strings.ToLower(option)
			sanitizedMavenOptions = append(sanitizedMavenOptions, option)
		}
		// Sort mavenOptions to generate consistent image name
		sort.Strings(sanitizedMavenOptions)

		projectImageNameParts = append(projectImageNameParts, sanitizedMavenOptions...)
	}

	return strings.Join(projectImageNameParts, "-")
}

// GetBuiltRuntimeImageTagContextKey Returns context tag used to store built runtime image tag
func GetBuiltRuntimeImageTagContextKey(kogitoApplicationName string) string {
	return fmt.Sprintf("built-image-%s", kogitoApplicationName)
}

func onImageBuiltPostCreated(namespace, projectImageName, runtimeApplicationImageTag string) {
	if err := framework.AddLineToFile(projectImageName, builtProjectImageNamesLogFile); err != nil {
		framework.GetLogger(namespace).Warn("Error updating built project image names", "error", err)
	}
	if err := framework.AddLineToFile(runtimeApplicationImageTag, builtTagsLogFile); err != nil {
		framework.GetLogger(namespace).Warn("Error updating built image tags", "error", err)
	}
}
