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

package kogitobuild

import (
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	"testing"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func Test_resolveSourceStrategyImageNameForBuilds(t *testing.T) {
	buildQuarkusNonNative := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildQuarkusNonNative", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.QuarkusRuntimeType, Native: false},
	}
	buildQuarkusNative := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildQuarkusNative", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.QuarkusRuntimeType, Native: true},
	}
	buildSpringBoot := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildSpringBoot", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.SpringBootRuntimeType},
	}
	buildQuarkusCustom := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildQuarkusCustom", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.QuarkusRuntimeType, RuntimeImage: "my-image:1.0"},
	}
	buildSpringBootCustom := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildSpringBootCustom", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.SpringBootRuntimeType, BuildImage: "my-image:1.0"},
	}
	tag := ":" + infrastructure.GetKogitoImageVersion()
	type args struct {
		build        *v1beta1.KogitoBuild
		builderImage bool
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{"Quarkus Non Native Builder", args{buildQuarkusNonNative, true}, infrastructure.KogitoBuilderImage + tag},
		{"Quarkus Non Native Base", args{buildQuarkusNonNative, false}, infrastructure.KogitoRuntimeJVM + tag},
		{"Quarkus Native Builder", args{buildQuarkusNative, true}, infrastructure.KogitoBuilderImage + tag},
		{"Quarkus Native Base", args{buildQuarkusNative, false}, infrastructure.KogitoRuntimeNative + tag},
		{"SpringBoot Builder", args{buildSpringBoot, true}, infrastructure.KogitoBuilderImage + tag},
		{"SpringBoot Base", args{buildSpringBoot, false}, infrastructure.KogitoRuntimeJVM + tag},
		{"SpringBoot Custom Builder", args{buildSpringBootCustom, true}, "my-image:1.0"},
		{"Quarkus Custom Base", args{buildQuarkusCustom, false}, "my-image:1.0"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := resolveKogitoImageNameTag(tt.args.build, tt.args.builderImage); got != tt.want {
				t.Errorf("resolveKogitoImageNameTag() = %v, want %v", got, tt.want)
			}
		})
	}
}

func Test_resolveKogitoImageStreamName(t *testing.T) {
	buildQuarkusNonNative := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildQuarkusNonNative", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.QuarkusRuntimeType, Native: false},
	}
	buildQuarkusNative := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildQuarkusNative", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.QuarkusRuntimeType, Native: true},
	}
	buildSpringBoot := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildSpringBoot", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.SpringBootRuntimeType},
	}
	buildQuarkusCustom := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildQuarkusCustom", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.QuarkusRuntimeType, RuntimeImage: "my-image"},
	}
	buildSpringBootCustom := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "buildSpringBootCustom", Namespace: t.Name()},
		Spec:       v1beta1.KogitoBuildSpec{Runtime: api.SpringBootRuntimeType, BuildImage: "my-image"},
	}
	type args struct {
		build     *v1beta1.KogitoBuild
		isBuilder bool
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{"Quarkus Non Native Builder", args{buildQuarkusNonNative, true}, infrastructure.KogitoBuilderImage},
		{"Quarkus Non Native Base", args{buildQuarkusNonNative, false}, infrastructure.KogitoRuntimeJVM},
		{"Quarkus Native Builder", args{buildQuarkusNative, true}, infrastructure.KogitoBuilderImage},
		{"Quarkus Native Base", args{buildQuarkusNative, false}, infrastructure.KogitoRuntimeNative},
		{"SpringBoot Builder", args{buildSpringBoot, true}, infrastructure.KogitoBuilderImage},
		{"SpringBoot Base", args{buildSpringBoot, false}, infrastructure.KogitoRuntimeJVM},
		{"SpringBoot Custom Builder", args{buildSpringBootCustom, true}, "custom-my-image"},
		{"Quarkus Custom Base", args{buildQuarkusCustom, false}, "custom-my-image"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := resolveKogitoImageStreamName(tt.args.build, tt.args.isBuilder); got != tt.want {
				t.Errorf("resolveKogitoImageStreamName() = %v, want %v", got, tt.want)
			}
		})
	}
}
