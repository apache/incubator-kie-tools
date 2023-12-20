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

package controllers

import (
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform/services"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/constants"
	"github.com/apache/incubator-kie-kogito-serverless-operator/test"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
)

var (
	envDBKind = corev1.EnvVar{
		Name:  "QUARKUS_DATASOURCE_DB_KIND",
		Value: constants.PersistenceTypePostgreSQL,
	}

	envDataIndex = corev1.EnvVar{
		Name:  "KOGITO_DATA_INDEX_QUARKUS_PROFILE",
		Value: "http-events-support",
	}
)

func TestSonataFlowPlatformController(t *testing.T) {
	t.Run("verify that a basic reconcile is performed without error", func(t *testing.T) {
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatform()

		// Create a fake client to mock API calls.
		cl := test.NewSonataFlowClientBuilder().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		// Create a SonataFlowPlatformReconciler object with the scheme and fake client.
		r := &SonataFlowPlatformReconciler{cl, cl, cl.Scheme(), &rest.Config{}, &record.FakeRecorder{}}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      ksp.Name,
				Namespace: ksp.Namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.Equal(t, "quay.io/kiegroup", ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.Nil(t, ksp.Spec.Services.DataIndex)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)

		assert.Equal(t, v1alpha08.PlatformCreatingReason, ksp.Status.GetTopLevelCondition().Reason)
	})

	t.Run("verify that a basic reconcile with data index service & persistence is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		ksp.Spec.Services = v1alpha08.ServicesPlatformSpec{
			DataIndex: &v1alpha08.ServiceSpec{},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		// Create a SonataFlowPlatformReconciler object with the scheme and fake client.
		r := &SonataFlowPlatformReconciler{cl, cl, cl.Scheme(), &rest.Config{}, &record.FakeRecorder{}}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      ksp.Name,
				Namespace: ksp.Namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.Equal(t, "quay.io/kiegroup", ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.NotNil(t, ksp.Spec.Services.DataIndex)
		assert.Nil(t, ksp.Spec.Services.DataIndex.PodTemplate.Replicas)
		assert.NotNil(t, ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)

		assert.Equal(t, "", ksp.Status.GetTopLevelCondition().Reason)

		// Check data index deployment
		dep := &appsv1.Deployment{}
		di := services.NewDataIndexService(ksp)
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: di.GetServiceName(), Namespace: ksp.Namespace}, dep))

		env := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: constants.PersistenceTypePostgreSQL,
		}
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, env)

		// Check with persistence set
		ksp.Spec.Services.DataIndex.Persistence = &v1alpha08.PersistenceOptions{PostgreSql: &v1alpha08.PersistencePostgreSql{
			SecretRef:  v1alpha08.PostgreSqlSecretOptions{Name: "test"},
			ServiceRef: &v1alpha08.PostgreSqlServiceOptions{Name: "test"},
		}}
		// Ensure correct container overriding anything set in PodSpec
		ksp.Spec.Services.DataIndex.PodTemplate.Container = v1alpha08.ContainerSpec{TerminationMessagePath: "testing"}
		ksp.Spec.Services.DataIndex.PodTemplate.Containers = []corev1.Container{{Name: constants.DataIndexServiceName + "2", TerminationMessagePath: "testing"}}
		assert.NoError(t, cl.Update(context.TODO(), ksp))

		_, err = r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: di.GetServiceName(), Namespace: ksp.Namespace}, dep))
		assert.Len(t, dep.Spec.Template.Spec.Containers, 2)
		assert.Equal(t, constants.DataIndexServiceName+"2", dep.Spec.Template.Spec.Containers[0].Name)
		assert.Equal(t, "testing", dep.Spec.Template.Spec.Containers[0].TerminationMessagePath)
		assert.Equal(t, constants.DataIndexServiceName, dep.Spec.Template.Spec.Containers[1].Name)
		assert.Equal(t, "testing", dep.Spec.Template.Spec.Containers[1].TerminationMessagePath)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypePostgreSQL), dep.Spec.Template.Spec.Containers[1].Image)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[1].Env, env)
	})

	t.Run("verify that a basic reconcile with data index service & jdbcUrl is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		var replicas int32 = 2
		ksp.Spec.Services = v1alpha08.ServicesPlatformSpec{
			DataIndex: &v1alpha08.ServiceSpec{
				PodTemplate: v1alpha08.PodTemplateSpec{
					Replicas: &replicas,
					Container: v1alpha08.ContainerSpec{
						Command: []string{"test:latest"},
					},
				},
			},
		}

		di := services.NewDataIndexService(ksp)

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		// Create a SonataFlowPlatformReconciler object with the scheme and fake client.
		r := &SonataFlowPlatformReconciler{cl, cl, cl.Scheme(), &rest.Config{}, &record.FakeRecorder{}}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      ksp.Name,
				Namespace: ksp.Namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.Equal(t, "quay.io/kiegroup", ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.NotNil(t, ksp.Spec.Services.DataIndex)
		assert.NotNil(t, ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)

		assert.Equal(t, "", ksp.Status.GetTopLevelCondition().Reason)

		// Check data index deployment
		dep := &appsv1.Deployment{}
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: di.GetServiceName(), Namespace: ksp.Namespace}, dep))

		env := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: constants.PersistenceTypePostgreSQL,
		}
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, env)

		// Check with persistence set
		url := "jdbc:postgresql://host:1234/database?currentSchema=data-index-service"
		ksp.Spec.Services.DataIndex.Persistence = &v1alpha08.PersistenceOptions{PostgreSql: &v1alpha08.PersistencePostgreSql{
			SecretRef: v1alpha08.PostgreSqlSecretOptions{Name: "test"},
			JdbcUrl:   url,
		}}
		// Ensure correct container overriding anything set in PodSpec
		ksp.Spec.Services.DataIndex.PodTemplate.PodSpec.Containers = []corev1.Container{{Name: constants.DataIndexServiceName}}
		assert.NoError(t, cl.Update(context.TODO(), ksp))

		_, err = r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		env2 := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_JDBC_URL",
			Value: url,
		}
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: di.GetServiceName(), Namespace: ksp.Namespace}, dep))
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypePostgreSQL), dep.Spec.Template.Spec.Containers[0].Image)
		assert.Equal(t, &replicas, dep.Spec.Replicas)
		assert.Equal(t, []string{"test:latest"}, dep.Spec.Template.Spec.Containers[0].Command)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, env)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, env2)
	})

	// Job Service tests
	t.Run("verify that a basic reconcile with job service & persistence is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		ksp.Spec.Services = v1alpha08.ServicesPlatformSpec{
			JobService: &v1alpha08.ServiceSpec{},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		// Create a SonataFlowPlatformReconciler object with the scheme and fake client.
		r := &SonataFlowPlatformReconciler{cl, cl, cl.Scheme(), &rest.Config{}, &record.FakeRecorder{}}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      ksp.Name,
				Namespace: ksp.Namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.Equal(t, "quay.io/kiegroup", ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.NotNil(t, ksp.Spec.Services.JobService)
		assert.Nil(t, ksp.Spec.Services.DataIndex)
		assert.NotNil(t, ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)

		assert.Equal(t, "", ksp.Status.GetTopLevelCondition().Reason)

		// Check data index deployment
		dep := &appsv1.Deployment{}
		js := services.NewJobService(ksp)
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: js.GetServiceName(), Namespace: ksp.Namespace}, dep))

		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, js.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDBKind)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDataIndex)

		// Check with persistence set
		ksp.Spec.Services.JobService.Persistence = &v1alpha08.PersistenceOptions{PostgreSql: &v1alpha08.PersistencePostgreSql{
			SecretRef:  v1alpha08.PostgreSqlSecretOptions{Name: "test"},
			ServiceRef: &v1alpha08.PostgreSqlServiceOptions{Name: "test"},
		}}
		// Ensure correct container overriding anything set in PodSpec
		ksp.Spec.Services.JobService.PodTemplate.Container = v1alpha08.ContainerSpec{TerminationMessagePath: "testing"}
		ksp.Spec.Services.JobService.PodTemplate.Containers = []corev1.Container{{Name: constants.JobServiceName + "2", TerminationMessagePath: "testing"}}
		assert.NoError(t, cl.Update(context.TODO(), ksp))

		_, err = r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: js.GetServiceName(), Namespace: ksp.Namespace}, dep))
		assert.Len(t, dep.Spec.Template.Spec.Containers, 2)
		assert.Equal(t, constants.JobServiceName+"2", dep.Spec.Template.Spec.Containers[0].Name)
		assert.Equal(t, "testing", dep.Spec.Template.Spec.Containers[0].TerminationMessagePath)
		assert.Equal(t, constants.JobServiceName, dep.Spec.Template.Spec.Containers[1].Name)
		assert.Equal(t, "testing", dep.Spec.Template.Spec.Containers[1].TerminationMessagePath)
		assert.Equal(t, js.GetServiceImageName(constants.PersistenceTypePostgreSQL), dep.Spec.Template.Spec.Containers[1].Image)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[1].Env, envDBKind)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[1].Env, envDataIndex)
	})

	t.Run("verify that a basic reconcile with job service & jdbcUrl is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		var replicas int32 = 2
		ksp.Spec.Services = v1alpha08.ServicesPlatformSpec{
			JobService: &v1alpha08.ServiceSpec{
				PodTemplate: v1alpha08.PodTemplateSpec{
					Replicas: &replicas,
					Container: v1alpha08.ContainerSpec{
						Command: []string{"test:latest"},
					},
				},
			},
		}

		js := services.NewJobService(ksp)

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		// Create a SonataFlowPlatformReconciler object with the scheme and fake client.
		r := &SonataFlowPlatformReconciler{cl, cl, cl.Scheme(), &rest.Config{}, &record.FakeRecorder{}}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      ksp.Name,
				Namespace: ksp.Namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.Equal(t, "quay.io/kiegroup", ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.NotNil(t, ksp.Spec.Services.JobService)
		assert.NotNil(t, ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)

		assert.Equal(t, "", ksp.Status.GetTopLevelCondition().Reason)

		// Check job service deployment
		dep := &appsv1.Deployment{}
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: js.GetServiceName(), Namespace: ksp.Namespace}, dep))

		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, js.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDBKind)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDataIndex)

		// Check with persistence set
		url := "jdbc:postgresql://host:1234/database?currentSchema=data-index-service"
		ksp.Spec.Services.JobService.Persistence = &v1alpha08.PersistenceOptions{PostgreSql: &v1alpha08.PersistencePostgreSql{
			SecretRef: v1alpha08.PostgreSqlSecretOptions{Name: "test"},
			JdbcUrl:   url,
		}}
		// Ensure correct container overriding anything set in PodSpec
		ksp.Spec.Services.JobService.PodTemplate.PodSpec.Containers = []corev1.Container{{Name: constants.JobServiceName}}
		assert.NoError(t, cl.Update(context.TODO(), ksp))

		_, err = r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: js.GetServiceName(), Namespace: ksp.Namespace}, dep))
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, js.GetServiceImageName(constants.PersistenceTypePostgreSQL), dep.Spec.Template.Spec.Containers[0].Image)
		assert.Equal(t, int32(1), *dep.Spec.Replicas)
		assert.Equal(t, []string{"test:latest"}, dep.Spec.Template.Spec.Containers[0].Command)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, envDBKind)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDataIndex)
	})

	t.Run("verify that a default deployment of a job and data index service will is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		ksp.Spec.Services = v1alpha08.ServicesPlatformSpec{
			DataIndex:  &v1alpha08.ServiceSpec{},
			JobService: &v1alpha08.ServiceSpec{},
		}

		di := services.NewDataIndexService(ksp)
		js := services.NewJobService(ksp)
		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		// Create a SonataFlowPlatformReconciler object with the scheme and fake client.
		r := &SonataFlowPlatformReconciler{cl, cl, cl.Scheme(), &rest.Config{}, &record.FakeRecorder{}}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      ksp.Name,
				Namespace: ksp.Namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.Equal(t, "quay.io/kiegroup", ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.NotNil(t, ksp.Spec.Services.DataIndex)
		assert.NotNil(t, ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.DataIndex.Enabled)
		assert.NotNil(t, ksp.Spec.Services.JobService)
		assert.NotNil(t, ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)

		assert.Equal(t, "", ksp.Status.GetTopLevelCondition().Reason)

		// Check data index deployment
		dep := &appsv1.Deployment{}
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: di.GetServiceName(), Namespace: ksp.Namespace}, dep))

		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDBKind)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, envDataIndex)

		// Check job service deployment
		dep = &appsv1.Deployment{}
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: js.GetServiceName(), Namespace: ksp.Namespace}, dep))

		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, js.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDBKind)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDataIndex)

	})
}
