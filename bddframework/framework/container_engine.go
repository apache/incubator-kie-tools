// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package framework

import "github.com/kiegroup/kogito-cloud-operator/test/config"

// ContainerEngine is the engine providing container and registry functionality
type ContainerEngine interface {
	// PullImage pull image from external registry to internal registry
	PullImage(imageTag string) ContainerEngine

	// PushImage push image from internal registry to external registry
	PushImage(imageTag string) ContainerEngine

	// BuildImage builds the container image from specified directory
	BuildImage(projectLocation, imageTag string) ContainerEngine

	// GetError returns error in case any execution failed
	GetError() error
}

type containerEngineStruct struct {
	engine           string
	buildCommand     string
	namespace        string
	supportTLSVerify bool
	err              error
}

var dockerContainerEngine = containerEngineStruct{
	engine:           "docker",
	supportTLSVerify: false,
	buildCommand:     "build",
}

var podmanContainerEngine = containerEngineStruct{
	engine:           "podman",
	supportTLSVerify: true,
	buildCommand:     "build",
}

var buildahContainerEngine = containerEngineStruct{
	engine:           "buildah",
	supportTLSVerify: true,
	buildCommand:     "build-using-dockerfile",
}

var containerEngines = map[string]containerEngineStruct{
	(dockerContainerEngine.engine):  dockerContainerEngine,
	(podmanContainerEngine.engine):  podmanContainerEngine,
	(buildahContainerEngine.engine): buildahContainerEngine,
}

// GetContainerEngine returns container engine based on test configuration
func GetContainerEngine(namespace string) ContainerEngine {
	containerEngine := containerEngines[config.GetContainerEngine()]

	containerEngine.namespace = namespace
	return &containerEngine
}

func (containerEngine *containerEngineStruct) PullImage(imageTag string) ContainerEngine {
	pullImageArgs := []string{"pull"}

	if containerEngine.supportTLSVerify {
		pullImageArgs = append(pullImageArgs, "--tls-verify=false")
	}

	pullImageArgs = append(pullImageArgs, imageTag)

	if containerEngine.err == nil {
		_, containerEngine.err = CreateCommand(containerEngine.engine, pullImageArgs...).WithLoggerContext(containerEngine.namespace).Execute()
	}
	return containerEngine
}

func (containerEngine *containerEngineStruct) PushImage(imageTag string) ContainerEngine {
	pushImageArgs := []string{"push"}

	if containerEngine.supportTLSVerify {
		pushImageArgs = append(pushImageArgs, "--tls-verify=false")
	}

	pushImageArgs = append(pushImageArgs, imageTag)

	if containerEngine.err == nil {
		_, containerEngine.err = CreateCommand(containerEngine.engine, pushImageArgs...).WithLoggerContext(containerEngine.namespace).Execute()
	}
	return containerEngine
}

func (containerEngine *containerEngineStruct) BuildImage(projectLocation, imageTag string) ContainerEngine {
	if containerEngine.err == nil {
		_, containerEngine.err = CreateCommand(containerEngine.engine, containerEngine.buildCommand, "--tag", imageTag, ".").InDirectory(projectLocation).WithLoggerContext(containerEngine.namespace).Execute()
	}
	return containerEngine
}

func (containerEngine *containerEngineStruct) GetError() error {
	return containerEngine.err
}
