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

package common

import (
	"context"
	"testing"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/version"

	"github.com/magiconair/properties"
	prometheus "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	"knative.dev/pkg/kmeta"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
	kubeutil "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/kubernetes"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
)

const platformName = "test-platform"

func Test_ensureWorkflowPropertiesConfigMapMutator(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())
	platform := test.GetBasePlatformInReadyPhase(workflow.Namespace)
	// can't be new
	managedProps, _ := ManagedPropsConfigMapCreator(workflow, platform)
	managedProps.SetUID("1")
	managedProps.SetResourceVersion("1")
	managedPropsCM := managedProps.(*corev1.ConfigMap)

	userProps, _ := UserPropsConfigMapCreator(workflow)
	userPropsCM := userProps.(*corev1.ConfigMap)
	visitor := ManagedPropertiesMutateVisitor(context.TODO(), nil, workflow, platform, userPropsCM)
	mutateFn := visitor(managedProps)

	assert.NoError(t, mutateFn())
	assert.Empty(t, managedPropsCM.Data[workflowproj.ApplicationPropertiesFileName])
	assert.NotEmpty(t, managedPropsCM.Data[workflowproj.GetManagedPropertiesFileName(workflow)])

	props := properties.MustLoadString(managedPropsCM.Data[workflowproj.GetManagedPropertiesFileName(workflow)])
	assert.Equal(t, "8080", props.GetString("quarkus.http.port", ""))

	// we change the properties to something different, we add ours and change the default
	userPropsCM.Data[workflowproj.ApplicationPropertiesFileName] = "quarkus.http.port=9090\nmy.new.prop=1"
	visitor(managedPropsCM)
	assert.NoError(t, mutateFn())

	// we should preserve the default, and still got ours
	props = properties.MustLoadString(managedPropsCM.Data[workflowproj.GetManagedPropertiesFileName(workflow)])
	assert.Equal(t, "8080", props.GetString("quarkus.http.port", ""))
	assert.Equal(t, "0.0.0.0", props.GetString("quarkus.http.host", ""))
	assert.NotContains(t, "my.new.prop", props.Keys())
}

func Test_ensureWorkflowPropertiesConfigMapMutator_DollarReplacement(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())
	platform := test.GetBasePlatformInReadyPhase(workflow.Namespace)

	managedProps, _ := ManagedPropsConfigMapCreator(workflow, platform)
	managedProps.SetName(workflow.Name)
	managedProps.SetNamespace(workflow.Namespace)
	managedProps.SetUID("0000-0001-0002-0003")
	managedPropsCM := managedProps.(*corev1.ConfigMap)

	userProps, _ := UserPropsConfigMapCreator(workflow)
	userPropsCM := userProps.(*corev1.ConfigMap)
	userPropsCM.Data[workflowproj.ApplicationPropertiesFileName] = "mp.messaging.outgoing.kogito_outgoing_stream.url=${kubernetes:services.v1/event-listener}"

	mutateVisitorFn := ManagedPropertiesMutateVisitor(context.TODO(), nil, workflow, platform, userPropsCM)

	err := mutateVisitorFn(managedPropsCM)()
	assert.NoError(t, err)
	assert.NotContains(t, managedPropsCM.Data[workflowproj.GetManagedPropertiesFileName(workflow)], "mp.messaging.outgoing.kogito_outgoing_stream.url")
}

func TestMergePodSpec(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	workflow.Spec.PodTemplate = v1alpha08.FlowPodTemplateSpec{
		Container: v1alpha08.ContainerSpec{
			// this one we can override
			Image: test.CommonImageTag,
			Ports: []corev1.ContainerPort{
				// let's override a immutable attribute
				{Name: utils.DefaultServicePortName, ContainerPort: 9090},
			},
			Env: []corev1.EnvVar{
				// We should be able to override this too
				{Name: "ENV1", Value: "VALUE_CUSTOM"},
			},
			VolumeMounts: []corev1.VolumeMount{
				{Name: "myvolume", ReadOnly: true, MountPath: "/tmp/any/path"},
			},
		},
		PodSpec: v1alpha08.PodSpec{
			ServiceAccountName: "superuser",
			Containers: []corev1.Container{
				{
					Name: "sidecar",
				},
			},
			Volumes: []corev1.Volume{
				{
					Name: "myvolume",
					VolumeSource: corev1.VolumeSource{
						ConfigMap: &corev1.ConfigMapVolumeSource{
							LocalObjectReference: corev1.LocalObjectReference{Name: "customproperties"},
						},
					},
				},
			},
		},
	}

	object, err := DeploymentCreator(workflow, nil)
	assert.NoError(t, err)

	deployment := object.(*appsv1.Deployment)

	assert.Len(t, deployment.Spec.Template.Spec.Containers, 2)
	assert.Equal(t, "superuser", deployment.Spec.Template.Spec.ServiceAccountName)
	assert.Len(t, deployment.Spec.Template.Spec.Volumes, 1)
	flowContainer, _ := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, &deployment.Spec.Template.Spec)
	assert.Equal(t, test.CommonImageTag, flowContainer.Image)
	assert.Equal(t, int32(8080), flowContainer.Ports[0].ContainerPort)
	assert.Equal(t, "VALUE_CUSTOM", flowContainer.Env[0].Value)
	assert.Len(t, flowContainer.VolumeMounts, 1)
}

