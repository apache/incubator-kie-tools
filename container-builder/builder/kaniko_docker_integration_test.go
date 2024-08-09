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
	"os"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-kogito-serverless-operator/container-builder/util/log"
)

func TestKanikoTestSuite(t *testing.T) {
	suite.Run(t, new(KanikoDockerTestSuite))
}

func (suite *KanikoDockerTestSuite) TestKanikoBuild() {
	imageName := "localhost:5000/kaniko-test/kaniko-dockerfile_test_swf"
	//@TODO investigate when the code will be in the mono repo
	//registry, err, repos := checkEmptyDockerRegistry(suite)
	mydir, err := os.Getwd()
	if err != nil {
		klog.V(log.E).ErrorS(err, "error getting working directory.")
	}
	dockefileDir := mydir + "/../examples/dockerfiles"
	assert.Nil(suite.T(), suite.Docker.PullImage(executorImage), "Pull image failed")
	config := KanikoVanillaConfig{
		DockerFilePath:         dockefileDir,
		VerbosityLevel:         "info",
		KanikoExecutorImage:    executorImage,
		ContainerName:          "kaniko-build",
		DockerFileName:         "SonataFlow.dockerfile",
		RegistryFinalImageName: imageName,
		ReadBuildOutput:        false,
	}
	klog.V(log.I).InfoS("Start Kaniko build")
	start := time.Now()
	imageID, err := KanikoBuild(suite.Docker.Connection, config)
	timeElapsed := time.Since(start)
	klog.V(log.I).InfoS("The Kaniko build took", "duration", timeElapsed)
	assert.Nil(suite.T(), err, "ContainerBuild failed")
	assert.NotNil(suite.T(), imageID, err, "ContainerBuild failed")
	//@TODO investigate when the code will be in the mono repo
	//checkImageOnDockerRegistry(suite, imageName, repos, registry)
}
