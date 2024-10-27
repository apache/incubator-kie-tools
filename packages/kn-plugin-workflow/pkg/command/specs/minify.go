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

func minifyCommand() *cobra.Command {
	// add command minify here

	var cmd = &cobra.Command{
		Use:   "minify",
		Short: "Minification of OpenAPI specs",
		Long: `
	Minification of OpenAPI specs:
	Minification allows us to reduce the size of an OpenAPI spec file, which is essential given the maximum YAML
	size supported by Kubernetes is limited to 3,145,728 bytes.`,
		Example: `
	#Minify the workflow project's OpenAPI spec file located in the current project.
	{{.Name}} specs minify openapi
		`,
	}

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)
	cmd.AddCommand(minifyOpenApi())

	return cmd
}
