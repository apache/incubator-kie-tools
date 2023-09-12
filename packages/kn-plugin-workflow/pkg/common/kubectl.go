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

package common

import (
	"fmt"
	"os/exec"
	"strings"
)

func GetKubectlNamespace() (string, error) {
	fmt.Println("🔎 Checking current namespace in kubectl...")
	cmd := ExecCommand("kubectl", "config", "view", "--minify", "--output", "jsonpath={..namespace}")
	output, err := cmd.Output()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: Failed to get current kubectl namespace: %w", err)
	}
	namespace := strings.TrimSpace(string(output))
	if namespace == "" {
		return "", fmt.Errorf("❌ ERROR: No current kubectl namespace found")
	}
	fmt.Printf(" - ✅  kubectl current namespace: %s\n", namespace)
	return namespace, nil
}

func CheckKubectlContext() (string, error) {
	fmt.Println("🔎 Checking if kubectl has a context configured...")
	cmd := ExecCommand("kubectl", "config", "current-context")
	output, err := cmd.Output()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: No current kubectl context found %w", err)
	}
	context := strings.TrimSpace(string(output))
	if context == "" {
		return "", fmt.Errorf("❌ ERROR: No current kubectl context found")
	}
	fmt.Printf(" - ✅ kubectl current context: %s \n", context)
	return context, nil
}

func CheckKubectl() error {
	fmt.Println("🔎 Checking if kubectl is available...")
	_, kubectlCheck := exec.LookPath("kubectl")
	if err := kubectlCheck; err != nil {
		fmt.Println("ERROR: kubectl not found")
		fmt.Println("kubectl is required for deploy")
		fmt.Println("Download it from https://kubectl.docs.kubernetes.io/installation/kubectl/")
		return fmt.Errorf("❌ ERROR: kubectl not found %w", err)
	}

	fmt.Println(" - ✅ kubectl is available")
	return nil
}
