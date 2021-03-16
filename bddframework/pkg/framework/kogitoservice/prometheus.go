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
	"github.com/kiegroup/kogito-operator/api"
	"net/http"

	monv1 "github.com/coreos/prometheus-operator/pkg/apis/monitoring/v1"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

const prometheusServerGroup = "monitoring.coreos.com"

// PrometheusManager ...
type PrometheusManager interface {
	ConfigurePrometheus(kogitoService api.KogitoService) error
}

type prometheusManager struct {
	*operator.Context
}

// NewPrometheusManager ...
func NewPrometheusManager(context *operator.Context) PrometheusManager {
	return &prometheusManager{
		context,
	}
}

func (m *prometheusManager) ConfigurePrometheus(kogitoService api.KogitoService) error {
	prometheusAvailable := m.isPrometheusAvailable()
	if !prometheusAvailable {
		m.Log.Debug("prometheus operator not available in namespace")
		return nil
	}

	deploymentHandler := NewDeploymentHandler(m.Context)
	deploymentAvailable, err := deploymentHandler.IsDeploymentAvailable(kogitoService)
	if err != nil {
		return err
	}
	if !deploymentAvailable {
		m.Log.Debug("Deployment is currently not available, will try in next reconciliation loop")
		return nil
	}

	prometheusAddOnAvailable, err := m.isPrometheusAddOnAvailable(kogitoService)
	if err != nil {
		return err
	}
	if prometheusAddOnAvailable {
		if err := m.createPrometheusServiceMonitorIfNotExists(kogitoService); err != nil {
			return err
		}
	}
	return nil
}

// isPrometheusAvailable checks if Prometheus CRD is available in the cluster
func (m *prometheusManager) isPrometheusAvailable() bool {
	return m.Client.HasServerGroup(prometheusServerGroup)
}

func (m *prometheusManager) isPrometheusAddOnAvailable(kogitoService api.KogitoService) (bool, error) {
	kogitoServiceHandler := NewKogitoServiceHandler(m.Context)
	url := kogitoServiceHandler.GetKogitoServiceEndpoint(kogitoService)
	url = url + getMonitoringPath(kogitoService.GetSpec().GetMonitoring())
	if resp, err := http.Head(url); err != nil {
		return false, err
	} else if resp.StatusCode == http.StatusOK {
		return true, nil
	}
	m.Log.Debug("Non-OK Http Status received")
	return false, nil
}

func (m *prometheusManager) createPrometheusServiceMonitorIfNotExists(kogitoService api.KogitoService) error {
	serviceMonitor, err := m.loadDeployedServiceMonitor(kogitoService.GetName(), kogitoService.GetNamespace())
	if err != nil {
		return err
	}
	if serviceMonitor == nil {
		_, err := m.createServiceMonitor(kogitoService)
		if err != nil {
			return err
		}
	}
	return nil
}

func (m *prometheusManager) loadDeployedServiceMonitor(instanceName, namespace string) (*monv1.ServiceMonitor, error) {
	m.Log.Debug("fetching deployed Service monitor instance", "instanceName", instanceName, "namespace", namespace)
	serviceMonitor := &monv1.ServiceMonitor{}
	if exits, err := kubernetes.ResourceC(m.Client).FetchWithKey(types.NamespacedName{Name: instanceName, Namespace: namespace}, serviceMonitor); err != nil {
		m.Log.Error(err, "Error occurs while fetching Service monitor instance")
		return nil, err
	} else if !exits {
		m.Log.Debug("Service monitor instance is not exists")
		return nil, nil
	} else {
		m.Log.Debug("Service monitor instance found")
		return serviceMonitor, nil
	}
}

// createServiceMonitor create ServiceMonitor used for scraping by prometheus for kogito service
func (m *prometheusManager) createServiceMonitor(kogitoService api.KogitoService) (*monv1.ServiceMonitor, error) {
	monitoring := kogitoService.GetSpec().GetMonitoring()
	endPoint := monv1.Endpoint{}
	endPoint.Path = getMonitoringPath(monitoring)
	endPoint.Scheme = getMonitoringScheme(monitoring)

	serviceSelectorLabels := make(map[string]string)
	serviceSelectorLabels[framework.LabelAppKey] = kogitoService.GetName()

	serviceMonitorLabels := make(map[string]string)
	serviceMonitorLabels["name"] = operator.Name
	serviceMonitorLabels[framework.LabelAppKey] = kogitoService.GetName()

	sm := &monv1.ServiceMonitor{
		ObjectMeta: metav1.ObjectMeta{
			Name:      kogitoService.GetName(),
			Namespace: kogitoService.GetNamespace(),
			Labels:    serviceMonitorLabels,
		},
		Spec: monv1.ServiceMonitorSpec{
			NamespaceSelector: monv1.NamespaceSelector{
				MatchNames: []string{
					kogitoService.GetNamespace(),
				},
			},
			Selector: metav1.LabelSelector{
				MatchLabels: serviceSelectorLabels,
			},
			Endpoints: []monv1.Endpoint{
				endPoint,
			},
		},
	}

	if err := framework.SetOwner(kogitoService, m.Scheme, sm); err != nil {
		return nil, err
	}
	if err := kubernetes.ResourceC(m.Client).Create(sm); err != nil {
		m.Log.Error(err, "Error occurs while creating Service Monitor instance")
		return nil, err
	}
	return sm, nil
}

func getMonitoringPath(monitoring api.MonitoringInterface) string {
	path := monitoring.GetPath()
	if len(path) == 0 {
		path = api.MonitoringDefaultPath
	}
	return path
}

func getMonitoringScheme(monitoring api.MonitoringInterface) string {
	scheme := monitoring.GetScheme()
	if len(scheme) == 0 {
		scheme = api.MonitoringDefaultScheme
	}
	return scheme
}
