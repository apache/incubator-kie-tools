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
	"strconv"
	"strings"
)

const javaVersion int64 = 11
const mavenMajorVersion int64 = 3
const mavenMinorVersion int64 = 8

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
	javaCheck := exec.Command("java", "-version")
	version, err := javaCheck.CombinedOutput()
	if err != nil {
		return fmt.Errorf("Java not installed, %w", err)
	}
	userJavaVersion, err := parseJavaVersion(string(version))
	if err != nil {
		return fmt.Errorf("error while parsing Java version: %w", err)
	}

	if userJavaVersion < javaVersion {
		fmt.Printf("ERROR: Please make sure you are using Java version %.2d or later", javaVersion)
		fmt.Println("Installation stopped. Please upgrade Java and run again")
		os.Exit(1)
	} else {
		fmt.Println(" - Java version check.")
	}
	return nil
}

func checkMaven() error {
	mavenCheck := exec.Command("mvn", "--version")
	version, err := mavenCheck.CombinedOutput()
	if err != nil {
		return fmt.Errorf("Maven not installed, %w", err)
	}
	major, minor, err := parseMavenVersion(string(version))
	if err != nil {
		return fmt.Errorf("error while parsing Maven version: %w", err)
	}

	if major < mavenMajorVersion && minor < mavenMinorVersion {
		fmt.Printf("ERROR: Please make sure you are using Maven version %d.%d.1 or later", major, minor)
		fmt.Println("Installation stopped. Please upgrade Maven and run again")
		os.Exit(1)
	} else {
		fmt.Println(" - Maven version check.")
	}

	return nil
}

func CheckContainerRuntime() error {
	fmt.Println("✅ Checking if Docker is available...")
	dockerCheck := exec.Command("docker", "stats", "--no-stream")
	if err := dockerCheck.Run(); err != nil {
		fmt.Println("ERROR: Docker not found.")
		fmt.Println("Download from https://docs.docker.com/get-docker/")
		fmt.Println("Or if it's already installed, check if it's running")
		return fmt.Errorf("%w", err)
	}

	fmt.Println(" - Docker is running")
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
	_, err := os.Stat(fmt.Sprintf("./%s", dirName))
	if err == nil {
		return true, nil
	}
	if os.IsNotExist(err) {
		return false, nil
	}
	return false, err
}
