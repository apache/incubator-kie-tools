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
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/spf13/cobra"
)

func SpecsCommand() *cobra.Command {
	// add command specs here

	var cmd = &cobra.Command{
		Use:   "specs",
		Short: "Set of utilities to work with specs",
		Long:  "Set of utilities to work with specs",
	}

	//add --help flag
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	cmd.AddCommand(minifyCommand())

	return cmd
}
