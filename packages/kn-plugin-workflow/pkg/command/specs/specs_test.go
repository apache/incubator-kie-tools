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

package specs

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestSpecsCommand(t *testing.T) {
	cmd := SpecsCommand()

	assert.NotNil(t, cmd)

	assert.Equal(t, "specs", cmd.Use)

	subcommands := cmd.Commands()
	assert.NotEmpty(t, subcommands)
	assert.Equal(t, 1, len(subcommands))

	found := false
	for _, c := range subcommands {
		if c.Name() == "minify" {
			found = true
			break
		}
	}
	assert.True(t, found, "minify subcommand not found")
}
