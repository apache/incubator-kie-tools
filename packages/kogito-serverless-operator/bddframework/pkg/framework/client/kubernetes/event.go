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

	v1beta1 "k8s.io/api/events/v1beta1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"
)

// EventInterface has functions that interacts with pod object in the Kubernetes cluster
type EventInterface interface {
	// Retrieve all events from a namespace
	GetEvents(namespace string) (*v1beta1.EventList, error)
}

type event struct {
	client *client.Client
}

func newEvent(c *client.Client) EventInterface {
	return &event{
		client: c,
	}
}

func (event *event) GetEvents(namespace string) (*v1beta1.EventList, error) {
	opts := metav1.ListOptions{}
	return event.client.KubernetesExtensionCli.EventsV1beta1().Events(namespace).List(context.TODO(), opts)
}
