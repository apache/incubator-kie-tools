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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/record"
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
	// SingleReplica if set to true, avoids that the service has more than one pod replica
	SingleReplica bool
	// KafkaTopics is a collection of Kafka Topics to be created within the service
	KafkaTopics []string
	// CustomService indicates that the service can be built within the cluster
	// A custom service means that could be built by a third party, not being provided by the Kogito Team Services catalog (such as Data Index, Management Console and etc.).
	CustomService bool

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
	if err = imageHandler.ReconcileImageStream(s.instance); err != nil {
		return err
	}

	deploymentReconciler := newDeploymentReconciler(s.Context, s.instance, s.definition, imageHandler)
	if err = deploymentReconciler.Reconcile(); err != nil {
		return err
	}

	serviceReconciler := newServiceReconciler(s.Context, s.instance)
	if err = serviceReconciler.Reconcile(); err != nil {
		return err
	}

	routeReconciler := newRouteReconciler(s.Context, s.instance)
	if err = routeReconciler.Reconcile(); err != nil {
		return err
	}

	err = s.configureMonitoring()
	if err != nil {
		return err
	}

	err = s.configureMessaging()

	return err
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

func (s *serviceDeployer) newImageHandler() infrastructure.ImageHandler {
	addDockerImageReference := len(s.instance.GetSpec().GetImage()) != 0 || !s.definition.CustomService
	image := s.resolveImage()
	return infrastructure.NewImageHandler(s.Context, image, s.definition.DefaultImageName, image.Name, s.instance.GetNamespace(), addDockerImageReference, s.instance.GetSpec().IsInsecureImageRegistry())
}

func (s *serviceDeployer) resolveImage() *api.Image {
	var image api.Image
	if len(s.instance.GetSpec().GetImage()) == 0 {
		image = api.Image{
			Name: s.definition.DefaultImageName,
			Tag:  s.definition.DefaultImageTag,
		}
	} else {
		image = framework.ConvertImageTagToImage(s.instance.GetSpec().GetImage())
	}
	return &image
}
