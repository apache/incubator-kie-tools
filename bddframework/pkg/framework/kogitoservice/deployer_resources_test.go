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
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/kiegroup/kogito-operator/version"
	imgv1 "github.com/openshift/api/image/v1"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
	"testing"
)

func Test_serviceDeployer_createRequiredResources_OnOCPImageStreamCreated(t *testing.T) {
	jobsService := test.CreateFakeJobsService(t.Name())
	is, tag := test.CreateImageStreams("kogito-jobs-service", jobsService.GetNamespace(), jobsService.GetName(), infrastructure.GetKogitoImageVersion(version.Version))
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(is).AddImageObjects(tag).Build()
	deployer := newTestSupServiceDeployer(cli, jobsService, "kogito-jobs-service")
	resources, err := deployer.createRequiredResources()
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)
	// we have the Image Stream, so other resources should have been created
	assert.True(t, len(resources) > 1)
}

func Test_serviceDeployer_createRequiredResources_OnOCPNoImageStreamCreated(t *testing.T) {
	jobsService := test.CreateFakeJobsService(t.Name())
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	deployer := newTestSupServiceDeployer(cli, jobsService, "kogito-jobs-service")
	resources, err := deployer.createRequiredResources()
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)
	// we don't have the Image Stream, so other resources should not have been created other than ConfigMap
	assert.True(t, len(resources) == 2)
	assert.Equal(t, resources[reflect.TypeOf(imgv1.ImageStream{})][0].GetName(), "kogito-jobs-service")
	assert.Equal(t, resources[reflect.TypeOf(corev1.ConfigMap{})][0].GetName(), "jobs-service"+appPropConfigMapSuffix)
}

