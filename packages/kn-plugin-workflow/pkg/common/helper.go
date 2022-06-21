/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common

import (
	"fmt"
	"os"
	"os/exec"
)

func GetEnv(key, fallback string) string {
	if value, ok := os.LookupEnv(key); ok {
		return value
	}
	return fallback
}

func RunCommand(command *exec.Cmd, verbose bool, errorMessage string) error {
	stdout, _ := command.StdoutPipe()
	stderr, _ := command.StderrPipe()

	if err := command.Start(); err != nil {
		return fmt.Errorf("%s: %w", errorMessage, err)
	}

	if verbose {
		VerboseLog(stdout, stderr)
	}

	if err := command.Wait(); err != nil {
		return fmt.Errorf("%s: %w", errorMessage, err)
	}
	return nil
}
