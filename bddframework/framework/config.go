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
	"flag"
	"path/filepath"

	"github.com/kiegroup/kogito-cloud-operator/version"
)

// TestConfig contains the information about the tests environment
type TestConfig struct {
	localTests bool
	smoke      bool

	operatorImageName string
	operatorImageTag  string

	operatorDeployURI string
	cliPath           string

	servicesImageVersion string

	mavenMirrorURL       string
	buildImageVersion    string
	buildS2iImageTag     string
	buildRuntimeImageTag string

	examplesRepositoryURI string
	examplesRepositoryRef string

	showScenarios bool
	dryRun        bool
}

const (
	defaultOperatorImageName = "quay.io/kiegroup/kogito-cloud-operator"

	defaultOperatorDeployURI = "../deploy/"
	defaultCliPath           = "../build/_output/bin/kogito"

	defaultKogitoExamplesURI = "https://github.com/kiegroup/kogito-examples"
)

var (
	defaultOperatorImageTag = version.Version

	env = TestConfig{}
)

// BindTestsConfigFlags binds BDD tests env flags to given flag set
func BindTestsConfigFlags(set *flag.FlagSet) {
	prefix := "tests."

	set.BoolVar(&env.localTests, prefix+"local", false, "If tests are launch on local machine")
	set.BoolVar(&env.smoke, prefix+"smoke", false, "Launch only smoke tests")

	set.StringVar(&env.operatorImageName, prefix+"operator-image-name", defaultOperatorImageName, "Operator image name")
	set.StringVar(&env.operatorImageTag, prefix+"operator-image-tag", defaultOperatorImageTag, "Operator image tag")
	set.StringVar(&env.operatorDeployURI, prefix+"operator-deploy-uri", defaultOperatorDeployURI, "Url or Path to operator 'deploy' folder")
	set.StringVar(&env.cliPath, prefix+"cli-path", defaultCliPath, "Path to built CLI to test")
	set.StringVar(&env.servicesImageVersion, prefix+"services-image-version", version.Version, "Set the services (jobs-service, data-index) image version")
	set.StringVar(&env.mavenMirrorURL, prefix+"maven-mirror-url", "", "Maven mirror url to be used when building app in the tests")
	set.StringVar(&env.buildImageVersion, prefix+"build-image-version", version.Version, "Set the build image version")
	set.StringVar(&env.buildS2iImageTag, prefix+"build-s2i-image-tag", "", "Set the S2I build image full tag")
	set.StringVar(&env.buildRuntimeImageTag, prefix+"build-runtime-image-tag", "", "Set the Runtime build image full tag")
	set.StringVar(&env.examplesRepositoryURI, prefix+"examples-uri", defaultKogitoExamplesURI, "Set the URI for the kogito-examples repository")
	set.StringVar(&env.examplesRepositoryRef, prefix+"examples-ref", "", "Set the branch for the kogito-examples repository")

	set.BoolVar(&env.showScenarios, prefix+"show-scenarios", false, "Show all scenarios which should be executed.")
	set.BoolVar(&env.dryRun, prefix+"dry-run", false, "Dry Run the tests.")
}

// GetConfigMavenMirrorURL return the maven mirror url used for building applications
func GetConfigMavenMirrorURL() string {
	return env.mavenMirrorURL
}

// GetConfigOperatorCliPath return the path to the kogito CLI binary
func GetConfigOperatorCliPath() (string, error) {
	return filepath.Abs(env.cliPath)
}

// GetConfigServicesImageVersion return the version for the services images
func GetConfigServicesImageVersion() string {
	return env.servicesImageVersion
}

// GetConfigOperatorDeployURI return the uri for deployment folder
func GetConfigOperatorDeployURI() string {
	return env.operatorDeployURI
}

// GetConfigOperatorImageName return the image name for the operator
func GetConfigOperatorImageName() string {
	return env.operatorImageName
}

// GetConfigOperatorImageTag return the image tag for the operator
func GetConfigOperatorImageTag() string {
	return env.operatorImageTag
}

// GetConfigExamplesRepositoryURI return the uri for the examples repository
func GetConfigExamplesRepositoryURI() string {
	return env.examplesRepositoryURI
}

// GetConfigExamplesRepositoryRef return the branch for the examples repository
func GetConfigExamplesRepositoryRef() string {
	return env.examplesRepositoryRef
}

// GetConfigBuildImageVersion return the version for the build images
func GetConfigBuildImageVersion() string {
	return env.buildImageVersion
}

// GetConfigBuildS2IImageStreamTag return the tag for the s2i build image
func GetConfigBuildS2IImageStreamTag() string {
	return env.buildS2iImageTag
}

// GetConfigBuildRuntimeImageStreamTag return the tag for the runtime build image
func GetConfigBuildRuntimeImageStreamTag() string {
	return env.buildRuntimeImageTag
}

// IsConfigLocalTests return whether tests are executed in local
func IsConfigLocalTests() bool {
	return env.localTests
}

// IsSmokeTests return whether tests are executed in local
func IsSmokeTests() bool {
	return env.smoke
}

// IsConfigShowScenarios return we should display scenarios
func IsConfigShowScenarios() bool {
	return env.showScenarios
}

// IsConfigDryRun return we should do a dry run
func IsConfigDryRun() bool {
	return env.dryRun
}
