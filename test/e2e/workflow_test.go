//go:build integration
// +build integration

package e2e

import (
	"fmt"
	"github.com/davidesalerno/kogito-serverless-operator/test/utils"
	"os/exec"
	"path/filepath"
	"time"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/ginkgo"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/gomega"
)

// namespace store the ns where the Operator and Operand will be executed
const namespace = "kogito-serverless-operator-system"

var _ = Describe("kogito-serverless", func() {

	Context("ensure that Operator and Operand(s) can run in restricted namespaces", func() {
		BeforeEach(func() {

			// The namespace can be created when we run make install
			// However, in this test we want ensure that the solution
			// can run in a ns labeled as restricted. Therefore, we are
			// creating the namespace an lebeling it.
			By("creating manager namespace")
			cmd := exec.Command("kubectl", "create", "ns", namespace)
			_, _ = utils.Run(cmd)

			// Now, let's ensure that all namespaces can raise an Warn when we apply the manifests
			// and that the namespace where the Operator and Operand will run are enforced as
			// restricted so that we can ensure that both can be admitted and run with the enforcement
			By("labeling all namespaces to warn when we apply the manifest if would violate the PodStandards")
			cmd = exec.Command("kubectl", "label", "--overwrite", "ns", "--all",
				"pod-security.kubernetes.io/audit=restricted",
				"pod-security.kubernetes.io/enforce-version=v1.24",
				"pod-security.kubernetes.io/warn=restricted")
			_, err := utils.Run(cmd)
			ExpectWithOffset(1, err).NotTo(HaveOccurred())

			By("labeling enforce the namespace where the Operator and Operand(s) will run")
			cmd = exec.Command("kubectl", "label", "--overwrite", "ns", namespace,
				"pod-security.kubernetes.io/audit=restricted",
				"pod-security.kubernetes.io/enforce-version=v1.24",
				"pod-security.kubernetes.io/enforce=restricted")
			_, err = utils.Run(cmd)
			Expect(err).To(Not(HaveOccurred()))
		})

		AfterEach(func() {

			By("removing manager namespace")
			cmd := exec.Command("kubectl", "create", "ns", namespace)
			_, _ = utils.Run(cmd)
		})

		It("should successfully run the Kogito Serverless Operator", func() {
			var controllerPodName string
			var err error
			projectDir, _ := utils.GetProjectDir()

			// operatorImage store the name of the imahe used in the example
			const operatorImage = "=quay.io/davidesalerno/kogito-serverless-operator:v0.0.1"

			By("building the manager(Operator) image")
			cmd := exec.Command("make", "docker-build", "IMG=quay.io/davidesalerno/kogito-serverless-operator:v0.0.1")
			_, err = utils.Run(cmd)
			ExpectWithOffset(1, err).NotTo(HaveOccurred())

			By("loading the the manager(Operator) image on Kind")
			err = utils.LoadImageToKindClusterWithName("=quay.io/davidesalerno/kogito-serverless-operator:v0.0.1")
			ExpectWithOffset(1, err).NotTo(HaveOccurred())

			By("installing CRDs")
			cmd = exec.Command("make", "install")
			_, err = utils.Run(cmd)
			ExpectWithOffset(1, err).NotTo(HaveOccurred())

			By("deploying the controller-manager")
			cmd = exec.Command("make", "deploy", fmt.Sprintf("IMG=%s", operatorImage))
			outputMake, err := utils.Run(cmd)
			ExpectWithOffset(1, err).NotTo(HaveOccurred())

			By("validating that manager Pod/container(s) are restricted")
			ExpectWithOffset(1, outputMake).NotTo(ContainSubstring("Warning: would violate PodSecurity"))

			By("validating that the controller-manager pod is running as expected")
			verifyControllerUp := func() error {
				// Get pod name
				cmd = exec.Command("kubectl", "get",
					"pods", "-l", "control-plane=controller-manager",
					"-o", "go-template={{ range .items }}{{ if not .metadata.deletionTimestamp }}{{ .metadata.name }}"+
						"{{ \"\\n\" }}{{ end }}{{ end }}",
					"-n", namespace,
				)
				podOutput, err := utils.Run(cmd)
				ExpectWithOffset(2, err).NotTo(HaveOccurred())
				podNames := utils.GetNonEmptyLines(string(podOutput))
				if len(podNames) != 1 {
					return fmt.Errorf("expect 1 controller pods running, but got %d", len(podNames))
				}
				controllerPodName = podNames[0]
				ExpectWithOffset(2, controllerPodName).Should(ContainSubstring("controller-manager"))

				// Validate pod status
				cmd = exec.Command("kubectl", "get",
					"pods", controllerPodName, "-o", "jsonpath={.status.phase}",
					"-n", namespace,
				)
				status, err := utils.Run(cmd)
				ExpectWithOffset(2, err).NotTo(HaveOccurred())
				if string(status) != "Running" {
					return fmt.Errorf("controller pod in %s status", status)
				}
				return nil
			}
			EventuallyWithOffset(1, verifyControllerUp, time.Minute, time.Second).Should(Succeed())

			By("creating an instance of the Kogito Serverless Operand(CR)")
			EventuallyWithOffset(1, func() error {
				cmd = exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"config/samples/sw.kogito.kie.org__v08_kogitoserverlessworkflow.yaml"), "-n", namespace)
				_, err = utils.Run(cmd)
				return err
			}, time.Minute, time.Second).Should(Succeed())
		})
	})
})
