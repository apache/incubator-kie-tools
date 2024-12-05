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
	"context"
	"fmt"
	"os/exec"
	"testing"
	"time"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test/utils"

	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"
)

// Run e2e tests using the Ginkgo runner.
func TestE2E(t *testing.T) {
	RegisterFailHandler(Fail)
	GinkgoWriter.Println("Starting SonataFlow Operator suite")
	RunSpecs(t, "SonataFlow Operator e2e suite")
}

// prebuiltWorkflows global accessor for the prebuilt workflows
var prebuiltWorkflows E2EWorkflows

const (
	// resourcesNamespace is the global namespace used to deploy e2e resources
	resourcesNamespace = "e2e-resources"
	// waitForBuildTimeout is the time we should wait for the workflows to be deployed
	waitForBuildTimeout = 15 * time.Minute

	flowCallbackPersistenceName = "callbackstatetimeouts-persistence"
	flowCallbackName            = "callbackstatetimeouts"
	flowGreetingsName           = "greetings"
)

type E2EWorkflowEntry struct {
	Name string
	Tag  string
}

func (e *E2EWorkflowEntry) String() string {
	return fmt.Sprintf("%s: %s", e.Name, e.Tag)
}

type E2EWorkflows struct {
	// CallBackPersistence is the prebuilt workflow to use on every use case that requires persistence
	CallBackPersistence E2EWorkflowEntry
	// CallBack is the prebuilt workflow to use on every use case that requires events or jobs services integration
	CallBack E2EWorkflowEntry
	// Greetings is the general usage image to use on any use case that requires a SonataFlow deployment
	Greetings E2EWorkflowEntry
}

func (e *E2EWorkflows) String() string {
	return fmt.Sprintf("%s \n %s \n %s", e.CallBack, e.CallBackPersistence, e.Greetings)
}

type DeployedWorkflow struct {
	YAMLFile string
	ImageTag string
}

var _ = BeforeSuite(func() {
	GinkgoWriter.Print("Checking if resources namespace is available\n")
	namespaceExists, err := kubectlNamespaceExists(resourcesNamespace)
	Expect(err).NotTo(HaveOccurred())

	workflows := make(map[string]*DeployedWorkflow, 2)
	workflows[flowCallbackName] = &DeployedWorkflow{YAMLFile: test.GetPathFromE2EDirectory("before-suite", "sonataflow.org_v1alpha08_sonataflow-callbackstatetimeouts.yaml")}
	workflows[flowCallbackPersistenceName] = &DeployedWorkflow{YAMLFile: test.GetPathFromE2EDirectory("before-suite", "sonataflow.org_v1alpha08_sonataflow-callbackstatetimeouts-persistence.yaml")}
	workflows[flowGreetingsName] = &DeployedWorkflow{YAMLFile: test.GetPathFromE2EDirectory("before-suite", "sonataflow.org_v1alpha08_sonataflow-greetings.yaml")}
	if !namespaceExists {
		GinkgoWriter.Println("Creating the resources namespace")
		err = kubectlCreateNamespace(resourcesNamespace)
		Expect(err).NotTo(HaveOccurred())

		GinkgoWriter.Println("Pre-built workflows within the cluster")
		err = deployWorkflowsAndWaitForBuild(workflows)
		Expect(err).NotTo(HaveOccurred())
	} else {
		GinkgoWriter.Println("Fetch pre-built workflows images in the cluster")
		err = fetchImageTagsBuiltWorkflows(workflows)
		if err != nil {
			GinkgoWriter.Println("Failed to fetch pre-built workflows images, try to build them")
			err = deployWorkflowsAndWaitForBuild(workflows)
		}
		Expect(err).NotTo(HaveOccurred())
	}

	// Convert to a simpler structure
	prebuiltWorkflows.CallBack.Tag = workflows[flowCallbackName].ImageTag
	prebuiltWorkflows.CallBack.Name = flowCallbackName
	prebuiltWorkflows.CallBackPersistence.Tag = workflows[flowCallbackPersistenceName].ImageTag
	prebuiltWorkflows.CallBackPersistence.Name = flowCallbackPersistenceName
	prebuiltWorkflows.Greetings.Tag = workflows[flowGreetingsName].ImageTag
	prebuiltWorkflows.Greetings.Name = flowGreetingsName
	GinkgoWriter.Println("Images are ready for upcoming tests")

	// Delete the workflows since we already have the images in the internal registry, all gucci
	for name := range workflows {
		err = kubectlPatchSonataFlowScaleDown(resourcesNamespace, name)
		Expect(err).NotTo(HaveOccurred())
	}
})

func deployWorkflows(workflows map[string]*DeployedWorkflow) error {
	for _, workflow := range workflows {
		if err := kubectlApplyFileOnCluster(workflow.YAMLFile, resourcesNamespace); err != nil {
			return err
		}
	}
	return nil
}

func fetchImageTagsBuiltWorkflows(workflows map[string]*DeployedWorkflow) error {
	ctx, cancel := context.WithTimeout(context.Background(), waitForBuildTimeout)
	defer cancel()

	statusChan := make(chan error, len(workflows))
	for name, workflow := range workflows {
		go func(w *DeployedWorkflow) {
			ticker := time.NewTicker(10 * time.Second)
			defer ticker.Stop()

			for {
				select {
				case <-ctx.Done():
					statusChan <- fmt.Errorf("timeout reached: workflow %s in namespace %s did not reach running state", name, resourcesNamespace)
					return
				case <-ticker.C:
					if len(workflow.ImageTag) == 0 {
						cmd := exec.Command("kubectl", "get", "sonataflowbuild", name, "-n", resourcesNamespace, "-o", "jsonpath={.status.imageTag}")
						response, err := utils.Run(cmd)
						if err != nil {
							GinkgoWriter.Println(fmt.Errorf("failed to check the workflow image tag: %v", err))
							statusChan <- err
						}
						if len(response) > 0 {
							GinkgoWriter.Printf("Got response: %s \n", response)
							workflow.ImageTag = string(response)
							statusChan <- nil
						}
					}
				}
			}
		}(workflow)
	}

	// Wait for all workflows to be in a running state or for errors to occur
	for i := 0; i < len(workflows); i++ {
		if err := <-statusChan; err != nil {
			return err
		}
	}

	return nil
}

func deployWorkflowsAndWaitForBuild(workflows map[string]*DeployedWorkflow) error {
	if err := deployWorkflows(workflows); err != nil {
		return err
	}
	if err := fetchImageTagsBuiltWorkflows(workflows); err != nil {
		return err
	}
	return nil
}
