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

import v1 "k8s.io/api/core/v1"

func CreateOrReplaceEnv(container *v1.Container, name, value string) {
	found := false
	for i := range container.Env {
		if container.Env[i].Name == name {
			container.Env[i].Value = value
			found = true
		}
	}
	if !found {
		container.Env = append(container.Env, v1.EnvVar{
			Name:  name,
			Value: value,
		})
	}
}

// AddEnvIfNotPresent Adds and env variable to a container if not already present. Returns true if the variable didn't exist
// and was added, false in any other case.
func AddEnvIfNotPresent(container *v1.Container, envVar v1.EnvVar) bool {
	for i := range container.Env {
		if container.Env[i].Name == envVar.Name {
			return false
		}
	}
	container.Env = append(container.Env, envVar)
	return true
}
