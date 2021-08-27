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

package shared

import (
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
)

const (
	// Proto Buf folder env
	protoBufKeyFolder string = "KOGITO_PROTOBUF_FOLDER"
	// Proto Buf watch env
	protoBufKeyWatch string = "KOGITO_PROTOBUF_WATCH"
)

// ProtoBufHandler ...
type ProtoBufHandler interface {
	MountProtoBufConfigMapOnDataIndex(runtimeInstance api.KogitoRuntimeInterface) (err error)
}

type protoBufHandler struct {
	operator.Context
	supportingServiceHandler manager.KogitoSupportingServiceHandler
	supportingServiceManager manager.KogitoSupportingServiceManager
	protoBufConfigMapHandler ProtoBufConfigMapHandler
	configMapHandler         infrastructure.ConfigMapHandler
}

// NewProtoBufHandler ...
func NewProtoBufHandler(context operator.Context, supportingServiceHandler manager.KogitoSupportingServiceHandler) ProtoBufHandler {
	return &protoBufHandler{
		Context:                  context,
		supportingServiceHandler: supportingServiceHandler,
		supportingServiceManager: manager.NewKogitoSupportingServiceManager(context, supportingServiceHandler),
		protoBufConfigMapHandler: NewProtoBufConfigMapHandler(context),
		configMapHandler:         infrastructure.NewConfigMapHandler(context),
	}
}

// MountProtoBufConfigMapOnDataIndex mounts protobuf configMaps from KogitoRuntime services into the given deployment instance of DataIndex
func (p *protoBufHandler) MountProtoBufConfigMapOnDataIndex(runtimeInstance api.KogitoRuntimeInterface) (err error) {

	// Load Data index service instance
	dataIndexService, err := p.supportingServiceManager.FetchKogitoSupportingServiceForServiceType(runtimeInstance.GetNamespace(), api.DataIndex)
	if err != nil {
		return err
	}

	// check if data-index service not exists then return
	if dataIndexService == nil {
		p.Log.Debug("Data-index service not exists, returning")
		return
	}

	// Fetch Protobuf configmap for provided runtime
	protoBufConfigMap, err := p.protoBufConfigMapHandler.FetchProtoBufConfigMap(runtimeInstance)
	if err != nil {
		return err
	}

	// If protobuf configmap not exists then create new configmap
	if protoBufConfigMap == nil {
		protoBufConfigMap, err = p.protoBufConfigMapHandler.CreateProtoBufConfigMap(runtimeInstance)
		if err != nil {
			return err
		}
		// set data-index service as owner of that configmap
		if err = framework.SetOwner(dataIndexService, p.Scheme, protoBufConfigMap); err != nil {
			return err
		}
		if err = kubernetes.ResourceC(p.Client).Create(protoBufConfigMap); err != nil {
			return err
		}
		return infrastructure.ErrorForProcessingProtoBufConfigMapDelta()
	}

	// mount protobuf configmap on data-index deployment
	dataIndexDeployment, err := p.supportingServiceManager.FetchKogitoSupportingServiceDeployment(runtimeInstance.GetNamespace(), api.DataIndex)
	if err != nil || dataIndexDeployment == nil {
		return
	}

	volumeReference := p.protoBufConfigMapHandler.CreateProtoBufConfigMapReference(runtimeInstance)
	if err = p.configMapHandler.MountAsVolume(dataIndexDeployment, volumeReference); err != nil {
		return
	}
	updateProtoBufPropInToDeploymentEnv(dataIndexDeployment)

	return kubernetes.ResourceC(p.Client).Update(dataIndexDeployment)
}

func updateProtoBufPropInToDeploymentEnv(deployment *appsv1.Deployment) {
	if len(deployment.Spec.Template.Spec.Volumes) > 0 {
		framework.SetEnvVar(protoBufKeyWatch, "true", &deployment.Spec.Template.Spec.Containers[0])
		framework.SetEnvVar(protoBufKeyFolder, DefaultProtobufMountPath, &deployment.Spec.Template.Spec.Containers[0])
	} else {
		framework.SetEnvVar(protoBufKeyWatch, "false", &deployment.Spec.Template.Spec.Containers[0])
		framework.SetEnvVar(protoBufKeyFolder, "", &deployment.Spec.Template.Spec.Containers[0])
	}
}
