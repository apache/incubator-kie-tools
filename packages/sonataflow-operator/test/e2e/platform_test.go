// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package e2e

import (
	"bytes"
	"encoding/json"
	"fmt"
	"math/rand"
	"os/exec"
	"path/filepath"
	"strings"
	"time"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test/utils"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/ginkgo/v2"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/gomega"
)

var _ = Describe("Platform Use Cases :: ", Label("platform"), Ordered, func() {

	var (
		projectDir       string
		targetNamespace  string
		targetNamespace2 string
	)

	BeforeEach(func() {
		targetNamespace = fmt.Sprintf("test-%d", rand.Intn(randomIntRange)+1)
		cmd := exec.Command("kubectl", "create", "namespace", targetNamespace)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())

		targetNamespace2 = fmt.Sprintf("test-%d", rand.Intn(1024)+1)
		cmd = exec.Command("kubectl", "create", "namespace", targetNamespace2)
		_, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
	})
	AfterEach(func() {
		// Remove resources in test namespace with no failure
		if !CurrentSpecReport().Failed() {
			if len(targetNamespace) > 0 {
				cmd := exec.Command("kubectl", "delete", "namespace", targetNamespace, "--wait")
				_, err := utils.Run(cmd)
				Expect(err).NotTo(HaveOccurred())
			}
			if len(targetNamespace2) > 0 {
				cmd := exec.Command("kubectl", "delete", "namespace", targetNamespace2, "--wait")
				_, err := utils.Run(cmd)
				Expect(err).NotTo(HaveOccurred())
			}
		}
	})

	var _ = Describe("Db migration :: ", Ordered, func() {

		Describe("ensure service based db migration", func() {
			projectDir, _ := utils.GetProjectDir()
			It("should successfully deploy the SonataFlowPlatform with data index and jobs service", func() {
				By("Deploy the CR")
				var manifests []byte
				EventuallyWithOffset(1, func() error {
					var err error
					cmd := exec.Command("kubectl", "kustomize", filepath.Join(projectDir,
						"test/e2e/testdata/platform/persistence/service_based_db_migration"))
					manifests, err = utils.Run(cmd)
					return err
				}, time.Minute, time.Second).Should(Succeed())
				cmd := exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
				cmd.Stdin = bytes.NewBuffer(manifests)
				_, err := utils.Run(cmd)
				Expect(err).NotTo(HaveOccurred())

				By("Wait for SonatatFlowPlatform CR to complete deployment")
				// wait for service deployments to be ready
				EventuallyWithOffset(1, func() error {
					cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "--for", "condition=Ready", "--timeout=5s")
					_, err = utils.Run(cmd)
					return err
				}, 10*time.Minute, 5).Should(Succeed())

				By("Evaluate status of all service's health endpoint")
				cmd = exec.Command("kubectl", "get", "pod", "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "-n", targetNamespace, "-ojsonpath={.items[*].metadata.name}")
				output, err := utils.Run(cmd)
				Expect(err).NotTo(HaveOccurred())
				for _, pn := range strings.Split(string(output), " ") {
					verifyHealthStatusInPod(pn, targetNamespace)
				}
			})
		})

	})

	var _ = Context("with platform services", func() {

		DescribeTable("when creating a simple workflow", func(testcaseDir string, profile metadata.ProfileType, persistenceType string) {
			By("Deploy the SonataFlowPlatform CR")
			var manifests []byte
			EventuallyWithOffset(1, func() error {
				var err error
				cmd := exec.Command("kubectl", "kustomize", filepath.Join(projectDir,
					testcaseDir, profile.String(), persistenceType))
				manifests, err = utils.Run(cmd)
				return err
			}, time.Minute, time.Second).Should(Succeed())
			cmd := exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
			cmd.Stdin = bytes.NewBuffer(manifests)
			_, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
			By("Wait for SonataFlowPlatform CR to complete deployment")
			// wait for service deployments to be ready
			EventuallyWithOffset(1, func() bool {
				cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "--for", "condition=Ready", "--timeout=5s")
				_, err = utils.Run(cmd)
				if err != nil {
					return false
				}
				if profile == metadata.GitOpsProfile {
					GinkgoWriter.Println("waitForPodRestartCompletion")
					waitForPodRestartCompletion("app.kubernetes.io/name=jobs-service", targetNamespace)
					GinkgoWriter.Println("waitForPodRestartCompletion done")
					return true
				}
				return true
			}, 30*time.Minute, 5).Should(BeTrue())
			By("Evaluate status of service's health endpoint")
			cmd = exec.Command("kubectl", "get", "pod", "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "-n", targetNamespace, "-ojsonpath={.items[*].metadata.name}")
			output, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
			// remove the last CR that is added by default as the last character of the string.
			for _, pn := range strings.Split(string(output), " ") {
				verifyHealthStatusInPod(pn, targetNamespace)
			}
			By("Deploy the SonataFlow CR")
			cmd = exec.Command("kubectl", "create", "-n", targetNamespace, "-f", filepath.Join(projectDir,
				testcaseDir, profile.String(), persistenceType, "sonataflow"))
			manifests, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())

			By("Retrieve SonataFlow CR name")
			cmd = exec.Command("kubectl", "get", "sonataflow", "-n", targetNamespace, `-ojsonpath={.items[*].metadata.name}`)
			output, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
			sfNames := strings.TrimRight(string(output), " ")

			if profile == metadata.GitOpsProfile {
				workflowTag := prebuiltWorkflows.CallBack.Tag
				if persistenceType == postgreSQL {
					workflowTag = prebuiltWorkflows.CallBackPersistence.Tag
				}
				By("Replacing the image with a prebuilt one and rollout")
				EventuallyWithOffset(1, func() error {
					return kubectlPatchSonataFlowImageAndRollout(targetNamespace, sfNames, workflowTag)
				}, 3*time.Minute, time.Second).Should(Succeed())
			}

			By("Evaluate status of SonataFlow CR")
			for _, sf := range strings.Split(sfNames, " ") {
				Expect(sf).NotTo(BeEmpty(), "sonataflow name is empty")
				EventuallyWithOffset(1, func() bool {
					return verifyWorkflowIsInRunningState(sf, targetNamespace)
				}, 10*time.Minute, 5*time.Second).Should(BeTrue())
			}
		},
			Entry("with both Job Service and Data Index and ephemeral persistence and the workflow in a dev profile", test.GetPathFromE2EDirectory("platform", "services"), metadata.DevProfile, ephemeral),
			Entry("with both Job Service and Data Index and ephemeral persistence and the workflow in a gitops profile", test.GetPathFromE2EDirectory("platform", "services"), metadata.GitOpsProfile, ephemeral),
			Entry("with both Job Service and Data Index and postgreSQL persistence and the workflow in a dev profile", test.GetPathFromE2EDirectory("platform", "services"), metadata.DevProfile, postgreSQL),
			Entry("with both Job Service and Data Index and postgreSQL persistence and the workflow in a gitops profile", test.GetPathFromE2EDirectory("platform", "services"), metadata.GitOpsProfile, postgreSQL),
		)

	})

	DescribeTable("when deploying a SonataFlowPlatform CR with PostgreSQL Persistence", func(testcaseDir string) {
		By("Deploy the CR")
		var manifests []byte
		EventuallyWithOffset(1, func() error {
			var err error
			cmd := exec.Command("kubectl", "kustomize", testcaseDir)
			manifests, err = utils.Run(cmd)
			return err
		}, time.Minute, time.Second).Should(Succeed())
		cmd := exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
		cmd.Stdin = bytes.NewBuffer(manifests)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		By("Wait for SonatatFlowPlatform CR to complete deployment")
		// wait for service deployments to be ready
		EventuallyWithOffset(1, func() error {
			cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "--for", "condition=Ready", "--timeout=5s")
			_, err = utils.Run(cmd)
			return err
		}, 10*time.Minute, 5).Should(Succeed())
		By("Evaluate status of all service's health endpoint")
		cmd = exec.Command("kubectl", "get", "pod", "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "-n", targetNamespace, "-ojsonpath={.items[*].metadata.name}")
		output, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		for _, pn := range strings.Split(string(output), " ") {
			verifyHealthStatusInPod(pn, targetNamespace)
		}
	},
		Entry("and both Job Service and Data Index using the persistence from platform CR", test.GetPathFromE2EDirectory("platform", "persistence", "generic_from_platform_cr")),
		Entry("and both Job Service and Data Index using the one defined in each service, discarding the one from the platform CR", test.GetPathFromE2EDirectory("platform", "persistence", "overwritten_by_services")),
		Entry("Job Service and Data Index come up with service based db migration", test.GetPathFromE2EDirectory("platform", "persistence", "service_based_db_migration")),
	)

	DescribeTable("when deploying a SonataFlowPlatform CR with PostgreSQL Persistence and using Job based DB migration", func(testcaseDir string) {
		By("Deploy the Postgres DB")
		var manifests []byte
		EventuallyWithOffset(1, func() error {
			var err error
			cmd := exec.Command("kubectl", "kustomize", testcaseDir+"/pg-service")
			manifests, err = utils.Run(cmd)
			return err
		}, time.Minute, time.Second).Should(Succeed())
		cmd := exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
		cmd.Stdin = bytes.NewBuffer(manifests)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())

		By("Wait for Postgres DB to come alive")
		// wait for service deployments to be ready
		EventuallyWithOffset(1, func() error {
			cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app.kubernetes.io/name in (postgres)", "--for", "condition=Ready", "--timeout=5s")
			_, err = utils.Run(cmd)
			return err
		}, 5*time.Minute, 5).Should(Succeed())

		By("Deploy the CR")
		EventuallyWithOffset(1, func() error {
			var err error
			cmd := exec.Command("kubectl", "kustomize", testcaseDir+"/sonataflow-platform")
			manifests, err = utils.Run(cmd)
			return err
		}, time.Minute, time.Second).Should(Succeed())
		cmd = exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
		cmd.Stdin = bytes.NewBuffer(manifests)
		_, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())

		By("Wait for SonatatFlowPlatform CR to complete deployment")
		// wait for service deployments to be ready
		EventuallyWithOffset(1, func() error {
			cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "--for", "condition=Ready", "--timeout=5s")
			_, err = utils.Run(cmd)
			return err
		}, 10*time.Minute, 5).Should(Succeed())

		By("Evaluate status of all service's health endpoint")
		cmd = exec.Command("kubectl", "get", "pod", "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "-n", targetNamespace, "-ojsonpath={.items[*].metadata.name}")
		output, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		for _, pn := range strings.Split(string(output), " ") {
			verifyHealthStatusInPod(pn, targetNamespace)
		}
	},
		Entry("Job Service and Data Index come up with job based db migration", test.GetPathFromE2EDirectory("platform", "persistence", "job_based_db_migration")),
	)

	DescribeTable("when deploying a SonataFlowPlatform CR with brokers", func(testcaseDir string) {
		By("Deploy the brokers")
		cmd := exec.Command("kubectl", "create", "-n", targetNamespace, "-f", filepath.Join(projectDir,
			testcaseDir, "broker"))
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())

		By("Wait for the brokers to be ready")
		EventuallyWithOffset(1, func() error {
			cmd = exec.Command("kubectl", "wait", "broker", "-l", "test=test-e2e", "-n", targetNamespace, "--for", "condition=Ready=True", "--timeout=5s")
			_, err = utils.Run(cmd)
			return err
		}, time.Minute, time.Second).Should(Succeed())

		By("Deploy the CR")
		var manifests []byte
		EventuallyWithOffset(1, func() error {
			var err error
			cmd := exec.Command("kubectl", "kustomize", testcaseDir)
			manifests, err = utils.Run(cmd)
			return err
		}, time.Minute, time.Second).Should(Succeed())
		cmd = exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
		cmd.Stdin = bytes.NewBuffer(manifests)
		_, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		By("Wait for SonatatFlowPlatform CR to complete deployment")
		// wait for service deployments to be ready
		EventuallyWithOffset(1, func() error {
			cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "--for", "condition=Ready", "--timeout=5s")
			_, err = utils.Run(cmd)
			return err
		}, 10*time.Minute, 5).Should(Succeed())

		GinkgoWriter.Println("waitForPodRestartCompletion")
		waitForPodRestartCompletion("app.kubernetes.io/name=jobs-service", targetNamespace)
		GinkgoWriter.Println("waitForPodRestartCompletion done")

		By("Evaluate status of all service's health endpoint")
		cmd = exec.Command("kubectl", "get", "pod", "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "-n", targetNamespace, "-ojsonpath={.items[*].metadata.name}")
		output, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		for _, pn := range strings.Split(string(output), " ") {
			verifyHealthStatusInPod(pn, targetNamespace)
		}
		By("Evaluate triggers and sinkbindings")
		cmd = exec.Command("kubectl", "get", "sonataflowplatform", "sonataflow-platform", "-n", targetNamespace, "-ojsonpath={.status.triggers}")
		output, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		var triggers []operatorapi.SonataFlowPlatformTriggerRef
		err = json.Unmarshal(output, &triggers)
		Expect(err).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-error-", constants.KogitoProcessInstancesEventsPath, targetNamespace, "di-source")).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-node-", constants.KogitoProcessInstancesEventsPath, targetNamespace, "di-source")).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-state-", constants.KogitoProcessInstancesEventsPath, targetNamespace, "di-source")).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-variable-", constants.KogitoProcessInstancesEventsPath, targetNamespace, "di-source")).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-definition-", constants.KogitoProcessDefinitionsEventsPath, targetNamespace, "di-source")).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-jobs-", constants.KogitoJobsPath, targetNamespace, "di-source")).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "jobs-service-create-job-", constants.JobServiceJobEventsPath, targetNamespace, "js-source")).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "jobs-service-delete-job-", constants.JobServiceJobEventsPath, targetNamespace, "js-source")).NotTo(HaveOccurred())
		Expect(verifySinkBinding("sonataflow-platform-jobs-service-sb", targetNamespace, "js-sink")).NotTo(HaveOccurred())
	},
		Entry("and both Job Service and Data Index have service level brokers", test.GetPathFromE2EDirectory("platform", "services", "gitops", "knative", "service-level-broker")),
	)

	DescribeTable("when deploying a SonataFlowPlatform CR with platform broker", func(testcaseDir string, brokerInAnotherNamespace bool) {
		By("Deploy the broker")
		brokerName := "default"
		brokerNamespace := targetNamespace
		if brokerInAnotherNamespace {
			brokerNamespace = targetNamespace2
		}
		GinkgoWriter.Println(fmt.Sprintf("testcaseDir=%v, brokerNamespace = %s", testcaseDir, brokerNamespace))
		cmd := exec.Command("kubectl", "create", "-n", brokerNamespace, "-f", filepath.Join(projectDir,
			testcaseDir, "broker"))
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())

		By("Wait for the broker to be ready")
		EventuallyWithOffset(1, func() error {
			cmd = exec.Command("kubectl", "wait", "broker", brokerName, "-n", brokerNamespace, "--for", "condition=Ready=True", "--timeout=5s")
			_, err = utils.Run(cmd)
			return err
		}, time.Minute, time.Second).Should(Succeed())

		By("Deploy the SonataFlowPlatform CR")
		var manifests []byte
		EventuallyWithOffset(1, func() error {
			var err error
			cmd := exec.Command("kubectl", "kustomize", testcaseDir)
			manifests, err = utils.Run(cmd)
			return err
		}, time.Minute, time.Second).Should(Succeed())
		manifestsUpdated := strings.ReplaceAll(string(manifests), "${BROKER_NAMESPACE}", brokerNamespace)
		cmd = exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
		cmd.Stdin = bytes.NewBuffer([]byte(manifestsUpdated))
		_, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())

		By("Wait for SonatatFlowPlatform CR to complete deployment")
		// wait for service deployments to be ready
		EventuallyWithOffset(1, func() error {
			cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "--for", "condition=Ready", "--timeout=5s")
			_, err = utils.Run(cmd)
			return err
		}, 10*time.Minute, 5).Should(Succeed())

		GinkgoWriter.Println("waitForPodRestartCompletion")
		waitForPodRestartCompletion("app.kubernetes.io/name=jobs-service", targetNamespace)
		GinkgoWriter.Println("waitForPodRestartCompletion done")

		By("Evaluate status of all service's health endpoint")
		cmd = exec.Command("kubectl", "get", "pod", "-l", "app.kubernetes.io/name in (jobs-service,data-index-service)", "-n", targetNamespace, "-ojsonpath={.items[*].metadata.name}")
		output, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		for _, pn := range strings.Split(string(output), " ") {
			verifyHealthStatusInPod(pn, targetNamespace)
		}
		By("Evaluate triggers and sinkbindings for DI and JS")
		cmd = exec.Command("kubectl", "get", "sonataflowplatform", "sonataflow-platform", "-n", targetNamespace, "-ojsonpath={.status.triggers}")
		output, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		var triggers []operatorapi.SonataFlowPlatformTriggerRef
		err = json.Unmarshal(output, &triggers)
		Expect(err).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-error-", constants.KogitoProcessInstancesEventsPath, brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-node-", constants.KogitoProcessInstancesEventsPath, brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-state-", constants.KogitoProcessInstancesEventsPath, brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-variable-", constants.KogitoProcessInstancesEventsPath, brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-process-definition-", constants.KogitoProcessDefinitionsEventsPath, brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "data-index-jobs-", constants.KogitoJobsPath, brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "jobs-service-create-job-", constants.JobServiceJobEventsPath, brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, "jobs-service-delete-job-", constants.JobServiceJobEventsPath, brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifySinkBinding("sonataflow-platform-jobs-service-sb", targetNamespace, brokerName)).NotTo(HaveOccurred())

		By("Deploy the SonataFlow CR")
		cmd = exec.Command("kubectl", "create", "-n", targetNamespace, "-f", filepath.Join(projectDir,
			testcaseDir, "sonataflow"))
		manifests, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())

		By("Replacing the image with a prebuilt one and rollout")
		EventuallyWithOffset(1, func() error {
			return kubectlPatchSonataFlowImageAndRollout(targetNamespace, prebuiltWorkflows.CallBack.Name, prebuiltWorkflows.CallBack.Tag)
		}, 3*time.Minute, time.Second).Should(Succeed())

		By("Evaluate status of SonataFlow CR")
		EventuallyWithOffset(1, func() bool {
			return verifyWorkflowIsInRunningState(prebuiltWorkflows.CallBack.Name, targetNamespace)
		}, 5*time.Minute, 5).Should(BeTrue())

		By("Evaluate triggers and sinkbindings for the workflow")
		cmd = exec.Command("kubectl", "get", "sonataflow", prebuiltWorkflows.CallBack.Name, "-n", targetNamespace, "-ojsonpath={.status.triggers}")
		output, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		err = json.Unmarshal(output, &triggers)
		Expect(err).NotTo(HaveOccurred())
		Expect(verifyTrigger(triggers, prebuiltWorkflows.CallBack.Name, "", brokerNamespace, brokerName)).NotTo(HaveOccurred())
		Expect(verifySinkBinding(fmt.Sprintf("%s-sb", prebuiltWorkflows.CallBack.Name), targetNamespace, brokerName)).NotTo(HaveOccurred())
	},
		Entry("and with broker and platform in the same namespace", test.GetPathFromE2EDirectory("platform", "services", "gitops", "knative", "platform-level-broker"), false),
		Entry("and with broker and platform in a separate namespace", test.GetPathFromE2EDirectory("platform", "services", "gitops", "knative", "platform-level-broker"), true),
	)
})
