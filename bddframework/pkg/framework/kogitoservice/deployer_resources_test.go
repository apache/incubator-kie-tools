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

package kogitoservice

import (
	"reflect"
	"testing"

	"github.com/RHsyseng/operator-utils/pkg/resource"
	api "github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/kiegroup/kogito-operator/version"
	routev1 "github.com/openshift/api/route/v1"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/intstr"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
)

func Test_serviceDeployer_createRequiredResources_OnOCPImageStreamCreated(t *testing.T) {
	jobsService := test.CreateFakeJobsService(t.Name())
	is, tag := test.CreateFakeImageStreams("kogito-jobs-service", jobsService.GetNamespace(), infrastructure.GetKogitoImageVersion(version.Version))
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(is).AddImageObjects(tag).Build()
	deployer := newTestSupServiceDeployer(cli, jobsService, "kogito-jobs-service")
	imageName := "quay.io/kiegroup/kogito-jobs-service"
	resources, err := deployer.createRequiredResources(imageName)
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)
	// we have the Image Stream, so other resources should have been created
	assert.True(t, len(resources) > 1)
}

func Test_serviceDeployer_createRequiredResources_OnOCPNoImageStreamCreated(t *testing.T) {
	jobsService := test.CreateFakeJobsService(t.Name())
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	deployer := newTestSupServiceDeployer(cli, jobsService, "kogito-jobs-service")
	imageName := "quay.io/kiegroup/kogito-jobs-service"
	resources, err := deployer.createRequiredResources(imageName)
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)
	// we don't have the Image Stream, so other resources should not have been created other than ConfigMap
	assert.True(t, len(resources) == 3)
	assert.Equal(t, resources[reflect.TypeOf(appsv1.Deployment{})][0].GetName(), "jobs-service")
	assert.Equal(t, resources[reflect.TypeOf(corev1.Service{})][0].GetName(), "jobs-service")
	assert.Equal(t, resources[reflect.TypeOf(routev1.Route{})][0].GetName(), "jobs-service")
}

func Test_serviceDeployer_createServiceComparator(t *testing.T) {
	type args struct {
		deployed  resource.KubernetesResource
		requested resource.KubernetesResource
	}
	familyPolicy := corev1.IPFamilyPolicyRequireDualStack
	port1 := make([]corev1.ServicePort, 1)
	port1[0] = corev1.ServicePort{
		Name:       "http",
		Protocol:   "TCP",
		Port:       80,
		TargetPort: intstr.FromInt(int(8080)),
	}

	port2 := make([]corev1.ServicePort, 1)
	port2[0] = corev1.ServicePort{
		Name:       "http",
		Protocol:   "TCP",
		Port:       90,
		TargetPort: intstr.FromInt(int(8080)),
	}
	tests := []struct {
		name  string
		args  args
		want  reflect.Type
		want1 bool
	}{
		{
			"Equals",
			args{
				deployed: &corev1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &corev1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
			},
			reflect.TypeOf(corev1.Service{}),
			true,
		},
		{
			"NotEquals",
			args{
				deployed: &corev1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &corev1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test1",
						},
					},
				},
			},
			reflect.TypeOf(corev1.Service{}),
			false,
		},
		{
			"DifferentClusterIPs",
			args{
				deployed: &corev1.Service{
					Spec: corev1.ServiceSpec{
						ClusterIPs: []string{"10.217.5.142"},
					},
				},
				requested: &corev1.Service{},
			},
			reflect.TypeOf(corev1.Service{}),
			true,
		},
		{
			"DifferentClusterIP",
			args{
				deployed: &corev1.Service{
					Spec: corev1.ServiceSpec{
						ClusterIP: "10.217.5.142",
					},
				},
				requested: &corev1.Service{},
			},
			reflect.TypeOf(corev1.Service{}),
			true,
		},
		{
			"DifferentIPFamilies",
			args{
				deployed: &corev1.Service{
					Spec: corev1.ServiceSpec{
						IPFamilies: []corev1.IPFamily{"IPv4"},
					},
				},
				requested: &corev1.Service{},
			},
			reflect.TypeOf(corev1.Service{}),
			true,
		},
		{
			"DifferentIPFamilyPolicy",
			args{
				deployed: &corev1.Service{
					Spec: corev1.ServiceSpec{
						IPFamilyPolicy: &familyPolicy,
					},
				},
				requested: &corev1.Service{},
			},
			reflect.TypeOf(corev1.Service{}),
			true,
		},
		{
			"NotEqualPorts",
			args{
				deployed: &corev1.Service{
					Spec: corev1.ServiceSpec{
						Ports: port1,
					},
				},
				requested: &corev1.Service{
					Spec: corev1.ServiceSpec{
						Ports: port2,
					},
				},
			},
			reflect.TypeOf(corev1.Service{}),
			false,
		},
		{
			"NotEqualSelector",
			args{
				deployed: &corev1.Service{
					Spec: corev1.ServiceSpec{
						Selector: map[string]string{"app": "test"},
					},
				},
				requested: &corev1.Service{
					Spec: corev1.ServiceSpec{
						Selector: map[string]string{"app": "test1"},
					},
				},
			},
			reflect.TypeOf(corev1.Service{}),
			false,
		},
	}
	jobsService := test.CreateFakeJobsService(t.Name())
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	deployer := newTestSupServiceDeployer(cli, jobsService, "kogito-jobs-service")
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, got1 :=
				framework.NewComparatorBuilder().
					WithType(tt.want).
					WithCustomComparator(deployer.CreateServiceComparator()).
					Build()
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("createServiceComparator() got = %v, want %v", got, tt.want)
			}
			if !reflect.DeepEqual(got1(tt.args.deployed, tt.args.requested), tt.want1) {
				t.Errorf("createServiceComparator() got1 = %v, want %v", got1(tt.args.deployed, tt.args.requested), tt.want1)
			}
		})
	}
}

func newTestSupServiceDeployer(cli *client.Client, instance api.KogitoService, imageName string) serviceDeployer {
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	return serviceDeployer{Context: context, instance: instance,
		definition: ServiceDefinition{
			DefaultImageName: imageName,
			Request: reconcile.Request{
				NamespacedName: types.NamespacedName{
					Name:      instance.GetName(),
					Namespace: instance.GetNamespace(),
				},
			},
		},
	}
}
