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

package config

import (
	"fmt"
	"path/filepath"

	flag "github.com/spf13/pflag"
)

// TestConfig contains the information about the tests environment
type TestConfig struct {
	// tests configuration
	smoke            bool
	performance      bool
	loadFactor       int
	ciName           string
	crDeploymentOnly bool
	containerEngine  string
	domainSuffix     string
	imageCacheMode   string
	httpRetryNumber  int
	olmNamespace     string

	// operator information
	operatorImageTag           string
	operatorInstallationSource string
	operatorCatalogImage       string
	useProductOperator         bool

	// profiling
	operatorProfiling                  bool
	operatorProfilingDataAccessYamlURI string
	operatorProfilingOutputFileURI     string

	// files/binaries
	operatorYamlURI      string
	rhpamOperatorYamlURI string
	cliPath              string

	// runtime
	servicesImageTags                 imageTags
	servicesImageRegistry             string
	servicesImageNameSuffix           string
	servicesImageVersion              string
	runtimeApplicationImageRegistry   string
	runtimeApplicationImageNamePrefix string
	runtimeApplicationImageNameSuffix string
	runtimeApplicationImageVersion    string

	// build
	customMavenRepoURL                 string
	customMavenRepoReplaceDefault      bool
	mavenMirrorURL                     string
	quarkusPlatformMavenMirrorURL      string
	mavenIgnoreSelfSignedCertificate   bool
	buildBuilderImageTag               string
	buildRuntimeJVMImageTag            string
	buildRuntimeNativeImageTag         string
	disableMavenNativeBuildInContainer bool
	nativeBuilderImage                 string

	// examples repository
	examplesRepositoryURI       string
	examplesRepositoryRef       string
	examplesRepositoryIgnoreSSL bool

	// Infinispan
	infinispanInstallationSource string
	infinispanStorageClass       string

	// Hyperfoil
	hyperfoilOutputDirectory        string
	hyperfoilControllerImageVersion string

	// dev options
	showScenarios bool
	showSteps     bool
	dryRun        bool
	keepNamespace bool
	namespaceName string
	localCluster  bool
	localTests    bool
}

const (
	defaultOperatorYamlURI      = "../operator.yaml"
	defaultRhpamOperatorYamlURI = "../rhpam-operator.yaml"
	defaultCliPath              = "../build/_output/bin/kogito"

	defaultOperatorProfilingDataAccessYamlURI = "../profiling/kogito-operator-profiling-data-access.yaml"
	defaultOperatorProfilingOutputFileURI     = "./bdd-cover.out"

	defaultKogitoExamplesURI = "https://github.com/kiegroup/kogito-examples"

	defaultLoadFactor      = 1
	defaultHTTPRetryNumber = 3

	defaultContainerEngine = "podman"

	installationSourceOlm  = "olm"
	installationSourceYaml = "yaml"
)

var (
	env = TestConfig{}
)

