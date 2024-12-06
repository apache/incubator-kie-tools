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

package dev

import (
	"context"
	"sort"
	"testing"

	"k8s.io/client-go/rest"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"

	corev1 "k8s.io/api/core/v1"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	kubeutil "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/kubernetes"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/workflowdef"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"

	"github.com/stretchr/testify/assert"

	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/intstr"
	clientruntime "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
)

func Test_OverrideStartupProbe(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow).WithStatusSubresource(workflow).Build()

	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	devReconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// get the deployment, change the probe and reconcile it again
	newThreshold := int32(5) //yes we have to force the type for the assertion below
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, cfg.GetCfg().HealthFailureThresholdDevMode, deployment.Spec.Template.Spec.Containers[0].StartupProbe.FailureThreshold)
	deployment.Spec.Template.Spec.Containers[0].StartupProbe.FailureThreshold = newThreshold
	assert.NoError(t, client.Update(context.TODO(), deployment))
	// reconcile and fetch from the cluster
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)
	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, newThreshold, deployment.Spec.Template.Spec.Containers[0].StartupProbe.FailureThreshold)
}

func Test_recoverFromFailureNoDeployment(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())
	workflowID := clientruntime.ObjectKeyFromObject(workflow)

	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, "")
	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow).WithStatusSubresource(workflow).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())
	reconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	// we are in failed state and have no objects
	result, err := reconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// the recover state tried to clear the conditions of our workflow, so we can try reconciling it again
	workflow = test.MustGetWorkflow(t, client, workflowID)
	assert.True(t, workflow.Status.GetTopLevelCondition().IsUnknown())
	result, err = reconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// the deployment should be there
	_ = test.MustGetDeployment(t, client, workflow)

	// we failed again, but now we have the deployment
	workflow = test.MustGetWorkflow(t, client, workflowID)
	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, "")
	err = client.Status().Update(context.TODO(), workflow)
	assert.NoError(t, err)
	// the fake client won't update the deployment status condition since we don't have a deployment controller
	// our state will think that we don't have a deployment available yet, so it will try to reset the pods
	result, err = reconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	workflow = test.MustGetWorkflow(t, client, workflowID)
	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, "")
	assert.Equal(t, 1, workflow.Status.RecoverFailureAttempts)

	deployment := test.MustGetDeployment(t, client, workflow)
	assert.NotEmpty(t, deployment.Spec.Template.ObjectMeta.Annotations[metadata.RestartedAt])
}

