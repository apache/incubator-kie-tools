package steps

import (
	"fmt"
	"strings"

	"github.com/cucumber/godog/gherkin"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
)

const (
	//DataTable first column
	Config         = "config"
	BuildEnv       = "build-env"
	RuntimeEnv     = "runtime-env"
	Label          = "label"
	BuildRequest   = "build-request"
	BuildLimit     = "build-limit"
	RuntimeRequest = "runtime-request"
	RuntimeLimit   = "runtime-limit"

	//DataTable second column
	Native      = "native"
	Persistence = "persistence"
	Events      = "events"
)

func configureKogitoAppFromTable(table *gherkin.DataTable, kogitoApp *v1alpha1.KogitoApp) error {
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
		case Config:
			parseConfigRow(row, kogitoApp, &profiles)

		case BuildEnv:
			kogitoApp.Spec.Build.AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

		case RuntimeEnv:
			kogitoApp.Spec.AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

		case Label:
			kogitoApp.Spec.Service.Labels[getSecondColumn(row)] = getThirdColumn(row)

		case BuildRequest:
			kogitoApp.Spec.Build.AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

		case BuildLimit:
			kogitoApp.Spec.Build.AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

		case RuntimeRequest:
			kogitoApp.Spec.AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

		case RuntimeLimit:
			kogitoApp.Spec.AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

		default:
			return fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}
	}

	if len(profiles) > 0 {
		kogitoApp.Spec.Build.AddEnvironmentVariable(MavenArgsAppendEnvVar, "-P" + strings.Join(profiles, ","))
	}

	addDefaultJavaOptionsIfNotProvided(kogitoApp)

	return nil
}

func parseConfigRow(row *gherkin.TableRow, kogitoApp *v1alpha1.KogitoApp, profilesPtr *[]string) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case Native:
		native := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if native {
			kogitoApp.Spec.Build.Native = native
			// Make sure that enough memory is allocated for builder pod in case of native build
			kogitoApp.Spec.Build.AddResourceRequest("memory", "4Gi")
		}

	case Persistence:
		persistence := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if persistence {
			*profilesPtr = append(*profilesPtr, "persistence")
			kogitoApp.Spec.EnablePersistence = true
		}

	case Events:
		events := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if events {
			*profilesPtr = append(*profilesPtr, "events")
			kogitoApp.Spec.EnableEvents = true
			kogitoApp.Spec.KogitoServiceSpec.AddEnvironmentVariable("MP_MESSAGING_OUTGOING_KOGITO_PROCESSINSTANCES_EVENTS_BOOTSTRAP_SERVERS", "")
			kogitoApp.Spec.KogitoServiceSpec.AddEnvironmentVariable("MP_MESSAGING_OUTGOING_KOGITO_USERTASKINSTANCES_EVENTS_BOOTSTRAP_SERVERS", "")
		}
	}
}

func addDefaultJavaOptionsIfNotProvided(kogitoApp *v1alpha1.KogitoApp) {
	javaOptionsProvided := false
	for _, env := range kogitoApp.Spec.Envs {
		if env.Name == JavaOptionsEnvVar {
			javaOptionsProvided = true
		}
	}

	if !javaOptionsProvided {
		kogitoApp.Spec.AddEnvironmentVariable(JavaOptionsEnvVar, "-Xmx2G")
	}
}

func getFirstColumn(row *gherkin.TableRow) string {
	return row.Cells[0].Value
}

func getSecondColumn(row *gherkin.TableRow) string {
	return row.Cells[1].Value
}

func getThirdColumn(row *gherkin.TableRow) string {
	return row.Cells[2].Value
}

// parseResourceRequirementsTable is useful for steps that check resource requirements, table is a subset of KogitoApp
// configuration table
func parseResourceRequirementsTable(table *gherkin.DataTable) (build, runtime *v1.ResourceRequirements, err error) {
	build = &v1.ResourceRequirements{Limits: v1.ResourceList{}, Requests: v1.ResourceList{}}
	runtime = &v1.ResourceRequirements{Limits: v1.ResourceList{}, Requests: v1.ResourceList{}}

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)
		switch firstColumn {
		case BuildRequest:
			build.Requests[v1.ResourceName(getSecondColumn(row))] = resource.MustParse(getThirdColumn(row))

		case BuildLimit:
			build.Limits[v1.ResourceName(getSecondColumn(row))] = resource.MustParse(getThirdColumn(row))

		case RuntimeRequest:
			runtime.Requests[v1.ResourceName(getSecondColumn(row))] = resource.MustParse(getThirdColumn(row))

		case RuntimeLimit:
			runtime.Limits[v1.ResourceName(getSecondColumn(row))] = resource.MustParse(getThirdColumn(row))

		default:
			return build, runtime, fmt.Errorf("Unrecognized resource option: %s", firstColumn)
		}

	}
	return
}
