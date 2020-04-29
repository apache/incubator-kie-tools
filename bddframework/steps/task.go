// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package steps

import (
	"fmt"

	"github.com/cucumber/godog"
	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

func registerTaskSteps(s *godog.Suite, data *Data) {
	s.Step(`^Service "([^"]*)" contains (\d+) (?:task|tasks) of process with name "([^"]*)" and task name "([^"]*)"$`, data.serviceContainsTasksOfProcessWithNameAndTaskName)
	s.Step(`^Complete "([^"]*)" task on service "([^"]*)" and process with name "([^"]*)" with body:$`, data.completeTaskOnServiceAndProcessWithName)
	s.Step(`^Complete "([^"]*)" task on service "([^"]*)" and process with name "([^"]*)" by user "([^"]*)" with body:$`, data.completeTaskOnServiceAndProcessWithNameAndUser)
}

func (data *Data) serviceContainsTasksOfProcessWithNameAndTaskName(serviceName string, numberOfTasks int, processName, taskName string) error {
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	processInstanceID, err := getProcessInstanceID(data.Namespace, routeURI, processName)
	if err != nil {
		return err
	}

	foundTasks, err := framework.GetTasks(data.Namespace, routeURI, processName, processInstanceID)
	if err != nil {
		return err
	}
	for _, foundTaskName := range foundTasks {
		if taskName != foundTaskName {
			return fmt.Errorf("found unexpected task name %s", foundTaskName)
		}
	}
	if len(foundTasks) < numberOfTasks {
		return fmt.Errorf("not enough tasks found, expected at least %d tasks, but found just %d tasks", numberOfTasks, len(foundTasks))
	}

	return nil
}

func (data *Data) completeTaskOnServiceAndProcessWithName(taskName, serviceName, processName string, body *messages.PickleStepArgument_PickleDocString) error {
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	processInstanceID, err := getProcessInstanceID(data.Namespace, routeURI, processName)
	if err != nil {
		return err
	}

	foundTasks, err := framework.GetTasks(data.Namespace, routeURI, processName, processInstanceID)
	if err != nil {
		return err
	}

	taskID, exists := getTaskID(foundTasks, taskName)
	if !exists {
		return fmt.Errorf("task with name %s not found", taskName)
	}

	bodyContent := data.ResolveWithScenarioContext(body.GetContent())
	err = framework.CompleteTask(data.Namespace, routeURI, processName, processInstanceID, taskName, taskID, body.GetMediaType(), bodyContent)
	if err != nil {
		return err
	}
	return nil
}

func (data *Data) completeTaskOnServiceAndProcessWithNameAndUser(taskName, serviceName, processName, user string, body *messages.PickleStepArgument_PickleDocString) error {
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	processInstanceID, err := getProcessInstanceID(data.Namespace, routeURI, processName)
	if err != nil {
		return err
	}

	foundTasks, err := framework.GetTasksByUser(data.Namespace, routeURI, processName, processInstanceID, user)
	if err != nil {
		return err
	}

	taskID, exists := getTaskID(foundTasks, taskName)
	if !exists {
		return fmt.Errorf("task with name %s not found", taskName)
	}

	bodyContent := data.ResolveWithScenarioContext(body.GetContent())
	err = framework.CompleteTaskByUser(data.Namespace, routeURI, processName, processInstanceID, taskName, taskID, user, body.GetMediaType(), bodyContent)
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
func getTaskID(tasks map[string]string, searchedTaskName string) (string, bool) {
	for taskID, taskName := range tasks {
		if taskName == searchedTaskName {
			return taskID, true
		}
	}
	return "", false
}
