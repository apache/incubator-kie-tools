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

package cmd

import (
	"fmt"
	"html/template"
	"os"

	"github.com/spf13/cobra"
)

type CmdConfig struct {
	Version string
}

var (
	directory  string
	names      []string
	jsonSchema string
)

func RootCmd(cfg CmdConfig) *cobra.Command {
	var cmd = &cobra.Command{
		Use:     "image-env-to-json",
		Short:   "Image env to JSON",
		Long:    `Image env to JSON`,
		Aliases: []string{"image-env-to-json"},
		Run: func(cmd *cobra.Command, args []string) {
			fmt.Println("Directory is", directory)
		},
	}

	cmd.Version = cfg.Version
	cmd.SetVersionTemplate(`{{printf "%s\n" .Version}}`)

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return nil
	}

	cmd.PersistentFlags().StringVarP(&directory, "directory", "d", "", "directory to create or update an existing env.json file.")
	cmd.PersistentFlags().StringArrayVarP(&names, "names", "n", nil, "environment variable names to look for.")
	cmd.PersistentFlags().StringVar(&jsonSchema, "json-schema", "", "JSON Schema file path to validate.")
	cmd.MarkFlagRequired("directory")

	cmd.SetHelpFunc(func(cmd *cobra.Command, args []string) {
		var (
			body = cmd.Long + "\n\n" + cmd.UsageString()
			t    = template.New("<name>")
			tpl  = template.Must(t.Parse(body))
		)

		var data = struct {
			Name string
		}{
			Name: cmd.Root().Use,
		}

		if err := tpl.Execute(cmd.OutOrStdout(), data); err != nil {
			fmt.Fprintf(cmd.ErrOrStderr(), "unable to display help text: %v", err)
		}
	})

	if err := cmd.Execute(); err != nil {
		if err.Error() != "subcommand is required" {
			fmt.Fprintln(os.Stderr, err)
		}
		os.Exit(1)
	}

	return cmd
}
