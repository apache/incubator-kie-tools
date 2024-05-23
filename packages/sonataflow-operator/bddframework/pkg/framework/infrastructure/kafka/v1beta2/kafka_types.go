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

package v1beta2

import (
	"time"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// KafkaSpec defines the desired state of Kafka
type KafkaSpec struct {
	Kafka          KafkaClusterSpec     `json:"kafka,omitempty"`
	Zookeeper      ZookeeperClusterSpec `json:"zookeeper,omitempty"`
	EntityOperator EntityOperatorSpec   `json:"entityOperator,omitempty"`
}

// EntityOperatorSpec ...
type EntityOperatorSpec struct {
	TopicOperator EntityTopicOperatorSpec `json:"topicOperator,omitempty"`
	UserOperator  EntityUserOperatorSpec  `json:"userOperator,omitempty"`
}

// EntityTopicOperatorSpec ...
type EntityTopicOperatorSpec struct {
}

// EntityUserOperatorSpec ...
type EntityUserOperatorSpec struct {
}

// KafkaMap a feasible way to implement a map with interface values to match the Java counterpart: Map<String, Object>
type KafkaMap map[string]interface{}

// DeepCopy implements a custom deepcopy function since map[string]interface{} it's not available
func (kafkaMap *KafkaMap) DeepCopy() *KafkaMap {
	o := &KafkaMap{}
	*o = *kafkaMap
	return o
}

// KafkaClusterSpec defines the desired state of Kafka Cluster
type KafkaClusterSpec struct {
	Replicas   int32                  `json:"replicas,omitempty"`
	Listeners  []GenericKafkaListener `json:"listeners,omitempty"`
	Storage    KafkaStorage           `json:"storage,omitempty"`
	Config     KafkaMap               `json:"config,omitempty"`
	JvmOptions KafkaMap               `json:"jvmOptions,omitempty"`
}

// GenericKafkaListener ...
type GenericKafkaListener struct {
	Name         string `json:"name,omitempty"`
	Port         int    `json:"port,omitempty"`
	ListenerType string `json:"type,omitempty"`
	TLS          bool   `json:"tls"`
}

// ZookeeperClusterSpec Representation of a Strimzi-managed ZooKeeper "cluster".
type ZookeeperClusterSpec struct {
	Replicas int32        `json:"replicas,omitempty"`
	Storage  KafkaStorage `json:"storage,omitempty"`
}

// KafkaStorage The type of storage used by Kafka brokers
type KafkaStorage struct {
	StorageType KafkaStorageType `json:"type,omitempty"`
}

// KafkaStorageType defines the enum for Kafka storage
type KafkaStorageType string

const (
	// KafkaEphemeralStorage ...
	KafkaEphemeralStorage KafkaStorageType = "ephemeral"
)

// KafkaStatus defines the observed state of Kafka
type KafkaStatus struct {
	Listeners  []ListenerStatus `json:"listeners,omitempty"`
	Conditions []KafkaCondition `json:"conditions,omitempty"`
}

// KafkaCondition conditions for a Kafka resource
type KafkaCondition struct {
	Type   string                 `json:"type"`
	Status corev1.ConditionStatus `json:"status"`
	// This is defined by the Strimzi Java API as String
	LastTransitionTime string `json:"lastTransitionTime,omitempty"`
	// we don't have all possibilities for this type, leave as string and compare with known values
	Reason  string `json:"reason,omitempty"`
	Message string `json:"message,omitempty"`
}

const (
	// KafkaConditionTypeReady ...
	KafkaConditionTypeReady = "Ready"
	// KafkaLastTransitionTimeLayout the default date layout for KafkaCondition
	KafkaLastTransitionTimeLayout = time.RFC3339
)

// ListenerStatus defines a single listener
type ListenerStatus struct {
	Type      string            `json:"type,omitempty"`
	Addresses []ListenerAddress `json:"addresses,omitempty"`
}

// ListenerAddress defines a single address of particular listener
type ListenerAddress struct {
	Host string `json:"host,omitempty"`
	Port int32  `json:"port,omitempty"`
}

// Kafka is the Schema for the kafkas API
// +kubebuilder:object:root=true
type Kafka struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KafkaSpec   `json:"spec,omitempty"`
	Status KafkaStatus `json:"status,omitempty"`
}

// KafkaList contains a list of Kafka
// +kubebuilder:object:root=true
type KafkaList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []Kafka `json:"items"`
}

func init() {
	SchemeBuilder.Register(&Kafka{}, &KafkaList{})
}
