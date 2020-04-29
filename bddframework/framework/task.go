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

package framework

import (
	"fmt"
	"strings"
)

// GetTasks retrieves tasks of specific process instance
func GetTasks(namespace, routeURI, processName, processInstanceID string) (foundTasks map[string]string, err error) {
	tasksEndpointPath := getTasksEndpointPath(processName, processInstanceID)
	err = ExecuteHTTPRequestWithUnmarshalledResponse(namespace, "GET", routeURI, tasksEndpointPath, "", "", &foundTasks)
	return
}

// GetTasksByUser retrieves tasks of specific process instance and user
func GetTasksByUser(namespace, routeURI, processName, processInstanceID, user string) (foundTasks map[string]string, err error) {
	tasksEndpointPath := getTasksEndpointPath(processName, processInstanceID) + "?user=" + user
	err = ExecuteHTTPRequestWithUnmarshalledResponse(namespace, "GET", routeURI, tasksEndpointPath, "", "", &foundTasks)
	return
}

// CompleteTask completes task
func CompleteTask(namespace, routeURI, processName, processInstanceID, taskName, taskID, bodyFormat, bodyContent string) (err error) {
	taskIDEndpointPath := getTaskIDEndpointPath(processName, processInstanceID, taskName, taskID)
	return completeTask(namespace, routeURI, taskIDEndpointPath, bodyFormat, bodyContent)
}

// CompleteTaskByUser completes task by user
func CompleteTaskByUser(namespace, routeURI, processName, processInstanceID, taskName, taskID, user, bodyFormat, bodyContent string) (err error) {
	taskIDEndpointPath := getTaskIDEndpointPath(processName, processInstanceID, taskName, taskID) + "?user=" + user
	return completeTask(namespace, routeURI, taskIDEndpointPath, bodyFormat, bodyContent)
}

func completeTask(namespace, routeURI, taskEndpointPath, bodyFormat, bodyContent string) (err error) {
	_, err = ExecuteHTTPRequest(namespace, "POST", routeURI, taskEndpointPath, bodyFormat, bodyContent)
	return
}

func getTasksEndpointPath(processName, processInstanceID string) string {
	return fmt.Sprintf("%s/%s/tasks", processName, processInstanceID)
}

func getTaskIDEndpointPath(processName, processInstanceID, taskName, taskID string) string {
	taskName = strings.ReplaceAll(taskName, " ", "_")
	return fmt.Sprintf("%s/%s/%s/%s", processName, processInstanceID, taskName, taskID)
}
