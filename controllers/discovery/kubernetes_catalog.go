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
	"context"
	"fmt"

	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	serviceKind = "services"
	podKind     = "pods"
)

type k8sServiceCatalog struct {
	Client client.Client
}

func newK8SServiceCatalog(cli client.Client) k8sServiceCatalog {
	return k8sServiceCatalog{
		Client: cli,
	}
}

func (c k8sServiceCatalog) Query(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	switch uri.GVK.Kind {
	case serviceKind:
		return c.resolveServiceQuery(ctx, uri, outputFormat)
	case podKind:
		return c.resolvePodQuery(ctx, uri, outputFormat)
	default:
		return "", fmt.Errorf("resolution of kind: %s is not yet implemented", uri.GVK.Kind)
	}
}

func (c k8sServiceCatalog) resolveServiceQuery(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if service, err := findService(ctx, c.Client, uri.Namespace, uri.Name); err != nil {
		return "", err
	} else if serviceUri, err := resolveServiceUri(service, uri.GetPort(), outputFormat); err != nil {
		return "", err
	} else {
		return serviceUri, nil
	}
}

func (c k8sServiceCatalog) resolvePodQuery(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	if pod, service, err := findPodAndReferenceServiceByPodLabels(ctx, c.Client, uri.Namespace, uri.Name); err != nil {
		return "", err
	} else {
		if service != nil {
			if serviceUri, err := resolveServiceUri(service, uri.GetPort(), outputFormat); err != nil {
				return "", err
			} else {
				return serviceUri, nil
			}
		} else {
			if podUri, err := resolvePodUri(pod, "", uri.GetPort(), outputFormat); err != nil {
				return "", err
			} else {
				return podUri, nil
			}
		}
	}
}
