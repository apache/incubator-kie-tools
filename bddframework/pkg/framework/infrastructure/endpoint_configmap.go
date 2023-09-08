// Copyright 2022 Red Hat, Inc. and/or its affiliates
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
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	endPointConfigMapSuffix = "-endpoint"
)

// EndPointConfigMapHandler ...
type EndPointConfigMapHandler interface {
	GetEndPointConfigMapName(serviceName string) string
	FetchEndPointConfigMap(key types.NamespacedName) (*v1.ConfigMap, error)
}

type endPointConfigMapHandler struct {
	operator.Context
	configMapHandler ConfigMapHandler
}

// NewEndPointConfigMapHandler ...
func NewEndPointConfigMapHandler(context operator.Context) EndPointConfigMapHandler {
	return &endPointConfigMapHandler{
		Context:          context,
		configMapHandler: NewConfigMapHandler(context),
	}
}

func (e endPointConfigMapHandler) GetEndPointConfigMapName(serviceName string) string {
	return serviceName + endPointConfigMapSuffix
}

func (e endPointConfigMapHandler) FetchEndPointConfigMap(serviceKey types.NamespacedName) (*v1.ConfigMap, error) {
	key := types.NamespacedName{
		Name:      e.GetEndPointConfigMapName(serviceKey.Name),
		Namespace: serviceKey.Namespace,
	}
	return e.configMapHandler.FetchConfigMap(key)
}
