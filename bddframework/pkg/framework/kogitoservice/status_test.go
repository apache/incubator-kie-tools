// Copyright 2019 Red Hat, Inc. and/or its affiliates
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
	"fmt"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	meta2 "k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestReconciliation_ErrorOccur(t *testing.T) {
	instance := test.CreateFakeDataIndex(t.Name())
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	statusHandler := NewStatusHandler(context)
	reconciliationError := fmt.Errorf("test error")
	statusHandler.HandleStatusUpdate(instance, &reconciliationError)
	assert.NotNil(t, instance)

	_, err := kubernetes.ResourceC(cli).Fetch(instance)
	assert.NoError(t, err)
	assert.NotNil(t, instance.Status)
	conditions := *instance.Status.Conditions
	assert.Len(t, conditions, 3)
	failedCondition := getSpecificCondition(conditions, api.FailedConditionType)
	assert.NotNil(t, failedCondition)

	provisionedCondition := getSpecificCondition(conditions, api.ProvisioningConditionType)
	assert.NotNil(t, provisionedCondition)
	assert.Equal(t, metav1.ConditionFalse, provisionedCondition.Status)

	deployedCondition := getSpecificCondition(conditions, api.DeployedConditionType)
	assert.NotNil(t, deployedCondition)
	assert.Equal(t, metav1.ConditionFalse, deployedCondition.Status)
}

func TestReconciliation_RecoverableErrorOccur(t *testing.T) {
	instance := test.CreateFakeDataIndex(t.Name())
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	statusHandler := NewStatusHandler(context)
	var reconciliationError error = infrastructure.ErrorForMonitoring(fmt.Errorf("test error"))
	statusHandler.HandleStatusUpdate(instance, &reconciliationError)
	assert.NotNil(t, instance)

	_, err := kubernetes.ResourceC(cli).Fetch(instance)
	assert.NoError(t, err)
	assert.NotNil(t, instance.Status)
	conditions := *instance.Status.Conditions
	assert.Len(t, conditions, 3)
	failedCondition := getSpecificCondition(conditions, api.FailedConditionType)
	assert.NotNil(t, failedCondition)

	provisionedCondition := getSpecificCondition(conditions, api.ProvisioningConditionType)
	assert.NotNil(t, provisionedCondition)
	assert.Equal(t, metav1.ConditionTrue, provisionedCondition.Status)

	deployedCondition := getSpecificCondition(conditions, api.DeployedConditionType)
	assert.NotNil(t, deployedCondition)
	assert.Equal(t, metav1.ConditionFalse, deployedCondition.Status)
}

func TestReconciliation_RecoveredAfterError(t *testing.T) {
	instance := test.CreateFakeDataIndex(t.Name())
	existingConditions := &[]metav1.Condition{
		{
			Type:    string(api.FailedConditionType),
			Status:  metav1.ConditionTrue,
			Reason:  "Error",
			Message: "error",
		},
		{
			Type:    string(api.ProvisioningConditionType),
			Status:  metav1.ConditionTrue,
			Reason:  "Provisioning",
			Message: "Provisioning",
		},
		{
			Type:    string(api.DeployedConditionType),
			Status:  metav1.ConditionFalse,
			Reason:  "Error",
			Message: "error",
		},
	}
	instance.GetStatus().SetConditions(existingConditions)
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	statusHandler := NewStatusHandler(context)
	var noError error
	statusHandler.HandleStatusUpdate(instance, &noError)
	assert.NotNil(t, instance)

	_, err := kubernetes.ResourceC(cli).Fetch(instance)
	assert.NoError(t, err)
	assert.NotNil(t, instance.Status)
	conditions := *instance.Status.Conditions
	assert.Len(t, conditions, 3)

	failedCondition := getSpecificCondition(conditions, api.FailedConditionType)
	assert.NotNil(t, failedCondition)
	assert.Equal(t, metav1.ConditionFalse, failedCondition.Status)

	provisionedCondition := getSpecificCondition(conditions, api.ProvisioningConditionType)
	assert.NotNil(t, provisionedCondition)
	assert.Equal(t, metav1.ConditionTrue, provisionedCondition.Status)

	deployedCondition := getSpecificCondition(conditions, api.DeployedConditionType)
	assert.NotNil(t, deployedCondition)
	assert.Equal(t, metav1.ConditionFalse, deployedCondition.Status)
}

func TestReconciliation(t *testing.T) {
	instance := test.CreateFakeDataIndex(t.Name())
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	statusHandler := NewStatusHandler(context)
	var err error = nil
	statusHandler.HandleStatusUpdate(instance, &err)
	assert.NotNil(t, instance)

	_, err = kubernetes.ResourceC(cli).Fetch(instance)
	assert.NoError(t, err)
	assert.NotNil(t, instance.Status)
	conditions := *instance.Status.Conditions
	assert.Len(t, conditions, 2)

	provisionedCondition := getSpecificCondition(conditions, api.ProvisioningConditionType)
	assert.NotNil(t, provisionedCondition)
	assert.Equal(t, metav1.ConditionTrue, provisionedCondition.Status)

	deployedCondition := getSpecificCondition(conditions, api.DeployedConditionType)
	assert.NotNil(t, deployedCondition)
	assert.Equal(t, metav1.ConditionFalse, deployedCondition.Status)
}

func TestReconciliation_PodAlreadyRunning(t *testing.T) {
	instance := test.CreateFakeDataIndex(t.Name())
	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      instance.Name,
			Namespace: instance.Namespace,
		},
		Status: appsv1.DeploymentStatus{
			AvailableReplicas: 1,
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance, deployment).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	statusHandler := NewStatusHandler(context)
	var err error = nil
	statusHandler.HandleStatusUpdate(instance, &err)
	assert.NotNil(t, instance)

	_, err = kubernetes.ResourceC(cli).Fetch(instance)
	assert.NoError(t, err)
	assert.NotNil(t, instance.Status)
	conditions := *instance.Status.Conditions
	assert.Len(t, conditions, 2)

	provisionedCondition := getSpecificCondition(conditions, api.ProvisioningConditionType)
	assert.NotNil(t, provisionedCondition)
	assert.Equal(t, metav1.ConditionFalse, provisionedCondition.Status)

	deployedCondition := getSpecificCondition(conditions, api.DeployedConditionType)
	assert.NotNil(t, deployedCondition)
	assert.Equal(t, metav1.ConditionTrue, deployedCondition.Status)
}

func getSpecificCondition(conditions []metav1.Condition, conditionType api.KogitoServiceConditionType) *metav1.Condition {
	return meta2.FindStatusCondition(conditions, string(conditionType))
}
