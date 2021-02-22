// Copyright 2019 Red Hat, Inc. and/or its affiliates
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
	"crypto/md5"
	"fmt"
	"math/rand"
	"strconv"
)

// GenerateMD5Hash will generate a MD5 hash from the given map
func GenerateMD5Hash(source map[string]string) string {
	if len(source) == 0 {
		return ""
	}
	b := new(bytes.Buffer)
	for k, v := range source {
		fmt.Fprintf(b, "%s=\"%s\"\n", k, v)
	}
	return fmt.Sprintf("%x", md5.Sum(b.Bytes()))
}

// RandomSuffix generates a random suffix to be used in names for kubernetes objects
func RandomSuffix() string {
	return strconv.Itoa(rand.Intn(10000))
}