// BindFlags binds BDD tests env flags to given flag set
func BindFlags(set *flag.FlagSet) {
	prefix := "tests."
	developmentOptionsPrefix := prefix + "dev."

	// tests configuration
	set.BoolVar(&env.smoke, prefix+"smoke", false, "Launch only smoke tests")
	set.BoolVar(&env.performance, prefix+"performance", false, "Launch performance tests")
	set.IntVar(&env.loadFactor, prefix+"load_factor", defaultLoadFactor, "Set the tests load factor. Useful for the tests to take into account that the cluster can be overloaded, for example for the calculation of timeouts. Default value is 1.")
	set.BoolVar(&env.localTests, prefix+"local_execution", false, "If tests are launch on local machine using either a local or remote cluster")
	set.StringVar(&env.ciName, prefix+"ci", "", "If tests are launch on ci machine, give the CI name")
	set.BoolVar(&env.crDeploymentOnly, prefix+"cr_deployment_only", false, "Use this option if you have no CLI to test against. It will use only direct CR deployments.")
	set.StringVar(&env.containerEngine, prefix+"container_engine", defaultContainerEngine, "Engine used to interact with images and local containers.")
	set.StringVar(&env.domainSuffix, prefix+"domain_suffix", "", "Set the domain suffix for exposed services. Ignored when running tests on Openshift.")
	set.StringVar(&env.imageCacheMode, prefix+"image_cache_mode", "if-available", "Use this option to specify whether you want to use image cache for runtime images. Available options are 'always', 'never' or 'if-available'(default).")
	set.IntVar(&env.httpRetryNumber, prefix+"http_retry_nb", defaultHTTPRetryNumber, "Set the retry number for all HTTP calls in case it fails (and response code != 500). Default value is 3.")
	set.StringVar(&env.olmNamespace, prefix+"olm_namespace", "", "Set the namespace which is used for cluster scope operators. Default is 'openshift-operators'.")

	// operator information
	set.StringVar(&env.operatorImageTag, prefix+"operator_image_tag", "", "Operator image full tag")
	set.StringVar(&env.operatorInstallationSource, prefix+"operator_installation_source", installationSourceYaml, "Operator installation source")
	set.StringVar(&env.operatorCatalogImage, prefix+"operator_catalog_image", "", "Operator catalog image")
	set.BoolVar(&env.useProductOperator, prefix+"use_product_operator", false, "Set to true to deploy RHPAM Kogito operator, false for using Kogito operator. Default is false.")

	// operator profiling
	set.BoolVar(&env.operatorProfiling, prefix+"operator_profiling_enabled", false, "Enable the profiling of the operator. If enabled, operator will be automatically deployed with yaml files.")
	set.StringVar(&env.operatorProfilingDataAccessYamlURI, prefix+"operator_profiling_data_access_yaml_uri", defaultOperatorProfilingDataAccessYamlURI, "Url or Path to kogito-operator-profiling-data-access.yaml file.")
	set.StringVar(&env.operatorProfilingOutputFileURI, prefix+"operator_profiling_output_file_uri", defaultOperatorProfilingOutputFileURI, "Url or Path where to store the profiling outputs.")

	// files/binaries
	set.StringVar(&env.operatorYamlURI, prefix+"operator_yaml_uri", defaultOperatorYamlURI, "Url or Path to kogito-operator.yaml file")
	set.StringVar(&env.rhpamOperatorYamlURI, prefix+"rhpam_operator_yaml_uri", defaultRhpamOperatorYamlURI, "Url or Path to kogito-operator.yaml file")
	set.StringVar(&env.cliPath, prefix+"cli_path", defaultCliPath, "Path to built CLI to test")

	// runtime
	addAllPersistenceTypesImageTagFlags(set, &env.servicesImageTags, prefix+"services")
	set.StringVar(&env.servicesImageRegistry, prefix+"services_image_registry", "", "Set the global services image registry")
	set.StringVar(&env.servicesImageNameSuffix, prefix+"services_image_name_suffix", "", "Set the global services image name suffix")
	set.StringVar(&env.servicesImageVersion, prefix+"services_image_version", "", "Set the global services image version")
	set.StringVar(&env.runtimeApplicationImageRegistry, prefix+"runtime_application_image_registry", "", "Set the runtime application (built Kogito application image) image registry")
	set.StringVar(&env.runtimeApplicationImageNamePrefix, prefix+"runtime_application_image_name_prefix", "", "Set the runtime application (built Kogito application image) image name prefix")
	set.StringVar(&env.runtimeApplicationImageNameSuffix, prefix+"runtime_application_image_name_suffix", "", "Set the runtime application (built Kogito application image) image name suffix")
	set.StringVar(&env.runtimeApplicationImageVersion, prefix+"runtime_application_image_version", "", "Set the runtime application (built Kogito application image) image version")

	// build
	set.StringVar(&env.customMavenRepoURL, prefix+"custom_maven_repo_url", "", "Set a custom Maven repository url for S2I builds, in case your artifacts are in a specific repository. See https://github.com/kiegroup/kogito-images/README.md for more information")
	set.BoolVar(&env.customMavenRepoReplaceDefault, prefix+"custom_maven_repo_replace_default", false, "If you specified the option 'tests.custom_maven_repo_url' and you want that one to replace the main Apache repository (useful with snapshots).")
	set.StringVar(&env.mavenMirrorURL, prefix+"maven_mirror_url", "", "Maven mirror url to be used when building app in the tests")
	set.StringVar(&env.quarkusPlatformMavenMirrorURL, prefix+"quarkusPlatformMavenMirrorURL", "", "Maven mirror url to be used when building app from source files with Quarkus, using the quarkus maven plugin.")
	set.BoolVar(&env.mavenIgnoreSelfSignedCertificate, prefix+"maven_ignore_self_signed_certificate", false, "Set to true if maven build need to ignore self-signed certificate. This could happen when using internal maven mirror url.")
	set.StringVar(&env.buildBuilderImageTag, prefix+"build_builder_image_tag", "", "Set the S2I build image full tag")
	set.StringVar(&env.buildRuntimeJVMImageTag, prefix+"build_runtime_jvm_image_tag", "", "Set the Runtime build image full tag")
	set.StringVar(&env.buildRuntimeNativeImageTag, prefix+"build_runtime_native_image_tag", "", "Set the Runtime build image full tag")
	set.BoolVar(&env.disableMavenNativeBuildInContainer, prefix+"disable_maven_native_build_container", false, "By default, Maven native builds are done in container (via container engine). Possibility to disable it.")
	set.StringVar(&env.nativeBuilderImage, prefix+"native_builder_image", "", "Force the native builder image.")

	// examples repository
	set.StringVar(&env.examplesRepositoryURI, prefix+"examples_uri", defaultKogitoExamplesURI, "Set the URI for the kogito-examples repository")
	set.StringVar(&env.examplesRepositoryRef, prefix+"examples_ref", "", "Set the branch for the kogito-examples repository")
	set.BoolVar(&env.examplesRepositoryIgnoreSSL, prefix+"examples_ignore_ssl", false, "Set to true to ignore SSL check when checking out examples repository")

	// Infinispan
	set.StringVar(&env.infinispanInstallationSource, prefix+"infinispan_installation_source", installationSourceOlm, "Infinispan operator installation source")
	set.StringVar(&env.infinispanStorageClass, prefix+"infinispan_storage_class", "", "Defines storage class for Infinispan PVC to be used.")

	// Hyperfoil
	set.StringVar(&env.hyperfoilOutputDirectory, prefix+"hyperfoil_output_directory", "..", "Defines output directory to store Hyperfoil run statistics. Default is Kogito operator base folder.")
	set.StringVar(&env.hyperfoilControllerImageVersion, prefix+"hyperfoil_controller_image_version", "", "Set the Hyperfoil controller image version")

	// dev options
	set.BoolVar(&env.showScenarios, prefix+"show_scenarios", false, "Show all scenarios which will be executed.")
	set.BoolVar(&env.showSteps, prefix+"show_steps", false, "Show all scenarios and their steps which will be executed.")
	set.BoolVar(&env.dryRun, prefix+"dry_run", false, "Dry Run the tests.")
	set.BoolVar(&env.keepNamespace, prefix+"keep_namespace", false, "Do not delete namespace(s) after scenario run (WARNING: can be resources consuming ...)")
	set.StringVar(&env.namespaceName, developmentOptionsPrefix+"namespace_name", "", "Use the specified namespace for scenarios, don't generate random namespace.")
	set.BoolVar(&env.localCluster, developmentOptionsPrefix+"local_cluster", false, "If tests are launch using a local cluster")
}

