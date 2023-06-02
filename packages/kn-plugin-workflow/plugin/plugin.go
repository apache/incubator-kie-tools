/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package plugin

import (
	"os"
	"runtime/debug"
	"strings"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/root"
	knplugin "knative.dev/client/pkg/kn/plugin"
)

type workflowPlugin struct{}

var quarkusPlatformGroupId, quarkusVersion, pluginVersion string

func init() {
	knplugin.InternalPlugins = append(knplugin.InternalPlugins, &workflowPlugin{})
}

// Name is a plugin's name
func (w *workflowPlugin) Name() string {
	return "kn-workflow"
}

// Execute represents the plugin's entrypoint when called through kn
func (w *workflowPlugin) Execute(args []string) error {
	var version string
	info, _ := debug.ReadBuildInfo()
	for _, dep := range info.Deps {
		if strings.Contains(dep.Path, "github.com/kiegroup/kie-tools/packages/kn-plugin-workflow") {
			version = dep.Version
		}
	}

	cmd := root.NewRootCommand(root.RootCmdConfig{Name: "kn workflow", Version: version})
	oldArgs := os.Args
	defer (func() {
		os.Args = oldArgs
	})()
	os.Args = append([]string{"kn-workflow"}, args...)
	return cmd.Execute()
}

// Description is displayed in kn's plugin section
func (w *workflowPlugin) Description() (string, error) {
	return "Manage Workflow projects", nil
}

// CommandParts defines for plugin is executed from kn
func (w *workflowPlugin) CommandParts() []string {
	return []string{"workflow"}
}

// Path is empty because its an internal plugins
func (w *workflowPlugin) Path() string {
	return ""
}
