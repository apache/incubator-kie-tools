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

package framework

import (
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/client/apiutil"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
)

// IsOwner checks if the given owner is in the `ownerReferences` of the given resource
func IsOwner(resource client.Object, owner client.Object) bool {
	for _, resOwner := range resource.GetOwnerReferences() {
		if resOwner.UID == owner.GetUID() {
			return true
		}
	}
	return false
}

// SetOwner sets the given owner object into the given resources
func SetOwner(owner client.Object, scheme *runtime.Scheme, resources ...client.Object) error {
	for _, res := range resources {
		if err := controllerutil.SetControllerReference(owner, res, scheme); err != nil {
			return err
		}
	}
	return nil
}

// AddOwnerReference adds given owner as a OwnerReference in the given resources
func AddOwnerReference(owner client.Object, scheme *runtime.Scheme, resources ...client.Object) error {
	gvk, err := apiutil.GVKForObject(owner, scheme)
	if err != nil {
		return err
	}
	for _, res := range resources {
		if !IsOwner(res, owner) {
			owners := res.GetOwnerReferences()
			falseValue := false
			owners = append(owners, v1.OwnerReference{
				APIVersion:         gvk.GroupVersion().String(),
				Kind:               gvk.Kind,
				Name:               owner.GetName(),
				Controller:         &falseValue,
				BlockOwnerDeletion: &falseValue,
				UID:                owner.GetUID(),
			})
			res.SetOwnerReferences(owners)
		}
	}
	return nil
}

// RemoveOwnerReference remove given owner from OwnerReference in the given resources
func RemoveOwnerReference(owner client.Object, resources ...client.Object) {
	for _, res := range resources {
		for i, ownerRef := range res.GetOwnerReferences() {
			if ownerRef.UID == owner.GetUID() {
				updatedOwnerReferences := append(res.GetOwnerReferences()[:i], res.GetOwnerReferences()[i+1:]...)
				res.SetOwnerReferences(updatedOwnerReferences)
				break
			}
		}
	}
}

// RemoveSharedOwnerReference remove given owner from OwnerReference in the given resources only if resource is referred by multiple owners.
func RemoveSharedOwnerReference(owner client.Object, resources ...client.Object) bool {
	for _, res := range resources {
		ownerRefs := res.GetOwnerReferences()
		// remove owner reference only if resource is referred by multiple owners
		if len(ownerRefs) > 1 {
			for i, ownerRef := range ownerRefs {
				if ownerRef.UID == owner.GetUID() {
					updatedOwnerReferences := append(res.GetOwnerReferences()[:i], res.GetOwnerReferences()[i+1:]...)
					res.SetOwnerReferences(updatedOwnerReferences)
					return true
				}
			}
		}
	}
	return false
}
