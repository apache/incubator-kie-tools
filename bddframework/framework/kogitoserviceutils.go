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
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// KogitoServiceHolder Helper structure holding informations which are not available in KogitoService
type KogitoServiceHolder struct {
	v1alpha1.KogitoService
	Infinispan struct {
		Username string
		Password string
	}
}

// InstallServiceWithoutCliFlags the Kogito Service component without any CLI flags.
func InstallServiceWithoutCliFlags(serviceHolder *KogitoServiceHolder, installerType InstallerType, cliName string) error {
	return InstallService(serviceHolder, installerType, cliName, nil)
}

// InstallService the Kogito Service component
func InstallService(serviceHolder *KogitoServiceHolder, installerType InstallerType, cliName string, cliFlags []string) error {
	GetLogger(serviceHolder.GetNamespace()).Infof("%s install %s with %d replicas", serviceHolder.GetName(), installerType, *serviceHolder.GetSpec().GetReplicas())
	switch installerType {
	case CLIInstallerType:
		return cliInstall(serviceHolder, cliName, cliFlags)
	case CRInstallerType:
		return crInstall(serviceHolder)
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
func NewKogitoServiceSpec(replicas int32, fullImage string, defaultImageName string) v1alpha1.KogitoServiceSpec {
	return v1alpha1.KogitoServiceSpec{
		Replicas: &replicas,
		Image:    newImageOrDefault(fullImage, defaultImageName),
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

func newImageOrDefault(fullImage string, defaultImageName string) v1alpha1.Image {
	if len(fullImage) > 0 {
		return framework.ConvertImageTagToImage(fullImage)
	}

	image := v1alpha1.Image{}
	if len(config.GetServicesImageRegistry()) > 0 || len(config.GetServicesImageNamespace()) > 0 || len(config.GetServicesImageVersion()) > 0 {
		image.Domain = config.GetServicesImageRegistry()
		image.Namespace = config.GetServicesImageNamespace()
		image.Name = defaultImageName
		image.Tag = config.GetServicesImageVersion()

		if len(image.Domain) == 0 {
			image.Domain = infrastructure.DefaultImageRegistry
		}

		if len(image.Namespace) == 0 {
			image.Namespace = infrastructure.DefaultImageNamespace
		}

		if len(image.Tag) == 0 {
			image.Tag = infrastructure.GetRuntimeImageVersion()
		}
	}

	return image
}

func crInstall(serviceHolder *KogitoServiceHolder) error {
	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(serviceHolder.KogitoService); err != nil {
		return fmt.Errorf("Error creating service: %v", err)
	}
	return nil
}

func cliInstall(serviceHolder *KogitoServiceHolder, cliName string, cliFlags []string) error {
	cmd := []string{"install", cliName}

	for _, cliFlag := range cliFlags {
		cmd = append(cmd, cliFlag)
	}

	image := framework.ConvertImageToImageTag(*serviceHolder.GetSpec().GetImage())
	if len(image) > 0 {
		cmd = append(cmd, "--image", image)
	}

	cmd = append(cmd, "--replicas", strconv.Itoa(int(*serviceHolder.GetSpec().GetReplicas())))

	if infinispanAware, ok := serviceHolder.GetSpec().(v1alpha1.InfinispanAware); ok {
		infinispanProperties := infinispanAware.GetInfinispanProperties()
		if authRealm := infinispanProperties.AuthRealm; len(authRealm) > 0 {
			cmd = append(cmd, "--infinispan-authrealm", authRealm)
		}
		if saslMechanism := infinispanProperties.SaslMechanism; len(saslMechanism) > 0 {
			cmd = append(cmd, "--infinispan-sasl", string(saslMechanism))
		}
		if uri := infinispanProperties.URI; len(uri) > 0 {
			cmd = append(cmd, "--infinispan-url", uri)
		}

		if username := serviceHolder.Infinispan.Username; len(username) > 0 {
			cmd = append(cmd, "--infinispan-user", username)
		}
		if password := serviceHolder.Infinispan.Password; len(password) > 0 {
			cmd = append(cmd, "--infinispan-password", password)
		}
	}

	_, err := ExecuteCliCommandInNamespace(serviceHolder.GetNamespace(), cmd...)
	return err
}

//IsInfinispanUsernameSpecified Returns true if Infinispan username is specified
func (serviceHolder *KogitoServiceHolder) IsInfinispanUsernameSpecified() bool {
	return len(serviceHolder.Infinispan.Username) > 0
}
