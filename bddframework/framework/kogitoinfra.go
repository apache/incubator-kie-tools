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

package framework

import (
	"fmt"

	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/test/framework/mappers"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
)

// InstallKogitoInfraComponent installs the desired component with the given installer type
func InstallKogitoInfraComponent(namespace string, installerType InstallerType, infra *v1beta1.KogitoInfra) error {
	GetLogger(namespace).Info("Installing kogito infra resource", "installType", installerType, "APIVersion", infra.Spec.Resource.APIVersion, "kind", infra.Spec.Resource.Kind)
	switch installerType {
	case CLIInstallerType:
		return cliInstallKogitoInfraComponent(namespace, infra)
	case CRInstallerType:
		return crInstallKogitoInfraComponent(infra)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
}

func crInstallKogitoInfraComponent(infra *v1beta1.KogitoInfra) error {
	if err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(infra); err != nil {
		return fmt.Errorf("Error creating KogitoInfra: %v", err)
	}
	return nil
}

func cliInstallKogitoInfraComponent(namespace string, infraResource *v1beta1.KogitoInfra) error {
	cmd := []string{"install", "infra", infraResource.Name}

	cmd = append(cmd, mappers.GetInfraCLIFlags(infraResource)...)

	_, err := ExecuteCliCommandInNamespace(namespace, cmd...)
	return err
}

// GetKogitoInfraResourceStub Get basic KogitoInfra stub with all needed fields initialized
func GetKogitoInfraResourceStub(namespace, name, targetResourceType, targetResourceName string) (*v1beta1.KogitoInfra, error) {
	infraResource, err := parseKogitoInfraResource(targetResourceType)
	if err != nil {
		return nil, err
	}
	infraResource.SetName(targetResourceName)

	return &v1beta1.KogitoInfra{
		ObjectMeta: NewObjectMetadata(namespace, name),
		Spec: v1beta1.KogitoInfraSpec{
			Resource: *infraResource,
		},
		Status: v1beta1.KogitoInfraStatus{
			Condition: v1beta1.KogitoInfraCondition{
				LastTransitionTime: v1.Now(),
			},
		},
	}, nil
}

// Converts infra resource from name to Resource struct
func parseKogitoInfraResource(targetResourceType string) (*v1beta1.Resource, error) {
	switch targetResourceType {
	case infrastructure.InfinispanKind:
		return &v1beta1.Resource{APIVersion: infrastructure.InfinispanAPIVersion, Kind: infrastructure.InfinispanKind}, nil
	case infrastructure.KafkaKind:
		return &v1beta1.Resource{APIVersion: infrastructure.KafkaAPIVersion, Kind: infrastructure.KafkaKind}, nil
	case infrastructure.KeycloakKind:
		return &v1beta1.Resource{APIVersion: infrastructure.KeycloakAPIVersion, Kind: infrastructure.KeycloakKind}, nil
	case infrastructure.MongoDBKind:
		return &v1beta1.Resource{APIVersion: infrastructure.MongoDBAPIVersion, Kind: infrastructure.MongoDBKind}, nil
	case infrastructure.KnativeEventingBrokerKind:
		return &v1beta1.Resource{APIVersion: infrastructure.KnativeEventingAPIVersion, Kind: infrastructure.KnativeEventingBrokerKind}, nil
	default:
		return nil, fmt.Errorf("Unknown KogitoInfra target resource type %s", targetResourceType)
	}
}

// WaitForKogitoInfraResource waits for the given KogitoInfra resource to be ready
func WaitForKogitoInfraResource(namespace, name string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("KogitoInfra %s status to be Success", name), timeoutInMin,
		func() (bool, error) {
			infraResource, err := getKogitoInfraResource(namespace, name)
			if err != nil {
				return false, err
			}
			if infraResource == nil {
				return false, nil
			}

			return infraResource.Status.Condition.Type == "Success" && infraResource.Status.Condition.Status == "True", nil
		})
}

// retrieves the KogitoInfra resource
func getKogitoInfraResource(namespace, name string) (*v1beta1.KogitoInfra, error) {
	infraResource := &v1beta1.KogitoInfra{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, infraResource); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for KogitoInfra %s: %v ", name, err)
	} else if !exists {
		return nil, nil
	}
	return infraResource, nil
}
