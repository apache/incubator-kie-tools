/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package e2e

import (
	"fmt"
	"net/url"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"strings"
	"time"

	"github.com/apache/incubator-kie-kogito-serverless-operator/test"
	"github.com/apache/incubator-kie-kogito-serverless-operator/test/utils"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/ginkgo/v2"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/gomega"
)

// namespace store the ns where the Operator and Operand will be executed
const namespace = "sonataflow-operator-system"

const (
	minikubePlatform  = "minikube"
	openshiftPlatform = "openshift"
)

var _ = Describe("SonataFlow Operator", Ordered, func() {

	BeforeAll(func() {

		// Now, let's ensure that all namespaces can raise a Warn when we apply the manifests
		// and that the namespace where the Operator and Operand will run are enforced as
		// restricted so that we can ensure that both can be admitted and run with the enforcement

		// See: https://kubernetes.io/docs/tutorials/security/seccomp/

		/*
			   TODO: Uncomment to enable when https://issues.redhat.com/browse/KOGITO-9110 will be available
				By("labeling all namespaces to warn when we apply the manifest if would violate the PodStandards")
				cmd = exec.Command("kubectl", "label", "--overwrite", "ns", "--all",
					"pod-security.kubernetes.io/audit=restricted",
					"pod-security.kubernetes.io/enforce-version=v1.22",
					"pod-security.kubernetes.io/warn=restricted")
				_, err := utils.Run(cmd)
				ExpectWithOffset(1, err).NotTo(HaveOccurred())

				By("labeling enforce the namespace where the Operator and Operand(s) will run")
				cmd = exec.Command("kubectl", "label", "--overwrite", "ns", namespace,
					"pod-security.kubernetes.io/audit=restricted",
					"pod-security.kubernetes.io/enforce-version=v1.22",
					"pod-security.kubernetes.io/enforce=restricted")
				_, err = utils.Run(cmd)
				Expect(err).To(Not(HaveOccurred()))

		*/

		var controllerPodName string
		operatorImageName, err := utils.GetOperatorImageName()
		ExpectWithOffset(1, err).NotTo(HaveOccurred())

		By("deploying the controller-manager")
		cmd := exec.Command("make", "deploy", fmt.Sprintf("IMG=%s", operatorImageName))

		outputMake, err := utils.Run(cmd)
		fmt.Println(string(outputMake))
		ExpectWithOffset(1, err).NotTo(HaveOccurred())

		/* // TODO: Uncomment to enable when https://issues.redhat.com/browse/KOGITO-9110 will be available

		By("validating that manager Pod/container(s) are restricted")
		// Get Podsecurity violation lines
		lines, err := utils.StringToLines(string(outputMake))
		ExpectWithOffset(1, err).NotTo(HaveOccurred())
		var violationLines []string
		applySeccompProfilePatch := false
		for _, line := range lines {
			if strings.Contains(line, "Warning: would violate PodSecurity") {
				if strings.Contains(line, "must set securityContext.seccompProfile.type to") {
					// Ignore this violation as it is expected
					applySeccompProfilePatch = true
				} else {
					violationLines = append(violationLines, line)
				}
			}
		}
		Expect(violationLines).To(BeEmpty())

		if applySeccompProfilePatch {
			By("Applying seccompProfile")
			cmd = exec.Command("kubectl", "patch", "deployment", "sonataflow-operator-controller-manager", "-p", `{"spec":{"template":{"spec":{"securityContext":{"seccompProfile":{"type":"RuntimeDefault"}}}}}}`, "-n", namespace)
			_, err := utils.Run(cmd)
			if utils.IsDebugEnabled() {
				err = utils.OutputDeployment(namespace, "sonataflow-operator-controller-manager")
			}
			ExpectWithOffset(1, err).NotTo(HaveOccurred())
		}
		*/

		By("validating that the controller-manager pod is running as expected")
		verifyControllerUp := func() error {
			var podOutput []byte
			var err error

			if utils.IsDebugEnabled() {
				err = utils.OutputAllPods()
				err = utils.OutputAllEvents(namespace)
			}

			// Get pod name
			cmd = exec.Command("kubectl", "get",
				"pods", "-l", "control-plane=controller-manager",
				"-o", "go-template={{ range .items }}{{ if not .metadata.deletionTimestamp }}{{ .metadata.name }}"+
					"{{ \"\\n\" }}{{ end }}{{ end }}",
				"-n", namespace,
			)
			podOutput, err = utils.Run(cmd)
			fmt.Println(string(podOutput))
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
			fmt.Println(string(status))
			ExpectWithOffset(2, err).NotTo(HaveOccurred())
			if string(status) != "Running" {
				return fmt.Errorf("controller pod in %s status", status)
			}
			return nil
		}
		EventuallyWithOffset(1, verifyControllerUp, 2*time.Minute, time.Second).Should(Succeed())
	})

	AfterAll(func() {
		By("removing manager namespace")
		cmd := exec.Command("make", "undeploy")
		_, _ = utils.Run(cmd)
		By("uninstalling CRDs")
		cmd = exec.Command("make", "uninstall")
		_, _ = utils.Run(cmd)
	})

	Describe("ensure that Operator and Operand(s) can run in restricted namespaces", func() {
		projectDir, _ := utils.GetProjectDir()

		It("should successfully deploy the Simple Workflow in prod ops mode and verify if it's running", func() {
			By("creating an instance of the SonataFlow Operand(CR)")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowSimpleOpsYamlCR), "-n", namespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())

			By("check the workflow is in running state")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsInRunningState("simple") }, 15*time.Minute, 30*time.Second).Should(BeTrue())

			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "delete", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowSimpleOpsYamlCR), "-n", namespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())
		})

		It("should successfully deploy the Greeting Workflow in prod mode and verify if it's running", func() {
			By("creating external resources DataInputSchema configMap")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsDataInputSchemaConfig), "-n", namespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())

			By("creating an instance of the SonataFlow Operand(CR)")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsWithDataInputSchemaCR), "-n", namespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())

			By("check the workflow is in running state")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsInRunningState("greeting") }, 15*time.Minute, 30*time.Second).Should(BeTrue())

			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "delete", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsWithDataInputSchemaCR), "-n", namespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())
		})

		It("should successfully deploy the orderprocessing workflow in devmode and verify if it's running", func() {

			By("creating an instance of the SonataFlow Workflow in DevMode")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					test.GetSonataFlowE2eOrderProcessingFolder()), "-n", namespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())

			By("check the workflow is in running state")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsInRunningState("orderprocessing") }, 10*time.Minute, 30*time.Second).Should(BeTrue())

			cmdLog := exec.Command("kubectl", "logs", "orderprocessing", "-n", namespace)
			if responseLog, errLog := utils.Run(cmdLog); errLog == nil {
				GinkgoWriter.Println(fmt.Sprintf("devmode podlog %s", responseLog))
			}

			By("check that the workflow is addressable")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsAddressable("orderprocessing") }, 10*time.Minute, 30*time.Second).Should(BeTrue())

			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "delete", "-f", filepath.Join(projectDir,
					test.GetSonataFlowE2eOrderProcessingFolder()), "-n", namespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())
		})
	})
})

