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
	"fmt"
	"os"
	"regexp"
	"testing"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"

	"github.com/stretchr/testify/assert"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
)

const dockerFile = "FROM host/namespace/default-test-kie-sonataflow-builder:main AS builder\n\n# ETC, \n\n# ETC, \n\n# ETC"

func TestSonataFlowBuildController(t *testing.T) {
	platform := test.GetBasePlatform()
	dockerfileBytes, err := os.ReadFile("testdata/platformTest.Dockerfile")
	if err != nil {
		assert.Fail(t, "Unable to read base Dockerfile")
	}
	dockerfile := string(dockerfileBytes)
	// 1 - Let's verify that the default image is used
	resDefault := GetCustomizedBuilderDockerfile(dockerfile, *platform)
	foundDefault, err := regexp.MatchString(fmt.Sprintf("FROM %s AS builder", test.CommonImageTag), resDefault)
	assert.NoError(t, err)
	assert.True(t, foundDefault)

	// 2 - Let's try to override using the productized image
	platform.Spec.Build.Config.BaseImage = "host2.org/namespace2/builder2:main"
	resProductized := GetCustomizedBuilderDockerfile(dockerfile, *platform)
	foundProductized, err := regexp.MatchString(fmt.Sprintf("FROM %s AS builder", platform.Spec.Build.Config.BaseImage), resProductized)
	assert.NoError(t, err)
	assert.True(t, foundProductized)
}

func TestGetCustomizedBuilderDockerfile_NoBaseImageCustomization(t *testing.T) {
	sfp := v1alpha08.SonataFlowPlatform{
		TypeMeta:   metav1.TypeMeta{},
		ObjectMeta: metav1.ObjectMeta{},
		Spec:       v1alpha08.SonataFlowPlatformSpec{},
		Status:     v1alpha08.SonataFlowPlatformStatus{},
	}
	customizedDockerfile := GetCustomizedBuilderDockerfile(dockerFile, sfp)
	assert.Equal(t, dockerFile, customizedDockerfile)
}

func TestGetCustomizedBuilderDockerfile_BaseImageCustomizationFromPlatform(t *testing.T) {
	sfp := v1alpha08.SonataFlowPlatform{
		TypeMeta:   metav1.TypeMeta{},
		ObjectMeta: metav1.ObjectMeta{},
		Spec: v1alpha08.SonataFlowPlatformSpec{
			Build: v1alpha08.BuildPlatformSpec{
				Template: v1alpha08.BuildTemplate{},
				Config: v1alpha08.BuildPlatformConfig{
					BaseImage: test.CommonImageTag,
				},
			},
		},
		Status: v1alpha08.SonataFlowPlatformStatus{},
	}

	expectedDockerFile := fmt.Sprintf("FROM %s AS builder\n\n# ETC, \n\n# ETC, \n\n# ETC", test.CommonImageTag)
	customizedDockerfile := GetCustomizedBuilderDockerfile(dockerFile, sfp)
	assert.Equal(t, expectedDockerFile, customizedDockerfile)
}

func TestGetCustomizedBuilderDockerfile_BaseImageCustomizationFromControllersConfig(t *testing.T) {
	sfp := v1alpha08.SonataFlowPlatform{
		TypeMeta:   metav1.TypeMeta{},
		ObjectMeta: metav1.ObjectMeta{},
		Spec:       v1alpha08.SonataFlowPlatformSpec{},
		Status:     v1alpha08.SonataFlowPlatformStatus{},
	}

	_, err := cfg.InitializeControllersCfgAt("../cfg/testdata/controllers-cfg-test.yaml")
	assert.NoError(t, err)
	expectedDockerFile := "FROM local/sonataflow-builder:1.0.0 AS builder\n\n# ETC, \n\n# ETC, \n\n# ETC"
	customizedDockerfile := GetCustomizedBuilderDockerfile(dockerFile, sfp)
	assert.Equal(t, expectedDockerFile, customizedDockerfile)
}
