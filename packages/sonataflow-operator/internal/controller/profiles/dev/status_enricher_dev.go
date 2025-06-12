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

package dev

import (
	"context"
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"

	openshiftv1 "github.com/openshift/api/route/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"knative.dev/pkg/apis"
	duckv1 "knative.dev/pkg/apis/duck/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
)

func statusEnricher(ctx context.Context, c client.Client, workflow *operatorapi.SonataFlow) (client.Object, error) {
	//If the workflow Status hasn't got a NodePort Endpoint, we are ensuring it will be set
	// If we aren't on OpenShift we will enrich the status with 2 info:
	// - Address the service can be reached
	// - Node port used
	service := &v1.Service{}

	err := c.Get(ctx, types.NamespacedName{Namespace: workflow.Namespace, Name: workflow.Name}, service)
	if err != nil {
		return nil, err
	}

	//If the service has got a Port that is a nodePort we have to use it to create the workflow's NodePort Endpoint
	if service.Spec.Ports != nil && len(service.Spec.Ports) > 0 {
		if port := findNodePortFromPorts(service.Spec.Ports); port > 0 {
			labels := workflowproj.GetDefaultLabels(workflow)

			podList := &v1.PodList{}
			opts := []client.ListOption{
				client.InNamespace(workflow.Namespace),
				client.MatchingLabels{metadata.KubernetesLabelName: labels[metadata.KubernetesLabelName]},
			}
			err := c.List(ctx, podList, opts...)
			if err != nil {
				return nil, err
			}
			var ipaddr string
			for _, p := range podList.Items {
				ipaddr = p.Status.HostIP
				break
			}

			url, err := apis.ParseURL("http://" + ipaddr + ":" + fmt.Sprint(port) + "/" + workflow.Name)
			if err != nil {
				return nil, err
			}
			workflow.Status.Endpoint = url
		}

		address, err := kubernetes.RetrieveServiceURL(service)
		if err != nil {
			return nil, err
		}
		workflow.Status.Address = duckv1.Addressable{
			URL: address,
		}
	}

	return workflow, nil
}

// findNodePortFromPorts returns the first Port in an array of ServicePort
func findNodePortFromPorts(ports []v1.ServicePort) int {
	if len(ports) > 0 {
		for _, p := range ports {
			if p.NodePort != 0 {
				return int(p.NodePort)
			}
		}
	}
	//If we are not able to find a NodePort let's return the zero value
	return 0
}

func statusEnricherOpenShift(ctx context.Context, client client.Client, workflow *operatorapi.SonataFlow) (client.Object, error) {
	// On OpenShift we need to retrieve the Route to have the URL the service is available to
	route := &openshiftv1.Route{}
	err := client.Get(ctx, types.NamespacedName{Namespace: workflow.Namespace, Name: workflow.Name}, route)
	if err != nil {
		return nil, err
	}
	var url *apis.URL
	if route.Spec.TLS != nil {
		url = apis.HTTPS(route.Spec.Host)
	} else {
		url = apis.HTTP(route.Spec.Host)
	}
	url.Path = workflow.Name

	workflow.Status.Endpoint = url

	if err != nil {
		return nil, err
	}
	workflow.Status.Address = duckv1.Addressable{
		URL: url,
	}
	return workflow, nil
}