func Test_serviceDeployer_createRequiredResources_NoImageStreamCreated_CreateWithPropertiesConfigMap(t *testing.T) {
	propertiesConfigMapName := "jobs-service-cm"
	instance := test.CreateFakeJobsServiceWithPropertiesConfigMap(t.Name(), propertiesConfigMapName)
	propertiesConfigMap := &corev1.ConfigMap{
		TypeMeta: metav1.TypeMeta{
			Kind:       "ConfigMap",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      propertiesConfigMapName,
			Namespace: instance.GetNamespace(),
		},
		Data: map[string]string{
			ConfigMapApplicationPropertyKey: "\ntestKey=testValue",
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(propertiesConfigMap).Build()
	deployer := newTestSupServiceDeployer(cli, instance, "kogito-jobs-service")
	resources, err := deployer.createRequiredResources()
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)

	configmaps, exist := resources[reflect.TypeOf(corev1.ConfigMap{})]
	assert.True(t, exist)
	assert.Equal(t, 1, len(configmaps))
	configmaps[0].SetOwnerReferences(nil)
	assert.Equal(t, propertiesConfigMap, configmaps[0])

	assert.Equal(t, 1, len(resources[reflect.TypeOf(appsv1.Deployment{})]))
	deployment, ok := resources[reflect.TypeOf(appsv1.Deployment{})][0].(*appsv1.Deployment)
	assert.True(t, ok)
	_, ok = deployment.Spec.Template.Annotations[AppPropContentHashKey]
	assert.True(t, ok)
}

func Test_serviceDeployer_createRequiredResources_NoImageStreamCreated_CreateWithPropertiesConfigMapNotExist(t *testing.T) {
	propertiesConfigMapName := "jobs-service-cm"
	instance := test.CreateFakeJobsServiceWithPropertiesConfigMap(t.Name(), propertiesConfigMapName)
	cli := test.NewFakeClientBuilder().Build()
	deployer := newTestSupServiceDeployer(cli, instance, "kogito-jobs-service")
	resources, err := deployer.createRequiredResources()
	assert.Errorf(t, err, "propertiesConfigMap %s not found", propertiesConfigMapName)
	assert.Empty(t, resources)
}

func Test_serviceDeployer_createRequiredResources_CreateNewAppPropConfigMap(t *testing.T) {
	kogitoKafka := test.CreateFakeKogitoKafka(t.Name())
	kogitoInfinispan := test.CreateFakeKogitoInfinispan(t.Name())
	kogitoKnative := test.CreateFakeKogitoKnative(t.Name())
	instance := test.CreateFakeDataIndex(t.Name())
	instance.GetSpec().AddInfra(kogitoKafka.GetName())
	instance.GetSpec().AddInfra(kogitoInfinispan.GetName())
	instance.GetSpec().AddInfra(kogitoKnative.GetName())
	is, tag := test.CreateImageStreams("kogito-data-index-infinispan", instance.GetNamespace(), instance.GetName(), infrastructure.GetKogitoImageVersion(version.Version))
	cli := test.NewFakeClientBuilder().OnOpenShift().
		AddK8sObjects(is, kogitoKafka, kogitoInfinispan, kogitoKnative).
		AddImageObjects(tag).
		Build()
	deployer := newTestSupServiceDeployer(cli, instance, "kogito-data-index-infinispan")
	deployer.infraHandler = internal.NewKogitoInfraHandler(deployer.Context)
	resources, err := deployer.createRequiredResources()
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)

	assert.Equal(t, 1, len(resources[reflect.TypeOf(corev1.ConfigMap{})]))
	assert.Equal(t, "data-index"+appPropConfigMapSuffix, resources[reflect.TypeOf(corev1.ConfigMap{})][0].GetName())

	assert.Equal(t, 1, len(resources[reflect.TypeOf(appsv1.Deployment{})]))
	deployment, ok := resources[reflect.TypeOf(appsv1.Deployment{})][0].(*appsv1.Deployment)
	assert.True(t, ok)
	_, ok = deployment.Spec.Template.Annotations[AppPropContentHashKey]
	assert.True(t, ok)
	_, ok = resources[reflect.TypeOf(corev1.ConfigMap{})][0].(*corev1.ConfigMap).Data[ConfigMapApplicationPropertyKey]
	assert.True(t, ok)

	// we should have TLS volumes created
	assert.Len(t, deployment.Spec.Template.Spec.Volumes, 2)                    // 1 for properties, 2 for tls
	assert.Len(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts, 2) // 1 for properties, 2 for tls
	assert.Contains(t, deployment.Spec.Template.Spec.Volumes, kogitoInfinispan.GetStatus().GetVolumes()[0].GetNamedVolume().ToKubeVolume())
	assert.Contains(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts, kogitoInfinispan.GetStatus().GetVolumes()[0].GetMount())
}

