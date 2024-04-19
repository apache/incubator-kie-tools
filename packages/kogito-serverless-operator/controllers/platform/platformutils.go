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

package platform

import (
	"context"
	"regexp"
	"runtime"
	"strings"
	"time"

	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/yaml"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/workflowdef"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
)

var builderDockerfileFromRE = regexp.MustCompile(`FROM (.*) AS builder`)

// ResourceCustomizer can be used to inject code that changes the objects before they are created.
type ResourceCustomizer func(object ctrl.Object) ctrl.Object

func configureRegistry(ctx context.Context, c client.Client, p *operatorapi.SonataFlowPlatform, verbose bool) error {
	if p.Spec.Build.Config.BuildStrategy == operatorapi.PlatformBuildStrategy && p.Status.Cluster == operatorapi.PlatformClusterOpenShift {
		p.Spec.Build.Config.Registry = operatorapi.RegistrySpec{}
		klog.V(log.D).InfoS("Platform registry not set and ignored on openshift cluster")
		return nil
	}

	if p.Spec.Build.Config.Registry.Address == "" && p.Status.Cluster == operatorapi.PlatformClusterKubernetes {
		// try KEP-1755
		address, err := GetRegistryAddress(ctx, c)
		if err != nil && verbose {
			klog.V(log.E).ErrorS(err, "Cannot find a registry where to push images via KEP-1755")
		} else if err == nil && address != nil {
			p.Spec.Build.Config.Registry.Address = *address
		}
	}

	klog.V(log.D).InfoS("Final Registry Address", "address", p.Spec.Build.Config.Registry.Address)
	return nil
}

func setPlatformDefaults(p *operatorapi.SonataFlowPlatform, verbose bool) error {
	if p.Spec.Build.Config.BuildStrategyOptions == nil {
		klog.V(log.D).InfoS("SonataFlow Platform: setting publish strategy options", "namespace", p.Namespace)
		p.Spec.Build.Config.BuildStrategyOptions = map[string]string{}
	}

	if p.Spec.Build.Config.GetTimeout().Duration != 0 {
		d := p.Spec.Build.Config.GetTimeout().Duration.Truncate(time.Second)

		if verbose && p.Spec.Build.Config.Timeout.Duration != d {
			klog.V(log.I).InfoS("ContainerBuild timeout minimum unit is sec", "configured", p.Spec.Build.Config.GetTimeout().Duration, "truncated", d)
		}

		klog.V(log.D).InfoS("SonataFlow Platform: setting build timeout", "namespace", p.Namespace)
		p.Spec.Build.Config.Timeout = &metav1.Duration{
			Duration: d,
		}
	} else {
		klog.V(log.D).InfoS("SonataFlow Platform setting default build timeout to 5 minutes", "namespace", p.Namespace)
		p.Spec.Build.Config.Timeout = &metav1.Duration{
			Duration: 5 * time.Minute,
		}
	}

	if p.Spec.Build.Config.IsStrategyOptionEnabled(kanikoBuildCacheEnabled) {
		p.Spec.Build.Config.BuildStrategyOptions[kanikoPVCName] = p.Name
		if len(p.Spec.Build.Config.BaseImage) == 0 {
			p.Spec.Build.Config.BaseImage = workflowdef.GetDefaultWorkflowBuilderImageTag()
		}
	}

	if p.Spec.Build.Config.BuildStrategy == operatorapi.OperatorBuildStrategy && !p.Spec.Build.Config.IsStrategyOptionEnabled(kanikoBuildCacheEnabled) {
		// Default to disabling Kaniko cache warmer
		// Using the cache warmer pod seems unreliable with the current Kaniko version
		// and requires relying on a persistent volume.
		defaultKanikoBuildCache := "false"
		p.Spec.Build.Config.BuildStrategyOptions[kanikoBuildCacheEnabled] = defaultKanikoBuildCache
		if verbose {
			klog.V(log.I).InfoS("Kaniko cache set", "value", defaultKanikoBuildCache)
		}
	}

	// When dataIndex object set, default to enabled if bool not set
	if p.Spec.Services != nil {
		var enable = true
		if p.Spec.Services.DataIndex != nil && p.Spec.Services.DataIndex.Enabled == nil {
			p.Spec.Services.DataIndex.Enabled = &enable
		}
		// When the JobService field has a value, default to enabled if the `Enabled` field's value is nil
		if p.Spec.Services.JobService != nil && p.Spec.Services.JobService.Enabled == nil {
			p.Spec.Services.JobService.Enabled = &enable
		}
	}
	setStatusAdditionalInfo(p)

	if verbose {
		klog.V(log.I).InfoS("baseImage set", "value", p.Spec.Build.Config.BaseImage)
		klog.V(log.I).InfoS("Timeout set", "value", p.Spec.Build.Config.GetTimeout())
	}
	return nil
}

func setStatusAdditionalInfo(platform *operatorapi.SonataFlowPlatform) {
	platform.Status.Info = make(map[string]string)

	klog.V(log.D).InfoS("SonataFlow setting status info", "namespace", platform.Namespace)
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

// GetCustomizedBuilderDockerfile gets the Dockerfile as defined in the default platform ConfigMap, apply any custom requirements and return.
func GetCustomizedBuilderDockerfile(dockerfile string, platform operatorapi.SonataFlowPlatform) string {
	if len(platform.Spec.Build.Config.BaseImage) > 0 {
		dockerfile = strings.Replace(dockerfile, GetFromImageTagDockerfile(dockerfile), platform.Spec.Build.Config.BaseImage, 1)
	}
	return dockerfile
}

func GetFromImageTagDockerfile(dockerfile string) string {
	res := builderDockerfileFromRE.FindAllStringSubmatch(dockerfile, 1)
	return strings.Trim(res[0][1], " ")
}
