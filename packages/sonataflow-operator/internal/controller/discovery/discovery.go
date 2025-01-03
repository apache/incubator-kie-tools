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

	"k8s.io/client-go/rest"

	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	KnativeScheme    = "knative"
	KubernetesScheme = "kubernetes"
	OpenshiftScheme  = "openshift"

	// PortQueryParam well known query param to select a particular target port, for example when a service is being
	// discovered and there are many ports to select.
	PortQueryParam = "port"

	// KubernetesDNSAddress use this output format with kubernetes services and pods to resolve to the corresponding
	// kubernetes DNS name. see: https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/
	KubernetesDNSAddress = "KubernetesDNSAddress"

	// KubernetesIPAddress default format, resolves objects addresses to the corresponding cluster IP address.
	KubernetesIPAddress = "KubernetesIPAddress"

	// kubernetes groups
	kubernetesServices     = "kubernetes:services.v1"
	kubernetesPods         = "kubernetes:pods.v1"
	kubernetesDeployments  = "kubernetes:deployments.v1.apps"
	kubernetesStatefulSets = "kubernetes:statefulsets.v1.apps"
	kubernetesIngresses    = "kubernetes:ingresses.v1.networking.k8s.io"

	// knative groups
	knativeServices = "knative:services.v1.serving.knative.dev"
	knativeBrokers  = "knative:brokers.v1.eventing.knative.dev"

	// openshift groups
	openshiftRoutes            = "openshift:routes.v1.route.openshift.io"
	openshiftDeploymentConfigs = "openshift:deploymentconfigs.v1.apps.openshift.io"
)

type ResourceUri struct {
	Scheme      string
	GVK         v1.GroupVersionKind
	Namespace   string
	Name        string
	QueryParams map[string]string
}

// ServiceCatalog is the entry point to resolve resource addresses given a ResourceUri.
type ServiceCatalog interface {
	// Query returns the address corresponding to the resource identified by the uri. In the case of services or pods,
	// the outputFormat can be used to determine the type of address to calculate.
	// If the outputFormat is KubernetesDNSAddress, the returned value for a service will be like this: http://my-service.my-namespace.svc:8080,
	// and the returned value for pod will be like this: http://10-244-1-135.my-namespace.pod.cluster.local:8080.
	// If the outputFormat is KubernetesIPAddress, the returned value for pods and services, and other resource types,
	// will be like this: http://10.245.1.132:8080
	Query(ctx context.Context, uri ResourceUri, outputFormat string) (string, error)
}

type sonataFlowServiceCatalog struct {
	kubernetesCatalog ServiceCatalog
	knativeCatalog    ServiceCatalog
	openshiftCatalog  ServiceCatalog
}

// NewServiceCatalog returns a new ServiceCatalog configured to resolve kubernetes, knative, and openshift resource addresses.
func NewServiceCatalog(cli client.Client, knDiscoveryClient *KnDiscoveryClient, openShiftDiscoveryClient *OpenShiftDiscoveryClient) ServiceCatalog {
	return &sonataFlowServiceCatalog{
		kubernetesCatalog: newK8SServiceCatalog(cli),
		knativeCatalog:    newKnServiceCatalog(knDiscoveryClient),
		openshiftCatalog:  newOpenShiftServiceCatalog(openShiftDiscoveryClient),
	}
}

func NewServiceCatalogForConfig(cli client.Client, cfg *rest.Config) ServiceCatalog {
	return &sonataFlowServiceCatalog{
		kubernetesCatalog: newK8SServiceCatalog(cli),
		knativeCatalog:    newKnServiceCatalogForConfig(cfg),
		openshiftCatalog:  newOpenShiftServiceCatalogForClientAndConfig(cli, cfg),
	}
}

func (c *sonataFlowServiceCatalog) Query(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	switch uri.Scheme {
	case KubernetesScheme:
		return c.kubernetesCatalog.Query(ctx, uri, outputFormat)
	case KnativeScheme:
		return c.knativeCatalog.Query(ctx, uri, outputFormat)
	case OpenshiftScheme:
		return c.openshiftCatalog.Query(ctx, uri, outputFormat)
	default:
		return "", fmt.Errorf("unknown scheme was provided for service discovery: %s", uri.Scheme)
	}
}

