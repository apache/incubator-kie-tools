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

	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
)

const (
	buildRequestKey   = "build-request"
	buildLimitKey     = "build-limit"
	runtimeRequestKey = "runtime-request"
	runtimeLimitKey   = "runtime-limit"
)

func addDefaultJavaOptionsIfNotProvided(spec v1alpha1.KogitoServiceSpec) {
	javaOptionsProvided := false
	for _, env := range spec.Envs {
		if env.Name == javaOptionsEnvVar {
			javaOptionsProvided = true
		}
	}

	if !javaOptionsProvided {
		spec.AddEnvironmentVariable(javaOptionsEnvVar, "-Xmx2G")
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
