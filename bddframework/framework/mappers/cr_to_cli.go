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

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

//GetServiceCLIFlags returns CLI flags based on Kogito service passed in parameter
func GetServiceCLIFlags(serviceHolder *bddtypes.KogitoServiceHolder) []string {
	cmd := []string{}

	for _, envVar := range serviceHolder.GetSpec().GetEnvs() {
		cmd = append(cmd, "--env", fmt.Sprintf("%s=%s", envVar.Name, envVar.Value))
	}

	image := framework.ConvertImageToImageTag(*serviceHolder.GetSpec().GetImage())
	if len(image) > 0 {
		cmd = append(cmd, "--image", image)
	}

	cmd = append(cmd, "--replicas", strconv.Itoa(int(*serviceHolder.GetSpec().GetReplicas())))

	if infinispanAware, ok := serviceHolder.GetSpec().(v1alpha1.InfinispanAware); ok {
		infinispanProperties := infinispanAware.GetInfinispanProperties()
		if authRealm := infinispanProperties.AuthRealm; len(authRealm) > 0 {
			cmd = append(cmd, "--infinispan-authrealm", authRealm)
		}
		if saslMechanism := infinispanProperties.SaslMechanism; len(saslMechanism) > 0 {
			cmd = append(cmd, "--infinispan-sasl", string(saslMechanism))
		}
		if uri := infinispanProperties.URI; len(uri) > 0 {
			cmd = append(cmd, "--infinispan-url", uri)
		}
		if infinispanProperties.UseKogitoInfra {
			cmd = append(cmd, "--enable-persistence")
		}

		if username := serviceHolder.Infinispan.Username; len(username) > 0 {
			cmd = append(cmd, "--infinispan-user", username)
		}
		if password := serviceHolder.Infinispan.Password; len(password) > 0 {
			cmd = append(cmd, "--infinispan-password", password)
		}
	}

	if kafkaAware, ok := serviceHolder.GetSpec().(v1alpha1.KafkaAware); ok {
		kafkaProperties := kafkaAware.GetKafkaProperties()
		if externalURI := kafkaProperties.ExternalURI; len(externalURI) > 0 {
			cmd = append(cmd, "--kafka-url", externalURI)
		}
		if instance := kafkaProperties.Instance; len(instance) > 0 {
			cmd = append(cmd, "--kafka-instance", instance)
		}
		if kafkaProperties.UseKogitoInfra {
			cmd = append(cmd, "--enable-events")
		}
	}

	if httpPort := serviceHolder.GetSpec().GetHTTPPort(); httpPort > 0 {
		cmd = append(cmd, "--http-port", strconv.Itoa(int(httpPort)))
	}

	if kogitoRuntime, ok := serviceHolder.KogitoService.(*v1alpha1.KogitoRuntime); ok {
		if runtime := kogitoRuntime.Spec.Runtime; len(runtime) > 0 {
			cmd = append(cmd, "--runtime", string(runtime))
		}
	}

	return cmd
}

//GetBuildCLIFlags returns CLI flags based on KogitoBuild passed in parameter
func GetBuildCLIFlags(kogitoBuild *v1alpha1.KogitoBuild) []string {
	cmd := []string{}

	if reference := kogitoBuild.Spec.GitSource.Reference; len(reference) > 0 {
		cmd = append(cmd, "--branch", reference)
	}

	for _, envVar := range kogitoBuild.Spec.Envs {
		cmd = append(cmd, "--build-env", fmt.Sprintf("%s=%s", envVar.Name, envVar.Value))
	}

	image := framework.ConvertImageToImageTag(kogitoBuild.Spec.BuildImage)
	if len(image) > 0 {
		cmd = append(cmd, "--image-s2i", image)
	}

	for resourceName, quantity := range kogitoBuild.Spec.Resources.Limits {
		cmd = append(cmd, "--build-limits", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}

	for resourceName, quantity := range kogitoBuild.Spec.Resources.Requests {
		cmd = append(cmd, "--build-requests", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}
	if contextDir := kogitoBuild.Spec.GitSource.ContextDir; len(contextDir) > 0 {
		cmd = append(cmd, "--context-dir", contextDir)
	}

	if mavenMirrorURL := kogitoBuild.Spec.MavenMirrorURL; len(mavenMirrorURL) > 0 {
		cmd = append(cmd, "--maven-mirror-url", mavenMirrorURL)
	}

	if kogitoBuild.Spec.Native {
		cmd = append(cmd, "--native")
	}

	image = framework.ConvertImageToImageTag(kogitoBuild.Spec.RuntimeImage)
	if len(image) > 0 {
		cmd = append(cmd, "--image-runtime", image)
	}

	// webhooks
	if len(kogitoBuild.Spec.WebHooks) > 0 {
		for _, webhook := range kogitoBuild.Spec.WebHooks {
			cmd = append(cmd, "--web-hook", fmt.Sprintf("%s=%s", webhook.Type, webhook.Secret))
		}
	}

	return cmd
}
