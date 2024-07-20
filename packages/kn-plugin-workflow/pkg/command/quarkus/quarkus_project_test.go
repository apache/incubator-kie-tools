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
	"os"
	"testing"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
)

func TestManipulatePom(t *testing.T) {

	//setup
	metadata.KogitoVersion = "1.0.0.Final"
	metadata.PluginVersion = "0.0.0"
	metadata.KogitoBomDependency.Version = "0.0.0"

	inputPath := "testdata/pom1-input.xml_no_auto_formatting"
	expectedPath := "testdata/pom1-expected.xml_no_auto_formatting"

	var deps = metadata.DependenciesVersion{
		QuarkusPlatformGroupId: "org.quarkus.fake",
		QuarkusVersion:         "0.0.1",
	}

	var cfg = CreateQuarkusProjectConfig{
		DependenciesVersion: deps,
	}

	tempFile := "testdata/temp.xml"
	err := copyFile(inputPath, tempFile)
	if err != nil {
		t.Fatalf("Error copying test XML: %v", err)
	}
	defer os.Remove(tempFile)

	err = manipulatePomToKogito(tempFile, cfg)
	if err != nil {
		t.Fatalf("Error manipulating XML: %v", err)
	}

	modifiedData, err := os.ReadFile(tempFile)
	if err != nil {
		t.Fatalf("Error reading modified XML: %v", err)
	}

	expectedData, err := os.ReadFile(expectedPath)

	if err != nil {
		t.Fatalf("Error reading expected XML: %v", err)
	}
	if string(modifiedData) != string(expectedData) {
		t.Errorf("Manipulated XML does not match expected XML")
	}
}
