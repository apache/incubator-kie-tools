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

package main

import (
	"fmt"
	"os"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/root"
)

var quarkusVersion, kogitoVersion, pluginVersion string

func main() {
	cfg := root.RootCmdConfig{
		DependenciesVersion: common.DependenciesVersion{
			QuarkusVersion: quarkusVersion,
			KogitoVersion:  kogitoVersion,
		},
		PluginVersion: pluginVersion,
	}

	if err := root.NewRootCommand(cfg).Execute(); err != nil {
		if err.Error() != "subcommand is required" {
			fmt.Fprintln(os.Stderr, err)
		}
		os.Exit(1)
	}
}
