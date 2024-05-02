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

package infrastructure

import (
	"reflect"
	"testing"

	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/log"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/kafka/v1beta2"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/logger"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/operator"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/meta"
)

func Test_getKafkaInstanceWithName(t *testing.T) {
	ns := t.Name()

	kafka := &v1beta2.Kafka{
		TypeMeta: v1.TypeMeta{Kind: "Kafka", APIVersion: "kafka.strimzi.io/v1beta2"},
		ObjectMeta: v1.ObjectMeta{
			Name:      "kafka",
			Namespace: ns,
		},
		Spec: v1beta2.KafkaSpec{
			Kafka: v1beta2.KafkaClusterSpec{
				Replicas: 1,
				Listeners: []v1beta2.GenericKafkaListener{
					{
						Name:         "plain",
						Port:         9092,
						TLS:          false,
						ListenerType: "internal",
					},
					{
						Name:         "tls",
						Port:         9093,
						TLS:          true,
						ListenerType: "internal",
					},
				},
			},
		},
	}

	cli := NewFakeClientBuilder().AddK8sObjects(kafka).Build()

	type args struct {
		name      string
		namespace string
		client    *client.Client
	}
	tests := []struct {
		name    string
		args    args
		want    *v1beta2.Kafka
		wantErr bool
	}{
		{
			"KafkaInstanceExists",
			args{
				"kafka",
				ns,
				cli,
			},
			kafka,
			false,
		},
		{
			"KafkaInstanceNotExists",
			args{
				"kafka1",
				ns,
				cli,
			},
			nil,
			false,
		},
	}
	context := operator.Context{
		Client: cli,
		Log:    logger.Logger{Logger: log.Log.WithName("test")},
		Scheme: meta.GetRegisteredSchema(),
	}
	kafkaHandler := NewKafkaHandler(context)
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := kafkaHandler.FetchKafkaInstance(types.NamespacedName{Name: tt.args.name, Namespace: tt.args.namespace})
			if (err != nil) != tt.wantErr {
				t.Errorf("getKafkaInstanceWithName() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("getKafkaInstanceWithName() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func Test_resolveKafkaServerURI(t *testing.T) {
	type args struct {
		kafka *v1beta2.Kafka
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{
			"ResolveKafkaServerURI",
			args{
				&v1beta2.Kafka{
					Status: v1beta2.KafkaStatus{
						Listeners: []v1beta2.ListenerStatus{
							{
								Type: "tls",
								Addresses: []v1beta2.ListenerAddress{
									{
										Host: "kafka1",
										Port: 9093,
									},
								},
							},
							{
								Type: "plain",
								Addresses: []v1beta2.ListenerAddress{
									{
										Host: "kafka",
										Port: 9092,
									},
								},
							},
						},
					},
				},
			},
			"kafka:9092",
		},
	}
	cli := NewFakeClientBuilder().Build()
	context := operator.Context{
		Client: cli,
		Log:    logger.Logger{Logger: log.Log.WithName("test")},
		Scheme: meta.GetRegisteredSchema(),
	}
	kafkaHandler := NewKafkaHandler(context)
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got, _ := kafkaHandler.ResolveKafkaServerURI(tt.args.kafka); got != tt.want {
				t.Errorf("ResolveKafkaServerURI() = %v, want %v", got, tt.want)
			}
		})
	}
}
