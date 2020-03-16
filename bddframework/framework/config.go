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
	// tests configuration
	smoke      bool
	loadFactor int
	localTests bool
	ciName     string

	// operator information
	operatorImageName string
	operatorImageTag  string

	// files/binaries
	operatorDeployURI string
	cliPath           string

	// runtime
	servicesImageVersion string

	// build
	mavenMirrorURL       string
	buildImageVersion    string
	buildS2iImageTag     string
	buildRuntimeImageTag string

	// examples repository
	examplesRepositoryURI string
	examplesRepositoryRef string

	// dev options
	showScenarios bool
	dryRun        bool
	keepNamespace bool
	namespaceName string
}

const (
	defaultOperatorImageName = "quay.io/kiegroup/kogito-cloud-operator"

	defaultOperatorDeployURI = "../deploy/"
	defaultCliPath           = "../build/_output/bin/kogito"

	defaultKogitoExamplesURI = "https://github.com/kiegroup/kogito-examples"

	defaultLoadFactor = 1
)

var (
	defaultOperatorImageTag = version.Version

	env = TestConfig{}
)

// BindTestsConfigFlags binds BDD tests env flags to given flag set
func BindTestsConfigFlags(set *flag.FlagSet) {
	prefix := "tests."
	developmentOptionsPrefix := prefix + "dev."

	// tests configuration
	set.BoolVar(&env.smoke, prefix+"smoke", false, "Launch only smoke tests")
	set.IntVar(&env.loadFactor, prefix+"load-factor", defaultLoadFactor, "Set the tests load factor. Useful for the tests to take into account that the cluster can be overloaded, for example for the calculation of timeouts. Default value is 1.")
	set.BoolVar(&env.localTests, prefix+"local", false, "If tests are launch on local machine")
	set.StringVar(&env.ciName, prefix+"ci", "", "If tests are launch on ci machine, give the CI name")

	// operator information
	set.StringVar(&env.operatorImageName, prefix+"operator-image-name", defaultOperatorImageName, "Operator image name")
	set.StringVar(&env.operatorImageTag, prefix+"operator-image-tag", defaultOperatorImageTag, "Operator image tag")

	// files/binaries
	set.StringVar(&env.operatorDeployURI, prefix+"operator-deploy-uri", defaultOperatorDeployURI, "Url or Path to operator 'deploy' folder")
	set.StringVar(&env.cliPath, prefix+"cli-path", defaultCliPath, "Path to built CLI to test")

	// runtime
	set.StringVar(&env.servicesImageVersion, prefix+"services-image-version", version.Version, "Set the services (jobs-service, data-index) image version")

	// build
	set.StringVar(&env.mavenMirrorURL, prefix+"maven-mirror-url", "", "Maven mirror url to be used when building app in the tests")
	set.StringVar(&env.buildImageVersion, prefix+"build-image-version", version.Version, "Set the build image version")
	set.StringVar(&env.buildS2iImageTag, prefix+"build-s2i-image-tag", "", "Set the S2I build image full tag")
	set.StringVar(&env.buildRuntimeImageTag, prefix+"build-runtime-image-tag", "", "Set the Runtime build image full tag")

	// examples repository
	set.StringVar(&env.examplesRepositoryURI, prefix+"examples-uri", defaultKogitoExamplesURI, "Set the URI for the kogito-examples repository")
	set.StringVar(&env.examplesRepositoryRef, prefix+"examples-ref", "", "Set the branch for the kogito-examples repository")

	// dev options
	set.BoolVar(&env.showScenarios, prefix+"show-scenarios", false, "Show all scenarios which should be executed.")
	set.BoolVar(&env.dryRun, prefix+"dry-run", false, "Dry Run the tests.")
	set.BoolVar(&env.keepNamespace, prefix+"keep-namespace", false, "Do not delete namespace(s) after scenario run (WARNING: can be resources consuming ...)")
	set.StringVar(&env.namespaceName, developmentOptionsPrefix+"namespace-name", "", "Use the specified namespace for scenarios, don't generate random namespace.")
}

// tests configuration

// IsConfigSmokeTests return whether tests are executed in local
func IsConfigSmokeTests() bool {
	return env.smoke
}

// GetConfigLoadFactor return the load factor of the cluster
func GetConfigLoadFactor() int {
	return env.loadFactor
}

// IsConfigLocalTests return whether tests are executed in local
func IsConfigLocalTests() bool {
	return env.localTests
}

// GetConfigCiName return the CI name that executes the tests, if any
func GetConfigCiName() string {
	return env.ciName
}

// operator information

// GetConfigOperatorImageName return the image name for the operator
func GetConfigOperatorImageName() string {
	return env.operatorImageName
}

// GetConfigOperatorImageTag return the image tag for the operator
func GetConfigOperatorImageTag() string {
	return env.operatorImageTag
}

// files/binaries

// GetConfigOperatorDeployURI return the uri for deployment folder
func GetConfigOperatorDeployURI() string {
	return env.operatorDeployURI
}

// GetConfigOperatorCliPath return the path to the kogito CLI binary
func GetConfigOperatorCliPath() (string, error) {
	return filepath.Abs(env.cliPath)
}

// runtime

// GetConfigServicesImageVersion return the version for the services images
func GetConfigServicesImageVersion() string {
	return env.servicesImageVersion
}

// build

// GetConfigMavenMirrorURL return the maven mirror url used for building applications
func GetConfigMavenMirrorURL() string {
	return env.mavenMirrorURL
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

// examples repository

// GetConfigExamplesRepositoryURI return the uri for the examples repository
func GetConfigExamplesRepositoryURI() string {
	return env.examplesRepositoryURI
}

// GetConfigExamplesRepositoryRef return the branch for the examples repository
func GetConfigExamplesRepositoryRef() string {
	return env.examplesRepositoryRef
}

// dev options

// IsConfigShowScenarios return whether we should display scenarios
func IsConfigShowScenarios() bool {
	return env.showScenarios
}

// IsConfigDryRun return whether we should do a dry run
func IsConfigDryRun() bool {
	return env.dryRun
}

// IsConfigKeepNamespace return whether we should keep namespace after scenario run
func IsConfigKeepNamespace() bool {
	return env.keepNamespace
}

// GetConfigNamespaceName return namespace name if it was defined
func GetConfigNamespaceName() string {
	return env.namespaceName
}
