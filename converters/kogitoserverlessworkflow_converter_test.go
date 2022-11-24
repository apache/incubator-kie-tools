// Copyright 2022 Red Hat, Inc. and/or its affiliates
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

package converters

import (
	"context"
	"github.com/kiegroup/kogito-serverless-operator/test/utils"
	"github.com/stretchr/testify/assert"
	"k8s.io/client-go/rest"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/envtest"
	"testing"
)

var cfg *rest.Config
var k8sClient client.Client
var testEnv *envtest.Environment

func TestKogitoServerlessWorkflowConverter(t *testing.T) {
	t.Run("verify that when KogitoServerlessWorkflow CR is nil an error is returned", func(t *testing.T) {
		context := context.TODO()
		// Create a KogitoServerlessWorkflow object with metadata and spec.
		ksw, _ := utils.GetKogitoServerlessWorkflow("../config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml")
		converterToTest := NewKogitoServerlessWorkflowConverter(context)
		out, err := converterToTest.ToCNCFWorkflow(ksw)
		assert.NoError(t, err)
		assert.True(t, out != nil)
		assert.True(t, out.Name == "greeting")
		assert.True(t, out.Description == "Greeting example on k8s!")
		assert.True(t, out.Functions != nil && len(out.Functions) == 1)
		assert.True(t, out.States != nil && len(out.States) == 4)
	})

}
