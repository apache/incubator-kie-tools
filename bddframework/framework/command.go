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

import "os/exec"

// ExecuteCommand Execute command in current directory
func ExecuteCommand(commandName string, args ...string) (string, error) {
	return ExecuteCommandInDirectory("", commandName, args...)
}

// ExecuteCommandInDirectory Execute command in defined directory
func ExecuteCommandInDirectory(directory, commandName string, args ...string) (string, error) {
	if len(directory) == 0 {
		GetMainLogger().Infof("Execute command %s %v", commandName, args)
	} else {
		GetMainLogger().Infof("Execute command %s %v in directory %s", commandName, args, directory)
	}

	command := exec.Command(commandName, args...)
	command.Dir = directory
	out, err := command.Output()

	if err != nil {
		GetMainLogger().Errorf("output command: %s", string(out[:]))
		if ee, ok := err.(*exec.ExitError); ok {
			GetMainLogger().Errorf("error output command: %s", string(ee.Stderr))
		}
	} else {
		GetMainLogger().Debugf("output command: %s", string(out[:]))
	}

	return string(out[:]), err
}
