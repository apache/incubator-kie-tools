/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils

import (
	"bytes"
	"github.com/kiegroup/container-builder/util/log"
	apiv08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"io/ioutil"
	"k8s.io/apimachinery/pkg/util/yaml"
)

func GetKogitoServerlessPlatform(path string) (*apiv08.KogitoServerlessPlatform, error) {

	ksp := &apiv08.KogitoServerlessPlatform{}
	yamlFile, err := ioutil.ReadFile(path)
	if err != nil {
		log.Errorf(err, "yamlFile.Get err #%v ", err)
		return nil, err
	}
	// Important: Here we are reading the CR deployment file from a given path and creating a &apiv08.KogitoServerlessPlatform struct
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(ksp)
	if err != nil {
		log.Errorf(err, "Unmarshal: %v", err)
		return nil, err
	}
	log.Debugf("Successfully read KSP  #%v ", ksp)
	return ksp, err
}
