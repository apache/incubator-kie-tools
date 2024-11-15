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
	"runtime"
	"time"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/workflowdef"
	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sigs.k8s.io/yaml"

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
)

const defaultSonataFlowPlatformName = "sonataflow-platform"

func CreateOrUpdateWithDefaults(ctx context.Context, p *operatorapi.SonataFlowPlatform, verbose bool) error {
	// update missing fields in the resource
	if p.Status.Cluster == "" || utils.IsOpenShift() {
		p.Status.Cluster = operatorapi.PlatformClusterOpenShift
		p.Spec.Build.Config.BuildStrategy = operatorapi.PlatformBuildStrategy
	}
	if p.Status.Cluster == "" || !utils.IsOpenShift() {
		p.Status.Cluster = operatorapi.PlatformClusterKubernetes
		p.Spec.Build.Config.BuildStrategy = operatorapi.OperatorBuildStrategy
	}

	err := setPlatformDefaults(p, verbose)
	if err != nil {
		return err
	}

	err = configureRegistry(ctx, p, verbose)
	if err != nil {
		return err
	}

	if verbose && p.Spec.Build.Config.Timeout.Duration != 0 {
		klog.V(log.I).InfoS("Maven Timeout set", "timeout", p.Spec.Build.Config.Timeout.Duration)
	}

	return createPlatformIfNotExists(ctx, p)
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

func configureRegistry(ctx context.Context, p *operatorapi.SonataFlowPlatform, verbose bool) error {
	if p.Spec.Build.Config.BuildStrategy == operatorapi.PlatformBuildStrategy && p.Status.Cluster == operatorapi.PlatformClusterOpenShift {
		p.Spec.Build.Config.Registry = operatorapi.RegistrySpec{}
		klog.V(log.D).InfoS("Platform registry not set and ignored on openshift cluster")
		return nil
	}

	if p.Spec.Build.Config.Registry.Address == "" && p.Status.Cluster == operatorapi.PlatformClusterKubernetes {
		// try KEP-1755
		address, err := getRegistryAddress(ctx)
		if err != nil && verbose {
			klog.V(log.E).ErrorS(err, "Cannot find a registry where to push images via KEP-1755")
		} else if err == nil && address != nil {
			p.Spec.Build.Config.Registry.Address = *address
		}
	}

	klog.V(log.D).InfoS("Final Registry Address", "address", p.Spec.Build.Config.Registry.Address)
	return nil
}

// getRegistryAddress KEP-1755
// https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry
func getRegistryAddress(ctx context.Context) (*string, error) {
	config := corev1.ConfigMap{}
	err := utils.GetClient().Get(ctx, ctrl.ObjectKey{Namespace: "kube-public", Name: "local-registry-hosting"}, &config)
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

func createPlatformIfNotExists(ctx context.Context, p *operatorapi.SonataFlowPlatform) error {
	newPlt := operatorapi.SonataFlowPlatform{}
	err := utils.GetClient().Get(ctx, ctrl.ObjectKey{Namespace: p.Namespace, Name: p.Name}, &newPlt)
	if errors.IsNotFound(err) {
		klog.V(log.D).ErrorS(err, "Platform not found, creating it")
		return utils.GetClient().Create(ctx, p)
	}

	// FIXME: We should never update the object within methods like this, but let the actual reconciler to do it
	// https://github.com/apache/incubator-kie-tools/packages/sonataflow-operator/issues/538
	if err = SafeUpdatePlatformStatus(ctx, p); err != nil {
		klog.V(log.E).ErrorS(err, "Error updating the platform status")
		return err
	}

	// FIXME: We should never update the object within methods like this, but let the actual reconciler to do it
	// https://github.com/apache/incubator-kie-tools/packages/sonataflow-operator/issues/538
	if err = SafeUpdatePlatform(ctx, p); err != nil {
		klog.V(log.E).ErrorS(err, "Error updating the platform")
		return err
	}
	return nil
}
