// Copyright 2024 Apache Software Foundation (ASF)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package common

import (
	"context"

	"k8s.io/klog/v2"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

var _ KnativeEventingHandler = &knativeObjectManager{}

type knativeObjectManager struct {
	sinkBinding ObjectEnsurerWithPlatform
	trigger     ObjectsEnsurerWithPlatform
	platform    *operatorapi.SonataFlowPlatform
	*StateSupport
}

func NewKnativeEventingHandler(support *StateSupport, pl *operatorapi.SonataFlowPlatform) KnativeEventingHandler {
	return &knativeObjectManager{
		sinkBinding:  NewObjectEnsurerWithPlatform(support.C, SinkBindingCreator),
		trigger:      NewObjectsEnsurerWithPlatform(support.C, TriggersCreator),
		platform:     pl,
		StateSupport: support,
	}
}

type KnativeEventingHandler interface {
	Ensure(ctx context.Context, workflow *operatorapi.SonataFlow) ([]client.Object, error)
}

func (k knativeObjectManager) Ensure(ctx context.Context, workflow *operatorapi.SonataFlow) ([]client.Object, error) {
	var objs []client.Object

	knativeAvail, err := knative.GetKnativeAvailability(k.Cfg)
	if err != nil {
		klog.V(log.I).InfoS("Error checking Knative Eventing: %v", err)
		return nil, err
	}
	if !knativeAvail.Eventing {
		klog.V(log.I).InfoS("Knative Eventing is not installed")
	} else {
		// create sinkBinding and trigger
		sinkBinding, _, err := k.sinkBinding.Ensure(ctx, workflow, k.platform)
		if err != nil {
			return objs, err
		} else if sinkBinding != nil {
			objs = append(objs, sinkBinding)
		}

		triggers := k.trigger.Ensure(ctx, workflow, k.platform)
		for _, trigger := range triggers {
			if trigger.Error != nil {
				return objs, trigger.Error
			}
			objs = append(objs, trigger.Object)
		}
	}
	return objs, nil
}
