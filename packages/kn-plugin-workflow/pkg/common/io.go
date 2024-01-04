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
	"io"
	"os"
	"path/filepath"
	"strings"
)

func FindFilesWithExtensions(directoryPath string, extensions []string) ([]string, error) {
	filePaths := []string{}

	_, err := os.Stat(directoryPath)
	if os.IsNotExist(err) {
		return filePaths, nil
	} else if err != nil {
		return nil, fmt.Errorf("❌ ERROR: failed to access directory: %s", err)
	}

	files, err := os.ReadDir(directoryPath)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: failed to read directory: %s", err)
	}

	for _, file := range files {
		if file.IsDir() {
			continue
		}

		filename := file.Name()
		for _, ext := range extensions {
			if strings.HasSuffix(strings.ToLower(filename), strings.ToLower(ext)) {
				filePath := filepath.Join(directoryPath, filename)
				filePaths = append(filePaths, filePath)
				break
			}
		}
	}

	return filePaths, nil
}

func MustGetFile(filepath string) (io.Reader, error) {
	file, err := os.OpenFile(filepath, os.O_RDONLY, os.ModePerm)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: failed to read file: %s", err)
	}
	return file, nil
}
