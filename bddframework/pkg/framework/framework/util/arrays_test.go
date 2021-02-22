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
	"reflect"
	"testing"
)

func TestArrayToSet(t *testing.T) {
	type args struct {
		array []string
	}
	tests := []struct {
		name string
		args args
		want map[string]bool
	}{
		{
			"Different elements",
			args{
				[]string{"1", "2", "3", "4", "5"},
			},
			map[string]bool{
				"1": true,
				"2": true,
				"3": true,
				"4": true,
				"5": true,
			},
		},
		{
			"Same elements",
			args{
				[]string{"1", "2", "3", "2", "1"},
			},
			map[string]bool{
				"1": true,
				"2": true,
				"3": true,
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := ArrayToSet(tt.args.array); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("ArrayToSet() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestContainsAll(t *testing.T) {
	type args struct {
		array1 []string
		array2 []string
	}
	tests := []struct {
		name string
		args args
		want bool
	}{
		{
			"Contains",
			args{
				[]string{"1", "2", "3", "4", "5"},
				[]string{"1", "2", "3", "3", "2", "1"},
			},
			true,
		},
		{
			"NotContains",
			args{
				[]string{"1", "2", "3", "4", "5"},
				[]string{"1", "2", "3", "3", "2", "7"},
			},
			false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := ContainsAll(tt.args.array1, tt.args.array2); got != tt.want {
				t.Errorf("ContainsAll() = %v, want %v", got, tt.want)
			}
		})
	}
}
