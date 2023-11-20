/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package workflowproj

import (
	"fmt"
	"os"
	"path/filepath"
	"reflect"
	"strings"

	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/yaml"
)

const (
	yamlFileExt = ".yaml"
)

func ensurePath(path string) error {
	err := os.MkdirAll(path, os.ModePerm)
	if err != nil {
		return err
	}
	return nil
}

func saveAsKubernetesManifest(object client.Object, savePath, prefix string) error {
	if reflect.ValueOf(object).IsNil() {
		return nil
	}
	filename := strings.ToLower(fmt.Sprintf("%s%s_%s%s",
		prefix,
		object.GetObjectKind().GroupVersionKind().Kind,
		object.GetName(),
		yamlFileExt))
	finalPath := filepath.Join(savePath, filename)
	contents, err := yaml.Marshal(object)
	if err != nil {
		return err
	}
	err = os.WriteFile(finalPath, contents, os.ModePerm)
	if err != nil {
		return err
	}
	return nil
}
