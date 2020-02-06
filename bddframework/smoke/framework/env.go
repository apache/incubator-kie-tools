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
	"github.com/kiegroup/kogito-cloud-operator/version"
	"path/filepath"

	"github.com/kiegroup/kogito-cloud-operator/pkg/util"
)

const (
	defaultKogitoExamplesURI = "https://github.com/kiegroup/kogito-examples"

	defaultOperatorImageName = "quay.io/kiegroup/kogito-cloud-operator"
	defaultOperatorDeployURI = "../../deploy/"
	defaultCliPath           = "../../build/_output/bin/kogito"

	mavenArgsAppendEnvVar = "MAVEN_ARGS_APPEND"
	mavenMirrorURLEnvVar  = "MAVEN_MIRROR_URL"
)

func getEnvMavenMirrorURL() string {
	return util.GetOSEnv(mavenMirrorURLEnvVar, "")
}

func getEnvOperatorCliPath() (string, error) {
	path := util.GetOSEnv("OPERATOR_CLI_PATH", defaultCliPath)
	return filepath.Abs(path)
}

func getEnvOperatorDeployURI() string {
	return util.GetOSEnv("OPERATOR_DEPLOY_FOLDER", defaultOperatorDeployURI)
}

func getEnvOperatorImageName() string {
	return util.GetOSEnv("OPERATOR_IMAGE_NAME", defaultOperatorImageName)
}

func getEnvOperatorImageTag() string {
	return util.GetOSEnv("OPERATOR_IMAGE_TAG", defaultOperatorImageTag)
}

func getEnvExamplesRepositoryURI() string {
	return util.GetOSEnv("KOGITO_EXAMPLES_REPOSITORY_URI", defaultKogitoExamplesURI)
}

func getEnvExamplesRepositoryRef() string {
	return util.GetOSEnv("KOGITO_EXAMPLES_REPOSITORY_REF", "")
}

func getEnvImageVersion() string {
	return util.GetOSEnv("KOGITO_BUILD_IMAGE_VERSION", version.Version)
}

func getEnvS2IImageStreamTag() string {
	return getOsMultipleEnv("KOGITO_BUILD_IMAGE_STREAM_TAG", "KOGITO_BUILD_S2I_IMAGE_STREAM_TAG", "")
}

func getEnvRuntimeImageStreamTag() string {
	return getOsMultipleEnv("KOGITO_BUILD_IMAGE_STREAM_TAG", "KOGITO_BUILD_RUNTIME_IMAGE_STREAM_TAG", "")
}

func getEnvLocalTests() string {
	return util.GetOSEnv("LOCAL_TESTS", "false")
}

func getEnvUsername() string {
	return util.GetOSEnv("USERNAME", "nouser")
}

func getOsMultipleEnv(env1, env2, defaultValue string) string {
	return util.GetOSEnv(env1, util.GetOSEnv(env2, defaultValue))
}
