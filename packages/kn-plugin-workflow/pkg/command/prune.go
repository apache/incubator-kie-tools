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
	"os"
	"path/filepath"
	"time"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/spf13/cobra"
)

func NewPruneCommand() *cobra.Command {
	cmd := &cobra.Command{
		Use:   "prune",
		Short: "Deletes temporary files",
		Long: `
		Deletes files located in the temporary folder.
		  `,
		Example: `
	  # Deletes all files located in the temporary folder
	  {{.Name}} prune
		  `,
		SuggestFor: []string{"prnue", "prneu"},
	}

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	cmd.Run = func(cmd *cobra.Command, args []string) {
		runPrune()
	}

	return cmd
}

func runPrune() (err error) {
	start := time.Now()
	fmt.Println("🔨 Prunning")

	pathToPrune := filepath.Join(os.TempDir(), fmt.Sprintf("%s-%s-*",
		common.KN_WORKFLOW_NAME,
		metadata.PluginVersion,
	))

	paths, err := filepath.Glob(pathToPrune)
	if err != nil {
		fmt.Println("ERROR: creating glob")
		return
	}

	for _, path := range paths {
		fmt.Printf("Removing all files from %s\n", path)
		err = os.RemoveAll(path)
		if err != nil {
			fmt.Printf("ERROR: remove from %s\n", path)
			return
		}
	}
	// add stop all dev containers?

	fmt.Printf("🚀 Prune command took: %s \n", time.Since(start))
	return
}
