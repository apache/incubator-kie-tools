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

package config

import (
	"flag"
	"path/filepath"

	"github.com/kiegroup/kogito-cloud-operator/version"
)

// TestConfig contains the information about the tests environment
type TestConfig struct {
	// tests configuration
	smoke            bool
	performance      bool
	loadFactor       int
	localTests       bool
	ciName           string
	crDeploymentOnly bool

	// operator information
	operatorImageName string
	operatorImageTag  string

	// files/binaries
	operatorDeployURI string
	cliPath           string

	// runtime
	servicesImageVersion   string
	servicesImageNamespace string
	servicesImageRegistry  string
	dataIndexImageTag      string
	jobsServiceImageTag    string
	mgmtConsoleImageTag    string

	// build
	mavenMirrorURL       string
	buildImageVersion    string
	buildImageNamespace  string
	buildImageRegistry   string
	buildS2iImageTag     string
	buildRuntimeImageTag string

	// examples repository
	examplesRepositoryURI string
	examplesRepositoryRef string

	// dev options
	showScenarios bool
	showSteps     bool
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

// BindFlags binds BDD tests env flags to given flag set
func BindFlags(set *flag.FlagSet) {
	prefix := "tests."
	developmentOptionsPrefix := prefix + "dev."

	// tests configuration
	set.BoolVar(&env.smoke, prefix+"smoke", false, "Launch only smoke tests")
	set.BoolVar(&env.performance, prefix+"performance", false, "Launch performance tests")
	set.IntVar(&env.loadFactor, prefix+"load-factor", defaultLoadFactor, "Set the tests load factor. Useful for the tests to take into account that the cluster can be overloaded, for example for the calculation of timeouts. Default value is 1.")
	set.BoolVar(&env.localTests, prefix+"local", false, "If tests are launch on local machine")
	set.StringVar(&env.ciName, prefix+"ci", "", "If tests are launch on ci machine, give the CI name")
	set.BoolVar(&env.crDeploymentOnly, prefix+"cr-deployment-only", false, "Use this option if you have no CLI to test against. It will use only direct CR deployments.")

	// operator information
	set.StringVar(&env.operatorImageName, prefix+"operator-image-name", defaultOperatorImageName, "Operator image name")
	set.StringVar(&env.operatorImageTag, prefix+"operator-image-tag", defaultOperatorImageTag, "Operator image tag")

	// files/binaries
	set.StringVar(&env.operatorDeployURI, prefix+"operator-deploy-uri", defaultOperatorDeployURI, "Url or Path to operator 'deploy' folder")
	set.StringVar(&env.cliPath, prefix+"cli-path", defaultCliPath, "Path to built CLI to test")

	// runtime
	set.StringVar(&env.servicesImageVersion, prefix+"services-image-version", "", "Set the services (jobs-service, data-index) image version")
	set.StringVar(&env.servicesImageNamespace, prefix+"services-image-namespace", "", "Set the services (jobs-service, data-index) image namespace")
	set.StringVar(&env.servicesImageRegistry, prefix+"services-image-registry", "", "Set the services (jobs-service, data-index) image registry")
	set.StringVar(&env.dataIndexImageTag, prefix+"data-index-image-tag", "", "Set the Kogito Data Index image tag ('services-image-version' is ignored)")
	set.StringVar(&env.jobsServiceImageTag, prefix+"jobs-service-image-tag", "", "Set the Kogito Jobs Service image tag ('services-image-version' is ignored)")
	set.StringVar(&env.mgmtConsoleImageTag, prefix+"management-console-image-tag", "", "Set the Kogito Management Console image tag ('services-image-version' is ignored)")

	// build
	set.StringVar(&env.mavenMirrorURL, prefix+"maven-mirror-url", "", "Maven mirror url to be used when building app in the tests")
	set.StringVar(&env.buildImageVersion, prefix+"build-image-version", "", "Set the build image version")
	set.StringVar(&env.buildImageNamespace, prefix+"build-image-namespace", "", "Set the build image namespace")
	set.StringVar(&env.buildImageRegistry, prefix+"build-image-registry", "", "Set the build image registry")
	set.StringVar(&env.buildS2iImageTag, prefix+"build-s2i-image-tag", "", "Set the S2I build image full tag")
	set.StringVar(&env.buildRuntimeImageTag, prefix+"build-runtime-image-tag", "", "Set the Runtime build image full tag")

	// examples repository
	set.StringVar(&env.examplesRepositoryURI, prefix+"examples-uri", defaultKogitoExamplesURI, "Set the URI for the kogito-examples repository")
	set.StringVar(&env.examplesRepositoryRef, prefix+"examples-ref", "", "Set the branch for the kogito-examples repository")

	// dev options
	set.BoolVar(&env.showScenarios, prefix+"show-scenarios", false, "Show all scenarios which will be executed.")
	set.BoolVar(&env.showSteps, prefix+"show-steps", false, "Show all scenarios and their steps which will be executed.")
	set.BoolVar(&env.dryRun, prefix+"dry-run", false, "Dry Run the tests.")
	set.BoolVar(&env.keepNamespace, prefix+"keep-namespace", false, "Do not delete namespace(s) after scenario run (WARNING: can be resources consuming ...)")
	set.StringVar(&env.namespaceName, developmentOptionsPrefix+"namespace-name", "", "Use the specified namespace for scenarios, don't generate random namespace.")
}

// tests configuration

// IsSmokeTests return whether smoke tests should be executed
func IsSmokeTests() bool {
	return env.smoke
}

// IsPerformanceTests return whether performance tests should be executed
func IsPerformanceTests() bool {
	return env.performance
}

// GetLoadFactor return the load factor of the cluster
func GetLoadFactor() int {
	return env.loadFactor
}

// IsLocalTests return whether tests are executed in local
func IsLocalTests() bool {
	return env.localTests
}

// GetCiName return the CI name that executes the tests, if any
func GetCiName() string {
	return env.ciName
}

// IsCrDeploymentOnly returns whether the deployment should be done only with CR
func IsCrDeploymentOnly() bool {
	return env.crDeploymentOnly
}

// operator information

// GetOperatorImageName return the image name for the operator
func GetOperatorImageName() string {
	return env.operatorImageName
}

// GetOperatorImageTag return the image tag for the operator
func GetOperatorImageTag() string {
	return env.operatorImageTag
}

// files/binaries

// GetOperatorDeployURI return the uri for deployment folder
func GetOperatorDeployURI() string {
	return env.operatorDeployURI
}

// GetOperatorCliPath return the path to the kogito CLI binary
func GetOperatorCliPath() (string, error) {
	return filepath.Abs(env.cliPath)
}

// runtime

// GetServicesImageVersion return the version for the services images
func GetServicesImageVersion() string {
	return env.servicesImageVersion
}

// GetServicesImageRegistry return the registry for the services images
func GetServicesImageRegistry() string {
	return env.servicesImageRegistry
}

// GetServicesImageNamespace return the namespace for the services images
func GetServicesImageNamespace() string {
	return env.servicesImageNamespace
}

// GetDataIndexImageTag return the Kogito Data Index image tag
func GetDataIndexImageTag() string {
	return env.dataIndexImageTag
}

// GetJobsServiceImageTag return the Kogito Jobs Service image tag
func GetJobsServiceImageTag() string {
	return env.jobsServiceImageTag
}

// GetManagementConsoleImageTag return the Kogito Management Console image tag
func GetManagementConsoleImageTag() string {
	return env.mgmtConsoleImageTag
}

// build

// GetMavenMirrorURL return the maven mirror url used for building applications
func GetMavenMirrorURL() string {
	return env.mavenMirrorURL
}

// GetBuildImageVersion return the version for the build images
func GetBuildImageVersion() string {
	return env.buildImageVersion
}

// GetBuildImageNamespace return the namespace for the build images
func GetBuildImageNamespace() string {
	return env.buildImageNamespace
}

// GetBuildImageRegistry return the registry for the build images
func GetBuildImageRegistry() string {
	return env.buildImageRegistry
}

// GetBuildS2IImageStreamTag return the tag for the s2i build image
func GetBuildS2IImageStreamTag() string {
	return env.buildS2iImageTag
}

// GetBuildRuntimeImageStreamTag return the tag for the runtime build image
func GetBuildRuntimeImageStreamTag() string {
	return env.buildRuntimeImageTag
}

// examples repository

// GetExamplesRepositoryURI return the uri for the examples repository
func GetExamplesRepositoryURI() string {
	return env.examplesRepositoryURI
}

// GetExamplesRepositoryRef return the branch for the examples repository
func GetExamplesRepositoryRef() string {
	return env.examplesRepositoryRef
}

// dev options

// IsShowScenarios return whether we should display scenarios
func IsShowScenarios() bool {
	return env.showScenarios
}

// IsShowSteps return whether we should display scenarios's steps
func IsShowSteps() bool {
	return env.showSteps
}

// IsDryRun return whether we should do a dry run
func IsDryRun() bool {
	return env.dryRun
}

// IsKeepNamespace return whether we should keep namespace after scenario run
func IsKeepNamespace() bool {
	return env.keepNamespace
}

// GetNamespaceName return namespace name if it was defined
func GetNamespaceName() string {
	return env.namespaceName
}
