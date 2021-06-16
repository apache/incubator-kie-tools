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

package infrastructure

import (
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	imgv1 "github.com/openshift/api/image/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
	"time"
)

type imageStreamReconciler struct {
	operator.Context
	key                   types.NamespacedName
	tag                   string
	addFromReference      bool
	imageName             string
	insecureImageRegistry bool
	owner                 resource.KubernetesResource
}

// ImageStreamReconciler ...
type ImageStreamReconciler interface {
	Reconcile() (time.Duration, error)
}

// NewImageStreamReconciler ...
func NewImageStreamReconciler(context operator.Context, key types.NamespacedName, tag string, addFromReference bool, imageName string, insecureImageRegistry bool, owner resource.KubernetesResource) ImageStreamReconciler {
	return &imageStreamReconciler{
		Context:               context,
		key:                   key,
		tag:                   tag,
		addFromReference:      addFromReference,
		imageName:             imageName,
		insecureImageRegistry: insecureImageRegistry,
		owner:                 owner,
	}
}

func (i *imageStreamReconciler) Reconcile() (reconcileInterval time.Duration, err error) {

	// Create Required resource
	requestedResources, err := i.createRequiredResources()
	if err != nil {
		return
	}

	// Get Deployed resource
	deployedResources, err := i.getDeployedResources()
	if err != nil {
		return
	}

	// Process Delta
	return i.processDelta(requestedResources, deployedResources)
}

func (i *imageStreamReconciler) createRequiredResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	imageStreamHandler := NewImageStreamHandler(i.Context)
	imageStream, err := imageStreamHandler.CreateImageStreamIfNotExists(i.key, i.tag, i.addFromReference, i.imageName, i.insecureImageRegistry)
	if err != nil {
		return nil, err
	}
	if imageStream != nil {
		resources[reflect.TypeOf(imgv1.ImageStream{})] = []resource.KubernetesResource{imageStream}
	}
	if err := i.setOwner(resources); err != nil {
		return resources, err
	}
	return resources, nil
}

func (i *imageStreamReconciler) getDeployedResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	imageStreamHandler := NewImageStreamHandler(i.Context)
	// fetch owned image stream
	ownedImageStream, err := imageStreamHandler.FetchImageStreamForOwner(i.owner)
	if err != nil {
		return nil, err
	}
	if ownedImageStream != nil {
		resources[reflect.TypeOf(imgv1.ImageStream{})] = append(resources[reflect.TypeOf(imgv1.ImageStream{})], ownedImageStream...)
	}

	// fetch the shared image
	sharedImageStream, err := imageStreamHandler.FetchImageStream(i.key)
	if err != nil {
		return nil, err
	}
	if sharedImageStream != nil {
		resources[reflect.TypeOf(imgv1.ImageStream{})] = append(resources[reflect.TypeOf(imgv1.ImageStream{})], sharedImageStream)
	}
	return resources, nil
}

func (i *imageStreamReconciler) processDelta(requestedResources map[reflect.Type][]resource.KubernetesResource, deployedResources map[reflect.Type][]resource.KubernetesResource) (reconcileInterval time.Duration, err error) {
	comparator := i.getComparator()
	deltas := comparator.Compare(deployedResources, requestedResources)
	isDeltaProcessed := false
	for resourceType, delta := range deltas {
		if !delta.HasChanges() {
			i.Log.Info("No delta found", "resourceType", resourceType)
			continue
		}
		i.Log.Info("Will", "create", len(delta.Added), "update", len(delta.Updated), "delete", len(delta.Removed), "resourceType", resourceType)

		if _, err = kubernetes.ResourceC(i.Client).CreateResources(delta.Added); err != nil {
			return
		}

		if _, err = kubernetes.ResourceC(i.Client).UpdateResources(deployedResources[resourceType], delta.Updated); err != nil {
			return
		}

		if _, err = kubernetes.ResourceC(i.Client).DeleteResources(delta.Removed); err != nil {
			return
		}

		isDeltaProcessed = true
	}

	if isDeltaProcessed {
		return time.Second * 10, nil
	}
	return
}

// setOwner sets this service instance as the owner of each resource.
func (i *imageStreamReconciler) setOwner(resources map[reflect.Type][]resource.KubernetesResource) error {
	for _, resourceArr := range resources {
		for _, res := range resourceArr {
			if err := framework.AddOwnerReference(i.owner, i.Scheme, res); err != nil {
				return err
			}
		}
	}
	return nil
}

func (i *imageStreamReconciler) getComparator() compare.MapComparator {
	resourceComparator := compare.DefaultComparator()
	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(imgv1.ImageStream{})).
			UseDefaultComparator().
			WithCustomComparator(framework.CreateSharedImageStreamComparator()).
			Build())
	return compare.MapComparator{Comparator: resourceComparator}
}
