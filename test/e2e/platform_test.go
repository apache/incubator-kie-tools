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
	//nolint:golint
	//nolint:revive
	"bytes"
	"fmt"
	"math/rand"
	"os/exec"
	"path/filepath"
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

const (
	ephemeral  = "ephemeral"
	postgreSQL = "postgreSQL"
	dev        = "dev"
	production = "prod"
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
			By("Wait for SonatatFlowPlatform CR to complete deployment")
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
			Entry("with both Job Service and Data Index and ephemeral persistence and the workflow in a dev profile", test.GetSonataFlowE2EPlatformServicesDirectory(), dev, ephemeral),
			Entry("with both Job Service and Data Index and ephemeral persistence and the workflow in a production profile", test.GetSonataFlowE2EPlatformServicesDirectory(), production, ephemeral),
			Entry("with both Job Service and Data Index and postgreSQL persistence and the workflow in a dev profile", test.GetSonataFlowE2EPlatformServicesDirectory(), dev, postgreSQL),
			Entry("with both Job Service and Data Index and postgreSQL persistence and the workflow in a production profile", test.GetSonataFlowE2EPlatformServicesDirectory(), production, postgreSQL),
		)

	})
})
