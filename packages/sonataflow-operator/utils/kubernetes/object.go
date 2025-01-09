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
	v1 "k8s.io/api/core/v1"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
)

// IsObjectNew verifies if the given object hasn't been created in the cluster
func IsObjectNew(object ctrl.Object) bool {
	// UID should be enough, but we check for resourceVersion because the Fake client won't set UIDs, failing our tests
	return len(object.GetUID()) == 0 && len(object.GetResourceVersion()) == 0
}

// ToTypedLocalReference ...
func ToTypedLocalReference(object ctrl.Object) *v1.TypedLocalObjectReference {
	apiGroup := object.GetObjectKind().GroupVersionKind().GroupVersion().String()
	return &v1.TypedLocalObjectReference{
		APIGroup: &apiGroup,
		Kind:     object.GetObjectKind().GroupVersionKind().Kind,
		Name:     object.GetName(),
	}

}
