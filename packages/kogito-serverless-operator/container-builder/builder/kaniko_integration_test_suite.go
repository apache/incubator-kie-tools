//go:build integration_kaniko_docker

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

package builder

import (
	"time"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/common"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/util/log"
)

type KanikoDockerTestSuite struct {
	suite.Suite
	LocalRegistry common.DockerLocalRegistry
	RegistryID    string
	Docker        common.Docker
}

func (suite *KanikoDockerTestSuite) SetupSuite() {
	dockerRegistryContainer, registryID, docker := common.SetupDockerSocket()
	if len(registryID) > 0 {
		suite.LocalRegistry = dockerRegistryContainer
		suite.RegistryID = registryID
		suite.Docker = docker
	} else {
		assert.FailNow(suite.T(), "Initialization failed %s", registryID)
	}

	pullErr := suite.Docker.PullImage(executorImage)
	if pullErr != nil {
		klog.V(log.E).ErrorS(pullErr, "Pull Kaniko executor")
	}
	time.Sleep(4 * time.Second) // Needed on CI
}

func (suite *KanikoDockerTestSuite) TearDownSuite() {
	registryID := suite.LocalRegistry.GetRegistryRunningID()
	if len(registryID) > 0 {
		common.DockerTearDown(suite.LocalRegistry)
	} else {
		suite.LocalRegistry.StopRegistry()
	}
	purged, err := suite.Docker.PurgeContainer("", common.RegistryImg)
	klog.V(log.I).InfoS("Purged containers", "containers", purged)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Purged registry")
	}

	purgedBuild, err := suite.Docker.PurgeContainer("", executorImage)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Purged container")
	}
	klog.V(log.I).InfoS("Purged container build", "container", purgedBuild)
}
