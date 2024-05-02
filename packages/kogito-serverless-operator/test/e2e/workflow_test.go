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
	"bytes"
	"fmt"
	"math/rand"
	"os/exec"
	"path/filepath"
	"strings"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test/utils"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/ginkgo/v2"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/gomega"
)

var _ = Describe("SonataFlow Operator", Ordered, func() {

	var targetNamespace string
	BeforeEach(func() {
		targetNamespace = fmt.Sprintf("test-%d", rand.Intn(1024)+1)
		cmd := exec.Command("kubectl", "create", "namespace", targetNamespace)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
	})
	AfterEach(func() {
		// Remove resources in test namespace
		if !CurrentSpecReport().Failed() && len(targetNamespace) > 0 {
			cmd := exec.Command("kubectl", "delete", "namespace", targetNamespace, "--wait")
			_, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
		}
	})

	Describe("ensure that Operator and Operand(s) can run in restricted namespaces", func() {
		projectDir, _ := utils.GetProjectDir()

		It("should successfully deploy the Simple Workflow in  GitOps mode and verify if it's running", func() {
			By("creating an instance of the SonataFlow Operand(CR)")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowSimpleOpsYamlCR), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 3*time.Minute, time.Second).Should(Succeed())

			By("check the workflow is in running state")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsInRunningState("simple", targetNamespace) }, 15*time.Minute, 30*time.Second).Should(BeTrue())

			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "delete", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowSimpleOpsYamlCR), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 3*time.Minute, time.Second).Should(Succeed())
		})

		It("should successfully deploy the Greeting Workflow in preview mode and verify if it's running", func() {
			By("creating external resources DataInputSchema configMap")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsDataInputSchemaConfig), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 3*time.Minute, time.Second).Should(Succeed())

			By("creating an instance of the SonataFlow Operand(CR)")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsWithDataInputSchemaCR), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 3*time.Minute, time.Second).Should(Succeed())

			By("check the workflow is in running state")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsInRunningState("greeting", targetNamespace) }, 15*time.Minute, 30*time.Second).Should(BeTrue())

			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "delete", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsWithDataInputSchemaCR), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 3*time.Minute, time.Second).Should(Succeed())
		})

		It("should successfully deploy the orderprocessing workflow in devmode and verify if it's running", func() {

			By("creating an instance of the SonataFlow Workflow in DevMode")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					test.GetSonataFlowE2eOrderProcessingFolder()), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 3*time.Minute, time.Second).Should(Succeed())

			By("check the workflow is in running state")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsInRunningState("orderprocessing", targetNamespace) }, 10*time.Minute, 30*time.Second).Should(BeTrue())

			cmdLog := exec.Command("kubectl", "logs", "orderprocessing", "-n", targetNamespace)
			if responseLog, errLog := utils.Run(cmdLog); errLog == nil {
				GinkgoWriter.Println(fmt.Sprintf("devmode podlog %s", responseLog))
			}

			By("check that the workflow is addressable")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsAddressable("orderprocessing", targetNamespace) }, 10*time.Minute, 30*time.Second).Should(BeTrue())

			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "delete", "-f", filepath.Join(projectDir,
					test.GetSonataFlowE2eOrderProcessingFolder()), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 3*time.Minute, time.Second).Should(Succeed())
		})

	})

})

var _ = Describe("Validate the persistence ", Ordered, func() {

	const (
		dbConnectionName = "Database connections health check"
		defaultDataCheck = "<default>"
	)
	var (
		ns string
	)

	BeforeEach(func() {
		ns = fmt.Sprintf("test-%d", rand.Intn(1024)+1)
		cmd := exec.Command("kubectl", "create", "namespace", ns)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
	})
	AfterEach(func() {
		// Remove platform CR if it exists
		if len(ns) > 0 {
			cmd := exec.Command("kubectl", "delete", "namespace", ns, "--wait")
			_, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
		}

	})

	DescribeTable("when deploying a SonataFlow CR with PostgreSQL persistence", func(testcaseDir string, withPersistence bool) {
		By("Deploy the CR")
		var manifests []byte
		EventuallyWithOffset(1, func() error {
			var err error
			cmd := exec.Command("kubectl", "kustomize", testcaseDir)
			manifests, err = utils.Run(cmd)
			return err
		}, time.Minute, time.Second).Should(Succeed())
		cmd := exec.Command("kubectl", "create", "-n", ns, "-f", "-")
		cmd.Stdin = bytes.NewBuffer(manifests)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		By("Wait for SonatatFlow CR to complete deployment")
		// wait for service deployments to be ready
		EventuallyWithOffset(1, func() error {
			cmd = exec.Command("kubectl", "wait", "pod", "-n", ns, "-l", "sonataflow.org/workflow-app", "--for", "condition=Ready", "--timeout=5s")
			out, err := utils.Run(cmd)
			GinkgoWriter.Printf("%s\n", string(out))
			return err
		}, 12*time.Minute, 5).Should(Succeed())

		By("Evaluate status of the workflow's pod database connection health endpoint")
		cmd = exec.Command("kubectl", "get", "pod", "-l", "sonataflow.org/workflow-app", "-n", ns, "-ojsonpath={.items[*].metadata.name}")
		output, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		EventuallyWithOffset(1, func() bool {
			for _, pn := range strings.Split(string(output), " ") {
				h, err := getHealthFromPod(pn, ns)
				if err != nil {
					continue
				}
				Expect(h.Status).To(Equal(upStatus), "Pod health is not UP")
				for _, c := range h.Checks {
					if c.Name == dbConnectionName {
						Expect(c.Status).To(Equal(upStatus), "Pod's database connection is not UP")
						if withPersistence {
							Expect(c.Data[defaultDataCheck]).To(Equal(upStatus), "Pod's 'default' database data is not UP")
							return true
						} else {
							Expect(defaultDataCheck).NotTo(BeElementOf(c.Data), "Pod's 'default' database data check exists in health manifest")
							return true
						}
					}
				}
			}
			return false
		}, 1*time.Minute).Should(BeTrue())
	},
		Entry("defined in the workflow from an existing kubernetes service as a reference", test.GetSonataFlowE2EWorkflowPersistenceSampleDataDirectory("by_service"), true),
		Entry("defined in the workflow and from the sonataflow platform", test.GetSonataFlowE2EWorkflowPersistenceSampleDataDirectory("from_platform_overwritten_by_service"), true),
		Entry("defined from the sonataflow platform as reference and with DI and JS", test.GetSonataFlowE2EWorkflowPersistenceSampleDataDirectory("from_platform_with_di_and_js_services"), true),
		Entry("defined from the sonataflow platform as reference and without DI and JS", test.GetSonataFlowE2EWorkflowPersistenceSampleDataDirectory("from_platform_without_di_and_js_services"), true),
		Entry("defined from the sonataflow platform as reference but not required by the workflow", test.GetSonataFlowE2EWorkflowPersistenceSampleDataDirectory("from_platform_with_no_persistence_required"), false),
	)

})
