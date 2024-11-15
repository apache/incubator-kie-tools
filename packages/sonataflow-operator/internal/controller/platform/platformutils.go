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
	"strings"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/util/retry"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
)

var builderDockerfileFromRE = regexp.MustCompile(`FROM (.*) AS builder`)

// ResourceCustomizer can be used to inject code that changes the objects before they are created.
type ResourceCustomizer func(object ctrl.Object) ctrl.Object

// GetCustomizedBuilderDockerfile determines if the default Dockerfile provided by the
// sonataflow-operator-builder-config_v1_configmap.yaml must be customized to use a different builder base image,
// before building a workflow.
// The following ordered criteria are applied:
// 1) if the current platform has a configured platform.Spec.Build.Config.BaseImage, that base image must be used.
// 2) if the current sonataflow-operator-controllers-config.yaml has a configured SonataFlowBaseBuilderImageTag, that
// base image must be used.
// 3) No customization apply.
func GetCustomizedBuilderDockerfile(dockerfile string, platform operatorapi.SonataFlowPlatform) string {
	if len(platform.Spec.Build.Config.BaseImage) > 0 {
		dockerfile = strings.Replace(dockerfile, GetFromImageTagDockerfile(dockerfile), platform.Spec.Build.Config.BaseImage, 1)
	} else if len(cfg.GetCfg().SonataFlowBaseBuilderImageTag) > 0 {
		dockerfile = strings.Replace(dockerfile, GetFromImageTagDockerfile(dockerfile), cfg.GetCfg().SonataFlowBaseBuilderImageTag, 1)
	}
	return dockerfile
}

func GetFromImageTagDockerfile(dockerfile string) string {
	res := builderDockerfileFromRE.FindAllStringSubmatch(dockerfile, 1)
	return strings.Trim(res[0][1], " ")
}

// ReplaceFromImageTagDockerfile replaces the "FROM" clause from the given dockerfile with the given fromReplacement.
// For example: "FROM myimage:latest AS builder"
func ReplaceFromImageTagDockerfile(dockerfile string, fromReplacement string) string {
	return string(builderDockerfileFromRE.ReplaceAll([]byte(dockerfile), []byte(fromReplacement)))
}

func SafeUpdatePlatform(ctx context.Context, target *operatorapi.SonataFlowPlatform) error {
	return retry.RetryOnConflict(retry.DefaultRetry, func() error {
		refreshedInst := &operatorapi.SonataFlowPlatform{}
		if getErr := utils.GetClient().Get(ctx, types.NamespacedName{Namespace: target.Namespace, Name: target.Name}, refreshedInst); getErr != nil {
			return getErr
		}
		refreshedInst.Spec = target.Spec
		if updateErr := utils.GetClient().Update(ctx, refreshedInst); updateErr != nil {
			return updateErr
		}
		return nil
	})
}

func SafeUpdatePlatformStatus(ctx context.Context, target *operatorapi.SonataFlowPlatform) error {
	return retry.RetryOnConflict(retry.DefaultRetry, func() error {
		refreshedInst := &operatorapi.SonataFlowPlatform{}
		if getErr := utils.GetClient().Get(ctx, types.NamespacedName{Namespace: target.Namespace, Name: target.Name}, refreshedInst); getErr != nil {
			return getErr
		}
		refreshedInst.Status = target.Status
		if updateErr := utils.GetClient().Status().Update(ctx, refreshedInst); updateErr != nil {
			return updateErr
		}
		return nil
	})
}
