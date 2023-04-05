// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package profiles

import (
	"context"
	"testing"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/kogito-serverless-operator/utils"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"

	"github.com/kiegroup/kogito-serverless-operator/version"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/intstr"
	clientruntime "sigs.k8s.io/controller-runtime/pkg/client"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"

	"github.com/kiegroup/kogito-serverless-operator/api"

	"github.com/kiegroup/kogito-serverless-operator/test"
)

func Test_recoverFromFailureNoDeployment(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleYamlCR, t.Name())
	workflowID := clientruntime.ObjectKeyFromObject(workflow)

	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, "")
	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()

	reconciler := newDevProfileReconciler(client, &logger)

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
	assert.NotEmpty(t, deployment.Spec.Template.ObjectMeta.Annotations["kubectl.kubernetes.io/restartedAt"])
}

func Test_newDevProfile(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleYamlCR, t.Name())
	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()
	devReconciler := newDevProfileReconciler(client, &logger)

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, defaultKogitoServerlessWorkflowDevImage+"-"+nightlySuffix+":latest", deployment.Spec.Template.Spec.Containers[0].Image)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].LivenessProbe)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].ReadinessProbe)
	assert.NotNil(t, deployment.Spec.Template.Spec.Containers[0].StartupProbe)

	defCM := test.MustGetConfigMap(t, client, workflow)
	assert.NotEmpty(t, defCM.Data[workflow.Name+kogitoWorkflowJSONFileExt])
	assert.Equal(t, configMapWorkflowDefMountPath, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].MountPath)
	assert.Equal(t, "", deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].SubPath) //https://kubernetes.io/docs/concepts/configuration/configmap/#mounted-configmaps-are-updated-automatically
	assert.Equal(t, configMountPathMap[WORKFLOW], deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].MountPath)

	propCM := &v1.ConfigMap{}
	_ = client.Get(context.TODO(), types.NamespacedName{Namespace: workflow.Namespace, Name: getWorkflowPropertiesConfigMapName(workflow)}, propCM)
	assert.NotEmpty(t, propCM.Data[applicationPropertiesFileName])
	assert.Equal(t, quarkusDevConfigMountPath, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[1].MountPath)
	assert.Equal(t, "", deployment.Spec.Template.Spec.Containers[0].VolumeMounts[1].SubPath) //https://kubernetes.io/docs/concepts/configuration/configmap/#mounted-configmaps-are-updated-automatically
	assert.Contains(t, propCM.Data[applicationPropertiesFileName], "quarkus.http.port")

	service := test.MustGetService(t, client, workflow)
	assert.Equal(t, int32(defaultHTTPWorkflowPort), service.Spec.Ports[0].TargetPort.IntVal)

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
	assert.Equal(t, int32(defaultHTTPWorkflowPort), service.Spec.Ports[0].TargetPort.IntVal)

	// now with the deployment
	deployment = test.MustGetDeployment(t, client, workflow)
	deployment.Spec.Template.Spec.Containers[0].Image = "default"
	err = client.Update(context.TODO(), deployment)
	assert.NoError(t, err)

	propCM = &v1.ConfigMap{}
	_ = client.Get(context.TODO(), types.NamespacedName{Namespace: workflow.Namespace, Name: getWorkflowPropertiesConfigMapName(workflow)}, propCM)
	assert.NotEmpty(t, propCM.Data[applicationPropertiesFileName])
	assert.Contains(t, propCM.Data[applicationPropertiesFileName], "quarkus.http.port")

	// reconcile
	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, defaultKogitoServerlessWorkflowDevImage+"-"+nightlySuffix+":latest", deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_devProfileImageDefaultsNoPlatform(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeYamlCR, t.Name())
	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()

	devReconciler := newDevProfileReconciler(client, &logger)

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	if isSnapshot(version.OperatorVersion) {
		assert.Equal(t, defaultKogitoServerlessWorkflowDevImage+"-"+nightlySuffix+":latest", deployment.Spec.Template.Spec.Containers[0].Image)
	} else {
		assert.Equal(t, defaultKogitoServerlessWorkflowDevImage+":"+version.OperatorVersion, deployment.Spec.Template.Spec.Containers[0].Image)
	}
}

