//go:build integration_buildah

/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package builder

import (
	"os"
	"testing"

	"github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/common"
)

func TestBuildahTestSuite(t *testing.T) {
	suite.Run(t, new(BuildahTestSuite))
}

func currentDir() (string, error) {
	currentDir, err := os.Getwd()
	if err != nil {
		logrus.Error(err)
	}
	return currentDir, err
}

func (suite *BuildahTestSuite) TestBuildahBuild() {
	logrus.Info("TestBuildahBuild API")
	imageName := "localhost:5000/kiegroup/buildah-test:latest"
	registry, err, repos, size := CheckInitialStatePodmanRegistry(suite)

	currentDir, _ := currentDir()

	config := BuildahVanillaConfig{
		DockerFilePath:     currentDir + "/../examples/dockerfiles/",
		DockerFileName:     "Kogito.dockerfile",
		Ulimit:             "nofile=262144:262144", // this is needed to avoid the Java Exception too many files open during the maven build
		Output:             "kiegroup/buildah-build:latest",
		Tags:               []string{imageName},
		SeccompProfilePath: "unconfined",
		AddCapabilities:    []string{"all"},
	}
	connection, _ := common.GetRootlessPodmanConnection()
	id, err := BuildahBuild(connection, config)

	assert.NotNil(suite.T(), id)
	assert.Nil(suite.T(), err)

	reposSize := CheckImageOnPodmanRegistry(suite, imageName, repos, registry)
	assert.True(suite.T(), reposSize == size+1)
}

func CheckImageOnPodmanRegistry(suite *BuildahTestSuite, imageName string, repos []string, registry common.RegistryContainer) int {
	pushErr := suite.Podman.PushImage(imageName, imageName, "", "")
	assert.Nil(suite.T(), pushErr)
	repos, _ = registry.GetRepositories()
	return len(repos)
}

func CheckInitialStatePodmanRegistry(suite *BuildahTestSuite) (common.RegistryContainer, error, []string, int) {
	assert.Truef(suite.T(), suite.RegistryID != "", "Registry not started")
	registry, err := common.GetRegistryContainer()
	if err != nil {
		logrus.Error("registry not found")
	}
	repos, _ := registry.GetRepositories()
	assert.Nil(suite.T(), err)
	return registry, err, repos, len(repos)
}
