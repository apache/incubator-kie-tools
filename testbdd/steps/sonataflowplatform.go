// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package steps

import (
	"fmt"
	"github.com/cucumber/godog"
	kogitoFramework "github.com/kiegroup/kogito-operator/test/pkg/framework"
	"github.com/kiegroup/kogito-serverless-operator/test"
	"github.com/kiegroup/kogito-serverless-operator/test/utils"
	"path/filepath"
	"strings"
	//"github.com/kiegroup/kogito-serverless-operator/test"
	"os"
)

const (
	minikubePlatform  = "minikube"
	openshiftPlatform = "openshift"
)

func registerPlatformSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^SonataFlowPlatform is deployed$`, data.sonataFlowPlatformIsDeployed)
}

func (data *Data) sonataFlowPlatformIsDeployed() error {
	projectDir, _ := utils.GetProjectDir()
	projectDir = strings.Replace(projectDir, "/testbdd", "", -1)

	// TODO or kubectl
	out, err := kogitoFramework.CreateCommand("oc", "apply", "-f", filepath.Join(projectDir, getSonataFlowPlatformFilename()), "-n", data.Namespace).Execute()

	if err != nil {
		kogitoFramework.GetLogger(data.Namespace).Error(err, fmt.Sprintf("Applying SonataFlowPlatform failed, output: %s", out))
	}

	return err
}

func getSonataFlowPlatformFilename() string {
	if getClusterPlatform() == openshiftPlatform {
		return test.GetPlatformOpenshiftE2eTest()
	}
	return test.GetPlatformMinikubeE2eTest()
}

func getClusterPlatform() string {
	if v, ok := os.LookupEnv("CLUSTER_PLATFORM"); ok {
		return v
	}
	return minikubePlatform
}
