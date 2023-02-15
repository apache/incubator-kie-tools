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

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

// NewMonitorAction returns an action that monitors the build platform after it's fully initialized.
func NewMonitorAction() Action {
	return &monitorAction{}
}

type monitorAction struct {
	baseAction
}

func (action *monitorAction) Name() string {
	return "monitor"
}

func (action *monitorAction) CanHandle(platform *operatorapi.KogitoServerlessPlatform) bool {
	return platform.Status.Phase == operatorapi.PlatformPhaseReady
}

func (action *monitorAction) Handle(ctx context.Context, platform *operatorapi.KogitoServerlessPlatform) (*operatorapi.KogitoServerlessPlatform, error) {
	// Just track the version of the operator in the platform resource
	if platform.Status.Version != metadata.SpecVersion {
		platform.Status.Version = metadata.SpecVersion
		action.Logger.Info("Platform version updated", "version", platform.Status.Version)
	}

	// Refresh applied configuration
	if err := ConfigureDefaults(ctx, action.client, platform, false); err != nil {
		return nil, err
	}

	return platform, nil
}
