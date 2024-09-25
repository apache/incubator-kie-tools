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
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"gopkg.in/yaml.v2"
	"io"
	"os"
	"path/filepath"
	"strings"
)

type Document struct {
	Kind     string `yaml:"kind"`
	Metadata struct {
		Name string `yaml:"name"`
	} `yaml:"metadata"`
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
