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

package connector

import (
	"github.com/google/uuid"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sort"
	"testing"
)

func TestMountProtoBufConfigMapsOnDeployment(t *testing.T) {
	fileName1 := "mydomain.proto"
	fileName2 := "mydomain2.proto"
	cm := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "my-domain-protobufs",
			Labels:    map[string]string{ConfigMapProtoBufEnabledLabelKey: "true"},
		},
		Data: map[string]string{
			fileName1: "This is a protobuf file",
			fileName2: "This is another file",
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(cm).OnOpenShift().Build()

	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Namespace: t.Name(), Name: "data-index"},
		Spec: appsv1.DeploymentSpec{
			Template: v1.PodTemplateSpec{
				Spec: v1.PodSpec{
					Containers: []v1.Container{
						{
							Name: "my-container",
						},
					},
				},
			},
		},
	}
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	supportingServiceHandler := internal.NewKogitoSupportingServiceHandler(context)
	protoBufHandler := NewProtoBufHandler(context, supportingServiceHandler)
	err := protoBufHandler.MountProtoBufConfigMapsOnDeployment(deployment)
	assert.NoError(t, err)
	assert.Len(t, deployment.Spec.Template.Spec.Volumes, 1)
	assert.Contains(t, deployment.Spec.Template.Spec.Volumes[0].Name, cm.Name)
	assert.Len(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts, 2)

	// we need to have them ordered to be able to do the appropriate comparision.
	sort.Slice(deployment.Spec.Template.Spec.Containers[0].VolumeMounts, func(i, j int) bool {
		return deployment.Spec.Template.Spec.Containers[0].VolumeMounts[i].SubPath < deployment.Spec.Template.Spec.Containers[0].VolumeMounts[j].SubPath
	})
	assert.Equal(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].Name, cm.Name)
	assert.Equal(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[1].Name, cm.Name)
	assert.Contains(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].SubPath, fileName1)
	assert.Contains(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts[0].MountPath, fileName1)
}

func TestMountProtoBufConfigMapOnDataIndex(t *testing.T) {
	fileName1 := "mydomain.proto"
	fileName2 := "mydomain2.proto"
	instance := &v1beta1.KogitoSupportingService{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "data-index",
			UID:       types.UID(uuid.New().String()),
		},
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.DataIndex,
		},
	}
	dc := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{Name: instance.Name, Namespace: instance.Namespace, OwnerReferences: []metav1.OwnerReference{{UID: instance.UID}}},
		Spec: appsv1.DeploymentSpec{
			Template: v1.PodTemplateSpec{
				Spec: v1.PodSpec{Containers: []v1.Container{{Name: "test"}}},
			},
		},
	}

	cm1 := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "my-domain-protobufs1",
			Labels: map[string]string{
				ConfigMapProtoBufEnabledLabelKey: "true",
				framework.LabelAppKey:            "my-domain-protobufs1",
			},
		},
		Data: map[string]string{fileName1: "This is a protobuf file"},
	}
	cm2 := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "my-domain-protobufs2",
			Labels: map[string]string{
				ConfigMapProtoBufEnabledLabelKey: "true",
				framework.LabelAppKey:            "my-domain-protobufs2",
			},
		},
		Data: map[string]string{fileName2: "This is a protobuf file"},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance, dc, cm1, cm2).OnOpenShift().Build()

	runtimeService := &v1beta1.KogitoRuntime{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "my-domain-protobufs1",
		},
	}

	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	supportingServiceHandler := internal.NewKogitoSupportingServiceHandler(context)
	protoBufHandler := NewProtoBufHandler(context, supportingServiceHandler)
	err := protoBufHandler.MountProtoBufConfigMapOnDataIndex(runtimeService)
	assert.NoError(t, err)
	supportingServiceManager := manager.NewKogitoSupportingServiceManager(context, supportingServiceHandler)
	deployment, err := supportingServiceManager.FetchKogitoSupportingServiceDeployment(t.Name(), api.DataIndex)
	assert.NoError(t, err)

	assert.Len(t, deployment.Spec.Template.Spec.Volumes, 1)
	assert.Len(t, deployment.Spec.Template.Spec.Containers[0].VolumeMounts, 1)
}

func Test_getProtoBufConfigMapsForAllRuntimeServices(t *testing.T) {
	cm1 := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "my-domain-protobufs1",
			Labels: map[string]string{
				ConfigMapProtoBufEnabledLabelKey: "true",
				framework.LabelAppKey:            "my-domain-protobufs1",
			},
		},
		Data: map[string]string{"mydomain.proto": "This is a protobuf file"},
	}
	cm2 := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "my-domain-protobufs2",
			Labels: map[string]string{
				ConfigMapProtoBufEnabledLabelKey: "true",
				framework.LabelAppKey:            "my-domain-protobufs2",
			},
		},
		Data: map[string]string{"mydomain2.proto": "This is a protobuf file"},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(cm1, cm2).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	supportingServiceHandler := internal.NewKogitoSupportingServiceHandler(context)
	protoBufHandler := &protoBufHandler{
		Context:                  context,
		supportingServiceHandler: supportingServiceHandler,
	}
	cms, err := protoBufHandler.getProtoBufConfigMapsForAllRuntimeServices(t.Name())
	assert.NoError(t, err)
	assert.Equal(t, 2, len(cms.Items))
}

func Test_getProtoBufConfigMapsForSpecificRuntimeService(t *testing.T) {
	cm1 := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "my-domain-protobufs1",
			Labels: map[string]string{
				ConfigMapProtoBufEnabledLabelKey: "true",
				framework.LabelAppKey:            "my-domain-protobufs1",
			},
		},
		Data: map[string]string{"mydomain.proto": "This is a protobuf file"},
	}
	cm2 := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: t.Name(),
			Name:      "my-domain-protobufs2",
			Labels: map[string]string{
				ConfigMapProtoBufEnabledLabelKey: "true",
				framework.LabelAppKey:            "my-domain-protobufs2",
			},
		},
		Data: map[string]string{"mydomain2.proto": "This is a protobuf file"},
	}
	runtimeInstance := &v1beta1.KogitoRuntime{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "my-domain-protobufs1",
			Namespace: t.Name(),
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(cm1, cm2).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	supportingServiceHandler := internal.NewKogitoSupportingServiceHandler(context)
	protoBufHandler := &protoBufHandler{
		Context:                  context,
		supportingServiceHandler: supportingServiceHandler,
	}
	cms, err := protoBufHandler.getProtoBufConfigMapsForSpecificRuntimeService(runtimeInstance)
	assert.NoError(t, err)
	assert.Equal(t, 1, len(cms.Items))
	assert.Equal(t, "my-domain-protobufs1", cms.Items[0].Name)
}
