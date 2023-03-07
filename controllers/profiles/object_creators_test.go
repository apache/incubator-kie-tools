// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package profiles

import (
	"testing"

	"github.com/magiconair/properties"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"

	"github.com/kiegroup/kogito-serverless-operator/test"
)

func Test_ensureWorkflowPropertiesConfigMapMutator(t *testing.T) {
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeYamlCR, t.Name())
	// can't be new
	cm, _ := workflowDevPropsConfigMapCreator(workflow)
	cm.SetUID("1")
	cm.SetResourceVersion("1")
	reflectCm := cm.(*v1.ConfigMap)

	visitor := ensureWorkflowDevPropertiesConfigMapMutator(workflow)
	mutateFn := visitor(cm)

	assert.NoError(t, mutateFn())
	assert.NotEmpty(t, reflectCm.Data[applicationPropertiesFileName])

	props := properties.MustLoadString(reflectCm.Data[applicationPropertiesFileName])
	assert.Equal(t, "8080", props.GetString("quarkus.http.port", ""))

	// we change the properties to something different, we add ours and change the default
	reflectCm.Data[applicationPropertiesFileName] = "quarkus.http.port=9090\nmy.new.prop=1"
	visitor(reflectCm)
	assert.NoError(t, mutateFn())

	// we should preserve the default, and still got ours
	props = properties.MustLoadString(reflectCm.Data[applicationPropertiesFileName])
	assert.Equal(t, "8080", props.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", props.GetString("quarkus.http.host", ""))
	assert.Equal(t, "1", props.GetString("my.new.prop", ""))
}
