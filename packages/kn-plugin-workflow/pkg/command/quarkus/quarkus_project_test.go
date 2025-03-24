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
	"bufio"
	"os"
	"path"
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

func TestManipulateDockerFiles(t *testing.T) {
	text := []string{"COPY --chown=185 target/classes/*.sw.json /deployments/app/", "COPY --chown=185 target/classes/*.sw.yaml /deployments/app/" }
	tempDir, err := os.MkdirTemp("", "project")
	if err != nil {
		t.Fatalf("❌ ERROR: failed to create temporary directory: %v", err)
	}
	defer os.RemoveAll(tempDir)

	dockerDir := path.Join(tempDir, "/src/main/docker")
	err = os.MkdirAll(dockerDir, 0755)
	if err != nil {
		t.Fatalf("Error creating docker directory: %v", err)
	}
	err = copyDir("testdata/docker", dockerDir)
	if err != nil {
		t.Fatalf("Error copying Dockerfiles: %v", err)
	}

	extensions := []string{"jvm", "legacy-jar", "native", "native-micro"}

	for _, extension := range extensions {
		dockerFilePath := path.Join(dockerDir, "Dockerfile."+extension)
		_, err := os.Stat(dockerFilePath)
		if err != nil {
			t.Fatalf("Error reading Dockerfile: %v", err)
		}

		if err := manipulateDockerfile(dockerFilePath); err != nil {
			t.Fatalf("Error manipulating Dockerfile: %v", err)
		}

		for _, fragment := range text {
			contains, err := checkFileContainsText(dockerFilePath, fragment)
			if err != nil {
				t.Fatalf("Failed to stat Dockerfile for extension %s: %v", extension, err)
			}
			if !contains {
				t.Errorf("Dockerfile does not contain expected text")
			}
		}
	}
}
func TestManipulateDockerIgnoreFile(t *testing.T) {
	text := []string{"!target/classes/*.sw.json", "!target/classes/*.sw.yaml"}
	tempDir, err := os.MkdirTemp("", "project")
	if err != nil {
		t.Fatalf("❌ ERROR: failed to create temporary directory: %v", err)
	}
	defer os.RemoveAll(tempDir)

	dockerIgnorePath := path.Join(tempDir, ".dockerignore")
	err = copyFile("testdata/dockerignore", dockerIgnorePath)
	if err != nil {
		t.Fatalf("Error copying .dockerignore: %v", err)
	}
	if err := manipulateDockerIgnore(dockerIgnorePath); err != nil {
		t.Fatalf("Error manipulating .dockerignore: %v", err)
	}
	for _, fragment := range text {
		contains, err := checkFileContainsText(dockerIgnorePath, fragment)
		if err != nil {
			t.Fatalf("Error reading .dockerignore: %v", err)
		}
		if !contains {
			t.Errorf(".dockerignore does not contain expected text")
		}
	}

}

func checkFileContainsText(filePath, text string) (bool, error) {
	file, err := os.Open(filePath)
	if err != nil {
		return false, err
	}
	defer file.Close()

	var contains = false
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()
		if line == text {
			contains = true
			break
		}
	}
	return contains, nil
}
