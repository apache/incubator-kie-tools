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

package framework

import (
	"reflect"
	"testing"

	"github.com/RHsyseng/operator-utils/pkg/resource"
	appsv1 "github.com/openshift/api/apps/v1"
	buildv1 "github.com/openshift/api/build/v1"
	routev1 "github.com/openshift/api/route/v1"
	apps "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
)

func Test_containAllLabels(t *testing.T) {
	type args struct {
		deployed  resource.KubernetesResource
		requested resource.KubernetesResource
	}
	tests := []struct {
		name string
		args args
		want bool
	}{
		{
			"Contains",
			args{
				deployed: &v1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &v1.Pod{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
			},
			true,
		},
		{
			"NotContains",
			args{
				deployed: &v1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test1",
						},
					},
				},
				requested: &v1.Pod{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
			},
			false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := containAllLabels(tt.args.deployed, tt.args.requested); got != tt.want {
				t.Errorf("containAllLabels() = %v, want %v", got, tt.want)
			}
		})
	}
}

func Test_CreateBuildConfigComparator(t *testing.T) {
	type args struct {
		deployed  resource.KubernetesResource
		requested resource.KubernetesResource
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
				deployed: &buildv1.BuildConfig{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &buildv1.BuildConfig{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
			},
			reflect.TypeOf(buildv1.BuildConfig{}),
			true,
		},
		{
			"NotEquals",
			args{
				deployed: &buildv1.BuildConfig{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &buildv1.BuildConfig{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test1",
						},
					},
				},
			},
			reflect.TypeOf(buildv1.BuildConfig{}),
			false,
		},
		{
			"With empty triggers KOGITO-2872",
			args{
				deployed: &buildv1.BuildConfig{
					Spec: buildv1.BuildConfigSpec{Triggers: nil},
				},
				requested: &buildv1.BuildConfig{
					Spec: buildv1.BuildConfigSpec{Triggers: []buildv1.BuildTriggerPolicy{}},
				},
			},
			reflect.TypeOf(buildv1.BuildConfig{}),
			true,
		},
		{
			"With empty triggers reference",
			args{
				deployed: &buildv1.BuildConfig{
					Spec: buildv1.BuildConfigSpec{Triggers: []buildv1.BuildTriggerPolicy{}},
				},
				requested: &buildv1.BuildConfig{
					Spec: buildv1.BuildConfigSpec{Triggers: []buildv1.BuildTriggerPolicy{}},
				},
			},
			reflect.TypeOf(buildv1.BuildConfig{}),
			true,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, got1 :=
				NewComparatorBuilder().
					WithType(tt.want).
					UseDefaultComparator().
					WithCustomComparator(CreateBuildConfigComparator()).
					Build()
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("createBuildConfigComparator() got = %v, want %v", got, tt.want)
			}
			if !reflect.DeepEqual(got1(tt.args.deployed, tt.args.requested), tt.want1) {
				t.Errorf("createBuildConfigComparator() got1 = %v, want %v", got1(tt.args.deployed, tt.args.requested), tt.want1)
			}
		})
	}
}

