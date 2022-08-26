package converters

import (
	"context"
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
		converterToTest := NewKogitoServerlessWorkflowConverter(context)
		_, err := converterToTest.ToCNCFWorkflow(nil)
		if err == nil {
			t.Fatal("Expected an error")
		}
	})

}
