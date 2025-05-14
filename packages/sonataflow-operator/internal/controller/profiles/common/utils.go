// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package common

import (
	"context"
	"fmt"

	"k8s.io/client-go/rest"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
)

// CleanupOutdatedRevisions helper function to remove the outdated revisions for a workflow deployed with the knative
// deploymentMode.
// TODO: Refactor when we can remove dependency between properties.go -> knative.go, and k8s.go -> knative.go.
func CleanupOutdatedRevisions(ctx context.Context, cfg *rest.Config, workflow *operatorapi.SonataFlow) error {
	sfp, err := platform.GetActivePlatform(ctx, utils.GetClient(), workflow.Namespace, false)
	if err != nil {
		return fmt.Errorf("failed to get active platform to clean workflow outdated revisions, workflow: %s, namespace: %s : %v", workflow.Name, workflow.Namespace, err)
	}
	if err := knative.CleanupOutdatedRevisions(ctx, cfg, workflow, sfp); err != nil {
		return fmt.Errorf("failed to cleanup workflow outdated revisions, workflow: %s, namespace: %s - %v", workflow.Name, workflow.Namespace, err)
	}
	return nil
}
