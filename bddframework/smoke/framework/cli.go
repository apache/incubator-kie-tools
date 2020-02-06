// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

import (
	"fmt"
	"os"
	"os/exec"
)

// CheckCliBinaryExist checks if the CLI binary does exist
func CheckCliBinaryExist() (bool, error) {
	path, err := getEnvOperatorCliPath()
	if err != nil {
		return false, err
	}

	if _, err := os.Stat(path); err != nil {
		if os.IsNotExist(err) {
			return false, nil
		}
		return false, err
	}
	return true, nil
}

// ExecuteCliCommand executes a kogito cli command for a given namespace
func ExecuteCliCommand(namespace string, args ...string) (string, error) {
	GetLogger(namespace).Infof("Execute CLI %v", args)
	path, err := getEnvOperatorCliPath()
	if err != nil {
		return "", err
	}
	out, err := exec.Command(path, args...).Output()
	GetLogger(namespace).Debugf("output command: %s", string(out[:]))
	return string(out[:]), err
}

// ExecuteCliCommandInNamespace executes a kogito cli command in a specific namespace
func ExecuteCliCommandInNamespace(namespace string, args ...string) (string, error) {
	args = append(args, "-p", namespace)
	return ExecuteCliCommand(namespace, args...)
}

// CliDeployQuarkusExample deploy a Quarkus example with the CLI
func CliDeployQuarkusExample(namespace, appName, contextDir string, native, persistence bool) error {
	GetLogger(namespace).Infof("CLI Deploy quarkus example %s with name %s, native %v and persistence %v", contextDir, appName, native, persistence)
	return CliDeployExample(namespace, appName, contextDir, "quarkus", native, persistence)
}

// CliDeploySpringBootExample deploys a Spring boot example with the CLI
func CliDeploySpringBootExample(namespace, appName, contextDir string, persistence bool) error {
	GetLogger(namespace).Infof("CLI Deploy spring boot example %s with name %s and persistence %v", contextDir, appName, persistence)
	return CliDeployExample(namespace, appName, contextDir, "springboot", false, persistence)
}

// CliDeployExample deploys an example with the CLI
func CliDeployExample(namespace, appName, contextDir, runtime string, native, persistence bool) error {
	cmd := []string{"deploy-service", appName, getEnvExamplesRepositoryURI()}

	cmd = append(cmd, "-c", contextDir)
	cmd = append(cmd, "-r", runtime)
	if native {
		cmd = append(cmd, "--native")
	}
	if ref := getEnvExamplesRepositoryRef(); ref != "" {
		cmd = append(cmd, "-b", ref)
	}

	if mavenMirrorURL := getEnvMavenMirrorURL(); mavenMirrorURL != "" {
		cmd = append(cmd, "--maven-mirror-url", mavenMirrorURL)
	}

	if persistence {
		cmd = append(cmd, "--install-infinispan", "Always")
		cmd = append(cmd, "--build-env", fmt.Sprintf("%s=-Ppersistence", mavenArgsAppendEnvVar))
	}

	cmd = append(cmd, "--image-version", getEnvImageVersion())

	_, err := ExecuteCliCommandInNamespace(namespace, cmd...)
	return err
}

// CliInstallKogitoJobsService installs the Kogito Jobs Service
func CliInstallKogitoJobsService(namespace string, replicas int, persistence bool) error {
	cmd := []string{"install", "jobs-service"}

	if persistence {
		cmd = append(cmd, "--enable-persistence")
	}

	_, err := ExecuteCliCommandInNamespace(namespace, cmd...)
	return err

}
