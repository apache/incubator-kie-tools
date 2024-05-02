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
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
)

const defaultSonataFlowPlatformName = "sonataflow-platform"

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

	err := setPlatformDefaults(p, verbose)
	if err != nil {
		return err
	}

	err = configureRegistry(ctx, c, p, verbose)
	if err != nil {
		return err
	}

	if verbose && p.Spec.Build.Config.Timeout.Duration != 0 {
		klog.V(log.I).InfoS("Maven Timeout set", "timeout", p.Spec.Build.Config.Timeout.Duration)
	}

	return createOrUpdatePlatform(ctx, c, p)
}

func createOrUpdatePlatform(ctx context.Context, c client.Client, p *operatorapi.SonataFlowPlatform) error {
	config := operatorapi.SonataFlowPlatform{}
	err := c.Get(ctx, ctrl.ObjectKey{Namespace: p.Namespace, Name: p.Name}, &config)
	if errors.IsNotFound(err) {
		klog.V(log.D).ErrorS(err, "Platform not found, creating it")
		return c.Create(ctx, p)
	} else if err != nil {
		klog.V(log.E).ErrorS(err, "Error reading the Platform")
		return err
	}

	config.Spec = p.Spec
	config.Status.Cluster = p.Status.Cluster
	err = c.Update(ctx, &config)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Error updating the BuildPlatform")
	}
	return err
}

func newDefaultSonataFlowPlatform(namespace string) *operatorapi.SonataFlowPlatform {
	if utils.IsOpenShift() {
		return &operatorapi.SonataFlowPlatform{
			ObjectMeta: metav1.ObjectMeta{Name: defaultSonataFlowPlatformName, Namespace: namespace},
			Spec: operatorapi.SonataFlowPlatformSpec{
				Build: operatorapi.BuildPlatformSpec{
					Config: operatorapi.BuildPlatformConfig{
						BuildStrategy: operatorapi.PlatformBuildStrategy,
					},
				},
			},
		}
	}

	return &operatorapi.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{Name: defaultSonataFlowPlatformName, Namespace: namespace},
		Spec: operatorapi.SonataFlowPlatformSpec{
			Build: operatorapi.BuildPlatformSpec{
				Config: operatorapi.BuildPlatformConfig{
					BuildStrategyOptions: map[string]string{
						kanikoBuildCacheEnabled: "true",
					},
				},
			},
		},
	}
}
