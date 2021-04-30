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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// ServiceHandler ...
type ServiceHandler interface {
	CreateService(instance api.KogitoService, deployment *appsv1.Deployment) *corev1.Service
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

func (s *serviceHandler) CreateService(instance api.KogitoService, deployment *appsv1.Deployment) *corev1.Service {
	ports := framework.ExtractPortsFromContainer(&deployment.Spec.Template.Spec.Containers[0])
	if len(ports) == 0 {
		// a service without port to expose doesn't exist
		s.Log.Warn("The deployment spec doesn't have any ports exposed. Won't be possible to create a new service.", "deployment", deployment.Name)
		return nil
	}

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
			Selector: deployment.Spec.Selector.MatchLabels,
			Type:     corev1.ServiceTypeClusterIP,
		},
	}

	return &svc
}
