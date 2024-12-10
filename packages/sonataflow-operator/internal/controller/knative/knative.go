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

package knative

import (
	"context"
	"fmt"
	"strings"

	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/rest"
	"knative.dev/eventing/pkg/apis/eventing"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	clienteventingv1 "knative.dev/eventing/pkg/client/clientset/versioned/typed/eventing/v1"
	"knative.dev/pkg/apis"
	duckv1 "knative.dev/pkg/apis/duck/v1"
	clientservingv1 "knative.dev/serving/pkg/client/clientset/versioned/typed/serving/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
	kubeutil "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
)

var servingClient clientservingv1.ServingV1Interface
var eventingClient clienteventingv1.EventingV1Interface

type Availability struct {
	Eventing bool
	Serving  bool
}

const (
	kSink                                    = "K_SINK"
	knativeBundleVolume                      = "kne-bundle-volume"
	kCeOverRides                             = "K_CE_OVERRIDES"
	knativeServingGroup                      = "serving.knative.dev"
	knativeEventingGroup                     = "eventing.knative.dev"
	knativeEventingAPIVersion                = "eventing.knative.dev/v1"
	knativeBrokerKind                        = "Broker"
	knativeSinkProvided                      = "SinkProvided"
	KafkaKnativeEventingDeliveryOrder        = "kafka.eventing.knative.dev/delivery.order"
	KafkaKnativeEventingDeliveryOrderOrdered = "ordered"
)

func GetKnativeServingClient(cfg *rest.Config) (clientservingv1.ServingV1Interface, error) {
	if servingClient == nil {
		if knServingClient, err := NewKnativeServingClient(cfg); err != nil {
			return nil, err
		} else {
			servingClient = knServingClient
		}
	}
	return servingClient, nil
}

func GetKnativeEventingClient(cfg *rest.Config) (clienteventingv1.EventingV1Interface, error) {
	if eventingClient == nil {
		if knEventingClient, err := NewKnativeEventingClient(cfg); err != nil {
			return nil, err
		} else {
			eventingClient = knEventingClient
		}
	}
	return eventingClient, nil
}

func NewKnativeServingClient(cfg *rest.Config) (*clientservingv1.ServingV1Client, error) {
	return clientservingv1.NewForConfig(cfg)
}

func NewKnativeEventingClient(cfg *rest.Config) (*clienteventingv1.EventingV1Client, error) {
	return clienteventingv1.NewForConfig(cfg)
}

func GetKnativeAvailability(cfg *rest.Config) (*Availability, error) {
	if cli, err := utils.GetDiscoveryClient(cfg); err != nil {
		return nil, err
	} else {
		apiList, err := cli.ServerGroups()
		if err != nil {
			return nil, err
		}
		result := new(Availability)
		for _, group := range apiList.Groups {
			if group.Name == knativeServingGroup {
				result.Serving = true
			}
			if group.Name == knativeEventingGroup {
				result.Eventing = true
			}
		}
		return result, nil
	}
}

// getRemotePlatform returns the remote platfrom referred by a SonataFlowClusterPlatform
func getRemotePlatform(pl *operatorapi.SonataFlowPlatform) (*operatorapi.SonataFlowPlatform, error) {
	if pl.Status.ClusterPlatformRef != nil {
		// Find the platform referred by the cluster platform
		platform := &operatorapi.SonataFlowPlatform{}
		if err := utils.GetClient().Get(context.TODO(), types.NamespacedName{Namespace: pl.Status.ClusterPlatformRef.PlatformRef.Namespace, Name: pl.Status.ClusterPlatformRef.PlatformRef.Name}, platform); err != nil {
			return nil, fmt.Errorf("error reading the platform referred by the cluster platform")
		}
		return platform, nil
	}
	return nil, nil
}

func getDestinationWithNamespace(dest *duckv1.Destination, namespace string) *duckv1.Destination {
	if dest != nil && dest.Ref != nil && len(dest.Ref.Namespace) == 0 {
		dest.Ref.Namespace = namespace
	}
	return dest
}

func ValidateBroker(name, namespace string) (*eventingv1.Broker, error) {
	broker := &eventingv1.Broker{}
	if err := utils.GetClient().Get(context.TODO(), types.NamespacedName{Name: name, Namespace: namespace}, broker); err != nil {
		if errors.IsNotFound(err) {
			return nil, fmt.Errorf("broker %s in namespace %s does not exist", name, namespace)
		}
		return nil, err
	}
	cond := broker.Status.GetCondition(apis.ConditionReady)
	if cond != nil && cond.Status == corev1.ConditionTrue {
		return broker, nil
	}
	return nil, fmt.Errorf("broker %s in namespace %s is not ready", name, namespace)
}

// GetBrokerClass returns the broker class for a Knative Eventing Broker.
func GetBrokerClass(broker *eventingv1.Broker) string {
	if broker.Annotations == nil {
		return ""
	}
	return broker.Annotations[eventing.BrokerClassKey]
}

// IsKafkaBroker returns true if the class for a Knative Eventing Broker corresponds to a Kafka broker.
func IsKafkaBroker(brokerClass string) bool {
	// currently available kafka broker classes are "Kafka", and "KafkaNamespaced", for safety ask for the substring "Kafka".
	return strings.Contains(brokerClass, "Kafka")
}

