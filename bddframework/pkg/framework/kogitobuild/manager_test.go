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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/kiegroup/kogito-operator/version"
	"reflect"
	"testing"

	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	"github.com/stretchr/testify/assert"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func TestNewWhenBuildingFromRemoteSource(t *testing.T) {
	build := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{Name: "quarkus-example", Namespace: t.Name()},
		Spec: v1beta1.KogitoBuildSpec{
			Runtime: api.QuarkusRuntimeType,
			Type:    api.RemoteSourceBuildType,
			GitSource: v1beta1.GitSource{
				URI: "http://myrepo.com/namespace/project",
			},
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(build).OnOpenShift().Build()
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	deltaProcessor := &deltaProcessor{Context: context, build: build}
	manager := deltaProcessor.getBuildManager()
	assert.NotNil(t, manager)

	resources, err := manager.GetRequestedResources()
	assert.NoError(t, err)
	assert.Len(t, resources[reflect.TypeOf(buildv1.BuildConfig{})], 2)
	assert.Len(t, resources[reflect.TypeOf(imgv1.ImageStream{})], 2)

	bcBuilder := resources[reflect.TypeOf(buildv1.BuildConfig{})][0].(*buildv1.BuildConfig)
	assert.NotNil(t, bcBuilder)
	assert.Contains(t, bcBuilder.Spec.Strategy.SourceStrategy.From.Name, GetDefaultBuilderImage())
	assert.Contains(t, bcBuilder.Spec.Strategy.SourceStrategy.From.Name, infrastructure.GetKogitoImageVersion(version.Version))
	assert.Equal(t, buildv1.BuildSourceGit, bcBuilder.Spec.Source.Type)
	assert.Contains(t, bcBuilder.Name, builderSuffix)

	bcRuntime := resources[reflect.TypeOf(buildv1.BuildConfig{})][1].(*buildv1.BuildConfig)
	assert.NotNil(t, bcRuntime)
	assert.Contains(t, bcRuntime.Spec.Strategy.SourceStrategy.From.Name, GetDefaultRuntimeJVMImage())
	assert.Contains(t, bcRuntime.Spec.Strategy.SourceStrategy.From.Name, infrastructure.GetKogitoImageVersion(version.Version))
	assert.Contains(t, bcRuntime.Spec.Triggers[0].ImageChange.From.Name, bcBuilder.Name)
	assert.Equal(t, bcRuntime.Name, build.Name)

	isBuilder := resources[reflect.TypeOf(imgv1.ImageStream{})][0].(*imgv1.ImageStream)
	assert.NotNil(t, isBuilder)
	assert.Contains(t, bcBuilder.Spec.Output.To.Name, isBuilder.Name)

	isRuntime := resources[reflect.TypeOf(imgv1.ImageStream{})][1].(*imgv1.ImageStream)
	assert.NotNil(t, isBuilder)
	assert.Contains(t, bcRuntime.Spec.Output.To.Name, isRuntime.Name)
}

func TestNewWhenBuildingFromLocalSource(t *testing.T) {
	build := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "quarkus-example",
			Namespace: t.Name(),
		},
		Spec: v1beta1.KogitoBuildSpec{
			Type:    api.LocalSourceBuildType,
			Runtime: api.QuarkusRuntimeType,
		},
	}
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(build).Build()
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	deltaProcessor := &deltaProcessor{Context: context, build: build}
	manager := deltaProcessor.getBuildManager()
	assert.NotNil(t, manager)

	resources, err := manager.GetRequestedResources()
	assert.NoError(t, err)
	assert.Len(t, resources[reflect.TypeOf(buildv1.BuildConfig{})], 2)
	assert.Len(t, resources[reflect.TypeOf(imgv1.ImageStream{})], 2)

	bcBuilder := resources[reflect.TypeOf(buildv1.BuildConfig{})][0].(*buildv1.BuildConfig)
	assert.NotNil(t, bcBuilder)
	assert.Contains(t, bcBuilder.Spec.Strategy.SourceStrategy.From.Name, GetDefaultBuilderImage())
	assert.Contains(t, bcBuilder.Spec.Strategy.SourceStrategy.From.Name, infrastructure.GetKogitoImageVersion(version.Version))
	assert.Equal(t, buildv1.BuildSourceBinary, bcBuilder.Spec.Source.Type)
	assert.Contains(t, bcBuilder.Name, builderSuffix)

	bcRuntime := resources[reflect.TypeOf(buildv1.BuildConfig{})][1].(*buildv1.BuildConfig)
	assert.NotNil(t, bcRuntime)
	assert.Contains(t, bcRuntime.Spec.Strategy.SourceStrategy.From.Name, GetDefaultRuntimeJVMImage())
	assert.Contains(t, bcRuntime.Spec.Strategy.SourceStrategy.From.Name, infrastructure.GetKogitoImageVersion(version.Version))
	assert.Contains(t, bcRuntime.Spec.Triggers[0].ImageChange.From.Name, bcBuilder.Name)
	assert.Equal(t, bcRuntime.Name, build.Name)

	isBuilder := resources[reflect.TypeOf(imgv1.ImageStream{})][0].(*imgv1.ImageStream)
	assert.NotNil(t, isBuilder)
	assert.Contains(t, bcBuilder.Spec.Output.To.Name, isBuilder.Name)

	isRuntime := resources[reflect.TypeOf(imgv1.ImageStream{})][1].(*imgv1.ImageStream)
	assert.NotNil(t, isBuilder)
	assert.Contains(t, bcRuntime.Spec.Output.To.Name, isRuntime.Name)
}

