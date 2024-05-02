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

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/knative"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

var _ KnativeEventingHandler = &knativeObjectManager{}

type knativeObjectManager struct {
	sinkBinding ObjectEnsurer
	trigger     ObjectsEnsurer
	*StateSupport
}

func NewKnativeEventingHandler(support *StateSupport) KnativeEventingHandler {
	return &knativeObjectManager{
		sinkBinding:  NewObjectEnsurer(support.C, SinkBindingCreator),
		trigger:      NewObjectsEnsurer(support.C, TriggersCreator),
		StateSupport: support,
	}
}

type KnativeEventingHandler interface {
	Ensure(ctx context.Context, workflow *operatorapi.SonataFlow) ([]client.Object, error)
}

func (k knativeObjectManager) Ensure(ctx context.Context, workflow *operatorapi.SonataFlow) ([]client.Object, error) {
	var objs []client.Object

	if workflow.Spec.Flow.Events == nil {
		// skip if no event is found
		klog.V(log.I).InfoS("skip knative resource creation as no event is found")
	} else if workflow.Spec.Sink == nil {
		klog.V(log.I).InfoS("Spec.Sink is not provided")
	} else if knativeAvail, err := knative.GetKnativeAvailability(k.Cfg); err != nil || knativeAvail == nil || !knativeAvail.Eventing {
		klog.V(log.I).InfoS("Knative Eventing is not installed")
	} else {
		// create sinkBinding and trigger
		sinkBinding, _, err := k.sinkBinding.Ensure(ctx, workflow)
		if err != nil {
			return objs, err
		} else if sinkBinding != nil {
			objs = append(objs, sinkBinding)
		}

		triggers := k.trigger.Ensure(ctx, workflow)
		for _, trigger := range triggers {
			if trigger.Error != nil {
				return objs, trigger.Error
			}
			objs = append(objs, trigger.Object)
		}
	}
	return objs, nil
}
