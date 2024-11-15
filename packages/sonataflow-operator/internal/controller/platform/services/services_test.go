// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package services

import (
	"testing"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
)

func TestMergeContainerSpec(t *testing.T) {
	container := &corev1.Container{
		Env: []corev1.EnvVar{{Name: "var1", Value: "value1"}, {Name: "var2", Value: "value2"}},
	}
	containerSpec := &operatorapi.ContainerSpec{
		Env: []corev1.EnvVar{{Name: "var1", Value: "value1Changed"}, {Name: "var3", Value: "value3"}},
	}
	result, err := mergeContainerSpec(container, containerSpec)
	assert.Nil(t, err)
	assert.Len(t, result.Env, 3)
	assert.Equal(t, result.Env[0], corev1.EnvVar{Name: "var1", Value: "value1"})
	assert.Equal(t, result.Env[1], corev1.EnvVar{Name: "var2", Value: "value2"})
	assert.Equal(t, result.Env[2], corev1.EnvVar{Name: "var3", Value: "value3"})
}

func TestMergeContainerPreservingEnvVars(t *testing.T) {
	container1 := &corev1.Container{
		Env: []corev1.EnvVar{{Name: "var1", Value: "value1"}, {Name: "var2", Value: "value2"}},
	}
	container2 := &corev1.Container{
		Env: []corev1.EnvVar{{Name: "var1", Value: "value1Changed"}, {Name: "var3", Value: "value3"}},
	}
	err := mergeContainerPreservingEnvVars(container1, container2)
	assert.Nil(t, err)
	assert.Len(t, container1.Env, 3)
	assert.Equal(t, container1.Env[0], corev1.EnvVar{Name: "var1", Value: "value1"})
	assert.Equal(t, container1.Env[1], corev1.EnvVar{Name: "var2", Value: "value2"})
	assert.Equal(t, container1.Env[2], corev1.EnvVar{Name: "var3", Value: "value3"})
}
