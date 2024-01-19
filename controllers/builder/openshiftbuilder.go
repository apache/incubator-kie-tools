/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package builder

import (
	"context"
	"strings"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/openshift"

	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	buildclientv1 "github.com/openshift/client-go/build/clientset/versioned/typed/build/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/rest"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform"
	"github.com/apache/incubator-kie-kogito-serverless-operator/workflowproj"

	kubeutil "github.com/apache/incubator-kie-kogito-serverless-operator/utils/kubernetes"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/workflowdef"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
)

const (
	imageStreamTagKind         = "ImageStreamTag"
	defaultBuildMessageTrigger = "Triggered by SonataFlow Operator"
)

//		openshiftBuildPhaseMatrix Build phases correlations:
//
//	  - BuildPhaseScheduling: When we first schedule the build to the OpenShift cluster by creating a new BuildConfig for the workflow.
//	    There's no Build instance at this point, if there's, it's in "New" phase.
//	  - BuildPhasePending: When we fetch the Build, and we mimic its status, meaning that a pod has been scheduled to start the build.
//	  - BuildPhaseRunning: --
//	  - BuildPhaseSucceeded: "Complete" for the OCP Build
//	  - BuildPhaseFailed: --
//	  - BuildPhaseError: --
//	  - BuildPhaseInterrupted: "Cancelled" for the OCP Build
var openshiftBuildPhaseMatrix = map[buildv1.BuildPhase]operatorapi.BuildPhase{
	buildv1.BuildPhaseNew:       operatorapi.BuildPhaseScheduling,
	buildv1.BuildPhasePending:   operatorapi.BuildPhasePending,
	buildv1.BuildPhaseRunning:   operatorapi.BuildPhaseRunning,
	buildv1.BuildPhaseComplete:  operatorapi.BuildPhaseSucceeded,
	buildv1.BuildPhaseFailed:    operatorapi.BuildPhaseFailed,
	buildv1.BuildPhaseError:     operatorapi.BuildPhaseError,
	buildv1.BuildPhaseCancelled: operatorapi.BuildPhaseInterrupted,
}

var _ BuildManager = &openshiftBuilderManager{}

type openshiftBuilderManager struct {
	buildManagerContext
	buildClient buildclientv1.BuildV1Interface
}

func newOpenShiftBuilderManager(managerContext buildManagerContext, cliConfig *rest.Config) (BuildManager, error) {
	buildClient, err := openshift.NewOpenShiftBuildClient(cliConfig)
	if err != nil {
		return nil, err
	}
	return newOpenShiftBuilderManagerWithClient(managerContext, buildClient), err
}

// Used internally for testing purposes, but in the future could be used by the main factory.
// There's no special code related to testing, but we do expose an interface to inject the internal build client.
func newOpenShiftBuilderManagerWithClient(managerContext buildManagerContext, buildClient buildclientv1.BuildV1Interface) BuildManager {
	manager := &openshiftBuilderManager{
		buildManagerContext: managerContext,
	}
	manager.buildClient = buildClient
	return manager
}

func (o *openshiftBuilderManager) Schedule(build *operatorapi.SonataFlowBuild) error {
	is := &imgv1.ImageStream{
		ObjectMeta: metav1.ObjectMeta{
			Name:      build.Name,
			Namespace: build.Namespace,
		},
		Spec: imgv1.ImageStreamSpec{
			LookupPolicy: imgv1.ImageLookupPolicy{
				Local: true,
			},
		},
	}
	workflow, err := o.fetchWorkflowForBuild(build)
	if err != nil {
		return err
	}
	bc := o.newDefaultBuildConfig(build, workflow)
	if err = o.addExternalResources(bc, workflow); err != nil {
		return err
	}
	workflowproj.SetDefaultLabels(workflow, is)
	workflowproj.SetDefaultLabels(workflow, bc)
	if err = controllerutil.SetControllerReference(build, bc, o.buildManagerContext.client.Scheme()); err != nil {
		return err
	}
	if err = controllerutil.SetControllerReference(build, is, o.buildManagerContext.client.Scheme()); err != nil {
		return err
	}

	// Persist our objects
	if _, err = controllerutil.CreateOrPatch(o.ctx, o.client, is, func() error {
		is.Spec.LookupPolicy.Local = true
		return nil
	}); err != nil {
		return err
	}
	if _, err = controllerutil.CreateOrPatch(o.ctx, o.client, bc, func() error {
		if kubeutil.IsObjectNew(bc) {
			return nil
		}
		referenceBC := o.newDefaultBuildConfig(build, workflow)
		bc.Spec = *referenceBC.Spec.DeepCopy()
		return o.addExternalResources(bc, workflow)
	}); err != nil {
		return err
	}

	build.Status.BuildPhase = operatorapi.BuildPhaseInitialization
	return nil
}

