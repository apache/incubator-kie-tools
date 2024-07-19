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

package quarkus

import (
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"path/filepath"
)

type DeployCmdConfig struct {
	Path      string // service name
	Namespace string
}

func NewDeployCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "deploy",
		Short: "Deploy a Quarkus SonataFlow project",
		Long: `
	Deploys a Quarkus SonataFlow project in the current directory. 
	By default, this command uses the ./target/kubernetes folder to find
	the deployment files generated in the build process. The build step
	is required before using the deploy command.

	Before you use the deploy command, ensure that your cluster have 
	access to the build output image.
		`,
		Example: `
	# Deploy the workflow from the current directory's project. 
	# Deploy as Knative service.
	{{.Name}} deploy

	# You can provide target namespace or use default
	{{.Name}} deploy --namespace <your_namespace>
	
	# Specify the path of the directory containing the "knative.yml" 
	{{.Name}} deploy --path ./kubernetes
		`,
		SuggestFor: []string{"delpoy", "deplyo"},
		PreRunE:    common.BindEnv("namespace", "path"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDeploy(cmd, args)
	}

	cmd.Flags().StringP("path", "p", "./target/kubernetes", fmt.Sprintf("%s path to knative deployment files", cmd.Name()))
	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your deployment.")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

var crds = map[string][]string{
	"SonataFlow Operator":                  metadata.SonataflowCRDs,
	"Knative Serving and Knative Eventing": metadata.KnativeCoreServingCRDs,
}

func runDeploy(cmd *cobra.Command, args []string) error {
	fmt.Println("🛠️  Deploying your Quarkus SonataFlow project...")

	cfg, err := runDeployCmdConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing deploy config: %w", err)
	}

	// check necessary CRDs are installed
	for name, crds := range crds {
		if err := command.CheckCRDs(crds, name); err != nil {
			return err
		}
	}

	if _, err = deployKnativeServiceAndEventingBindings(cfg); err != nil {
		return err
	}

	fmt.Println("🎉 Quarkus SonataFlow project successfully deployed")

	return nil
}

func deployKnativeServiceAndEventingBindings(cfg DeployCmdConfig) (bool, error) {
	isKnativeEventingBindingsCreated := false

	err := common.ExecuteApply(filepath.Join(cfg.Path, "knative.yml"), cfg.Namespace)
	if err != nil {
		return isKnativeEventingBindingsCreated, err
	}
	fmt.Println("🎉 Knative service successfully created")

	if exists, err := checkIfKogitoFileExists(cfg); exists && err == nil {
		if err := common.ExecuteApply(filepath.Join(cfg.Path, "kogito.yml"), cfg.Namespace); err != nil {
			return isKnativeEventingBindingsCreated, err
		}
		isKnativeEventingBindingsCreated = true
		fmt.Println("🎉 Knative Eventing bindings successfully created")
	}
	return isKnativeEventingBindingsCreated, nil
}

func runDeployCmdConfig(cmd *cobra.Command) (cfg DeployCmdConfig, err error) {
	cfg = DeployCmdConfig{
		Path:      viper.GetString("path"),
		Namespace: viper.GetString("namespace"),
	}
	return
}

func checkIfKogitoFileExists(cfg DeployCmdConfig) (bool, error) {
	if _, err := common.FS.Stat(filepath.Join(cfg.Path, "kogito.yml")); err == nil {
		return true, nil
	} else {
		return false, err
	}
}
