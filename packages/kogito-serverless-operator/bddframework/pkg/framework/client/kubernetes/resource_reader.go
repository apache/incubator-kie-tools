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

package kubernetes

import (
	"context"
	"reflect"

	"github.com/RHsyseng/operator-utils/pkg/resource/read"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"

	kogitocli "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"
)

// ResourceReader interface to read kubernetes object
type ResourceReader interface {
	// FetchWithKey fetches and binds a resource from the Kubernetes cluster with the defined key. If not exists, returns false.
	FetchWithKey(key types.NamespacedName, resource client.Object) (exists bool, err error)
	// Fetch fetches and binds a resource with given name and namespace from the Kubernetes cluster. If not exists, returns false.
	Fetch(resource client.Object) (exists bool, err error)
	// ListWithNamespace fetches and binds a list resource from the Kubernetes cluster with the defined namespace.
	ListWithNamespace(namespace string, list client.ObjectList) error
	// ListWithNamespaceAndLabel same as ListWithNamespace, but also limit the query scope by the given labels
	ListWithNamespaceAndLabel(namespace string, list client.ObjectList, labels map[string]string) error
	// ListAll returns a map of Kubernetes resources organized by type, based on provided List objects
	ListAll(objectTypes []client.ObjectList, namespace string, ownerObject metav1.Object) (map[reflect.Type][]client.Object, error)
}

// ResourceReaderC provide ResourceReader reference
func ResourceReaderC(cli *kogitocli.Client) ResourceReader {
	return &resourceReader{
		client: cli,
	}
}

type resourceReader struct {
	client *kogitocli.Client
}

func (r *resourceReader) Fetch(resource client.Object) (bool, error) {
	return r.FetchWithKey(types.NamespacedName{Name: resource.GetName(), Namespace: resource.GetNamespace()}, resource)
}

func (r *resourceReader) FetchWithKey(key types.NamespacedName, resource client.Object) (bool, error) {
	log.Debug("About to fetch object", "name", key.Name, "namespace", key.Namespace)
	err := r.client.ControlCli.Get(context.TODO(), key, resource)
	if err != nil && errors.IsNotFound(err) {
		return false, nil
	} else if err != nil {
		return false, err
	}
	log.Debug("Found object", "kind", resource.GetObjectKind().GroupVersionKind().Kind, "name", key.Name, "namespace", key.Namespace, "Creation time", resource.GetCreationTimestamp())
	return true, nil
}

func (r *resourceReader) ListWithNamespace(namespace string, list client.ObjectList) error {
	err := r.client.ControlCli.List(context.TODO(), list, client.InNamespace(namespace))
	if err != nil {
		log.Error(err, "Failed to list resource.")
		return err
	}
	return nil
}

func (r *resourceReader) ListWithNamespaceAndLabel(namespace string, list client.ObjectList, labels map[string]string) error {
	err := r.client.ControlCli.List(context.TODO(), list, client.InNamespace(namespace), client.MatchingLabels(labels))
	if err != nil {
		log.Error(err, "Failed to list resource. ")
		return err
	}
	return nil
}

func (r *resourceReader) ListAll(objectTypes []client.ObjectList, namespace string, ownerObject metav1.Object) (map[reflect.Type][]client.Object, error) {
	reader := read.New(r.client.ControlCli).WithNamespace(namespace).WithOwnerObject(ownerObject)
	return reader.ListAll(objectTypes...)
}
