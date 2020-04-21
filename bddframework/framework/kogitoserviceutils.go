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
	"strconv"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// InstallServiceWithoutCliFlags the Kogito Service component without any CLI flags.
func InstallServiceWithoutCliFlags(service v1alpha1.KogitoService, installerType InstallerType, cliName string) error {
	return InstallService(service, installerType, cliName, nil)
}

// InstallService the Kogito Service component
func InstallService(service v1alpha1.KogitoService, installerType InstallerType, cliName string, cliFlags []string) error {
	GetLogger(service.GetNamespace()).Infof("%s install %s with %d replicas", service.GetName(), installerType, *service.GetSpec().GetReplicas())
	switch installerType {
	case CLIInstallerType:
		return cliInstall(service, cliName, cliFlags)
	case CRInstallerType:
		return crInstall(service)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
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
func NewKogitoServiceSpec(replicas int32, fullImage string, defaultImage string) v1alpha1.KogitoServiceSpec {
	return v1alpha1.KogitoServiceSpec{
		Replicas: &replicas,
		Image:    newImageOrDefault(fullImage, defaultImage),
	}
}

// NewKogitoServiceStatus creates a new Kogito Service Status object.
func NewKogitoServiceStatus() v1alpha1.KogitoServiceStatus {
	return v1alpha1.KogitoServiceStatus{
		ConditionsMeta: v1alpha1.ConditionsMeta{
			Conditions: []v1alpha1.Condition{},
		},
	}
}

// GetCliFlags maps the parameters into a list of CLI flags.
func GetCliFlags(persistence, events bool) []string {
	cliFlags := []string{}

	if persistence {
		cliFlags = append(cliFlags, "--enable-persistence")
	}

	if events {
		cliFlags = append(cliFlags, "--enable-events")
	}

	return cliFlags
}

func newImageOrDefault(fullImage string, defaultImage string) v1alpha1.Image {
	if len(fullImage) > 0 {
		return framework.ConvertImageTagToImage(fullImage)
	}

	image := framework.ConvertImageTagToImage(defaultImage)
	image.Tag = config.GetServicesImageVersion()
	return image
}

func crInstall(service v1alpha1.KogitoService) error {
	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(service); err != nil {
		return fmt.Errorf("Error creating service: %v", err)
	}
	return nil
}

func cliInstall(service v1alpha1.KogitoService, cliName string, cliFlags []string) error {
	cmd := []string{"install", cliName}

	for _, cliFlag := range cliFlags {
		cmd = append(cmd, cliFlag)
	}

	cmd = append(cmd, "--image", framework.ConvertImageToImageTag(*service.GetSpec().GetImage()))
	cmd = append(cmd, "--replicas", strconv.Itoa(int(*service.GetSpec().GetReplicas())))

	_, err := ExecuteCliCommandInNamespace(service.GetNamespace(), cmd...)
	return err
}
