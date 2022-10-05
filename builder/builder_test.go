package builder

import (
	"context"
	"github.com/davidesalerno/kogito-serverless-operator/constants"
	"github.com/stretchr/testify/assert"
	"os"
	"testing"
	"time"
)

// Minikube must be up and running with the default BUILDER_NAMESPACE_DEFAULT available,
// if the repo is not available on quay, will be created with the first pull but wil be created private
func TestBuild(t *testing.T) {
	wd, _ := os.Getwd()
	dockerFile, err := os.ReadFile(wd + "/../builder/Dockerfile")
	if err != nil {
		panic("Can't read dockerfile")
	}
	source, err := os.ReadFile(wd + "/../test/builder/greetings.sw.json")
	if err != nil {
		panic("Can't read source file")
	}

	ib := NewImageBuilder("greetings.sw.json", source, dockerFile)
	ib.OnNamespace(constants.BUILDER_NAMESPACE_DEFAULT)
	ib.WithPodMiddleName(constants.BUILDER_IMG_NAME_DEFAULT)
	ib.WithInsecureRegistry(false)
	ib.WithImageName("greetings:latest")
	ib.WithSecret("regcred")
	ib.WithRegistryAddress("quay.io/mdessi")

	ib.WithTimeout(5 * time.Minute)

	builder := NewBuilder(context.TODO())
	build, error := builder.BuildImage(ib.Build())
	// after some build minikube need more time for the schedule
	time.Sleep(20 * time.Second)
	assert.NotNilf(t, build, "Build  result")
	assert.Nil(t, error, "Build Error ")

}
