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
	"fmt"
	"strings"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	kogitocli "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"

	"k8s.io/apimachinery/pkg/util/yaml"
)

const (
	// Name is the name of the Kogito Operator deployed in a namespace
	KogitoOperatorName = "kogito-operator"
)

// ResourceInterface has functions that interacts with any resource object in the Kubernetes cluster
type ResourceInterface interface {
	ResourceReader
	ResourceWriter
	// CreateIfNotExists will fetch for the object resource in the Kubernetes cluster, if not exists, will create it.
	CreateIfNotExists(resource client.Object) (err error)
	// CreateIfNotExistsForOwner sets the controller owner to the given resource and creates if it not exists.
	// If the given resource exists, won't update the object with the given owner.
	CreateIfNotExistsForOwner(resource client.Object, owner metav1.Object, scheme *runtime.Scheme) (err error)
	// CreateForOwner sets the controller owner to the given resource and creates the resource.
	CreateForOwner(resource client.Object, owner metav1.Object, scheme *runtime.Scheme) error
	// CreateFromYamlContent creates Kubernetes resources from a yaml string content
	CreateFromYamlContent(yamlContent, namespace string, resourceRef client.Object, beforeCreate func(object interface{})) error
}

type resource struct {
	ResourceReader
	ResourceWriter
}

func newResource(c *kogitocli.Client) *resource {
	return &resource{
		ResourceReader: ResourceReaderC(c),
		ResourceWriter: ResourceWriterC(c),
	}
}

func (r *resource) CreateIfNotExists(resource client.Object) error {
	log.Info("Create resource if not exists", "kind", resource.GetObjectKind().GroupVersionKind().Kind, "name", resource.GetName(), "namespace", resource.GetNamespace())

	if exists, err := r.ResourceReader.Fetch(resource); err == nil && !exists {
		return r.ResourceWriter.Create(resource)
	} else if err != nil {
		log.Error(err, "Failed to fetch object. ")
		return err
	}
	log.Info("Skip creating - object already exists")
	return nil
}

func (r *resource) CreateIfNotExistsForOwner(resource client.Object, owner metav1.Object, scheme *runtime.Scheme) error {
	err := controllerutil.SetControllerReference(owner, resource, scheme)
	if err != nil {
		return err
	}
	return r.CreateIfNotExists(resource)
}

func (r *resource) CreateForOwner(resource client.Object, owner metav1.Object, scheme *runtime.Scheme) error {
	err := controllerutil.SetControllerReference(owner, resource, scheme)
	if err != nil {
		return err
	}
	return r.ResourceWriter.Create(resource)
}

func (r *resource) CreateFromYamlContent(yamlFileContent, namespace string, resourceRef client.Object, beforeCreate func(object interface{})) error {
	docs := strings.Split(yamlFileContent, "---")
	for _, doc := range docs {
		if len(doc) <= 0 {
			log.Debug("Empty content ... Skipping it")
			continue
		}

		log.Debug("Create from yaml content", "content", doc)
		if err := yaml.NewYAMLOrJSONDecoder(strings.NewReader(doc), len([]byte(doc))).Decode(resourceRef); err != nil {
			return fmt.Errorf("Error while unmarshalling file: %v ", err)
		}

		if len(resourceRef.GetObjectKind().GroupVersionKind().Kind) <= 0 {
			log.Error(fmt.Errorf("Error while unmarshalling yaml content"), "Cannot parse yaml content into resources... Skipping it", "content", doc)
			continue
		}

		if namespace != "" {
			resourceRef.SetNamespace(namespace)
		}
		resourceRef.SetResourceVersion("")
		resourceRef.SetLabels(map[string]string{"app": KogitoOperatorName})

		log.Debug("Will create a new resource", "kind", resourceRef.GetObjectKind().GroupVersionKind().Kind, "name", resourceRef.GetName(), "namespace", resourceRef.GetNamespace())
		if beforeCreate != nil {
			beforeCreate(resourceRef)
		}
		if err := r.CreateIfNotExists(resourceRef); err != nil {
			return fmt.Errorf("Error creating object %s: %v ", resourceRef.GetName(), err)
		}
	}
	return nil
}
