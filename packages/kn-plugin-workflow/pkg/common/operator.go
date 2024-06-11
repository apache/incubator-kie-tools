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
	"bufio"
	"bytes"
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"gopkg.in/yaml.v2"
	"io"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
)

type Document struct {
	Kind string `yaml:"kind"`
}

func ExecuteKubectlApply(crd, namespace string) error {

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

func ExecuteKubectlDelete(crd, namespace string) error {

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

func FindServiceFiles(directory string) ([]string, error) {
	var serviceFiles []string

	err := filepath.Walk(directory, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure accessing a path %q: %v\n", path, err)
		}

		if info.IsDir() || filepath.Ext(path) != ".yaml" {
			return nil
		}

		file, err := os.Open(path)
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure opening file %q: %v\n", path, err)
		}
		defer file.Close()

		byteValue, err := io.ReadAll(file)
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure reading file %q: %v\n", path, err)
		}

		var doc Document
		if err := yaml.Unmarshal(byteValue, &doc); err != nil {
			return fmt.Errorf("❌ ERROR: failure unmarshalling YAML from file %q: %v\n", path, err)
		}

		if doc.Kind == metadata.ManifestServiceFilesKind {
			serviceFiles = append(serviceFiles, path)
		}

		return nil
	})

	if err != nil {
		return nil, err
	}

	return serviceFiles, nil
}
