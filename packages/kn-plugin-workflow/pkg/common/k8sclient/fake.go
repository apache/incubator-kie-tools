/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package k8sclient

import (
	"fmt"
	"github.com/spf13/afero"
	v1 "k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/util/yaml"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/dynamic/fake"
	"strings"
)

type Fake struct {
	FS afero.Fs
}

var currentDynamicClient = initDynamicClient()

func initDynamicClient() dynamic.Interface {
	scheme := runtime.NewScheme()
	fakeDynamicClient := fake.NewSimpleDynamicClient(scheme)
	return fakeDynamicClient
}

func (m Fake) FakeDynamicClient() (dynamic.Interface, error) {
	return currentDynamicClient, nil
}

func (m Fake) GetCurrentNamespace() (string, error) {
	return "default", nil
}

func (m Fake) FakeParseYamlFile(path string) ([]unstructured.Unstructured, error) {
	data, err := afero.ReadFile(m.FS, path)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to read YAML file: %w", err)
	}
	decoder := yaml.NewYAMLOrJSONDecoder(strings.NewReader(string(data)), 4096)
	result := []unstructured.Unstructured{}
	for {
		rawObj := &unstructured.Unstructured{}
		err := decoder.Decode(rawObj)
		if err != nil {
			break
		}
		result = append(result, *rawObj)
	}
	return result, nil
}

func (m Fake) GetDeploymentStatus(namespace, deploymentName string) (v1.DeploymentStatus, error) {
	return v1.DeploymentStatus{}, nil
}

func (m Fake) PortForward(namespace, serviceName, portFrom, portTo string, onReady func()) error  {
	return nil
}