func TestMergePodSpecOverrideContainers(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	workflow.Spec.PodTemplate = v1alpha08.FlowPodTemplateSpec{
		PodSpec: v1alpha08.PodSpec{
			// Try to override the workflow container via the podspec
			Containers: []corev1.Container{
				{
					Name:  v1alpha08.DefaultContainerName,
					Image: test.CommonImageTag,
					Ports: []corev1.ContainerPort{
						{Name: utils.DefaultServicePortName, ContainerPort: 9090},
					},
					Env: []corev1.EnvVar{
						{Name: "ENV1", Value: "VALUE_CUSTOM"},
					},
				},
			},
		},
	}

	object, err := DeploymentCreator(workflow, nil)
	assert.NoError(t, err)

	deployment := object.(*appsv1.Deployment)

	assert.Len(t, deployment.Spec.Template.Spec.Containers, 1)
	flowContainer, _ := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, &deployment.Spec.Template.Spec)
	assert.NotEqual(t, test.CommonImageTag, flowContainer.Image)
	assert.Equal(t, int32(8080), flowContainer.Ports[0].ContainerPort)
	assert.Empty(t, flowContainer.Env)
}

func TestEnsureWorkflowSinkBindingWithWorkflowSinkIsCreated(t *testing.T) {
	workflow := test.GetVetEventSonataFlow(t.Name())
	plf := test.GetBasePlatform()
	//On Kubernetes we want the service exposed in Dev with NodePort
	sinkBinding, err := SinkBindingCreator(workflow, plf)
	assert.NoError(t, err)
	assert.NotNil(t, sinkBinding)
	sinkBinding.SetUID("1")
	sinkBinding.SetResourceVersion("1")

	reflectSinkBinding := sinkBinding.(*sourcesv1.SinkBinding)

	assert.NotNil(t, reflectSinkBinding)
	assert.NotNil(t, reflectSinkBinding.Spec)
	assert.NotEmpty(t, reflectSinkBinding.Spec.Sink)
	assert.Equal(t, reflectSinkBinding.Spec.Sink.Ref.Kind, "Broker")
	assert.Equal(t, reflectSinkBinding.Spec.Sink.Ref.Name, "default")
	assert.NotNil(t, reflectSinkBinding.GetLabels())
	assert.Equal(t, reflectSinkBinding.ObjectMeta.Labels, map[string]string{
		"app":                               "vet",
		"sonataflow.org/workflow-app":       "vet",
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/name":            "vet",
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
		"app.kubernetes.io/instance":        "vet",
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator"})
}

func TestEnsureWorkflowSinkBindingWithPlatformBrokerIsCreated(t *testing.T) {
	workflow := test.GetVetEventSonataFlow(t.Name())
	workflow.Spec.Sink = nil
	workflow.Spec.Sources = nil
	plf := test.GetBasePlatformWithBroker()
	sinkBinding, err := SinkBindingCreator(workflow, plf)
	assert.NoError(t, err)
	assert.NotNil(t, sinkBinding)
	sinkBinding.SetUID("1")
	sinkBinding.SetResourceVersion("1")

	reflectSinkBinding := sinkBinding.(*sourcesv1.SinkBinding)

	assert.NotNil(t, reflectSinkBinding)
	assert.NotNil(t, reflectSinkBinding.Spec)
	assert.NotEmpty(t, reflectSinkBinding.Spec.Sink)
	assert.Equal(t, reflectSinkBinding.Spec.Sink.Ref.Kind, "Broker")
	assert.Equal(t, reflectSinkBinding.Spec.Sink.Ref.Name, "default")
	assert.NotNil(t, reflectSinkBinding.GetLabels())
	assert.Equal(t, reflectSinkBinding.ObjectMeta.Labels, map[string]string{"app": "vet",
		"sonataflow.org/workflow-app":       "vet",
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/name":            "vet",
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
		"app.kubernetes.io/instance":        "vet",
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator"})
}

func TestEnsureWorkflowSinkBindingWithoutBrokerAreNotCreated(t *testing.T) {
	workflow := test.GetVetEventSonataFlow(t.Name())
	workflow.Spec.Sink = nil
	workflow.Spec.Sources = nil
	plf := test.GetBasePlatformWithBroker()
	plf.Spec.Eventing = nil // No broker configured in the platform, but data index and jobs service are enabled
	sinkBinding, err := SinkBindingCreator(workflow, plf)
	assert.NoError(t, err)
	assert.Nil(t, sinkBinding)
}

func getTrigger(name string, objs []client.Object) *eventingv1.Trigger {
	for _, obj := range objs {
		if trigger, ok := obj.(*eventingv1.Trigger); ok {
			if trigger.Name == name {
				return trigger
			}
		}
	}
	return nil
}

func TestEnsureWorkflowTriggersWithPlatformBrokerAreCreated(t *testing.T) {
	workflow := test.GetVetEventSonataFlow(t.Name())
	workflow.Spec.Sink = nil
	workflow.Spec.Sources = nil
	plf := test.GetBasePlatformWithBroker()
	plf.Namespace = "platform-namespace"
	plf.Spec.Eventing.Broker.Ref.Namespace = plf.Namespace
	broker := test.GetDefaultBroker(plf.Namespace)

	// Create a fake client to mock API calls.
	cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(workflow, broker).WithStatusSubresource(workflow, broker).Build()
	utils.SetClient(cl)

	triggers, err := TriggersCreator(workflow, plf)
	assert.NoError(t, err)
	assert.NotEmpty(t, triggers)
	assert.Len(t, triggers, 2)
	//Check the 1st trigger
	name := kmeta.ChildName("vet-vetappointmentrequestreceived-", string(workflow.GetUID()))
	trigger := getTrigger(name, triggers)
	assert.NotNil(t, trigger)
	assert.NotNil(t, trigger.GetLabels())
	assert.Equal(t, trigger.GetLabels(), map[string]string{"app": "vet",
		"sonataflow.org/workflow-app":       "vet",
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/name":            "vet",
		"app.kubernetes.io/instance":        "vet",
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator"})
	assert.Equal(t, trigger.Namespace, plf.Namespace) //trigger should be in the platform namespace
	assert.Equal(t, trigger.Spec.Broker, "default")
	assert.NotNil(t, trigger.Spec.Filter)
	assert.Len(t, trigger.Spec.Filter.Attributes, 1)
	assert.Equal(t, trigger.Spec.Filter.Attributes["type"], "events.vet.appointments.request")
	//Check the 2nd trigger
	name = kmeta.ChildName("vet-vetappointmentinfo-", string(workflow.GetUID()))
	trigger = getTrigger(name, triggers)
	assert.NotNil(t, trigger)
	assert.NotNil(t, trigger.GetLabels())
	assert.Equal(t, trigger.GetLabels(), map[string]string{"app": "vet",
		"sonataflow.org/workflow-app":       "vet",
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/name":            "vet",
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
		"app.kubernetes.io/instance":        "vet",
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator"})
	assert.Equal(t, trigger.Namespace, plf.Namespace) //trigger should be in the platform namespace
	assert.Equal(t, trigger.Spec.Broker, "default")
	assert.NotNil(t, trigger.Spec.Filter)
	assert.Len(t, trigger.Spec.Filter.Attributes, 1)
	assert.Equal(t, trigger.Spec.Filter.Attributes["type"], "events.vet.appointments")
}

func TestEnsureWorkflowTriggersWithWorkflowBrokerAreCreated(t *testing.T) {
	workflow := test.GetVetEventSonataFlow(t.Name())
	workflow.Spec.Sources[0].Destination.Ref.Namespace = workflow.Namespace
	workflow.Spec.Sources[1].Destination.Ref.Namespace = workflow.Namespace
	plf := test.GetBasePlatform() // No broker defined in the platform
	broker1 := test.GetDefaultBroker(workflow.Namespace)
	broker1.Name = "broker-appointments-request"
	broker2 := test.GetDefaultBroker(workflow.Namespace)
	broker2.Name = "broker-appointments"
	// Create a fake client to mock API calls.
	cl := test.NewKogitoClientBuilderWithOpenShift().WithRuntimeObjects(workflow, plf, broker1, broker2).WithStatusSubresource(workflow, plf, broker1, broker2).Build()
	utils.SetClient(cl)

	triggers, err := TriggersCreator(workflow, plf)
	assert.NoError(t, err)
	assert.NotEmpty(t, triggers)
	assert.Len(t, triggers, 2)
	//Check the 1st trigger
	name := kmeta.ChildName("vet-vetappointmentrequestreceived-", string(workflow.GetUID()))

	trigger := getTrigger(name, triggers)
	assert.NotNil(t, trigger)
	assert.NotNil(t, trigger.GetLabels())
	assert.Equal(t, trigger.GetLabels(), map[string]string{"app": "vet",
		"sonataflow.org/workflow-app":       "vet",
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
		"app.kubernetes.io/instance":        "vet",
		"app.kubernetes.io/name":            "vet",
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator"})
	assert.Equal(t, trigger.Namespace, workflow.Namespace) //trigger should be in the workflow namespace
	assert.Equal(t, trigger.Spec.Broker, "broker-appointments-request")
	assert.NotNil(t, trigger.Spec.Filter)
	assert.Len(t, trigger.Spec.Filter.Attributes, 1)
	assert.Equal(t, trigger.Spec.Filter.Attributes["type"], "events.vet.appointments.request")
	//Check the 2nd trigger
	name = kmeta.ChildName("vet-vetappointmentinfo-", string(workflow.GetUID()))
	trigger = getTrigger(name, triggers)
	assert.NotNil(t, trigger)
	assert.NotNil(t, trigger.GetLabels())
	assert.Equal(t, trigger.GetLabels(), map[string]string{"app": "vet",
		"sonataflow.org/workflow-app":       "vet",
		"app.kubernetes.io/instance":        "vet",
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/name":            "vet",
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator"})
	assert.Equal(t, trigger.Namespace, workflow.Namespace) //trigger should be in the workflow namespace
	assert.Equal(t, trigger.Spec.Broker, "broker-appointments")
	assert.NotNil(t, trigger.Spec.Filter)
	assert.Len(t, trigger.Spec.Filter.Attributes, 1)
	assert.Equal(t, trigger.Spec.Filter.Attributes["type"], "events.vet.appointments")
}

func TestEnsureWorkflowTriggersWithoutBrokerAreNotCreated(t *testing.T) {
	workflow := test.GetVetEventSonataFlow(t.Name())
	workflow.Spec.Sink = nil
	workflow.Spec.Sources = nil
	plf := test.GetBasePlatform()

	triggers, err := TriggersCreator(workflow, plf)
	assert.NoError(t, err)
	assert.Nil(t, triggers)
}

func TestMergePodSpec_WithPostgreSQL_and_JDBC_URL_field(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	workflow.Spec = v1alpha08.SonataFlowSpec{
		PodTemplate: v1alpha08.FlowPodTemplateSpec{
			Container: v1alpha08.ContainerSpec{
				// this one we can override
				Image: test.CommonImageTag,
				Ports: []corev1.ContainerPort{
					// let's override a immutable attribute
					{Name: utils.DefaultServicePortName, ContainerPort: 9090},
				},
				Env: []corev1.EnvVar{
					// We should be able to override this too
					{Name: "ENV1", Value: "VALUE_CUSTOM"},
				},
				VolumeMounts: []corev1.VolumeMount{
					{Name: "myvolume", ReadOnly: true, MountPath: "/tmp/any/path"},
				},
			},
			PodSpec: v1alpha08.PodSpec{
				ServiceAccountName: "superuser",
				Containers: []corev1.Container{
					{
						Name: "sidecar",
					},
				},
				Volumes: []corev1.Volume{
					{
						Name: "myvolume",
						VolumeSource: corev1.VolumeSource{
							ConfigMap: &corev1.ConfigMapVolumeSource{
								LocalObjectReference: corev1.LocalObjectReference{Name: "customproperties"},
							},
						},
					},
				},
			},
		},
		Persistence: &v1alpha08.PersistenceOptionsSpec{
			PostgreSQL: &v1alpha08.PersistencePostgreSQL{
				SecretRef: v1alpha08.PostgreSQLSecretOptions{Name: "test"},
				JdbcUrl:   "jdbc:postgresql://host:port/database?currentSchema=workflow",
			},
		},
	}

	object, err := DeploymentCreator(workflow, nil)
	assert.NoError(t, err)

	deployment := object.(*appsv1.Deployment)
	expectedEnvVars := []corev1.EnvVar{
		{
			Name:  "ENV1",
			Value: "VALUE_CUSTOM",
		},
		{
			Name:  "QUARKUS_DATASOURCE_USERNAME",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "test"}, Key: "POSTGRESQL_USER",
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_PASSWORD",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "test"}, Key: "POSTGRESQL_PASSWORD",
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: "postgresql",
		},
		{
			Name:  "QUARKUS_DATASOURCE_JDBC_URL",
			Value: "jdbc:postgresql://host:port/database?currentSchema=workflow",
		},
		{
			Name:  "KOGITO_PERSISTENCE_TYPE",
			Value: "jdbc",
		},
	}
	assert.Len(t, deployment.Spec.Template.Spec.Containers, 2)
	assert.Equal(t, "superuser", deployment.Spec.Template.Spec.ServiceAccountName)
	assert.Len(t, deployment.Spec.Template.Spec.Volumes, 1)
	flowContainer, _ := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, &deployment.Spec.Template.Spec)
	assert.Equal(t, test.CommonImageTag, flowContainer.Image)
	assert.Equal(t, int32(8080), flowContainer.Ports[0].ContainerPort)
	assert.Equal(t, expectedEnvVars, flowContainer.Env)
	assert.Len(t, flowContainer.VolumeMounts, 1)
}

var (
	postgreSQLPort = 5432
)

func TestMergePodSpec_OverrideContainers_WithPostgreSQL_In_Workflow_CR(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	workflow.Spec = v1alpha08.SonataFlowSpec{
		PodTemplate: v1alpha08.FlowPodTemplateSpec{
			PodSpec: v1alpha08.PodSpec{
				// Try to override the workflow container via the podspec
				Containers: []corev1.Container{
					{
						Name:  v1alpha08.DefaultContainerName,
						Image: test.CommonImageTag,
						Ports: []corev1.ContainerPort{
							{Name: utils.DefaultServicePortName, ContainerPort: 9090},
						},
						Env: []corev1.EnvVar{
							{Name: "ENV1", Value: "VALUE_CUSTOM"},
						},
					},
				},
			},
		},
		Persistence: &v1alpha08.PersistenceOptionsSpec{
			PostgreSQL: &v1alpha08.PersistencePostgreSQL{
				SecretRef: v1alpha08.PostgreSQLSecretOptions{Name: "test"},
				ServiceRef: &v1alpha08.PostgreSQLServiceOptions{
					SQLServiceOptions: &v1alpha08.SQLServiceOptions{
						Name:         "test",
						Namespace:    "foo",
						Port:         &postgreSQLPort,
						DatabaseName: "petstore"},
					DatabaseSchema: "bar"},
			},
		},
	}

	object, err := DeploymentCreator(workflow, nil)
	assert.NoError(t, err)

	deployment := object.(*appsv1.Deployment)
	expectedEnvVars := []corev1.EnvVar{
		{
			Name:  "QUARKUS_DATASOURCE_USERNAME",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "test"}, Key: "POSTGRESQL_USER",
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_PASSWORD",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "test"}, Key: "POSTGRESQL_PASSWORD",
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: "postgresql",
		},
		{
			Name:  "QUARKUS_DATASOURCE_JDBC_URL",
			Value: "jdbc:postgresql://test.foo:5432/petstore?currentSchema=bar",
		},
		{
			Name:  "KOGITO_PERSISTENCE_TYPE",
			Value: "jdbc",
		},
	}
	assert.Len(t, deployment.Spec.Template.Spec.Containers, 1)
	flowContainer, _ := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, &deployment.Spec.Template.Spec)
	assert.Empty(t, flowContainer.Image)
	assert.Equal(t, int32(8080), flowContainer.Ports[0].ContainerPort)
	assert.Equal(t, expectedEnvVars, flowContainer.Env)
}

func TestMergePodSpec_WithServicedPostgreSQL_In_Platform_CR_And_Worflow_Requesting_It(t *testing.T) {
	p := &v1alpha08.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "foo",
			Namespace: "default",
		},
		Spec: v1alpha08.SonataFlowPlatformSpec{
			Persistence: &v1alpha08.PlatformPersistenceOptionsSpec{
				PostgreSQL: &v1alpha08.PlatformPersistencePostgreSQL{
					SecretRef: v1alpha08.PostgreSQLSecretOptions{
						Name:        "foo_secret",
						UserKey:     "username",
						PasswordKey: "password",
					},
					ServiceRef: &v1alpha08.SQLServiceOptions{
						Name:         "service_name",
						Namespace:    "service_namespace",
						Port:         &postgreSQLPort,
						DatabaseName: "foo",
					},
				},
			},
		},
	}

	workflow := test.GetBaseSonataFlow(t.Name())
	workflow.Spec = v1alpha08.SonataFlowSpec{
		Persistence: nil,
	}
	object, err := DeploymentCreator(workflow, p)
	assert.NoError(t, err)

	deployment := object.(*appsv1.Deployment)
	expectedEnvVars := []corev1.EnvVar{
		{
			Name:  "QUARKUS_DATASOURCE_USERNAME",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "foo_secret"}, Key: "username",
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_PASSWORD",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "foo_secret"}, Key: "password",
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: "postgresql",
		},
		{
			Name:  "QUARKUS_DATASOURCE_JDBC_URL",
			Value: "jdbc:postgresql://service_name.service_namespace:5432/foo?currentSchema=greeting",
		},
		{
			Name:  "KOGITO_PERSISTENCE_TYPE",
			Value: "jdbc",
		},
	}
	assert.Len(t, deployment.Spec.Template.Spec.Containers, 1)
	flowContainer, _ := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, &deployment.Spec.Template.Spec)
	assert.Empty(t, flowContainer.Image)
	assert.Equal(t, int32(8080), flowContainer.Ports[0].ContainerPort)
	assert.Equal(t, expectedEnvVars, flowContainer.Env)
}

func TestMergePodSpec_WithServicedPostgreSQL_In_Platform_And_In_Workflow_CR(t *testing.T) {

	p := &v1alpha08.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "foo",
			Namespace: "default",
		},
		Spec: v1alpha08.SonataFlowPlatformSpec{
			Persistence: &v1alpha08.PlatformPersistenceOptionsSpec{
				PostgreSQL: &v1alpha08.PlatformPersistencePostgreSQL{
					SecretRef: v1alpha08.PostgreSQLSecretOptions{
						Name:        "foo_secret",
						UserKey:     "username",
						PasswordKey: "password",
					},
					ServiceRef: &v1alpha08.SQLServiceOptions{
						Name:         "service_name",
						Namespace:    "service_namespace",
						Port:         &postgreSQLPort,
						DatabaseName: "foo",
					},
				},
			},
		},
	}
	workflow := test.GetBaseSonataFlow(t.Name())
	workflow.Spec = v1alpha08.SonataFlowSpec{
		PodTemplate: v1alpha08.FlowPodTemplateSpec{
			PodSpec: v1alpha08.PodSpec{
				// Try to override the workflow container via the podspec
				Containers: []corev1.Container{
					{
						Name:  v1alpha08.DefaultContainerName,
						Image: test.CommonImageTag,
						Ports: []corev1.ContainerPort{
							{Name: utils.DefaultServicePortName, ContainerPort: 9090},
						},
						Env: []corev1.EnvVar{
							{Name: "ENV1", Value: "VALUE_CUSTOM"},
						},
					},
				},
			},
		},
		Persistence: &v1alpha08.PersistenceOptionsSpec{
			PostgreSQL: &v1alpha08.PersistencePostgreSQL{
				SecretRef: v1alpha08.PostgreSQLSecretOptions{Name: "test"},
				ServiceRef: &v1alpha08.PostgreSQLServiceOptions{
					SQLServiceOptions: &v1alpha08.SQLServiceOptions{
						Name:         "test",
						Namespace:    "default",
						Port:         &postgreSQLPort,
						DatabaseName: "my_database"},
					DatabaseSchema: "bar"},
			},
		},
	}
	object, err := DeploymentCreator(workflow, p)
	assert.NoError(t, err)

	deployment := object.(*appsv1.Deployment)
	expectedEnvVars := []corev1.EnvVar{
		{
			Name:  "QUARKUS_DATASOURCE_USERNAME",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "test"}, Key: "POSTGRESQL_USER",
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_PASSWORD",
			Value: "",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					LocalObjectReference: corev1.LocalObjectReference{Name: "test"}, Key: "POSTGRESQL_PASSWORD",
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: "postgresql",
		},
		{
			Name:  "QUARKUS_DATASOURCE_JDBC_URL",
			Value: "jdbc:postgresql://test.default:5432/my_database?currentSchema=bar",
		},
		{
			Name:  "KOGITO_PERSISTENCE_TYPE",
			Value: "jdbc",
		},
	}
	assert.Len(t, deployment.Spec.Template.Spec.Containers, 1)
	flowContainer, _ := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, &deployment.Spec.Template.Spec)
	assert.Empty(t, flowContainer.Image)
	assert.Equal(t, int32(8080), flowContainer.Ports[0].ContainerPort)
	assert.Equal(t, expectedEnvVars, flowContainer.Env)
}

