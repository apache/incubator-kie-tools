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

package openshift

import (
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
)

func CheckOCInstalled() error {
	fmt.Println("✅ Checking if Openshift CLI installed...")
	ocCheck := common.ExecCommand("oc", "version")
	err := ocCheck.Run()
	if err != nil {
		fmt.Println("ERROR: Openshift CLI not found.")
		return fmt.Errorf("rror while checking if Openshift CLI is installed: %w", err)
	}
	return nil
}

func CheckPermissions() error {
	fmt.Println("✅ Checking permissions...")
	namespace := "sonataflow-operator-system"
	checkPermissions := common.ExecCommand("oc", "auth", "can-i", "create", "subscription", "-n", namespace)
	result, err := checkPermissions.CombinedOutput()
	if err != nil {
		return fmt.Errorf("error while checking permissions: %w", err)
	}
	if string(result) != "yes\n" {
		return fmt.Errorf("you don't have the required permissions to create a subscription in the namespace %s", namespace)
	}

	return nil
}