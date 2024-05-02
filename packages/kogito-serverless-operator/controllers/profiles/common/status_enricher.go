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

package common

import (
	"context"

	"k8s.io/klog/v2"

	"k8s.io/client-go/rest"

	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
)

// NewStatusEnricher ...
func NewStatusEnricher(client client.Client, enricher StatusEnricherFn) *StatusEnricher {
	return &StatusEnricher{
		C:        client,
		Enricher: enricher,
	}
}

// StatusEnricherFn is the func that creates the initial reference object, if the object doesn't exist in the cluster, this one is created.
// Can be used as a reference to keep the object immutable
type StatusEnricherFn func(ctx context.Context, client client.Client, workflow *operatorapi.SonataFlow) (client.Object, error)

// StatusEnricher ...
type StatusEnricher struct {
	C        client.Client
	Config   *rest.Config
	Enricher StatusEnricherFn
}

func (d *StatusEnricher) Enrich(ctx context.Context, workflow *operatorapi.SonataFlow) (controllerutil.OperationResult, error) {
	result := controllerutil.OperationResultNone
	_, err := d.Enricher(ctx, d.C, workflow)
	if err != nil {
		return result, err
	}

	klog.V(log.I).InfoS("Enrichment operation finalized", "result", result, "workflow", workflow.GetName(), "namespace", workflow.GetNamespace())
	return result, nil
}
