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

	"github.com/kiegroup/kogito-cloud-operator/pkg/util"
)

// SmokeEnv contains the information about the smoke tests environment
type SmokeEnv struct {
	LocalTests bool

	OperatorImageName string
	OperatorImageTag  string

	OperatorDeployURI string
	CliPath           string

	MavenMirrorURL       string
	BuildImageVersion    string
	BuildS2iImageTag     string
	BuildRuntimeImageTag string

	ExamplesRepositoryURI string
	ExamplesRepositoryRef string
}

const (
	defaultOperatorImageName = "quay.io/kiegroup/kogito-cloud-operator"

	defaultOperatorDeployURI = "../../deploy/"
	defaultCliPath           = "../../build/_output/bin/kogito"

	defaultKogitoExamplesURI = "https://github.com/kiegroup/kogito-examples"
)

var (
	defaultOperatorImageTag = version.Version

	env = SmokeEnv{}
)

// BindEnvFlags binds smoke tests env flags to given flag set
func BindEnvFlags(set *flag.FlagSet) {
	prefix := "smoke."

	set.BoolVar(&env.LocalTests, prefix+"local", false, "If tests are launch on local machine")
	set.StringVar(&env.OperatorImageName, prefix+"operator-image-name", defaultOperatorImageName, "Operator image name")
	set.StringVar(&env.OperatorImageTag, prefix+"operator-image-tag", defaultOperatorImageTag, "Operator image tag")
	set.StringVar(&env.OperatorDeployURI, prefix+"operator-deploy-uri", defaultOperatorDeployURI, "Url or Path to operator 'deploy' folder")
	set.StringVar(&env.CliPath, prefix+"cli-path", defaultCliPath, "Path to built CLI to test")
	set.StringVar(&env.MavenMirrorURL, prefix+"maven-mirror-url", "", "Maven mirror url to be used when building app in the tests")
	set.StringVar(&env.BuildImageVersion, prefix+"build-image-version", version.Version, "Set the build image version")
	set.StringVar(&env.BuildS2iImageTag, prefix+"build-s2i-image-tag", "", "Set the S2I build image full tag")
	set.StringVar(&env.BuildRuntimeImageTag, prefix+"build-runtime-image-tag", "", "Set the Runtime build image full tag")
	set.StringVar(&env.ExamplesRepositoryURI, prefix+"examples-uri", defaultKogitoExamplesURI, "Set the URI for the kogito-examples repository")
	set.StringVar(&env.ExamplesRepositoryRef, prefix+"examples-ref", "", "Set the branch for the kogito-examples repository")
}

func getEnvMavenMirrorURL() string {
	return env.MavenMirrorURL
}

func getEnvOperatorCliPath() (string, error) {
	return filepath.Abs(env.CliPath)
}

func getEnvOperatorDeployURI() string {
	return env.OperatorDeployURI
}

func getEnvOperatorImageName() string {
	return env.OperatorImageName
}

func getEnvOperatorImageTag() string {
	return env.OperatorImageTag
}

func getEnvExamplesRepositoryURI() string {
	return env.ExamplesRepositoryURI
}

func getEnvExamplesRepositoryRef() string {
	return env.ExamplesRepositoryRef
}

func getEnvImageVersion() string {
	return env.BuildImageVersion
}

func getEnvS2IImageStreamTag() string {
	return env.BuildS2iImageTag
}

func getEnvRuntimeImageStreamTag() string {
	return env.BuildRuntimeImageTag
}

func getEnvLocalTests() bool {
	return env.LocalTests
}

func getEnvUsername() string {
	return util.GetOSEnv("USERNAME", "nouser")
}
