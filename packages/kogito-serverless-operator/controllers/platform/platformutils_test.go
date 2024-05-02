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

package platform

import (
	"os"
	"regexp"
	"testing"

	"github.com/stretchr/testify/assert"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
)

func TestSonataFlowBuildController(t *testing.T) {
	platform := test.GetBasePlatform()
	dockerfileBytes, err := os.ReadFile("../../test/builder/Dockerfile")
	if err != nil {
		assert.Fail(t, "Unable to read base Dockerfile")
	}
	dockerfile := string(dockerfileBytes)
	// 1 - Let's verify that the default image is used (for this unit test is quay.io/kiegroup/kogito-swf-builder-nightly:latest)
	resDefault := GetCustomizedBuilderDockerfile(dockerfile, *platform)
	foundDefault, err := regexp.MatchString("FROM quay.io/kiegroup/kogito-swf-builder-nightly:latest AS builder", resDefault)
	assert.NoError(t, err)
	assert.True(t, foundDefault)

	// 2 - Let's try to override using the productized image
	platform.Spec.Build.Config.BaseImage = "registry.access.redhat.com/openshift-serverless-1-tech-preview/logic-swf-builder-rhel8"
	resProductized := GetCustomizedBuilderDockerfile(dockerfile, *platform)
	foundProductized, err := regexp.MatchString("FROM registry.access.redhat.com/openshift-serverless-1-tech-preview/logic-swf-builder-rhel8 AS builder", resProductized)
	assert.NoError(t, err)
	assert.True(t, foundProductized)
}
