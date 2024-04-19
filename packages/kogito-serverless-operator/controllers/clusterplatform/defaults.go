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

package clusterplatform

import (
	"context"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
)

func configureDefaults(ctx context.Context, c client.Client, cp *operatorapi.SonataFlowClusterPlatform, verbose bool) error {
	if cp.Spec.Capabilities == nil {
		cp.Spec.Capabilities = &operatorapi.SonataFlowClusterPlatformCapSpec{
			Workflows: []operatorapi.WorkFlowCapability{PlatformServices},
		}
	}

	return updateClusterPlatform(ctx, c, cp)
}

func updateClusterPlatform(ctx context.Context, c client.Client, cp *operatorapi.SonataFlowClusterPlatform) error {
	sfcPlatform := operatorapi.SonataFlowClusterPlatform{}
	if err := c.Get(ctx, ctrl.ObjectKey{Namespace: cp.Namespace, Name: cp.Name}, &sfcPlatform); err != nil {
		klog.V(log.E).ErrorS(err, "Error reading the Cluster Platform")
		return err
	}

	sfcPlatform.Spec = cp.Spec
	if err := c.Update(ctx, &sfcPlatform); err != nil {
		klog.V(log.E).ErrorS(err, "Error updating the Cluster Platform")
	}

	return nil
}