func Test_serviceDeployer_createRequiredResources_CreateWithAppPropConfigMap(t *testing.T) {
	instance := test.CreateFakeDataIndex(t.Name())
	is, tag := test.CreateImageStreams("kogito-data-index-infinispan", instance.GetNamespace(), instance.GetName(), infrastructure.GetKogitoImageVersion(version.Version))
	cm := &corev1.ConfigMap{
		TypeMeta: metav1.TypeMeta{
			Kind:       "ConfigMap",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      instance.Name + appPropConfigMapSuffix,
			Namespace: instance.GetNamespace(),
		},
		Data: map[string]string{
			ConfigMapApplicationPropertyKey: defaultAppPropContent,
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(is, cm).AddImageObjects(tag).Build()
	deployer := newTestSupServiceDeployer(cli, instance, "kogito-data-index-infinispan")
	resources, err := deployer.createRequiredResources()
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)

	configmaps, exist := resources[reflect.TypeOf(corev1.ConfigMap{})]
	assert.True(t, exist)
	assert.Equal(t, 1, len(configmaps))
	configmaps[0].SetOwnerReferences(nil)
	assert.Equal(t, cm, configmaps[0])

	assert.Equal(t, 1, len(resources[reflect.TypeOf(appsv1.Deployment{})]))
	deployment, ok := resources[reflect.TypeOf(appsv1.Deployment{})][0].(*appsv1.Deployment)
	assert.True(t, ok)
	_, ok = deployment.Spec.Template.Annotations[AppPropContentHashKey]
	assert.True(t, ok)
}

func Test_serviceDeployer_createRequiredResources_MountTrustStore(t *testing.T) {
	trustStore := &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{Name: "kogitoTrustStore", Namespace: t.Name()},
		Data:       map[string][]byte{trustStoreSecretFileKey: []byte("mycerthashs"), trustStoreSecretPasswordKey: []byte("changeit")},
	}
	instance := test.CreateFakeKogitoRuntime(t.Name())
	instance.Spec.TrustStoreSecret = trustStore.Name
	cli := test.NewFakeClientBuilder().AddK8sObjects(trustStore, instance).Build()
	deployer := newTestServiceDeployer(cli, instance)
	_ = assertDeployerNoErrorAndCreateResources(t, deployer, cli, instance)

	deployment := &appsv1.Deployment{ObjectMeta: metav1.ObjectMeta{Name: instance.Name, Namespace: instance.Namespace}}
	test.AssertFetchMustExist(t, cli, deployment)
	assert.Condition(t, func() (success bool) {
		var trustStoreVolume corev1.Volume
		for _, volume := range deployment.Spec.Template.Spec.Volumes {
			if volume.Name == trustStoreVolumeName {
				trustStoreVolume = volume
			}
		}
		// makes it easy to debug
		success = assert.NotNil(t, &trustStoreVolume)
		success = success && assert.NotNil(t, trustStoreVolume.Secret)
		success = success && assert.Len(t, trustStoreVolume.Secret.Items, 1)
		success = success && assert.Equal(t, "cacerts", trustStoreVolume.Secret.Items[0].Key)
		success = success && assert.Equal(t, "cacerts", trustStoreVolume.Secret.Items[0].Path)

		return success
	}, "TrustStoreSecret Volume is incorrectly mounted")

	assert.Condition(t, func() (success bool) {
		var trustStoreEnvVar corev1.EnvVar
		for _, env := range deployment.Spec.Template.Spec.Containers[0].Env {
			if env.Name == trustStoreEnvVarCertFileName {
				trustStoreEnvVar = env
				break
			}
		}
		success = assert.NotNil(t, trustStoreEnvVar)
		success = success && assert.Equal(t, "cacerts", trustStoreEnvVar.Value)
		return success
	}, "TrustStoreSecret Volume is incorrectly mounted")

}

func Test_serviceDeployer_createRequiredResources_MountTrustStore_MissingCM(t *testing.T) {
	instance := test.CreateFakeKogitoRuntime(t.Name())
	instance.Spec.TrustStoreSecret = "missingSecret"
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	deployer := newTestServiceDeployer(cli, instance)
	resources, err := deployer.createRequiredResources()
	assert.Error(t, err)
	assert.Empty(t, resources)
	assert.Equal(t, api.TrustStoreMountFailureReason, reasonForError(err))
}

func assertDeployerNoErrorAndCreateResources(t *testing.T, deployer serviceDeployer, cli *client.Client, instance api.KogitoService) map[reflect.Type][]resource.KubernetesResource {
	resources, err := deployer.createRequiredResources()
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)
	test.AssertFetchMustExist(t, cli, instance)

	for _, resourceType := range resources {
		for _, k8sResource := range resourceType {
			// if the resource already exists, we just ignore.
			_ = kubernetes.ResourceC(cli).Create(k8sResource)
		}
	}
	return resources
}

func newTestSupServiceDeployer(cli *client.Client, instance api.KogitoService, imageName string) serviceDeployer {
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	return serviceDeployer{Context: context, instance: instance,
		definition: ServiceDefinition{
			DefaultImageName: imageName,
			Request: reconcile.Request{
				NamespacedName: types.NamespacedName{
					Name:      instance.GetName(),
					Namespace: instance.GetNamespace(),
				},
			},
		},
	}
}

func newTestServiceDeployer(cli *client.Client, instance api.KogitoService) serviceDeployer {
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	return serviceDeployer{Context: context, instance: instance,
		definition: ServiceDefinition{
			Request: reconcile.Request{
				NamespacedName: types.NamespacedName{
					Name:      instance.GetName(),
					Namespace: instance.GetNamespace(),
				},
			},
		},
	}
}
