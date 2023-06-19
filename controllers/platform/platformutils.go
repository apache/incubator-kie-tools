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

package platform

import (
	"context"
	"runtime"
	"time"

	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/yaml"

	"github.com/kiegroup/kogito-serverless-operator/controllers/workflowdef"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/client"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/defaults"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/log"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

// ResourceCustomizer can be used to inject code that changes the objects before they are created.
type ResourceCustomizer func(object ctrl.Object) ctrl.Object

func ConfigureRegistry(ctx context.Context, c client.Client, p *operatorapi.SonataFlowPlatform, verbose bool) error {
	//@TODO Add a notification on the status about this registry value ignored when https://issues.redhat.com/browse/KOGITO-9218 will be implemented
	if p.Spec.BuildPlatform.BuildStrategy == operatorapi.PlatformBuildStrategy && p.Status.Cluster == operatorapi.PlatformClusterOpenShift {
		p.Spec.BuildPlatform.Registry = operatorapi.RegistrySpec{}
		log.Info("Platform registry not set and ignored on openshift cluster")
		return nil
	}

	if p.Spec.BuildPlatform.Registry.Address == "" && p.Status.Cluster == operatorapi.PlatformClusterKubernetes {
		// try KEP-1755
		address, err := GetRegistryAddress(ctx, c)
		if err != nil && verbose {
			log.Error(err, "Cannot find a registry where to push images via KEP-1755")
		} else if err == nil && address != nil {
			p.Spec.BuildPlatform.Registry.Address = *address
		}
	}

	log.Debugf("Final Registry Address: %s", p.Spec.BuildPlatform.Registry.Address)
	return nil
}

func SetPlatformDefaults(p *operatorapi.SonataFlowPlatform, verbose bool) error {
	if p.Spec.BuildPlatform.BuildStrategyOptions == nil {
		log.Debugf("SonataFlow Platform [%s]: setting publish strategy options", p.Namespace)
		p.Spec.BuildPlatform.BuildStrategyOptions = map[string]string{}
	}

	if p.Spec.BuildPlatform.GetTimeout().Duration != 0 {
		d := p.Spec.BuildPlatform.GetTimeout().Duration.Truncate(time.Second)

		if verbose && p.Spec.BuildPlatform.Timeout.Duration != d {
			log.Log.Infof("ContainerBuild timeout minimum unit is sec (configured: %s, truncated: %s)", p.Spec.BuildPlatform.GetTimeout().Duration, d)
		}

		log.Debugf("SonataFlow Platform [%s]: setting build timeout", p.Namespace)
		p.Spec.BuildPlatform.Timeout = &metav1.Duration{
			Duration: d,
		}
	} else {
		log.Debugf("SonataFlow Platform [%s]: setting default build timeout to 5 minutes", p.Namespace)
		p.Spec.BuildPlatform.Timeout = &metav1.Duration{
			Duration: 5 * time.Minute,
		}
	}

	if p.Spec.BuildPlatform.IsOptionEnabled(kanikoBuildCacheEnabled) {
		p.Spec.BuildPlatform.BuildStrategyOptions[kanikoPVCName] = p.Name
		if len(p.Spec.BuildPlatform.BaseImage) == 0 {
			p.Spec.BuildPlatform.BaseImage = workflowdef.GetDefaultWorkflowBuilderImageTag()
		}
	}

	if p.Spec.BuildPlatform.BuildStrategy == operatorapi.OperatorBuildStrategy && !p.Spec.BuildPlatform.IsOptionEnabled(kanikoBuildCacheEnabled) {
		// Default to disabling Kaniko cache warmer
		// Using the cache warmer pod seems unreliable with the current Kaniko version
		// and requires relying on a persistent volume.
		defaultKanikoBuildCache := "false"
		p.Spec.BuildPlatform.BuildStrategyOptions[kanikoBuildCacheEnabled] = defaultKanikoBuildCache
		if verbose {
			log.Log.Infof("Kaniko cache set to %s", defaultKanikoBuildCache)
		}
	}

	setStatusAdditionalInfo(p)

	if verbose {
		log.Log.Infof("BaseImage set to %s", p.Spec.BuildPlatform.BaseImage)
		log.Log.Infof("Timeout set to %s", p.Spec.BuildPlatform.GetTimeout())
	}
	return nil
}

func setStatusAdditionalInfo(platform *operatorapi.SonataFlowPlatform) {
	platform.Status.Info = make(map[string]string)

	log.Debugf("SonataFlow Platform [%s]: setting build publish strategy", platform.Namespace)
	if platform.Spec.BuildPlatform.BuildStrategy == operatorapi.OperatorBuildStrategy {
		platform.Status.Info["kanikoVersion"] = defaults.KanikoVersion
	}
	log.Debugf("SonataFlow [%s]: setting status info", platform.Namespace)
	platform.Status.Info["goVersion"] = runtime.Version()
	platform.Status.Info["goOS"] = runtime.GOOS
}

// GetRegistryAddress KEP-1755
// https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry
func GetRegistryAddress(ctx context.Context, c client.Client) (*string, error) {
	config := corev1.ConfigMap{}
	err := c.Get(ctx, ctrl.ObjectKey{Namespace: "kube-public", Name: "local-registry-hosting"}, &config)
	if err != nil {
		if k8serrors.IsNotFound(err) {
			return nil, nil
		}
		return nil, err
	}
	if data, ok := config.Data["localRegistryHosting.v1"]; ok {
		result := LocalRegistryHostingV1{}
		if err := yaml.Unmarshal([]byte(data), &result); err != nil {
			return nil, err
		}
		return &result.HostFromClusterNetwork, nil
	}
	return nil, nil
}
