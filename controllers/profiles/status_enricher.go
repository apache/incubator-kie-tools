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

package profiles

import (
	"context"

	"k8s.io/client-go/rest"

	"github.com/go-logr/logr"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

// newStatusEnricher see defaultObjectEnsurer
func newStatusEnricher(client client.Client, logger *logr.Logger, enricher statusEnricherFn) *statusEnricher {
	return &statusEnricher{
		client:   client,
		logger:   logger,
		enricher: enricher,
	}
}

// statusEnricherFn is the func that creates the initial reference object, if the object doesn't exist in the cluster, this one is created.
// Can be used as a reference to keep the object immutable
type statusEnricherFn func(ctx context.Context, client client.Client, workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error)

// statusEnricher provides the engine for a ReconciliationState that needs to create or update a given Kubernetes object during the reconciliation cycle.
type statusEnricher struct {
	client   client.Client
	config   *rest.Config
	logger   *logr.Logger
	enricher statusEnricherFn
}

func (d *statusEnricher) Enrich(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (controllerutil.OperationResult, error) {
	result := controllerutil.OperationResultNone
	_, err := d.enricher(ctx, d.client, workflow)
	if err != nil {
		return result, err
	}

	d.logger.Info("Enrichment operation finalized", "result", result, "workflow", workflow.GetName(), "namespace", workflow.GetNamespace())
	return result, nil
}
