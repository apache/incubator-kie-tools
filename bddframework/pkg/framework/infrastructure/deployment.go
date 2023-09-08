// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package infrastructure

import (
	"fmt"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	appsv1 "k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
)

// DeploymentHandler ...
type DeploymentHandler interface {
	FetchDeployment(key types.NamespacedName) (*appsv1.Deployment, error)
	FetchDeploymentList(namespace string) (*appsv1.DeploymentList, error)
	MustFetchDeployment(key types.NamespacedName) (*appsv1.Deployment, error)
	IsDeploymentAvailable(key types.NamespacedName) (bool, error)
	FetchReadyReplicas(key types.NamespacedName) (int32, error)
	GetComparator() compare.MapComparator
}

type deploymentHandler struct {
	operator.Context
}

// NewDeploymentHandler ...
func NewDeploymentHandler(context operator.Context) DeploymentHandler {
	return &deploymentHandler{
		context,
	}
}

func (d *deploymentHandler) FetchDeployment(key types.NamespacedName) (*appsv1.Deployment, error) {
	deployment := &appsv1.Deployment{}
	if exists, err := kubernetes.ResourceC(d.Client).FetchWithKey(key, deployment); err != nil {
		return nil, err
	} else if !exists {
		d.Log.Debug("Deployment not found.")
		return nil, nil
	} else {
		d.Log.Debug("Successfully fetch deployed Deployment")
		return deployment, nil
	}
}

func (d *deploymentHandler) FetchDeploymentList(namespace string) (*appsv1.DeploymentList, error) {
	dcs := &appsv1.DeploymentList{}
	if err := kubernetes.ResourceC(d.Client).ListWithNamespace(namespace, dcs); err != nil {
		return nil, err
	}
	return dcs, nil
}

func (d *deploymentHandler) MustFetchDeployment(key types.NamespacedName) (*appsv1.Deployment, error) {
	deployment, err := d.FetchDeployment(key)
	if err != nil {
		return nil, err
	} else if deployment == nil {
		return nil, fmt.Errorf("deployment not found with name %s in namespace %s", key.Name, key.Namespace)
	}
	return deployment, nil
}

// IsDeploymentAvailable verifies if the Deployment resource from the given KogitoService has replicas available
func (d *deploymentHandler) IsDeploymentAvailable(key types.NamespacedName) (bool, error) {
	deployment, err := d.FetchDeployment(key)
	if err != nil {
		return false, err
	} else if deployment == nil {
		return false, nil
	}
	return deployment.Status.AvailableReplicas > 0, nil
}

func (d *deploymentHandler) FetchReadyReplicas(key types.NamespacedName) (int32, error) {
	deployment, err := d.FetchDeployment(key)
	if err != nil {
		return 0, err
	} else if deployment == nil {
		return 0, nil
	}
	return deployment.Status.AvailableReplicas, nil
}

// GetComparator gets the comparator for the owned resources
func (d *deploymentHandler) GetComparator() compare.MapComparator {
	resourceComparator := compare.DefaultComparator()

	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(appsv1.Deployment{})).
			UseDefaultComparator().
			WithCustomComparator(framework.CreateDeploymentComparator()).
			Build())
	return compare.MapComparator{Comparator: resourceComparator}
}
