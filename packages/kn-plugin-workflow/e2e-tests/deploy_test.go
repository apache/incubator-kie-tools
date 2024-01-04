//go:build e2e_tests

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

package e2e_tests

import (
	"testing"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command"
)

type cfgTestInputDeploy struct {
	input command.DeployUndeployCmdConfig
}

var cfgTestInputDeploy_Success = []cfgTestInputDeploy{
	{input: command.DeployUndeployCmdConfig{}},
}

func transformDeployCmdCfgToArgs(cfg command.DeployUndeployCmdConfig) []string {
	args := []string{"deploy"}
	return args
}

func TestDeployProjectSuccess(t *testing.T) {
	////TODO: implement deploy test
	// for testIndex, test := range cfgTestInputDeploy_Success {
	// 	t.Run(fmt.Sprintf("Test deploy project success index: %d", testIndex), func(t *testing.T) {
	// 		// Run `deploy` command
	// 		out, err := ExecuteKnWorkflow(transformDeployCmdCfgToArgs(test.input)...)
	// 		require.NoErrorf(t, err, "Expected nil error, got: %v", err)
	// 		fmt.Println(out)
	// 		require.Equal(t, command.DeployCommandOutput, out)
	// 	})
	// }
}
