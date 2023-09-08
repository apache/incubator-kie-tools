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
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	apps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"testing"
)

func TestIsOwner(t *testing.T) {
	uuid := test.GenerateUID()
	type args struct {
		resource client.Object
		owner    client.Object
	}
	tests := []struct {
		name string
		args args
		want bool
	}{
		{
			"I own you",
			args{
				resource: &corev1.ConfigMap{ObjectMeta: metav1.ObjectMeta{Name: "config-map", Namespace: t.Name(),
					OwnerReferences: []metav1.OwnerReference{{
						Kind: "Deployment",
						Name: "deployment",
						UID:  uuid,
					}}},
				},
				owner: &apps.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment", Namespace: t.Name(), UID: uuid}},
			},
			true,
		},
		{
			"I don't own you",
			args{
				resource: &corev1.ConfigMap{ObjectMeta: metav1.ObjectMeta{Name: "config-map", Namespace: t.Name()}},
				owner:    &apps.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment", Namespace: t.Name(), UID: uuid}},
			},
			false,
		},
		{
			"I'm not the only one :(",
			args{
				resource: &corev1.ConfigMap{ObjectMeta: metav1.ObjectMeta{Name: "config-map", Namespace: t.Name(),
					OwnerReferences: []metav1.OwnerReference{
						{
							Kind: "Deployment",
							Name: "deployment",
							UID:  uuid,
						},
						{
							Kind: "BuildConfig",
							Name: "the-builder",
							UID:  test.GenerateUID(),
						},
					}},
				},
				owner: &apps.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment", Namespace: t.Name(), UID: uuid}},
			},
			true,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := IsOwner(tt.args.resource, tt.args.owner)
			if got != tt.want {
				t.Errorf("IsOwner() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestAddOwnerReference(t *testing.T) {
	scheme := meta.GetRegisteredSchema()
	owner := &apps.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment", Namespace: t.Name(), UID: test.GenerateUID()}}
	owned := &corev1.ConfigMap{ObjectMeta: metav1.ObjectMeta{Name: "config-map", Namespace: t.Name(), UID: test.GenerateUID()}}

	err := AddOwnerReference(owner, scheme, owned)
	assert.NoError(t, err)
	assert.Len(t, owned.OwnerReferences, 1)

	err = AddOwnerReference(owner, scheme, owned)
	assert.NoError(t, err)
	assert.Len(t, owned.OwnerReferences, 1)
}

func TestRemoveOwnerReference(t *testing.T) {
	scheme := meta.GetRegisteredSchema()

	owner := &apps.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment", Namespace: t.Name(), UID: test.GenerateUID()}}
	owned := &corev1.ConfigMap{ObjectMeta: metav1.ObjectMeta{Name: "config-map", Namespace: t.Name(), UID: test.GenerateUID()}}
	err := AddOwnerReference(owner, scheme, owned)
	assert.NoError(t, err)

	assert.Equal(t, 1, len(owned.GetOwnerReferences()))
	RemoveOwnerReference(owner, owned)
	assert.Equal(t, 0, len(owned.GetOwnerReferences()))
}

func TestRemoveSharedOwnerReference_ResourceNotShared(t *testing.T) {
	scheme := meta.GetRegisteredSchema()

	owner := &apps.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment", Namespace: t.Name(), UID: test.GenerateUID()}}
	owned := &corev1.ConfigMap{ObjectMeta: metav1.ObjectMeta{Name: "config-map", Namespace: t.Name(), UID: test.GenerateUID()}}
	err := AddOwnerReference(owner, scheme, owned)
	assert.NoError(t, err)

	assert.Equal(t, 1, len(owned.GetOwnerReferences()))
	RemoveSharedOwnerReference(owner, owned)
	assert.Equal(t, 1, len(owned.GetOwnerReferences()))
}

func TestRemoveSharedOwnerReference_ResourceShared(t *testing.T) {
	scheme := meta.GetRegisteredSchema()

	owner1 := &apps.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment1", Namespace: t.Name(), UID: test.GenerateUID()}}
	owner2 := &apps.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment2", Namespace: t.Name(), UID: test.GenerateUID()}}
	owned := &corev1.ConfigMap{ObjectMeta: metav1.ObjectMeta{Name: "config-map", Namespace: t.Name(), UID: test.GenerateUID()}}
	err := AddOwnerReference(owner1, scheme, owned)
	assert.NoError(t, err)
	err = AddOwnerReference(owner2, scheme, owned)
	assert.NoError(t, err)

	RemoveSharedOwnerReference(owner1, owned)
	assert.Equal(t, 1, len(owned.GetOwnerReferences()))
	assert.Equal(t, "deployment2", owned.GetOwnerReferences()[0].Name)
}
