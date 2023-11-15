// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package discovery

import (
	"fmt"
	"regexp"
	"strings"

	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	// valid namespace, name, or label name.
	dns1123LabelFmt         string = "[a-z0-9]([-a-z0-9]*[a-z0-9])?"
	namespaceAndNamePattern        = "^/((" + dns1123LabelFmt + ")+)(/(" + dns1123LabelFmt + ")+)?"
	queryStringPattern             = "^(\\?((" + dns1123LabelFmt + ")+\\=(" + dns1123LabelFmt + ")+)" +
		"(&(" + dns1123LabelFmt + ")+\\=(" + dns1123LabelFmt + ")+)*)?$"

	kubernetesGroupsPattern = "^(" + kubernetesServices +
		"|" + kubernetesPods +
		"|" + kubernetesDeployments +
		"|" + kubernetesStatefulSets +
		"|" + kubernetesIngresses + ")"

	knativeGroupsPattern = "^(" + knativeServices + ")"

	openshiftGroupsPattern = "^(" + openshiftDeploymentConfigs +
		"|" + openshiftRoutes + ")"
)

var kubernetesGroupsExpr = regexp.MustCompile(kubernetesGroupsPattern)
var knativeGroupsExpr = regexp.MustCompile(knativeGroupsPattern)
var openshiftGroupsExpr = regexp.MustCompile(openshiftGroupsPattern)
var namespaceAndNameExpr = regexp.MustCompile(namespaceAndNamePattern)
var queryStringExpr = regexp.MustCompile(queryStringPattern)

func ParseUri(uri string) (*ResourceUri, error) {
	if split := kubernetesGroupsExpr.Split(uri, -1); len(split) == 2 {
		return parseKubernetesUri(uri, kubernetesGroupsExpr.FindString(uri), split[1])
	} else if split := knativeGroupsExpr.Split(uri, -1); len(split) == 2 {
		return parseKnativeUri(knativeGroupsExpr.FindString(uri), split[1])
	} else if split := openshiftGroupsExpr.Split(uri, -1); len(split) == 2 {
		return parseOpenshiftUri(openshiftGroupsExpr.FindString(uri), split[1])
	}
	return nil, fmt.Errorf("invalid uri: %s, not correspond to any of the available schemes format: %s, %s, %s", uri, KubernetesScheme, KnativeScheme, OpenshiftScheme)
}

func parseKubernetesUri(uri string, schemaAndGroup string, after string) (*ResourceUri, error) {
	if split := namespaceAndNameExpr.Split(after, -1); len(split) == 2 {
		namespaceAndName := namespaceAndNameExpr.FindString(after)
		namespaceAndNameSplit := strings.Split(namespaceAndName, "/")
		var namespace, name string
		if len(namespaceAndNameSplit) == 3 {
			namespace = namespaceAndNameSplit[1]
			name = namespaceAndNameSplit[2]
		} else {
			name = namespaceAndNameSplit[1]
		}
		var queryParams map[string]string
		var err error
		if queryParams, err = parseQueryParams(uri, split[1]); err != nil {
			return nil, err
		}

		gvk, _ := parseGVK(schemaAndGroup)
		return &ResourceUri{
			Scheme:       KubernetesScheme,
			GVK:          *gvk,
			Namespace:    namespace,
			Name:         name,
			CustomLabels: queryParams,
		}, nil

	} else {
		return nil, fmt.Errorf("invalid kubernetes uri: %s, provided namespace, name, or query parameters %s not correspond to the expected formats: /my-namespace/my-service?label-name=label-value&another-label=another-value", uri, after)
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
	default:
		return nil, fmt.Errorf("unknown schema and gvk: %s", schemaGvk)
	}
}

func parseKnativeUri(group string, after string) (*ResourceUri, error) {
	return nil, fmt.Errorf("knative is parsing not yet implemented")
}

func parseOpenshiftUri(findString string, s string) (*ResourceUri, error) {
	return nil, fmt.Errorf("openshit is parsing not yet implemented")
}
