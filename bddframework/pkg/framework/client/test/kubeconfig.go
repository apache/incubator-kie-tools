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

package test

import (
	"fmt"
	"k8s.io/client-go/tools/clientcmd"
	clientcmdapi "k8s.io/client-go/tools/clientcmd/api"
	"os"
)

const (
	tempKubeEnvConfig = "/tmp/.kube/config"
)

// OverrideDefaultKubeConfigEmptyContext same as OverrideDefaultKubeConfigWithNamespace, but sets default namespace to "default"
func OverrideDefaultKubeConfigEmptyContext() (kubeconfigfile string, rollbackEnvOverride func()) {
	return OverrideDefaultKubeConfigWithNamespace("default")
}

// OverrideDefaultKubeConfigWithNamespace overrides the env var variable meant to point to the kube config file with a temporary file to be used in tests
// You must call defer with rollbackEnvOverride func to switch back to the original value and not jeopardize the local configuration
func OverrideDefaultKubeConfigWithNamespace(namespace string) (kubeconfigfile string, rollbackEnvOverride func()) {
	defaultConfig := clientcmdapi.NewConfig()
	defaultConfig.CurrentContext = namespace + "/cluster:8080/user"
	defaultConfig.Contexts[defaultConfig.CurrentContext] = clientcmdapi.NewContext()
	defaultConfig.Contexts[defaultConfig.CurrentContext].Namespace = namespace
	defaultConfig.Contexts[defaultConfig.CurrentContext].Cluster = "cluster:8080"
	defaultConfig.Contexts[defaultConfig.CurrentContext].AuthInfo = "user"
	if err := clientcmd.WriteToFile(*defaultConfig, tempKubeEnvConfig); err != nil {
		panic(fmt.Errorf("Impossible to write default kubeclient config: %s ", err))
	}
	return OverrideDefaultKubeConfig()
}

// OverrideDefaultKubeConfig overrides the default KUBECONFIG env var to point to a temporary file, does not create any context.
func OverrideDefaultKubeConfig() (kubeconfigfile string, rollbackEnvOverride func()) {
	oldEnvVar := os.Getenv(clientcmd.RecommendedConfigPathEnvVar)
	os.Setenv(clientcmd.RecommendedConfigPathEnvVar, tempKubeEnvConfig)
	return tempKubeEnvConfig, func() {
		if err := os.Remove(tempKubeEnvConfig); err != nil {
			if !os.IsNotExist(err) {
				panic(fmt.Errorf("Impossible to remove file %s: %s ", tempKubeEnvConfig, err))
			}
		}
		os.Setenv(clientcmd.RecommendedConfigPathEnvVar, oldEnvVar)
	}
}
