//go:build integration_podman

/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package cleaner

import (
	"testing"
	"time"

	"github.com/sirupsen/logrus"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/common"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

func TestPodmanIntegrationTestSuite(t *testing.T) {
	suite.Run(t, new(PodmanTestSuite))
}

func (suite *PodmanTestSuite) TestImagesOperationsOnPodmanRegistryForTest() {
	registryContainer, err := common.GetRegistryContainer()
	assert.NotNil(suite.T(), registryContainer)
	assert.Nil(suite.T(), err)
	repos, err := registryContainer.GetRepositories()
	assert.Nil(suite.T(), err)
	assert.True(suite.T(), len(repos) == 0)
	_, pullErr := suite.Podman.PullImage(common.TEST_IMG)
	if pullErr != nil {
		logrus.Infof("Pull Error:%s", pullErr)
	}
	assert.Nil(suite.T(), pullErr, "Pull image failed")
	time.Sleep(2 * time.Second) // Needed on CI

	tagErr := suite.Podman.TagImage(common.TEST_IMG, common.LATEST_TAG, common.TEST_REGISTRY_REPO+common.TEST_IMG)
	if tagErr != nil {
		logrus.Infof("Tag Error:%s", tagErr)
	}

	assert.Nil(suite.T(), tagErr, "Tag image failed")
	time.Sleep(2 * time.Second) // Needed on CI

	pushErr := suite.Podman.PushImage(common.TEST_IMG_LOCAL_TAG, common.TEST_IMG_LOCAL_TAG, "", "")

	if pushErr != nil {
		logrus.Infof("Push Error:%s", pushErr)
	}
	assert.Nil(suite.T(), pushErr, "Push image in the Podman container failed")
	//give the time to update the registry status
	time.Sleep(2 * time.Second)
	repos, err = registryContainer.GetRepositories()
	assert.Nil(suite.T(), err)
	assert.True(suite.T(), len(repos) == 1)

	digest, erroDIgest := registryContainer.Connection.ManifestDigest(common.TEST_IMG, common.LATEST_TAG)
	assert.Nil(suite.T(), erroDIgest)
	assert.NotNil(suite.T(), digest)
	assert.NotNil(suite.T(), registryContainer.DeleteImage(common.TEST_IMG, common.LATEST_TAG), "Delete Image not allowed")
}
