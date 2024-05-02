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
	"time"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/platform/services"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test/utils"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/ginkgo/v2"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/gomega"
)

var _ = Describe("Validate a clusterplatform", Ordered, func() {

	var (
		projectDir       string
		targetNamespace  string
		targetNamespace2 string
	)

	BeforeEach(func() {
		targetNamespace = fmt.Sprintf("test-%d", rand.Intn(1024)+1)
		cmd := exec.Command("kubectl", "create", "namespace", targetNamespace)
		_, err := utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())

		targetNamespace2 = fmt.Sprintf("test-%d", rand.Intn(1024)+1)
		cmd = exec.Command("kubectl", "create", "namespace", targetNamespace2)
		_, err = utils.Run(cmd)
		Expect(err).NotTo(HaveOccurred())
	})
	AfterEach(func() {
		// Remove resources in test namespacs with no failure
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
	var _ = Context("with supporting services enabled", func() {
		DescribeTable("against a platform in a separate namespace", func(testcaseDir string, profile string, persistenceType string, withServices bool) {
			By("Deploy the SonataFlowPlatform CR")
			var manifests []byte
			EventuallyWithOffset(1, func() error {
				var err error
				cmd := exec.Command("kubectl", "kustomize", filepath.Join(projectDir,
					test.GetSonataFlowE2EPlatformServicesDirectory(), profile, clusterWideEphemeral))
				manifests, err = utils.Run(cmd)
				return err
			}, time.Minute, time.Second).Should(Succeed())

			cmd := exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
			cmd.Stdin = bytes.NewBuffer(manifests)
			_, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())

			By("Wait for SonatatFlowPlatform CR in " + targetNamespace + " to be ready")
			// wait for platform to be ready
			EventuallyWithOffset(1, func() error {
				cmd = exec.Command("kubectl", "wait", "sfplatform", "-n", targetNamespace, "sonataflow-platform", "--for", "condition=Succeed", "--timeout=5s")
				_, err = utils.Run(cmd)
				return err
			}, 20*time.Minute, 5).Should(Succeed())
			EventuallyWithOffset(1, func() []byte {
				cmd = exec.Command("kubectl", "get", "sfplatform", "-n", targetNamespace, "sonataflow-platform", "-o", "jsonpath='{.status.clusterPlatformRef}'")
				returnedValue, _ := utils.Run(cmd)
				println(string(returnedValue))
				return returnedValue
			}, 20*time.Minute, 5).Should(Equal([]byte("''")))

			By("Evaluate status of SonataFlowClusterPlatform CR")
			cmd = exec.Command("kubectl", "patch", "SonataFlowClusterPlatform", "cluster", "--type", "merge", "-p", `{"spec": {"platformRef": {"namespace": "`+targetNamespace+`"}}}`)
			_, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
			EventuallyWithOffset(1, func() error {
				cmd = exec.Command("kubectl", "wait", "SonataFlowClusterPlatform", "cluster", "--for", "condition=Succeed", "--timeout=5s")
				_, err = utils.Run(cmd)
				return err
			}, 20*time.Minute, 5).Should(Succeed())

			if withServices {
				By("Deploy SonatatFlowPlatform CR with services configured in " + targetNamespace2)
				EventuallyWithOffset(1, func() error {
					var err error
					cmd := exec.Command("kubectl", "kustomize", filepath.Join(projectDir,
						testcaseDir, profile, persistenceType))
					manifests, err = utils.Run(cmd)
					return err
				}, time.Minute, time.Second).Should(Succeed())

				cmd = exec.Command("kubectl", "create", "-n", targetNamespace2, "-f", "-")
				cmd.Stdin = bytes.NewBuffer(manifests)
				_, err = utils.Run(cmd)
				Expect(err).NotTo(HaveOccurred())

				By("Wait for SonatatFlowPlatform CR in " + targetNamespace2 + " to be ready")
				// wait for platform to be ready
				EventuallyWithOffset(1, func() error {
					cmd = exec.Command("kubectl", "wait", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "--for", "condition=Succeed", "--timeout=5s")
					_, err = utils.Run(cmd)
					return err
				}, 20*time.Minute, 5).Should(Succeed())
				EventuallyWithOffset(1, func() []byte {
					cmd = exec.Command("kubectl", "get", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "-o", "jsonpath='{.status.clusterPlatformRef}'")
					returnedValue, _ := utils.Run(cmd)
					return returnedValue
				}, 20*time.Minute, 5).Should(Not(Equal([]byte("''"))))
				EventuallyWithOffset(1, func() []byte {
					cmd = exec.Command("kubectl", "get", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "-o", "jsonpath='{.status.clusterPlatformRef.services}'")
					returnedValue, _ := utils.Run(cmd)
					return returnedValue
				}, 20*time.Minute, 5).Should(Equal([]byte("''")))

				dataIndexServiceUrl := services.GenerateServiceURL(constants.KogitoServiceURLProtocol, targetNamespace2, "sonataflow-platform-"+constants.DataIndexServiceName)
				jobServiceUrl := services.GenerateServiceURL(constants.KogitoServiceURLProtocol, targetNamespace2, "sonataflow-platform-"+constants.JobServiceName)
				EventuallyWithOffset(1, func() []byte {
					cmd = exec.Command("kubectl", "get", "sf", "-n", targetNamespace2, "callbackstatetimeouts", "-o", "jsonpath='{.status.services.dataIndexRef.url}'")
					returnedValue, _ := utils.Run(cmd)
					return returnedValue
				}, 20*time.Minute, 5).Should(Equal([]byte("'" + dataIndexServiceUrl + "'")))
				EventuallyWithOffset(1, func() []byte {
					cmd = exec.Command("kubectl", "get", "sf", "-n", targetNamespace2, "callbackstatetimeouts", "-o", "jsonpath='{.status.services.jobServiceRef.url}'")
					returnedValue, _ := utils.Run(cmd)
					return returnedValue
				}, 20*time.Minute, 5).Should(Equal([]byte("'" + jobServiceUrl + "'")))
			} else {
				EventuallyWithOffset(1, func() error {
					var err error
					cmd := exec.Command("kubectl", "kustomize", filepath.Join(projectDir,
						testcaseDir, profile, persistenceType))
					manifests, err = utils.Run(cmd)
					return err
				}, time.Minute, time.Second).Should(Succeed())

				cmd = exec.Command("kubectl", "create", "-n", targetNamespace2, "-f", "-")
				cmd.Stdin = bytes.NewBuffer(manifests)
				_, err = utils.Run(cmd)
				Expect(err).NotTo(HaveOccurred())

				By("Wait for SonatatFlowPlatform CR in " + targetNamespace2 + " to be ready")
				dataIndexServiceUrl := services.GenerateServiceURL(constants.KogitoServiceURLProtocol, targetNamespace, "sonataflow-platform-"+constants.DataIndexServiceName)
				jobServiceUrl := services.GenerateServiceURL(constants.KogitoServiceURLProtocol, targetNamespace, "sonataflow-platform-"+constants.JobServiceName)
				// wait for platform to be ready
				EventuallyWithOffset(1, func() error {
					cmd = exec.Command("kubectl", "wait", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "--for", "condition=Succeed", "--timeout=5s")
					_, err = utils.Run(cmd)
					return err
				}, 20*time.Minute, 5).Should(Succeed())
				EventuallyWithOffset(1, func() []byte {
					cmd = exec.Command("kubectl", "get", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "-o", "jsonpath='{.status.clusterPlatformRef.services.dataIndexRef.url}'")
					returnedValue, _ := utils.Run(cmd)
					return returnedValue
				}, 20*time.Minute, 5).Should(Equal([]byte("'" + dataIndexServiceUrl + "'")))
				EventuallyWithOffset(1, func() []byte {
					cmd = exec.Command("kubectl", "get", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "-o", "jsonpath='{.status.clusterPlatformRef.services.jobServiceRef.url}'")
					returnedValue, _ := utils.Run(cmd)
					return returnedValue
				}, 20*time.Minute, 5).Should(Equal([]byte("'" + jobServiceUrl + "'")))
				EventuallyWithOffset(1, func() []byte {
					cmd = exec.Command("kubectl", "get", "sf", "-n", targetNamespace2, "callbackstatetimeouts", "-o", "jsonpath='{.status.services.dataIndexRef.url}'")
					returnedValue, _ := utils.Run(cmd)
					return returnedValue
				}, 20*time.Minute, 5).Should(Equal([]byte("'" + dataIndexServiceUrl + "'")))
				EventuallyWithOffset(1, func() []byte {
					cmd = exec.Command("kubectl", "get", "sf", "-n", targetNamespace2, "callbackstatetimeouts", "-o", "jsonpath='{.status.services.jobServiceRef.url}'")
					returnedValue, _ := utils.Run(cmd)
					return returnedValue
				}, 20*time.Minute, 5).Should(Equal([]byte("'" + jobServiceUrl + "'")))
			}
			cmd = exec.Command("kubectl", "delete", "SonataFlowClusterPlatform", "cluster", "--wait")
			_, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
		},
			Entry("without services configured", test.GetSonataFlowE2EPlatformNoServicesDirectory(), metadata.PreviewProfile.String(), ephemeral, false),
			Entry("with services configured", test.GetSonataFlowE2EPlatformServicesDirectory(), metadata.PreviewProfile.String(), "ephemeral-with-workflow", true),
		)

		DescribeTable("against a platform in a separate namespace", func(testcaseDir string, profile string, persistenceType string) {
			By("Deploy the SonataFlowPlatform CR")
			var manifests []byte
			EventuallyWithOffset(1, func() error {
				var err error
				cmd := exec.Command("kubectl", "kustomize", filepath.Join(projectDir,
					test.GetSonataFlowE2EPlatformServicesDirectory(), profile, clusterWideEphemeral))
				manifests, err = utils.Run(cmd)
				return err
			}, time.Minute, time.Second).Should(Succeed())

			cmd := exec.Command("kubectl", "create", "-n", targetNamespace, "-f", "-")
			cmd.Stdin = bytes.NewBuffer(manifests)
			_, err := utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())

			By("Wait for SonatatFlowPlatform CR in " + targetNamespace + " to be ready")
			// wait for platform to be ready
			EventuallyWithOffset(1, func() error {
				cmd = exec.Command("kubectl", "wait", "sfplatform", "-n", targetNamespace, "sonataflow-platform", "--for", "condition=Succeed", "--timeout=5s")
				_, err = utils.Run(cmd)
				return err
			}, 20*time.Minute, 5).Should(Succeed())
			EventuallyWithOffset(1, func() []byte {
				cmd = exec.Command("kubectl", "get", "sfplatform", "-n", targetNamespace, "sonataflow-platform", "-o", "jsonpath='{.status.clusterPlatformRef}'")
				returnedValue, _ := utils.Run(cmd)
				println(string(returnedValue))
				return returnedValue
			}, 20*time.Minute, 5).Should(Equal([]byte("''")))

			By("Evaluate status of SonataFlowClusterPlatform CR")
			cmd = exec.Command("kubectl", "patch", "SonataFlowClusterPlatform", "cluster", "--type", "merge", "-p", `{"spec": {"platformRef": {"namespace": "`+targetNamespace+`"}}}`)
			_, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
			EventuallyWithOffset(1, func() error {
				cmd = exec.Command("kubectl", "wait", "SonataFlowClusterPlatform", "cluster", "--for", "condition=Succeed", "--timeout=5s")
				_, err = utils.Run(cmd)
				return err
			}, 20*time.Minute, 5).Should(Succeed())

			EventuallyWithOffset(1, func() error {
				var err error
				cmd := exec.Command("kubectl", "kustomize", filepath.Join(projectDir,
					testcaseDir, profile, persistenceType))
				manifests, err = utils.Run(cmd)
				return err
			}, time.Minute, time.Second).Should(Succeed())

			cmd = exec.Command("kubectl", "create", "-n", targetNamespace2, "-f", "-")
			cmd.Stdin = bytes.NewBuffer(manifests)
			_, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())

			By("Wait for SonatatFlowPlatform CR in " + targetNamespace2 + " to be ready")
			// wait for platform to be ready
			EventuallyWithOffset(1, func() error {
				cmd = exec.Command("kubectl", "wait", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "--for", "condition=Succeed", "--timeout=5s")
				_, err = utils.Run(cmd)
				return err
			}, 20*time.Minute, 5).Should(Succeed())
			EventuallyWithOffset(1, func() []byte {
				cmd = exec.Command("kubectl", "get", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "-o", "jsonpath='{.status.clusterPlatformRef}'")
				returnedValue, _ := utils.Run(cmd)
				return returnedValue
			}, 20*time.Minute, 5).Should(Not(Equal([]byte("''"))))
			EventuallyWithOffset(1, func() []byte {
				cmd = exec.Command("kubectl", "get", "sfplatform", "-n", targetNamespace2, "sonataflow-platform", "-o", "jsonpath='{.status.clusterPlatformRef.services}'")
				returnedValue, _ := utils.Run(cmd)
				return returnedValue
			}, 20*time.Minute, 5).Should(Equal([]byte("''")))

			cmd = exec.Command("kubectl", "delete", "SonataFlowClusterPlatform", "cluster", "--wait")
			_, err = utils.Run(cmd)
			Expect(err).NotTo(HaveOccurred())
		},
			Entry("with only Data Index configured", test.GetSonataFlowE2EPlatformServicesDirectory(), metadata.PreviewProfile.String(), ephemeralDataIndex),
			Entry("with only Job Service configured", test.GetSonataFlowE2EPlatformServicesDirectory(), metadata.PreviewProfile.String(), ephemeralJobService),
		)
	})
})
