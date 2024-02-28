/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package common

import (
	"context"

	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
)

var _ ObjectEnsurer = &defaultObjectEnsurer{}
var _ ObjectEnsurer = &noopObjectEnsurer{}

type ObjectEnsurer interface {
	Ensure(ctx context.Context, workflow *operatorapi.SonataFlow, visitors ...MutateVisitor) (client.Object, controllerutil.OperationResult, error)
}
type ObjectEnsurerWithPlatform interface {
	Ensure(ctx context.Context, workflow *operatorapi.SonataFlow, pl *operatorapi.SonataFlowPlatform, visitors ...MutateVisitor) (client.Object, controllerutil.OperationResult, error)
}

// MutateVisitor is a visitor function that mutates the given object before performing any updates in the cluster.
// It gets called after the objectEnforcer reference.
//
// The defaultObjectEnsurer will call the returned MutateVisitor function after creating the given object structure,
// so callers is ensured to have the default reference of the given object.
//
// Usually you can safely do `object.(*<kubernetesType>).Spec...` since you control the ObjectCreator.
//
// Example: `object.(*appsv1.Deployment).Spec.Template.Name="myApp"` to change the pod's name.
type MutateVisitor func(object client.Object) controllerutil.MutateFn

// NewObjectEnsurer see defaultObjectEnsurer
func NewObjectEnsurer(client client.Client, creator ObjectCreator) ObjectEnsurer {
	return &defaultObjectEnsurer{
		c:       client,
		creator: creator,
	}
}

// NewObjectEnsurerWithPlatform see defaultObjectEnsurerWithPLatform
func NewObjectEnsurerWithPlatform(client client.Client, creator ObjectCreatorWithPlatform) ObjectEnsurerWithPlatform {
	return &defaultObjectEnsurerWithPlatform{
		c:       client,
		creator: creator,
	}
}

// defaultObjectEnsurer provides the engine for a ReconciliationState that needs to create or update a given Kubernetes object during the reconciliation cycle.
type defaultObjectEnsurer struct {
	c       client.Client
	creator ObjectCreator
}

func (d *defaultObjectEnsurer) Ensure(ctx context.Context, workflow *operatorapi.SonataFlow, visitors ...MutateVisitor) (client.Object, controllerutil.OperationResult, error) {
	result := controllerutil.OperationResultNone

	object, err := d.creator(workflow)
	if err != nil {
		return nil, result, err
	}
	if result, err = controllerutil.CreateOrPatch(ctx, d.c, object,
		func() error {
			for _, v := range visitors {
				if visitorErr := v(object)(); visitorErr != nil {
					return visitorErr
				}
			}
			return controllerutil.SetControllerReference(workflow, object, d.c.Scheme())
		}); err != nil {
		return nil, result, err
	}
	klog.V(log.I).InfoS("Object operation finalized", "result", result, "kind", object.GetObjectKind().GroupVersionKind().String(), "name", object.GetName(), "namespace", object.GetNamespace())
	return object, result, nil
}

// defaultObjectEnsurerWithPlatform is the equivalent of defaultObjectEnsurer for resources that require a reference to the SonataFlowPlatform
type defaultObjectEnsurerWithPlatform struct {
	c       client.Client
	creator ObjectCreatorWithPlatform
}

func (d *defaultObjectEnsurerWithPlatform) Ensure(ctx context.Context, workflow *operatorapi.SonataFlow, pl *operatorapi.SonataFlowPlatform, visitors ...MutateVisitor) (client.Object, controllerutil.OperationResult, error) {
	result := controllerutil.OperationResultNone

	object, err := d.creator(workflow, pl)
	if err != nil {
		return nil, result, err
	}
	if result, err = controllerutil.CreateOrPatch(ctx, d.c, object,
		func() error {
			for _, v := range visitors {
				if visitorErr := v(object)(); visitorErr != nil {
					return visitorErr
				}
			}
			return controllerutil.SetControllerReference(workflow, object, d.c.Scheme())
		}); err != nil {
		return nil, result, err
	}
	klog.V(log.I).InfoS("Object operation finalized", "result", result, "kind", object.GetObjectKind().GroupVersionKind().String(), "name", object.GetName(), "namespace", object.GetNamespace())
	return object, result, nil
}

// NewNoopObjectEnsurer see noopObjectEnsurer
func NewNoopObjectEnsurer() ObjectEnsurer {
	return &noopObjectEnsurer{}
}

// noopObjectEnsurer is a useful Object ensurer to apply the null pattern. Use it when you need a creator that does nothing
type noopObjectEnsurer struct {
}

func (d *noopObjectEnsurer) Ensure(ctx context.Context, workflow *operatorapi.SonataFlow, visitors ...MutateVisitor) (client.Object, controllerutil.OperationResult, error) {
	result := controllerutil.OperationResultNone
	return nil, result, nil
}