func Test_devProfileWithImageSnapshotOverrideWithPlatform(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeYamlCR, t.Name())

	platform := test.GetKogitoServerlessPlatform("../../config/samples/" + test.KogitoServerlessPlatformWithDevBaseImageYamlCR)
	platform.Status.Phase = operatorapi.PlatformPhaseReady
	platform.Namespace = workflow.Namespace

	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()
	errCreatePlatform := client.Create(context.Background(), platform)
	assert.Nil(t, errCreatePlatform)
	devReconciler := newDevProfileReconciler(client, &logger)

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, "quay.io/customgroup/custom-swf-builder-nightly:42.43.7", deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_devProfileWithWPlatformWithoutDevBaseImageAndWithBaseImage(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeYamlCR, t.Name())

	platform := test.GetKogitoServerlessPlatform("../../config/samples/" + test.KogitoServerlessPlatformWithBaseImageYamlCR)
	platform.Status.Phase = operatorapi.PlatformPhaseReady
	platform.Namespace = workflow.Namespace

	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()
	errCreatePlatform := client.Create(context.Background(), platform)
	assert.Nil(t, errCreatePlatform)
	devReconciler := newDevProfileReconciler(client, &logger)

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, "quay.io/kiegroup/kogito-swf-builder-nightly:latest", deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_devProfileWithPlatformWithoutDevBaseImageAndWithoutBaseImage(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeYamlCR, t.Name())

	platform := test.GetKogitoServerlessPlatform("../../config/samples/" + test.KogitoServerlessPlatformYamlCR)
	platform.Status.Phase = operatorapi.PlatformPhaseReady
	platform.Namespace = workflow.Namespace

	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()
	errCreatePlatform := client.Create(context.Background(), platform)
	assert.Nil(t, errCreatePlatform)
	devReconciler := newDevProfileReconciler(client, &logger)

	result, err := devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	// check if the objects have been created
	deployment := test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, defaultKogitoServerlessWorkflowDevImage+"-"+nightlySuffix+":latest", deployment.Spec.Template.Spec.Containers[0].Image)
}

func Test_newDevProfileWithExternalConfigMaps(t *testing.T) {
	logger := ctrllog.FromContext(context.TODO())
	workflow := test.GetKogitoServerlessWorkflow("../../config/samples/"+test.KogitoServerlessWorkflowSampleDevModeWithExternalResourceYamlCR, t.Name())
	client := test.NewKogitoClientBuilder().WithRuntimeObjects(workflow).Build()
	devReconciler := newDevProfileReconciler(client, &logger)
	configmapName := "mycamel-configmap"
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
	assert.Equal(t, 3, len(deployment.Spec.Template.Spec.Containers[0].VolumeMounts))
	assert.Equal(t, 3, len(deployment.Spec.Template.Spec.Volumes))

	wd := deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0]
	props := deployment.Spec.Template.Spec.Containers[0].VolumeMounts[1]
	extCamel := deployment.Spec.Template.Spec.Containers[0].VolumeMounts[2]
	assert.Equal(t, wd.Name, configMapWorkflowDefVolumeName)
	assert.Equal(t, wd.MountPath, configMountPathMap[WORKFLOW])
	assert.Equal(t, props.Name, configMapWorkflowPropsVolumeName)
	assert.Equal(t, props.MountPath, quarkusDevConfigMountPath)
	assert.Equal(t, extCamel.Name, configmapName)
	assert.Equal(t, extCamel.MountPath, configMountPathMap[CAMEL])

	cmData[camelYamlRouteFileName] = yamlRoute
	errUpdate := client.Update(context.Background(), cmUser)
	assert.Nil(t, errUpdate)

	// reconcile again
	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	deployment = test.MustGetDeployment(t, client, workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	//Now we expect 4 volumes mount wd, props  camelroute.xml and camelroute.yaml
	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, 3, len(deployment.Spec.Template.Spec.Containers[0].VolumeMounts))
	assert.Equal(t, 3, len(deployment.Spec.Template.Spec.Volumes))

	extCamelRouteOne := deployment.Spec.Template.Spec.Containers[0].VolumeMounts[2]
	assert.Equal(t, extCamelRouteOne.Name, configmapName)
	assert.Equal(t, extCamelRouteOne.MountPath, configMountPathMap[CAMEL])

	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	deployment = test.MustGetDeployment(t, client, workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, 3, len(deployment.Spec.Template.Spec.Containers[0].VolumeMounts))
	assert.Equal(t, 3, len(deployment.Spec.Template.Spec.Volumes))

	// remove the external configmaps without removing the labels
	errDel := client.Delete(context.Background(), cmUser)
	assert.Nil(t, errDel)

	// reconcile
	workflow.Status.Manager().MarkTrue(api.RunningConditionType)
	err = client.Update(context.TODO(), workflow)
	assert.NoError(t, err)
	result, err = devReconciler.Reconcile(context.TODO(), workflow)
	assert.NoError(t, err)
	assert.NotNil(t, result)

	deployment = test.MustGetDeployment(t, client, workflow)
	assert.Equal(t, 2, len(deployment.Spec.Template.Spec.Volumes))
	assert.Equal(t, 2, len(deployment.Spec.Template.Spec.Containers[0].VolumeMounts))
	wd = deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0]
	assert.Equal(t, wd.Name, configMapWorkflowDefVolumeName)
	assert.Equal(t, wd.MountPath, configMountPathMap[WORKFLOW])
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
