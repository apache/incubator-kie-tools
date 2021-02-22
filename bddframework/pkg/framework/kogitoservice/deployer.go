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

package kogitoservice

import (
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/core/manager"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
	"time"

	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/record"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/runtime"
	controller "sigs.k8s.io/controller-runtime/pkg/reconcile"
)

// ServiceDefinition defines the structure for a Kogito Service
type ServiceDefinition struct {
	// DefaultImageName is the name of the default image distributed for Kogito, e.g. kogito-jobs-service, kogito-data-index and so on
	// can be empty, in this case Request.Name will be used as image name
	DefaultImageName string
	// DefaultImageTag is the default image tag to use for this service. If left empty, will use the minor version of the operator, e.g. 0.11
	DefaultImageTag string
	// Request made for the service
	Request controller.Request
	// OnDeploymentCreate applies custom deployment configuration in the required Deployment resource
	OnDeploymentCreate func(deployment *appsv1.Deployment) error
	// OnObjectsCreate applies custom object creation in the service deployment logic.
	// E.g. if you need an additional Kubernetes resource, just create your own map that the API will append to its managed resources.
	// The "objectLists" array is the List object reference of the types created.
	// For example: if a ConfigMap is created, then ConfigMapList empty reference should be added to this list
	OnObjectsCreate func(kogitoService api.KogitoService) (resources map[reflect.Type][]resource.KubernetesResource, objectLists []runtime.Object, err error)
	// OnGetComparators is called during the deployment phase to compare the deployed resources against the created ones
	// Use this hook to add your comparators to override a specific comparator or to add your own if you have created extra objects via OnObjectsCreate
	// Use framework.NewComparatorBuilder() to build your own
	OnGetComparators func(comparator compare.ResourceComparator)
	// SingleReplica if set to true, avoids that the service has more than one pod replica
	SingleReplica bool
	// KafkaTopics is a collection of Kafka Topics to be created within the service
	KafkaTopics []string
	// HealthCheckProbe is the probe that needs to be configured in the service. Defaults to TCPHealthCheckProbe
	HealthCheckProbe HealthCheckProbeType
	// CustomService indicates that the service can be built within the cluster
	// A custom service means that could be built by a third party, not being provided by the Kogito Team Services catalog (such as Data Index, Management Console and etc.).
	CustomService bool
	// extraManagedObjectLists is a holder for the OnObjectsCreate return function
	extraManagedObjectLists []runtime.Object
}

const (
	defaultReplicas = int32(1)
)

// ServiceDeployer is the API to handle a Kogito Service deployment by Operator SDK controllers
type ServiceDeployer interface {
	// Deploy deploys the Kogito Service in the Kubernetes cluster according to a given ServiceDefinition
	Deploy() (reconcileAfter time.Duration, err error)
}

type serviceDeployer struct {
	*operator.Context
	definition   ServiceDefinition
	instance     api.KogitoService
	recorder     record.EventRecorder
	infraHandler manager.KogitoInfraHandler
}

// NewServiceDeployer creates a new ServiceDeployer to handle a custom Kogito Service instance to be handled by Operator SDK controller.
func NewServiceDeployer(context *operator.Context, definition ServiceDefinition, serviceType api.KogitoService, infraHandler manager.KogitoInfraHandler) ServiceDeployer {
	if len(definition.Request.NamespacedName.Namespace) == 0 && len(definition.Request.NamespacedName.Name) == 0 {
		panic("No Request provided for the Service Deployer")
	}
	return &serviceDeployer{
		Context:      context,
		definition:   definition,
		instance:     serviceType,
		recorder:     newRecorder(context.Scheme, definition.Request.Name),
		infraHandler: infraHandler,
	}
}

func newRecorder(scheme *runtime.Scheme, eventSourceName string) record.EventRecorder {
	return record.NewRecorder(scheme, v1.EventSource{Component: eventSourceName, Host: record.GetHostName()})
}

func (s *serviceDeployer) getNamespace() string { return s.definition.Request.Namespace }

