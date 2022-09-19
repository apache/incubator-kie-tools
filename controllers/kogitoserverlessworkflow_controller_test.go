package controllers

import (
	"context"
	"github.com/davidesalerno/kogito-serverless-operator/api/v1alpha1"
	"github.com/davidesalerno/kogito-serverless-operator/test/utils"
	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/kubernetes/scheme"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
	"testing"
)

func TestKogitoServerlessWorkflowController(t *testing.T) {
	t.Run("verify that a basic reconcile is performed without error", func(t *testing.T) {
		var (
			name      = "kogito-serverless-operator"
			namespace = "kogito-serverless-operator-system"
		)
		// Create a KogitoServerlessWorkflow object with metadata and spec.
		ksw, errYaml := utils.GetKogitoServerlessWorkflow("../config/samples/kie.kogito.sw.org__v1alpha1_kogitoserverlessworkflow.yaml")
		if errYaml != nil {
			t.Fatalf("Error reading YAML file #%v ", errYaml)
		}
		// Objects to track in the fake client.
		objs := []runtime.Object{ksw}

		// Register operator types with the runtime scheme.
		s := scheme.Scheme
		s.AddKnownTypes(v1alpha1.GroupVersion, ksw)

		// Create a fake client to mock API calls.
		cl := fake.NewFakeClient(objs...)

		// Create a KogitoServerlessWorkflowReconciler object with the scheme and fake client.
		r := &KogitoServerlessWorkflowReconciler{cl, s}

		// Mock request to simulate Reconcile() being called on an event for a
		// watched resource .
		req := reconcile.Request{
			NamespacedName: types.NamespacedName{
				Name:      name,
				Namespace: namespace,
			},
		}
		_, err := r.Reconcile(context.TODO(), req)
		if err != nil {
			t.Fatalf("reconcile: (%v)", err)
		}
		// Perform some checks on the created CR
		assert.True(t, ksw.Spec.Start == "ChooseOnLanguage")
		assert.True(t, len(ksw.Spec.States) == 4)
	})
}
