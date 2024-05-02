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

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
)

func newErrorAction() Action {
	return &errorAction{}
}

type errorAction struct {
	baseAction
}

// Name returns a common name of the action.
func (action *errorAction) Name() string {
	return "error"
}

// CanHandle tells whether this action can handle the build.
func (action *errorAction) CanHandle(build *api.ContainerBuild) bool {
	return build.Status.Phase == api.ContainerBuildPhaseError
}

// Handle handles the builds.
func (action *errorAction) Handle(ctx context.Context, build *api.ContainerBuild) (*api.ContainerBuild, error) {
	return nil, nil
}