func verifyWorkflowIsInRunningState(workflowName string) bool {
	cmd := exec.Command("kubectl", "get", "workflow", workflowName, "-n", namespace, "-o", "jsonpath={.status.conditions[?(@.type=='Running')].status}")
	if response, err := utils.Run(cmd); err != nil {
		GinkgoWriter.Println(fmt.Errorf("failed to check if greeting workflow is running: %v", err))
		return false
	} else {
		GinkgoWriter.Println(fmt.Sprintf("Got response %s", response))

		if len(strings.TrimSpace(string(response))) > 0 {
			status, err := strconv.ParseBool(string(response))
			if err != nil {
				GinkgoWriter.Println(fmt.Errorf("failed to parse result %v", err))
				return false
			}
			return status
		}
		return false
	}
}

func verifyWorkflowIsAddressable(workflowName string) bool {
	cmd := exec.Command("kubectl", "get", "workflow", workflowName, "-n", namespace, "-o", "jsonpath={.status.address.url}")
	if response, err := utils.Run(cmd); err != nil {
		GinkgoWriter.Println(fmt.Errorf("failed to check if greeting workflow is running: %v", err))
		return false
	} else {
		GinkgoWriter.Println(fmt.Sprintf("Got response %s", response))
		if len(strings.TrimSpace(string(response))) > 0 {
			_, err := url.ParseRequestURI(string(response))
			if err != nil {
				GinkgoWriter.Println(fmt.Errorf("failed to parse result %v", err))
				return false
			}
			// The response is a valid URL so the test is passed
			return true
		}
		return false
	}
}

func getSonataFlowPlatformFilename() string {
	if getClusterPlatform() == openshiftPlatform {
		return test.GetPlatformOpenshiftE2eTest()
	}
	return test.GetPlatformMinikubeE2eTest()
}

func getClusterPlatform() string {
	if v, ok := os.LookupEnv("CLUSTER_PLATFORM"); ok {
		return v
	}
	return minikubePlatform
}
