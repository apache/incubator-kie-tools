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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestNewBuildConfig_CustomLabels(t *testing.T) {
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

	context := BuildContext{
		Context: operator.Context{
			Log: test.TestLogger,
			Labels: map[string]string{
				"key1": "value1",
			},
		},
	}

	decoratorHandler := NewDecoratorHandler(context)
	decorator := decoratorHandler.decoratorForCustomLabels()
	buildConfigHandler := NewBuildConfigHandler(context)
	buildConfig := buildConfigHandler.newBuildConfig(kogitoBuild, decorator)
	finalLabels := buildConfig.Labels
	assert.Equal(t, 3, len(finalLabels))
	assert.Equal(t, "value1", finalLabels["key1"])
}
