// Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package profiles

import (
	"context"

	"k8s.io/klog/v2"

	"github.com/kiegroup/kogito-serverless-operator/log"

	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

type ObjectEnsurer interface {
	ensure(ctx context.Context, workflow *operatorapi.SonataFlow, visitors ...mutateVisitor) (client.Object, controllerutil.OperationResult, error)
}

// newDefaultObjectEnsurer see defaultObjectEnsurer
func newDefaultObjectEnsurer(client client.Client, creator objectCreator) ObjectEnsurer {
	return &defaultObjectEnsurer{
		client:  client,
		creator: creator,
	}
}

// defaultObjectEnsurer provides the engine for a ReconciliationState that needs to create or update a given Kubernetes object during the reconciliation cycle.
type defaultObjectEnsurer struct {
	client  client.Client
	creator objectCreator
}

// newDummyObjectEnsurer see dummyObjectEnsurer
func newDummyObjectEnsurer() ObjectEnsurer {
	return &dummyObjectEnsurer{}
}

// dummyObjectEnsurer is a useful Object ensurer to apply the null pattern. Use it when you need a creator that does nothing
type dummyObjectEnsurer struct {
}

// mutateVisitor is a visitor function that mutates the given object before performing any updates in the cluster.
// It gets called after the objectEnforcer reference.
//
// The defaultObjectEnsurer will call the returned mutateVisitor function after creating the given object structure,
// so callers is ensured to have the default reference of the given object.
//
// Usually you can safely do `object.(*<kubernetesType>).Spec...` since you control the objectCreator.
//
// Example: `object.(*appsv1.Deployment).Spec.Template.Name="myApp"` to change the pod's name.
type mutateVisitor func(object client.Object) controllerutil.MutateFn

func (d *defaultObjectEnsurer) ensure(ctx context.Context, workflow *operatorapi.SonataFlow, visitors ...mutateVisitor) (client.Object, controllerutil.OperationResult, error) {
	result := controllerutil.OperationResultNone

	object, err := d.creator(workflow)
	if err != nil {
		return nil, result, err
	}
	if result, err = controllerutil.CreateOrPatch(ctx, d.client, object,
		func() error {
			for _, v := range visitors {
				if visitorErr := v(object)(); visitorErr != nil {
					return visitorErr
				}
			}
			return controllerutil.SetControllerReference(workflow, object, d.client.Scheme())
		}); err != nil {
		return nil, result, err
	}
	klog.V(log.I).InfoS("Object operation finalized", "result", result, "kind", object.GetObjectKind().GroupVersionKind().String(), "name", object.GetName(), "namespace", object.GetNamespace())
	return object, result, nil
}

func (d *dummyObjectEnsurer) ensure(ctx context.Context, workflow *operatorapi.SonataFlow, visitors ...mutateVisitor) (client.Object, controllerutil.OperationResult, error) {
	result := controllerutil.OperationResultNone
	return nil, result, nil
}