func Test_CreateDeploymentConfigComparator(t *testing.T) {
	type args struct {
		deployed  resource.KubernetesResource
		requested resource.KubernetesResource
	}
	tests := []struct {
		name  string
		args  args
		want  reflect.Type
		want1 bool
	}{
		{
			"Equals Env",
			args{
				deployed: &appsv1.DeploymentConfig{
					Spec: appsv1.DeploymentConfigSpec{
						Strategy: appsv1.DeploymentStrategy{
							Type: appsv1.DeploymentStrategyTypeRolling,
							RollingParams: &appsv1.RollingDeploymentStrategyParams{
								MaxUnavailable: &intstr.IntOrString{StrVal: "30%"},
							},
						},
						Template: &v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Env: []v1.EnvVar{
											{Name: "app", Value: "test"},
											{Name: "service", Value: "test"},
										},
									},
								},
							},
						},
					},
				},
				requested: &appsv1.DeploymentConfig{
					Spec: appsv1.DeploymentConfigSpec{
						Strategy: appsv1.DeploymentStrategy{
							Type: appsv1.DeploymentStrategyTypeRolling,
						},
						Template: &v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Env: []v1.EnvVar{
											{Name: "app", Value: "test"},
											{Name: "service", Value: "test"},
										},
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(appsv1.DeploymentConfig{}),
			true,
		},
		{
			"NotEquals Env",
			args{
				deployed: &appsv1.DeploymentConfig{
					Spec: appsv1.DeploymentConfigSpec{
						Strategy: appsv1.DeploymentStrategy{
							Type: appsv1.DeploymentStrategyTypeRolling,
							RollingParams: &appsv1.RollingDeploymentStrategyParams{
								MaxUnavailable: &intstr.IntOrString{StrVal: "30%"},
							},
						},
						Template: &v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Env: []v1.EnvVar{
											{Name: "app", Value: "test"},
											{Name: "service", Value: "test"},
										},
									},
								},
							},
						},
					},
				},
				requested: &appsv1.DeploymentConfig{
					Spec: appsv1.DeploymentConfigSpec{
						Strategy: appsv1.DeploymentStrategy{
							Type: appsv1.DeploymentStrategyTypeRolling,
						},
						Template: &v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Env: []v1.EnvVar{
											{Name: "app", Value: "test"},
											{Name: "service", Value: "test1"},
										},
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(appsv1.DeploymentConfig{}),
			false,
		},
		{
			"Equals Volumes",
			args{
				deployed: &appsv1.DeploymentConfig{
					Spec: appsv1.DeploymentConfigSpec{
						Strategy: appsv1.DeploymentStrategy{
							Type: appsv1.DeploymentStrategyTypeRolling,
							RollingParams: &appsv1.RollingDeploymentStrategyParams{
								MaxUnavailable: &intstr.IntOrString{StrVal: "30%"},
							},
						},
						Template: &v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										VolumeMounts: []v1.VolumeMount{
											{
												Name:      "volume1",
												MountPath: "/mnt/dev/vol1",
											},
											{
												Name:      "volume2",
												MountPath: "/mnt/dev/vol2",
											},
										},
									},
								},
							},
						},
					},
				},
				requested: &appsv1.DeploymentConfig{
					Spec: appsv1.DeploymentConfigSpec{
						Strategy: appsv1.DeploymentStrategy{
							Type: appsv1.DeploymentStrategyTypeRolling,
						},
						Template: &v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										// notice the array order, that matters
										VolumeMounts: []v1.VolumeMount{
											{
												Name:      "volume2",
												MountPath: "/mnt/dev/vol2",
											},
											{
												Name:      "volume1",
												MountPath: "/mnt/dev/vol1",
											},
										},
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(appsv1.DeploymentConfig{}),
			true,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, got1 :=
				NewComparatorBuilder().
					WithType(tt.want).
					UseDefaultComparator().
					WithCustomComparator(CreateDeploymentConfigComparator()).
					Build()
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("createDeploymentConfigComparator() got = %v, want %v", got, tt.want)
			}
			if !reflect.DeepEqual(got1(tt.args.deployed, tt.args.requested), tt.want1) {
				t.Errorf("createDeploymentConfigComparator() got1 = %v, want %v", got1(tt.args.deployed, tt.args.requested), tt.want1)
			}
		})
	}
}

