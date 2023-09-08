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
	"testing"
)

func TestMapContainsMap(t *testing.T) {
	type args struct {
		source   map[string]string
		expected map[string]string
	}
	tests := []struct {
		name string
		args args
		want bool
	}{
		{"Simple case", args{
			source:   map[string]string{"key1": "value1"},
			expected: map[string]string{"key1": "value1"},
		}, true},
		{"Two keys case", args{
			source:   map[string]string{"key1": "value1", "key2": "value2"},
			expected: map[string]string{"key1": "value1"},
		}, true},
		{"Two keys both ways case", args{
			source:   map[string]string{"key1": "value1", "key2": "value2"},
			expected: map[string]string{"key1": "value1", "key2": "value2"},
		}, true},
		{"Both empty case", args{
			source:   map[string]string{},
			expected: map[string]string{},
		}, false},
		{"Source empty case", args{
			source:   map[string]string{},
			expected: map[string]string{"key1": "value1"},
		}, false},
		{"Expected empty case", args{
			source:   map[string]string{"key1": "value1"},
			expected: map[string]string{},
		}, false},
		{"Does not contain case", args{
			source:   map[string]string{"key1": "value1", "key2": "value2"},
			expected: map[string]string{"key3": "value3", "key4": "value4"},
		}, false},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := MapContainsMap(tt.args.source, tt.args.expected); got != tt.want {
				t.Errorf("MapContainsMap() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestFromMapToString(t *testing.T) {
	type args struct {
		labels map[string]string
	}
	tests := []struct {
		name string
		args args
		want []string
	}{
		{"Usual case", args{labels: map[string]string{"key": "value"}}, []string{"key=value"}},
		{"Usual case 2", args{labels: map[string]string{"key1": "value1", "key2": "value2"}}, []string{"key1=value1,key2=value2", "key2=value2,key1=value1"}},
		{"Empty case", args{labels: map[string]string{}}, []string{""}},
		{"Only key case", args{labels: map[string]string{"key1": ""}}, []string{"key1="}},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := FromMapToString(tt.args.labels)
			for _, v := range tt.want {
				if got == v {
					return
				}
			}
			t.Errorf("FromMapToString() = %v, want %v", got, tt.want)
		})
	}
}
