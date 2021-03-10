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
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	"sigs.k8s.io/controller-runtime/pkg/builder"
	"sort"
	"strings"
	"time"

	"github.com/kiegroup/kogito-cloud-operator/core/framework"
	kafkabetav1 "github.com/kiegroup/kogito-cloud-operator/core/infrastructure/kafka/v1beta1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	enableEventsEnvKey = "ENABLE_EVENTS"

	// springKafkaBootstrapAppProp spring boot application property for setting kafka server
	springKafkaBootstrapAppProp = "spring.kafka.bootstrap-servers"
	// QuarkusKafkaBootstrapAppProp quarkus application property for setting kafka server
	QuarkusKafkaBootstrapAppProp = "kafka.bootstrap.servers"
)

// AppendKafkaWatchedObjects ...
func AppendKafkaWatchedObjects(b *builder.Builder) *builder.Builder {
	return b
}

func initkafkaInfraReconciler(context infraContext) *kafkaInfraReconciler {
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
func (k *kafkaInfraReconciler) Reconcile() (requeue bool, resultErr error) {
	var kafkaInstance *kafkabetav1.Kafka

	// Verify kafka
	kafkaHandler := infrastructure.NewKafkaHandler(k.Context)
	if !kafkaHandler.IsStrimziAvailable() {
		return false, errorForResourceAPINotFound(k.instance.GetSpec().GetResource().GetAPIVersion())
	}

	if len(k.instance.GetSpec().GetResource().GetName()) > 0 {
		k.Log.Debug("Custom kafka instance reference is provided")
		namespace := k.instance.GetSpec().GetResource().GetNamespace()
		if len(namespace) == 0 {
			namespace = k.instance.GetNamespace()
			k.Log.Debug("Namespace is not provided for custom resource, taking", "Namespace", namespace)
		}
		if kafkaInstance, resultErr = kafkaHandler.FetchKafkaInstance(types.NamespacedName{Name: k.instance.GetSpec().GetResource().GetName(), Namespace: namespace}); resultErr != nil {
			return false, resultErr
		} else if kafkaInstance == nil {
			return false,
				errorForResourceNotFound("Kafka", k.instance.GetSpec().GetResource().GetName(), namespace)
		}
	} else {
		return false, errorForResourceConfigError(k.instance, "No Kafka resource name given")
	}

	kafkaStatus := k.getLatestKafkaCondition(kafkaInstance)
	if kafkaStatus == nil || kafkaStatus.Type != kafkabetav1.KafkaConditionTypeReady {
		return false, errorForResourceNotReadyError(fmt.Errorf("kafka instance %s not ready yet. Waiting for Condition status Ready", kafkaInstance.Name))
	}
	if resultErr = k.updateKafkaRuntimePropsInStatus(kafkaInstance, api.QuarkusRuntimeType); resultErr != nil {
		return true, resultErr
	}
	if resultErr = k.updateKafkaRuntimePropsInStatus(kafkaInstance, api.SpringBootRuntimeType); resultErr != nil {
		return true, resultErr
	}
	return false, nil
}

func (k *kafkaInfraReconciler) getLatestKafkaCondition(kafka *kafkabetav1.Kafka) *kafkabetav1.KafkaCondition {
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
	parsedTime, err := time.Parse(kafkabetav1.KafkaLastTransitionTimeLayout, transitionTime)
	if err != nil {
		k.Log.Error(err, "Impossible to parse", "Kafka time condition", transitionTime)
		return nil, false
	}
	return &parsedTime, true
}

func (k *kafkaInfraReconciler) updateKafkaRuntimePropsInStatus(kafkaInstance *kafkabetav1.Kafka, runtime api.RuntimeType) error {
	k.Log.Debug("going to Update Kafka runtime properties in kogito infra instance status", "runtime", runtime)
	runtimeProps, err := k.getKafkaRuntimeProps(kafkaInstance, runtime)
	if err != nil {
		return errorForResourceNotReadyError(err)
	}
	setRuntimeProperties(k.instance, runtime, runtimeProps)
	k.Log.Debug("Following Kafka runtime properties are set in infra status:", "runtime", runtime, "properties", runtimeProps)
	return nil
}

func (k *kafkaInfraReconciler) getKafkaEnvVars(kafkaInstance *kafkabetav1.Kafka) ([]corev1.EnvVar, error) {
	kafkaHandler := infrastructure.NewKafkaHandler(k.Context)
	kafkaURI, err := kafkaHandler.ResolveKafkaServerURI(kafkaInstance)
	if err != nil {
		return nil, err
	}
	var envProps []corev1.EnvVar
	if len(kafkaURI) > 0 {
		envProps = append(envProps, framework.CreateEnvVar(enableEventsEnvKey, "true"))
	} else {
		envProps = append(envProps, framework.CreateEnvVar(enableEventsEnvKey, "false"))
	}
	return envProps, nil
}

func (k *kafkaInfraReconciler) getKafkaRuntimeAppProps(kafkaInstance *kafkabetav1.Kafka, runtime api.RuntimeType) (map[string]string, error) {
	kafkaHandler := infrastructure.NewKafkaHandler(k.Context)
	kafkaURI, err := kafkaHandler.ResolveKafkaServerURI(kafkaInstance)
	if err != nil {
		return nil, err
	}
	appProps := map[string]string{}
	if len(kafkaURI) > 0 {
		if runtime == api.QuarkusRuntimeType {
			appProps[QuarkusKafkaBootstrapAppProp] = kafkaURI
		} else if runtime == api.SpringBootRuntimeType {
			appProps[springKafkaBootstrapAppProp] = kafkaURI
		}
	}
	return appProps, nil
}

func (k *kafkaInfraReconciler) getKafkaRuntimeProps(kafkaInstance *kafkabetav1.Kafka, runtime api.RuntimeType) (api.RuntimePropertiesInterface, error) {
	runtimeProps := v1beta1.RuntimeProperties{}
	appProps, err := k.getKafkaRuntimeAppProps(kafkaInstance, runtime)
	if err != nil {
		return runtimeProps, err
	}
	runtimeProps.AppProps = appProps

	envVars, err := k.getKafkaEnvVars(kafkaInstance)
	if err != nil {
		return runtimeProps, err
	}
	runtimeProps.Env = envVars

	return runtimeProps, nil
}
