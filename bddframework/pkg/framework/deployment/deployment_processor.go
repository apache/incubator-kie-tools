// Copyright 2022 Red Hat, Inc. and/or its affiliates
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

package deployment

import (
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/connector"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	v1 "k8s.io/api/apps/v1"
	v12 "k8s.io/api/core/v1"
	"k8s.io/utils/pointer"
)

// Processor ...
type Processor interface {
	Process() error
}

type deploymentProcessor struct {
	operator.Context
	deployment               *v1.Deployment
	runtimeHandler           manager.KogitoRuntimeHandler
	supportingServiceHandler manager.KogitoSupportingServiceHandler
}

// NewDeploymentProcessor ...
func NewDeploymentProcessor(context operator.Context, deployment *v1.Deployment, runtimeHandler manager.KogitoRuntimeHandler, supportingServiceHandler manager.KogitoSupportingServiceHandler) Processor {
	return &deploymentProcessor{
		Context:                  context,
		deployment:               deployment,
		runtimeHandler:           runtimeHandler,
		supportingServiceHandler: supportingServiceHandler,
	}
}

// Process function is called for every deployment managed or watched by operator
// e.g. crd or full deployment with the expected label
func (d *deploymentProcessor) Process() (err error) {
	if err = d.injectKogitoAnnotations(); err != nil {
		return err
	}
	if err = d.injectSupportingServiceEndpointIntoDeployment(); err != nil {
		return err
	}
	return d.injectSecurityContextDefaults()
}

func (d *deploymentProcessor) injectSupportingServiceEndpointIntoDeployment() error {
	urlHandler := connector.NewURLHandler(d.Context, d.runtimeHandler, d.supportingServiceHandler)
	if err := urlHandler.InjectDataIndexEndpointOnDeployment(d.deployment); err != nil {
		return err
	}
	if err := urlHandler.InjectJobsServiceEndpointOnDeployment(d.deployment); err != nil {
		return err
	}
	if err := urlHandler.InjectTrustyEndpointOnDeployment(d.deployment); err != nil {
		return err
	}
	return kubernetes.ResourceC(d.Client).Update(d.deployment)
}

func (d *deploymentProcessor) injectKogitoAnnotations() error {
	if len(d.Context.Version) == 0 {
		d.Log.Warn("Not able to get detect Kogito version, version annotation will not be set", "Version Label", framework.KogitoOperatorVersionAnnotation)
	} else {
		// annotate the given deployment with the operator version
		if d.deployment.Annotations == nil {
			d.deployment.Annotations = make(map[string]string)
		}
		if d.deployment.Spec.Template.Annotations == nil {
			d.deployment.Spec.Template.Annotations = make(map[string]string)
		}
		d.deployment.Annotations[framework.KogitoOperatorVersionAnnotation] = d.Context.Version
		d.deployment.Spec.Template.Annotations[framework.KogitoOperatorVersionAnnotation] = d.Context.Version
	}
	return nil
}

func (d *deploymentProcessor) injectSecurityContextDefaults() error {
	d.deployment.Spec.Template.Spec.SecurityContext = &v12.PodSecurityContext{
		RunAsNonRoot: pointer.Bool(true),
	}
	for index := range d.deployment.Spec.Template.Spec.Containers {
		d.deployment.Spec.Template.Spec.Containers[index].SecurityContext = &v12.SecurityContext{
			Capabilities: &v12.Capabilities{
				Drop: []v12.Capability{"ALL"},
			},
			Privileged:               pointer.Bool(false),
			RunAsNonRoot:             pointer.Bool(true),
			AllowPrivilegeEscalation: pointer.Bool(false),
		}
	}
	return nil
}
