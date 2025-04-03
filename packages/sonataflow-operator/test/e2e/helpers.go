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

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test/utils"

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

func verifyKSinkInjection(label, ns string) bool {
	cmd := exec.Command("kubectl", "get", "pod", "-n", ns, "-l", label, "-o", "jsonpath={.items[*].metadata.name}")
	out, err := utils.Run(cmd)
	if err != nil {
		GinkgoWriter.Println(fmt.Errorf("failed to get pods: %v", err))
		return false
	}
	podNames := strings.Fields(string(out))
	if len(podNames) == 0 {
		GinkgoWriter.Println("no pods found to check K_SINK")
		return false // pods haven't created yet
	}
	GinkgoWriter.Println(fmt.Sprintf("pods found: %s", podNames))
	for _, pod := range podNames {
		cmd = exec.Command("kubectl", "get", "pod", pod, "-n", ns, "-o", "json")
		out, err := utils.Run(cmd)
		if err != nil {
			GinkgoWriter.Println(fmt.Errorf("failed to get pod: %v", err))
			return false
		}
		GinkgoWriter.Println(string(out))
		if !strings.Contains(string(out), "K_SINK") { // The pod does not have K_SINK injected
			GinkgoWriter.Println(fmt.Sprintf("Pod does not have K_SINK injected: %s", string(out)))
			return false
		}
	}
	return true
}

func waitForPodRestartCompletion(label, ns string) (podRunning string) {
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
		podRunning = podNames[0]
		return true
	}, 10*time.Minute, 5).Should(BeTrue())

	return
}

