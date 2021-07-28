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
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/kiegroup/kogito-operator/version"
	routev1 "github.com/openshift/api/route/v1"
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
	is, tag := test.CreateFakeImageStreams("kogito-jobs-service", jobsService.GetNamespace(), infrastructure.GetKogitoImageVersion(version.Version))
	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(is).AddImageObjects(tag).Build()
	deployer := newTestSupServiceDeployer(cli, jobsService, "kogito-jobs-service")
	imageName := "quay.io/kiegroup/kogito-jobs-service"
	resources, err := deployer.createRequiredResources(imageName)
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)
	// we have the Image Stream, so other resources should have been created
	assert.True(t, len(resources) > 1)
}

func Test_serviceDeployer_createRequiredResources_OnOCPNoImageStreamCreated(t *testing.T) {
	jobsService := test.CreateFakeJobsService(t.Name())
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	deployer := newTestSupServiceDeployer(cli, jobsService, "kogito-jobs-service")
	imageName := "quay.io/kiegroup/kogito-jobs-service"
	resources, err := deployer.createRequiredResources(imageName)
	assert.NoError(t, err)
	assert.NotEmpty(t, resources)
	// we don't have the Image Stream, so other resources should not have been created other than ConfigMap
	assert.True(t, len(resources) == 3)
	assert.Equal(t, resources[reflect.TypeOf(appsv1.Deployment{})][0].GetName(), "jobs-service")
	assert.Equal(t, resources[reflect.TypeOf(corev1.Service{})][0].GetName(), "jobs-service")
	assert.Equal(t, resources[reflect.TypeOf(routev1.Route{})][0].GetName(), "jobs-service")
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
	imageName := "quay.io/kiegroup/kogito-jobs-service"
	resources, err := deployer.createRequiredResources(imageName)
	assert.Error(t, err)
	assert.Empty(t, resources)
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	errorHandler := infrastructure.NewReconciliationErrorHandler(context)
	assert.Equal(t, infrastructure.TrustStoreMountFailureReason, errorHandler.GetReasonForError(err))
}

func assertDeployerNoErrorAndCreateResources(t *testing.T, deployer serviceDeployer, cli *client.Client, instance api.KogitoService) map[reflect.Type][]resource.KubernetesResource {
	imageName := "quay.io/kiegroup/kogito-jobs-service"
	resources, err := deployer.createRequiredResources(imageName)
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
