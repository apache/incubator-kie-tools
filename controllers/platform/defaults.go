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

	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
)

func ConfigureDefaults(ctx context.Context, c client.Client, p *operatorapi.SonataFlowPlatform, verbose bool) error {
	// update missing fields in the resource
	if p.Status.Cluster == "" || utils.IsOpenShift() {
		p.Status.Cluster = operatorapi.PlatformClusterOpenShift
		p.Spec.Build.Config.BuildStrategy = operatorapi.PlatformBuildStrategy
	}
	if p.Status.Cluster == "" || !utils.IsOpenShift() {
		p.Status.Cluster = operatorapi.PlatformClusterKubernetes
		p.Spec.Build.Config.BuildStrategy = operatorapi.OperatorBuildStrategy
	}

	err := SetPlatformDefaults(p, verbose)
	if err != nil {
		return err
	}

	err = ConfigureRegistry(ctx, c, p, verbose)
	if err != nil {
		return err
	}

	if verbose && p.Spec.Build.Config.Timeout.Duration != 0 {
		klog.V(log.I).InfoS("Maven Timeout set", "timeout", p.Spec.Build.Config.Timeout.Duration)
	}

	updatePlatform(ctx, c, p)

	return nil
}

func updatePlatform(ctx context.Context, c client.Client, p *operatorapi.SonataFlowPlatform) {
	config := operatorapi.SonataFlowPlatform{}
	errGet := c.Get(ctx, ctrl.ObjectKey{Namespace: p.Namespace, Name: p.Name}, &config)
	if errGet != nil {
		klog.V(log.E).ErrorS(errGet, "Error reading the Platform")
	}
	config.Spec = p.Spec
	config.Status.Cluster = p.Status.Cluster

	updateErr := c.Update(ctx, &config)
	if updateErr != nil {
		klog.V(log.E).ErrorS(updateErr, "Error updating the BuildPlatform")
	}
}
