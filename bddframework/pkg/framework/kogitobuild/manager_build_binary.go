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
	"github.com/kiegroup/kogito-operator/core/framework"
	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	"reflect"
)

type binaryManager struct {
	manager
}

func (m *binaryManager) GetRequestedResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	decoratorHandler := NewDecoratorHandler(m.Context)
	buildConfig := newBuildConfig(m.build, decoratorHandler.decoratorForRuntimeBuilder(), decoratorHandler.decoratorForBinaryRuntimeBuilder())
	imageStream, err := newOutputImageStreamForRuntime(m.Context, &buildConfig, m.build)
	if err != nil {
		return resources, err
	}
	if err := framework.SetOwner(m.build, m.Scheme, &buildConfig); err != nil {
		return resources, nil
	}
	// we share the ImageStream among others resources, that's why we won't own it
	if err := framework.AddOwnerReference(m.build, m.Scheme, imageStream); err != nil {
		return resources, nil
	}
	resources[reflect.TypeOf(buildv1.BuildConfig{})] = []resource.KubernetesResource{&buildConfig}
	resources[reflect.TypeOf(imgv1.ImageStream{})] = []resource.KubernetesResource{imageStream}
	return resources, nil
}
