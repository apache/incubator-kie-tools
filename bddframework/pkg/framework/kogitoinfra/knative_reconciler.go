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

package kogitoinfra

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"k8s.io/apimachinery/pkg/types"
)

// knativeInfraReconciler for Knative resources reconciliation
type knativeInfraReconciler struct {
	infraContext
}

func initknativeInfraReconciler(context infraContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "knative")
	return &knativeInfraReconciler{
		infraContext: context,
	}
}

// Reconcile ...
func (k *knativeInfraReconciler) Reconcile() (requeue bool, resultErr error) {
	knativeHandler := infrastructure.NewKnativeHandler(k.Context)
	if !knativeHandler.IsKnativeEventingAvailable() {
		return false, errorForResourceAPINotFound(k.instance.GetSpec().GetResource().GetAPIVersion())
	}

	if len(k.instance.GetSpec().GetResource().GetName()) > 0 {
		ns := k.instance.GetSpec().GetResource().GetNamespace()
		if len(ns) == 0 {
			k.Log.Debug("Namespace not defined, setting to current namespace")
			ns = k.instance.GetNamespace()
		}

		broker, resultErr := knativeHandler.FetchBroker(types.NamespacedName{Name: k.instance.GetSpec().GetResource().GetName(), Namespace: ns})
		if resultErr != nil {
			return false, resultErr
		} else if broker == nil {
			return false, errorForResourceNotFound(infrastructure.KnativeEventingBrokerKind, k.instance.GetSpec().GetResource().GetName(), ns)
		}
	} else {
		return false,
			fmt.Errorf("No Knative Eventing Broker resource defined in the KogitoInfra CR %s on namespace %s, impossible to continue ", k.instance.GetName(), k.instance.GetNamespace())
	}
	return false, nil
}
