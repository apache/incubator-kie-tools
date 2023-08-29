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
	"path/filepath"
	"testing"
)

func DeleteFolderStructure(t *testing.T, path string) {
	err := FS.RemoveAll(path)
	if err != nil {
		t.Error("Unable to delete folder structure" + path)
	}
}

func CreateFolderStructure(t *testing.T, path string) {
	err := FS.MkdirAll(path, 0750)
	if err != nil {
		t.Error("Unable to create folder structure" + path)
	}
}

func CreateFileInFolderStructure(t *testing.T, path string, fileName string) {
	_, err := FS.Create(filepath.Join(path, fileName))
	//defer file.Close()
	if err != nil {
		t.Error("Unable to create" + fileName + "file in" + path)
	}
}
