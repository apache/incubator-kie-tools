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
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/framework"
	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	"reflect"
)

type sourceManager struct {
	manager
}

func (m *sourceManager) GetRequestedResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	decoratorHandler := NewDecoratorHandler(m.BuildContext)
	buildConfigHandler := NewBuildConfigHandler(m.BuildContext)
	builderBC := buildConfigHandler.newBuildConfig(m.build, decoratorHandler.decoratorForSourceBuilder(), m.getBuilderDecorator(), decoratorHandler.decoratorForCustomLabels())
	runtimeBC := buildConfigHandler.newBuildConfig(m.build, decoratorHandler.decoratorForRuntimeBuilder(), decoratorHandler.decoratorForSourceRuntimeBuilder(), decoratorHandler.decoratorForCustomLabels())
	builderIS := newOutputImageStreamForBuilder(&builderBC)
	runtimeIS, err := newOutputImageStreamForRuntime(m.Context, &runtimeBC, m.build)
	if err != nil {
		return resources, err
	}
	if err := framework.SetOwner(m.build, m.Scheme, &builderBC, &runtimeBC, &builderIS); err != nil {
		return resources, err
	}
	// the runtime ImageStream is a shared resource among other KogitoBuild instances and KogitoRuntime, we can't own it
	if err := framework.AddOwnerReference(m.build, m.Scheme, runtimeIS); err != nil {
		return resources, err
	}
	resources[reflect.TypeOf(imgv1.ImageStream{})] = []resource.KubernetesResource{&builderIS, runtimeIS}
	resources[reflect.TypeOf(buildv1.BuildConfig{})] = []resource.KubernetesResource{&builderBC, &runtimeBC}
	return resources, nil
}

func (m *sourceManager) getBuilderDecorator() decorator {
	decoratorHandler := NewDecoratorHandler(m.BuildContext)
	if api.LocalSourceBuildType == m.build.GetSpec().GetType() {
		return decoratorHandler.decoratorForLocalSourceBuilder()
	}
	return decoratorHandler.decoratorForRemoteSourceBuilder()
}
