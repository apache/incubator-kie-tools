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

package record

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/core/client"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/tools/record/util"
	ref "k8s.io/client-go/tools/reference"
	"os"
	"strings"
)

// EventRecorder knows how to record events on behalf of an EventSource.
type EventRecorder interface {

	// The resulting event will be created in the same namespace as the reference object.
	Event(cli *client.Client, object runtime.Object, eventType, reason, message string)

	// Eventf is just like Event, but with Sprintf for the message field.
	Eventf(cli *client.Client, object runtime.Object, eventType, reason, messageFmt string, args ...interface{})
}

// NewRecorder returns an EventRecorder that can be used to send events with the given event source.
func NewRecorder(scheme *runtime.Scheme, source corev1.EventSource) EventRecorder {
	return &recorderImpl{scheme, source}
}

type recorderImpl struct {
	scheme *runtime.Scheme
	source corev1.EventSource
}

func (recorder *recorderImpl) Event(cli *client.Client, object runtime.Object, eventType, reason, message string) {
	recorder.generateEvent(cli, object, eventType, reason, message)
}

func (recorder *recorderImpl) Eventf(cli *client.Client, object runtime.Object, eventType, reason, messageFmt string, args ...interface{}) {
	recorder.Event(cli, object, eventType, reason, fmt.Sprintf(messageFmt, args...))
}

func (recorder *recorderImpl) generateEvent(cli *client.Client, object runtime.Object, eventType, reason, message string) {
	objectRef, err := ref.GetReference(recorder.scheme, object)
	if err != nil {
		log.Error(err, "Could not construct object reference. Event will not be reported", "object", object, "error", err, "event type", eventType, "reason", reason, "message", message)
		return
	}

	if !util.ValidateEventType(eventType) {
		log.Error(fmt.Errorf("Unsupported event type: '%v'", eventType), "")
		return
	}

	event := makeEvent(objectRef, eventType, reason, message)
	event.Source = recorder.source

	if err := kubernetes.ResourceC(cli).Create(event); err != nil {
		log.Error(err, "Error occurs while creating event", "event", event)
	}
}

func makeEvent(ref *corev1.ObjectReference, eventType, reason, message string) *corev1.Event {
	t := metav1.Now()
	namespace := ref.Namespace
	if namespace == "" {
		namespace = metav1.NamespaceDefault
	}
	return &corev1.Event{
		ObjectMeta: metav1.ObjectMeta{
			Name:      fmt.Sprintf("%v.%x", ref.Name, t.UnixNano()),
			Namespace: namespace,
		},
		InvolvedObject: *ref,
		Reason:         reason,
		Message:        message,
		FirstTimestamp: t,
		LastTimestamp:  t,
		Count:          1,
		Type:           eventType,
	}
}

// GetHostName returns OS's hostname
func GetHostName() string {
	hostName, err := os.Hostname()
	if err != nil {
		return ""
	}

	// Trim whitespaces first to avoid getting an empty hostname
	// For linux, the hostname is read from file /proc/sys/kernel/hostname directly
	hostName = strings.TrimSpace(hostName)
	if len(hostName) == 0 {
		return ""
	}
	return strings.ToLower(hostName)
}
