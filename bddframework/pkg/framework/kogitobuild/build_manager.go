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

package kogitobuild

import (
	"fmt"
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const errorPrefix = "error while creating build resources: "

// DeltaProcessor describes the interface to communicate with the build package.
// The controller typically manipulates the Kubernetes resources through this implementation.
type DeltaProcessor interface {
	// ProcessDelta...
	ProcessDelta() error
}

type deltaProcessor struct {
	operator.Context
	build        api.KogitoBuildInterface
	buildHandler manager.KogitoBuildHandler
}

// NewDeltaProcessor creates a new DeltaProcessor instance for the given KogitoBuild
func NewDeltaProcessor(context operator.Context, build api.KogitoBuildInterface, buildHandler manager.KogitoBuildHandler) (DeltaProcessor, error) {
	setDefaults(build)
	if err := sanityCheck(build); err != nil {
		return nil, err
	}
	return &deltaProcessor{
		Context:      context,
		build:        build,
		buildHandler: buildHandler,
	}, nil
}

// setDefaults sets the default values for the given KogitoBuild
func setDefaults(build api.KogitoBuildInterface) {
	if len(build.GetSpec().GetRuntime()) == 0 {
		build.GetSpec().SetRuntime(api.QuarkusRuntimeType)
	}
}

// sanityCheck verifies the spec attributes for the given KogitoBuild instance
func sanityCheck(build api.KogitoBuildInterface) error {
	if len(build.GetSpec().GetType()) == 0 {
		return fmt.Errorf("%s: %s", errorPrefix, "build Type is required")
	}
	if build.GetSpec().GetType() == api.RemoteSourceBuildType &&
		len(build.GetSpec().GetGitSource().GetURI()) == 0 {
		return fmt.Errorf("%s: %s %s", errorPrefix, "Git URL is required when build type is", api.RemoteSourceBuildType)
	}
	return nil
}

type buildManager struct {
	build api.KogitoBuildInterface
	operator.Context
}

// BuildManager ...
type BuildManager interface {
	GetRequestedResources() (map[reflect.Type][]client.Object, error)
	GetDeployedResources() (map[reflect.Type][]client.Object, error)
	GetComparator() compare.MapComparator
}

func (d *deltaProcessor) ProcessDelta() (resultErr error) {

	m := d.getBuildManager()
	// get the resources as we want them to be
	requested, resultErr := m.GetRequestedResources()
	if resultErr != nil {
		return
	}
	// get the deployed resources
	deployed, resultErr := m.GetDeployedResources()
	if resultErr != nil {
		return
	}
	//let's compare
	comparator := m.GetComparator()
	deltas := comparator.Compare(deployed, requested)
	for resourceType, delta := range deltas {
		if !delta.HasChanges() {
			continue
		}
		d.Log.Info("Updating kogito build", "Create", len(delta.Added), "Update", len(delta.Updated), "Delete", len(delta.Removed), "Instance", resourceType)
		_, resultErr = kubernetes.ResourceC(d.Client).CreateResources(delta.Added)
		if resultErr != nil {
			return
		}
		_, resultErr = kubernetes.ResourceC(d.Client).UpdateResources(deployed[resourceType], delta.Updated)
		if resultErr != nil {
			return
		}
		_, resultErr = kubernetes.ResourceC(d.Client).DeleteResources(delta.Removed)
		if resultErr != nil {
			return
		}

		if len(delta.Updated) > 0 {
			if resultErr = d.onResourceChange(d.build, resourceType, delta.Updated); resultErr != nil {
				return
			}
		}
	}
	return
}

func (d *deltaProcessor) getBuildManager() BuildManager {
	buildManager := buildManager{
		Context: d.Context,
		build:   d.build,
	}
	if api.LocalSourceBuildType == d.build.GetSpec().GetType() ||
		api.RemoteSourceBuildType == d.build.GetSpec().GetType() {
		buildManager.Log = buildManager.Log.WithValues("build_type", "source")
		return &sourceBuildManager{buildManager}
	}

	buildManager.Log = buildManager.Log.WithValues("build_type", "binary")
	return &binaryBuildManager{buildManager}
}

func (m *buildManager) GetDeployedResources() (map[reflect.Type][]client.Object, error) {
	objectTypes := []client.ObjectList{&buildv1.BuildConfigList{}, &imgv1.ImageStreamList{}}
	resources, err := kubernetes.ResourceC(m.Client).ListAll(objectTypes, m.build.GetNamespace(), m.build)
	if err != nil {
		return nil, err
	}
	if err := m.addSharedImageStreamToResources(resources, GetApplicationName(m.build), m.build.GetNamespace()); err != nil {
		return nil, err
	}
	return resources, nil
}

func (m *buildManager) GetComparator() compare.MapComparator {
	resourceComparator := compare.DefaultComparator()
	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(buildv1.BuildConfig{})).
			UseDefaultComparator().
			WithCustomComparator(framework.CreateBuildConfigComparator()).
			Build())

	resourceComparator.SetComparator(
		framework.NewComparatorBuilder().
			WithType(reflect.TypeOf(imgv1.ImageStream{})).
			UseDefaultComparator().
			WithCustomComparator(framework.CreateSharedImageStreamComparator()).
			Build())
	return compare.MapComparator{Comparator: resourceComparator}
}

// onResourceChange triggers hooks when a resource is changed
func (d *deltaProcessor) onResourceChange(instance api.KogitoBuildInterface, resourceType reflect.Type, resources []client.Object) error {
	// add other resources if need
	switch resourceType {
	case reflect.TypeOf(buildv1.BuildConfig{}):
		return d.onBuildConfigChange(instance, resources)
	}
	return nil
}

// onBuildConfigChange triggers when a build config changes
func (d *deltaProcessor) onBuildConfigChange(instance api.KogitoBuildInterface, buildConfigs []client.Object) error {
	// triggers only on source builds
	if instance.GetSpec().GetType() == api.RemoteSourceBuildType ||
		instance.GetSpec().GetType() == api.LocalSourceBuildType {
		for _, bc := range buildConfigs {
			// building from source
			if bc.GetName() == GetBuildBuilderName(instance) {
				d.Log.Info("Changes detected for build config, starting again", "Build Config", bc.GetName())
				triggerHandler := NewTriggerHandler(d.Context, d.buildHandler)
				if err := triggerHandler.StartNewBuild(bc.(*buildv1.BuildConfig)); err != nil {
					return err
				}
			}
		}
	}
	return nil
}

// AddSharedImageStreamToResources adds the shared ImageStream in the given resource map.
// Normally used during reconciliation phase to bring a not yet owned ImageStream to the deployed list.
func (m *buildManager) addSharedImageStreamToResources(resources map[reflect.Type][]client.Object, name, ns string) error {
	if m.Client.IsOpenshift() {
		// is the image already there?
		for _, is := range resources[reflect.TypeOf(imgv1.ImageStream{})] {
			if is.GetName() == name &&
				is.GetNamespace() == ns {
				return nil
			}
		}
		// fetch the shared image
		imageStreamHandler := infrastructure.NewImageStreamHandler(m.Context)
		sharedImageStream, err := imageStreamHandler.FetchImageStream(types.NamespacedName{Name: name, Namespace: ns})
		if err != nil {
			return err
		}
		if sharedImageStream != nil {
			resources[reflect.TypeOf(imgv1.ImageStream{})] = append(resources[reflect.TypeOf(imgv1.ImageStream{})], sharedImageStream)
		}
	}
	return nil
}
