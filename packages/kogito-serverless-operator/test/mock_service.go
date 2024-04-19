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

package test

import (
	"context"

	oappsv1 "github.com/openshift/api/apps/v1"
	buildv1 "github.com/openshift/api/build/v1"
	consolev1 "github.com/openshift/api/console/v1"
	oimagev1 "github.com/openshift/api/image/v1"
	routev1 "github.com/openshift/api/route/v1"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbacv1 "k8s.io/api/rbac/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/runtime/schema"
	clientv1 "sigs.k8s.io/controller-runtime/pkg/client"
)

type MockPlatformService struct {
	Client          clientv1.Client
	scheme          *runtime.Scheme
	CreateFunc      func(ctx context.Context, obj clientv1.Object, opts ...clientv1.CreateOption) error
	DeleteFunc      func(ctx context.Context, obj clientv1.Object, opts ...clientv1.DeleteOption) error
	GetFunc         func(ctx context.Context, key clientv1.ObjectKey, obj clientv1.Object) error
	ListFunc        func(ctx context.Context, list clientv1.ObjectList, opts ...clientv1.ListOption) error
	UpdateFunc      func(ctx context.Context, obj clientv1.Object, opts ...clientv1.UpdateOption) error
	PatchFunc       func(ctx context.Context, obj clientv1.Object, patch clientv1.Patch, opts ...clientv1.PatchOption) error
	DeleteAllOfFunc func(ctx context.Context, obj clientv1.Object, opts ...clientv1.DeleteAllOfOption) error
	GetCachedFunc   func(ctx context.Context, key clientv1.ObjectKey, obj clientv1.Object) error
	GetSchemeFunc   func() *runtime.Scheme
	StatusFunc      func() clientv1.StatusWriter
}

var knownTypes = map[schema.GroupVersion][]runtime.Object{
	corev1.SchemeGroupVersion: {
		&corev1.PersistentVolumeClaim{},
		&corev1.ServiceAccount{},
		&corev1.Secret{},
		&corev1.Service{},
		&corev1.ServiceList{},
		&corev1.PersistentVolumeClaimList{},
		&corev1.ServiceAccountList{},
		&corev1.ConfigMap{},
		&corev1.ConfigMapList{},
	},
	oappsv1.GroupVersion: {
		&oappsv1.DeploymentConfig{},
		&oappsv1.DeploymentConfigList{},
	},
	appsv1.SchemeGroupVersion: {
		&appsv1.StatefulSet{},
		&appsv1.StatefulSetList{},
	},
	routev1.GroupVersion: {
		&routev1.Route{},
		&routev1.RouteList{},
	},
	oimagev1.GroupVersion: {
		&oimagev1.ImageStream{},
		&oimagev1.ImageStreamList{},
	},
	rbacv1.SchemeGroupVersion: {
		&rbacv1.Role{},
		&rbacv1.RoleList{},
		&rbacv1.RoleBinding{},
		&rbacv1.RoleBindingList{},
	},
	buildv1.GroupVersion: {
		&buildv1.BuildConfig{},
		&buildv1.BuildConfigList{},
	},
	consolev1.GroupVersion: {
		&consolev1.ConsoleLink{},
		&consolev1.ConsoleLinkList{},
		&consolev1.ConsoleYAMLSample{},
		&consolev1.ConsoleYAMLSampleList{},
	},
}

func (service *MockPlatformService) Create(ctx context.Context, obj clientv1.Object, opts ...clientv1.CreateOption) error {
	return service.CreateFunc(ctx, obj, opts...)
}

func (service *MockPlatformService) Delete(ctx context.Context, obj clientv1.Object, opts ...clientv1.DeleteOption) error {
	return service.DeleteFunc(ctx, obj, opts...)
}

func (service *MockPlatformService) Get(ctx context.Context, key clientv1.ObjectKey, obj clientv1.Object) error {
	return service.GetFunc(ctx, key, obj)
}

func (service *MockPlatformService) List(ctx context.Context, list clientv1.ObjectList, opts ...clientv1.ListOption) error {
	return service.ListFunc(ctx, list, opts...)
}

func (service *MockPlatformService) Update(ctx context.Context, obj clientv1.Object, opts ...clientv1.UpdateOption) error {
	return service.UpdateFunc(ctx, obj, opts...)
}

func (service *MockPlatformService) Patch(ctx context.Context, obj clientv1.Object, patch clientv1.Patch, opts ...clientv1.PatchOption) error {
	return service.PatchFunc(ctx, obj, patch, opts...)
}

func (service *MockPlatformService) DeleteAllOf(ctx context.Context, obj clientv1.Object, opts ...clientv1.DeleteAllOfOption) error {
	return service.DeleteAllOfFunc(ctx, obj, opts...)
}

func (service *MockPlatformService) GetCached(ctx context.Context, key clientv1.ObjectKey, obj clientv1.Object) error {
	return service.GetCachedFunc(ctx, key, obj)
}

func (service *MockPlatformService) GetScheme() *runtime.Scheme {
	return service.GetSchemeFunc()
}

func (service *MockPlatformService) Status() clientv1.StatusWriter {
	return service.StatusFunc()
}

func (service *MockPlatformService) IsMockService() bool {
	return true
}
