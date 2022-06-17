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

package create

import (
	"gopkg.in/yaml.v2"
)

type MetadataStruct struct {
	Name      string `yaml:name`
	Namespace string `yaml:namespace`
}

type PortsStruct struct {
	ContainerPort int `yaml:"containerPort"`
}

type ContainerStruct struct {
	Image string        `yaml:image`
	Ports []PortsStruct `yaml:"ports"`
}

type TemplateSpecStruct struct {
	Containers []ContainerStruct `yaml:containers`
}

type TemplateStruct struct {
	Spec TemplateSpecStruct `yaml:spec`
}

type SpecStruct struct {
	Template TemplateStruct `yaml:template`
}

type Config struct {
	ApiVersion string         `yaml:"apiVersion"`
	Kind       string         `yaml:kind`
	Metadata   MetadataStruct `yaml:metadata`
	Spec       SpecStruct     `yaml:spec`
}

func GenerateConfigYamlTemplate(cfg CreateConfig) ([]byte, error) {
	configYaml := Config{
		ApiVersion: "serving.knative.dev/v1",
		Kind:       "Service",
		Metadata: MetadataStruct{
			Name:      cfg.ProjectName,
			Namespace: cfg.Namespace,
		},
		Spec: SpecStruct{
			Template: TemplateStruct{
				Spec: TemplateSpecStruct{
					Containers: []ContainerStruct{{
						Image: cfg.Image,
						Ports: []PortsStruct{{
							ContainerPort: 8080,
						}},
					}},
				},
			},
		},
	}

	return yaml.Marshal(&configYaml)
}
