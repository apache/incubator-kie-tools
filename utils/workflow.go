/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils

import (
	"context"
	"encoding/json"
	apiv08 "github.com/davidesalerno/kogito-serverless-operator/api/v08"
	"github.com/davidesalerno/kogito-serverless-operator/constants"
	"github.com/davidesalerno/kogito-serverless-operator/converters"
	"github.com/go-logr/logr"
	"github.com/ricardozanini/kogito-builder/util/log"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"os"
	"sigs.k8s.io/controller-runtime/pkg/client"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"
)

func GetWorkflowFromCR(workflowCR *apiv08.KogitoServerlessWorkflow, ctx context.Context) ([]byte, error) {
	log := ctrllog.FromContext(ctx)
	converter := converters.NewKogitoServerlessWorkflowConverter(ctx)
	workflow, err := converter.ToCNCFWorkflow(workflowCR)
	if err != nil {
		log.Error(err, "Failed converting KogitoServerlessWorkflow into Workflow")
		return nil, err
	}
	jsonWorkflow, err := json.Marshal(workflow)
	if err != nil {
		log.Error(err, "Failed converting KogitoServerlessWorkflow into JSON")
		return nil, err
	}
	return jsonWorkflow, nil
}

func GetConfigMap(client client.Client, namespace string) (corev1.ConfigMap, error) {

	existingConfigMap := corev1.ConfigMap{
		TypeMeta: metav1.TypeMeta{
			Kind:       "ConfigMap",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      constants.BUILDER_CM_NAME,
			Namespace: namespace,
		},
		Data: map[string]string{},
	}

	err := client.Get(context.TODO(), types.NamespacedName{Name: constants.BUILDER_CM_NAME, Namespace: namespace}, &existingConfigMap)
	if err != nil {
		log.Error(err, "reading configmap")
		return corev1.ConfigMap{}, err
	} else {
		return existingConfigMap, nil
	}
}

func CreateConfigMap(client client.Client, namespace string, cmData map[string]string, log logr.Logger) (cm corev1.ConfigMap, error error) {
	myDep := &appsv1.Deployment{}
	error = client.Get(context.TODO(), types.NamespacedName{Namespace: namespace, Name: constants.BUILDER_CM_NAME}, myDep)

	cm = corev1.ConfigMap{
		TypeMeta: metav1.TypeMeta{
			Kind:       "ConfigMap",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      constants.BUILDER_CM_NAME,
			Namespace: namespace,
		},
		Data: cmData,
	}
	cm.SetGroupVersionKind(corev1.SchemeGroupVersion.WithKind("ConfigMap"))
	cm.SetOwnerReferences(myDep.GetOwnerReferences())
	error = client.Create(context.TODO(), &cm)
	if error != nil {
		log.Error(error, "configmap create error")
		return cm, error
	} else {
		return cm, nil
	}
}

func InitConfigMap(client client.Client, namespace string, log logr.Logger) (cm corev1.ConfigMap, error error) {
	configMap, err := GetConfigMap(client, namespace)
	if err != nil {
		wd, _ := os.Getwd()
		dockerFile, _ := os.ReadFile(wd + "/builder/Dockerfile")
		cmData := constants.GetKogitoBuilderConfigMap()
		cmData[constants.BUILDER_RESOURCE_NAME_DEFAULT] = string(dockerFile)

		_, errx := CreateConfigMap(client, constants.BUILDER_NAMESPACE_DEFAULT, cmData, log)
		if errx != nil {
			log.Error(err, "configmap error:")
		} else {
			log.Info(constants.BUILDER_CM_NAME+" created", "")
		}
		return GetConfigMap(client, namespace)
	} else {
		log.Info("initconfigmap conigmap found")
		return configMap, err
	}
}
