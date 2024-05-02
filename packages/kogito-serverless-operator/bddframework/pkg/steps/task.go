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

package steps

import (
	"fmt"

	"github.com/cucumber/godog"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
)

func registerTaskSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Service "([^"]*)" contains (\d+) (?:task|tasks) of process with name "([^"]*)" and task name "([^"]*)"$`, data.serviceContainsTasksOfProcessWithNameAndTaskName)
	ctx.Step(`^Service "([^"]*)" contains (\d+) (?:task|tasks) of process with name "([^"]*)" and task name "([^"]*)" for user "([^"]*)"$`, data.serviceContainsTasksOfProcessWithNameAndTaskNameForUser)
	ctx.Step(`^Service "([^"]*)" contains (\d+) (?:task|tasks) of process with name "([^"]*)" and task name "([^"]*)" for user "([^"]*)" within (\d+) minutes$`, data.serviceContainsTasksOfProcessWithNameAndTaskNameForUserWithinMinutes)
	ctx.Step(`^Complete "([^"]*)" task on service "([^"]*)" and process with name "([^"]*)" with body:$`, data.completeTaskOnServiceAndProcessWithName)
	ctx.Step(`^Complete "([^"]*)" task on service "([^"]*)" and process with name "([^"]*)" by user "([^"]*)" with body:$`, data.completeTaskOnServiceAndProcessWithNameAndUser)
}

func (data *Data) serviceContainsTasksOfProcessWithNameAndTaskName(serviceName string, numberOfTasks int, processName, taskName string) error {
	return data.serviceContainsTasksOfProcessWithNameAndTaskNameForUser(serviceName, numberOfTasks, processName, taskName, "")
}

func (data *Data) serviceContainsTasksOfProcessWithNameAndTaskNameForUserWithinMinutes(serviceName string, numberOfTasks int, processName, taskName, user string, timeoutInMin int) error {
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("Process %s has %d %s task(s)", processName, numberOfTasks, taskName), timeoutInMin,
		func() (bool, error) {
			err := data.serviceContainsTasksOfProcessWithNameAndTaskNameForUser(serviceName, numberOfTasks, processName, taskName, user)
			if err != nil {
				return false, err
			}

			return true, nil
		})
}

func (data *Data) serviceContainsTasksOfProcessWithNameAndTaskNameForUser(serviceName string, numberOfTasks int, processName, taskName, user string) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	processInstanceID, err := getProcessInstanceID(data.Namespace, uri, processName)
	if err != nil {
		return err
	}

	var foundTasks []framework.Task
	if user != "" {
		foundTasks, err = framework.GetTasksByUser(data.Namespace, uri, processName, processInstanceID, user)
	} else {
		foundTasks, err = framework.GetTasks(data.Namespace, uri, processName, processInstanceID)
	}
	if err != nil {
		return err
	}
	for _, foundTask := range foundTasks {
		if taskName != foundTask.Name {
			return fmt.Errorf("found unexpected task name %s", foundTask.Name)
		}
	}
	if len(foundTasks) < numberOfTasks {
		return fmt.Errorf("not enough tasks found, expected at least %d tasks, but found just %d tasks", numberOfTasks, len(foundTasks))
	}

	return nil
}

func (data *Data) completeTaskOnServiceAndProcessWithName(taskName, serviceName, processName string, body *godog.DocString) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	processInstanceID, err := getProcessInstanceID(data.Namespace, uri, processName)
	if err != nil {
		return err
	}

	foundTasks, err := framework.GetTasks(data.Namespace, uri, processName, processInstanceID)
	if err != nil {
		return err
	}

	taskID, exists := getTaskID(foundTasks, taskName)
	if !exists {
		return fmt.Errorf("task with name %s not found", taskName)
	}

	bodyContent := data.ResolveWithScenarioContext(body.Content)
	err = framework.CompleteTask(data.Namespace, uri, processName, processInstanceID, taskName, taskID, body.MediaType, bodyContent)
	if err != nil {
		return err
	}
	return nil
}

func (data *Data) completeTaskOnServiceAndProcessWithNameAndUser(taskName, serviceName, processName, user string, body *godog.DocString) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	processInstanceID, err := getProcessInstanceID(data.Namespace, uri, processName)
	if err != nil {
		return err
	}

	foundTasks, err := framework.GetTasksByUser(data.Namespace, uri, processName, processInstanceID, user)
	if err != nil {
		return err
	}

	taskID, exists := getTaskID(foundTasks, taskName)
	if !exists {
		return fmt.Errorf("task with name %s not found", taskName)
	}

	bodyContent := data.ResolveWithScenarioContext(body.Content)
	err = framework.CompleteTaskByUser(data.Namespace, uri, processName, processInstanceID, taskName, taskID, user, body.MediaType, bodyContent)
	if err != nil {
		return err
	}
	return nil
}

// getProcessInstanceID returns the process instance ID by process name
func getProcessInstanceID(namespace, routeURI, processName string) (string, error) {
	foundProcessInstances, err := framework.GetProcessInstances(namespace, routeURI, processName)
	if err != nil {
		return "", err
	}
	if len(foundProcessInstances) == 0 {
		return "", fmt.Errorf("no process instance found, expected one instance")
	}
	if len(foundProcessInstances) > 1 {
		return "", fmt.Errorf("too many process instances found, expected one instance, but found %d instances", len(foundProcessInstances))
	}

	return foundProcessInstances[0]["id"].(string), nil
}

// getTaskID Returns task id of the task with name searchedTaskName and flag is the task with such name exists in tasks map
func getTaskID(tasks []framework.Task, searchedTaskName string) (string, bool) {
	for _, task := range tasks {
		if task.Name == searchedTaskName {
			return task.ID, true
		}
	}
	return "", false
}
