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

package create

import (
	"fmt"

	"github.com/spf13/cobra"
)

var createName string
var createExtensions string

func NewCreateCommand() *cobra.Command {
	var createCmd = &cobra.Command{
		Use:   "create",
		Short: "Create a Quarkus serverless workflow project",
		RunE: func(cmd *cobra.Command, args []string) error {
			fmt.Println("Create")
			return Create(createName, createExtensions)
		},
	}

	createNameOption(createCmd, "quickstart")
	createExtensionsOption(createCmd)

	return createCmd
}

func createNameOption(createCmd *cobra.Command, flagDefault string) {
	createCmd.Flags().StringVarP(
		&createName,
		"name",
		"n",
		flagDefault,
		fmt.Sprintf("%s project name to be used", createCmd.Name()),
	)
}

func createExtensionsOption(createCmd *cobra.Command) {
	createCmd.Flags().StringVarP(
		&createExtensions,
		"extension",
		"e",
		"",
		fmt.Sprintf("%s project extensions, can add multiples, separate then with a comma", createCmd.Name()),
	)
}
