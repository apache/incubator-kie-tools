// Copyright 2022 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package install

import (
	"context"

	"github.com/kiegroup/container-builder/client"

	v08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/utils/resources"
)

func installBuilderServiceAccountRolesOpenShift(ctx context.Context, c client.Client, namespace string) error {
	return resources.ResourcesOrCollect(ctx, c, namespace, nil, true, resources.IdentityResourceCustomizer,
		"/resources/builder/builder-service-account.yaml",
		"/resources/builder/builder-role.yaml",
		"/resources/builder/builder-role-binding.yaml",
		"/resources/builder/builder-role-openshift.yaml",
		"/resources/builder/builder-role-binding-openshift.yaml",
	)
}

func installBuilderServiceAccountRolesKubernetes(ctx context.Context, c client.Client, namespace string) error {
	return resources.ResourcesOrCollect(ctx, c, namespace, nil, true, resources.IdentityResourceCustomizer,
		"/resources/builder/builder-service-account.yaml",
		"/resources/builder/builder-role.yaml",
		"/resources/builder/builder-role-binding.yaml",
	)
}

// BuilderServiceAccountRoles installs the builder service account and related roles in the given namespace.
func BuilderServiceAccountRoles(ctx context.Context, c client.Client, namespace string, cluster v08.PlatformCluster) error {
	if cluster == v08.PlatformClusterOpenShift {
		if err := installBuilderServiceAccountRolesOpenShift(ctx, c, namespace); err != nil {
			return err
		}
	} else {
		if err := installBuilderServiceAccountRolesKubernetes(ctx, c, namespace); err != nil {
			return err
		}
	}
	return nil
}
