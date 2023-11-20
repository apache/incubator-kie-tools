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

package api

import (
	"k8s.io/apimachinery/pkg/types"
)

// ObjectReference is a subset of the kubernetes k8s.io/apimachinery/pkg/apis/meta/v1.Object interface.
// Objects in this API not necessarily represent Kubernetes objects, but this structure can help when needed.
type ObjectReference struct {
	Namespace string `json:"namespace,omitempty"`
	Name      string `json:"name,omitempty"`
}

func (o *ObjectReference) GetName() string {
	return o.Name
}

func (o *ObjectReference) GetNamespace() string {
	return o.Namespace
}

func (o *ObjectReference) GetObjectKey() types.NamespacedName {
	return types.NamespacedName{Name: o.Name, Namespace: o.Namespace}
}
