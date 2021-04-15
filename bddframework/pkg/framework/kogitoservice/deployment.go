// Copyright 2021 Red Hat, Inc. and/or its affiliates
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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"strconv"
	"strings"
)

const (
	portName                 = "http"
	singleReplica            = int32(1)
	startupProbeMinorVersion = 18
)

// DeploymentHandler ...
type DeploymentHandler interface {
	CreateRequiredDeployment(service api.KogitoService, resolvedImage string, definition ServiceDefinition) *appsv1.Deployment
	IsDeploymentAvailable(kogitoService api.KogitoService) (bool, error)
}

type deploymentHandler struct {
	*operator.Context
}

// NewDeploymentHandler ...
func NewDeploymentHandler(context *operator.Context) DeploymentHandler {
	return &deploymentHandler{
		context,
	}
}

func (d *deploymentHandler) CreateRequiredDeployment(service api.KogitoService, resolvedImage string, definition ServiceDefinition) *appsv1.Deployment {
	if definition.SingleReplica && *service.GetSpec().GetReplicas() > singleReplica {
		service.GetSpec().SetReplicas(singleReplica)
		d.Log.Warn("Service can't scale vertically, only one replica is allowed.", "service", service.GetName())
	}
	replicas := service.GetSpec().GetReplicas()
	probes := getProbeForKogitoService(definition, service)
	labels := service.GetSpec().GetDeploymentLabels()
	if labels == nil {
		labels = make(map[string]string)
	}
	labels[framework.LabelAppKey] = service.GetName()

	// clone env var slice so that any changes in deployment env var should not reflect in kogitoInstance env var
	// KOGITO-3947: we don't want an empty reference (0 len), since this is nil to k8s. Comparator will go crazy
	var env []corev1.EnvVar
	if len(service.GetSpec().GetEnvs()) > 0 {
		env = make([]corev1.EnvVar, len(service.GetSpec().GetEnvs()))
		copy(env, service.GetSpec().GetEnvs())
	}

	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: service.GetName(), Namespace: service.GetNamespace(), Labels: labels},
		Spec: appsv1.DeploymentSpec{
			Replicas: replicas,
			Selector: &metav1.LabelSelector{MatchLabels: map[string]string{framework.LabelAppKey: service.GetName()}},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{Labels: labels},
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{
						{
							Name: service.GetName(),
							Ports: []corev1.ContainerPort{
								{
									Name:          portName,
									ContainerPort: int32(framework.DefaultExposedPort),
									Protocol:      corev1.ProtocolTCP,
								},
							},
							Env:             env,
							Resources:       service.GetSpec().GetResources(),
							LivenessProbe:   probes.liveness,
							ReadinessProbe:  probes.readiness,
							ImagePullPolicy: corev1.PullAlways,
							Image:           resolvedImage,
						},
					},
				},
			},
			Strategy: appsv1.DeploymentStrategy{Type: appsv1.RollingUpdateDeploymentStrategyType},
		},
	}
	addStartupProbe(d, deployment, probes.startup)

	return deployment
}

// addStartupProbe adds a startup probe to deployment if the Kubernetes version is >= 1.18 when the feature is enabled by default
func addStartupProbe(d *deploymentHandler, deployment *appsv1.Deployment, startupProbe *corev1.Probe) {
	versionInfo, err := d.Client.Discovery.ServerVersion()
	if err != nil {
		d.Log.Warn("Could not access Kubernetes server version. Startup probes will not be added.")
	}
	d.Log.Debug("K8s version", "major version", versionInfo.Major, "minor version", versionInfo.Minor)
	minorVersion := strings.TrimSuffix(versionInfo.Minor, "+")
	if minorVersionInt, err := strconv.Atoi(minorVersion); err != nil {
		d.Log.Warn("Could not parse Kubernetes server minor version. Startup probes will not be added.")
	} else if minorVersionInt >= startupProbeMinorVersion {
		deployment.Spec.Template.Spec.Containers[0].StartupProbe = startupProbe
	}
}

// IsDeploymentAvailable verifies if the Deployment resource from the given KogitoService has replicas available
func (d *deploymentHandler) IsDeploymentAvailable(kogitoService api.KogitoService) (bool, error) {
	// service's deployment hasn't been deployed yet, no need to fetch
	if len(kogitoService.GetStatus().GetDeploymentConditions()) == 0 {
		return false, nil
	}

	coreDeployHandler := infrastructure.NewDeploymentHandler(d.Context)
	deployment, err := coreDeployHandler.FetchDeployment(types.NamespacedName{Name: kogitoService.GetName(), Namespace: kogitoService.GetNamespace()})
	if err != nil {
		return false, err
	} else if deployment == nil {
		return false, nil
	}
	return deployment.Status.AvailableReplicas > 0, nil
}
