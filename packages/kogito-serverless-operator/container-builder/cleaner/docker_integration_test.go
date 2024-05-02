//go:build integration_docker

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

package cleaner

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/common"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/util/log"
)

const (
	testImg         = "busybox"
	latestTag       = "latest"
	testImgLocalTag = "localhost:5000/busybox:latest"
)

func TestDockerIntegrationTestSuite(t *testing.T) {
	suite.Run(t, new(DockerTestSuite))
}

func (suite *DockerTestSuite) TestImagesOperationsOnDockerRegistryForTest() {
	registryContainer, err := common.GetRegistryContainer()
	assert.NotNil(suite.T(), registryContainer)
	assert.Nil(suite.T(), err)
	repos, err := registryContainer.GetRepositories()
	initialSize := len(repos)
	assert.Nil(suite.T(), err)
	pullErr := suite.Docker.PullImage(testImg + ":" + latestTag)
	if pullErr != nil {
		klog.V(log.E).ErrorS(pullErr, "Pull Error")
	}
	assert.Nil(suite.T(), pullErr, "Pull image failed")
	time.Sleep(2 * time.Second) // Needed on CI
	assert.True(suite.T(), suite.LocalRegistry.IsImagePresent(testImg), "Test image not found in the registry after the pull")
	tagErr := suite.Docker.TagImage(testImg, testImgLocalTag)
	if tagErr != nil {
		klog.V(log.E).ErrorS(tagErr, "Tag Error")
	}

	assert.Nil(suite.T(), tagErr, "Tag image failed")
	time.Sleep(2 * time.Second) // Needed on CI
	pushErr := suite.Docker.PushImage(testImgLocalTag, common.RegistryContainerUrlFromDockerSocket, "", "")
	if pushErr != nil {
		klog.V(log.E).ErrorS(pushErr, "Push Error")
	}

	assert.Nil(suite.T(), pushErr, "Push image in the Docker container failed")
	//give the time to update the registry status
	time.Sleep(2 * time.Second)
	repos, err = registryContainer.GetRepositories()
	assert.Nil(suite.T(), err)
	assert.NotNil(suite.T(), repos)
	assert.True(suite.T(), len(repos) == initialSize+1)

	digest, erroDIgest := registryContainer.Connection.ManifestDigest(testImg, latestTag)
	assert.Nil(suite.T(), erroDIgest)
	assert.NotNil(suite.T(), digest)
	assert.NotNil(suite.T(), registryContainer.DeleteImage(testImg, latestTag), "Delete Image not allowed")
}
