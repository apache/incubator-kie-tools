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

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/filters"
	"github.com/docker/docker/client"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type PruneCmdConfig struct {
	DevContainers bool
	DevImages     bool
	Dev           bool
	BaseImages    bool
	TempFiles     bool
	All           bool
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
		PreRunE:    common.BindEnv("dev-containers", "dev-images", "runner-images", "temp-files", "all"),
	}

	cmd.Flags().BoolP("dev-containers", "", false, "Stop and delete all development containers.")
	cmd.Flags().BoolP("dev-images", "", false, "Delete all development images.")
	cmd.Flags().BoolP("dev", "", false, "Delete all development container and images.")
	cmd.Flags().BoolP("base-images", "", false, "Delete all base images.")
	cmd.Flags().BoolP("temp-files", "", false, "Delete all temporary files.")
	cmd.Flags().BoolP("all", "", false, "Delete all")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	cmd.Run = func(cmd *cobra.Command, args []string) {
		err := runPrune(cmd)
		if err != nil {
			fmt.Println(err)
		}
	}

	return cmd
}

func runPruneConfig(cmd *cobra.Command) (cfg PruneCmdConfig, err error) {
	cfg = PruneCmdConfig{
		DevContainers: viper.GetBool("dev-containers"),
		DevImages:     viper.GetBool("dev-images"),
		Dev:           viper.GetBool("dev"),
		BaseImages:    viper.GetBool("base-images"),
		TempFiles:     viper.GetBool("temp-files"),
		All:           viper.GetBool("all"),
	}
	return
}

func runPrune(cmd *cobra.Command) (err error) {
	start := time.Now()
	cfg, err := runPruneConfig(cmd)
	if err != nil {
		fmt.Println("ERROR: parsing flags")
		return
	}

	if cfg.All || cfg.TempFiles {
		confirmation := confirm("All files located in the temporary folder will be deleted. Are you sure?")
		if !confirmation {
			return
		}
		fmt.Println("- Pruning temporary files")
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

	if cfg.All || cfg.Dev || cfg.DevContainers || cfg.DevImages || cfg.BaseImages {
		ctx := cmd.Context()
		dockerCli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
		if err != nil {
			return err
		}

		if cfg.All || cfg.Dev || cfg.DevContainers {
			fmt.Println("- Stopping development containers")
			filter := filters.NewArgs()
			filter.Add("name", "kn-workflow-dev")
			containers, err := dockerCli.ContainerList(ctx, types.ContainerListOptions{Filters: filter, All: true})
			if err != nil {
				return err
			}

			if len(containers) > 0 {
				fmt.Println("All the following containers are going to be removed:")
				for _, container := range containers {
					for _, name := range container.Names {
						fmt.Println(name)
					}
				}
				confirmation := confirm("Are you sure?")
				if !confirmation {
					return err
				}

				for _, container := range containers {
					fmt.Printf("- Stopping and Removing %s\n", container.ID)
					if err := dockerCli.ContainerStop(ctx, container.ID, nil); err != nil {
						return err
					}
					if err := dockerCli.ContainerRemove(ctx, container.ID, types.ContainerRemoveOptions{}); err != nil {
						return err
					}
				}
			}
		}

		if cfg.All || cfg.Dev || cfg.DevImages {
			fmt.Println("- Removing development images")
			filter := filters.NewArgs()
			filter.Add("reference", "dev.local/kn-workflow-development")
			images, err := dockerCli.ImageList(ctx, types.ImageListOptions{Filters: filter})
			if err != nil {
				return err
			}

			if len(images) > 0 {
				fmt.Println("All the following images are going to be removed:")
				for _, image := range images {
					for _, repoTag := range image.RepoTags {
						fmt.Println(repoTag)
					}
				}
				confirmation := confirm("Are you sure?")
				if !confirmation {
					return err
				}

				for _, image := range images {
					fmt.Printf("- Removing: %s", image.ID)
					_, err := dockerCli.ImageRemove(ctx, image.ID, types.ImageRemoveOptions{})
					if err != nil {
						fmt.Println(`ERROR: check if you still have containers using the image.
You can try to use the --dev-containers flag to remove all dev containers`)
						return err
					}
				}
			}
		}
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
