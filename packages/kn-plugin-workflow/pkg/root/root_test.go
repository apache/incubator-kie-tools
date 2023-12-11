package root

import (
	"bytes"
	"testing"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/spf13/cobra"
	"github.com/stretchr/testify/require"
)

var cfgTestInputRoot = RootCmdConfig{Name: "kn\u00A0workflow", Version: metadata.PluginVersion}

func TestNewRootCommand(t *testing.T) {
	//https://issues.redhat.com/browse/KOGITO-9847
	//t.Run("Test root command name and help", func(t *testing.T) {
	//	cmd := NewRootCommand(cfgTestInputRoot)
	//	output, err := ExecuteCommandSoft(cmd)
	//	require.NoError(t, err, "Error: %v", err)
	//	require.Contains(t, output, "Usage:\n  kn\u00a0workflow [command]\n\nAliases:\n  kn\u00a0workflow, kn-workflow")
	//	require.Contains(t, output, "Use \"kn\u00a0workflow [command] --help\" for more information about a command.")
	//})

	t.Run("Check subcommands except Cobra generated (help, completion)", func(t *testing.T) {
		expectedSubCommands := []string{
			"create",
			"deploy",
			"quarkus",
			"run",
			"undeploy",
			"gen-manifest",
			"version",
		}

		cmd := NewRootCommand(cfgTestInputRoot)
		require.True(t, cmd.HasSubCommands())
		require.Equal(t, len(expectedSubCommands), len(cmd.Commands()))

		for _, e := range expectedSubCommands {
			_, _, err := cmd.Find([]string{e})
			require.NoError(t, err, "Root command should have subcommand `%q`", e)
		}
	})
}

// ExecuteCommandC execute cobra.command and catch the output
func ExecuteCommandSoftC(root *cobra.Command, args ...string) (c *cobra.Command, output string, err error) {
	buf := new(bytes.Buffer)
	root.SetOut(buf)
	root.SetErr(buf)
	root.SetArgs(args)
	c, err = root.ExecuteC()
	return c, buf.String(), err
}

// ExecuteCommand similar to ExecuteCommandC but does not return *cobra.Command
func ExecuteCommandSoft(root *cobra.Command, args ...string) (output string, err error) {
	_, o, err := ExecuteCommandSoftC(root, args...)
	return o, err
}
