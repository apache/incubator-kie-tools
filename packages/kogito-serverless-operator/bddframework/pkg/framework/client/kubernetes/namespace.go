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

	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"
)

// NamespaceInterface has functions that interacts with namespace object in the Kubernetes cluster
type NamespaceInterface interface {
	Fetch(name string) (*corev1.Namespace, error)
	Create(name string) (*corev1.Namespace, error)
	CreateIfNotExists(name string) (*corev1.Namespace, error)
}

type namespace struct {
	client *client.Client
}

func newNamespace(c *client.Client) *namespace {
	return &namespace{
		client: c,
	}
}

func (n *namespace) Fetch(name string) (*corev1.Namespace, error) {
	log.Debug("About to fetch namespace from cluster", "namespace", name)
	ns := &corev1.Namespace{ObjectMeta: metav1.ObjectMeta{Name: name}}
	if err := n.client.ControlCli.Get(context.TODO(), types.NamespacedName{Name: name}, ns); err != nil && errors.IsNotFound(err) {
		log.Debug("Not found", "namespace", name)
		return nil, nil
	} else if err != nil {
		return nil, err
	}
	return ns, nil
}

func (n *namespace) Create(name string) (*corev1.Namespace, error) {
	ns := &corev1.Namespace{ObjectMeta: metav1.ObjectMeta{Name: name}}
	if err := n.client.ControlCli.Create(context.TODO(), ns); err != nil {
		return nil, err
	}
	return ns, nil
}

func (n *namespace) CreateIfNotExists(name string) (*corev1.Namespace, error) {
	if ns, err := n.Fetch(name); err != nil {
		return nil, err
	} else if ns != nil {
		return ns, nil
	}
	ns, err := n.Create(name)
	if err != nil {
		return nil, err
	}
	return ns, nil
}
