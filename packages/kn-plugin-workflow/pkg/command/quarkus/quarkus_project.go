package quarkus

import (
	"fmt"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
)

type CreateQuarkusProjectConfig struct {
	ProjectName         string
	Extensions          string // List of extensions separated by "," to be added to the Quarkus project
	DependenciesVersion metadata.DependenciesVersion
}

func CreateQuarkusProject(cfg CreateQuarkusProjectConfig) error {
	if err := common.CheckProjectName(cfg.ProjectName); err != nil {
		return err
	}
	exists, err := common.CheckIfDirExists(cfg.ProjectName)
	if err != nil || exists {
		return fmt.Errorf("directory with name \"%s\" already exists: %w", cfg.ProjectName, err)
	}
	create := common.ExecCommand(
		"mvn",
		fmt.Sprintf("%s:%s:%s:create", cfg.DependenciesVersion.QuarkusPlatformGroupId, metadata.QuarkusMavenPlugin, cfg.DependenciesVersion.QuarkusVersion),
		"-DprojectGroupId=org.acme",
		"-DnoCode",
		fmt.Sprintf("-DplatformVersion=%s", cfg.DependenciesVersion.QuarkusVersion),
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extensions))

	if err := common.RunCommand(
		create,
		"create",
	); err != nil {
		return err
	}

	return nil
}
