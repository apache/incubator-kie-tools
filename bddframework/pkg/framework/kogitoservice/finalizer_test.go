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

package kogitoservice

import (
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestAddFinalizer(t *testing.T) {
	ns := t.Name()
	dataIndex := test.CreateFakeDataIndex(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(dataIndex).Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	finalizerHandler := NewFinalizerHandler(context, infraHandler)
	err := finalizerHandler.AddFinalizer(dataIndex)
	assert.NoError(t, err)
	exists, err := kubernetes.ResourceC(cli).Fetch(dataIndex)
	assert.NoError(t, err)
	assert.True(t, exists)
	assert.Equal(t, 1, len(dataIndex.GetFinalizers()))
}

func TestHandleFinalization(t *testing.T) {
	ns := t.Name()
	dataIndex := test.CreateFakeDataIndex(ns)
	dataIndex.SetFinalizers([]string{"delete.kogitoInfra.ownership.finalizer"})
	cli := test.NewFakeClientBuilder().AddK8sObjects(dataIndex).Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	finalizerHandler := NewFinalizerHandler(context, infraHandler)
	err := finalizerHandler.HandleFinalization(dataIndex)
	assert.NoError(t, err)
	exists, err := kubernetes.ResourceC(cli).Fetch(dataIndex)
	assert.NoError(t, err)
	assert.True(t, exists)
	assert.Equal(t, 0, len(dataIndex.GetFinalizers()))
}
