//go:build e2e_tests

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

package e2e_tests

import (
	"fmt"
	"os"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command/operator"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
)

var operatorManager = common.NewOperatorManager("")

func InstallOperator() {
	installOperator()
	waitForOperatorReady()
	checkOperatorInstalled()
}

func UninstallOperator() {
	uninstallOperator()
}

func installOperator() {
	var install = operator.NewInstallOperatorCommand()
	err := install.Execute()
	if err != nil {
		fmt.Println("Failed to install operator:", err)
		os.Exit(1)
	}
}

func waitForOperatorReady() {
	deployed := make(chan bool)
	defer close(deployed)
	timeoutCh := time.After(5 * time.Minute)

	go func() {
		for {
			select {
			case <-timeoutCh:
				fmt.Println("Timeout waiting for operator to be ready")
				os.Exit(1)
			default:
				resources, err := operatorManager.ListOperatorResources();
				if err != nil {
					fmt.Println("Failed to list operator resources:", err)
					os.Exit(1)
				}

				if(len(resources) == 0) {
					continue
				}

				var ready = true
				for _, resource := range resources {
					phase, found, err := unstructured.NestedString(resource.Object, "status", "phase")
					if !found {
						ready = false
					}
					if err != nil {
						fmt.Println("Failed to get resource status:", err)
						os.Exit(1)
					}
					if phase != "Succeeded" {
						ready = false
					}
				}

				if ready {
					deployed <- true
					return

				}
				time.Sleep(5 * time.Second)
			}
		}
	}()

	select {
	case <-deployed:
		fmt.Printf(" - ✅ Operator is ready\n")
	}
}

func checkOperatorInstalled() {
	var status = operator.NewStatusOperatorCommand()
	err := status.Execute()
	if err != nil {
		fmt.Println("Failed to check operator status:", err)
		os.Exit(1)
	}
}

func uninstallOperator() {
	var uninstall = operator.NewUnInstallOperatorCommand()
	err := uninstall.Execute()
	if err != nil {
		fmt.Println("Failed to uninstall operator:", err)
		os.Exit(1)
	}
}
