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
	"bufio"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type PruneCmdConfig struct {
	DevContainer bool
	DevImage     bool
	RunnerImage  bool
	TempFiles    bool
	All          bool
}

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

	cmd.Flags().BoolP("dev-container", "", false, "Stop and delete all development containers.")
	cmd.Flags().BoolP("dev-image", "", false, "Delete all development images.")
	cmd.Flags().BoolP("runner-image", "", false, "Delete all runner images.")
	cmd.Flags().BoolP("temp-files", "", false, "Delete all temporary files.")
	cmd.Flags().BoolP("all", "", false, "Delete all")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	cmd.Run = func(cmd *cobra.Command, args []string) {
		runPrune(cmd)
	}

	return cmd
}

func runPruneConfig(cmd *cobra.Command) (cfg PruneCmdConfig, err error) {
	cfg = PruneCmdConfig{
		DevContainer: viper.GetBool("dev-container"),
		DevImage:     viper.GetBool("dev-image"),
		RunnerImage:  viper.GetBool("runner-image"),
		TempFiles:    viper.GetBool("temp-files"),
		All:          viper.GetBool("all"),
	}
	return
}

func runPrune(cmd *cobra.Command) (err error) {
	confirmation := confirm("Are you sure?")
	if !confirmation {
		return
	}

	start := time.Now()
	cfg, err := runPruneConfig(cmd)
	if err != nil {
		fmt.Println("ERROR: parsing flags")
		return
	}

	if cfg.All || cfg.TempFiles {
		fmt.Println("🗑️ Pruning temporary files")
		pathToPrune := filepath.Join(os.TempDir(), fmt.Sprintf("%s-%s-*",
			common.KN_WORKFLOW_NAME,
			metadata.PluginVersion,
		))

		paths, err := filepath.Glob(pathToPrune)
		if err != nil {
			fmt.Println("ERROR: creating glob")
			return err
		}

		for _, path := range paths {
			fmt.Printf("Removing files from %s\n", path)
			err = os.RemoveAll(path)
			if err != nil {
				fmt.Printf("ERROR: remove from %s\n", path)
				return err
			}
		}
	}

	if cfg.All || cfg.DevContainer {
		fmt.Println("🗑️ Stopping development containers")
	}

	if cfg.All || cfg.DevImage {
		fmt.Println("🗑️ Removing development images")
	}

	if cfg.All || cfg.RunnerImage {
		fmt.Println("🗑️ Removing runner images")
	}

	fmt.Printf("🚀 Prune command took: %s \n", time.Since(start))
	return
}

func confirm(s string) bool {
	r := bufio.NewReader(os.Stdin)
	fmt.Printf("%s [y/n]: ", s)

	res, err := r.ReadString('\n')
	if err != nil {
		log.Fatal(err)
	}

	return strings.ToLower(strings.TrimSpace(res))[0] == 'y'
}
