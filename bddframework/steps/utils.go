// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package steps

import (
	"fmt"
	"strings"

	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
)

const (
	mavenArgsAppendEnvVar = "MAVEN_ARGS_APPEND"
	javaOptionsEnvVar     = "JAVA_OPTIONS"

	//DataTable first column
	configKey         = "config"
	buildEnvKey       = "build-env"
	runtimeEnvKey     = "runtime-env"
	labelKey          = "label"
	buildRequestKey   = "build-request"
	buildLimitKey     = "build-limit"
	runtimeRequestKey = "runtime-request"
	runtimeLimitKey   = "runtime-limit"

	//DataTable second column
	nativeKey      = "native"
	persistenceKey = "persistence"
	eventsKey      = "events"
)

func configureKogitoAppFromTable(table *messages.PickleStepArgument_PickleTable, kogitoApp *v1alpha1.KogitoApp) error {
	if len(table.Rows) == 0 { // Using default configuration
		return nil
	}

	if len(table.Rows[0].Cells) != 3 {
		return fmt.Errorf("expected table to have exactly three columns")
	}

	var profiles []string

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)
		switch firstColumn {
		case configKey:
			parseConfigRow(row, kogitoApp, &profiles)

		case buildEnvKey:
			kogitoApp.Spec.Build.AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

		case runtimeEnvKey:
			kogitoApp.Spec.AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

		case labelKey:
			kogitoApp.Spec.Service.Labels[getSecondColumn(row)] = getThirdColumn(row)

		case buildRequestKey:
			kogitoApp.Spec.Build.AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

		case buildLimitKey:
			kogitoApp.Spec.Build.AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

		case runtimeRequestKey:
			kogitoApp.Spec.AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

		case runtimeLimitKey:
			kogitoApp.Spec.AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

		default:
			return fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}
	}

	if len(profiles) > 0 {
		kogitoApp.Spec.Build.AddEnvironmentVariable(mavenArgsAppendEnvVar, "-P"+strings.Join(profiles, ","))
	}

	addDefaultJavaOptionsIfNotProvided(kogitoApp)

	return nil
}

func parseConfigRow(row *messages.PickleStepArgument_PickleTable_PickleTableRow, kogitoApp *v1alpha1.KogitoApp, profilesPtr *[]string) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case nativeKey:
		native := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if native {
			kogitoApp.Spec.Build.Native = native
			// Make sure that enough memory is allocated for builder pod in case of native build
			kogitoApp.Spec.Build.AddResourceRequest("memory", "4Gi")
		}

	case persistenceKey:
		persistence := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if persistence {
			*profilesPtr = append(*profilesPtr, "persistence")
			kogitoApp.Spec.EnablePersistence = true
		}

	case eventsKey:
		events := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if events {
			*profilesPtr = append(*profilesPtr, "events")
			kogitoApp.Spec.EnableEvents = true
		}
	}
}

func addDefaultJavaOptionsIfNotProvided(kogitoApp *v1alpha1.KogitoApp) {
	javaOptionsProvided := false
	for _, env := range kogitoApp.Spec.Envs {
		if env.Name == javaOptionsEnvVar {
			javaOptionsProvided = true
		}
	}

	if !javaOptionsProvided {
		kogitoApp.Spec.AddEnvironmentVariable(javaOptionsEnvVar, "-Xmx2G")
	}
}

func getFirstColumn(row *messages.PickleStepArgument_PickleTable_PickleTableRow) string {
	return row.Cells[0].Value
}

func getSecondColumn(row *messages.PickleStepArgument_PickleTable_PickleTableRow) string {
	return row.Cells[1].Value
}

func getThirdColumn(row *messages.PickleStepArgument_PickleTable_PickleTableRow) string {
	return row.Cells[2].Value
}

// parseResourceRequirementsTable is useful for steps that check resource requirements, table is a subset of KogitoApp
// configuration table
func parseResourceRequirementsTable(table *messages.PickleStepArgument_PickleTable) (build, runtime *v1.ResourceRequirements, err error) {
	build = &v1.ResourceRequirements{Limits: v1.ResourceList{}, Requests: v1.ResourceList{}}
	runtime = &v1.ResourceRequirements{Limits: v1.ResourceList{}, Requests: v1.ResourceList{}}

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)
		switch firstColumn {
		case buildRequestKey:
			build.Requests[v1.ResourceName(getSecondColumn(row))] = resource.MustParse(getThirdColumn(row))

		case buildLimitKey:
			build.Limits[v1.ResourceName(getSecondColumn(row))] = resource.MustParse(getThirdColumn(row))

		case runtimeRequestKey:
			runtime.Requests[v1.ResourceName(getSecondColumn(row))] = resource.MustParse(getThirdColumn(row))

		case runtimeLimitKey:
			runtime.Limits[v1.ResourceName(getSecondColumn(row))] = resource.MustParse(getThirdColumn(row))

		default:
			return build, runtime, fmt.Errorf("Unrecognized resource option: %s", firstColumn)
		}

	}
	return
}
