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
	"math/rand"
	"os/exec"
	"path/filepath"
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

var _ = Describe("SonataFlow Operator", func() {

	var targetNamespace string
	BeforeEach(func() {
		targetNamespace = fmt.Sprintf("test-%d", rand.Intn(1024)+1)
		cmd := exec.Command("kubectl", "create", "namespace", targetNamespace)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
	})
	AfterEach(func() {
		// Remove resources in test namespace
		if !CurrentGinkgoTestDescription().Failed && len(targetNamespace) > 0 {
			cmd := exec.Command("kubectl", "delete", "namespace", targetNamespace, "--wait")
			_, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
		}
	})

	Describe("ensure that Operator and Operand(s) can run in restricted namespaces", func() {
		projectDir, _ := utils.GetProjectDir()

		It("should successfully deploy the Simple Workflow in prod ops mode and verify if it's running", func() {
			By("creating an instance of the SonataFlow Operand(CR)")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowSimpleOpsYamlCR), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())

			By("check the workflow is in running state")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsInRunningState("simple", targetNamespace) }, 15*time.Minute, 30*time.Second).Should(BeTrue())

			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "delete", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowSimpleOpsYamlCR), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())
		})

		It("should successfully deploy the Greeting Workflow in prod mode and verify if it's running", func() {
			By("creating external resources DataInputSchema configMap")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsDataInputSchemaConfig), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())

			By("creating an instance of the SonataFlow Operand(CR)")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsWithDataInputSchemaCR), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())

			By("check the workflow is in running state")
			EventuallyWithOffset(1, func() bool { return verifyWorkflowIsInRunningState("greeting", targetNamespace) }, 15*time.Minute, 30*time.Second).Should(BeTrue())

			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "delete", "-f", filepath.Join(projectDir,
					"test/testdata/"+test.SonataFlowGreetingsWithDataInputSchemaCR), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())
		})

		It("should successfully deploy the orderprocessing workflow in devmode and verify if it's running", func() {

			By("creating an instance of the SonataFlow Workflow in DevMode")
			EventuallyWithOffset(1, func() error {
				cmd := exec.Command("kubectl", "apply", "-f", filepath.Join(projectDir,
					test.GetSonataFlowE2eOrderProcessingFolder()), "-n", targetNamespace)
				_, err := utils.Run(cmd)
				return err
			}, 2*time.Minute, time.Second).Should(Succeed())

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
			}, 2*time.Minute, time.Second).Should(Succeed())
		})
	})

})
