// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package infrastructure

import (
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/intstr"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	defaultHTTPPort = 80
)

// ServiceHandler ...
type ServiceHandler interface {
	FetchService(key types.NamespacedName) (*corev1.Service, error)
	CreateService(instance api.KogitoService) *corev1.Service
	GetComparator() compare.MapComparator
}

type serviceHandler struct {
	operator.Context
}

// NewServiceHandler ...
func NewServiceHandler(context operator.Context) ServiceHandler {
	return &serviceHandler{
		context,
	}
}

func (s *serviceHandler) FetchService(key types.NamespacedName) (*corev1.Service, error) {
	s.Log.Debug("fetching service.")
	service := &corev1.Service{}
	if exists, err := kubernetes.ResourceC(s.Client).FetchWithKey(key, service); err != nil {
		return nil, err
	} else if !exists {
		s.Log.Debug("Service not found.")
		return nil, nil
	} else {
		s.Log.Debug("Successfully fetch deployed Service")
		return service, nil
	}
}

func (s *serviceHandler) CreateService(instance api.KogitoService) *corev1.Service {
	ports := createServicePorts()
	labels := instance.GetSpec().GetServiceLabels()
	if labels == nil {
		labels = make(map[string]string)
	}
	labels[framework.LabelAppKey] = instance.GetName()

	svc := corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      instance.GetName(),
			Namespace: instance.GetNamespace(),
			Labels:    labels,
		},
		Spec: corev1.ServiceSpec{
			Ports:    ports,
			Selector: map[string]string{framework.LabelAppKey: instance.GetName()},
			Type:     corev1.ServiceTypeClusterIP,
		},
	}
	return &svc
}

// createServicePorts converts ports defined in the given container to ServicePorts
func createServicePorts() []corev1.ServicePort {
	svcPorts := []corev1.ServicePort{
		{
			Name:       framework.DefaultPortName,
			Protocol:   corev1.ProtocolTCP,
			Port:       defaultHTTPPort,
			TargetPort: intstr.FromInt(framework.DefaultExposedPort),
		},
	}
	return svcPorts
}

func (s *serviceHandler) GetComparator() compare.MapComparator {
	resourceComparator := compare.DefaultComparator()
	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(corev1.Service{})).
			WithCustomComparator(s.createServiceComparator()).
			Build())
	return compare.MapComparator{Comparator: resourceComparator}
}

// createServiceComparator creates a new comparator for Service only checking required fields
func (s *serviceHandler) createServiceComparator() func(deployed client.Object, requested client.Object) bool {
	return func(deployed client.Object, requested client.Object) bool {
		svcDeployed := deployed.(*corev1.Service).DeepCopy()
		svcRequested := requested.(*corev1.Service)

		// Remove generated fields from deployed version, when not specified in requested object
		for _, portRequested := range svcRequested.Spec.Ports {
			if found, portDeployed := findServicePort(portRequested, svcDeployed.Spec.Ports); found {
				if portRequested.Protocol == "" {
					portDeployed.Protocol = ""
				}
			}
		}
		// Ignore empty label maps
		if svcRequested.GetLabels() == nil && svcDeployed.GetLabels() != nil && len(svcDeployed.GetLabels()) == 0 {
			svcDeployed.SetLabels(nil)
		}

		var pairs [][2]interface{}
		pairs = append(pairs, [2]interface{}{svcDeployed.Name, svcRequested.Name})
		pairs = append(pairs, [2]interface{}{svcDeployed.Namespace, svcRequested.Namespace})
		pairs = append(pairs, [2]interface{}{svcDeployed.Labels, svcRequested.Labels})
		pairs = append(pairs, [2]interface{}{svcDeployed.Spec.Ports, svcRequested.Spec.Ports})
		pairs = append(pairs, [2]interface{}{svcDeployed.Spec.Selector, svcRequested.Spec.Selector})
		pairs = append(pairs, [2]interface{}{svcDeployed.Spec.Type, svcRequested.Spec.Type})
		equal := compare.EqualPairs(pairs)

		if !equal {
			s.Log.Debug("Resources are not equal", "deployed", deployed, "requested", requested)
		}
		return equal
	}
}

// See https://github.com/RHsyseng/operator-utils/blob/0f7acfb7a492851cad0ca5eb327b85cee0aa7e10/pkg/resource/compare/defaults.go#L424
func findServicePort(port corev1.ServicePort, ports []corev1.ServicePort) (bool, *corev1.ServicePort) {
	for index, candidate := range ports {
		if port.Name == candidate.Name {
			return true, &ports[index]
		}
	}
	return false, &corev1.ServicePort{}
}
