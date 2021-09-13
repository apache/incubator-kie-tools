// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package connector

import (
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	"github.com/kiegroup/kogito-operator/core/client"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal"
	"github.com/kiegroup/kogito-operator/meta"
	"testing"

	"github.com/google/uuid"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

func TestInjectDataIndexURLIntoKogitoRuntime(t *testing.T) {
	ns := t.Name()
	name := "my-kogito-app"
	expectedRoute := "http://dataindex-route.com"
	kogitoRuntime := &v1beta1.KogitoRuntime{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: ns,
			UID:       types.UID(uuid.New().String()),
		},
	}
	dc := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: kogitoRuntime.Name, Namespace: kogitoRuntime.Namespace, OwnerReferences: []metav1.OwnerReference{{UID: kogitoRuntime.UID}}},
		Spec: appsv1.DeploymentSpec{
			Template: v1.PodTemplateSpec{
				Spec: v1.PodSpec{Containers: []v1.Container{{Name: "test"}}},
			},
		},
	}
	dataIndex := &v1beta1.KogitoSupportingService{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "data-index",
			Namespace: ns,
		},
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.DataIndex,
		},
		Status: v1beta1.KogitoSupportingServiceStatus{
			KogitoServiceStatus: v1beta1.KogitoServiceStatus{ExternalURI: expectedRoute},
		},
	}

	cli := test.NewFakeClientBuilder().AddK8sObjects(dc, kogitoRuntime, dataIndex).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	runtimeHandler := internal.NewKogitoRuntimeHandler(context)
	supportingServiceHandler := internal.NewKogitoSupportingServiceHandler(context)
	urlHandler := NewURLHandler(context, runtimeHandler, supportingServiceHandler)
	err := urlHandler.InjectDataIndexURLIntoKogitoRuntimeServices(ns)
	assert.NoError(t, err)

	exist, err := kubernetes.ResourceC(cli).Fetch(dc)
	assert.NoError(t, err)
	assert.True(t, exist)
	assert.Contains(t, dc.Spec.Template.Spec.Containers[0].Env, v1.EnvVar{Name: dataIndexHTTPRouteEnv, Value: expectedRoute})
}

func TestInjectJobsServicesURLIntoKogitoRuntime(t *testing.T) {
	URI := "http://localhost:8080"
	app := &v1beta1.KogitoRuntime{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "kogito-app",
			Namespace: t.Name(),
			UID:       types.UID(uuid.New().String()),
		},
	}
	jobs := &v1beta1.KogitoSupportingService{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "jobs-service",
			Namespace: t.Name(),
		},
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.JobsService,
		},
		Status: v1beta1.KogitoSupportingServiceStatus{
			KogitoServiceStatus: v1beta1.KogitoServiceStatus{
				ExternalURI: URI,
			},
		},
	}
	dc := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: "dc", Namespace: t.Name(), OwnerReferences: []metav1.OwnerReference{{
			Name: app.Name,
			UID:  app.UID,
		}}},
		Spec: appsv1.DeploymentSpec{
			Template: v1.PodTemplateSpec{Spec: v1.PodSpec{Containers: []v1.Container{{Name: "the-app"}}}},
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(app, dc, jobs).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	runtimeHandler := internal.NewKogitoRuntimeHandler(context)
	supportingServiceHandler := internal.NewKogitoSupportingServiceHandler(context)
	urlHandler := NewURLHandler(context, runtimeHandler, supportingServiceHandler)
	err := urlHandler.InjectJobsServicesURLIntoKogitoRuntimeServices(t.Name())
	assert.NoError(t, err)
	assert.Len(t, dc.Spec.Template.Spec.Containers[0].Env, 0)

	exists, err := kubernetes.ResourceC(cli).Fetch(dc)
	assert.NoError(t, err)
	assert.True(t, exists)
	assert.Len(t, dc.Spec.Template.Spec.Containers[0].Env, 1)
	assert.Contains(t, dc.Spec.Template.Spec.Containers[0].Env, v1.EnvVar{
		Name:  jobsServicesHTTPRouteEnv,
		Value: URI,
	})
}

