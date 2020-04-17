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

import (
	"os/exec"

	"go.uber.org/zap"
)

// CreateCommand methods initializes the basic data to run commands.
func CreateCommand(commandName string, args ...string) Command {
	return &commandStruct{name: commandName, args: args}
}

// Command wraps information about the command to execute.
type Command interface {
	// WithLoggerContext method attaches a logger context to trace all the command logs when executing it.
	WithLoggerContext(loggerContext string) Command
	// InDirectory method sets the directory where the command will be executed.
	InDirectory(directory string) Command
	// Execute command and returns the outputs.
	Execute() (string, error)
}

// struct that represents a command.
type commandStruct struct {
	name          string
	args          []string
	directory     string
	loggerContext string
}

func (cmd *commandStruct) WithLoggerContext(loggerContext string) Command {
	cmd.loggerContext = loggerContext
	return cmd
}

func (cmd *commandStruct) InDirectory(directory string) Command {
	cmd.directory = directory
	return cmd
}

func (cmd *commandStruct) Execute() (string, error) {
	var logger = cmd.getLogger()

	if len(cmd.directory) == 0 {
		logger.Infof("Execute command %s %v", cmd.name, cmd.args)
	} else {
		logger.Infof("Execute command %s %v in directory %s", cmd.name, cmd.args, cmd.directory)
	}

	command := exec.Command(cmd.name, cmd.args...)
	command.Dir = cmd.directory
	out, err := command.Output()

	if err != nil {
		logger.Errorf("output command: %s", string(out[:]))
		if ee, ok := err.(*exec.ExitError); ok {
			logger.Errorf("error output command: %s", string(ee.Stderr))
		}
	} else {
		logger.Debugf("output command: %s", string(out[:]))
	}

	return string(out[:]), err
}

func (cmd *commandStruct) getLogger() *zap.SugaredLogger {
	var logger *zap.SugaredLogger
	if len(cmd.loggerContext) > 0 {
		logger = GetLogger(cmd.loggerContext)
	} else {
		logger = GetMainLogger()
	}

	return logger
}