func GetWorkflowSink(workflow *operatorapi.SonataFlow, pl *operatorapi.SonataFlowPlatform) (*duckv1.Destination, error) {
	if workflow == nil {
		return nil, nil
	}
	if workflow.Spec.Sink != nil {
		return getDestinationWithNamespace(workflow.Spec.Sink, workflow.Namespace), nil
	}
	if pl != nil && pl.Spec.Eventing != nil && pl.Spec.Eventing.Broker != nil {
		// no sink defined in the workflow, use the platform broker
		return getDestinationWithNamespace(pl.Spec.Eventing.Broker, pl.Namespace), nil
	}
	// Find the remote platform referred by the cluster platform
	platform, err := getRemotePlatform(pl)
	if err != nil {
		return nil, err
	}
	if platform != nil && platform.Spec.Eventing != nil && platform.Spec.Eventing.Broker != nil {
		return getDestinationWithNamespace(platform.Spec.Eventing.Broker, platform.Namespace), nil
	}
	return nil, nil
}

func IsKnativeBroker(kRef *duckv1.KReference) bool {
	return kRef.APIVersion == knativeEventingAPIVersion && kRef.Kind == knativeBrokerKind
}

func SaveKnativeData(dest *corev1.PodSpec, source *corev1.PodSpec) {
	for _, volume := range source.Volumes {
		if volume.Name == knativeBundleVolume {
			kubeutil.AddOrReplaceVolume(dest, volume)
			break
		}
	}
	visitContainers(source, func(container *corev1.Container) {
		visitContainers(dest, func(destContainer *corev1.Container) {
			for _, mount := range container.VolumeMounts {
				if mount.Name == knativeBundleVolume {
					kubeutil.AddOrReplaceVolumeMount(destContainer, mount)
					break
				}
			}
			for _, env := range container.Env {
				if env.Name == kSink || env.Name == kCeOverRides {
					kubeutil.AddOrReplaceEnvVar(destContainer, env)
				}
			}
		})
	})
}

func moveKnativeVolumeToEnd(vols []corev1.Volume) {
	for i := 0; i < len(vols)-1; i++ {
		if vols[i].Name == knativeBundleVolume {
			vols[i], vols[i+1] = vols[i+1], vols[i]
		}
	}
}

func moveKnativeVolumeMountToEnd(mounts []corev1.VolumeMount) {
	for i := 0; i < len(mounts)-1; i++ {
		if mounts[i].Name == knativeBundleVolume {
			mounts[i], mounts[i+1] = mounts[i+1], mounts[i]
		}
	}
}

// Knative Sinkbinding injects K_SINK env, a volume and volume mount. The volume and volume mount
// must be in the end of the array to avoid repeadly restarting of the workflow pod
func RestoreKnativeVolumeAndVolumeMount(podSpec *corev1.PodSpec) {
	moveKnativeVolumeToEnd(podSpec.Volumes)
	visitContainers(podSpec, func(container *corev1.Container) {
		moveKnativeVolumeMountToEnd(container.VolumeMounts)
	})
}

// containerVisitor is called with each container
type containerVisitor func(container *corev1.Container)

// visitContainers invokes the visitor function for every container in the given pod template spec
func visitContainers(podSpec *corev1.PodSpec, visitor containerVisitor) {
	for i := range podSpec.InitContainers {
		visitor(&podSpec.InitContainers[i])
	}
	for i := range podSpec.Containers {
		visitor(&podSpec.Containers[i])
	}
	for i := range podSpec.EphemeralContainers {
		visitor((*corev1.Container)(&podSpec.EphemeralContainers[i].EphemeralContainerCommon))
	}
}

// if a trigger is changed and it has namespace different from the platform is changed, reconcile the parent SonataFlowPlatform in the cluster.
func MapTriggerToPlatformRequests(ctx context.Context, object client.Object) []reconcile.Request {
	if trigger, ok := object.(*eventingv1.Trigger); ok {
		nameFound := ""
		namespaceFound := ""
		for k, v := range trigger.GetLabels() {
			if k == workflowproj.LabelApp {
				nameFound = v
			} else if k == workflowproj.LabelAppNamespace {
				namespaceFound = v
			}
		}
		if len(nameFound) > 0 && len(namespaceFound) > 0 && namespaceFound != trigger.Namespace {
			return []reconcile.Request{reconcile.Request{NamespacedName: types.NamespacedName{Name: nameFound, Namespace: namespaceFound}}}
		}
	}
	return nil
}

// Does the sinkbinding completed K_SINK injection?
func CheckKSinkInjected(name, namespace string) (bool, error) {
	sb := &sourcesv1.SinkBinding{}
	if err := utils.GetClient().Get(context.TODO(), types.NamespacedName{Name: fmt.Sprintf("%s-sb", name), Namespace: namespace}, sb); err != nil {
		if errors.IsNotFound(err) {
			return false, nil // deployment hasn't been created yet
		}
		return false, err
	}
	cond := sb.Status.GetCondition(apis.ConditionType(knativeSinkProvided))
	if cond != nil && cond.Status == corev1.ConditionTrue {
		return true, nil
	}
	return false, nil // K_SINK has not been injected yet
}
