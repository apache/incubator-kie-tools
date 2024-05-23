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
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// KafkaTopicSpec defines the desired state of KafkaTopic
type KafkaTopicSpec struct {
	Partitions int32  `json:"partitions,omitempty"`
	Replicas   int32  `json:"replicas,omitempty"`
	TopicName  string `json:"topicName,omitempty"`
}

// KafkaTopicStatus defines the observed state of KafkaTopic
type KafkaTopicStatus struct {
}

// KafkaTopic is the Schema for the kafkatopics API
// +kubebuilder:object:root=true
type KafkaTopic struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KafkaTopicSpec   `json:"spec,omitempty"`
	Status KafkaTopicStatus `json:"status,omitempty"`
}

// KafkaTopicList contains a list of KafkaTopic
// +kubebuilder:object:root=true
type KafkaTopicList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []KafkaTopic `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KafkaTopic{}, &KafkaTopicList{})
}
