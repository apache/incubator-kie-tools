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

package mappers

import (
	"fmt"
	"strconv"

	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	bddtypes "github.com/kiegroup/kogito-operator/test/pkg/types"
)

//GetServiceCLIFlags returns CLI flags based on Kogito service passed in parameter
func GetServiceCLIFlags(serviceHolder *bddtypes.KogitoServiceHolder) []string {
	var cmd []string

	// Flags ordered alphabetically

	for _, envVar := range serviceHolder.GetSpec().GetEnvs() {
		cmd = append(cmd, "--env", fmt.Sprintf("%s=%s", envVar.Name, envVar.Value))
	}

	image := serviceHolder.GetSpec().GetImage()
	if len(image) > 0 {
		cmd = append(cmd, "--image", image)
	}

	for _, infra := range serviceHolder.GetSpec().GetInfra() {
		cmd = append(cmd, "--infra", infra)
	}

	for resourceName, quantity := range serviceHolder.GetSpec().GetResources().Limits {
		cmd = append(cmd, "--limits", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}

	cmd = append(cmd, "--replicas", strconv.Itoa(int(*serviceHolder.GetSpec().GetReplicas())))

	for resourceName, quantity := range serviceHolder.GetSpec().GetResources().Requests {
		cmd = append(cmd, "--requests", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}

	for labelName, labelValue := range serviceHolder.GetSpec().GetServiceLabels() {
		cmd = append(cmd, "--svc-labels", fmt.Sprintf("%s=%s", labelName, labelValue))
	}

	if kogitoRuntime, ok := serviceHolder.KogitoService.(*v1beta1.KogitoRuntime); ok {
		if runtime := kogitoRuntime.GetSpec().GetRuntime(); len(runtime) > 0 {
			cmd = append(cmd, "--runtime", string(runtime))
		}
	}

	return cmd
}

//GetBuildCLIFlags returns CLI flags based on KogitoBuild passed in parameter
func GetBuildCLIFlags(kogitoBuild api.KogitoBuildInterface) []string {
	var cmd []string

	// Flags ordered alphabetically

	if reference := kogitoBuild.GetSpec().GetGitSource().GetReference(); len(reference) > 0 {
		cmd = append(cmd, "--branch", reference)
	}

	for _, envVar := range kogitoBuild.GetSpec().GetEnv() {
		cmd = append(cmd, "--build-env", fmt.Sprintf("%s=%s", envVar.Name, envVar.Value))
	}

	for resourceName, quantity := range kogitoBuild.GetSpec().GetResources().Limits {
		cmd = append(cmd, "--build-limits", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}

	for resourceName, quantity := range kogitoBuild.GetSpec().GetResources().Requests {
		cmd = append(cmd, "--build-requests", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}
	if contextDir := kogitoBuild.GetSpec().GetGitSource().GetContextDir(); len(contextDir) > 0 {
		cmd = append(cmd, "--context-dir", contextDir)
	}

	image := kogitoBuild.GetSpec().GetRuntimeImage()
	if len(image) > 0 {
		cmd = append(cmd, "--image-runtime", image)
	}

	image = kogitoBuild.GetSpec().GetBuildImage()
	if len(image) > 0 {
		cmd = append(cmd, "--image-s2i", image)
	}

	if mavenMirrorURL := kogitoBuild.GetSpec().GetMavenMirrorURL(); len(mavenMirrorURL) > 0 {
		cmd = append(cmd, "--maven-mirror-url", mavenMirrorURL)
	}

	if kogitoBuild.GetSpec().IsNative() {
		cmd = append(cmd, "--native")
	}

	// webhooks
	if len(kogitoBuild.GetSpec().GetWebHooks()) > 0 {
		for _, webhook := range kogitoBuild.GetSpec().GetWebHooks() {
			cmd = append(cmd, "--web-hook", fmt.Sprintf("%s=%s", webhook.GetType(), webhook.GetSecret()))
		}
	}

	return cmd
}

//GetInfraCLIFlags returns CLI flags based on KogitoInfra passed in parameter
func GetInfraCLIFlags(infraResource api.KogitoInfraInterface) []string {
	var cmd []string

	// Flags ordered alphabetically

	if apiVersion := infraResource.GetSpec().GetResource().GetAPIVersion(); len(apiVersion) > 0 {
		cmd = append(cmd, "--apiVersion", apiVersion)
	}

	if kind := infraResource.GetSpec().GetResource().GetKind(); len(kind) > 0 {
		cmd = append(cmd, "--kind", kind)
	}

	if resourceName := infraResource.GetSpec().GetResource().GetName(); len(resourceName) > 0 {
		cmd = append(cmd, "--resource-name", resourceName)
	}

	if resourceNamespace := infraResource.GetSpec().GetResource().GetNamespace(); len(resourceNamespace) > 0 {
		cmd = append(cmd, "--resource-namespace", resourceNamespace)
	}

	for key, value := range infraResource.GetSpec().GetInfraProperties() {
		cmd = append(cmd, "--property", fmt.Sprintf("%s=%s", key, value))
	}

	return cmd
}