func TestNewWhenBuildingFromBinary(t *testing.T) {
	build := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "quarkus-example",
			Namespace: t.Name(),
		},
		Spec: v1beta1.KogitoBuildSpec{
			Type:    api.BinaryBuildType,
			Runtime: api.QuarkusRuntimeType,
		},
	}
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(build).Build()

	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	deltaProcessor := &deltaProcessor{Context: context, build: build}
	manager := deltaProcessor.getBuildManager()
	assert.NotNil(t, manager)

	resources, err := manager.GetRequestedResources()
	assert.NoError(t, err)
	assert.Len(t, resources[reflect.TypeOf(buildv1.BuildConfig{})], 1)
	assert.Len(t, resources[reflect.TypeOf(imgv1.ImageStream{})], 1)

	bcRuntime := resources[reflect.TypeOf(buildv1.BuildConfig{})][0].(*buildv1.BuildConfig)
	assert.NotNil(t, bcRuntime)
	assert.Equal(t, buildv1.BuildSourceBinary, bcRuntime.Spec.Source.Type)
	assert.Contains(t, bcRuntime.Spec.Strategy.SourceStrategy.From.Name, GetDefaultRuntimeJVMImage())
	assert.Contains(t, bcRuntime.Spec.Strategy.SourceStrategy.From.Name, infrastructure.GetKogitoImageVersion(version.Version))
	assert.Equal(t, bcRuntime.Name, build.Name)

	isRuntime := resources[reflect.TypeOf(imgv1.ImageStream{})][0].(*imgv1.ImageStream)
	assert.NotNil(t, isRuntime)
	assert.Contains(t, bcRuntime.Spec.Output.To.Name, isRuntime.Name)

}

func TestNewWhenSanityCheckComplainAboutType(t *testing.T) {
	build := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "quarkus-example",
			Namespace: t.Name(),
		},
		Spec: v1beta1.KogitoBuildSpec{
			Runtime: api.QuarkusRuntimeType,
		},
	}
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(build).Build()
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	manager, err := NewDeltaProcessor(context, build)
	assert.Error(t, err)
	assert.Nil(t, manager)
}

func TestNewWhenSanityCheckComplainAboutGit(t *testing.T) {
	build := &v1beta1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "quarkus-example",
			Namespace: t.Name(),
		},
		Spec: v1beta1.KogitoBuildSpec{
			Type:    api.RemoteSourceBuildType,
			Runtime: api.QuarkusRuntimeType,
		},
	}
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(build).Build()
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	manager, err := NewDeltaProcessor(context, build)
	assert.Error(t, err)
	assert.Nil(t, manager)
}
