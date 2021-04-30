// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package kogitosupportingservice

import (
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	"time"
)

// Reconciler Interface to represent type of kogito supporting service resources like JobsService & MgmtConcole
type Reconciler interface {
	Reconcile() (reconcileAfter time.Duration, resultErr error)
}

type supportingServiceContext struct {
	operator.Context
	instance                 api.KogitoSupportingServiceInterface
	infraHandler             manager.KogitoInfraHandler
	supportingServiceHandler manager.KogitoSupportingServiceHandler
	runtimeHandler           manager.KogitoRuntimeHandler
}

// ReconcilerHandler ...
type ReconcilerHandler interface {
	GetSupportingServiceReconciler(instance api.KogitoSupportingServiceInterface) Reconciler
}

type reconcilerHandler struct {
	operator.Context
	infraHandler             manager.KogitoInfraHandler
	supportingServiceHandler manager.KogitoSupportingServiceHandler
	runtimeHandler           manager.KogitoRuntimeHandler
}

// NewReconcilerHandler ...
func NewReconcilerHandler(context operator.Context, infraHandler manager.KogitoInfraHandler, supportingServiceHandler manager.KogitoSupportingServiceHandler, runtimeHandler manager.KogitoRuntimeHandler) ReconcilerHandler {
	return &reconcilerHandler{
		Context:                  context,
		infraHandler:             infraHandler,
		supportingServiceHandler: supportingServiceHandler,
		runtimeHandler:           runtimeHandler,
	}
}

// getKogitoInfraReconciler identify and return request kogito infra reconciliation logic on bases of information provided in kogitoInfra value
func (k *reconcilerHandler) GetSupportingServiceReconciler(instance api.KogitoSupportingServiceInterface) Reconciler {
	k.Log.Debug("going to fetch related kogito supporting service resource")
	context := supportingServiceContext{
		Context:                  k.Context,
		instance:                 instance,
		infraHandler:             k.infraHandler,
		supportingServiceHandler: k.supportingServiceHandler,
		runtimeHandler:           k.runtimeHandler,
	}
	return getSupportedResources(context)[instance.GetSupportingServiceSpec().GetServiceType()]
}

func getSupportedResources(context supportingServiceContext) map[api.ServiceType]Reconciler {
	return map[api.ServiceType]Reconciler{
		api.DataIndex:      initDataIndexSupportingServiceResource(context),
		api.Explainability: initExplainabilitySupportingServiceResource(context),
		api.JobsService:    initJobsServiceSupportingServiceResource(context),
		api.MgmtConsole:    initMgmtConsoleSupportingServiceResource(context),
		api.TaskConsole:    initTaskConsoleSupportingServiceResource(context),
		api.TrustyAI:       initTrustyAISupportingServiceResource(context),
		api.TrustyUI:       initTrustyUISupportingServiceResource(context),
	}
}
