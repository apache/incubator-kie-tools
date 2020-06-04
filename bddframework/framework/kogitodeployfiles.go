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

// DeploySourceFilesFromPath deploys source files from a path
func DeploySourceFilesFromPath(namespace, serviceName, path string) error {
	GetLogger(namespace).Infof("Deploy example %s with source files in path %s", serviceName, path)

	kogitoApp := GetKogitoAppStub(namespace, "quarkus", serviceName)
	kogitoApp.Spec.Build.GitSource.URI = path

	return DeployService(namespace, CLIInstallerType, kogitoApp)
}
