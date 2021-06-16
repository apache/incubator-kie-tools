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
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/kiegroup/kogito-operator/version"
	"github.com/stretchr/testify/assert"
	"k8s.io/api/apps/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"testing"
)

func TestRemoveSharedImageStreamOwnerShip(t *testing.T) {
	ns := t.Name()
	is, tag := test.CreateFakeImageStreams("dmn-quarkus-example", ns, GetKogitoImageVersion(version.Version))
	owner1 := &v1.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment1", Namespace: t.Name(), UID: test.GenerateUID()}}
	owner2 := &v1.Deployment{ObjectMeta: metav1.ObjectMeta{Name: "deployment2", Namespace: t.Name(), UID: test.GenerateUID()}}

	err := framework.AddOwnerReference(owner1, meta.GetRegisteredSchema(), is)
	assert.NoError(t, err)
	err = framework.AddOwnerReference(owner2, meta.GetRegisteredSchema(), is)
	assert.NoError(t, err)

	cli := test.NewFakeClientBuilder().OnOpenShift().AddK8sObjects(is).AddImageObjects(tag).Build()

	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: version.Version,
	}
	imageStreamHandler := NewImageStreamHandler(context)
	err = imageStreamHandler.RemoveSharedImageStreamOwnerShip(types.NamespacedName{Name: is.Name, Namespace: is.Namespace}, owner2)
	assert.NoError(t, err)

	updateImageStream, err := imageStreamHandler.FetchImageStream(types.NamespacedName{Name: is.Name, Namespace: is.Namespace})
	assert.NoError(t, err)
	ownerReferences := updateImageStream.GetOwnerReferences()
	assert.Equal(t, 1, len(ownerReferences))
	assert.Equal(t, "deployment1", ownerReferences[0].Name)
}
