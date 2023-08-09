/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quarkus

import (
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"os"
	"testing"
)

func TestManipulatePom(t *testing.T) {

	//setup
	metadata.KogitoVersion = "1.42.0.Final"

	inputPath := "testdata/pom1-input.xml"
	expectedPath := "testdata/pom1-expected.xml"

	tempFile := "testdata/temp.xml"
	err := copyFile(inputPath, tempFile)
	if err != nil {
		t.Fatalf("Error copying test XML: %v", err)
	}
	defer os.Remove(tempFile)

	err = manipulatePomToKogito(tempFile)
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
