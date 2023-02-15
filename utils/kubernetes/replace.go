// Copyright 2022 Red Hat, Inc. and/or its affiliates
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

package kubernetes

import (
	"context"
	"fmt"

	client "github.com/kiegroup/container-builder/client"

	"github.com/pkg/errors"

	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"

	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	routev1 "github.com/openshift/api/route/v1"
)

// ReplaceResource allows to completely replace a resource on Kubernetes, taking care of immutable fields and resource versions.
func ReplaceResource(ctx context.Context, c client.Client, res ctrl.Object) (bool, error) {
	replaced := false
	err := c.Create(ctx, res)
	if err != nil && k8serrors.IsAlreadyExists(err) {
		replaced = true
		existing, ok := res.DeepCopyObject().(ctrl.Object)
		if !ok {
			return replaced, fmt.Errorf("type assertion failed: %v", res.DeepCopyObject())
		}
		err = c.Get(ctx, ctrl.ObjectKeyFromObject(existing), existing)
		if err != nil {
			return replaced, err
		}
		mapRequiredMeta(existing, res)
		mapRequiredServiceData(existing, res)
		mapRequiredRouteData(existing, res)
		err = c.Update(ctx, res)
	}
	if err != nil {
		return replaced, errors.Wrap(err, "could not create or replace "+findResourceDetails(res))
	}
	return replaced, nil
}

func mapRequiredMeta(from ctrl.Object, to ctrl.Object) {
	to.SetResourceVersion(from.GetResourceVersion())
}

func mapRequiredServiceData(from runtime.Object, to runtime.Object) {
	if fromC, ok := from.(*corev1.Service); ok {
		if toC, ok := to.(*corev1.Service); ok {
			toC.Spec.ClusterIP = fromC.Spec.ClusterIP
		}
	}
}

func mapRequiredRouteData(from runtime.Object, to runtime.Object) {
	if fromC, ok := from.(*routev1.Route); ok {
		if toC, ok := to.(*routev1.Route); ok {
			toC.Spec.Host = fromC.Spec.Host
		}
	}
}

func findResourceDetails(res ctrl.Object) string {
	if res == nil {
		return "nil resource"
	}
	return res.GetObjectKind().GroupVersionKind().String() + " " + res.GetName()
}
