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
	appsv1 "k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"
	controller "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const (
	// DefaultTaskConsoleName ...
	DefaultTaskConsoleName = "task-console"
	// DefaultTaskConsoleImageName ...
	DefaultTaskConsoleImageName = "kogito-task-console"
)

// taskConsoleSupportingServiceResource implementation of SupportingServiceResource
type taskConsoleSupportingServiceResource struct {
	supportingServiceContext
}

func initTaskConsoleSupportingServiceResource(context supportingServiceContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "task-console")
	return &taskConsoleSupportingServiceResource{
		supportingServiceContext: context,
	}
}

// Reconcile reconcile Task Console
func (t *taskConsoleSupportingServiceResource) Reconcile() (err error) {
	t.Log.Info("Reconciling for KogitoTaskConsole")
	definition := kogitoservice.ServiceDefinition{
		DefaultImageName:   DefaultTaskConsoleImageName,
		Request:            controller.Request{NamespacedName: types.NamespacedName{Name: t.instance.GetName(), Namespace: t.instance.GetNamespace()}},
		SingleReplica:      false,
		OnDeploymentCreate: t.taskConsoleOnDeploymentCreate,
	}
	return kogitoservice.NewServiceDeployer(t.Context, definition, t.instance, t.infraHandler).Deploy()
}

func (t *taskConsoleSupportingServiceResource) taskConsoleOnDeploymentCreate(deployment *appsv1.Deployment) error {
	urlHandler := connector.NewURLHandler(t.Context, t.runtimeHandler, t.supportingServiceHandler)
	if err := urlHandler.InjectDataIndexEndpointOnDeployment(deployment); err != nil {
		return err
	}
	return nil
}
