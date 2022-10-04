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

package single

import (
	"fmt"
	"time"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type DevCmdConfig struct {
	Build               bool
	Run                 bool
	DependenciesVersion metadata.DependenciesVersion
}

func NewDevCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "dev",
		Short: "Run a workflow project in development mode",
		Long:  ``,
		Example: `

If you wish, you can run the development container using Docker directly:
		docker container run -it \
		--mount type=bind,source="$(pwd)",target=/tmp/kn-plugin-workflow/src/main/resources \
		-p 8080:8080 quay.io/lmotta/dev		
`,
		SuggestFor: []string{"dve", "start"},
		PreRunE:    common.BindEnv("build", "run", "quarkus-platform-group-id", "quarkus-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDev(cmd, args)
	}

	quarkusDepedencies := metadata.ResolveQuarkusDependencies()
	cmd.Flags().BoolP("build", "b", true, "Build dev image.")
	cmd.Flags().BoolP("run", "r", true, "Start the development container.")
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDepedencies.QuarkusPlatformGroupId, "Quarkus group id to be set in the project.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDepedencies.QuarkusVersion, "Quarkus version to be set in the project.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runDevCmdConfig(cmd *cobra.Command) (cfg DevCmdConfig, err error) {
	cfg = DevCmdConfig{
		Build: viper.GetBool("build"),
		Run:   viper.GetBool("run"),

		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: viper.GetString("quarkus-platform-group-id"),
			QuarkusVersion:         viper.GetString("quarkus-version"),
		},
	}
	return
}

// should build and run a container using the dockerfile under the /tmp folder.
// if already built, should only run.
// build image
// run container with buildt image
func runDev(cmd *cobra.Command, args []string) (err error) {
	start := time.Now()

	cfg, err := runDevCmdConfig(cmd)
	if err != nil {
		fmt.Println("ERROR: parsing flags")
		return
	}

	if err = common.CheckDocker(); err != nil {
		fmt.Println("ERROR: checking docker")
		return
	}

	if cfg.Build {
		fmt.Println("🔨 Building your development image")
		if err = buildDevImage(cfg, cmd); err != nil {
			fmt.Println("ERROR: building dev image")
			return
		}
	}

	if cfg.Run {
		fmt.Println("🔨 Running your development image")
		if err = runDevContainer(cfg, cmd); err != nil {
			fmt.Println("ERROR: running dev image")
			return
		}
	}

	fmt.Printf("🚀 Development command took: %s \n", time.Since(start))
	return nil
}

/*
— dev
docker build -f Dockerfile.workflow --target=dev \
--build-arg workflow_file=workflow.sw.json \
--build-arg extensions=quarkus-jsonp,quarkus-smallrye-openapi \
--build-arg workflow_name=my-project \
--build-arg container_registry=quay.io \
--build-arg container_group=lmotta \
--build-arg container_name=test \
--build-arg container_tag=0.0.1 \
-t quay.io/lmotta/dev .
*/
func buildDevImage(cfg DevCmdConfig, cmd *cobra.Command) (err error) {
	return
}

/*
docker container run -it \
--mount type=bind,source="$(pwd)",target=/tmp/kn-plugin-workflow/src/main/resources \
-p 8080:8080 quay.io/lmotta/dev
*/
func runDevContainer(cfg DevCmdConfig, cmd *cobra.Command) (err error) {
	return
}
