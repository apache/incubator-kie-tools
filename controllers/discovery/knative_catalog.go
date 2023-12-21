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

package discovery

import (
	"context"
	"fmt"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/knative"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
	"k8s.io/client-go/rest"
	"k8s.io/klog/v2"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	clienteventingv1 "knative.dev/eventing/pkg/client/clientset/versioned/typed/eventing/v1"
	clientservingv1 "knative.dev/serving/pkg/client/clientset/versioned/typed/serving/v1"
)

const (
	knServiceKind = "services"
	knBrokerKind  = "brokers"
)

type knServiceCatalog struct {
	dc *KnDiscoveryClient
}

type KnDiscoveryClient struct {
	ServingClient  clientservingv1.ServingV1Interface
	EventingClient clienteventingv1.EventingV1Interface
}

func newKnServiceCatalog(discoveryClient *KnDiscoveryClient) knServiceCatalog {
	return knServiceCatalog{
		dc: discoveryClient,
	}
}

func newKnServiceCatalogForConfig(cfg *rest.Config) knServiceCatalog {
	return knServiceCatalog{
		dc: newKnDiscoveryClientForConfig(cfg),
	}
}

// newKnDiscoveryClientForConfig returns a KnDiscoveryClient discovery client depending on the cluster status, if knative
// serving nor knative eventing are installed, or it was not possible to create that client, returns null.
func newKnDiscoveryClientForConfig(cfg *rest.Config) *KnDiscoveryClient {
	var servingClient clientservingv1.ServingV1Interface
	var eventingClient clienteventingv1.EventingV1Interface

	if avail, err := knative.GetKnativeAvailability(cfg); err != nil {
		klog.V(log.E).ErrorS(err, "Unable to determine if knative is installed in the cluster")
		return nil
	} else {
		if avail.Serving {
			if servingClient, err = knative.GetKnativeServingClient(cfg); err != nil {
				klog.V(log.E).ErrorS(err, "Unable to get the knative serving client")
				return nil
			}
		}
		if avail.Eventing {
			if eventingClient, err = knative.GetKnativeEventingClient(cfg); err != nil {
				klog.V(log.E).ErrorS(err, "Unable to get the knative eventing client")
				return nil
			}
		}
		if servingClient != nil || eventingClient != nil {
			return newKnDiscoveryClient(servingClient, eventingClient)
		}
	}
	return nil
}

func newKnDiscoveryClient(servingClient clientservingv1.ServingV1Interface, eventingClient clienteventingv1.EventingV1Interface) *KnDiscoveryClient {
	return &KnDiscoveryClient{
		ServingClient:  servingClient,
		EventingClient: eventingClient,
	}
}

func (c knServiceCatalog) Query(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if c.dc == nil {
		return "", fmt.Errorf("knative KnDiscoveryClient was not provided, maybe knative is not installed in current cluster")
	}
	switch uri.GVK.Kind {
	case knServiceKind:
		return c.resolveKnServiceQuery(ctx, uri)
	case knBrokerKind:
		return c.resolveKnBrokerQuery(ctx, uri)
	default:
		return "", fmt.Errorf("resolution of knative kind: %s is not implemented", uri.GVK.Kind)
	}
}

func (c knServiceCatalog) resolveKnServiceQuery(ctx context.Context, uri ResourceUri) (string, error) {
	if c.dc.ServingClient == nil {
		return "", fmt.Errorf("knative ServingClient was not provided, maybe the serving.knative.dev api is not installed in current cluster")
	}
	if service, err := c.dc.ServingClient.Services(uri.Namespace).Get(ctx, uri.Name, metav1.GetOptions{}); err != nil {
		return "", err
	} else {
		// knative objects discovery should rely on the addressable interface
		return service.Status.Address.URL.String(), nil
	}
}

func (c knServiceCatalog) resolveKnBrokerQuery(ctx context.Context, uri ResourceUri) (string, error) {
	if c.dc.EventingClient == nil {
		return "", fmt.Errorf("knative EventingClient was not provided, maybe the eventing.knative.dev api is not installed in current cluster")
	}
	if broker, err := c.dc.EventingClient.Brokers(uri.Namespace).Get(ctx, uri.Name, metav1.GetOptions{}); err != nil {
		return "", err
	} else {
		// knative objects discovery should rely on the addressable interface
		return broker.Status.Address.URL.String(), nil
	}
}
