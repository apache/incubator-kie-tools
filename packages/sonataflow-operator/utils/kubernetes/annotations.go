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
	"strconv"

	"sigs.k8s.io/controller-runtime/pkg/client"
)

// GetAnnotationAsBool returns the boolean value from the given annotation.
// If the annotation is not present or is there an error in the ParseBool conversion, returns false.
func GetAnnotationAsBool(object client.Object, key string) bool {
	if object.GetAnnotations() != nil {
		b, err := strconv.ParseBool(object.GetAnnotations()[key])
		if err != nil {
			return false
		}
		return b
	}
	return false
}

// SetAnnotation Safely set the annotation to the object
func SetAnnotation(object client.Object, key, value string) {
	if object.GetAnnotations() != nil {
		object.GetAnnotations()[key] = value
	} else {
		object.SetAnnotations(map[string]string{key: value})
	}
}
