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

package infrastructure

import (
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"k8s.io/apimachinery/pkg/types"
	"knative.dev/eventing/pkg/apis/eventing"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
)

const (
	// KnativeEventingBrokerKind is the Kind description for Knative Eventing Brokers
	KnativeEventingBrokerKind = "Broker"
)

var (
	// KnativeEventingAPIVersion API Group version as defined by Knative Eventing operator
	KnativeEventingAPIVersion = eventingv1.SchemeGroupVersion.String()
)

// KnativeHandler ...
type KnativeHandler interface {
	IsKnativeEventingAvailable() bool
	FetchBroker(key types.NamespacedName) (*eventingv1.Broker, error)
}

type knativeHandler struct {
	*operator.Context
}

// NewKnativeHandler ...
func NewKnativeHandler(context *operator.Context) KnativeHandler {
	return &knativeHandler{
		context,
	}
}

// IsKnativeEventingAvailable checks if Knative Eventing CRDs are available in the cluster
func (k *knativeHandler) IsKnativeEventingAvailable() bool {
	return k.Client.HasServerGroup(eventing.GroupName)
}

func (k *knativeHandler) FetchBroker(key types.NamespacedName) (*eventingv1.Broker, error) {
	broker := &eventingv1.Broker{}
	if exists, err := kubernetes.ResourceC(k.Client).FetchWithKey(key, broker); err != nil {
		return nil, err
	} else if !exists {
		return nil, nil
	}
	return broker, nil
}

// IsKnativeEventingResource checks if provided KogitoInfra instance is for Knative eventing resource
func IsKnativeEventingResource(apiVersion, kind string) bool {
	return apiVersion == KnativeEventingAPIVersion && kind == KnativeEventingBrokerKind
}
