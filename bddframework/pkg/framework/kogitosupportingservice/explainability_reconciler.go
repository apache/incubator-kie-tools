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
	"github.com/kiegroup/kogito-operator/core/kogitoservice"
	"k8s.io/apimachinery/pkg/types"
	controller "sigs.k8s.io/controller-runtime/pkg/reconcile"
	"time"
)

const (
	// DefaultExplainabilityImageName is just the image name for the Explainability Service
	DefaultExplainabilityImageName = "kogito-explainability"
	// DefaultExplainabilityName is the default name for the Explainability instance service
	DefaultExplainabilityName = "explainability"
)

// explainabilitySupportingServiceResource implementation of SupportingServiceResource
type explainabilitySupportingServiceResource struct {
	supportingServiceContext
}

func initExplainabilitySupportingServiceResource(context supportingServiceContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "explainability")
	return &explainabilitySupportingServiceResource{
		supportingServiceContext: context,
	}
}

// Reconcile reconcile Explainability Service
func (e *explainabilitySupportingServiceResource) Reconcile() (reconcileAfter time.Duration, err error) {
	e.Log.Info("Reconciling KogitoExplainability")
	definition := kogitoservice.ServiceDefinition{
		DefaultImageName: DefaultExplainabilityImageName,
		Request:          controller.Request{NamespacedName: types.NamespacedName{Name: e.instance.GetName(), Namespace: e.instance.GetNamespace()}},
		KafkaTopics:      explainabilitykafkaTopics,
	}
	return kogitoservice.NewServiceDeployer(e.Context, definition, e.instance, e.infraHandler).Deploy()
}

// Collection of kafka topics that should be handled by the Explainability service
var explainabilitykafkaTopics = []string{
	"trusty-explainability-request",
	"trusty-explainability-result",
}
