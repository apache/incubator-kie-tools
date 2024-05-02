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
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/klog/v2"
)

// NewInitializeAction returns an action that initializes the platform configuration when not provided by the user.
func NewInitializeAction() Action {
	return &initializeAction{}
}

type initializeAction struct {
	baseAction
}

func (action *initializeAction) Name() string {
	return "initialize"
}

func (action *initializeAction) CanHandle(ctx context.Context, cPlatform *operatorapi.SonataFlowClusterPlatform) bool {
	return !cPlatform.Status.IsDuplicated() || allDuplicatedClusterPlatforms(ctx, action.client)
}

func (action *initializeAction) Handle(ctx context.Context, cPlatform *operatorapi.SonataFlowClusterPlatform) error {
	duplicate, err := action.isPrimaryDuplicate(ctx, cPlatform)
	if err != nil {
		return err
	}
	if duplicate {
		// another cluster platform already present
		if !cPlatform.Status.IsDuplicated() {
			cPlatform.Status.Manager().MarkFalse(api.SucceedConditionType, operatorapi.PlatformDuplicatedReason, "")
		}
		return nil
	}

	if err = configureDefaults(ctx, action.client, cPlatform, true); err != nil {
		return err
	}
	cPlatform.Status.Version = metadata.SpecVersion
	platformRef := cPlatform.Spec.PlatformRef

	// Check referenced platform status
	platform := &operatorapi.SonataFlowPlatform{}
	err = action.client.Get(ctx, types.NamespacedName{Namespace: platformRef.Namespace, Name: platformRef.Name}, platform)
	if err != nil {
		if k8serrors.IsNotFound(err) {
			klog.V(log.D).InfoS("%s platform does not exist in %s namespace.", platformRef.Name, platformRef.Namespace)
			cPlatform.Status.Manager().MarkFalse(api.SucceedConditionType, operatorapi.PlatformNotFoundReason,
				fmt.Sprintf("%s platform does not exist in %s namespace.", platformRef.Name, platformRef.Namespace))
			return nil
		}
		return err
	}

	if platform != nil {
		condition := platform.Status.GetTopLevelCondition()
		if condition.IsTrue() {
			klog.V(log.D).InfoS("Referenced SonataFlowPlatform '%s/%s' is ready", platformRef.Namespace, platformRef.Name)
			cPlatform.Status.Manager().MarkTrueWithReason(api.SucceedConditionType, "",
				"Referenced SonataFlowPlatform '%s/%s' is ready", platformRef.Namespace, platformRef.Name)
		} else if condition.IsFalse() {
			klog.V(log.D).InfoS("Referenced SonataFlowPlatform '%s/%s' not ready", platformRef.Namespace, platformRef.Name)
			cPlatform.Status.Manager().MarkFalse(api.SucceedConditionType, operatorapi.PlatformFailureReason,
				"Referenced SonataFlowPlatform '%s/%s' not ready", platformRef.Namespace, platformRef.Name)
		} else {
			klog.V(log.D).InfoS("Waiting for referenced SonataFlowPlatform '%s/%s' to be ready", platformRef.Namespace, platformRef.Name)
			cPlatform.Status.Manager().MarkUnknown(api.SucceedConditionType, operatorapi.PlatformWarmingReason,
				"Waiting for referenced SonataFlowPlatform '%s/%s' to be ready", platformRef.Namespace, platformRef.Name)
		}
	}

	return nil
}

// Function to double-check if there is already an active cluster platform
func (action *initializeAction) isPrimaryDuplicate(ctx context.Context, cPlatform *operatorapi.SonataFlowClusterPlatform) (bool, error) {
	if IsSecondary(cPlatform) {
		// Always reconcile secondary cluster platforms
		return false, nil
	}
	platforms, err := listPrimaryClusterPlatforms(ctx, action.client)
	if err != nil {
		return false, err
	}
	for _, p := range platforms.Items {
		p := p // pin
		if p.Name != cPlatform.Name && IsActive(&p) {
			return true, nil
		}
	}

	return false, nil
}
