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

package util

import (
	"bytes"
	"fmt"
	"sort"
	"strings"
)

const (
	pairSeparator = ","
)

// MapContainsMap returns true only if source contains expected map
func MapContainsMap(source, expected map[string]string) bool {
	if len(source) == 0 || len(expected) == 0 {
		return false
	}
	for k, v := range expected {
		if source[k] != v {
			return false
		}
	}
	return true
}

// FromMapToString converts a map into a string format such as key1=value1,key2=value2
func FromMapToString(labels map[string]string) string {
	b := new(bytes.Buffer)
	var keys []string
	for k := range labels {
		keys = append(keys, k)
	}
	sort.Strings(keys)
	for _, k := range keys {
		fmt.Fprintf(b, "%s=%s%s", k, labels[k], pairSeparator)
	}
	return strings.TrimSuffix(b.String(), pairSeparator)
}

// AppendToStringMap appends source into dest. If keys are equal, value is overridden
func AppendToStringMap(source map[string]string, dest map[string]string) {
	for k, v := range source {
		dest[k] = v
	}
}

// AddToMap ...
func AddToMap(key, value string, dest map[string]string) {
	dest[key] = value
}

// MapContains ...
func MapContains(source map[string]string, key string, value string) bool {
	return source[key] == value
}
