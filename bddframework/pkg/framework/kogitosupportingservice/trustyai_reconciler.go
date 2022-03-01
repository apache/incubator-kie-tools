// Copyright 2020 Red Hat, Inc. and/or its affiliates
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
	"github.com/kiegroup/kogito-operator/core/connector"
	"github.com/kiegroup/kogito-operator/core/kogitoservice"
	"k8s.io/apimachinery/pkg/types"
	controller "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const (
	// DefaultTrustyImageName is just the image name for the Trusty Service
	DefaultTrustyImageName = "kogito-trusty-infinispan"
	// DefaultTrustyName is the default name for the Trusty instance service
	DefaultTrustyName = "trusty"
)

// trustyAISupportingServiceResource implementation of SupportingServiceResource
type trustyAISupportingServiceResource struct {
	supportingServiceContext
}

func initTrustyAISupportingServiceResource(context supportingServiceContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "trusty-AI")
	return &trustyAISupportingServiceResource{
		supportingServiceContext: context,
	}
}

// Reconcile reconcile TrustyAI Service
func (t *trustyAISupportingServiceResource) Reconcile() (err error) {
	t.Log.Info("Reconciling for KogitoTrusty")

	definition := kogitoservice.ServiceDefinition{
		DefaultImageName: DefaultTrustyImageName,
		Request:          controller.Request{NamespacedName: types.NamespacedName{Name: t.instance.GetName(), Namespace: t.instance.GetNamespace()}},
		KafkaTopics:      trustyAiKafkaTopics,
	}
	if err = kogitoservice.NewServiceDeployer(t.Context, definition, t.instance, t.infraHandler).Deploy(); err != nil {
		return
	}

	endpointConfigMapReconciler := newEndPointConfigMapReconciler(t.Context, t.instance, connector.TrustyHTTPRouteEnv, connector.TrustyWSRouteEnv)
	if err = endpointConfigMapReconciler.Reconcile(); err != nil {
		return
	}

	urlHandler := connector.NewURLHandler(t.Context, t.runtimeHandler, t.supportingServiceHandler)
	if err = urlHandler.InjectTrustyEndpointOnKogitoRuntimeServices(types.NamespacedName{Name: t.instance.GetName(), Namespace: t.instance.GetNamespace()}); err != nil {
		return
	}
	return
}

// Collection of kafka topics that should be handled by the Trusty service
var trustyAiKafkaTopics = []string{
	"kogito-tracing-decision",
	"kogito-tracing-model",
	"trusty-explainability-result",
	"trusty-explainability-request",
}
