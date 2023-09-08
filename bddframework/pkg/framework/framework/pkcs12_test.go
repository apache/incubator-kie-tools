// Copyright 2020 Red Hat, Inc. and/or its affiliates
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

package framework

import (
	"github.com/stretchr/testify/assert"
	"io/ioutil"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"software.sslmate.com/src/go-pkcs12"
	"testing"
)

func TestCreatePKCS12TrustStoreFromSecret(t *testing.T) {
	crt, err := ioutil.ReadFile("./testdata/tls.crt")
	assert.NoError(t, err)
	assert.NotEmpty(t, crt)
	secret := v1.Secret{
		ObjectMeta: metav1.ObjectMeta{Namespace: t.Name(), Name: "mysecret"},
		Data:       map[string][]byte{"tls.crt": crt},
	}
	pkcs, err := CreatePKCS12TrustStoreFromSecret(&secret, pkcs12.DefaultPassword, "tls.crt")
	assert.NoError(t, err)
	assert.NotEmpty(t, pkcs)
}
