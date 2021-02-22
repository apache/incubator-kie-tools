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
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestGenerateMD5Hash(t *testing.T) {
	map1 := map[string]string{"key": "value"}
	map2 := map[string]string{"key": "value"}
	map3 := map[string]string{"key1": "value1"}
	hashToCompare := GenerateMD5Hash(map1)
	differentHash := GenerateMD5Hash(map3)
	assert.NotNil(t, hashToCompare)

	type args struct {
		source map[string]string
	}
	tests := []struct {
		name string
		args args
		want string
	}{
		{"Should have the same hash as map1", args{map2}, hashToCompare},
		{"Should have no hash", args{map[string]string{}}, ""},
		{"Should have no hash because nil", args{nil}, ""},
		{"Should have different hash", args{map3}, differentHash},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := GenerateMD5Hash(tt.args.source); got != tt.want {
				t.Errorf("GenerateMD5Hash() = %v, want %v", got, tt.want)
			}
		})
	}

	assert.NotEqual(t, hashToCompare, differentHash)
}