func verifyTrigger(triggers []operatorapi.SonataFlowPlatformTriggerRef, namePrefix, path, ns, broker string) error {
	GinkgoWriter.Println("Triggers from platform status:", triggers)
	for _, ref := range triggers {
		if strings.HasPrefix(ref.Name, namePrefix) && ref.Namespace == ns {
			EventuallyWithOffset(1, func() error {
				return verifyTriggerData(ref.Name, ns, path, broker)
			}, 2*time.Minute, 5).Should(Succeed())
			return nil
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
	return fmt.Errorf("failed to verify trigger %v with namespace %v, path %v, broker %s, and received data=%s", name, ns, path, broker, string(out))
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

func getWorkflowId(resp string) (string, error) {
	// First find the json data
	ind1 := strings.Index(resp, "{")
	ind2 := strings.LastIndex(resp, "}")
	data := resp[ind1 : ind2+1]
	// Retrieve the id from json data
	m := make(map[string]interface{})
	err := json.Unmarshal([]byte(data), &m)
	if err != nil {
		return "", err
	}
	if id, ok := m["id"].(string); ok {
		return id, nil
	}
	return "", fmt.Errorf("failed to find workflow id")
}

func getMetricValue(resp string) (string, error) {
	fmt.Println(resp)
	ind1 := strings.Index(resp, "{")
	ind2 := strings.LastIndex(resp, "}")
	data := resp[ind1 : ind2+1]

	// Retrieve the metric value from json data
	m := make(map[string]interface{})
	err := json.Unmarshal([]byte(data), &m)
	if err != nil {
		return "", err
	}
	result, ok := m["data"].(map[string]interface{})["result"]
	if !ok {
		return "", fmt.Errorf("no valid response data received")
	}
	metrics := result.([]interface{})
	if len(metrics) == 0 {
		return "", fmt.Errorf("no valid metric data retrieved")
	}
	metric := metrics[0]
	values := metric.(map[string]interface{})["value"]
	if val, ok := (values.([]interface{}))[1].(string); ok {
		return val, nil
	} else {
		return "", fmt.Errorf("failed to get metric value")
	}
}

func getPodNameAfterWorkflowInstCreation(name, ns string) (string, error) {
	labels := fmt.Sprintf("sonataflow.org/workflow-app=%s,sonataflow.org/workflow-namespace=%s", name, ns)
	cmd := exec.Command("kubectl", "get", "pod", "-n", ns, "-l", labels, "-o=jsonpath='{range .items[*]}{.metadata.name} {.status.conditions[?(@.type=='Ready')].status}{';'}{end}'")
	fmt.Println(cmd.String())
	out, err := utils.Run(cmd)
	if err != nil {
		return "", err
	}
	fmt.Println(string(out))
	data := strings.Split(string(out), ";")
	for _, line := range data {
		res := strings.Fields(line)
		if len(res) == 2 && strings.Contains(res[0], "-00002-deployment-") {
			if res[1] == "True" {
				return res[0], nil
			} else {
				return "", fmt.Errorf("pod %s is not ready=", res)
			}
		}
	}
	return "", fmt.Errorf("invalid data received: %s", string(out))
}

// extractJSONResponse utility function to extract the json portion of the output produced when we execute commands
// inside a pod via kubectl exec podname xxxx. On the basis of course that the given command execution produces a json.
// Below we show an example of the full output returned by the kubectl exec podname xxxx command execution, however we
// are only interested on the json part: {"data":{"ProcessDefinitions":[]}}
//
// % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
// Dload  Upload   Total   Spent    Left  Speed
//
// 0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0{"data":{"ProcessDefinitions":[]}}
// 100    85  100    34  100    51   8500  12750 --:--:-- --:--:-- --:--:-- 21250
func extractJSONResponse(terminalOutput string) string {
	ind1 := strings.Index(terminalOutput, "{")
	ind2 := strings.LastIndex(terminalOutput, "}")
	return terminalOutput[ind1 : ind2+1]
}

// verifyWorkflowDefinitionIsInStatus returns true if the workflow definition has the status == expectedStatus in the
// target data-index, false in any other case.
func verifyWorkflowDefinitionIsInStatus(podName string, containerName string, namespace, dataIndexServiceName string, workflowId string, expectedStatus string) bool {
	status, ok, err := getWorkflowDefinitionStatus(podName, containerName, namespace, dataIndexServiceName, workflowId)
	if err != nil {
		GinkgoWriter.Println(fmt.Errorf("failed to verify workflow definition status for workflow: %s : %v", workflowId, err))
	}
	return ok && status == expectedStatus
}

// getWorkflowDefinitionStatus returns the "status" of a workflow definition, true if the workflow definition was found
// for that workflowId, false if not found. Returned value is read as follows:
// true, ""            : the "status" was not set in the workflow definition metadata. (not yet populated)
// true, "available"   : workflow definition is available.
// true, "unavailable" : workflow definition is not available.
// false, ""           : workflow definition was not found.
func getWorkflowDefinitionStatus(podName string, containerName string, namespace string, dataIndexServiceName string, workflowId string) (string, bool, error) {

	// DI query example like executed in bash terminal: {"query" : "{ ProcessDefinitions ( where:{id: {equal: \"greet\"}} ) { metadata } }"  }
	// WF not found query result example: {"data":{"ProcessDefinitions":[]}}
	// WF found query result example: {"data":{"ProcessDefinitions":[{"metadata":{"status":"available","Variable":"workflowdata","Description":"YAML based greeting workflow"}}]}}

	query := fmt.Sprintf("{\"query\" : \"{ ProcessDefinitions (where:{id: {equal: \\\"%s\\\"}} ) { metadata } }\"  }", workflowId)
	curlCmd := fmt.Sprintf("curl -H 'Content-Type: application/json' -H 'Accept: application/json' -X POST --data '%s' http://%s/graphql", query, dataIndexServiceName)
	fmt.Printf("querying workflow definition metadata for workflowId: %s, curl: %s\n", workflowId, curlCmd)

	// execute with bash command to ensure the json response is not clipped from the terminal output
	cmd := exec.Command("kubectl", "exec", podName, "-c", containerName, "-n", namespace, "--", "/bin/bash", "-c", curlCmd)
	output, err := utils.Run(cmd)
	if err != nil {
		return "", false, fmt.Errorf("failed to execute query against data-index service: %s, from pod: %s, containter: %s, output: %s, error: %v", dataIndexServiceName, podName, containerName, output, err)
	}
	stringOutput := string(output)
	jsonOutput := extractJSONResponse(stringOutput)
	fmt.Printf("query result: %s\n", jsonOutput)

	queryResult := make(map[string]interface{})
	err = json.Unmarshal([]byte(jsonOutput), &queryResult)
	if err != nil {
		return "", false, fmt.Errorf("failed to parse data-index query result from query against data-index service: %s, from pod: %s, container: %s, queryResult: %s, error: %v", dataIndexServiceName, podName, containerName, queryResult, err)
	}
	rawData, ok := queryResult["data"]
	if !ok {
		// the "data" field must be present, if not, an error was produced in the DI, e.g., the query was formulated wrong.
		return "", false, fmt.Errorf("failed to execute data-index query against data-index service: %s, from pod: %s, container: %s. It looks like the query was formulated wrong, query: %s, queryResult: %s", dataIndexServiceName, podName, containerName, query, queryResult)
	}
	data := rawData.(map[string]interface{})
	definitions := data["ProcessDefinitions"].([]interface{})
	if len(definitions) == 0 {
		// workflow definition not found
		return "", false, nil
	}
	definition := definitions[0].(map[string]interface{})
	rawMetadata, ok := definition["metadata"]
	if !ok {
		return "", false, nil
	}
	metadata := rawMetadata.(map[string]interface{})
	available, ok := metadata["status"]
	if !ok {
		return "", false, nil
	}
	return available.(string), true, nil
}
