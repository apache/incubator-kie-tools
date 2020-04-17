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
	"os"

	"github.com/kiegroup/kogito-cloud-operator/test/config"
)

// CheckCliBinaryExist checks if the CLI binary does exist
func CheckCliBinaryExist() (bool, error) {
	path, err := config.GetOperatorCliPath()
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
	path, err := config.GetOperatorCliPath()
	if err != nil {
		return "", err
	}

	return CreateCommand(path, args...).WithLoggerContext(namespace).Execute()
}

// ExecuteCliCommandInNamespace executes a kogito cli command in a specific namespace
func ExecuteCliCommandInNamespace(namespace string, args ...string) (string, error) {
	args = append(args, "-p", namespace)
	return ExecuteCliCommand(namespace, args...)
}