func TestMergePodSpec_WithServicedPostgreSQL_In_Platform_But_Workflow_CR_Not_Requesting_it(t *testing.T) {
	p := &v1alpha08.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "foo",
			Namespace: "default",
		},
		Spec: v1alpha08.SonataFlowPlatformSpec{
			Persistence: &v1alpha08.PlatformPersistenceOptionsSpec{
				PostgreSQL: &v1alpha08.PlatformPersistencePostgreSQL{
					SecretRef: v1alpha08.PostgreSQLSecretOptions{
						Name:        "foo_secret",
						UserKey:     "username",
						PasswordKey: "password",
					},
					ServiceRef: &v1alpha08.SQLServiceOptions{
						Name:         "service_name",
						Namespace:    "service_namespace",
						Port:         &postgreSQLPort,
						DatabaseName: "foo",
					},
				},
			},
		},
	}
	workflow := test.GetBaseSonataFlow(t.Name())
	workflow.Spec = v1alpha08.SonataFlowSpec{
		Persistence: &v1alpha08.PersistenceOptionsSpec{},
	}
	object, err := DeploymentCreator(workflow, p)
	assert.NoError(t, err)

	deployment := object.(*appsv1.Deployment)
	assert.Len(t, deployment.Spec.Template.Spec.Containers, 1)
	flowContainer, _ := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, &deployment.Spec.Template.Spec)
	assert.Empty(t, flowContainer.Image)
	assert.Equal(t, int32(8080), flowContainer.Ports[0].ContainerPort)
	assert.Nil(t, flowContainer.Env)
}

