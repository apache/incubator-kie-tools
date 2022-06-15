package pkg

import (
	"swf-cli/kn-plugin-workflow/pkg/command"

	"github.com/spf13/cobra"
)

func NewRootCommand() *cobra.Command {
	var rootCmd = &cobra.Command{
		Use:   "kn-workflow",
		Short: "Serverless Workflow",
		Long:  "Manage Quarkus workflow projects",
	}

	rootCmd.AddCommand(command.NewBuildCommand())
	rootCmd.AddCommand(command.NewCreateCommand())
	rootCmd.AddCommand(command.NewDeployCommand())

	return rootCmd
}
