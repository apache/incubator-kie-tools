// Copyright 2024 Apache Software Foundation (ASF)
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

package properties

import (
	"context"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils"
	"github.com/magiconair/properties"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/klog/v2"
)

func resolvePlatformWorkflowProperties(platform *operatorapi.SonataFlowPlatform) (*properties.Properties, error) {
	props := properties.NewProperties()

	if platform.Spec.Properties == nil {
		return props, nil
	}

	for _, propVar := range platform.Spec.Properties.Flow {
		if len(propVar.Value) > 0 {
			props.Set(propVar.Name, propVar.Value)
		} else if propVar.ValueFrom != nil {
			val, err := getPropVarRefValue(propVar.ValueFrom, platform.Namespace)
			if err != nil {
				return nil, err
			}
			props.Set(propVar.Name, val)
		}
	}

	return props, nil
}

func getPropVarRefValue(from *operatorapi.PropertyVarSource, namespace string) (string, error) {
	// same order as k8s api (we try to fetch first a secret)
	if from.SecretKeyRef != nil {
		secret := &v1.Secret{
			ObjectMeta: metav1.ObjectMeta{Namespace: namespace, Name: from.SecretKeyRef.Name},
		}
		err := utils.GetClient().Get(context.TODO(), types.NamespacedName{Namespace: secret.Namespace, Name: secret.Name}, secret)
		if err != nil && !errors.IsNotFound(err) {
			return "", err
		}
		if data, ok := secret.Data[from.SecretKeyRef.Key]; ok {
			return string(data), nil
		}
		if from.SecretKeyRef.Optional == utils.Pbool(false) {
			klog.V(log.D).InfoS("Key not found in secret", "Key", from.SecretKeyRef.Key)
		}
	}
	if from.ConfigMapKeyRef != nil {
		cm := &v1.ConfigMap{
			ObjectMeta: metav1.ObjectMeta{Namespace: namespace, Name: from.ConfigMapKeyRef.Name},
		}
		err := utils.GetClient().Get(context.TODO(), types.NamespacedName{Namespace: cm.Namespace, Name: cm.Name}, cm)
		if err != nil && !errors.IsNotFound(err) {
			return "", err
		}
		if data, ok := cm.Data[from.ConfigMapKeyRef.Key]; ok {
			return data, nil
		}
		if from.ConfigMapKeyRef.Optional == utils.Pbool(false) {
			klog.V(log.D).InfoS("Key not found in configMap", "Key", from.ConfigMapKeyRef.Key)
		}
	}

	return "", nil
}
