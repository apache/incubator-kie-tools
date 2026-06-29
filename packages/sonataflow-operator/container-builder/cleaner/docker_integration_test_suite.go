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
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/common"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/util/log"
)

type DockerTestSuite struct {
	suite.Suite
	LocalRegistry common.DockerLocalRegistry
	RegistryID    string
	Docker        common.Docker
}

func (suite *DockerTestSuite) SetupSuite() {
	// Clean up any existing registry containers first
	// Use common.RegistryImg which is "registry" to match all registry containers
	cli, _ := common.GetDockerConnection()
	if cli != nil {
		docker := common.Docker{Connection: cli}
		// Purge by image name to catch any leftover containers
		_, _ = docker.PurgeContainer("", "registry:latest")
		_, _ = docker.PurgeContainer("", "docker.io/library/registry:latest")
		_, _ = docker.PurgeContainer("", common.RegistryImg)
	}

	dockerRegistryContainer, registryID, docker := common.SetupDockerSocket()

	// Always set these so TearDownSuite can clean up
	suite.LocalRegistry = dockerRegistryContainer
	suite.Docker = docker
	suite.RegistryID = registryID

	if len(registryID) == 0 {
		// Clean up before failing
		if docker.Connection != nil {
			_, _ = docker.PurgeContainer("", "registry:latest")
			_, _ = docker.PurgeContainer("", common.RegistryImg)
		}
		assert.FailNow(suite.T(), "Initialization failed - registry ID is empty")
	}
}

func (suite *DockerTestSuite) TearDownSuite() {
	// Try multiple cleanup approaches to ensure registry is removed
	if suite.LocalRegistry.Connection != nil {
		registryID := suite.LocalRegistry.GetRegistryRunningID()
		if len(registryID) > 0 {
			common.DockerTearDown(suite.LocalRegistry)
		} else {
			suite.LocalRegistry.StopRegistry()
		}
	}

	// Always try to purge any remaining registry containers using multiple image names
	if suite.Docker.Connection != nil {
		// Try all possible image name variations
		_, _ = suite.Docker.PurgeContainer("", "registry:latest")
		purged, err := suite.Docker.PurgeContainer("", common.RegistryImg)
		if err != nil {
			klog.V(log.E).ErrorS(err, "Error during purged container in TearDown Suite.")
		}
		klog.V(log.I).InfoS("Purged container", "containers", purged)
	} else {
		// Fallback: create a fresh connection and clean up
		cli, err := common.GetDockerConnection()
		if err == nil && cli != nil {
			docker := common.Docker{Connection: cli}
			_, _ = docker.PurgeContainer("", "registry:latest")
			_, _ = docker.PurgeContainer("", common.RegistryImg)
		}
	}
}