func Test_newDevProfile(t *testing.T) {
	workflow := test.GetBaseSonataFlow(t.Name())

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow).WithStatusSubresource(workflow).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	devReconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	sortVolumeMounts(&deployment.Spec.Template.Spec.Containers[0])
	assert.Equal(t, workflowdef.GetDefaultWorkflowDevModeImageTag(), deployment.Spec.Template.Spec.Containers[0].Image)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].LivenessProbe)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].ReadinessProbe)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].StartupProbe)

	defCM := test.MustGetConfigMap(t, client, workflow)
	assert.NotEmpty(t, defCM.Data[workflow.Name+workflowdef.KogitoWorkflowJSONFileExt])
	assert.Equal(t, quarkusDevConfigMountPath, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].MountPath)
	assert.Equal(t, "", deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].SubPath) //https://kubernetes.io/docs/concepts/configuration/configmap/#mounted-configmaps-are-updated-automatically

	userPropsCM := &corev1.ConfigMap{}
	_ = client.Get(context.TODO(), types.NamespacedName{Namespace: workflow.Namespace, Name: workflowproj.GetWorkflowUserPropertiesConfigMapName(workflow)}, userPropsCM)
	assert.Empty(t, userPropsCM.Data[workflowproj.ApplicationPropertiesFileName])

	managedPropsCM := &corev1.ConfigMap{}
	_ = client.Get(context.TODO(), types.NamespacedName{Namespace: workflow.Namespace, Name: workflowproj.GetWorkflowManagedPropertiesConfigMapName(workflow)}, managedPropsCM)
	assert.NotEmpty(t, managedPropsCM.Data[workflowproj.GetManagedPropertiesFileName(workflow)])
	assert.Equal(t, quarkusDevConfigMountPath, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].MountPath)
	assert.Equal(t, "", deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].SubPath) //https://kubernetes.io/docs/concepts/configuration/configmap/#mounted-configmaps-are-updated-automatically
	assert.Contains(t, managedPropsCM.Data[workflowproj.GetManagedPropertiesFileName(workflow)], "quarkus.http.port")

	service := test.MustGetService(t, client, workflow)
	assert.Equal(t, int32(constants.DefaultHTTPWorkflowPortInt), service.Spec.Ports[0].TargetPort.IntVal)

	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Status().Update(context.TODO(), workflow)
	assert.NoError(t, err)

	// Mess with the object
	service.Spec.Ports[0].TargetPort = intstr.FromInt(9090)
	err = client.Update(context.TODO(), service)
	assert.NoError(t, err)

	// reconcile again
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the reconciliation ensures the object correctly
	service = test.MustGetService(t, client, workflow)
	assert.Equal(t, int32(constants.DefaultHTTPWorkflowPortInt), service.Spec.Ports[0].TargetPort.IntVal)

	// now with the deployment
	deployment = test.MustGetDeployment(t, client, workflow)
	deployment.Spec.Template.Spec.Containers[0].Image = "default"
	err = client.Update(context.TODO(), deployment)
	assert.NoError(t, err)

	userPropsCM = &corev1.ConfigMap{}
	_ = client.Get(context.TODO(), types.NamespacedName{Namespace: workflow.Namespace, Name: workflowproj.GetWorkflowUserPropertiesConfigMapName(workflow)}, userPropsCM)
	assert.Empty(t, userPropsCM.Data[workflowproj.ApplicationPropertiesFileName])

	managedPropsCM = &corev1.ConfigMap{}
	_ = client.Get(context.TODO(), types.NamespacedName{Namespace: workflow.Namespace, Name: workflowproj.GetWorkflowManagedPropertiesConfigMapName(workflow)}, managedPropsCM)
	assert.NotEmpty(t, managedPropsCM.Data[workflowproj.GetManagedPropertiesFileName(workflow)])
	assert.Contains(t, managedPropsCM.Data[workflowproj.GetManagedPropertiesFileName(workflow)], "quarkus.http.port")

	// reconcile
	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Status().Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, workflowdef.GetDefaultWorkflowDevModeImageTag(), deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_devProfileImageDefaultsNoPlatform(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())
	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow).WithStatusSubresource(workflow).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	devReconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, workflowdef.GetDefaultWorkflowDevModeImageTag(), deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_devProfileWithImageSnapshotOverrideWithPlatform(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())

	platform := test.GetBasePlatformWithDevBaseImageInReadyPhase(workflow.Namespace)

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow, platform).WithStatusSubresource(workflow, platform).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	devReconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, test.CommonImageTag, deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_devProfileWithWPlatformWithoutDevBaseImageAndWithBaseImage(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())

	platform := test.GetBasePlatformWithBaseImageInReadyPhase(workflow.Namespace)

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow, platform).WithStatusSubresource(workflow, platform).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	devReconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, workflowdef.GetDefaultWorkflowDevModeImageTag(), deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_devProfileWithPlatformWithoutDevBaseImageAndWithoutBaseImage(t *testing.T) {
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())

	platform := test.GetBasePlatformInReadyPhase(workflow.Namespace)

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow, platform).WithStatusSubresource(workflow, platform).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	devReconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, workflowdef.GetDefaultWorkflowDevModeImageTag(), deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_newDevProfileWithExternalConfigMaps(t *testing.T) {
	configmapName := "mycamel-configmap"
	workflow := test.GetBaseSonataFlowWithDevProfile(t.Name())
	workflow.Spec.Resources.ConfigMaps = append(workflow.Spec.Resources.ConfigMaps,
		operatorapi.ConfigMapWorkflowResource{ConfigMap: corev1.LocalObjectReference{Name: configmapName}, WorkflowPath: "routes"})

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow).WithStatusSubresource(workflow).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	devReconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	camelXmlRouteFileName := "camelroute-xml"
	xmlRoute := `<route routeConfigurationId="xmlError">
	   		<from uri="timer:xml?period=5s"/>
	   		<log message="I am XML"/>
	   		<throwException exceptionType="java.lang.Exception" message="Some kind of XML error"/>
	   		</route>`

	camelYamlRouteFileName := "camelroute-yaml"
	yamlRoute := `- from:
	       uri: direct:numberToWords
	       steps:
	         - bean:
	             beanType: java.math.BigInteger
	             method: valueOf
	         - setHeader:
	               name: operationName
	               constant: NumberToWords
	         - toD:
	             uri: cxf://{{com.dataaccess.webservicesserver.url}}?serviceClass=com.dataaccess.webservicesserver.NumberConversionSoapType&wsdlURL=/wsdl/numberconversion.wsdl`

	cmData := make(map[string]string)
	cmData[camelXmlRouteFileName] = xmlRoute
	cmUser := createConfigMapBase("Test_newDevProfileWithExternalConfigMaps", "mycamel-configmap", cmData)
	errCreate := client.Create(context.Background(), cmUser)
	assert.Nil(t, errCreate)

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, 2, len(deployment.Spec.Template.Spec.Containers[0].VolumeMounts))
	assert.Equal(t, 2, len(deployment.Spec.Template.Spec.Volumes))
	sortVolumeMounts(&deployment.Spec.Template.Spec.Containers[0])

	wd := deployment.Spec.Template.Spec.Containers[0].VolumeMounts[1]
	extCamel := deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0]
	assert.Equal(t, configMapResourcesVolumeName, wd.Name)
	assert.Equal(t, quarkusDevConfigMountPath, wd.MountPath)

	assert.Equal(t, extCamel.MountPath, quarkusDevConfigMountPath+"/routes")

	cmData[camelYamlRouteFileName] = yamlRoute
	errUpdate := client.Update(context.Background(), cmUser)
	assert.Nil(t, errUpdate)

	// reconcile again
	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	//Now we expect 4 volumes mount wd, props  camelroute.xml and camelroute.yaml
	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, 2, len(deployment.Spec.Template.Spec.Containers[0].VolumeMounts))
	assert.Equal(t, 2, len(deployment.Spec.Template.Spec.Volumes))
	sortVolumeMounts(&deployment.Spec.Template.Spec.Containers[0])

	extCamelRouteOne := deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0]
	assert.Equal(t, quarkusDevConfigMountPath+"/routes", extCamelRouteOne.MountPath)

	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, 2, len(deployment.Spec.Template.Spec.Containers[0].VolumeMounts))
	assert.Equal(t, 2, len(deployment.Spec.Template.Spec.Volumes))

	// remove the external configmaps without removing the labels
	errDel := client.Delete(context.Background(), cmUser)
	assert.Nil(t, errDel)

	// reconcile
	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Status().Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)
	assert.Equal(t, api.ExternalResourcesNotFoundReason, workflow.Status.GetTopLevelCondition().Reason)

	// delete the link
	workflow.Spec.Resources.ConfigMaps = nil
	assert.NoError(t, client.Update(context.TODO(), workflow))
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, 1, len(deployment.Spec.Template.Spec.Volumes))
	assert.Equal(t, 1, len(deployment.Spec.Template.Spec.Containers[0].VolumeMounts))
	sortVolumeMounts(&deployment.Spec.Template.Spec.Containers[0])
	wd = deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0]
	assert.Equal(t, wd.Name, configMapResourcesVolumeName)
	assert.Equal(t, wd.MountPath, quarkusDevConfigMountPath)
}