func addAllPersistenceTypesImageTagFlags(set *flag.FlagSet, imageTags *imageTags, keyPrefix string) {
	for imageType, persistenceTypes := range imageTypePersistenceMapping {
		for _, persistenceType := range persistenceTypes {
			addPersistenceTypeImageTagFlags(set, imageTags, imageType, persistenceType, keyPrefix)
		}
	}
}

func addPersistenceTypeImageTagFlags(set *flag.FlagSet, imageTags *imageTags, imageType ImageType, persistenceType ImagePersistenceType, keyPrefix string) {
	key := fmt.Sprintf("%s_%s", keyPrefix, imageType)
	description := fmt.Sprintf("Set the %s image tag", imageType)
	if len(persistenceType) > 0 {
		key = fmt.Sprintf("%s_%s", key, persistenceType)
		description = fmt.Sprintf("%s with %s persistence type. This overrides the `services_image_*` parameters.", description, persistenceType)
	}
	key = fmt.Sprintf("%s_image_tag", key)

	set.StringVar(imageTags.GetImageTagPointerFromPersistenceType(imageType, persistenceType), key, "", description)
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

// GetContainerEngine returns engine used to interact with images and local containers
func GetContainerEngine() string {
	return env.containerEngine
}

// GetDomainSuffix returns the domain suffix for exposed services
func GetDomainSuffix() string {
	return env.domainSuffix
}

// GetImageCacheMode returns image cache mode
func GetImageCacheMode() ImageCacheMode {
	return ImageCacheMode(env.imageCacheMode)
}

// GetHTTPRetryNumber return the number of retries to be applied for http calls
func GetHTTPRetryNumber() int {
	return env.httpRetryNumber
}

// GetOlmNamespace returns namespace which is used for cluster scope operators
func GetOlmNamespace() string {
	return env.olmNamespace
}

// operator information

// GetOperatorImageTag return the image tag for the operator
func GetOperatorImageTag() string {
	return env.operatorImageTag
}

// IsOperatorInstalledByOlm return true if Kogito operator is installed using OLM
func IsOperatorInstalledByOlm() bool {
	return env.operatorInstallationSource == installationSourceOlm
}

// IsOperatorInstalledByYaml return true if Kogito operator is installed using YAML files
func IsOperatorInstalledByYaml() bool {
	return env.operatorInstallationSource == installationSourceYaml
}

// GetOperatorCatalogImage return the image tag for the Kogito operator catalog
func GetOperatorCatalogImage() string {
	return env.operatorCatalogImage
}

// UseProductOperator return true if RHPAM Kogito operator should be used, false for Kogito operator
func UseProductOperator() bool {
	return env.useProductOperator
}

// operator profiling

// IsOperatorProfiling returns whether the operator profiling is activated
func IsOperatorProfiling() bool {
	return env.operatorProfiling
}

// GetOperatorProfilingDataAccessYamlURI return the uri for kogito-operator-profiling-data-access.yaml file
func GetOperatorProfilingDataAccessYamlURI() string {
	return env.operatorProfilingDataAccessYamlURI
}

// GetOperatorProfilingOutputFileURI return the uri for the profiling data output file
func GetOperatorProfilingOutputFileURI() string {
	return env.operatorProfilingOutputFileURI
}

// files/binaries

// GetOperatorYamlURI return the uri for kogito-operator.yaml file
func GetOperatorYamlURI() string {
	return env.operatorYamlURI
}

// GetRhpamOperatorYamlURI return the uri for rhpam-kogito-operator.yaml file
func GetRhpamOperatorYamlURI() string {
	return env.rhpamOperatorYamlURI
}

// GetOperatorCliPath return the path to the kogito CLI binary
func GetOperatorCliPath() (string, error) {
	return filepath.Abs(env.cliPath)
}

// runtime

// GetServiceImageTag returns the image tag based on the image type and the persistence type
func GetServiceImageTag(ImageType ImageType, persistenceType ImagePersistenceType) string {
	return *env.servicesImageTags.GetImageTagPointerFromPersistenceType(ImageType, persistenceType)
}

// GetServicesImageRegistry return the registry for the services images
func GetServicesImageRegistry() string {
	return env.servicesImageRegistry
}

// GetServicesImageNameSuffix return the name suffix for the services images
func GetServicesImageNameSuffix() string {
	return env.servicesImageNameSuffix
}

// GetServicesImageVersion return the version for the services images
func GetServicesImageVersion() string {
	return env.servicesImageVersion
}

// GetRuntimeApplicationImageRegistry return the registry for the runtime application images
func GetRuntimeApplicationImageRegistry() string {
	return env.runtimeApplicationImageRegistry
}

// GetRuntimeApplicationImageNamePrefix return the name prefix for runtime application images
func GetRuntimeApplicationImageNamePrefix() string {
	return env.runtimeApplicationImageNamePrefix
}

// GetRuntimeApplicationImageNameSuffix return the name suffix for runtime application images
func GetRuntimeApplicationImageNameSuffix() string {
	return env.runtimeApplicationImageNameSuffix
}

// GetRuntimeApplicationImageVersion return the version for runtime application images
func GetRuntimeApplicationImageVersion() string {
	return env.runtimeApplicationImageVersion
}

// build

// GetCustomMavenRepoURL return the custom maven repository url used by S2I builds
func GetCustomMavenRepoURL() string {
	return env.customMavenRepoURL
}

// IsCustomMavenRepoReplaceDefault return whether custom maven repo should replace the default JBoss repository
func IsCustomMavenRepoReplaceDefault() bool {
	return env.customMavenRepoReplaceDefault
}

// GetMavenMirrorURL return the maven mirror url used for building applications
func GetMavenMirrorURL() string {
	return env.mavenMirrorURL
}

// GetQuarkusPlatformMavenMirrorURL return the maven mirror url used for building applications from assets with quarkus platform
func GetQuarkusPlatformMavenMirrorURL() string {
	return env.quarkusPlatformMavenMirrorURL
}

// IsMavenIgnoreSelfSignedCertificate return whether self-signed certficate should be ignored
func IsMavenIgnoreSelfSignedCertificate() bool {
	return env.mavenIgnoreSelfSignedCertificate
}

// GetBuildBuilderImageStreamTag return the tag for the builder image
func GetBuildBuilderImageStreamTag() string {
	return env.buildBuilderImageTag
}

// GetBuildRuntimeJVMImageStreamTag return the tag for the runtime JVM image
func GetBuildRuntimeJVMImageStreamTag() string {
	return env.buildRuntimeJVMImageTag
}

// GetBuildRuntimeNativeImageStreamTag return the tag for the runtime native image
func GetBuildRuntimeNativeImageStreamTag() string {
	return env.buildRuntimeNativeImageTag
}

// IsDisableMavenNativeBuildInContainer return whether Maven native build in container should be disabled
func IsDisableMavenNativeBuildInContainer() bool {
	return env.disableMavenNativeBuildInContainer
}

// GetNativeBuilderImage return the native builder image for Maven native builds
func GetNativeBuilderImage() string {
	return env.nativeBuilderImage
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

// IsExamplesRepositoryIgnoreSSL return whether SSL should be ignored on Git checkout
func IsExamplesRepositoryIgnoreSSL() bool {
	return env.examplesRepositoryIgnoreSSL
}

// Infinispan

// IsInfinispanInstalledByOlm return true if Infinispan operator is installed using OLM
func IsInfinispanInstalledByOlm() bool {
	return env.infinispanInstallationSource == installationSourceOlm
}

// IsInfinispanInstalledByYaml return true if Infinispan operator is installed using YAML files
func IsInfinispanInstalledByYaml() bool {
	return env.infinispanInstallationSource == installationSourceYaml
}

// GetInfinispanStorageClass return the Infinispan storage class
func GetInfinispanStorageClass() string {
	return env.infinispanStorageClass
}

// Hyperfoil

// GetHyperfoilOutputDirectory returns directory to store Hyperfoil run results
func GetHyperfoilOutputDirectory() string {
	return env.hyperfoilOutputDirectory
}

// GetHyperfoilControllerImageVersion returns the Hyperfoil controller image version
func GetHyperfoilControllerImageVersion() string {
	return env.hyperfoilControllerImageVersion
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

// IsLocalCluster return whether tests are executed using a local cluster
func IsLocalCluster() bool {
	return env.localCluster
}
