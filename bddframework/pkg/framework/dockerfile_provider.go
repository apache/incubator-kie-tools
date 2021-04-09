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
	"github.com/kiegroup/kogito-operator/core/kogitobuild"
	"io/ioutil"
	"path/filepath"
	"strings"
)

const (
	quarkusFastJarFolder = "quarkus-app"
	quarkusFastJarName   = "quarkus-run.jar"

	quarkusJVMLegacyApplicationBinarySuffix = "-runner.jar"
	quarkusJVMFastApplicationBinarySuffix   = quarkusFastJarFolder
	quarkusNativeApplicationBinarySuffix    = "-runner"
	springBootApplicationBinarySuffix       = ".jar"
)

// KogitoApplicationDockerfileProvider is the API to provide Dockerfile content for image creation based on built project content
type KogitoApplicationDockerfileProvider interface {
	// GetDockerfileContent returns Dockerfile content for image creation
	GetDockerfileContent() (string, error)
}

type kogitoApplicationDockerfileProviderStruct struct {
	projectLocation         string
	imageName               string
	jarSubDirectory         string
	executableFileName      string
	applicationBinarySuffix string
	folderDependencies      []string
}

var quarkusNonNativeLegacyJarKogitoApplicationDockerfileProvider = kogitoApplicationDockerfileProviderStruct{
	imageName:               kogitobuild.GetDefaultRuntimeJVMImage(),
	applicationBinarySuffix: quarkusJVMLegacyApplicationBinarySuffix,
	folderDependencies:      []string{"lib"},
}

var quarkusNonNativeFastJarKogitoApplicationDockerfileProvider = kogitoApplicationDockerfileProviderStruct{
	imageName:               kogitobuild.GetDefaultRuntimeJVMImage(),
	jarSubDirectory:         quarkusFastJarFolder,
	executableFileName:      quarkusFastJarName,
	applicationBinarySuffix: quarkusJVMFastApplicationBinarySuffix,
	folderDependencies:      []string{"lib", "quarkus", "app"},
}

var quarkusNativeKogitoApplicationDockerfileProvider = kogitoApplicationDockerfileProviderStruct{
	imageName:               kogitobuild.GetDefaultRuntimeNativeImage(),
	applicationBinarySuffix: quarkusNativeApplicationBinarySuffix,
}

var springbootKogitoApplicationDockerfileProvider = kogitoApplicationDockerfileProviderStruct{
	imageName:               kogitobuild.GetDefaultRuntimeJVMImage(),
	applicationBinarySuffix: springBootApplicationBinarySuffix,
}

// GetKogitoApplicationDockerfileProvider returns KogitoApplicationDockerfileProvider based on project location
func GetKogitoApplicationDockerfileProvider(projectLocation string) KogitoApplicationDockerfileProvider {
	targetDir := projectLocation + "/target"
	dockerfileProvider := &springbootKogitoApplicationDockerfileProvider

	if fileWithSuffixExists(targetDir, quarkusNativeKogitoApplicationDockerfileProvider.applicationBinarySuffix) {
		dockerfileProvider = &quarkusNativeKogitoApplicationDockerfileProvider
	} else if fileWithSuffixExists(targetDir, quarkusNonNativeFastJarKogitoApplicationDockerfileProvider.applicationBinarySuffix) {
		dockerfileProvider = &quarkusNonNativeFastJarKogitoApplicationDockerfileProvider
	} else if fileWithSuffixExists(targetDir, quarkusNonNativeLegacyJarKogitoApplicationDockerfileProvider.applicationBinarySuffix) {
		dockerfileProvider = &quarkusNonNativeLegacyJarKogitoApplicationDockerfileProvider
	}

	dockerfileProvider.projectLocation = projectLocation
	return dockerfileProvider
}

func (dockerfileProvider *kogitoApplicationDockerfileProviderStruct) GetDockerfileContent() (string, error) {
	// Declare base image to build from
	dockerfileContent := fmt.Sprintf("FROM %s\n", GetBuildImage(dockerfileProvider.imageName))

	subDir := ""
	if len(dockerfileProvider.jarSubDirectory) > 0 {
		subDir = dockerfileProvider.jarSubDirectory + "/"
	}
	executableFileName := fmt.Sprintf("%s%s", filepath.Base(dockerfileProvider.projectLocation), dockerfileProvider.applicationBinarySuffix)
	if len(dockerfileProvider.executableFileName) > 0 {
		executableFileName = dockerfileProvider.executableFileName
	}

	// Copy application binary into $KOGITO_HOME/bin
	dockerfileContent += fmt.Sprintf("COPY target/%s%s $KOGITO_HOME/bin\n", subDir, executableFileName)

	// Copy dependencies folder
	for _, depFolder := range dockerfileProvider.folderDependencies {
		dockerfileContent += fmt.Sprintf("COPY target/%s%s $KOGITO_HOME/bin/%s\n", subDir, depFolder, depFolder)
	}

	return dockerfileContent, nil
}

func fileWithSuffixExists(scannedDirectory, fileSuffix string) bool {
	files, err := ioutil.ReadDir(scannedDirectory)
	if err != nil {
		panic(fmt.Errorf("Error reading directory %s: %v", scannedDirectory, err))
	}

	for _, file := range files {
		if strings.HasSuffix(file.Name(), fileSuffix) {
			return true
		}
	}
	return false
}
