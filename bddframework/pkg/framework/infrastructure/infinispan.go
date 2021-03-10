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
	ispn "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"gopkg.in/yaml.v2"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	// Default Infinispan port
	defaultInfinispanPort = 11222
	// InfinispanSecretUsernameKey is the secret username key set in the linked secret
	InfinispanSecretUsernameKey = "username"
	// InfinispanSecretPasswordKey is the secret password key set in the linked secret
	InfinispanSecretPasswordKey = "password"
	// Default Infinispan user
	defaultInfinispanUser = "developer"
	// InfinispanIdentityFileName is the name of YAML file containing list of Infinispan credentials
	InfinispanIdentityFileName = "identities.yaml"

	// InfinispanKind CRD Kind for Infinispan server (as defined by Infinispan Operator)
	InfinispanKind = "Infinispan"

	// InfinispanInstanceName is the default name for Infinispan managed by KogitoInfra
	InfinispanInstanceName = "kogito-infinispan"
)

// InfinispanHandler ...
type InfinispanHandler interface {
	FetchInfinispanInstance(key types.NamespacedName) (*ispn.Infinispan, error)
	IsInfinispanAvailable() bool
	FetchInfinispanInstanceURI(key types.NamespacedName) (string, error)
	GetInfinispanCredential(infinispanInstance *ispn.Infinispan) (*InfinispanCredential, error)
}

type infinispanHandler struct {
	*operator.Context
}

var (
	// InfinispanAPIVersion CRD API group version for Infinispan server (as defined by Infinispan Operator)
	InfinispanAPIVersion = ispn.SchemeGroupVersion.String()
)

// InfinispanIdentity is the struct for the secret holding the credential for the Infinispan server
type InfinispanIdentity struct {
	Credentials []InfinispanCredential `yaml:"credentials"`
}

// InfinispanCredential holds the information to authenticate into an infinispan server
type InfinispanCredential struct {
	Username string `yaml:"username"`
	Password string `yaml:"password"`
}

// NewInfinispanHandler ...
func NewInfinispanHandler(context *operator.Context) InfinispanHandler {
	return &infinispanHandler{
		context,
	}
}

func (i *infinispanHandler) FetchInfinispanInstance(key types.NamespacedName) (*ispn.Infinispan, error) {
	i.Log.Debug("fetching deployed kogito infinispan instance")
	infinispanInstance := &ispn.Infinispan{}
	if exits, err := kubernetes.ResourceC(i.Client).FetchWithKey(key, infinispanInstance); err != nil {
		i.Log.Error(err, "Error occurs while fetching infinispan instance")
		return nil, err
	} else if !exits {
		i.Log.Debug("Infinispan instance is not exists")
		return nil, nil
	} else {
		i.Log.Debug("Infinispan instance found")
		return infinispanInstance, nil
	}
}

// IsInfinispanAvailable checks whether Infinispan CRD is available or not
func (i *infinispanHandler) IsInfinispanAvailable() bool {
	return i.Client.HasServerGroup(ispn.SchemeGroupVersion.Group)
}

// FetchInfinispanInstanceURI provide infinispan URI for given instance name
func (i *infinispanHandler) FetchInfinispanInstanceURI(key types.NamespacedName) (string, error) {
	i.Log.Debug("Fetching kogito infinispan instance URI.")
	service := &corev1.Service{}
	if exits, err := kubernetes.ResourceC(i.Client).FetchWithKey(key, service); err != nil {
		return "", err
	} else if !exits {
		return "", fmt.Errorf("service with name %s not exist for Infinispan instance in given namespace %s", key.Name, key.Namespace)
	} else {
		for _, port := range service.Spec.Ports {
			if port.TargetPort.IntVal == defaultInfinispanPort {
				uri := fmt.Sprintf("%s:%d", service.Name, port.TargetPort.IntVal)
				i.Log.Debug("", "kogito infinispan instance URI", uri)
				return uri, nil
			}
		}
		return "", fmt.Errorf("Infinispan default port (%d) not found in service %s ", defaultInfinispanPort, service.Name)
	}
}

// GetInfinispanCredential gets the credential of the Infinispan server deployed with the Kogito Operator
func (i *infinispanHandler) GetInfinispanCredential(infinispanInstance *ispn.Infinispan) (*InfinispanCredential, error) {
	secret := &corev1.Secret{ObjectMeta: metav1.ObjectMeta{Name: infinispanInstance.GetSecretName(), Namespace: infinispanInstance.Namespace}}
	if exists, err := kubernetes.ResourceC(i.Client).Fetch(secret); err != nil {
		return nil, err
	} else if exists {
		return getDefaultInfinispanCredential(secret)
	}
	i.Log.Warn("Infinispan credential not found", "secret", infinispanInstance.GetSecretName())
	return nil, nil
}

// getDefaultInfinispanCredential will return the credential to be used by internal services
func getDefaultInfinispanCredential(infinispanSecret *corev1.Secret) (*InfinispanCredential, error) {
	return findInfinispanCredentialByUsernameOrFirst(defaultInfinispanUser, infinispanSecret)
}

// findInfinispanCredentialByUsernameOrFirst fetches the default credential in a infinispan operator generated cluster or first one found
func findInfinispanCredentialByUsernameOrFirst(username string, infinispanSecret *corev1.Secret) (*InfinispanCredential, error) {
	secretFileData := infinispanSecret.Data[InfinispanIdentityFileName]
	identity := &InfinispanIdentity{}
	if len(secretFileData) == 0 {
		return &InfinispanCredential{
			Username: string(infinispanSecret.Data[InfinispanSecretUsernameKey]),
			Password: string(infinispanSecret.Data[InfinispanSecretPasswordKey]),
		}, nil
	}
	err := yaml.Unmarshal(secretFileData, identity)
	if err != nil {
		return nil, err
	}
	for _, c := range identity.Credentials {
		if c.Username == username {
			return &c, nil
		}
	}
	if len(identity.Credentials) == 1 {
		return &identity.Credentials[0], nil
	}
	return nil, nil
}