func (o *openshiftBuilderManager) newDefaultBuildConfig(build *operatorapi.SonataFlowBuild, workflow *operatorapi.SonataFlow) *buildv1.BuildConfig {
	optimizationPol := buildv1.ImageOptimizationSkipLayers
	dockerFile := platform.GetCustomizedDockerfile(o.commonConfig.Data[o.commonConfig.Data[configKeyDefaultBuilderResourceName]], *o.platform)
	return &buildv1.BuildConfig{
		ObjectMeta: metav1.ObjectMeta{Namespace: build.Namespace, Name: build.Name},
		Spec: buildv1.BuildConfigSpec{
			RunPolicy:                    buildv1.BuildRunPolicySerial,
			FailedBuildsHistoryLimit:     utils.Pint(1),
			SuccessfulBuildsHistoryLimit: utils.Pint(3),
			CommonSpec: buildv1.CommonSpec{
				Source: buildv1.BuildSource{
					Type:       buildv1.BuildSourceBinary,
					Dockerfile: &dockerFile,
				},
				Strategy: buildv1.BuildStrategy{
					Type: buildv1.DockerBuildStrategyType,
					DockerStrategy: &buildv1.DockerBuildStrategy{
						ImageOptimizationPolicy: &optimizationPol,
						BuildArgs:               build.Spec.BuildArgs,
						Env:                     build.Spec.Envs,
					},
				},
				Output: buildv1.BuildOutput{
					To: &corev1.ObjectReference{
						Namespace: build.Namespace,
						Name:      workflowdef.GetWorkflowAppImageNameTag(workflow),
						Kind:      imageStreamTagKind,
					},
				},
				Resources: build.Spec.Resources,
			},
		},
	}
}

func (o *openshiftBuilderManager) addExternalResources(config *buildv1.BuildConfig, workflow *operatorapi.SonataFlow) error {
	if len(workflow.Spec.Resources.ConfigMaps) == 0 {
		return nil
	}
	var configMapSources []buildv1.ConfigMapBuildSource
	for _, workflowRes := range workflow.Spec.Resources.ConfigMaps {
		configMapSources = append(configMapSources, buildv1.ConfigMapBuildSource{
			ConfigMap:      workflowRes.ConfigMap,
			DestinationDir: workflowRes.WorkflowPath,
		})
	}
	config.Spec.Source.ConfigMaps = configMapSources
	return nil
}

func (o *openshiftBuilderManager) Reconcile(build *operatorapi.SonataFlowBuild) (err error) {
	var openshiftBuild *buildv1.Build

	if build.Status.BuildPhase == operatorapi.BuildPhaseNone ||
		build.Status.BuildPhase == operatorapi.BuildPhaseInitialization {

		// guard to avoid spamming multiple builds
		if openshiftBuild, err = o.fetchOpenShiftBuildRef(build); err != nil {
			return err
		}
		if openshiftBuild.Status.Phase == buildv1.BuildPhaseNew ||
			openshiftBuild.Status.Phase == buildv1.BuildPhaseRunning ||
			openshiftBuild.Status.Phase == buildv1.BuildPhasePending {
			build.Status.BuildPhase = operatorapi.BuildPhaseRunning
			return nil
		}

		// we push another one
		workflow, err := o.fetchWorkflowForBuild(build)
		if err != nil {
			return err
		}
		// kick a new build
		openshiftBuild, err = o.pushNewOpenShiftBuildForWorkflow(build, workflow)
		if err != nil {
			return err
		}
		build.Status.BuildPhase = operatorapi.BuildPhaseScheduling
		build.Status.ImageTag = openshiftBuild.Status.OutputDockerImageReference
		return build.Status.SetInnerBuild(kubeutil.ToTypedLocalReference(openshiftBuild))
	}

	if openshiftBuild, err = o.fetchOpenShiftBuildRef(build); err != nil {
		return err
	}
	if openshiftBuild == nil {
		build.Status.BuildPhase = operatorapi.BuildPhaseInitialization
		return nil
	}

	// Checks the phase
	build.Status.BuildPhase = openshiftBuildPhaseMatrix[openshiftBuild.Status.Phase]
	if openshiftBuild.Status.Phase == buildv1.BuildPhaseError {
		build.Status.Error = openshiftBuild.Status.Message
	}
	build.Status.ImageTag = openshiftBuild.Status.OutputDockerImageReference

	return build.Status.SetInnerBuild(kubeutil.ToTypedLocalReference(openshiftBuild))
}

func (o *openshiftBuilderManager) fetchOpenShiftBuildRef(build *operatorapi.SonataFlowBuild) (*buildv1.Build, error) {
	openshiftBuild := &buildv1.Build{}
	refOpenShiftBuild := &corev1.TypedLocalObjectReference{}
	if err := build.Status.GetInnerBuild(refOpenShiftBuild); err != nil {
		return nil, err
	}
	if err := o.client.Get(o.ctx, types.NamespacedName{Name: refOpenShiftBuild.Name, Namespace: build.Namespace}, openshiftBuild); err != nil {
		if errors.IsNotFound(err) {
			return openshiftBuild, nil
		}
		return nil, err
	}
	return openshiftBuild, nil
}

// TODO: this should be from fileS, in this case we can TAR everything in a temp directory within the operator pod fs and push
// TODO: for now, we mount the CMs from the devmode into the build and push only the bytes for the workflow definition from memory
func (o *openshiftBuilderManager) pushNewOpenShiftBuildForWorkflow(build *operatorapi.SonataFlowBuild, workflow *operatorapi.SonataFlow) (*buildv1.Build, error) {
	options := &buildv1.BinaryBuildRequestOptions{
		ObjectMeta: metav1.ObjectMeta{
			Name: build.Name, Namespace: build.Namespace,
		},
		AsFile:  workflow.Name + workflowdef.KogitoWorkflowJSONFileExt,
		Message: defaultBuildMessageTrigger,
	}
	workflowDef, err := workflowdef.GetJSONWorkflow(workflow, o.ctx)
	if err != nil {
		return nil, err
	}

	result := &buildv1.Build{}
	err = o.buildClient.RESTClient().Post().
		Namespace(build.Namespace).
		Resource("buildconfigs").
		Name(build.Name).
		SubResource("instantiatebinary").
		Body(strings.NewReader(string(workflowDef))).
		VersionedParams(options, runtime.NewParameterCodec(o.client.Scheme())).
		Do(context.TODO()).
		Into(result)

	return result, err
}
