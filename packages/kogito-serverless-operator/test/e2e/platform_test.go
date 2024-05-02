// Copyright 2024 Apache Software Foundation (ASF)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package e2e

import (
	"bytes"
	"fmt"
	"math/rand"
	"os/exec"
	"path/filepath"
	"strings"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test/utils"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/ginkgo/v2"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/gomega"
)

const (
	ephemeral            = "ephemeral"
	postgreSQL           = "postgreSQL"
	clusterWideEphemeral = "cluster-wide-ephemeral"
	ephemeralDataIndex   = "ephemeral-data-index"
	ephemeralJobService  = "ephemeral-job-service"
)

var _ = Describe("Validate the persistence", Ordered, func() {

	var (
		projectDir      string
		targetNamespace string
	)

	BeforeEach(func() {
		targetNamespace = fmt.Sprintf("test-%d", rand.Intn(1024)+1)
		cmd := exec.Command("kubectl", "create", "namespace", targetNamespace)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
	})
	AfterEach(func() {
		// Remove resources in test namespace with no failure
		if !CurrentSpecReport().Failed() && len(targetNamespace) > 0 {
			cmd := exec.Command("kubectl", "delete", "namespace", targetNamespace, "--wait")
			_, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
		}
	})
	var _ = Context("with platform services", func() {

		DescribeTable("when creating a simple workflow", func(testcaseDir string, profile string, persistenceType string) {
			By("Deploy the SonataFlowPlatform CR")
			var manifests []byte
			EventuallyWithOffset(1, func() error {
				var err error
				cmd := exec.Command("kubectl", "kustomize", filepath.Join(projectDir,
					testcaseDir, profile, persistenceType))
				manifests, err = utils.Run(cmd)
				return err
			}, time.Minute, time.Second).Should(Succeed())
			cmd := exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
			cmd.Stdin = bytes.NewBuffer(manifests)
			_, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
			By("Wait for SonataFlowPlatform CR to complete deployment")
			// wait for service deployments to be ready
			EventuallyWithOffset(1, func() error {
				cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app=sonataflow-platform", "--for", "condition=Ready", "--timeout=5s")
				_, err = utils.Run(cmd)
				return err
			}, 20*time.Minute, 5).Should(Succeed())
			By("Evaluate status of service's health endpoint")
			cmd = exec.Command("kubectl", "get", "pod", "-l", "app=sonataflow-platform", "-n", targetNamespace, "-ojsonpath={.items[*].metadata.name}")
			output, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
			// remove the last CR that is added by default as the last character of the string.
			for _, pn := range strings.Split(string(output), " ") {
				verifyHealthStatusInPod(pn, targetNamespace)
			}
			By("Deploy the SonataFlow CR")
			cmd = exec.Command("kubectl", "create", "-n", targetNamespace, "-f", filepath.Join(projectDir,
				testcaseDir, profile, persistenceType, "sonataflow"))
			manifests, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())

			By("Retrieve SonataFlow CR name")
			cmd = exec.Command("kubectl", "get", "sonataflow", "-n", targetNamespace, `-ojsonpath={.items[*].metadata.name}`)
			output, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
			sfNames := strings.TrimRight(string(output), " ")

			By("Evaluate status of SonataFlow CR")
			for _, sf := range strings.Split(string(sfNames), " ") {
				Expect(sf).NotTo(BeEmpty(), "sonataflow name is empty")
				EventuallyWithOffset(1, func() bool {
					return verifyWorkflowIsInRunningStateInNamespace(sf, targetNamespace)
				}, 10*time.Minute, 5).Should(BeTrue())
			}
		},
			Entry("with both Job Service and Data Index and ephemeral persistence and the workflow in a dev profile", test.GetSonataFlowE2EPlatformServicesDirectory(), metadata.DevProfile.String(), ephemeral),
			Entry("with both Job Service and Data Index and ephemeral persistence and the workflow in a preview profile", test.GetSonataFlowE2EPlatformServicesDirectory(), metadata.PreviewProfile.String(), ephemeral),
			Entry("with both Job Service and Data Index and postgreSQL persistence and the workflow in a dev profile", test.GetSonataFlowE2EPlatformServicesDirectory(), metadata.DevProfile.String(), postgreSQL),
			Entry("with both Job Service and Data Index and postgreSQL persistence and the workflow in a preview profile", test.GetSonataFlowE2EPlatformServicesDirectory(), metadata.PreviewProfile.String(), postgreSQL),
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
			cmd = exec.Command("kubectl", "wait", "pod", "-n", targetNamespace, "-l", "app=sonataflow-platform", "--for", "condition=Ready", "--timeout=5s")
			_, err = utils.Run(cmd)
			return err
		}, 10*time.Minute, 5).Should(Succeed())
		By("Evaluate status of all service's health endpoint")
		cmd = exec.Command("kubectl", "get", "pod", "-l", "app=sonataflow-platform", "-n", targetNamespace, "-ojsonpath={.items[*].metadata.name}")
		output, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
		for _, pn := range strings.Split(string(output), " ") {
			verifyHealthStatusInPod(pn, targetNamespace)
		}
	},
		Entry("and both Job Service and Data Index using the persistence from platform CR", test.GetSonataFlowE2EPlatformPersistenceSampleDataDirectory("generic_from_platform_cr")),
		Entry("and both Job Service and Data Index using the one defined in each service, discarding the one from the platform CR", test.GetSonataFlowE2EPlatformPersistenceSampleDataDirectory("overwritten_by_services")),
	)

})
