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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	buildv1 "github.com/openshift/api/build/v1"
	"github.com/stretchr/testify/assert"
	v12 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func Test_decoratorForSourceBuilder_enableIncrementalBuild_Test(t *testing.T) {
	kogitoBuild := &v1beta1.KogitoBuild{
		ObjectMeta: v12.ObjectMeta{Name: "test", Namespace: "test"},
		Spec: v1beta1.KogitoBuildSpec{
			DisableIncremental: false,
			Type:               "LocalSource",
		},
	}
	bc := &buildv1.BuildConfig{
		ObjectMeta: v12.ObjectMeta{
			Namespace: kogitoBuild.Namespace,
		},
		Spec: buildv1.BuildConfigSpec{
			CommonSpec: buildv1.CommonSpec{Resources: kogitoBuild.Spec.Resources},
		},
	}
	cli := test.NewFakeClientBuilder().Build()
	context := BuildContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
	}
	decoratorHandler := NewDecoratorHandler(context)
	decoratorHandler.decoratorForSourceBuilder()(kogitoBuild, bc)

	assert.Equal(t, true, *bc.Spec.CommonSpec.Strategy.SourceStrategy.Incremental)
}
func Test_decoratorForSourceBuilder_disableIncrementalBuild_Test(t *testing.T) {
	kogitoBuild := &v1beta1.KogitoBuild{
		ObjectMeta: v12.ObjectMeta{Name: "test", Namespace: "test"},
		Spec: v1beta1.KogitoBuildSpec{
			DisableIncremental: true,
			Type:               "LocalSource",
		},
	}
	bc := &buildv1.BuildConfig{
		ObjectMeta: v12.ObjectMeta{
			Namespace: kogitoBuild.Namespace,
		},
		Spec: buildv1.BuildConfigSpec{
			CommonSpec: buildv1.CommonSpec{Resources: kogitoBuild.Spec.Resources},
		},
	}
	cli := test.NewFakeClientBuilder().Build()
	context := BuildContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
	}
	decoratorHandler := NewDecoratorHandler(context)
	decoratorHandler.decoratorForSourceBuilder()(kogitoBuild, bc)

	assert.Equal(t, false, *bc.Spec.CommonSpec.Strategy.SourceStrategy.Incremental)
}

func Test_decoratorForRemoteSourceBuilder_specSource(t *testing.T) {
	kogitoBuild := &v1beta1.KogitoBuild{
		Spec: v1beta1.KogitoBuildSpec{
			GitSource: v1beta1.GitSource{
				URI:        "host:port",
				Reference:  "my_branch",
				ContextDir: "/mypath/",
			},
		},
	}
	bc := &buildv1.BuildConfig{}
	cli := test.NewFakeClientBuilder().Build()
	context := BuildContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
	}
	decoratorHandler := NewDecoratorHandler(context)
	decoratorHandler.decoratorForRemoteSourceBuilder()(kogitoBuild, bc)

	assert.Equal(t, buildv1.BuildSourceGit, bc.Spec.Source.Type)
	assert.Equal(t, "/mypath", bc.Spec.Source.ContextDir)
	assert.Equal(t, "host:port", bc.Spec.Source.Git.URI)
	assert.Equal(t, "my_branch", bc.Spec.Source.Git.Ref)
}

func Test_decoratorForRemoteSourceBuilder_githubWebHook(t *testing.T) {
	kogitoBuild := &v1beta1.KogitoBuild{
		Spec: v1beta1.KogitoBuildSpec{
			WebHooks: []v1beta1.WebHookSecret{
				{
					Type:   api.GitHubWebHook,
					Secret: "github_secret",
				},
			},
		},
	}
	bc := &buildv1.BuildConfig{}
	cli := test.NewFakeClientBuilder().Build()
	context := BuildContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
	}
	decoratorHandler := NewDecoratorHandler(context)
	decoratorHandler.decoratorForRemoteSourceBuilder()(kogitoBuild, bc)

	assert.Equal(t, 1, len(bc.Spec.Triggers))
	assert.NotNil(t, bc.Spec.Triggers[0].GitHubWebHook)
	assert.False(t, bc.Spec.Triggers[0].GitHubWebHook.AllowEnv)
	assert.Equal(t, "github_secret", bc.Spec.Triggers[0].GitHubWebHook.SecretReference.Name)
}

func Test_decoratorForRemoteSourceBuilder_genericWebHook(t *testing.T) {
	kogitoBuild := &v1beta1.KogitoBuild{
		Spec: v1beta1.KogitoBuildSpec{
			WebHooks: []v1beta1.WebHookSecret{
				{
					Type:   api.GenericWebHook,
					Secret: "generic_secret",
				},
			},
		},
	}
	bc := &buildv1.BuildConfig{}
	cli := test.NewFakeClientBuilder().Build()
	context := BuildContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
	}
	decoratorHandler := NewDecoratorHandler(context)
	decoratorHandler.decoratorForRemoteSourceBuilder()(kogitoBuild, bc)

	assert.Equal(t, 1, len(bc.Spec.Triggers))
	assert.NotNil(t, bc.Spec.Triggers[0].GenericWebHook)
	assert.True(t, bc.Spec.Triggers[0].GenericWebHook.AllowEnv)
	assert.Equal(t, "generic_secret", bc.Spec.Triggers[0].GenericWebHook.SecretReference.Name)
}

func Test_decoratorForCustomLabels(t *testing.T) {
	kogitoBuild := &v1beta1.KogitoBuild{
		Spec: v1beta1.KogitoBuildSpec{
			WebHooks: []v1beta1.WebHookSecret{
				{
					Type:   api.GenericWebHook,
					Secret: "generic_secret",
				},
			},
		},
	}
	bc := &buildv1.BuildConfig{
		ObjectMeta: v12.ObjectMeta{
			Labels: map[string]string{},
		},
	}
	cli := test.NewFakeClientBuilder().Build()
	context := BuildContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
			Labels: map[string]string{
				"key1": "value1",
			},
		},
	}

	decoratorHandler := NewDecoratorHandler(context)
	decoratorHandler.decoratorForCustomLabels()(kogitoBuild, bc)
	assert.Equal(t, 1, len(bc.Labels))
	assert.Equal(t, "value1", bc.Labels["key1"])
}
