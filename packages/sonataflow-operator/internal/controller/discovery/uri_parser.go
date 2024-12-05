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
	"fmt"
	"regexp"
	"strings"

	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	// valid namespace, name, or label name.
	dns1123LabelFmt string = "[a-z0-9]([-a-z0-9]*[a-z0-9])?"
	queryParamName         = "[a-zA-Z0-9][-a-zAz0-9]*"
	queryParamValue        = "[/a-zA-Z0-9][/-a-zAz0-9]*"

	namespaceAndNamePattern = "^/((" + dns1123LabelFmt + ")+)(/(" + dns1123LabelFmt + ")+)?"
	queryStringPattern      = "^(\\?((" + queryParamName + ")+\\=(" + queryParamValue + ")+)" +
		"(&(" + queryParamName + ")+\\=(" + queryParamValue + ")+)*)?$"

	kubernetesGroupsPattern = "^(" + kubernetesServices +
		"|" + kubernetesPods +
		"|" + kubernetesDeployments +
		"|" + kubernetesStatefulSets +
		"|" + kubernetesIngresses + ")"

	knativeGroupsPattern = "^(" + knativeServices + "|" + knativeBrokers + ")"

	knativeSimplifiedServicePatten = "knative:" + "(" + dns1123LabelFmt + ")" + "(/(" + dns1123LabelFmt + ")+)?"

	openshiftGroupsPattern = "^(" + openshiftDeploymentConfigs +
		"|" + openshiftRoutes + ")"
)

var kubernetesGroupsExpr = regexp.MustCompile(kubernetesGroupsPattern)
var knativeGroupsExpr = regexp.MustCompile(knativeGroupsPattern)
var knativeSimplifiedServiceExpr = regexp.MustCompile(knativeSimplifiedServicePatten)
var openshiftGroupsExpr = regexp.MustCompile(openshiftGroupsPattern)
var namespaceAndNameExpr = regexp.MustCompile(namespaceAndNamePattern)
var queryStringExpr = regexp.MustCompile(queryStringPattern)

func ParseUri(uri string) (*ResourceUri, error) {
	if split := kubernetesGroupsExpr.Split(uri, -1); len(split) == 2 {
		return parseKubernetesUri(uri, kubernetesGroupsExpr.FindString(uri), split[1])
	} else if split := knativeGroupsExpr.Split(uri, -1); len(split) == 2 {
		return parseKnativeUri(uri, knativeGroupsExpr.FindString(uri), split[1])
	} else if knativeSimplifiedServiceExpr.MatchString(uri) {
		return parseKnativeSimplifiedServiceUri(uri)
	} else if split := openshiftGroupsExpr.Split(uri, -1); len(split) == 2 {
		return parseOpenshiftUri(uri, openshiftGroupsExpr.FindString(uri), split[1])
	}
	return nil, fmt.Errorf("invalid uri: %s, not correspond to any of the available schemes format: %s, %s, %s", uri, KubernetesScheme, KnativeScheme, OpenshiftScheme)
}

func parseKubernetesUri(uri string, schemaAndGroup string, after string) (*ResourceUri, error) {
	if namespace, name, gvk, queryParams, err := parseNamespaceNameGVKAndQueryParams(uri, schemaAndGroup, after); err != nil {
		return nil, err
	} else {
		return &ResourceUri{
			Scheme:      KubernetesScheme,
			GVK:         *gvk,
			Namespace:   namespace,
			Name:        name,
			QueryParams: queryParams,
		}, nil
	}
}

func parseNamespaceNameGVKAndQueryParams(uri string, schemaAndGroup string, after string) (namespace string, name string, gvk *v1.GroupVersionKind, queryParams map[string]string, err error) {
	if split := namespaceAndNameExpr.Split(after, -1); len(split) == 2 {
		namespaceAndName := namespaceAndNameExpr.FindString(after)
		namespaceAndNameSplit := strings.Split(namespaceAndName, "/")
		if len(namespaceAndNameSplit) == 3 {
			namespace = namespaceAndNameSplit[1]
			name = namespaceAndNameSplit[2]
		} else {
			name = namespaceAndNameSplit[1]
		}
		var queryParams map[string]string
		var err error
		if queryParams, err = parseQueryParams(uri, split[1]); err != nil {
			return "", "", nil, queryParams, err
		}
		if gvk, err = parseGVK(schemaAndGroup); err != nil {
			return "", "", nil, queryParams, err
		} else {
			return namespace, name, gvk, queryParams, nil
		}
	} else {
		return "", "", nil, queryParams, fmt.Errorf("invalid %s service uri: %s, provided namespace, name, or query parameters %s not correspond "+
			"to the expected formats: /my-namespace/my-service?label-name=label-value&another-label=another-value", schemaAndGroup, uri, after)
	}
}
func parseQueryParams(uri string, queryParams string) (map[string]string, error) {
	result := make(map[string]string)
	if len(queryParams) > 0 {
		if !queryStringExpr.MatchString(queryParams) {
			return nil, fmt.Errorf("invalid uri: %s, provided query string: %s not correspond to the expeced format: ?label-name=label-value&another-label=another-value", uri, queryParams)
		} else {
			queryParamsTerms := strings.Split(queryParams[1:], "&")
			for _, term := range queryParamsTerms {
				termSplit := strings.Split(term, "=")
				result[termSplit[0]] = termSplit[1]
			}
		}
	}
	return result, nil
}

