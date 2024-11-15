// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package monitoring

import (
	"context"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/monitoring"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

var _ MonitoringEventingHandler = &monitoringObjectManager{}

type monitoringObjectManager struct {
	serviceMonitor common.ObjectEnsurer
	*common.StateSupport
}

func NewMonitoringHandler(support *common.StateSupport) MonitoringEventingHandler {
	return &monitoringObjectManager{
		serviceMonitor: common.NewObjectEnsurer(support.C, common.ServiceMonitorCreator),
		StateSupport:   support,
	}
}

type MonitoringEventingHandler interface {
	Ensure(ctx context.Context, workflow *operatorapi.SonataFlow) ([]client.Object, error)
}

func (k monitoringObjectManager) Ensure(ctx context.Context, workflow *operatorapi.SonataFlow) ([]client.Object, error) {
	var objs []client.Object
	monitoringAvail, err := monitoring.GetPrometheusAvailability(k.Cfg)
	if err != nil {
		klog.V(log.I).InfoS("Error checking Prometheus availability: %v", err)
		return nil, err
	}
	if monitoringAvail {
		// create serviceMonitor
		serviceMonitor, _, err := k.serviceMonitor.Ensure(ctx, workflow)
		if err != nil {
			return objs, err
		} else if serviceMonitor != nil {
			objs = append(objs, serviceMonitor)
		}
	}
	return objs, nil
}
