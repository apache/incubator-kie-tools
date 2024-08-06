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

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
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

	return createOrUpdatePlatform(ctx, p)
}

func createOrUpdatePlatform(ctx context.Context, p *operatorapi.SonataFlowPlatform) error {
	config := operatorapi.SonataFlowPlatform{}
	err := utils.GetClient().Get(ctx, ctrl.ObjectKey{Namespace: p.Namespace, Name: p.Name}, &config)
	if errors.IsNotFound(err) {
		klog.V(log.D).ErrorS(err, "Platform not found, creating it")
		return utils.GetClient().Create(ctx, p)
	} else if err != nil {
		klog.V(log.E).ErrorS(err, "Error reading the Platform")
		return err
	}

	config.Spec = p.Spec
	config.Status.Cluster = p.Status.Cluster
	err = utils.GetClient().Update(ctx, &config)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Error updating the BuildPlatform")
	}
	return err
}