func Test_CreateDeploymentComparator(t *testing.T) {
	type args struct {
		deployed  resource.KubernetesResource
		requested resource.KubernetesResource
	}
	tests := []struct {
		name  string
		args  args
		want  reflect.Type
		want1 bool
	}{
		{
			"Equals Env",
			args{
				deployed: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Env: []v1.EnvVar{
											{Name: "app", Value: "test"},
											{Name: "service", Value: "test"},
										},
									},
								},
							},
						},
					},
				},
				requested: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Env: []v1.EnvVar{
											{Name: "app", Value: "test"},
											{Name: "service", Value: "test"},
										},
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(apps.Deployment{}),
			true,
		},
		{
			"NotEquals Env",
			args{
				deployed: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Env: []v1.EnvVar{
											{Name: "app", Value: "test"},
											{Name: "service", Value: "test"},
										},
									},
								},
							},
						},
					},
				},
				requested: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Env: []v1.EnvVar{
											{Name: "app", Value: "test"},
											{Name: "service", Value: "test1"},
										},
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(apps.Deployment{}),
			false,
		},
		{
			"Equals Volumes",
			args{
				deployed: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Volumes: []v1.Volume{
									{Name: "volume1"},
									{Name: "volume2"},
								},
								Containers: []v1.Container{
									{
										VolumeMounts: []v1.VolumeMount{
											{
												Name:      "volume1",
												MountPath: "/mnt/dev/vol1",
											},
											{
												Name:      "volume2",
												MountPath: "/mnt/dev/vol2",
											},
										},
									},
								},
							},
						},
					},
				},
				requested: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Volumes: []v1.Volume{
									{Name: "volume2"},
									{Name: "volume1"},
								},
								Containers: []v1.Container{
									{
										// notice the array order, that matters
										VolumeMounts: []v1.VolumeMount{
											{
												Name:      "volume2",
												MountPath: "/mnt/dev/vol2",
											},
											{
												Name:      "volume1",
												MountPath: "/mnt/dev/vol1",
											},
										},
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(apps.Deployment{}),
			true,
		},
		{
			"Equals Volumes Mount Path",
			args{
				deployed: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Volumes: []v1.Volume{
									{Name: "app-prop-config"},
									{Name: "process-quarkus-example-protobuf-files"},
								},
								Containers: []v1.Container{
									{
										VolumeMounts: []v1.VolumeMount{
											{
												Name:      "app-prop-config",
												MountPath: "/home/kogito/config",
											},
											{
												Name:      "process-quarkus-example-protobuf-files",
												MountPath: "/home/kogito/data/protobufs/process-quarkus-example/demo.orders.proto",
											},
											{
												Name:      "process-quarkus-example-protobuf-files",
												MountPath: "/home/kogito/data/protobufs/process-quarkus-example/persons.proto",
											},
										},
									},
								},
							},
						},
					},
				},
				requested: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Volumes: []v1.Volume{
									{Name: "app-prop-config"},
									{Name: "process-quarkus-example-protobuf-files"},
								},
								Containers: []v1.Container{
									{
										// notice the array order, that matters. using logs from KOGITO-2797 bug report
										VolumeMounts: []v1.VolumeMount{
											{
												Name:      "app-prop-config",
												MountPath: "/home/kogito/config",
											},
											{
												Name:      "process-quarkus-example-protobuf-files",
												MountPath: "/home/kogito/data/protobufs/process-quarkus-example/persons.proto",
											},
											{
												Name:      "process-quarkus-example-protobuf-files",
												MountPath: "/home/kogito/data/protobufs/process-quarkus-example/demo.orders.proto",
											},
										},
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(apps.Deployment{}),
			true,
		},
		{
			"Knative injected an env var",
			args{
				deployed: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Name: "service",
										Env: []v1.EnvVar{
											CreateEnvVar("K_SINK", "http://endpoint/"),
										},
									},
								},
							},
						},
					},
				},
				requested: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Name: "service",
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(apps.Deployment{}),
			true,
		},
		{
			"Knative injected only on request?",
			args{
				deployed: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Name: "service",
									},
								},
							},
						},
					},
				},
				requested: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Name: "service",
										Env: []v1.EnvVar{
											CreateEnvVar("K_SINK", "http://endpoint/"),
										},
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(apps.Deployment{}),
			false,
		},
		{
			"Knative with multiple containers",
			args{
				deployed: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Name: "service",
										Env: []v1.EnvVar{
											CreateEnvVar("K_SINK", "http://endpoint/"),
										},
									},
									{
										Name: "service2",
									},
								},
							},
						},
					},
				},
				requested: &apps.Deployment{
					Spec: apps.DeploymentSpec{
						Template: v1.PodTemplateSpec{
							Spec: v1.PodSpec{
								Containers: []v1.Container{
									{
										Name: "service",
									},
								},
							},
						},
					},
				},
			},
			reflect.TypeOf(apps.Deployment{}),
			false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, got1 :=
				NewComparatorBuilder().
					WithType(tt.want).
					UseDefaultComparator().
					WithCustomComparator(CreateDeploymentComparator()).
					Build()
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("createDeploymentComparator() got = %v, want %v", got, tt.want)
			}
			if !reflect.DeepEqual(got1(tt.args.deployed, tt.args.requested), tt.want1) {
				t.Errorf("createDeploymentComparator() got1 = %v, want %v", got1(tt.args.deployed, tt.args.requested), tt.want1)
			}
		})
	}
}

func Test_CreateRouteComparator(t *testing.T) {
	type args struct {
		deployed  resource.KubernetesResource
		requested resource.KubernetesResource
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
				deployed: &routev1.Route{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &routev1.Route{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
			},
			reflect.TypeOf(routev1.Route{}),
			true,
		},
		{
			"NotEquals",
			args{
				deployed: &routev1.Route{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &routev1.Route{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test1",
						},
					},
				},
			},
			reflect.TypeOf(routev1.Route{}),
			false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, got1 :=
				NewComparatorBuilder().
					WithType(tt.want).
					UseDefaultComparator().
					WithCustomComparator(CreateRouteComparator()).
					Build()
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("createRouteComparator() got = %v, want %v", got, tt.want)
			}
			if !reflect.DeepEqual(got1(tt.args.deployed, tt.args.requested), tt.want1) {
				t.Errorf("createRouteComparator() got1 = %v, want %v", got1(tt.args.deployed, tt.args.requested), tt.want1)
			}
		})
	}
}

func Test_CreateServiceComparator(t *testing.T) {
	type args struct {
		deployed  resource.KubernetesResource
		requested resource.KubernetesResource
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
				deployed: &v1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &v1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
			},
			reflect.TypeOf(v1.Service{}),
			true,
		},
		{
			"NotEquals",
			args{
				deployed: &v1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test",
						},
					},
				},
				requested: &v1.Service{
					ObjectMeta: metav1.ObjectMeta{
						Labels: map[string]string{
							"app":     "test",
							"service": "test1",
						},
					},
				},
			},
			reflect.TypeOf(v1.Service{}),
			false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, got1 :=
				NewComparatorBuilder().
					WithType(tt.want).
					UseDefaultComparator().
					WithCustomComparator(CreateServiceComparator()).
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
