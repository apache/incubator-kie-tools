/*
Copyright 2022.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package constants

func WorkflowMetadataKeys() func(string) string {
	// innerMap is captured in the closure returned below
	innerMap := map[string]string{
		"key":             "sw.kogito.kie.org/key",
		"name":            "sw.kogito.kie.org/name",
		"description":     "sw.kogito.kie.org/description",
		"annotations":     "sw.kogito.kie.org/annotations",
		"dataInputSchema": "sw.kogito.kie.org/dataInputSchema",
		"expressionLang":  "sw.kogito.kie.org/expressionLang",
		"metadata":        "sw.kogito.kie.org/metadata",
		"version":         "sw.kogito.kie.org/version",
	}

	return func(key string) string {
		return innerMap[key]
	}
}

func DeploymentMetadataKeys() func(string) string {
	// innerMap is captured in the closure returned below
	innerMap := map[string]string{
		"label":           "sw.kogito.kie.org/label",
		"serviceType":     "sw.kogito.kie.org/name",
		"description":     "sw.kogito.kie.org/description",
		"annotations":     "sw.kogito.kie.org/annotations",
		"dataInputSchema": "sw.kogito.kie.org/dataInputSchema",
		"expressionLang":  "sw.kogito.kie.org/expressionLang",
		"metadata":        "sw.kogito.kie.org/metadata",
		"version":         "sw.kogito.kie.org/version",
	}

	return func(key string) string {
		return innerMap[key]
	}
}

func PlatformAnnotation() func(string) string {
	// innerMap is captured in the closure returned below
	innerMap := map[string]string{
		"SecondaryPlatformAnnotation": "sw.kogito.kie.org/secondary.platform",
		"OperatorIDAnnotation":        "sw.kogito.kie.org/operator.id",
	}

	return func(key string) string {
		return innerMap[key]
	}
}
