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

package kubernetes

import (
	"fmt"

	"k8s.io/apimachinery/pkg/api/validation"
	"k8s.io/apimachinery/pkg/util/rand"
)

const dns1035MaxChar int = 63

// SafeDNS1035 generates a safe encoded string based on "s" with the given prefix.
// Ideally used with internal generated names.
//
// See https://kubernetes.io/docs/concepts/overview/working-with-objects/names/#rfc-1035-label-names
func SafeDNS1035(prefix, s string) (string, error) {
	safeNaming := prefix + rand.SafeEncodeString(s)
	if len(safeNaming) > dns1035MaxChar {
		safeNaming = safeNaming[:dns1035MaxChar]
	}
	errMsgs := validation.NameIsDNS1035Label(safeNaming, false)
	if len(errMsgs) > 0 {
		return "", fmt.Errorf("failed to generate a safe name for %s with prefix %s: %v", s, prefix, errMsgs)
	}
	return safeNaming, nil
}

// MustSafeDNS1035 see SafeDNS1035. Use this function only if you control the prefix.
func MustSafeDNS1035(prefix, s string) string {
	name, err := SafeDNS1035(prefix, s)
	if err != nil {
		panic(err)
	}
	return name
}
