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
	"encoding/json"
	"fmt"
	"net/url"
	"os/exec"
	"strconv"
	"strings"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/test/utils"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/ginkgo/v2"

	//nolint:golint
	//nolint:revive
	. "github.com/onsi/gomega"
)

type health struct {
	Status string  `json:"status"`
	Checks []check `json:"checks"`
}

type check struct {
	Name   string            `json:"name"`
	Status string            `json:"status"`
	Data   map[string]string `json:"data"`
}

var (
	upStatus string = "UP"
)

func getHealthFromPod(name, namespace string) (*health, error) {
	// iterate over all containers to find the one that responds to the HTTP health endpoint
	Expect(name).NotTo(BeEmpty(), "pod name is empty")
	cmd := exec.Command("kubectl", "get", "pod", name, "-n", namespace, "-o", `jsonpath={.spec.containers[*].name}`)
	output, err := utils.Run(cmd)
	Expect(err).NotTo(HaveOccurred())
	var errs error
	for _, cname := range strings.Split(string(output), " ") {
		var h *health
		h, err = getHealthStatusInContainer(name, cname, namespace)
		if err == nil {
			return h, nil
		}
		errs = fmt.Errorf("%v; %w", err, errs)
	}
	return nil, errs
}

func verifyHealthStatusInPod(name string, namespace string) {
	// iterate over all containers to find the one that responds to the HTTP health endpoint
	Expect(name).NotTo(BeEmpty(), "pod name is empty")
	cmd := exec.Command("kubectl", "get", "pod", name, "-n", namespace, "-o", `jsonpath={.spec.containers[*].name}`)
	output, err := utils.Run(cmd)
	Expect(err).NotTo(HaveOccurred())
	var errs error
	for _, cname := range strings.Split(string(output), " ") {
		var h *health
		h, err = getHealthStatusInContainer(name, cname, namespace)
		if err == nil {
			Expect(h.Status).To(Equal(upStatus))
			return
		}

		if len(errs.Error()) > 0 {
			errs = fmt.Errorf("%v; %w", err, errs)
		} else {
			errs = err
		}
	}
	Expect(errs).NotTo(HaveOccurred(), fmt.Sprintf("No container was found that could respond to the health endpoint %v", errs))

}

func getHealthStatusInContainer(podName string, containerName string, ns string) (*health, error) {
	h := health{}
	cmd := exec.Command("kubectl", "exec", "-t", podName, "-n", ns, "-c", containerName, "--", "curl", "-s", "localhost:8080/q/health")
	output, err := utils.Run(cmd)
	Expect(err).NotTo(HaveOccurred())
	// On Apache CI Nodes, does not return valid JSON, hence we match first and last brackets by index and extract it
	stringOutput := string(output)
	startIndex := strings.Index(stringOutput, "{")
	endIndex := strings.LastIndex(stringOutput, "}")
	if startIndex == 0 {
		stringOutput = stringOutput[startIndex : endIndex+1]
	} else {
		stringOutput = stringOutput[startIndex-1 : endIndex+1]
	}
	fmt.Printf("Parsed following JSON object from health Endpoint response: %v\n", stringOutput)
	err = json.Unmarshal([]byte(stringOutput), &h)
	if err != nil {
		return nil, fmt.Errorf("failed to execute curl command against health endpoint in container %s:%v with output %s", containerName, err, output)
	}
	GinkgoWriter.Println(fmt.Sprintf("Health status:\n%s", string(output)))
	return &h, nil
}
func verifyWorkflowIsInRunningStateInNamespace(workflowName string, ns string) bool {
	cmd := exec.Command("kubectl", "get", "workflow", workflowName, "-n", ns, "-o", "jsonpath={.status.conditions[?(@.type=='Running')].status}")
	response, err := utils.Run(cmd)
	if err != nil {
		GinkgoWriter.Println(fmt.Errorf("failed to check if greeting workflow is running: %v", err))
		return false
	}
	GinkgoWriter.Println(fmt.Sprintf("Got response %s", response))

	if len(strings.TrimSpace(string(response))) == 0 {
		GinkgoWriter.Println(fmt.Errorf("empty response %v", err))
		return false
	}
	status, err := strconv.ParseBool(string(response))
	if err != nil {
		GinkgoWriter.Println(fmt.Errorf("failed to parse result %v", err))
		return false
	}
	return status
}

func verifyWorkflowIsInRunningState(workflowName string, targetNamespace string) bool {
	return verifyWorkflowIsInRunningStateInNamespace(workflowName, targetNamespace)
}

func verifyWorkflowIsAddressable(workflowName string, targetNamespace string) bool {
	cmd := exec.Command("kubectl", "get", "workflow", workflowName, "-n", targetNamespace, "-o", "jsonpath={.status.address.url}")
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
