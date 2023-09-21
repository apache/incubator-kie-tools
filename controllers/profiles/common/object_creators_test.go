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

package common

import (
	"testing"

	"github.com/magiconair/properties"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/kogito-serverless-operator/test"
	"github.com/kiegroup/kogito-serverless-operator/workflowproj"
)

func Test_ensureWorkflowPropertiesConfigMapMutator(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())
	// can't be new
	cm, _ := WorkflowPropsConfigMapCreator(workflow)
	cm.SetUID("1")
	cm.SetResourceVersion("1")
	reflectCm := cm.(*v1.ConfigMap)

	visitor := WorkflowPropertiesMutateVisitor(workflow, DefaultApplicationProperties)
	mutateFn := visitor(cm)

	assert.NoError(t, mutateFn())
	assert.NotEmpty(t, reflectCm.Data[workflowproj.ApplicationPropertiesFileName])

	props := properties.MustLoadString(reflectCm.Data[workflowproj.ApplicationPropertiesFileName])
	assert.Equal(t, "8080", props.GetString("quarkus.http.port", ""))

	// we change the properties to something different, we add ours and change the default
	reflectCm.Data[workflowproj.ApplicationPropertiesFileName] = "quarkus.http.port=9090\nmy.new.prop=1"
	visitor(reflectCm)
	assert.NoError(t, mutateFn())

	// we should preserve the default, and still got ours
	props = properties.MustLoadString(reflectCm.Data[workflowproj.ApplicationPropertiesFileName])
	assert.Equal(t, "8080", props.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", props.GetString("quarkus.http.host", ""))
	assert.Equal(t, "1", props.GetString("my.new.prop", ""))
}

func Test_ensureWorkflowPropertiesConfigMapMutator_DollarReplacement(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())
	existingCM := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			UID:       "0000-0001-0002-0003",
		},
		Data: map[string]string{
			workflowproj.ApplicationPropertiesFileName: "mp.messaging.outgoing.kogito_outgoing_stream.url=${kubernetes:services.v1/event-listener}",
		},
	}
	mutateVisitorFn := WorkflowPropertiesMutateVisitor(workflow, DefaultApplicationProperties)

	err := mutateVisitorFn(existingCM)()
	assert.NoError(t, err)
	assert.Contains(t, existingCM.Data[workflowproj.ApplicationPropertiesFileName], "${kubernetes:services.v1/event-listener}")
}
