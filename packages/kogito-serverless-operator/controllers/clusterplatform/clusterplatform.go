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

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	PlatformServices operatorapi.WorkFlowCapability = "services"
)

// GetActiveClusterPlatform returns the currently installed active cluster platform.
func GetActiveClusterPlatform(ctx context.Context, c ctrl.Client) (*operatorapi.SonataFlowClusterPlatform, error) {
	return getClusterPlatform(ctx, c, true)
}

// getClusterPlatform returns the currently active cluster platform or any cluster platform existing in the cluster.
func getClusterPlatform(ctx context.Context, c ctrl.Client, active bool) (*operatorapi.SonataFlowClusterPlatform, error) {
	klog.V(log.D).InfoS("Finding available cluster platforms")

	lst, err := listPrimaryClusterPlatforms(ctx, c)
	if err != nil {
		return nil, err
	}

	for _, cPlatform := range lst.Items {
		if IsActive(&cPlatform) {
			klog.V(log.D).InfoS("Found active cluster platform", "platform", cPlatform.Name)
			return &cPlatform, nil
		}
	}

	if !active && len(lst.Items) > 0 {
		// does not require the cluster platform to be active, just return one if present
		res := lst.Items[0]
		klog.V(log.D).InfoS("Found cluster platform", "platform", res.Name)
		return &res, nil
	}
	klog.V(log.I).InfoS("No cluster platform found")
	return nil, k8serrors.NewNotFound(operatorapi.Resource(operatorapi.SonataFlowClusterPlatformKind), "")
}

// listPrimaryClusterPlatforms returns all non-secondary cluster platforms installed (only one will be active).
func listPrimaryClusterPlatforms(ctx context.Context, c ctrl.Reader) (*operatorapi.SonataFlowClusterPlatformList, error) {
	lst, err := listAllClusterPlatforms(ctx, c)
	if err != nil {
		return nil, err
	}

	filtered := &operatorapi.SonataFlowClusterPlatformList{}
	for i := range lst.Items {
		cPl := lst.Items[i]
		if !IsSecondary(&cPl) {
			filtered.Items = append(filtered.Items, cPl)
		}
	}
	return filtered, nil
}

// allDuplicatedClusterPlatforms returns true if every cluster platform has a "Duplicated" status set
func allDuplicatedClusterPlatforms(ctx context.Context, c ctrl.Reader) bool {
	lst, err := listAllClusterPlatforms(ctx, c)
	if err != nil {
		return false
	}

	for i := range lst.Items {
		if !lst.Items[i].Status.IsDuplicated() {
			return false
		}
	}

	return true
}

// listAllClusterPlatforms returns all clusterplatforms installed.
func listAllClusterPlatforms(ctx context.Context, c ctrl.Reader) (*operatorapi.SonataFlowClusterPlatformList, error) {
	lst := operatorapi.NewSonataFlowClusterPlatformList()
	if err := c.List(ctx, &lst); err != nil {
		return nil, err
	}
	return &lst, nil
}

// IsActive determines if the given cluster platform is being used.
func IsActive(p *operatorapi.SonataFlowClusterPlatform) bool {
	return p.Status.IsReady() && !p.Status.IsDuplicated()
}

// IsSecondary determines if the given cluster platform is marked as secondary.
func IsSecondary(p *operatorapi.SonataFlowClusterPlatform) bool {
	if l, ok := p.Annotations[metadata.SecondaryPlatformAnnotation]; ok && l == "true" {
		return true
	}
	return false
}