func Test_VolumeWithCapitalizedPaths(t *testing.T) {
	configMap := &corev1.ConfigMap{}
	test.GetKubernetesResource(test.SonataFlowGreetingsStaticFilesConfig, configMap)
	configMap.Namespace = t.Name()
	workflow := test.GetSonataFlow(test.SonataFlowGreetingsWithStaticResourcesCR, t.Name())

	client := test.NewSonataFlowClientBuilder().WithRuntimeObjects(workflow, configMap).WithStatusSubresource(workflow, configMap).Build()
	utils.SetDiscoveryClient(test.CreateFakeKnativeAndMonitoringDiscoveryClient())

	devReconciler := NewProfileReconciler(client, &rest.Config{}, test.NewFakeRecorder())

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	deployment := test.MustGetDeployment(t, client, workflow)
	assert.NotNil(t, deployment)

	container, _ := kubeutil.GetContainerByName(operatorapi.DefaultContainerName, &deployment.Spec.Template.Spec)
	// properties, definitions, and the capitalized value
	assert.Len(t, container.VolumeMounts, 2)
	assert.Len(t, deployment.Spec.Template.Spec.Volumes, 2)
}

func sortVolumeMounts(container *corev1.Container) {
	sort.SliceStable(container.VolumeMounts, func(i, j int) bool {
		return container.VolumeMounts[i].Name < container.VolumeMounts[j].Name
	})
}

func createConfigMapBase(namespace string, name string, cmData map[string]string) clientruntime.Object {
	cm := &corev1.ConfigMap{
		TypeMeta: metav1.TypeMeta{
			Kind:       "ConfigMap",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Immutable: utils.Pbool(false),
		Data:      cmData,
	}
	return cm
}
