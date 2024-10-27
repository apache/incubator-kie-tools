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
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"os"
)

func minifyOpenApi() *cobra.Command {

	var cmd = &cobra.Command{
		Use:     "openapi",
		Short:   "Minify the openAPI spec files to trim operations only used by the workflows",
		Long:    "Minify the openAPI spec files to trim operations only used by the workflows",
		PreRunE: common.BindEnv("specs-dir", "subflows-dir"),
	}

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	cmd.Flags().StringP("specs-dir", "p", "", "Specify a custom specs files directory")
	cmd.Flags().StringP("subflows-dir", "s", "", "Specify a custom subflows files directory")

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runMinifyOpenApi()
	}

	return cmd

}

func runMinifyOpenApi() error {

	var cfg = &common.OpenApiMinifierParams{
		SpecsDir:    viper.GetString("specs-dir"),
		SubflowsDir: viper.GetString("subflows-dir"),
	}

	if len(cfg.SubflowsDir) == 0 {
		dir, err := os.Getwd()
		cfg.SubflowsDir = dir + "/subflows"
		if err != nil {
			return fmt.Errorf("❌ ERROR: failed to get default subflows workflow files folder: %w", err)
		}
	}

	if len(cfg.SpecsDir) == 0 {
		dir, err := os.Getwd()
		cfg.SpecsDir = dir + "/specs"
		if err != nil {
			return fmt.Errorf("❌ ERROR: failed to get default specs files folder: %w", err)
		}
	}

	minifier := common.NewMinifier(cfg)
	_, err := minifier.Minify()
	return err
}
