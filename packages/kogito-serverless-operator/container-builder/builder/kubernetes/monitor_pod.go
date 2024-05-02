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
	"encoding/json"
	"os"
	"time"

	"github.com/pkg/errors"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes/scheme"
	"k8s.io/client-go/tools/remotecommand"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/api"
)

const timeoutAnnotation = "sonataflow.org/timeout"

func newMonitorPodAction() Action {
	return &monitorPodAction{}
}

type monitorPodAction struct {
	baseAction
}

// Name returns a common name of the action.
func (action *monitorPodAction) Name() string {
	return "monitor-pod"
}

// CanHandle tells whether this action can handle the build.
func (action *monitorPodAction) CanHandle(build *api.ContainerBuild) bool {
	return build.Status.Phase == api.ContainerBuildPhasePending || build.Status.Phase == api.ContainerBuildPhaseRunning
}

func (action *monitorPodAction) Handle(ctx context.Context, build *api.ContainerBuild) (*api.ContainerBuild, error) {
	pod, err := getBuilderPod(ctx, action.client, build)
	if err != nil {
		return nil, err
	}

	if pod == nil {
		switch build.Status.Phase {

		case api.ContainerBuildPhasePending:
			if pod, err = newBuildPod(ctx, action.client, build); err != nil {
				return nil, err
			}
			// TODO: every object we create, must pass to a listener for our client code. For example, an operator would like to add their labels/owner refs

			if err = action.client.Create(ctx, pod); err != nil {
				return nil, errors.Wrap(err, "cannot create build pod")
			}

		case api.ContainerBuildPhaseRunning:
			// Emulate context cancellation
			build.Status.Phase = api.ContainerBuildPhaseInterrupted
			build.Status.Error = "Pod deleted"
			return build, nil
		}
	}

	switch pod.Status.Phase {

	case corev1.PodPending, corev1.PodRunning:
		// Pod remains in pending phase when init containers execute
		if action.isPodScheduled(pod) {
			build.Status.Phase = api.ContainerBuildPhaseRunning
		}
		if time.Since(build.Status.StartedAt.Time) > build.Spec.Timeout.Duration {
			// Patch the Pod with an annotation, to identify termination signal
			// has been sent because the ContainerBuild has timed out
			if err = action.addTimeoutAnnotation(ctx, pod, metav1.Now()); err != nil {
				return nil, err
			}
		}

	case corev1.PodSucceeded:
		build.Status.Phase = api.ContainerBuildPhaseSucceeded
		// Remove the annotation in case the ContainerBuild succeeded, between
		// the timeout deadline and the termination signal.
		if err = action.removeTimeoutAnnotation(ctx, pod); err != nil {
			return nil, err
		}
		finishedAt := action.getTerminatedTime(pod)
		duration := finishedAt.Sub(build.Status.StartedAt.Time)
		build.Status.Duration = duration.String()

		for _, task := range build.Spec.Tasks {
			if t := task.Kaniko; t != nil {
				build.Status.RepositoryImageTag = t.GetRepositoryImageTag()
				break
			}
		}

	case corev1.PodFailed:
		phase := api.ContainerBuildPhaseFailed
		message := "Pod failed"
		if terminationMessage := action.getTerminationMessage(pod); terminationMessage != "" {
			message = terminationMessage
		}
		if pod.DeletionTimestamp != nil {
			phase = api.ContainerBuildPhaseInterrupted
			message = "Pod deleted"
		} else if _, ok := pod.GetAnnotations()[timeoutAnnotation]; ok {
			message = "ContainerBuild timeout"
		}
		// Do not override errored build
		if build.Status.Phase == api.ContainerBuildPhaseError {
			phase = api.ContainerBuildPhaseError
		}
		build.Status.Phase = phase
		build.Status.Error = message
		finishedAt := action.getTerminatedTime(pod)
		duration := finishedAt.Sub(build.Status.StartedAt.Time)
		build.Status.Duration = duration.String()
	}

	return build, nil
}

