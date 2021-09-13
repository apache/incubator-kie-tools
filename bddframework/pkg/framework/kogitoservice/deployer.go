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
	"fmt"
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/record"
	"github.com/kiegroup/kogito-operator/internal"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"reflect"
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
	// CustomService indicates that the service can be built within the cluster
	// A custom service means that could be built by a third party, not being provided by the Kogito Team Services catalog (such as Data Index, Management Console and etc.).
	CustomService bool
	// extraManagedObjectLists is a holder for the OnObjectsCreate return function
	extraManagedObjectLists []runtime.Object

	ConfigMapEnvFromReferences []string
	ConfigMapVolumeReferences  []api.VolumeReferenceInterface
	SecretEnvFromReferences    []string
	SecretVolumeReferences     []api.VolumeReferenceInterface
	Envs                       []v1.EnvVar
}

const (
	defaultReplicas = int32(1)
)

// ServiceDeployer is the API to handle a Kogito Service deployment by Operator SDK controllers
type ServiceDeployer interface {
	// Deploy deploys the Kogito Service in the Kubernetes cluster according to a given ServiceDefinition
	Deploy() error
}

type serviceDeployer struct {
	operator.Context
	definition   ServiceDefinition
	instance     api.KogitoService
	recorder     record.EventRecorder
	infraHandler manager.KogitoInfraHandler
	errorHandler infrastructure.ReconciliationErrorHandler
}

// NewServiceDeployer creates a new ServiceDeployer to handle a custom Kogito Service instance to be handled by Operator SDK controller.
func NewServiceDeployer(context operator.Context, definition ServiceDefinition, serviceType api.KogitoService, infraHandler manager.KogitoInfraHandler) ServiceDeployer {
	if len(definition.Request.NamespacedName.Namespace) == 0 && len(definition.Request.NamespacedName.Name) == 0 {
		panic("No Request provided for the Service Deployer")
	}
	if infraHandler == nil {
		context.Log.Debug("InfraHandler not defined. KogitoInfra features will be disabled.")
		infraHandler = internal.NewNoOpKogitoInfraHandler(context)
	}
	return &serviceDeployer{
		Context:      context,
		definition:   definition,
		instance:     serviceType,
		recorder:     newRecorder(context.Scheme, definition.Request.Name),
		infraHandler: infraHandler,
		errorHandler: infrastructure.NewReconciliationErrorHandler(context),
	}
}

func newRecorder(scheme *runtime.Scheme, eventSourceName string) record.EventRecorder {
	return record.NewRecorder(scheme, v1.EventSource{Component: eventSourceName, Host: record.GetHostName()})
}

func (s *serviceDeployer) Deploy() error {
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

	s.definition.Envs = s.instance.GetSpec().GetEnvs()

	infraPropertiesReconciler := newConfigReconciler(s.Context, s.instance, &s.definition)
	if err = infraPropertiesReconciler.Reconcile(); err != nil {
		return err
	}

	configMapReferenceReconciler := newPropertiesConfigMapReconciler(s.Context, s.instance, &s.definition)
	if err = configMapReferenceReconciler.Reconcile(); err != nil {
		return err
	}

	trustStoreReconciler := newTrustStoreReconciler(s.Context, s.instance, &s.definition)
	if err = trustStoreReconciler.Reconcile(); err != nil {
		return err
	}

	kogitoInfraReconciler := newKogitoInfraReconciler(s.Context, s.instance, &s.definition, s.infraHandler)
	if err = kogitoInfraReconciler.Reconcile(); err != nil {
		return err
	}

	imageHandler := s.newImageHandler()
	if err := imageHandler.ReconcileImageStream(s.instance); err != nil {
		return err
	}

	imageName, err := imageHandler.ResolveImage()
	// we only create the rest of the resources once we have a resolvable image
	if err != nil {
		return err
	} else if len(imageName) == 0 {
		return fmt.Errorf("image not found")
	}

	// create our resources
	requestedResources, err := s.createRequiredResources(imageName)
	if err != nil {
		return err
	}

	// get the deployed ones
	deployedResources, err := s.getDeployedResources()
	if err != nil {
		return err
	}

	// compare required and deployed, in case of any differences, we should create updateStatus or delete the k8s resources
	comparator := s.getComparator()
	deltas := comparator.Compare(deployedResources, requestedResources)
	for resourceType, delta := range deltas {
		if !delta.HasChanges() {
			s.Log.Info("No delta found", "resourceType", resourceType)
			continue
		}
		s.Log.Info("Will", "create", len(delta.Added), "update", len(delta.Updated), "delete", len(delta.Removed), "resourceType", resourceType)

		if _, err = kubernetes.ResourceC(s.Client).CreateResources(delta.Added); err != nil {
			return err
		}
		s.generateEventForDeltaResources("Created", resourceType, delta.Added)

		if _, err = kubernetes.ResourceC(s.Client).UpdateResources(deployedResources[resourceType], delta.Updated); err != nil {
			return err
		}

		if _, err = kubernetes.ResourceC(s.Client).DeleteResources(delta.Removed); err != nil {
			return err
		}
		s.generateEventForDeltaResources("Removed", resourceType, delta.Removed)
	}

	err = s.configureMonitoring()
	if err != nil {
		return err
	}

	err = s.configureMessaging()

	return err
}

func (s *serviceDeployer) generateEventForDeltaResources(eventReason string, resourceType reflect.Type, addedResources []resource.KubernetesResource) {
	for _, newResource := range addedResources {
		s.recorder.Eventf(s.Client, s.instance, v1.EventTypeNormal, eventReason, "%s %s: %s", eventReason, resourceType.Name(), newResource.GetName())
	}
}

func (s *serviceDeployer) configureMessaging() error {
	s.Log.Debug("Going to configuring messaging")
	kafkaMessagingDeployer := NewKafkaMessagingDeployer(s.Context, s.definition, s.infraHandler)
	if err := kafkaMessagingDeployer.CreateRequiredResources(s.instance); err != nil {
		return infrastructure.ErrorForMessaging(err)
	}

	knativeMessagingDeployer := NewKnativeMessagingDeployer(s.Context, s.definition, s.infraHandler)
	if err := knativeMessagingDeployer.CreateRequiredResources(s.instance); err != nil {
		return infrastructure.ErrorForMessaging(err)
	}
	return nil
}

func (s *serviceDeployer) configureMonitoring() error {
	s.Log.Debug("Going to configuring monitoring")
	prometheusManager := NewPrometheusManager(s.Context)
	if err := prometheusManager.ConfigurePrometheus(s.instance); err != nil {
		s.Log.Error(err, "Could not deploy prometheus monitoring")
		return infrastructure.ErrorForMonitoring(err)
	}

	grafanaDashboardManager := NewGrafanaDashboardManager(s.Context)
	if err := grafanaDashboardManager.ConfigureGrafanaDashboards(s.instance); err != nil {
		s.Log.Error(err, "Could not deploy grafana dashboards")
		return infrastructure.ErrorForDashboards(err)
	}

	return nil
}
