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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/connector"
	"github.com/kiegroup/kogito-operator/core/kogitoservice"
	"github.com/kiegroup/kogito-operator/core/shared"
	"k8s.io/apimachinery/pkg/types"
	controller1 "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

const (
	// DataIndexInfinispanImageName is the image name for the Data Index Service with Infinispan
	DataIndexInfinispanImageName = "kogito-data-index-infinispan"
	// DataIndexMongoDBImageName is the image name for the Data Index Service with MongoDB
	DataIndexMongoDBImageName = "kogito-data-index-mongodb"
	// DataIndexPostgresqlImageName is the image name for the Data Index Service with PostgreSQL
	DataIndexPostgresqlImageName = "kogito-data-index-postgresql"
	// DefaultDataIndexImageName is just the image name for the Data Index Service
	DefaultDataIndexImageName = DataIndexInfinispanImageName
	// DefaultDataIndexName is the default name for the Data Index instance service
	DefaultDataIndexName = "data-index"
)

// dataIndexSupportingServiceResource implementation of SupportingServiceResource
type dataIndexSupportingServiceResource struct {
	supportingServiceContext
}

func initDataIndexSupportingServiceResource(context supportingServiceContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "dataIndex")
	return &dataIndexSupportingServiceResource{
		supportingServiceContext: context,
	}
}

// Reconcile reconcile Data Index
func (d *dataIndexSupportingServiceResource) Reconcile() (err error) {
	d.Log.Info("Reconciling for KogitoDataIndex")
	protoBufHandler := shared.NewProtoBufHandler(d.Context, d.supportingServiceHandler)
	definition := kogitoservice.ServiceDefinition{
		DefaultImageName:   DefaultDataIndexImageName,
		Request:            controller1.Request{NamespacedName: types.NamespacedName{Name: d.instance.GetName(), Namespace: d.instance.GetNamespace()}},
		OnDeploymentCreate: protoBufHandler.MountAllProtoBufConfigMapOnDataIndexDeployment,
	}
	if err = kogitoservice.NewServiceDeployer(d.Context, definition, d.instance, d.infraHandler).Deploy(); err != nil {
		return
	}
	endpointConfigMapReconciler := newEndPointConfigMapReconciler(d.Context, d.instance, connector.DataIndexHTTPRouteEnv, connector.DataIndexWSRouteEnv)
	if err = endpointConfigMapReconciler.Reconcile(); err != nil {
		return
	}
	urlHandler := connector.NewURLHandler(d.Context, d.runtimeHandler, d.supportingServiceHandler)
	if err = urlHandler.InjectDataIndexEndPointOnKogitoRuntimeServices(types.NamespacedName{Name: d.instance.GetName(), Namespace: d.instance.GetNamespace()}); err != nil {
		return
	}
	if err = urlHandler.InjectDataIndexURLIntoSupportingService(types.NamespacedName{Name: d.instance.GetName(), Namespace: d.instance.GetNamespace()}, api.MgmtConsole); err != nil {
		return
	}
	return
}