func TestInjectJobsServicesURLIntoKogitoRuntimeCleanUp(t *testing.T) {
	URI := "http://localhost:8080"
	app := &v1beta1.KogitoRuntime{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "kogito-app",
			Namespace: t.Name(),
			UID:       types.UID(uuid.New().String()),
		},
	}
	jobs := &v1beta1.KogitoSupportingService{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "jobs-service",
			Namespace: t.Name(),
		},
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.JobsService,
		},
		Status: v1beta1.KogitoSupportingServiceStatus{KogitoServiceStatus: v1beta1.KogitoServiceStatus{ExternalURI: URI}},
	}
	dc := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: "dc", Namespace: t.Name(), OwnerReferences: []metav1.OwnerReference{{
			Name: app.Name,
			UID:  app.UID,
		}}},
		Spec: appsv1.DeploymentSpec{
			Template: v1.PodTemplateSpec{Spec: v1.PodSpec{Containers: []v1.Container{{Name: "the-app"}}}},
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(dc, app, jobs).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	runtimeHandler := internal.NewKogitoRuntimeHandler(context)
	supportingServiceHandler := internal.NewKogitoSupportingServiceHandler(context)
	urlHandler := NewURLHandler(context, runtimeHandler, supportingServiceHandler)
	// first we inject
	err := urlHandler.InjectJobsServicesURLIntoKogitoRuntimeServices(t.Name())
	assert.NoError(t, err)

	exists, err := kubernetes.ResourceC(cli).Fetch(dc)
	assert.NoError(t, err)
	assert.True(t, exists)
	assert.Contains(t, dc.Spec.Template.Spec.Containers[0].Env, v1.EnvVar{
		Name:  jobsServicesHTTPRouteEnv,
		Value: URI,
	})
}

func TestInjectTrustyURLIntoKogitoApps(t *testing.T) {
	ns := t.Name()
	name := "my-kogito-app"
	expectedRoute := "http://trusty-route.com"
	kogitoRuntime := &v1beta1.KogitoRuntime{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: ns,
			UID:       types.UID(uuid.New().String()),
		},
	}
	dc := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: kogitoRuntime.Name, Namespace: kogitoRuntime.Namespace, OwnerReferences: []metav1.OwnerReference{{UID: kogitoRuntime.UID}}},
		Spec: appsv1.DeploymentSpec{
			Template: v1.PodTemplateSpec{
				Spec: v1.PodSpec{Containers: []v1.Container{{Name: "test"}}},
			},
		},
	}
	trustyService := &v1beta1.KogitoSupportingService{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "trusty",
			Namespace: ns,
		},
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.TrustyAI,
		},
		Status: v1beta1.KogitoSupportingServiceStatus{
			KogitoServiceStatus: v1beta1.KogitoServiceStatus{ExternalURI: expectedRoute},
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(kogitoRuntime, dc, trustyService).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	runtimeHandler := internal.NewKogitoRuntimeHandler(context)
	supportingServiceHandler := internal.NewKogitoSupportingServiceHandler(context)
	urlHandler := NewURLHandler(context, runtimeHandler, supportingServiceHandler)
	err := urlHandler.InjectTrustyURLIntoKogitoRuntimeServices(ns)
	assert.NoError(t, err)

	exist, err := kubernetes.ResourceC(cli).Fetch(dc)
	assert.NoError(t, err)
	assert.True(t, exist)
	assert.Contains(t, dc.Spec.Template.Spec.Containers[0].Env, v1.EnvVar{Name: trustyHTTPRouteEnv, Value: expectedRoute})
}

