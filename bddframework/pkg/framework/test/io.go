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

package test

import (
	"github.com/google/uuid"
	"io/ioutil"
	"k8s.io/apimachinery/pkg/types"
	"path/filepath"
	"testing"
)

// HelperLoadBytes will load, in bytes, the file with the given name in the testdata dir
func HelperLoadBytes(t *testing.T, name string) []byte {
	path := filepath.Join("testdata", name) // relative path
	bytes, err := ioutil.ReadFile(path)
	if err != nil {
		t.Fatal(err)
	}
	return bytes
}

// GenerateUID generates a Unique ID to be used across test cases
func GenerateUID() types.UID {
	uid, err := uuid.NewRandom()
	if err != nil {
		panic(err)
	}
	return types.UID(uid.String())
}

// GenerateShortUID same as GenerateUID, but returns a fraction of the generated UID instead.
// If count > than UID total length, returns the entire sequence.
func GenerateShortUID(count int) string {
	if count == 0 {
		return ""
	}
	uid := GenerateUID()
	if count > len(uid) {
		count = len(uid)
	}
	return string(uid)[:count]
}
