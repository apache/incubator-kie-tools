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

package infrastructure

import (
	"github.com/RHsyseng/operator-utils/pkg/resource/compare"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	"reflect"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// DeltaProcessor ...
type DeltaProcessor interface {
	ProcessDelta(comparator compare.MapComparator, requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (isDeltaProcessed bool, err error)
}

type deltaProcessor struct {
	operator.Context
}

// NewDeltaProcessor ...
func NewDeltaProcessor(context operator.Context) DeltaProcessor {
	return &deltaProcessor{
		context,
	}
}

func (d *deltaProcessor) ProcessDelta(comparator compare.MapComparator, requestedResources map[reflect.Type][]client.Object, deployedResources map[reflect.Type][]client.Object) (isDeltaProcessed bool, err error) {
	deltas := comparator.Compare(deployedResources, requestedResources)
	for resourceType, delta := range deltas {
		if !delta.HasChanges() {
			d.Log.Debug("No delta found", "resourceType", resourceType)
			continue
		}
		d.Log.Info("Will", "create", len(delta.Added), "update", len(delta.Updated), "delete", len(delta.Removed), "resourceType", resourceType)

		if _, err = kubernetes.ResourceC(d.Client).CreateResources(delta.Added); err != nil {
			return
		}

		if _, err = kubernetes.ResourceC(d.Client).UpdateResources(deployedResources[resourceType], delta.Updated); err != nil {
			return
		}

		if _, err = kubernetes.ResourceC(d.Client).DeleteResources(delta.Removed); err != nil {
			return
		}

		isDeltaProcessed = true
	}
	return
}
