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

package kubernetes

import (
	"context"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
)

func newScheduleAction() Action {
	return &scheduleAction{}
}

type scheduleAction struct {
	baseAction
}

// Name returns a common name of the action.
func (action *scheduleAction) Name() string {
	return "schedule"
}

// CanHandle tells whether this action can handle the build.
func (action *scheduleAction) CanHandle(build *api.ContainerBuild) bool {
	return build.Status.Phase == api.ContainerBuildPhaseScheduling
}

// Handle handles the builds.
func (action *scheduleAction) Handle(ctx context.Context, build *api.ContainerBuild) (*api.ContainerBuild, error) {
	// TODO do any work required between initialization and scheduling, like enqueueing builds
	now := metav1.Now()
	build.Status.StartedAt = &now
	build.Status.Phase = api.ContainerBuildPhasePending

	return build, nil
}
