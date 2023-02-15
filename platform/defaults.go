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

	"github.com/pkg/errors"

	"github.com/kiegroup/container-builder/api"
	"github.com/kiegroup/container-builder/client"
	"github.com/kiegroup/container-builder/util/log"

	v08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

func ConfigureDefaults(ctx context.Context, c client.Client, p *v08.KogitoServerlessPlatform, verbose bool) error {
	// Reset the state to initial values
	p.ResyncStatusFullConfig()

	// update missing fields in the resource
	if p.Status.Cluster == "" {
		// determine the kind of cluster the platform is installed into
		isOpenShift, err := IsOpenShift(c)
		switch {
		case err != nil:
			return err
		case isOpenShift:
			p.Status.Cluster = v08.PlatformClusterOpenShift
		default:
			p.Status.Cluster = v08.PlatformClusterKubernetes
		}
	}

	if p.Status.BuildPlatform.BuildStrategy == "" {
		// The build output has to be shared via a volume
		p.Status.BuildPlatform.BuildStrategy = api.BuildStrategyPod
	}

	err := SetPlatformDefaults(p, verbose)
	if err != nil {
		return err
	}

	if p.Status.BuildPlatform.BuildStrategy == api.BuildStrategyPod && p.Status.Phase != v08.PlatformPhaseReady {
		if err := CreateBuilderServiceAccount(ctx, c, p); err != nil {
			return errors.Wrap(err, "cannot ensure service account is present")
		}
	}

	err = ConfigureRegistry(ctx, c, p, verbose)
	if err != nil {
		return err
	}

	if verbose && p.Status.BuildPlatform.Timeout.Duration != 0 {
		log.Log.Infof("Maven Timeout set to %s", p.Status.BuildPlatform.Timeout.Duration)
	}

	return nil
}