func parseGVK(schemaGvk string) (*v1.GroupVersionKind, error) {
	switch schemaGvk {
	case kubernetesServices:
		return &v1.GroupVersionKind{
			Version: "v1",
			Kind:    "services",
		}, nil
	case kubernetesPods:
		return &v1.GroupVersionKind{
			Version: "v1",
			Kind:    "pods",
		}, nil
	case kubernetesDeployments:
		return &v1.GroupVersionKind{
			Group:   "apps",
			Version: "v1",
			Kind:    "deployments",
		}, nil
	case kubernetesStatefulSets:
		return &v1.GroupVersionKind{
			Group:   "apps",
			Version: "v1",
			Kind:    "statefulsets",
		}, nil
	case kubernetesIngresses:
		return &v1.GroupVersionKind{
			Group:   "networking.k8s.io",
			Version: "v1",
			Kind:    "ingresses",
		}, nil
	case knativeServices:
		return &v1.GroupVersionKind{
			Group:   "serving.knative.dev",
			Version: "v1",
			Kind:    "services",
		}, nil
	case knativeBrokers:
		return &v1.GroupVersionKind{
			Group:   "eventing.knative.dev",
			Version: "v1",
			Kind:    "brokers",
		}, nil
	case openshiftRoutes:
		return &v1.GroupVersionKind{
			Group:   "route.openshift.io",
			Version: "v1",
			Kind:    "routes",
		}, nil
	case openshiftDeploymentConfigs:
		return &v1.GroupVersionKind{
			Group:   "apps.openshift.io",
			Version: "v1",
			Kind:    "deploymentconfigs",
		}, nil
	default:
		return nil, fmt.Errorf("unknown schema and gvk: %s", schemaGvk)
	}
}

func parseKnativeUri(uri string, schemaAndGroup string, after string) (*ResourceUri, error) {
	if namespace, name, gvk, queryParams, err := parseNamespaceNameGVKAndQueryParams(uri, schemaAndGroup, after); err != nil {
		return nil, err
	} else {
		return &ResourceUri{
			Scheme:      KnativeScheme,
			GVK:         *gvk,
			Namespace:   namespace,
			Name:        name,
			QueryParams: queryParams,
		}, nil
	}
}

func parseKnativeSimplifiedServiceUri(uri string) (*ResourceUri, error) {
	if !strings.HasPrefix(uri, "knative:") {
		return nil, fmt.Errorf("invalid knative simplified service uri: %s", uri)
	} else {
		nameAndNamespace := uri[len("knative:"):]
		var name, namespace string
		namespaceAndNameSplit := strings.Split(nameAndNamespace, "/")
		if len(namespaceAndNameSplit) == 2 {
			namespace = namespaceAndNameSplit[0]
			name = namespaceAndNameSplit[1]
		} else {
			name = namespaceAndNameSplit[0]
		}

		return &ResourceUri{
			Scheme: KnativeScheme,
			GVK: v1.GroupVersionKind{
				Group:   "serving.knative.dev",
				Version: "v1",
				Kind:    "services",
			},
			Namespace:   namespace,
			Name:        name,
			QueryParams: map[string]string{},
		}, nil
	}
}

func parseOpenshiftUri(uri string, schemaAndGroup string, after string) (*ResourceUri, error) {
	if namespace, name, gvk, queryParams, err := parseNamespaceNameGVKAndQueryParams(uri, schemaAndGroup, after); err != nil {
		return nil, err
	} else {
		return &ResourceUri{
			Scheme:      OpenshiftScheme,
			GVK:         *gvk,
			Namespace:   namespace,
			Name:        name,
			QueryParams: queryParams,
		}, nil
	}
}
