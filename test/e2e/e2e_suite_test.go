package e2e

import (
	"fmt"
	"testing"

	. "github.com/onsi/ginkgo"
	. "github.com/onsi/gomega"
)

// Run e2e tests using the Ginkgo runner.
func TestE2E(t *testing.T) {
	RegisterFailHandler(Fail)
	fmt.Fprintf(GinkgoWriter, "Starting Kogito Serverless Operator suite\n")
	RunSpecs(t, "Kogito Serverless e2e suite")
}
