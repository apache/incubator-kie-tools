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
	"encoding/json"
	"fmt"
	"net/url"
	"os/exec"
	"regexp"
	"strconv"
	"strings"
	"time"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/test/utils"

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

const (
	ephemeral            = "ephemeral"
	postgreSQL           = "postgreSQL"
	clusterWideEphemeral = "cluster-wide-ephemeral"
	ephemeralDataIndex   = "ephemeral-data-index"
	ephemeralJobService  = "ephemeral-job-service"
)

const randomIntRange = 16384 //Set to large number to avoid cluster namespace name collisions

var (
	upStatus string = "UP"
)

func kubectlApplyFileOnCluster(file, namespace string) error {
	cmd := exec.Command("kubectl", "apply", "-f", file, "-n", namespace)
	_, err := utils.Run(cmd)
	return err
}

func kubectlDeleteFileOnCluster(file, namespace string) error {
	cmd := exec.Command("kubectl", "delete", "-f", file, "-n", namespace)
	_, err := utils.Run(cmd)
	return err
}

func kubectlCreateNamespace(namespace string) error {
	cmd := exec.Command("kubectl", "create", "namespace", namespace)
	_, err := utils.Run(cmd)
	return err
}

func kubectlNamespaceExists(namespace string) (bool, error) {
	cmd := exec.Command("kubectl", "get", "namespace", "-o", fmt.Sprintf(`jsonpath={.items[?(@.metadata.name=="%s")].metadata.name}`, namespace))
	output, err := utils.Run(cmd)
	if err != nil {
		return false, err
	}
	return len(output) > 0, nil
}

func kubectlDeleteNamespace(namespace string) error {
	cmd := exec.Command("kubectl", "delete", "namespace", namespace)
	_, err := utils.Run(cmd)
	return err
}

func kubectlPatchSonataFlowImageAndRollout(namespace, workflowName, image string) error {
	cmd := exec.Command("kubectl", "patch", "sonataflow", workflowName,
		"--type", "json", "-n", namespace,
		"-p", fmt.Sprintf(`[{"op": "replace", "path": "/spec/podTemplate/container/image", "value": "%s"}, {"op": "replace", "path": "/spec/podTemplate/replicas", "value": 1}]`, image))
	_, err := utils.Run(cmd)
	return err
}

func kubectlPatchSonataFlowScaleDown(namespace, workflowName string) error {
	cmd := exec.Command("kubectl", "patch", "sonataflow", workflowName,
		"--type", "json", "-n", namespace,
		"-p", `[{"op": "replace", "path": "/spec/podTemplate/replicas", "value": 0}]`)
	_, err := utils.Run(cmd)
	return err
}

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

func verifyWorkflowIsInRunningState(workflowName string, ns string) bool {
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

func verifyWorkflowIsAddressable(workflowName string, targetNamespace string) bool {
	cmd := exec.Command("kubectl", "get", "workflow", workflowName, "-n", targetNamespace, "-ojsonpath={.status.address.url}")
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

func verifySchemaMigration(data, name string) bool {
	matched1, err := regexp.MatchString(fmt.Sprintf("Successfully applied \\d migrations to schema \"%s\"", name), data)
	if err != nil {
		GinkgoWriter.Println(fmt.Errorf("string match error:%v", err))
		return false
	}
	matched2, err := regexp.MatchString("Successfully validated \\d (migration|migrations)", data)
	if err != nil {
		GinkgoWriter.Println(fmt.Errorf("string match error:%v", err))
		return false
	}
	GinkgoWriter.Println(fmt.Sprintf("verifying schemaMigration, logs=%v", data))
	return (matched1 && strings.Contains(data, fmt.Sprintf("Creating schema \"%s\"", name)) &&
		strings.Contains(data, fmt.Sprintf("Migrating schema \"%s\" to version", name))) ||
		(matched2 && strings.Contains(data, fmt.Sprintf("Current version of schema \"%s\"", name)) &&
			strings.Contains(data, fmt.Sprintf("Schema \"%s\" is up to date. No migration necessary", name))) ||
		(strings.Contains(data, fmt.Sprintf("Creating schema \"%s\"", name)) &&
			strings.Contains(data, fmt.Sprintf("Current version of schema \"%s\"", name)) &&
			strings.Contains(data, fmt.Sprintf("Schema \"%s\" is up to date. No migration necessary", name)))
}

func waitForPodRestartCompletion(label, ns string) {
	EventuallyWithOffset(1, func() bool {
		cmd := exec.Command("kubectl", "get", "pod", "-n", ns, "-l", label, "-o", "jsonpath={.items[*].metadata.name}")
		out, err := utils.Run(cmd)
		if err != nil {
			GinkgoWriter.Println(fmt.Errorf("failed to get pods: %v", err))
			return false
		}
		podNames := strings.Fields(string(out))
		if len(podNames) == 0 {
			GinkgoWriter.Println("no pods found")
			return false // pods haven't created yet
		} else if len(podNames) > 1 {
			GinkgoWriter.Println("multiple pods found")
			return false // multiple pods found, wait for other pods to terminate
		}
		return true
	}, 1*time.Minute, 5).Should(BeTrue())
}

func verifyTrigger(triggers []operatorapi.SonataFlowPlatformTriggerRef, namePrefix, path, ns, broker string) error {
	GinkgoWriter.Println("Triggers from platform status:", triggers)
	for _, ref := range triggers {
		if strings.HasPrefix(ref.Name, namePrefix) && ref.Namespace == ns {
			return verifyTriggerData(ref.Name, ns, path, broker)
		}
	}
	return fmt.Errorf("failed to find trigger to verify with prefix: %v, namespace: %v", namePrefix, ns)
}

func verifyTriggerData(name, ns, path, broker string) error {
	cmd := exec.Command("kubectl", "get", "trigger", name, "-n", ns, "-ojsonpath={.spec.broker} {.status.subscriberUri} {.status.conditions[?(@.type=='Ready')].status}")
	out, err := utils.Run(cmd)
	if err != nil {
		return err
	}
	data := strings.Fields(string(out))
	if len(data) == 3 && broker == data[0] && strings.HasSuffix(data[1], path) && data[2] == "True" {
		return nil
	}
	return fmt.Errorf("failed to verify trigger %v, data=%s", name, string(out))
}

func verifySinkBinding(name, ns, broker string) error {
	cmd := exec.Command("kubectl", "get", "sinkbinding", name, "-n", ns, "-ojsonpath={.status.sinkUri} {.status.conditions[?(@.type=='Ready')].status}")
	out, err := utils.Run(cmd)
	if err != nil {
		return err
	}
	data := strings.Fields(string(out))
	if len(data) == 2 && strings.HasSuffix(data[0], broker) && data[1] == "True" {
		return nil
	}
	return fmt.Errorf("failed to verify sinkbinding %v, data=%s", name, string(out))
}