type ResourceUriBuilder struct {
	uri *ResourceUri
}

func NewResourceUriBuilder(scheme string) ResourceUriBuilder {
	return ResourceUriBuilder{
		uri: &ResourceUri{
			Scheme:      scheme,
			GVK:         v1.GroupVersionKind{},
			QueryParams: map[string]string{},
		},
	}
}

func (b ResourceUriBuilder) Kind(kind string) ResourceUriBuilder {
	b.uri.GVK.Kind = kind
	return b
}

func (b ResourceUriBuilder) Version(version string) ResourceUriBuilder {
	b.uri.GVK.Version = version
	return b
}

func (b ResourceUriBuilder) Group(group string) ResourceUriBuilder {
	b.uri.GVK.Group = group
	return b
}

func (b ResourceUriBuilder) Namespace(namespace string) ResourceUriBuilder {
	b.uri.Namespace = namespace
	return b
}

func (b ResourceUriBuilder) Name(name string) ResourceUriBuilder {
	b.uri.Name = name
	return b
}

func (b ResourceUriBuilder) WithPort(customPort string) ResourceUriBuilder {
	b.uri.SetPort(customPort)
	return b
}

func (b ResourceUriBuilder) WithQueryParam(param string, value string) ResourceUriBuilder {
	b.uri.AddQueryParam(param, value)
	return b
}

func (b ResourceUriBuilder) Build() *ResourceUri {
	return b.uri
}

func (r *ResourceUri) AddQueryParam(name string, value string) {
	if len(value) > 0 {
		r.QueryParams[name] = value
	}
}

func (r *ResourceUri) GetQueryParam(name string) string {
	if len(name) > 0 {
		return r.QueryParams[name]
	}
	return ""
}

func (r *ResourceUri) SetPort(value string) {
	r.AddQueryParam(PortQueryParam, value)
}

func (r *ResourceUri) GetPort() string {
	return r.GetQueryParam(PortQueryParam)
}

// GetCustomLabels returns all the query parameters that not considered well known query parameters, and thus, has no
// particular semantic during the discovery. These arbitrary parameters are normally considered as labels, and when
// present, and the service discovery must give a preference over a set of resources, they can be used to do a filtering.
// by labels.
func (r *ResourceUri) GetCustomLabels() map[string]string {
	customQueryParams := make(map[string]string)
	for k, v := range r.QueryParams {
		if !isWellKnownQueryParam(k) && len(v) > 0 {
			customQueryParams[k] = v
		}
	}
	return customQueryParams
}

func isWellKnownQueryParam(k string) bool {
	return k == PortQueryParam
}

func (r *ResourceUri) String() string {
	if r == nil {
		return ""
	}
	gvk := appendWithDelimiter("", r.GVK.Kind, ".")
	gvk = appendWithDelimiter(gvk, r.GVK.Version, ".")
	gvk = appendWithDelimiter(gvk, r.GVK.Group, ".")
	uri := r.Scheme + ":" + gvk
	uri = appendWithDelimiter(uri, r.Namespace, "/")
	uri = appendWithDelimiter(uri, r.Name, "/")

	return appendWithDelimiter(uri, buildLabelsString(r.QueryParams, "&"), "?")
}

func appendWithDelimiter(value string, toAppend string, delimiter string) string {
	if len(toAppend) > 0 {
		if len(value) > 0 {
			return fmt.Sprintf("%s%s%s", value, delimiter, toAppend)
		} else {
			return fmt.Sprintf("%s%s", value, toAppend)
		}
	}
	return value
}

func buildParam(name string, value string) string {
	return fmt.Sprintf("%s=%s", name, value)
}

func buildLabelsString(labels map[string]string, delimiter string) string {
	var labelsStr string
	for name, value := range labels {
		labelsStr = appendWithDelimiter(labelsStr, buildParam(name, value), delimiter)
	}
	return labelsStr
}
