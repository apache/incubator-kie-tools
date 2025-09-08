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

package controller

import (
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	duckv1 "knative.dev/pkg/apis/duck/v1"
	"knative.dev/pkg/kmeta"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/clusterplatform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
)

var (
	envDBKind = corev1.EnvVar{
		Name:  "QUARKUS_DATASOURCE_DB_KIND",
		Value: constants.PersistenceTypePostgreSQL.String(),
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
		utils.SetClient(cl)
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
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)

		assert.Equal(t, v1alpha08.PlatformCreatingReason, ksp.Status.GetTopLevelCondition().Reason)
	})

	t.Run("verify that a basic reconcile with data index service & persistence is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		ksp.Spec.Services = &v1alpha08.ServicesPlatformSpec{
			DataIndex: &v1alpha08.DataIndexServiceSpec{},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		utils.SetClient(cl)
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
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
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
		di := services.NewDataIndexHandler(ksp)
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: di.GetServiceName(), Namespace: ksp.Namespace}, dep))

		env := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: constants.PersistenceTypePostgreSQL.String(),
		}
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, env)

		// Check with persistence set
		ksp.Spec.Services.DataIndex.Persistence = &v1alpha08.PersistenceOptionsSpec{PostgreSQL: &v1alpha08.PersistencePostgreSQL{
			SecretRef:  v1alpha08.PostgreSQLSecretOptions{Name: "test"},
			ServiceRef: &v1alpha08.PostgreSQLServiceOptions{SQLServiceOptions: &v1alpha08.SQLServiceOptions{Name: "test"}},
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
		ksp.Spec.Services = &v1alpha08.ServicesPlatformSpec{
			DataIndex: &v1alpha08.DataIndexServiceSpec{
				ServiceSpec: v1alpha08.ServiceSpec{
					PodTemplate: v1alpha08.PodTemplateSpec{
						Replicas: &replicas,
						Container: v1alpha08.ContainerSpec{
							Command: []string{"test:latest"},
						},
					},
				},
				Source: nil,
			},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		utils.SetClient(cl)

		di := services.NewDataIndexHandler(ksp)

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
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
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
			Value: constants.PersistenceTypePostgreSQL.String(),
		}
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, env)

		// Check with persistence set
		url := "jdbc:postgresql://host:1234/database?currentSchema=data-index-service"
		ksp.Spec.Services.DataIndex.Persistence = &v1alpha08.PersistenceOptionsSpec{PostgreSQL: &v1alpha08.PersistencePostgreSQL{
			SecretRef: v1alpha08.PostgreSQLSecretOptions{Name: "test"},
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

	var (
		postgreSQLPort int = 5432
	)
	t.Run("verify that persistence options are correctly reconciled when defined in the platform", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		// Check with persistence set
		ksp.Spec = v1alpha08.SonataFlowPlatformSpec{
			Services: &v1alpha08.ServicesPlatformSpec{
				DataIndex: &v1alpha08.DataIndexServiceSpec{
					ServiceSpec: v1alpha08.ServiceSpec{
						Persistence: &v1alpha08.PersistenceOptionsSpec{
							DBMigrationStrategy: "none",
						},
					},
				},
				JobService: &v1alpha08.JobServiceServiceSpec{
					ServiceSpec: v1alpha08.ServiceSpec{
						Persistence: &v1alpha08.PersistenceOptionsSpec{
							DBMigrationStrategy: "none",
						},
					},
				},
			},
			Persistence: &v1alpha08.PlatformPersistenceOptionsSpec{
				PostgreSQL: &v1alpha08.PlatformPersistencePostgreSQL{
					SecretRef: v1alpha08.PostgreSQLSecretOptions{Name: "generic", UserKey: "POSTGRESQL_USER", PasswordKey: "POSTGRESQL_PASSWORD"},
					ServiceRef: &v1alpha08.SQLServiceOptions{
						Name:         "postgresql",
						Namespace:    "default",
						Port:         &postgreSQLPort,
						DatabaseName: "sonataflow"},
				},
			},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		utils.SetClient(cl)
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
		dbSourceKind := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: constants.PersistenceTypePostgreSQL.String(),
		}
		dbSourceDIURL := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_JDBC_URL",
			Value: "jdbc:postgresql://postgresql.default:5432/sonataflow?currentSchema=sonataflow-platform-data-index-service",
		}
		dbSourceJSURL := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_JDBC_URL",
			Value: "jdbc:postgresql://postgresql.default:5432/sonataflow?currentSchema=sonataflow-platform-jobs-service",
		}
		dbSourceJSReactiveURL := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_REACTIVE_URL",
			Value: "postgresql://postgresql.default:5432/sonataflow?search_path=sonataflow-platform-jobs-service",
		}
		dbUsername := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_USERNAME",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "generic"},
					Key:                  "POSTGRESQL_USER",
				},
			},
		}
		dbPassword := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_PASSWORD",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "generic"},
					Key:                  "POSTGRESQL_PASSWORD",
				},
			},
		}
		// Check Data Index deployment to ensure it contains references to the persistence values defined in the platform CR
		dep := &appsv1.Deployment{}
		di := services.NewDataIndexHandler(ksp)
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: di.GetServiceName(), Namespace: ksp.Namespace}, dep))
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypePostgreSQL), dep.Spec.Template.Spec.Containers[0].Image)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbSourceKind)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbUsername)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbPassword)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbSourceDIURL)

		js := services.NewJobServiceHandler(ksp)
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: js.GetServiceName(), Namespace: ksp.Namespace}, dep))
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, js.GetServiceImageName(constants.PersistenceTypePostgreSQL), dep.Spec.Template.Spec.Containers[0].Image)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbSourceKind)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbUsername)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbPassword)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbSourceJSURL)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbSourceJSReactiveURL)
	})

	t.Run("verify that persistence options are correctly reconciled when defined in the platform and overwriten in the services spec", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		// Check with persistence set
		urlDI := "jdbc:postgresql://localhost:5432/database?currentSchema=data-index-service"
		urlJS := "jdbc:postgresql://localhost:5432/database?currentSchema=job-service"
		ksp.Spec = v1alpha08.SonataFlowPlatformSpec{
			Services: &v1alpha08.ServicesPlatformSpec{
				DataIndex: &v1alpha08.DataIndexServiceSpec{
					ServiceSpec: v1alpha08.ServiceSpec{
						Persistence: &v1alpha08.PersistenceOptionsSpec{
							PostgreSQL: &v1alpha08.PersistencePostgreSQL{
								SecretRef: v1alpha08.PostgreSQLSecretOptions{Name: "dataIndex"},
								JdbcUrl:   urlDI,
							},
						},
					},
				},
				JobService: &v1alpha08.JobServiceServiceSpec{
					ServiceSpec: v1alpha08.ServiceSpec{
						Persistence: &v1alpha08.PersistenceOptionsSpec{
							PostgreSQL: &v1alpha08.PersistencePostgreSQL{
								SecretRef: v1alpha08.PostgreSQLSecretOptions{Name: "job"},
								JdbcUrl:   urlJS,
							},
						},
					},
				},
			},
			Persistence: &v1alpha08.PlatformPersistenceOptionsSpec{
				PostgreSQL: &v1alpha08.PlatformPersistencePostgreSQL{
					SecretRef:  v1alpha08.PostgreSQLSecretOptions{Name: "generic", UserKey: "POSTGRESQL_USER", PasswordKey: "POSTGRESQL_PASSWORD"},
					ServiceRef: &v1alpha08.SQLServiceOptions{Name: "postgresql", Namespace: "default", Port: &postgreSQLPort, DatabaseName: "sonataflow"},
				},
			},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		utils.SetClient(cl)
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
		dbSourceKind := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: constants.PersistenceTypePostgreSQL.String(),
		}
		dbDIUsername := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_USERNAME",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "dataIndex"},
					Key:                  "POSTGRESQL_USER",
				},
			},
		}
		dbDIPassword := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_PASSWORD",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "dataIndex"},
					Key:                  "POSTGRESQL_PASSWORD",
				},
			},
		}
		dbJSUsername := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_USERNAME",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "job"},
					Key:                  "POSTGRESQL_USER",
				},
			},
		}
		dbJSPassword := corev1.EnvVar{
			Name:  "QUARKUS_DATASOURCE_PASSWORD",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "job"},
					Key:                  "POSTGRESQL_PASSWORD",
				},
			},
		}
		// Check Data Index deployment to ensure it contains references to the persistence values defined in the platform CR
		dep := &appsv1.Deployment{}
		di := services.NewDataIndexHandler(ksp)
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: di.GetServiceName(), Namespace: ksp.Namespace}, dep))
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, di.GetServiceImageName(constants.PersistenceTypePostgreSQL), dep.Spec.Template.Spec.Containers[0].Image)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbSourceKind)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbDIUsername)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbDIPassword)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, corev1.EnvVar{Name: "QUARKUS_DATASOURCE_JDBC_URL", Value: urlDI})

		js := services.NewJobServiceHandler(ksp)
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: js.GetServiceName(), Namespace: ksp.Namespace}, dep))
		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, js.GetServiceImageName(constants.PersistenceTypePostgreSQL), dep.Spec.Template.Spec.Containers[0].Image)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbSourceKind)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbJSUsername)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, dbJSPassword)
		assert.Contains(t, dep.Spec.Template.Spec.Containers[0].Env, corev1.EnvVar{Name: "QUARKUS_DATASOURCE_JDBC_URL", Value: urlJS})
	})

	// Job Service tests
	t.Run("verify that a basic reconcile with job service & persistence is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		ksp.Spec.Services = &v1alpha08.ServicesPlatformSpec{
			JobService: &v1alpha08.JobServiceServiceSpec{},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		utils.SetClient(cl)
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
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
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
		js := services.NewJobServiceHandler(ksp)
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: js.GetServiceName(), Namespace: ksp.Namespace}, dep))

		assert.Len(t, dep.Spec.Template.Spec.Containers, 1)
		assert.Equal(t, js.GetServiceImageName(constants.PersistenceTypeEphemeral), dep.Spec.Template.Spec.Containers[0].Image)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDBKind)
		assert.NotContains(t, dep.Spec.Template.Spec.Containers[0].Env, envDataIndex)

		// Check with persistence set
		ksp.Spec.Services.JobService.Persistence = &v1alpha08.PersistenceOptionsSpec{PostgreSQL: &v1alpha08.PersistencePostgreSQL{
			SecretRef:  v1alpha08.PostgreSQLSecretOptions{Name: "test"},
			ServiceRef: &v1alpha08.PostgreSQLServiceOptions{SQLServiceOptions: &v1alpha08.SQLServiceOptions{Name: "test"}},
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
		ksp.Spec.Services = &v1alpha08.ServicesPlatformSpec{
			JobService: &v1alpha08.JobServiceServiceSpec{
				ServiceSpec: v1alpha08.ServiceSpec{
					PodTemplate: v1alpha08.PodTemplateSpec{
						Replicas: &replicas,
						Container: v1alpha08.ContainerSpec{
							Command: []string{"test:latest"},
						},
					},
				},
			},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		utils.SetClient(cl)
		js := services.NewJobServiceHandler(ksp)
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
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
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
		ksp.Spec.Services.JobService.Persistence = &v1alpha08.PersistenceOptionsSpec{PostgreSQL: &v1alpha08.PersistencePostgreSQL{
			SecretRef: v1alpha08.PostgreSQLSecretOptions{Name: "test"},
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
		ksp.Spec.Services = &v1alpha08.ServicesPlatformSpec{
			DataIndex:  &v1alpha08.DataIndexServiceSpec{},
			JobService: &v1alpha08.JobServiceServiceSpec{},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp).WithStatusSubresource(ksp).Build()
		utils.SetClient(cl)
		di := services.NewDataIndexHandler(ksp)
		js := services.NewJobServiceHandler(ksp)
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
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
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
	t.Run("verify that a basic reconcile of a cluster platform is performed without error", func(t *testing.T) {
		namespace := t.Name()

		// Create a SonataFlowClusterPlatform object with metadata and spec.
		kscp := test.GetBaseClusterPlatformInReadyPhase(namespace)

		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformInReadyPhase(namespace)
		ksp.Spec.Services = &v1alpha08.ServicesPlatformSpec{
			DataIndex:  &v1alpha08.DataIndexServiceSpec{},
			JobService: &v1alpha08.JobServiceServiceSpec{},
		}
		ksp2 := test.GetBasePlatformInReadyPhase(namespace)
		ksp2.Name = "ksp2"

		// Create a fake client to mock API calls.
		cl := test.NewSonataFlowClientBuilder().WithRuntimeObjects(kscp, ksp, ksp2).WithStatusSubresource(kscp, ksp, ksp2).Build()
		utils.SetClient(cl)

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
		assert.Greater(t, len(ksp2.Status.Conditions), 0)
		assert.Nil(t, ksp2.Status.ClusterPlatformRef)

		// Create a SonataFlowClusterPlatformReconciler object with the scheme and fake client.
		cr := &SonataFlowClusterPlatformReconciler{cl, cl, cl.Scheme(), &rest.Config{}, &record.FakeRecorder{}}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		cReq := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name: kscp.Name,
			},
		}
		_, err = cr.Reconcile(context.TODO(), cReq)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: kscp.Name}, kscp))
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.True(t, ksp.Status.IsReady())
		assert.True(t, kscp.Status.IsReady())
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.NotNil(t, ksp.Spec.Services.DataIndex)
		assert.NotNil(t, ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)
		assert.Equal(t, "", ksp.Status.GetTopLevelCondition().Reason)
		assert.Equal(t, kscp.Name, ksp.Status.ClusterPlatformRef.Name)
		assert.Equal(t, kscp.Spec.PlatformRef.Name, ksp.Status.ClusterPlatformRef.PlatformRef.Name)
		assert.Equal(t, kscp.Spec.PlatformRef.Namespace, ksp.Status.ClusterPlatformRef.PlatformRef.Namespace)
		assert.NotNil(t, kscp.Spec.Capabilities)
		assert.Contains(t, kscp.Spec.Capabilities.Workflows, clusterplatform.PlatformServices)

		assert.NotNil(t, ksp.Status.ClusterPlatformRef)
		assert.Nil(t, ksp.Status.ClusterPlatformRef.Services)

		req2 := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      ksp2.Name,
				Namespace: ksp2.Namespace,
			},
		}
		_, err = r.Reconcile(context.TODO(), req2)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp2.Name, Namespace: ksp2.Namespace}, ksp2))
		assert.True(t, ksp2.Status.IsReady())
		assert.NotNil(t, ksp2.Status.ClusterPlatformRef)
		assert.Equal(t, kscp.Name, ksp2.Status.ClusterPlatformRef.Name)
		assert.Equal(t, kscp.Spec.PlatformRef.Name, ksp2.Status.ClusterPlatformRef.PlatformRef.Name)
		assert.Equal(t, kscp.Spec.PlatformRef.Namespace, ksp2.Status.ClusterPlatformRef.PlatformRef.Namespace)
		assert.NotNil(t, ksp2.Status.ClusterPlatformRef.Services)
		assert.NotNil(t, ksp2.Status.ClusterPlatformRef.Services.DataIndexRef)
		assert.NotEmpty(t, ksp2.Status.ClusterPlatformRef.Services.DataIndexRef.Url)
		assert.NotNil(t, ksp2.Status.ClusterPlatformRef.Services.JobServiceRef)
		assert.NotEmpty(t, ksp2.Status.ClusterPlatformRef.Services.JobServiceRef.Url)

		psDi := services.NewDataIndexHandler(ksp)
		psDi2 := services.NewDataIndexHandler(ksp2)
		assert.Equal(t, ksp2.Status.ClusterPlatformRef.Services.DataIndexRef.Url, psDi.GetLocalServiceBaseUrl())
		assert.Equal(t, psDi.GetLocalServiceBaseUrl()+constants.KogitoProcessInstancesEventsPath, psDi2.GetServiceBaseUrl()+constants.KogitoProcessInstancesEventsPath)
		psJs := services.NewJobServiceHandler(ksp)
		psJs2 := services.NewJobServiceHandler(ksp2)
		assert.Equal(t, ksp2.Status.ClusterPlatformRef.Services.JobServiceRef.Url, psJs.GetLocalServiceBaseUrl())
		assert.Equal(t, psJs.GetLocalServiceBaseUrl()+constants.JobServiceJobEventsPath, psJs2.GetServiceBaseUrl()+constants.JobServiceJobEventsPath)

		ksp2.Spec.Services = &v1alpha08.ServicesPlatformSpec{}

		assert.NoError(t, cl.Update(context.TODO(), ksp2))
		_, err = r.Reconcile(context.TODO(), req2)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp2.Name, Namespace: ksp2.Namespace}, ksp2))
		assert.True(t, ksp2.Status.IsReady())
		assert.NotNil(t, ksp2.Status.ClusterPlatformRef)
		assert.Equal(t, kscp.Spec.PlatformRef.Name, ksp2.Status.ClusterPlatformRef.PlatformRef.Name)
		assert.Equal(t, kscp.Spec.PlatformRef.Namespace, ksp2.Status.ClusterPlatformRef.PlatformRef.Namespace)
		assert.Nil(t, ksp2.Status.ClusterPlatformRef.Services)

		kscp.Spec.Capabilities = &v1alpha08.SonataFlowClusterPlatformCapSpec{}
		assert.NoError(t, cl.Update(context.TODO(), kscp))
		_, err = cr.Reconcile(context.TODO(), cReq)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		_, err = r.Reconcile(context.TODO(), req2)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: kscp.Name}, kscp))
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp2.Name, Namespace: ksp2.Namespace}, ksp2))

		assert.NotNil(t, kscp.Spec.Capabilities)
		assert.Empty(t, kscp.Spec.Capabilities.Workflows)
		assert.NotNil(t, ksp2.Status.ClusterPlatformRef)
		assert.Nil(t, ksp2.Status.ClusterPlatformRef.Services)
	})
	t.Run("verify that knative resources creation for job service and data index service with platform level broker is performed without error", func(t *testing.T) {
		namespace := t.Name()
		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformWithBrokerInReadyPhase(namespace)
		broker := test.GetDefaultBroker(namespace)
		brokerName := broker.Name

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp, broker).WithStatusSubresource(ksp, broker).Build()
		utils.SetClient(cl)
		utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
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
		if err != nil && err.Error() != "waiting for K_SINK injection for service sonataflow-platform-jobs-service to complete" {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.NotNil(t, ksp.Spec.Eventing)
		assert.NotNil(t, ksp.Spec.Eventing.Broker)
		assert.NotNil(t, ksp.Spec.Eventing.Broker.Ref)
		assert.Equal(t, ksp.Spec.Eventing.Broker.Ref.Name, brokerName)
		assert.NotNil(t, ksp.Spec.Services.DataIndex)
		assert.NotNil(t, ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.DataIndex.Enabled)
		assert.NotNil(t, ksp.Spec.Services.JobService)
		assert.NotNil(t, ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)

		assert.Equal(t, "", ksp.Status.GetTopLevelCondition().Reason)

		// Check Triggers
		trigger := &eventingv1.Trigger{}
		validateTrigger(t, cl, "jobs-service-create-job-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "jobs-service-delete-job-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "data-index-jobs-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "jobs-service-create-job-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "data-index-process-definition-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "data-index-process-error-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "data-index-process-node-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "data-index-process-state-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "data-index-process-variable-", ksp.Namespace, ksp, trigger)

		// Check SinkBinding
		sinkBinding := &sourcesv1.SinkBinding{}
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: "sonataflow-platform-jobs-service-sb", Namespace: ksp.Namespace}, sinkBinding))

	})

	t.Run("verify that knative resources creation for job service and data index service  with services level brokers is performed without error", func(t *testing.T) {
		namespace := t.Name()

		// Create a SonataFlowPlatform object with metadata and spec.
		ksp := test.GetBasePlatformWithBrokerInReadyPhase(namespace)
		brokerName := "default"
		brokerNameDataIndexSource := "broker-di-source"
		brokerNameJobsServiceSource := "broker-jobs-source"
		brokerNameJobsServiceSink := "broker-jobs-sink"
		broker := test.GetDefaultBroker(namespace)
		brokerDataIndexSource := test.GetDefaultBroker(namespace)
		brokerDataIndexSource.Name = brokerNameDataIndexSource
		brokerJobsServiceSource := test.GetDefaultBroker(namespace)
		brokerJobsServiceSource.Name = brokerNameJobsServiceSource
		brokerJobsServiceSink := test.GetDefaultBroker(namespace)
		brokerJobsServiceSink.Name = brokerNameJobsServiceSink

		ksp.Spec.Services.DataIndex.Source = &duckv1.Destination{
			Ref: &duckv1.KReference{
				Name:       brokerNameDataIndexSource,
				Namespace:  namespace,
				APIVersion: "eventing.knative.dev/v1",
				Kind:       "Broker",
			},
		}
		ksp.Spec.Services.JobService.Sink = &duckv1.Destination{
			Ref: &duckv1.KReference{
				Name:       brokerNameJobsServiceSink,
				Namespace:  namespace,
				APIVersion: "eventing.knative.dev/v1",
				Kind:       "Broker",
			},
		}
		ksp.Spec.Services.JobService.Source = &duckv1.Destination{
			Ref: &duckv1.KReference{
				Name:       brokerNameJobsServiceSource,
				Namespace:  namespace,
				APIVersion: "eventing.knative.dev/v1",
				Kind:       "Broker",
			},
		}

		// Create a fake client to mock API calls.
		cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(ksp, broker, brokerDataIndexSource, brokerJobsServiceSource, brokerJobsServiceSink).WithStatusSubresource(ksp, broker, brokerDataIndexSource, brokerJobsServiceSource, brokerJobsServiceSink).Build()
		utils.SetClient(cl)
		utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
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
		if err != nil && err.Error() != "waiting for K_SINK injection for service sonataflow-platform-jobs-service to complete" {
			t.Fatalf("reconcile: (%v)", err)
		}

		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: ksp.Name, Namespace: ksp.Namespace}, ksp))

		// Perform some checks on the created CR
		assert.Equal(t, test.CommonImageRegistryAccount, ksp.Spec.Build.Config.Registry.Address)
		assert.Equal(t, "regcred", ksp.Spec.Build.Config.Registry.Secret)
		assert.Equal(t, v1alpha08.OperatorBuildStrategy, ksp.Spec.Build.Config.BuildStrategy)
		assert.NotNil(t, ksp.Spec.Eventing)
		assert.NotNil(t, ksp.Spec.Eventing.Broker)
		assert.NotNil(t, ksp.Spec.Eventing.Broker.Ref)
		assert.Equal(t, ksp.Spec.Eventing.Broker.Ref.Name, brokerName)
		assert.NotNil(t, ksp.Spec.Services.DataIndex)
		assert.NotNil(t, ksp.Spec.Services.DataIndex.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.DataIndex.Enabled)
		assert.NotNil(t, ksp.Spec.Services.DataIndex.Source)
		assert.NotNil(t, ksp.Spec.Services.DataIndex.Source.Ref)
		assert.Equal(t, ksp.Spec.Services.DataIndex.Source.Ref.Name, brokerNameDataIndexSource)
		assert.NotNil(t, ksp.Spec.Services.JobService)
		assert.NotNil(t, ksp.Spec.Services.JobService.Enabled)
		assert.Equal(t, true, *ksp.Spec.Services.JobService.Enabled)
		assert.NotNil(t, ksp.Spec.Services.JobService.Source)
		assert.NotNil(t, ksp.Spec.Services.JobService.Source.Ref)
		assert.Equal(t, ksp.Spec.Services.JobService.Source.Ref.Name, brokerNameJobsServiceSource)
		assert.NotNil(t, ksp.Spec.Services.JobService.Sink)
		assert.NotNil(t, ksp.Spec.Services.JobService.Sink.Ref)
		assert.Equal(t, ksp.Spec.Services.JobService.Sink.Ref.Name, brokerNameJobsServiceSink)
		assert.Equal(t, v1alpha08.PlatformClusterKubernetes, ksp.Status.Cluster)
		assert.Equal(t, "", ksp.Status.GetTopLevelCondition().Reason)

		// Check Triggers to have the service level source used
		trigger := &eventingv1.Trigger{}
		validateTrigger(t, cl, "jobs-service-create-job-", ksp.Namespace, ksp, trigger)
		assert.Equal(t, trigger.Spec.Broker, brokerNameJobsServiceSource)
		validateTrigger(t, cl, "jobs-service-delete-job-", ksp.Namespace, ksp, trigger)
		assert.Equal(t, trigger.Spec.Broker, brokerNameJobsServiceSource)
		validateTrigger(t, cl, "data-index-jobs-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "jobs-service-create-job-", ksp.Namespace, ksp, trigger)
		validateTrigger(t, cl, "data-index-process-definition-", ksp.Namespace, ksp, trigger)
		assert.Equal(t, trigger.Spec.Broker, brokerNameDataIndexSource)
		validateTrigger(t, cl, "data-index-process-error-", ksp.Namespace, ksp, trigger)
		assert.Equal(t, trigger.Spec.Broker, brokerNameDataIndexSource)
		validateTrigger(t, cl, "data-index-process-node-", ksp.Namespace, ksp, trigger)
		assert.Equal(t, trigger.Spec.Broker, brokerNameDataIndexSource)
		validateTrigger(t, cl, "data-index-process-state-", ksp.Namespace, ksp, trigger)
		assert.Equal(t, trigger.Spec.Broker, brokerNameDataIndexSource)
		validateTrigger(t, cl, "data-index-process-variable-", ksp.Namespace, ksp, trigger)
		assert.Equal(t, trigger.Spec.Broker, brokerNameDataIndexSource)

		// Check SinkBinding to have the sink level source used
		sinkBinding := &sourcesv1.SinkBinding{}
		assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: "sonataflow-platform-jobs-service-sb", Namespace: ksp.Namespace}, sinkBinding))
		assert.NotNil(t, sinkBinding.Spec.Sink)
		assert.NotNil(t, sinkBinding.Spec.Sink.Ref)
		assert.Equal(t, sinkBinding.Spec.Sink.Ref.Name, brokerNameJobsServiceSink)
	})
}

func validateTrigger(t *testing.T, cl client.WithWatch, prefix string, namespace string, ksp *v1alpha08.SonataFlowPlatform, trigger *eventingv1.Trigger) {
	assert.NoError(t, cl.Get(context.TODO(), types.NamespacedName{Name: kmeta.ChildName(prefix, string(ksp.GetUID())), Namespace: namespace}, trigger))
}
