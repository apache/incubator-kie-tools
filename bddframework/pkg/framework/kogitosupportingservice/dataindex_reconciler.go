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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/connector"
	"github.com/kiegroup/kogito-operator/core/kogitoservice"
	appsv1 "k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"
	controller1 "sigs.k8s.io/controller-runtime/pkg/reconcile"
	"time"
)

const (
	// DataIndexInfinispanImageName is the image name for the Data Index Service with Infinispan
	DataIndexInfinispanImageName = "kogito-data-index-infinispan"
	// DataIndexMongoDBImageName is the image name for the Data Index Service with MongoDB
	DataIndexMongoDBImageName = "kogito-data-index-mongodb"
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
func (d *dataIndexSupportingServiceResource) Reconcile() (reconcileAfter time.Duration, err error) {
	d.Log.Info("Reconciling for KogitoDataIndex")

	urlHandler := connector.NewURLHandler(d.Context, d.runtimeHandler, d.supportingServiceHandler)
	if err = urlHandler.InjectDataIndexURLIntoKogitoRuntimeServices(d.instance.GetNamespace()); err != nil {
		return
	}
	if err = urlHandler.InjectDataIndexURLIntoSupportingService(d.instance.GetNamespace(), api.MgmtConsole); err != nil {
		return
	}
	definition := kogitoservice.ServiceDefinition{
		DefaultImageName:   DefaultDataIndexImageName,
		OnDeploymentCreate: d.dataIndexOnDeploymentCreate,
		KafkaTopics:        dataIndexKafkaTopics,
		Request:            controller1.Request{NamespacedName: types.NamespacedName{Name: d.instance.GetName(), Namespace: d.instance.GetNamespace()}},
	}
	return kogitoservice.NewServiceDeployer(d.Context, definition, d.instance, d.infraHandler).Deploy()
}

// Collection of kafka topics that should be handled by the Data-Index service
var dataIndexKafkaTopics = []string{
	"kogito-processinstances-events",
	"kogito-usertaskinstances-events",
	"kogito-processdomain-events",
	"kogito-usertaskdomain-events",
	"kogito-jobs-events",
	"kogito-variables-events",
}

func (d *dataIndexSupportingServiceResource) dataIndexOnDeploymentCreate(deployment *appsv1.Deployment) error {
	if len(deployment.Spec.Template.Spec.Containers) > 0 {
		protoBufHandler := connector.NewProtoBufHandler(d.Context, d.supportingServiceHandler)
		if err := protoBufHandler.MountProtoBufConfigMapsOnDeployment(deployment); err != nil {
			return err
		}
	} else {
		d.Log.Warn("No container definition found for", "Service", d.instance.GetName())
		d.Log.Warn("Skipping applying custom Data Index deployment configuration")
	}
	return nil
}
