// Copyright 2021 Red Hat, Inc. and/or its affiliates
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
	"reflect"

	api "github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/infrastructure/kafka/v1beta2"
	v12 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	enableEventsEnvKey = "ENABLE_EVENTS"
	// springKafkaBootstrapAppProp spring boot application property for setting kafka server
	springKafkaBootstrapAppProp = "spring.kafka.bootstrap-servers"
	// QuarkusKafkaBootstrapAppProp quarkus application property for setting kafka server
	QuarkusKafkaBootstrapAppProp = "kafka.bootstrap.servers"
	kafkaConfigMapName           = "kogito-kafka-%s-config"
)

type kafkaConfigReconciler struct {
	infraContext
	kafkaInstance    *v1beta2.Kafka
	runtime          api.RuntimeType
	configMapHandler infrastructure.ConfigMapHandler
	kafkaHandler     infrastructure.KafkaHandler
}

func newKafkaConfigReconciler(ctx infraContext, kafkaInstance *v1beta2.Kafka, runtime api.RuntimeType) Reconciler {
	return &kafkaConfigReconciler{
		infraContext:     ctx,
		kafkaInstance:    kafkaInstance,
		runtime:          runtime,
		configMapHandler: infrastructure.NewConfigMapHandler(ctx.Context),
		kafkaHandler:     infrastructure.NewKafkaHandler(ctx.Context),
	}
}

func (k *kafkaConfigReconciler) Reconcile() (err error) {

	// Create Required resource
	requestedResources, err := k.createRequiredResources()
	if err != nil {
		return
	}

	// Get Deployed resource
	deployedResources, err := k.getDeployedResources()
	if err != nil {
		return
	}

	// Process Delta
	if err = k.processDelta(requestedResources, deployedResources); err != nil {
		return err
	}

	k.instance.GetStatus().AddConfigMapEnvFromReferences(GetKafkaConfigMapName(k.runtime))
	return nil
}

func (k *kafkaConfigReconciler) createRequiredResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	appProps, err := k.getKafkaAppProps()
	if err != nil {
		return nil, err
	}
	configMap := k.createKafkaConfigMap(appProps)
	if err := framework.SetOwner(k.instance, k.Scheme, configMap); err != nil {
		return resources, err
	}
	resources[reflect.TypeOf(v12.ConfigMap{})] = []client.Object{configMap}
	return resources, nil
}

func (k *kafkaConfigReconciler) getDeployedResources() (map[reflect.Type][]client.Object, error) {
	resources := make(map[reflect.Type][]client.Object)
	// fetch owned image stream
	deployedConfigMap, err := k.configMapHandler.FetchConfigMap(types.NamespacedName{Name: GetKafkaConfigMapName(k.runtime), Namespace: k.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if deployedConfigMap != nil {
		resources[reflect.TypeOf(v12.ConfigMap{})] = []client.Object{deployedConfigMap}
	}
	return resources, nil
}

func (k *kafkaConfigReconciler) processDelta(requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (err error) {
	comparator := k.configMapHandler.GetComparator()
	deltaProcessor := infrastructure.NewDeltaProcessor(k.Context)
	_, err = deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return err
}

func (k *kafkaConfigReconciler) getKafkaAppProps() (map[string]string, error) {
	appProps := map[string]string{}
	kafkaURI, err := k.kafkaHandler.ResolveKafkaServerURI(k.kafkaInstance)
	if err != nil {
		return nil, err
	}
	if len(kafkaURI) > 0 {
		appProps[enableEventsEnvKey] = "true"
		if k.runtime == api.QuarkusRuntimeType {
			appProps[QuarkusKafkaBootstrapAppProp] = kafkaURI
		} else if k.runtime == api.SpringBootRuntimeType {
			appProps[springKafkaBootstrapAppProp] = kafkaURI
		}
	} else {
		appProps[enableEventsEnvKey] = "false"
	}
	return appProps, nil
}

func (k *kafkaConfigReconciler) createKafkaConfigMap(appProps map[string]string) *v12.ConfigMap {
	var data map[string]string
	if len(appProps) > 0 {
		data = appProps
	}
	configMap := &v12.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      GetKafkaConfigMapName(k.runtime),
			Namespace: k.instance.GetNamespace(),
			Labels: map[string]string{
				framework.LabelAppKey: k.instance.GetName(),
			},
		},
		Data: data,
	}
	return configMap
}

// GetKafkaConfigMapName ...
func GetKafkaConfigMapName(runtime api.RuntimeType) string {
	return fmt.Sprintf(kafkaConfigMapName, runtime)
}