func TestDefaultContainer_WithPlatformPersistenceWorkflowWithDefaultProfile(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	doTestDefaultContainer_WithPlatformPersistence(t, workflow, true)
}

func TestDefaultContainer_WithPlatformPersistenceWorkflowWithPreviewProfile(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	workflowproj.SetWorkflowProfile(workflow, metadata.PreviewProfile)
	doTestDefaultContainer_WithPlatformPersistence(t, workflow, true)
}

func TestDefaultContainer_WithPlatformPersistenceWorkflowWithGitOpsProfile(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	workflowproj.SetWorkflowProfile(workflow, metadata.GitOpsProfile)
	doTestDefaultContainer_WithPlatformPersistence(t, workflow, true)
}

func TestDefaultContainer_WithPlatformPersistenceWorkflowWithDevProfile(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	workflowproj.SetWorkflowProfile(workflow, metadata.DevProfile)
	doTestDefaultContainer_WithPlatformPersistence(t, workflow, false)
}

func doTestDefaultContainer_WithPlatformPersistence(t *testing.T, workflow *v1alpha08.SonataFlow, checkPersistence bool) {
	platform := &v1alpha08.SonataFlowPlatform{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "foo",
			Namespace: "default",
		},
		Spec: v1alpha08.SonataFlowPlatformSpec{
			Persistence: &v1alpha08.PlatformPersistenceOptionsSpec{
				PostgreSQL: &v1alpha08.PlatformPersistencePostgreSQL{
					SecretRef: v1alpha08.PostgreSQLSecretOptions{
						Name:        "foo_secret",
						UserKey:     "username",
						PasswordKey: "password",
					},
					ServiceRef: &v1alpha08.SQLServiceOptions{
						Name:         "service_name",
						Namespace:    "service_namespace",
						Port:         &postgreSQLPort,
						DatabaseName: "foo",
					},
				},
			},
		},
	}

	container, err := defaultContainer(workflow, platform)
	assert.Nil(t, err)
	assert.NotNil(t, container)
	assert.Equal(t, "workflow", container.Name)

	//verify default container port.
	assert.Equal(t, 1, len(container.Ports))
	assert.Equal(t, "h2c", container.Ports[0].Name)
	assert.Equal(t, int32(0), container.Ports[0].HostPort)
	assert.Equal(t, int32(8080), container.Ports[0].ContainerPort)
	assert.Equal(t, corev1.Protocol("TCP"), container.Ports[0].Protocol)
	assert.Equal(t, "", container.Ports[0].HostIP)

	//verify default container health checks
	assert.Equal(t, &corev1.Probe{
		ProbeHandler: corev1.ProbeHandler{
			Exec: nil,
			HTTPGet: &corev1.HTTPGetAction{
				Path: "/q/health/live",
				Port: intstr.IntOrString{
					Type:   0,
					IntVal: 8080,
					StrVal: "",
				},
				Host:        "",
				Scheme:      "",
				HTTPHeaders: nil,
			},
			TCPSocket: nil,
			GRPC:      nil,
		},
		InitialDelaySeconds:           0,
		TimeoutSeconds:                3,
		PeriodSeconds:                 15,
		SuccessThreshold:              0,
		FailureThreshold:              0,
		TerminationGracePeriodSeconds: nil,
	}, container.LivenessProbe)

	assert.Equal(t, &corev1.Probe{
		ProbeHandler: corev1.ProbeHandler{
			Exec: nil,
			HTTPGet: &corev1.HTTPGetAction{
				Path: "/q/health/ready",
				Port: intstr.IntOrString{
					Type:   0,
					IntVal: 8080,
					StrVal: "",
				},
				Host:        "",
				Scheme:      "",
				HTTPHeaders: nil,
			},
			TCPSocket: nil,
			GRPC:      nil,
		},
		InitialDelaySeconds:           0,
		TimeoutSeconds:                3,
		PeriodSeconds:                 15,
		SuccessThreshold:              0,
		FailureThreshold:              0,
		TerminationGracePeriodSeconds: nil,
	}, container.ReadinessProbe)

	assert.Equal(t, &corev1.Probe{
		ProbeHandler: corev1.ProbeHandler{
			Exec: nil,
			HTTPGet: &corev1.HTTPGetAction{
				Path: "/q/health/started",
				Port: intstr.IntOrString{
					Type:   0,
					IntVal: 8080,
					StrVal: "",
				},
				Host:        "",
				Scheme:      "",
				HTTPHeaders: nil,
			},
			TCPSocket: nil,
			GRPC:      nil,
		},
		InitialDelaySeconds:           10,
		TimeoutSeconds:                3,
		PeriodSeconds:                 15,
		SuccessThreshold:              0,
		FailureThreshold:              5,
		TerminationGracePeriodSeconds: nil,
	}, container.StartupProbe)

	//verify the persistence configuration is present if requested.
	if checkPersistence {
		expectedEnvVars := []corev1.EnvVar{
			{
				Name:  "QUARKUS_DATASOURCE_USERNAME",
				Value: "",
				ValueFrom: &corev1.EnvVarSource{
					SecretKeyRef: &corev1.SecretKeySelector{
						LocalObjectReference: corev1.LocalObjectReference{Name: "foo_secret"}, Key: "username",
					},
				},
			},
			{
				Name:  "QUARKUS_DATASOURCE_PASSWORD",
				Value: "",
				ValueFrom: &corev1.EnvVarSource{
					SecretKeyRef: &corev1.SecretKeySelector{
						LocalObjectReference: corev1.LocalObjectReference{Name: "foo_secret"}, Key: "password",
					},
				},
			},
			{
				Name:  "QUARKUS_DATASOURCE_DB_KIND",
				Value: "postgresql",
			},
			{
				Name:  "QUARKUS_DATASOURCE_JDBC_URL",
				Value: "jdbc:postgresql://service_name.service_namespace:5432/foo?currentSchema=greeting",
			},
			{
				Name:  "KOGITO_PERSISTENCE_TYPE",
				Value: "jdbc",
			},
		}
		assert.Equal(t, expectedEnvVars, container.Env)
	} else {
		//no persistence
		assert.Nil(t, container.Env)
	}
}

