package converters

import (
	"context"
	"github.com/davidesalerno/kogito-serverless-operator/test/utils"
	"github.com/stretchr/testify/assert"
	"k8s.io/client-go/rest"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/envtest"
	"testing"
)

var cfg *rest.Config
var k8sClient client.Client
var testEnv *envtest.Environment

func TestKogitoServerlessWorkflowConverter(t *testing.T) {
	t.Run("verify that when KogitoServerlessWorkflow CR is nil an error is returned", func(t *testing.T) {
		context := context.TODO()
		// Create a KogitoServerlessWorkflow object with metadata and spec.
		ksw, _ := utils.GetKogitoServerlessWorkflow("../config/samples/kie.kogito.sw.org__v1alpha1_kogitoserverlessworkflow.yaml")
		converterToTest := NewKogitoServerlessWorkflowConverter(context)
		out, err := converterToTest.ToCNCFWorkflow(ksw)
		assert.NoError(t, err)
		assert.True(t, out != nil)
		assert.True(t, out.Name == "greeting")
		assert.True(t, out.Description == "Greeting example on k8s!")
		assert.True(t, out.Functions != nil && len(out.Functions) == 1)
		assert.True(t, out.States != nil && len(out.States) == 4)
	})

}
