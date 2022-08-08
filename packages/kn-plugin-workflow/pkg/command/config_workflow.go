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

package command

import (
	"fmt"
	"io/ioutil"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"gopkg.in/yaml.v3"
)

type Versions struct {
	QuarkusVersion string `yaml:"quarkusVersion"`
	KogitoVersion  string `yaml:"kogitoVersion"`
}

type Config struct {
	Versions Versions `yaml:"versions"`
}

func getConfigTemplate(quarkusVersion string, kogitoVersion string) (configJsonByte []byte, err error) {
	config := Config{
		Versions: Versions{
			QuarkusVersion: quarkusVersion,
			KogitoVersion:  kogitoVersion,
		},
	}

	configJsonByte, err = yaml.Marshal(config)
	if err != nil {
		fmt.Println("ERROR: marshaling config yml file.")
	}
	return
}

func CreateConfig(configFilePath string, quarkusVersion string, kogitoVersion string) (err error) {
	configFileData, err := getConfigTemplate(quarkusVersion, kogitoVersion)
	if err != nil {
		return
	}

	err = ioutil.WriteFile(configFilePath, configFileData, 0644)
	if err != nil {
		fmt.Printf("ERROR: writing the %s file.\n", common.WORKFLOW_CONFIG_YML)
		return
	}

	fmt.Printf("Config file created on %s \n", configFilePath)
	return
}

func ReadConfig(dependenciesVersion common.DependenciesVersion) (quarkusVersion string, kogitoVersion string, err error) {
	configFile, err := ioutil.ReadFile(common.WORKFLOW_CONFIG_YML)
	if err != nil {
		fmt.Printf("ERROR: reading the %s file.\n", common.WORKFLOW_CONFIG_YML)
		return
	}

	config := Config{}
	err = yaml.Unmarshal(configFile, &config)
	if err != nil {
		fmt.Printf("ERROR: unmarshaling the %s file.\n", common.WORKFLOW_CONFIG_YML)
		return
	}

	// check if quarkus and kogito versions are in the config file
	// if it's empty replace with the built version
	quarkusVersion = config.Versions.QuarkusVersion
	if len(quarkusVersion) == 0 {
		fmt.Println("WARNING: missing \"quarkusVersion\" value entry.")
		fmt.Printf("Using default value: %s\n", dependenciesVersion.QuarkusVersion)
		quarkusVersion = dependenciesVersion.QuarkusVersion
	}

	kogitoVersion = config.Versions.KogitoVersion
	if len(kogitoVersion) == 0 {
		fmt.Println("WARNING: missing \"kogitoVersion\" value entry.")
		fmt.Printf("Using default value: %s\n", dependenciesVersion.KogitoVersion)
		kogitoVersion = dependenciesVersion.KogitoVersion
	}

	return
}

func UpdateConfig(quarkusVersion string, kogitoVersion string) (err error) {
	configFile, err := ioutil.ReadFile(common.WORKFLOW_CONFIG_YML)
	if err != nil {
		fmt.Printf("ERROR: reading the %s file.\n", common.WORKFLOW_CONFIG_YML)
		return
	}

	config := Config{}
	err = yaml.Unmarshal(configFile, &config)
	if err != nil {
		fmt.Printf("ERROR: unmarshaling the %s file.\n", common.WORKFLOW_CONFIG_YML)
		return
	}

	config.Versions.QuarkusVersion = quarkusVersion
	config.Versions.KogitoVersion = kogitoVersion

	configJsonByte, err := yaml.Marshal(config)
	err = ioutil.WriteFile(common.WORKFLOW_CONFIG_YML, configJsonByte, 0644)
	if err != nil {
		fmt.Printf("ERROR: marshaling the %s file.\n", common.WORKFLOW_CONFIG_YML)
		return
	}

	return
}
