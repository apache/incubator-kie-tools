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

package kogitoservice

import (
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	"testing"

	"github.com/stretchr/testify/assert"
)

func Test_GetKogitoServiceEndpoint(t *testing.T) {
	service := test.CreateFakeDataIndex(t.Name())
	cli := test.NewFakeClientBuilder().Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	kogitoServiceHandler := NewKogitoServiceHandler(context)
	actualURL := kogitoServiceHandler.GetKogitoServiceEndpoint(service)
	assert.Equal(t, "http://"+service.GetName()+"."+t.Name(), actualURL)
}
