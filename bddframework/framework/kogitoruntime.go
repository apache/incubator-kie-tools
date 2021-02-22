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

package framework

import (
	"fmt"
	"github.com/kiegroup/kogito-cloud-operator/api"

	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

// DeployRuntimeService deploy a Kogito service
func DeployRuntimeService(namespace string, installerType InstallerType, serviceHolder *bddtypes.KogitoServiceHolder) error {
	return DeployService(serviceHolder, installerType)
}

// SetKogitoRuntimeReplicas sets the number of replicas for a Kogito application
func SetKogitoRuntimeReplicas(namespace, name string, nbPods int) error {
	GetLogger(namespace).Info("Set Kogito application props", "name", name, "replica number", nbPods)
	kogitoRuntime, err := getKogitoRuntime(namespace, name)
	if err != nil {
		return err
	} else if kogitoRuntime == nil {
		return fmt.Errorf("No KogitoRuntime found with name %s in namespace %s", name, namespace)
	}
	replicas := int32(nbPods)
	kogitoRuntime.Spec.KogitoServiceSpec.Replicas = &replicas
	return kubernetes.ResourceC(kubeClient).Update(kogitoRuntime)
}

// GetKogitoRuntimeStub Get basic KogitoRuntime stub with all needed fields initialized
func GetKogitoRuntimeStub(namespace, runtimeType, name, imageTag string) *v1beta1.KogitoRuntime {
	replicas := int32(1)
	kogitoRuntime := &v1beta1.KogitoRuntime{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Status: v1beta1.KogitoRuntimeStatus{
			KogitoServiceStatus: v1beta1.KogitoServiceStatus{
				ConditionsMeta: v1beta1.ConditionsMeta{Conditions: []v1beta1.Condition{}},
			},
		},
		Spec: v1beta1.KogitoRuntimeSpec{
			Runtime: api.RuntimeType(runtimeType),
			KogitoServiceSpec: v1beta1.KogitoServiceSpec{
				Image: imageTag,
				// Use insecure registry flag in tests
				InsecureImageRegistry: true,
				Replicas:              &replicas,
				// Extends the probe interval for slow test environment
				Probes: v1beta1.KogitoProbe{
					ReadinessProbe: corev1.Probe{
						FailureThreshold: 12,
					},
					LivenessProbe: corev1.Probe{
						FailureThreshold: 12,
					},
				},
			},
		},
	}

	return kogitoRuntime
}

func getKogitoRuntime(namespace, name string) (*v1beta1.KogitoRuntime, error) {
	kogitoRuntime := &v1beta1.KogitoRuntime{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, kogitoRuntime); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for KogitoRuntime %s: %v ", name, err)
	} else if errors.IsNotFound(err) || !exists {
		return nil, nil
	}
	return kogitoRuntime, nil
}
