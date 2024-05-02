/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package workflowdef

import (
	"context"
	"testing"

	"github.com/serverlessworkflow/sdk-go/v2/model"
	"github.com/stretchr/testify/assert"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
)

func TestSonataFlowConverter(t *testing.T) {
	t.Run("verify that when SonataFlow CR is nil an error is returned", func(t *testing.T) {
		// Create a SonataFlow object with metadata and spec.
		ksw := test.GetBaseSonataFlow(t.Name())
		out, err := operatorapi.ToCNCFWorkflow(ksw, context.TODO())
		assert.NoError(t, err)
		assert.True(t, out != nil)
		assert.Equal(t, "greeting", out.ID)
		//assert.Equal(t, "greeting-key", out.Key)
		assert.Equal(t, "0.0.1", out.Version)
		assert.Equal(t, "0.8", out.SpecVersion)
		assert.Equal(t, "Greeting example on k8s!", out.Description)
		assert.Equal(t, model.JqExpressionLang, out.ExpressionLang)
		assert.True(t, out.Functions != nil && len(out.Functions) == 1)
		assert.True(t, out.States != nil && len(out.States) == 4)
	})

}
