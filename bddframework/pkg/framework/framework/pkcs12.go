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
	"crypto/rand"
	"crypto/x509"
	"encoding/pem"
	"errors"
	v1 "k8s.io/api/core/v1"
	"software.sslmate.com/src/go-pkcs12"
	"strings"
)

const (
	certificateBlock    = "CERTIFICATE"
	certificateEndBlock = "-----END CERTIFICATE-----"
)

// CreatePKCS12TrustStoreFromSecret creates a PCKS12 with certificates inside a Kubernetes secret.
// The `keys` parameter must contain public certificates only.
func CreatePKCS12TrustStoreFromSecret(secret *v1.Secret, password string, keys ...string) ([]byte, error) {
	if len(keys) == 0 {
		return nil, errors.New("at least one key is required")
	}
	var crts []string
	for _, key := range keys {
		cert := string(secret.Data[key])
		crts = append(crts, splitCertificates(cert, 0, crts)...)
	}
	if len(password) == 0 {
		password = pkcs12.DefaultPassword
	}
	var certs []*x509.Certificate
	for _, crt := range crts {
		block, _ := pem.Decode([]byte(crt))
		if block == nil || block.Type != certificateBlock {
			continue
		}
		cert, err := x509.ParseCertificate(block.Bytes)
		if err != nil {
			return nil, err
		}
		certs = append(certs, cert)
	}

	pfx, err := pkcs12.EncodeTrustStore(rand.Reader, certs, password)
	return pfx, err
}

func splitCertificates(certificate string, index int, certificates []string) []string {
	if index == len(certificate) {
		return certificates
	}
	lastIdx := strings.Index(certificate[index:], certificateEndBlock)
	if lastIdx == -1 {
		return certificates
	}
	lastIdx += len(certificateEndBlock) + index
	certificates = append(certificates, strings.Trim(certificate[index:lastIdx], "\n"))
	return splitCertificates(certificate, lastIdx, certificates)
}
