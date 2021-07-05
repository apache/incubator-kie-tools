// Copyright 2021 Red Hat, Inc. and/or its affiliates
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
	"github.com/kiegroup/kogito-operator/core/framework"
	buildv1 "github.com/openshift/api/build/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	// LabelKeyBuildType identifies the instance build type
	LabelKeyBuildType = "buildType"
)

// BuildConfigHandler ...
type BuildConfigHandler interface {
	newBuildConfig(build api.KogitoBuildInterface, decorators ...decorator) buildv1.BuildConfig
}

type buildConfigHandler struct {
	BuildContext
}

// NewBuildConfigHandler ...
func NewBuildConfigHandler(context BuildContext) BuildConfigHandler {
	return &buildConfigHandler{
		context,
	}
}

// newBuildConfig creates a new reference for the very basic default OpenShift BuildConfig reference to build Kogito Services.
// Pass the required decorator(s) to create the BuildConfig for a particular use case
func (b *buildConfigHandler) newBuildConfig(build api.KogitoBuildInterface, decorators ...decorator) buildv1.BuildConfig {
	app := build.GetSpec().GetTargetKogitoRuntime()
	if len(app) == 0 {
		app = build.GetName()
	}

	labels := map[string]string{
		LabelKeyBuildType:     string(build.GetSpec().GetType()),
		framework.LabelAppKey: app,
	}

	bc := buildv1.BuildConfig{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: build.GetNamespace(),
			Labels:    labels,
		},
		Spec: buildv1.BuildConfigSpec{
			RunPolicy:  buildv1.BuildRunPolicySerial,
			CommonSpec: buildv1.CommonSpec{Resources: build.GetSpec().GetResources()},
		},
	}
	for _, decorate := range decorators {
		decorate(build, &bc)
	}
	return bc
}