func TestEnsureWorkflowServiceMonitorIsCreatedWhenDeployedAsDeployment(t *testing.T) {
	workflow := test.GetVetEventSonataFlow(t.Name())
	assert.Equal(t, workflow.IsKnativeDeployment(), false)
	serviceMonitor, err := ServiceMonitorCreator(workflow)
	assert.NoError(t, err)
	assert.NotNil(t, serviceMonitor)
	serviceMonitor.SetUID("1")
	serviceMonitor.SetResourceVersion("1")
	reflectServiceMonitor := serviceMonitor.(*prometheus.ServiceMonitor)

	assert.NotNil(t, reflectServiceMonitor)
	assert.NotNil(t, reflectServiceMonitor.Spec)
	assert.Equal(t, len(reflectServiceMonitor.Spec.Selector.MatchLabels), 2)
	assert.Equal(t, reflectServiceMonitor.Spec.Selector.MatchLabels[workflowproj.LabelWorkflow], workflow.Name)
	assert.Equal(t, reflectServiceMonitor.Spec.Selector.MatchLabels[workflowproj.LabelWorkflowNamespace], workflow.Namespace)
	assert.Equal(t, reflectServiceMonitor.Spec.Endpoints[0].Port, k8sServicePortName)
	assert.Equal(t, reflectServiceMonitor.Spec.Endpoints[0].Path, metricsServicePortPath)
	assert.NotNil(t, reflectServiceMonitor.GetLabels())
	assert.Equal(t, reflectServiceMonitor.ObjectMeta.Labels, map[string]string{
		"app":                               workflow.Name,
		"sonataflow.org/workflow-app":       workflow.Name,
		"sonataflow.org/workflow-namespace": workflow.Namespace,
		"app.kubernetes.io/name":            workflow.Name,
		"app.kubernetes.io/instance":        workflow.Name,
		"app.kubernetes.io/version":         version.GetImageTagVersion(),
		"app.kubernetes.io/component":       "serverless-workflow",
		"app.kubernetes.io/managed-by":      "sonataflow-operator"})
}
