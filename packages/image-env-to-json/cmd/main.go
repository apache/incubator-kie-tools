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

package main

import (
	"fmt"
	"html/template"
	"os"

	"github.com/apache/incubator-kie-tools/packages/image-env-to-json/internal"
	"github.com/spf13/cobra"
)

type CmdConfig struct {
	Version string
}

func main() {
	cfg := CmdConfig{Version: internal.Version}

	var cmd = &cobra.Command{
		Use:     "image-env-to-json",
		Short:   "Image env to JSON",
		Long:    `Image env to JSON`,
		Aliases: []string{"image-env-to-json"},
	}

	cmd.Version = cfg.Version
	cmd.SetVersionTemplate(`{{printf "%s\n" .Version}}`)

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return nil
	}

	cmd.Flags().StringP("directory", "d", "", "directory to create or update an existing env.json file.")
	cmd.Flags().StringArrayP("names", "n", nil, "environment variable names to look for.")

	cmd.SetHelpFunc(func(cmd *cobra.Command, args []string) {
		var (
			body = cmd.Long + "\n\n" + cmd.UsageString()
			t    = template.New("aaaa")
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
}
