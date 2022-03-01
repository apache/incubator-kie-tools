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
	// DefaultMgmtConsoleName ...
	DefaultMgmtConsoleName = "management-console"
	// DefaultMgmtConsoleImageName ...
	DefaultMgmtConsoleImageName = "kogito-management-console"
)

// mgmtConsoleSupportingServiceResource implementation of SupportingServiceResource
type mgmtConsoleSupportingServiceResource struct {
	supportingServiceContext
}

func initMgmtConsoleSupportingServiceResource(context supportingServiceContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "mgmt-console")
	return &mgmtConsoleSupportingServiceResource{
		supportingServiceContext: context,
	}
}

// Reconcile reconcile Management Console
func (m *mgmtConsoleSupportingServiceResource) Reconcile() (err error) {
	m.Log.Info("Reconciling for KogitoMgmtConsole")
	definition := kogitoservice.ServiceDefinition{
		DefaultImageName:   DefaultMgmtConsoleImageName,
		Request:            controller.Request{NamespacedName: types.NamespacedName{Name: m.instance.GetName(), Namespace: m.instance.GetNamespace()}},
		SingleReplica:      false,
		OnDeploymentCreate: m.mgmtConsoleOnDeploymentCreate,
	}
	return kogitoservice.NewServiceDeployer(m.Context, definition, m.instance, m.infraHandler).Deploy()
}

func (m *mgmtConsoleSupportingServiceResource) mgmtConsoleOnDeploymentCreate(deployment *appsv1.Deployment) error {
	urlHandler := connector.NewURLHandler(m.Context, m.runtimeHandler, m.supportingServiceHandler)
	if err := urlHandler.InjectDataIndexEndpointOnDeployment(deployment); err != nil {
		return err
	}
	return nil
}
