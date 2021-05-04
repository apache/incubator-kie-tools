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
	"time"
)

const (
	// DefaultJobsServiceImageName is the default image name for the Jobs Service image
	DefaultJobsServiceImageName = "kogito-jobs-service"
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
func (j *jobsServiceSupportingServiceResource) Reconcile() (reconcileAfter time.Duration, err error) {
	j.Log.Info("Reconciling for KogitoJobsService")

	// clean up variables if needed
	urlHandler := connector.NewURLHandler(j.Context, j.runtimeHandler, j.supportingServiceHandler)
	if err = urlHandler.InjectJobsServicesURLIntoKogitoRuntimeServices(j.instance.GetNamespace()); err != nil {
		return
	}
	definition := kogitoservice.ServiceDefinition{
		DefaultImageName: DefaultJobsServiceImageName,
		Request:          controller.Request{NamespacedName: types.NamespacedName{Name: j.instance.GetName(), Namespace: j.instance.GetNamespace()}},
		SingleReplica:    true,
		KafkaTopics:      jobsServicekafkaTopics,
	}
	return kogitoservice.NewServiceDeployer(j.Context, definition, j.instance, j.infraHandler).Deploy()
}

// Collection of kafka topics that should be handled by the Jobs service
var jobsServicekafkaTopics = []string{
	"kogito-job-service-job-status-events",
}
