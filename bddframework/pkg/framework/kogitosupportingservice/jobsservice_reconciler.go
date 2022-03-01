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
	// DefaultJobsServiceImageName is the default image name for the Jobs Service image
	DefaultJobsServiceImageName = "kogito-jobs-service-ephemeral"
	// JobsServiceInfinispanImageName is the image name for the Jobs Service Service with Infinispan
	JobsServiceInfinispanImageName = "kogito-jobs-service-infinispan"
	// JobsServiceMongoDBImageName is the image name for the Jobs Service Service with MongoDB
	JobsServiceMongoDBImageName = "kogito-jobs-service-mongodb"
	// JobsServicePostgresqlImageName is the image name for the Jobs Service Service with PostgreSQL
	JobsServicePostgresqlImageName = "kogito-jobs-service-postgresql"
	// DefaultJobsServiceName is the default name for the Jobs Services instance service
	DefaultJobsServiceName = "jobs-service"
)

// jobsServiceSupportingServiceResource implementation of SupportingServiceResource
type jobsServiceSupportingServiceResource struct {
	supportingServiceContext
}

func initJobsServiceSupportingServiceResource(context supportingServiceContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "jobsService")
	return &jobsServiceSupportingServiceResource{
		supportingServiceContext: context,
	}
}

// Reconcile reconcile Jobs service
func (j *jobsServiceSupportingServiceResource) Reconcile() (err error) {
	j.Log.Info("Reconciling for KogitoJobsService")

	definition := kogitoservice.ServiceDefinition{
		DefaultImageName: DefaultJobsServiceImageName,
		Request:          controller.Request{NamespacedName: types.NamespacedName{Name: j.instance.GetName(), Namespace: j.instance.GetNamespace()}},
		SingleReplica:    true,
		KafkaTopics:      jobsServicekafkaTopics,
	}
	if err = kogitoservice.NewServiceDeployer(j.Context, definition, j.instance, j.infraHandler).Deploy(); err != nil {
		return
	}

	endpointConfigMapReconciler := newEndPointConfigMapReconciler(j.Context, j.instance, connector.JobsServicesHTTPRouteEnv, "")
	if err = endpointConfigMapReconciler.Reconcile(); err != nil {
		return
	}

	urlHandler := connector.NewURLHandler(j.Context, j.runtimeHandler, j.supportingServiceHandler)
	if err = urlHandler.InjectJobsServicesEndPointOnKogitoRuntimeServices(types.NamespacedName{Name: j.instance.GetName(), Namespace: j.instance.GetNamespace()}); err != nil {
		return
	}

	return
}

// Collection of kafka topics that should be handled by the Jobs service
var jobsServicekafkaTopics = []string{
	"kogito-job-service-job-status-events",
}
