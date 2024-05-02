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

package kubernetes

import (
	"context"
	"io/ioutil"

	corev1 "k8s.io/api/core/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"
)

// PodInterface has functions that interacts with pod object in the Kubernetes cluster
type PodInterface interface {
	// Immediately return pod log
	GetLogs(namespace, podName, containerName string) (string, error)
	// Wait until pod is terminated and then return pod log
	GetLogsWithFollow(namespace, podName, containerName string) (string, error)
}

type pod struct {
	client *client.Client
}

func newPod(c *client.Client) PodInterface {
	return &pod{
		client: c,
	}
}

func (pod *pod) GetLogs(namespace, podName, containerName string) (string, error) {
	return pod.getLogs(namespace, podName, containerName, false)
}

func (pod *pod) GetLogsWithFollow(namespace, podName, containerName string) (string, error) {
	return pod.getLogs(namespace, podName, containerName, true)
}

func (pod *pod) getLogs(namespace, podName, containerName string, follow bool) (string, error) {
	log.Debug("About to fetch log of pod from cluster", "pod name", podName, "namespace", namespace, "follow", follow)
	podLogOpts := corev1.PodLogOptions{
		Follow:    follow,
		Container: containerName,
	}
	req := pod.client.KubernetesExtensionCli.CoreV1().Pods(namespace).GetLogs(podName, &podLogOpts)
	readCloser, err := req.Stream(context.TODO())
	if err != nil {
		return "", err
	}
	bytes, err := ioutil.ReadAll(readCloser)
	if err != nil {
		return "", err
	}
	return string(bytes), nil
}
