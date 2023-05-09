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

	"github.com/kiegroup/kogito-serverless-operator/container-builder/common"

	"github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

func TestRegistryPodmanIntegrationTestSuite(t *testing.T) {
	suite.Run(t, new(PodmanTestSuite))
}

func (suite *PodmanTestSuite) TestRegistry() {
	assert.Truef(suite.T(), suite.RegistryID != "", "Registry not started")
	assert.Truef(suite.T(), suite.LocalRegistry.IsRegistryImagePresent(), "Registry image not present")
	assert.Truef(suite.T(), suite.LocalRegistry.IsRegistryRunning(), "Registry container not running")
}

func (suite *PodmanTestSuite) TestPullTagPush() {

	assert.Truef(suite.T(), suite.RegistryID != "", "Registry not started")
	registryContainer, err := common.GetRegistryContainer()
	assert.Nil(suite.T(), err)
	reposInitial, _ := registryContainer.GetRepositories()
	initialRepoSize := len(reposInitial)
	repos := CheckRepositoriesSize(suite.T(), initialRepoSize, registryContainer)
	logrus.Info("Empty Repo Size = ", len(repos))

	result := podmanPullTagPushOnRegistryContainer(suite)
	logrus.Info("Pull Tag and Push Image on Registry Container successful = ", result)

	// Give some time to the registry to refresh status
	time.Sleep(2 * time.Second)
	repos = CheckRepositoriesSize(suite.T(), initialRepoSize+1, registryContainer)
	logrus.Info("Repo Size after pull image = ", len(repos))
}

func podmanPullTagPushOnRegistryContainer(suite *PodmanTestSuite) bool {
	podmanSocketConn, errSock := common.GetRootlessPodmanConnection()
	if errSock != nil {
		assert.FailNow(suite.T(), "Cant get podman socket")
	}
	p := common.Podman{Connection: podmanSocketConn}
	_, err := p.PullImage(common.TEST_IMG_SECOND)
	time.Sleep(2 * time.Second) // needed on CI
	if err != nil {
		assert.Fail(suite.T(), "Pull Image Failed", err)
		return false
	}

	logrus.Info("Pull image")

	err = p.TagImage(common.TEST_REPO+common.TEST_IMG_SECOND_TAG, common.LATEST_TAG, common.TEST_REGISTRY_REPO+common.TEST_IMG_SECOND)
	if err != nil {
		assert.Fail(suite.T(), "Tag Image Failed", err)
		return false
	}
	logrus.Info("Tag image")

	err = p.PushImage(common.TEST_IMG_SECOND_LOCAL_TAG, common.TEST_IMG_SECOND_LOCAL_TAG, "", "")
	if err != nil {
		assert.Fail(suite.T(), "Push Image Failed", err)
		return false
	}
	logrus.Info("Push image")
	return true
}
