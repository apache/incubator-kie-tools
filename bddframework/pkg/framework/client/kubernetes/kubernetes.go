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

package kubernetes

import (
	"github.com/kiegroup/kogito-cloud-operator/core/client"
	"github.com/kiegroup/kogito-cloud-operator/core/logger"
)

var log = logger.GetLogger("kubernetes_client")

// Namespace will fetch the inner Kubernetes API with a default client
func Namespace() NamespaceInterface {
	return newNamespace(&client.Client{})
}

// NamespaceC will use a defined client to fetch the Kubernetes API
func NamespaceC(c *client.Client) NamespaceInterface {
	return newNamespace(c)
}

// Resource will fetch the inner API for any Kubernetes resource with a default client
func Resource() ResourceInterface {
	return newResource(&client.Client{})
}

// ResourceC will use a defined client to fetch the Kubernetes API
func ResourceC(c *client.Client) ResourceInterface {
	return newResource(c)
}

// Pod will fetch the inner API for Kubernetes pod resource with a default client
func Pod() PodInterface {
	return newPod(&client.Client{})
}

// PodC will use a defined client to fetch the Kubernetes pod resources
func PodC(c *client.Client) PodInterface {
	return newPod(c)
}

// Event will fetch the inner API for Kubernetes event resource with a default client
func Event() EventInterface {
	return newEvent(&client.Client{})
}

// EventC will use a defined client to fetch the Kubernetes event resources
func EventC(c *client.Client) EventInterface {
	return newEvent(c)
}
