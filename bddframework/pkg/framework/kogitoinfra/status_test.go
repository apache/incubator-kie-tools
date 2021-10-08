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

package kogitoinfra

import (
	"errors"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal/app"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestUpdateBaseStatus(t *testing.T) {
	instance := &v1beta1.KogitoInfra{
		ObjectMeta: v1.ObjectMeta{
			Name:      "kogito-kafka",
			Namespace: t.Name(),
		},
		Spec: v1beta1.KogitoInfraSpec{
			Resource: &v1beta1.InfraResource{
				Kind:       "Kafka",
				APIVersion: "kafka.strimzi.io/v1beta2",
			},
		},
	}

	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Log:    test.TestLogger,
		Client: cli,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := app.NewKogitoInfraHandler(context)
	statusHandler := NewStatusHandler(context, infraHandler)
	err1 := errors.New("error1")
	statusHandler.UpdateBaseStatus(instance, &err1)
	test.AssertFetchMustExist(t, cli, instance)
	conditions := *instance.Status.Conditions
	assert.Equal(t, 1, len(conditions))
	assert.Equal(t, string(api.KogitoInfraConfigured), conditions[0].Type)
	assert.Equal(t, v1.ConditionFalse, conditions[0].Status)
	assert.Equal(t, "error1", conditions[0].Message)

	err2 := errors.New("error2")
	statusHandler.UpdateBaseStatus(instance, &err2)
	test.AssertFetchMustExist(t, cli, instance)
	conditions = *instance.Status.Conditions
	assert.Equal(t, 1, len(conditions))
	assert.Equal(t, string(api.KogitoInfraConfigured), conditions[0].Type)
	assert.Equal(t, v1.ConditionFalse, conditions[0].Status)
	assert.Equal(t, "error2", conditions[0].Message)

	var err3 error
	statusHandler.UpdateBaseStatus(instance, &err3)
	test.AssertFetchMustExist(t, cli, instance)
	conditions = *instance.Status.Conditions
	assert.Equal(t, 1, len(conditions))
	assert.Equal(t, string(api.KogitoInfraConfigured), conditions[0].Type)
	assert.Equal(t, v1.ConditionTrue, conditions[0].Status)
}
