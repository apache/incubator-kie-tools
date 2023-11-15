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

type knServiceCatalog struct {
	Client client.Client
}

func newKnServiceCatalog(cli client.Client) knServiceCatalog {
	return knServiceCatalog{
		Client: cli,
	}
}

func (c knServiceCatalog) Query(ctx context.Context, uri ResourceUri, outputFormat string) (string, error) {
	return "", fmt.Errorf("knative service discovery is not yet implemened")
}
