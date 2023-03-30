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

package kogitoinfra

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/infrastructure/kafka/v1beta2"
	"sigs.k8s.io/controller-runtime/pkg/builder"
	"sort"
	"strings"
	"time"

	"k8s.io/apimachinery/pkg/types"
)

// AppendKafkaWatchedObjects ...
func AppendKafkaWatchedObjects(b *builder.Builder) *builder.Builder {
	return b
}

func initKafkaInfraReconciler(context infraContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "kafka")
	return &kafkaInfraReconciler{
		infraContext: context,
	}
}

// kafkaInfraReconciler implementation of KogitoInfraResource
type kafkaInfraReconciler struct {
	infraContext
}

// Reconcile reconcile Kogito infra object
func (k *kafkaInfraReconciler) Reconcile() (resultErr error) {
	var kafkaInstance *v1beta2.Kafka

	// Verify kafka
	kafkaHandler := infrastructure.NewKafkaHandler(k.Context)
	if !kafkaHandler.IsStrimziAvailable() {
		return errorForResourceAPINotFound(k.instance.GetSpec().GetResource().GetAPIVersion())
	}

	if len(k.instance.GetSpec().GetResource().GetName()) > 0 {
		k.Log.Debug("Custom kafka instance reference is provided")
		namespace := k.instance.GetSpec().GetResource().GetNamespace()
		if len(namespace) == 0 {
			namespace = k.instance.GetNamespace()
			k.Log.Debug("Namespace is not provided for custom resource, taking", "Namespace", namespace)
		}
		if kafkaInstance, resultErr = kafkaHandler.FetchKafkaInstance(types.NamespacedName{Name: k.instance.GetSpec().GetResource().GetName(), Namespace: namespace}); resultErr != nil {
			return resultErr
		} else if kafkaInstance == nil {
			return errorForResourceNotFound("Kafka", k.instance.GetSpec().GetResource().GetName(), namespace)
		}
	} else {
		return errorForResourceConfigError(k.instance, "No Kafka resource name given")
	}

	kafkaStatus := k.getLatestKafkaCondition(kafkaInstance)
	if kafkaStatus == nil || kafkaStatus.Type != v1beta2.KafkaConditionTypeReady {
		return errorForResourceNotReadyError(fmt.Errorf("kafka instance %s not ready yet. Waiting for Condition status Ready", kafkaInstance.Name))
	}

	if resultErr = k.updateKafkaRuntimePropsInStatus(kafkaInstance, api.QuarkusRuntimeType); resultErr != nil {
		return resultErr
	}
	return k.updateKafkaRuntimePropsInStatus(kafkaInstance, api.SpringBootRuntimeType)
}

func (k *kafkaInfraReconciler) getLatestKafkaCondition(kafka *v1beta2.Kafka) *v1beta2.KafkaCondition {
	if len(kafka.Status.Conditions) == 0 {
		return nil
	}
	sort.Slice(kafka.Status.Conditions, func(i, j int) bool {
		t1, parsed := k.mustParseKafkaTransition(kafka.Status.Conditions[i].LastTransitionTime)
		if !parsed {
			return false
		}
		t2, parsed := k.mustParseKafkaTransition(kafka.Status.Conditions[j].LastTransitionTime)
		if !parsed {
			return false
		}
		return t1.Before(*t2)
	})
	return &kafka.Status.Conditions[len(kafka.Status.Conditions)-1]
}

func (k *kafkaInfraReconciler) mustParseKafkaTransition(transitionTime string) (*time.Time, bool) {
	// TODO: open an issue on Strimzi to handle this! (this is the UTC being set to GMT+0)
	zoneIndex := strings.LastIndex(transitionTime, "+")
	if zoneIndex > -1 {
		transitionTime = string([]rune(transitionTime)[0:zoneIndex])
	}
	if !strings.Contains(transitionTime, "Z") {
		transitionTime = transitionTime + "Z"
	}
	parsedTime, err := time.Parse(v1beta2.KafkaLastTransitionTimeLayout, transitionTime)
	if err != nil {
		k.Log.Error(err, "Impossible to parse", "Kafka time condition", transitionTime)
		return nil, false
	}
	return &parsedTime, true
}

func (k *kafkaInfraReconciler) updateKafkaRuntimePropsInStatus(kafkaInstance *v1beta2.Kafka, runtime api.RuntimeType) error {
	k.Log.Debug("going to Update Kafka runtime properties in kogito infra instance status", "runtime", runtime)
	kafkaConfigReconciler := newKafkaConfigReconciler(k.infraContext, kafkaInstance, runtime)
	return kafkaConfigReconciler.Reconcile()
}