func (action *monitorPodAction) sigterm(pod *corev1.Pod) error {
	var containers []corev1.ContainerStatus
	containers = append(containers, pod.Status.InitContainerStatuses...)
	containers = append(containers, pod.Status.ContainerStatuses...)

	for _, container := range containers {
		if container.State.Running == nil {
			continue
		}

		r := action.client.CoreV1().RESTClient().Post().
			Resource("pods").
			Namespace(pod.Namespace).
			Name(pod.Name).
			SubResource("exec").
			Param("container", container.Name)

		r.VersionedParams(&corev1.PodExecOptions{
			Container: container.Name,
			Command:   []string{"kill", "-SIGTERM", "1"},
			Stdout:    true,
			Stderr:    true,
			TTY:       false,
		}, scheme.ParameterCodec)

		exec, err := remotecommand.NewSPDYExecutor(action.client.GetConfig(), "POST", r.URL())
		if err != nil {
			return err
		}

		err = exec.Stream(remotecommand.StreamOptions{
			Stdout: os.Stdout,
			Stderr: os.Stderr,
			Tty:    false,
		})
		if err != nil {
			return err
		}
	}

	return nil
}

func (action *monitorPodAction) isPodScheduled(pod *corev1.Pod) bool {
	for _, condition := range pod.Status.Conditions {
		if condition.Type == corev1.PodScheduled && condition.Status == corev1.ConditionTrue {
			return true
		}
	}
	return false
}

func (action *monitorPodAction) addTimeoutAnnotation(ctx context.Context, pod *corev1.Pod, time metav1.Time) error {
	if _, ok := pod.GetAnnotations()[timeoutAnnotation]; ok {
		return nil
	}
	return action.patchPod(ctx, pod, func(p *corev1.Pod) {
		if p.GetAnnotations() != nil {
			p.GetAnnotations()[timeoutAnnotation] = time.String()
		} else {
			p.SetAnnotations(map[string]string{
				timeoutAnnotation: time.String(),
			})
		}
	})
}

func (action *monitorPodAction) removeTimeoutAnnotation(ctx context.Context, pod *corev1.Pod) error {
	if _, ok := pod.GetAnnotations()[timeoutAnnotation]; !ok {
		return nil
	}
	return action.patchPod(ctx, pod, func(p *corev1.Pod) {
		delete(p.GetAnnotations(), timeoutAnnotation)
	})
}

func (action *monitorPodAction) patchPod(ctx context.Context, pod *corev1.Pod, mutate func(*corev1.Pod)) error {
	target := pod.DeepCopy()
	mutate(target)
	if err := action.client.Patch(ctx, target, ctrl.MergeFrom(pod)); err != nil {
		return err
	}
	*pod = *target
	return nil
}

func (action *monitorPodAction) getTerminatedTime(pod *corev1.Pod) metav1.Time {
	var finishedAt metav1.Time

	var containers []corev1.ContainerStatus
	containers = append(containers, pod.Status.InitContainerStatuses...)
	containers = append(containers, pod.Status.ContainerStatuses...)

	for _, container := range containers {
		if container.State.Terminated == nil {
			// The container has not run
			continue
		}
		if t := container.State.Terminated.FinishedAt; finishedAt.IsZero() || t.After(finishedAt.Time) {
			finishedAt = t
		}
	}

	return finishedAt
}

func (action *monitorPodAction) getTerminationMessage(pod *corev1.Pod) string {
	var terminationMessages []terminationMessage

	var containers []corev1.ContainerStatus
	containers = append(containers, pod.Status.InitContainerStatuses...)
	containers = append(containers, pod.Status.ContainerStatuses...)

	for _, container := range containers {
		if t := container.State.Terminated; t != nil && t.ExitCode != 0 && t.Message != "" {
			terminationMessages = append(terminationMessages, terminationMessage{
				Container: container.Name,
				Message:   t.Message,
			})
		}
	}

	switch len(terminationMessages) {
	case 0:
		return ""
	case 1:
		return terminationMessages[0].Message
	default:
		message, err := json.Marshal(terminationMessages)
		if err != nil {
			return ""
		}
		return string(message)
	}
}

type terminationMessage struct {
	Container string `json:"container,omitempty"`
	Message   string `json:"message,omitempty"`
}
