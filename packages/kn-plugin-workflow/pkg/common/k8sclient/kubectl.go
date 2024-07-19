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

package k8sclient

import (
	"bufio"
	"bytes"
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"os"
	"os/exec"
	"strings"
)

type KubeCtl struct {}

func (m KubeCtl) GetKubectlNamespace() (string, error) {
	if err := CheckKubectl(); err != nil {
		return "", err
	}

	fmt.Println("🔎 Checking current namespace in kubectl...")
	cmd := exec.Command("kubectl", "config", "view", "--minify", "--output", "jsonpath={..namespace}")
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

func (m KubeCtl) CheckKubectlContext() (string, error) {
	if err := CheckKubectl(); err != nil {
		return "", err
	}
	fmt.Println("🔎 Checking if kubectl has a context configured...")
	cmd := exec.Command("kubectl", "config", "current-context")
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

func (m KubeCtl) ExecuteKubectlApply(crd, namespace string) error {
	if err := CheckKubectl(); err != nil {
		return err
	}
	cmd := exec.Command("kubectl",
		"apply",
		"-f", crd,
		"-n", namespace,
		"--validate=false")

	var stderror bytes.Buffer

	cmd.Stdout = os.Stdout
	cmd.Stderr = &stderror //os.Stderr

	err := cmd.Run()
	scanner := bufio.NewScanner(&stderror)
	for scanner.Scan() {
		line := scanner.Text()
		//Temporarily removing the following warning:
		//In this context, using apply or create are interchangeable, but generates a warning.
		//Warning: resource configmaps/service-props is missing the kubectl.kubernetes.io/last-applied-configuration annotation which is required by kubectl apply. kubectl apply should only be used on resources created declaratively by either kubectl create --save-config or kubectl apply. The missing annotation will be patched automatically.
		//This is tracked here: https://issues.redhat.com/browse/KOGITO-9391 and it will be fixed by
		//https://issues.redhat.com/browse/KOGITO-9381
		if !strings.Contains(line, "kubectl.kubernetes.io/last-applied-configuration") {
			fmt.Fprintln(os.Stderr, line)
		}
	}
	if err != nil {
		fmt.Printf("has a error")
		return fmt.Errorf("❌ ERROR: failed to execute kubectl apply command for %s: %s", crd, err)
	}

	return nil
}

func (m KubeCtl)  ExecuteKubectlDelete(crd, namespace string) error {
	if err := CheckKubectl(); err != nil {
		return err
	}
	cmd := exec.Command("kubectl",
		"delete",
		"-f", crd,
		"-n", namespace)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr

	err := cmd.Run()
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to execute kubectl delete command for %s: %s", crd, err)
	}

	return nil
}

func CheckOperatorInstalled() error {
	if err := CheckKubectl(); err != nil {
		return err
	}
	cmd := exec.Command("kubectl", "get", "pods", "-n", metadata.OperatorName)

	output, err := cmd.Output()
	if err != nil {
		return fmt.Errorf("❌ ERROR: SonataFlow Operator not found %w", err)
	}

	// Check if the pod is running
	operatorRunning := checkOperatorRunning(string(output))
	if !operatorRunning {
		return fmt.Errorf("❌ ERROR: SonataFlow Operator not found")
	}

	fmt.Println(" - ✅ SonataFlow Operator is available")
	return nil
}

func checkOperatorRunning(getPodsOutPut string) bool {
	pods := strings.Split(getPodsOutPut, "\n")
	for _, pod := range pods {
		// Split each line into fields (NAME, READY, STATUS, RESTARTS, AGE)
		fields := strings.Fields(pod)

		// Check if this line contains information about the desired operator manager pod
		if len(fields) > 2 && strings.HasPrefix(fields[0], metadata.OperatorManagerPod) && fields[2] == "Running" {
			return true
		}
	}
	return false
}
