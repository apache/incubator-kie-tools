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

package factory

import (
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/controllers/profiles"
	"github.com/kiegroup/kogito-serverless-operator/controllers/profiles/dev"
	"github.com/kiegroup/kogito-serverless-operator/controllers/profiles/prod"
)

const (
	defaultProfile = metadata.ProdProfile
)

type reconcilerBuilder func(client client.Client) profiles.ProfileReconciler

var profileBuilders = map[metadata.ProfileType]reconcilerBuilder{
	metadata.ProdProfile: prod.NewProfileReconciler,
	metadata.DevProfile:  dev.NewProfileReconciler,
}

func profileBuilder(workflow *operatorapi.SonataFlow) reconcilerBuilder {
	profile := workflow.Annotations[metadata.Profile]
	if len(profile) == 0 {
		return profileBuilders[defaultProfile]
	}
	if _, ok := profileBuilders[metadata.ProfileType(profile)]; !ok {
		return profileBuilders[defaultProfile]
	}
	return profileBuilders[metadata.ProfileType(profile)]
}

// NewReconciler creates a new ProfileReconciler based on the given workflow and context.
func NewReconciler(client client.Client, workflow *operatorapi.SonataFlow) profiles.ProfileReconciler {
	return profileBuilder(workflow)(client)
}