func (s *serviceDeployer) Deploy() (time.Duration, error) {
	if s.instance.GetSpec().GetReplicas() == nil {
		s.instance.GetSpec().SetReplicas(defaultReplicas)
	}
	if len(s.definition.DefaultImageName) == 0 {
		s.definition.DefaultImageName = s.definition.Request.Name
	}

	var err error

	// always updateStatus its status
	statusHandler := NewStatusHandler(s.Context)
	defer statusHandler.HandleStatusUpdate(s.instance, &err)

	// we need to take ownership of the custom configmap provided
	if len(s.instance.GetSpec().GetPropertiesConfigMap()) > 0 {
		reconcileAfter, err := s.takeCustomConfigMapOwnership()
		if err != nil || reconcileAfter > 0 {
			return reconcileAfter, err
		}
	}

	// we need to take ownership of the provided KogitoInfra instances
	if len(s.instance.GetSpec().GetInfra()) > 0 {
		err = s.takeKogitoInfraOwnership()
		if err != nil {
			return s.getReconcileResultFor(err)
		}
	}

	if err = s.checkInfraDependencies(); err != nil {
		return s.getReconcileResultFor(err)
	}

	// create our resources
	requestedResources, err := s.createRequiredResources()
	if err != nil {
		return s.getReconcileResultFor(err)
	}

	// get the deployed ones
	deployedResources, err := s.getDeployedResources()
	if err != nil {
		return s.getReconcileResultFor(err)
	}

	// compare required and deployed, in case of any differences, we should create updateStatus or delete the k8s resources
	comparator := s.getComparator()
	deltas := comparator.Compare(deployedResources, requestedResources)
	for resourceType, delta := range deltas {
		if !delta.HasChanges() {
			continue
		}
		s.Log.Info("Will", "create", len(delta.Added), "update", len(delta.Updated), "delete", len(delta.Removed), "resourceType", resourceType)

		if _, err = kubernetes.ResourceC(s.Client).CreateResources(delta.Added); err != nil {
			return s.getReconcileResultFor(err)
		}
		s.generateEventForDeltaResources("Created", resourceType, delta.Added)

		if _, err = kubernetes.ResourceC(s.Client).UpdateResources(deployedResources[resourceType], delta.Updated); err != nil {
			return s.getReconcileResultFor(err)
		}
		s.generateEventForDeltaResources("Updated", resourceType, delta.Updated)

		if _, err = kubernetes.ResourceC(s.Client).DeleteResources(delta.Removed); err != nil {
			return s.getReconcileResultFor(err)
		}
		s.generateEventForDeltaResources("Removed", resourceType, delta.Removed)
	}

	err = s.configureMonitoring()
	if err != nil {
		return s.getReconcileResultFor(err)
	}

	err = s.configureMessaging()

	return s.getReconcileResultFor(err)
}

func (s *serviceDeployer) generateEventForDeltaResources(eventReason string, resourceType reflect.Type, addedResources []resource.KubernetesResource) {
	for _, newResource := range addedResources {
		s.recorder.Eventf(s.Client, s.instance, v1.EventTypeNormal, eventReason, "%s %s: %s", eventReason, resourceType.Name(), newResource.GetName())
	}
}

func (s *serviceDeployer) takeCustomConfigMapOwnership() (requeueAfter time.Duration, err error) {
	configMapHandler := infrastructure.NewConfigMapHandler(s.Context, s.recorder)
	if updated, err := configMapHandler.TakeConfigMapOwnership(types.NamespacedName{Name: s.instance.GetSpec().GetPropertiesConfigMap(), Namespace: s.getNamespace()}, s.instance); err != nil {
		return 0, err
	} else if !updated {
		return 0, nil
	}
	return time.Second * 15, nil
}

func (s *serviceDeployer) takeKogitoInfraOwnership() error {
	infraManager := manager.NewKogitoInfraManager(s.Context, s.infraHandler)
	for _, infraName := range s.instance.GetSpec().GetInfra() {
		if err := infraManager.TakeKogitoInfraOwnership(types.NamespacedName{Name: infraName, Namespace: s.getNamespace()}, s.instance); err != nil {
			return err
		}
	}
	return nil
}

// checkInfraDependencies verifies if every KogitoInfra resource have an ok status.
func (s *serviceDeployer) checkInfraDependencies() error {
	kogitoInfraReferences := s.instance.GetSpec().GetInfra()
	s.Log.Debug("Going to fetch kogito infra properties", "infra name", kogitoInfraReferences)
	infraManager := manager.NewKogitoInfraManager(s.Context, s.infraHandler)
	for _, infraName := range kogitoInfraReferences {
		if isReady, err := infraManager.IsKogitoInfraReady(types.NamespacedName{Name: infraName, Namespace: s.getNamespace()}); err != nil {
			return err
		} else if !isReady {
			conditionReason, err := infraManager.GetKogitoInfraConditionReason(types.NamespacedName{Name: infraName, Namespace: s.getNamespace()})
			if err != nil {
				return err
			}
			return errorForInfraNotReady(s.instance, infraName, conditionReason)
		}
	}
	return nil
}

func (s *serviceDeployer) configureMessaging() error {
	kafkaMessagingDeployer := NewKafkaMessagingDeployer(s.Context, s.definition, s.infraHandler)
	if err := kafkaMessagingDeployer.CreateRequiredResources(s.instance); err != nil {
		return errorForMessaging(err)
	}

	knativeMessagingDeployer := NewKnativeMessagingDeployer(s.Context, s.definition, s.infraHandler)
	if err := knativeMessagingDeployer.CreateRequiredResources(s.instance); err != nil {
		return errorForMessaging(err)
	}
	return nil
}

func (s *serviceDeployer) configureMonitoring() error {
	prometheusManager := NewPrometheusManager(s.Context)
	if err := prometheusManager.ConfigurePrometheus(s.instance); err != nil {
		s.Log.Error(err, "Could not deploy prometheus monitoring")
		return errorForMonitoring(err)
	}

	grafanaDashboardManager := NewGrafanaDashboardManager(s.Context)
	if err := grafanaDashboardManager.ConfigureGrafanaDashboards(s.instance); err != nil {
		s.Log.Error(err, "Could not deploy grafana dashboards")
		return errorForDashboards(err)
	}

	return nil
}

func (s *serviceDeployer) getReconcileResultFor(err error) (time.Duration, error) {
	// reconciliation always happens if we return an error
	if reasonForError(err) == api.ServiceReconciliationFailure {
		return 0, err
	}
	reconcileAfter := reconciliationIntervalForError(err)
	return reconcileAfter, nil
}
