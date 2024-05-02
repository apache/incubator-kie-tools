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

package framework

import (
	"os/exec"
	"sync"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/logger"
)

var (
	syncMutexMap sync.Map
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
	// WithRetry method defines retry options to be applied to the command.
	WithRetry(opts ...RetryOption) Command
	// Sync method allows to execute only one command at a time based on the syncID.
	Sync(syncID string) Command
	// Execute command and returns the outputs.
	Execute() (string, error)
}

// struct that represents a command.
type commandStruct struct {
	name          string
	args          []string
	directory     string
	loggerContext string
	retries       int
	retryDelay    time.Duration
	syncID        string
}

func (cmd *commandStruct) WithLoggerContext(loggerContext string) Command {
	cmd.loggerContext = loggerContext
	return cmd
}

func (cmd *commandStruct) Sync(syncID string) Command {
	cmd.syncID = syncID
	return cmd
}

func (cmd *commandStruct) InDirectory(directory string) Command {
	cmd.directory = directory
	return cmd
}

func (cmd *commandStruct) WithRetry(opts ...RetryOption) Command {
	for _, opt := range opts {
		opt(cmd)
	}
	return cmd
}

func (cmd *commandStruct) Execute() (string, error) {
	if len(cmd.syncID) > 0 {
		mutex := getMutexOrCreate(cmd.syncID)
		mutex.Lock()
		defer mutex.Unlock()
	}
	return cmd.executeCommand()
}

func (cmd *commandStruct) executeCommand() (string, error) {
	var logger = cmd.getLogger()

	if len(cmd.directory) == 0 {
		logger.Info("Execute command", "command", cmd.name, "args", cmd.args)
	} else {
		logger.Info("Execute command ", "command", cmd.name, "args", cmd.args, "directory", cmd.directory)
	}

	var out []byte
	var err error

	// If retries are set then repeat until command succeed
	for i := 0; i <= cmd.retries; i++ {
		command := exec.Command(cmd.name, cmd.args...)
		command.Dir = cmd.directory
		out, err = command.Output()

		if err == nil {
			break
		}

		time.Sleep(cmd.retryDelay)
	}

	if err != nil {
		logger.Error(err, "output command", "output", string(out[:]))
		if ee, ok := err.(*exec.ExitError); ok {
			logger.Error(err, "error output command", "output", string(ee.Stderr))
		}
	} else {
		logger.Debug("output command", "output", string(out[:]))
	}

	return string(out[:]), err
}
func (cmd *commandStruct) getLogger() logger.Logger {
	var logger logger.Logger
	if len(cmd.loggerContext) > 0 {
		logger = GetLogger(cmd.loggerContext)
	} else {
		logger = GetMainLogger()
	}

	return logger
}

// Retry misc functions

// RetryOption declares funtion to be applied on Retry
type RetryOption func(*commandStruct)

// RetryDelay declares funtion setting delay between retries
func RetryDelay(delay time.Duration) RetryOption {
	return func(cmd *commandStruct) {
		cmd.retryDelay = delay
	}
}

// NumberOfRetries declares funtion setting number of retries
func NumberOfRetries(retries int) RetryOption {
	return func(cmd *commandStruct) {
		cmd.retries = retries
	}
}

func getMutexOrCreate(syncID string) *sync.Mutex {
	mutex, exists := syncMutexMap.Load(syncID)
	if !exists {
		mutex = &sync.Mutex{}
		syncMutexMap.Store(syncID, mutex)
	}
	return mutex.(*sync.Mutex)
}
