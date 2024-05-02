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

package framework

import (
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api/app/v1beta1"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
	bddtypes "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/types"
)

// InstallService install the Kogito Service component
func InstallService(serviceHolder *bddtypes.KogitoServiceHolder, installerType InstallerType, cliDeploymentName string) error {
	return installOrDeployService(serviceHolder, installerType, "install", cliDeploymentName)
}

// DeployService deploy the Kogito Service component
func DeployService(serviceHolder *bddtypes.KogitoServiceHolder, installerType InstallerType) error {
	return installOrDeployService(serviceHolder, installerType, "deploy", serviceHolder.GetName())
}

// InstallOrDeployService the Kogito Service component
func installOrDeployService(serviceHolder *bddtypes.KogitoServiceHolder, installerType InstallerType, cliDeployCommand, cliDeploymentName string) error {
	GetLogger(serviceHolder.GetNamespace()).Info("Installing kogito service", "name", serviceHolder.GetName(), "installerType", installerType, "replicas", *serviceHolder.GetSpec().GetReplicas())
	var err error
	switch installerType {
	case CLIInstallerType:
		if err = cliInstall(serviceHolder, cliDeployCommand, cliDeploymentName); err != nil {
			return err
		}
		if err = patchKogitoProbes(serviceHolder); err != nil {
			return err
		}
	case CRInstallerType:
		err = crInstall(serviceHolder)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}

	if err == nil {
		err = OnKogitoServiceDeployed(serviceHolder.GetNamespace(), serviceHolder)
	}

	return err
}

// WaitForService waits that the service has a certain number of replicas
func WaitForService(namespace string, serviceName string, replicas int, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, serviceName+" running", timeoutInMin,
		func() (bool, error) {
			deployment, err := GetDeployment(namespace, serviceName)
			if err != nil {
				return false, err
			}
			if deployment == nil {
				return false, nil
			}
			return deployment.Status.Replicas == int32(replicas) && deployment.Status.AvailableReplicas == int32(replicas), nil
		})
}

// NewObjectMetadata creates a new Object Metadata object.
func NewObjectMetadata(namespace string, name string) metav1.ObjectMeta {
	return metav1.ObjectMeta{
		Name:      name,
		Namespace: namespace,
	}
}

// NewKogitoServiceSpec creates a new Kogito Service Spec object.
func NewKogitoServiceSpec(replicas int32, fullImage string, defaultImageName string) v1beta1.KogitoServiceSpec {
	return v1beta1.KogitoServiceSpec{
		Replicas: &replicas,
		Image:    NewImageOrDefault(fullImage, defaultImageName),
		// Sets insecure image registry as service images can be stored in insecure registries
		InsecureImageRegistry: true,
		// Extends the probe interval for slow test environment
		Probes: v1beta1.KogitoProbe{
			ReadinessProbe: corev1.Probe{
				FailureThreshold: 12,
			},
			LivenessProbe: corev1.Probe{
				FailureThreshold: 12,
			},
			StartupProbe: corev1.Probe{
				FailureThreshold: 12,
			},
		},
	}
}

// NewImageOrDefault Returns Image parsed from provided image tag or created from configuration options
func NewImageOrDefault(fullImage string, defaultImageName string) string {
	if len(fullImage) > 0 {
		return fullImage
	}
	image := &api.Image{
		Domain: config.GetServicesImageRegistry(),
		Name:   defaultImageName,
		Tag:    config.GetServicesImageVersion(),
	}
	// Update image name with suffix if provided
	if len(config.GetServicesImageNameSuffix()) > 0 {
		image.Name = fmt.Sprintf("%s-%s", image.Name, config.GetServicesImageNameSuffix())
	}
	AppendImageDefaultValues(image)

	return ConvertImageToImageTag(*image)
}

func crInstall(serviceHolder *bddtypes.KogitoServiceHolder) error {
	if err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(serviceHolder.KogitoService); err != nil {
		return fmt.Errorf("Error creating service: %v", err)
	}
	return nil
}

func cliInstall(serviceHolder *bddtypes.KogitoServiceHolder, cliDeployCommand, cliDeploymentName string) error {
	return fmt.Errorf("not supported")
}

// OnKogitoServiceDeployed is called when a service deployed.
func OnKogitoServiceDeployed(namespace string, service api.KogitoService) error {
	if !IsOpenshift() {
		return ExposeServiceOnKubernetes(namespace, service.GetName())
	}

	return nil
}

// Kogito CLI doesn't contain all the probe configuration options which are needed to alter the deployments for slow environments. Therefore it is needed to patch CRs directly.
func patchKogitoProbes(serviceHolder *bddtypes.KogitoServiceHolder) error {
	var patched api.KogitoService
	var err error
	for i := 0; i < 3; i++ {
		patched, err = newKogitoService(serviceHolder.KogitoService)
		if err != nil {
			return err
		}

		// Fetch deployed service
		var exists bool
		if exists, err = kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Namespace: serviceHolder.GetNamespace(), Name: serviceHolder.GetName()}, patched); err != nil {
			return fmt.Errorf("Error fetching service %s in namespace %s: %v", serviceHolder.GetName(), serviceHolder.GetNamespace(), err)
		} else if !exists {
			return fmt.Errorf("Service %s in namespace %s doesn't exist", serviceHolder.GetName(), serviceHolder.GetNamespace())
		}

		// Set probe configuration
		patched.GetSpec().SetProbes(serviceHolder.GetSpec().GetProbes())

		// Update deployed service
		if err = kubernetes.ResourceC(kubeClient).Update(patched); err == nil {
			return nil
		}
	}
	return fmt.Errorf("Error updating service %s in namespace %s: %v", patched.GetName(), patched.GetNamespace(), err)
}

// Return new empty KogitoService based on same type as parameter
func newKogitoService(s api.KogitoService) (api.KogitoService, error) {
	switch v := s.GetSpec().(type) {
	case *v1beta1.KogitoSupportingServiceSpec:
		return &v1beta1.KogitoSupportingService{}, nil
	default:
		return nil, fmt.Errorf("Type %T not defined", v)
	}
}
