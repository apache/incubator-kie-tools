package cmd

import (
	"os"
	"os/exec"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestCliBinary(t *testing.T) {
	tempDir := t.TempDir()
	binPath := "../dist/image-env-to-json-linux-amd64"
	// Run the CLI binary with arguments
	runCmd := exec.Command(binPath, "--help")
	output, err := runCmd.CombinedOutput()
	assert.NoError(t, err, "CLI command failed")
	assert.Contains(t, string(output), "Usage:", "Help text not found in output")

	// Run the CLI with a JSON Schema file
	schemaPath := "testdata/schema.json" // Place a sample schema in `testdata/`
	directoryPath := filepath.Join(t.TempDir(), "env.json")

	runCmd = exec.Command(binPath, "--directory", directoryPath, "--json-schema", schemaPath)
	runCmd.Env = append(os.Environ(), "MY_ENV=value1", "MY_ENV2=value2") // Set environment vars

	envJsonPath := filepath.Join(t.TempDir(), "env.json")
	_, err = runCmd.CombinedOutput()
	assert.NoError(t, err, "CLI command failed")
	assert.FileExists(t, envJsonPath, "env.json should be created")

	// Validate `env.json` content (Optional)
	content, err := os.ReadFile(envJsonPath)
	assert.NoError(t, err)
	assert.Contains(t, string(content), `"MY_ENV": "value1"`)
	assert.Contains(t, string(content), `"MY_ENV2": "value2"`)
}
