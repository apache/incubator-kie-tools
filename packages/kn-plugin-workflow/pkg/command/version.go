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

package command

import (
	"fmt"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/spf13/cobra"
)

func NewVersionCommand(version string) *cobra.Command {
	cmd := &cobra.Command{
		Use:   "version",
		Short: "Show the version",
		Long: `
	 Shows the plugin version.
		 `,
		Example: `
	 # Shows the plugin version
	 {{.Name}} version
		 `,
		SuggestFor: []string{"vers", "verison"}, //nolint:misspell
	}

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	cmd.Run = func(cmd *cobra.Command, args []string) {
		runVersion(version)
	}

	return cmd
}

func runVersion(version string) {
	fmt.Printf("Version: %s\n\n", version)

	fmt.Printf("Default Quarkus version:           %s\n", metadata.QuarkusVersion)
	fmt.Printf("Default Quarkus platform group Id: %s\n", metadata.QuarkusPlatformGroupId)
}
