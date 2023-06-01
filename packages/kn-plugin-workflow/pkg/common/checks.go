/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package common

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"regexp"
	"strconv"
	"strings"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
)

func CheckJavaDependencies() error {
	fmt.Println("✅ Checking dependencies...")
	if err := checkJava(); err != nil {
		return fmt.Errorf("%w", err)
	}
	if err := checkMaven(); err != nil {
		return fmt.Errorf("%w", err)
	}
	return nil
}

func checkJava() error {
	javaCheck := ExecCommand("java", "-version")
	version, err := javaCheck.CombinedOutput()
	if err != nil {
		fmt.Println("ERROR: Java not found")
		fmt.Printf("At least Java %.2d is required to use this command\n", metadata.JavaVersion)
		return err
	}
	userJavaVersion, err := parseJavaVersion(string(version))
	if err != nil {
		return fmt.Errorf("error while parsing Java version: %w", err)
	}

	if userJavaVersion < metadata.JavaVersion {
		fmt.Printf("ERROR: Please make sure you are using Java version %.2d or later", metadata.JavaVersion)
		fmt.Println("Installation stopped. Please upgrade Java and run again")
		os.Exit(1)
	} else {
		fmt.Println(" - Java version check.")
	}
	return nil
}

func checkMaven() error {
	mavenCheck := ExecCommand("mvn", "--version")
	version, err := mavenCheck.CombinedOutput()
	if err != nil {
		fmt.Println("ERROR: Maven not found")
		fmt.Printf("At least Maven %.2d.%.2d.1 is required to use this command\n", metadata.MavenMajorVersion, metadata.MavenMinorVersion)
		return err
	}
	major, minor, err := parseMavenVersion(string(version))
	if err != nil {
		return fmt.Errorf("error while parsing Maven version: %w", err)
	}

	if major < metadata.MavenMajorVersion && minor < metadata.MavenMinorVersion {
		fmt.Printf("ERROR: Please make sure you are using Maven version %d.%d.1 or later", major, minor)
		fmt.Println("Installation stopped. Please upgrade Maven and run again")
		os.Exit(1)
	} else {
		fmt.Println(" - Maven version check.")
	}

	return nil
}

func CheckDocker() error {
	fmt.Println("✅ Checking if Docker is available...")
	dockerCheck := ExecCommand("docker", "stats", "--no-stream")
	if err := dockerCheck.Run(); err != nil {
		fmt.Println("ERROR: Docker not found.")
		fmt.Println("Download from https://docs.docker.com/get-docker/")
		fmt.Println("If it's already installed, check if the docker daemon is running")
		return err
	}

	fmt.Println(" - Docker is running")
	return nil
}

func CheckPodman() error {
	fmt.Println("✅ Checking if Podman is available...")
	dockerCheck := ExecCommand("podman", "stats", "--no-stream")
	if err := dockerCheck.Run(); err != nil {
		fmt.Println("ERROR: Podman not found.")
		fmt.Println("Download from https://docs.podman.io/en/latest/")
		fmt.Println("If it's already installed, check if the podman daemon is running")
		return err
	}

	fmt.Println(" - Podman is running")
	return nil
}

func CheckKubectl() error {
	fmt.Println("✅ Checking if kubectl is available...")
	_, kubectlCheck := exec.LookPath("kubectl")
	if err := kubectlCheck; err != nil {
		fmt.Println("ERROR: kubectl not found")
		fmt.Println("kubectl is required for deploy")
		fmt.Println("Download from https://kubectl.docs.kubernetes.io/installation/kubectl/")
		os.Exit(1)
		return err
	}

	fmt.Println(" - kubectl is available")
	return nil
}

func parseJavaVersion(version string) (int64, error) {
	dotVersion := strings.Split(strings.Split(version, "\"")[1], ".")
	intVersion, err := strconv.ParseInt(dotVersion[0], 10, 8)
	if err != nil {
		return 0, err
	}
	return intVersion, nil
}

func parseMavenVersion(version string) (int64, int64, error) {
	stringVersion := strings.Split(version, " ")[2]
	dotVersion := strings.Split(stringVersion, ".")
	majorVersion, err := strconv.ParseInt(dotVersion[0], 10, 8)
	if err != nil {
		return 0, 0, err
	}
	minorVersion, err := strconv.ParseInt(dotVersion[1], 10, 8)
	if err != nil {
		return 0, 0, err
	}
	return majorVersion, minorVersion, nil
}

func CheckIfDirExists(dirName string) (bool, error) {
	_, err := FS.Stat(fmt.Sprintf("./%s", dirName))
	if err == nil {
		return true, nil
	}
	if os.IsNotExist(err) {
		return false, nil
	}
	return false, err
}

func IsQuarkusSWFProject() bool {
	if fileExists("pom.xml") {
		return true
	}
	return false
}

func IsSWFProject() bool {
	if anyFileExists("*.sw.*") {
		return true
	}
	return false
}

func fileExists(filename string) bool {
	_, err := os.Stat(filename)
	return !os.IsNotExist(err)
}

func anyFileExists(extension string) bool {
	matches, err := filepath.Glob(extension)
	if err != nil {
		return false
	}

	if len(matches) > 0 {
		return true
	}
	return false
}

func CheckProjectName(name string) (err error) {
	matched, err := regexp.MatchString(`^([_\-\.a-zA-Z0-9]+)$`, name)
	if !matched {
		fmt.Printf("The project name (\"%s\") contains invalid characters. Valid characters are alphanumeric (A-Za-z), underscore, dash and dot.", name)
		err = fmt.Errorf("invalid project name")
	}
	return
}
