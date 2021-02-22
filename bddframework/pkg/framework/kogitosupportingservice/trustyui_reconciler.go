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
	"github.com/kiegroup/kogito-cloud-operator/core/connector"
	"github.com/kiegroup/kogito-cloud-operator/core/kogitoservice"
	appsv1 "k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"
	controller "sigs.k8s.io/controller-runtime/pkg/reconcile"
	"time"
)

const (
	// DefaultTrustyUIName ...
	DefaultTrustyUIName = "trusty-ui"
	// DefaultTrustyUIImageName ...
	DefaultTrustyUIImageName = "kogito-trusty-ui"
)

// trustyUISupportingServiceResource implementation of SupportingServiceResource
type trustyUISupportingServiceResource struct {
	supportingServiceContext
}

func initTrustyUISupportingServiceResource(context supportingServiceContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "trusty-AI")
	return &trustyUISupportingServiceResource{
		supportingServiceContext: context,
	}
}

// Reconcile reconcile TrustyUI Service
func (t *trustyUISupportingServiceResource) Reconcile() (reconcileAfter time.Duration, err error) {
	t.Log.Info("Reconciling for KogitoTrustyUI")
	definition := kogitoservice.ServiceDefinition{
		DefaultImageName:   DefaultTrustyUIImageName,
		Request:            controller.Request{NamespacedName: types.NamespacedName{Name: t.instance.GetName(), Namespace: t.instance.GetNamespace()}},
		SingleReplica:      false,
		OnDeploymentCreate: t.trustyUIOnDeploymentCreate,
	}
	return kogitoservice.NewServiceDeployer(t.Context, definition, t.instance, t.infraHandler).Deploy()
}

func (t *trustyUISupportingServiceResource) trustyUIOnDeploymentCreate(deployment *appsv1.Deployment) error {
	urlHandler := connector.NewURLHandler(t.Context, t.runtimeHandler, t.supportingServiceHandler)
	if err := urlHandler.InjectTrustyURLIntoDeployment(t.instance.GetNamespace(), deployment); err != nil {
		return err
	}
	return nil
}