func Test_getKogitoDataIndexURLs(t *testing.T) {
	ns := t.Name()
	hostname := "dataindex-route.com"
	expectedHTTPURL := "http://" + hostname
	expectedWSURL := "ws://" + hostname
	expectedHTTPSURL := "https://" + hostname
	expectedWSSURL := "wss://" + hostname
	insecureDI := &v1beta1.KogitoSupportingService{
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.DataIndex,
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      "data-index",
			Namespace: ns,
		},
		Status: v1beta1.KogitoSupportingServiceStatus{
			KogitoServiceStatus: v1beta1.KogitoServiceStatus{
				ExternalURI: expectedHTTPURL,
			},
		},
	}
	secureDI := &v1beta1.KogitoSupportingService{
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.DataIndex,
		},
		ObjectMeta: metav1.ObjectMeta{Name: "data-index", Namespace: ns},
		Status:     v1beta1.KogitoSupportingServiceStatus{KogitoServiceStatus: v1beta1.KogitoServiceStatus{ExternalURI: expectedHTTPSURL}},
	}

	cliInsecure := test.NewFakeClientBuilder().AddK8sObjects(insecureDI).Build()
	cliSecure := test.NewFakeClientBuilder().AddK8sObjects(secureDI).Build()
	inSecureContext := operator.Context{
		Client: cliInsecure,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	secureContext := operator.Context{
		Client: cliSecure,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	inSecureSupportingServiceHandler := internal.NewKogitoSupportingServiceHandler(inSecureContext)
	secureSupportingServiceHandler := internal.NewKogitoSupportingServiceHandler(secureContext)
	type args struct {
		client                   *client.Client
		namespace                string
		supportingServiceHandler manager.KogitoSupportingServiceHandler
	}
	tests := []struct {
		name        string
		args        args
		wantHTTPURL string
		wantWSURL   string
		wantErr     bool
	}{
		{
			name: "With insecure route",
			args: args{
				client:                   cliInsecure,
				namespace:                ns,
				supportingServiceHandler: inSecureSupportingServiceHandler,
			},
			wantHTTPURL: expectedHTTPURL,
			wantWSURL:   expectedWSURL,
			wantErr:     false,
		},
		{
			name: "With secure route",
			args: args{
				client:                   cliSecure,
				namespace:                ns,
				supportingServiceHandler: secureSupportingServiceHandler,
			},
			wantHTTPURL: expectedHTTPSURL,
			wantWSURL:   expectedWSSURL,
			wantErr:     false,
		},
		{
			name: "With blank route",
			args: args{
				client:                   test.NewFakeClientBuilder().Build(),
				namespace:                ns,
				supportingServiceHandler: internal.NewKogitoSupportingServiceHandler(operator.Context{Client: test.NewFakeClientBuilder().Build(), Log: test.TestLogger}),
			},
			wantHTTPURL: "",
			wantWSURL:   "",
			wantErr:     false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			runtimeHandler := internal.NewKogitoRuntimeHandler(operator.Context{Client: tt.args.client, Log: test.TestLogger})
			urlHandler := &urlHandler{
				Context: operator.Context{
					Client: tt.args.client,
					Log:    test.TestLogger,
					Scheme: meta.GetRegisteredSchema(),
				},
				runtimeHandler:           runtimeHandler,
				supportingServiceHandler: tt.args.supportingServiceHandler,
			}
			gotDataIndexEndpoints, err := urlHandler.getSupportingServiceEndpoints(tt.args.namespace, tt.wantHTTPURL, tt.wantWSURL, api.DataIndex)
			if (err != nil) != tt.wantErr {
				t.Errorf("GetDataIndexEndpoints() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if gotDataIndexEndpoints != nil &&
				gotDataIndexEndpoints.HTTPRouteURI != tt.wantHTTPURL {
				t.Errorf("GetDataIndexEndpoints() gotHTTPURL = %v, want %v", gotDataIndexEndpoints.HTTPRouteURI, tt.wantHTTPURL)
			}
			if gotDataIndexEndpoints != nil &&
				gotDataIndexEndpoints.WSRouteURI != tt.wantWSURL {
				t.Errorf("GetDataIndexEndpoints() gotWSURL = %v, want %v", gotDataIndexEndpoints.WSRouteURI, tt.wantWSURL)
			}
		})
	}
}
