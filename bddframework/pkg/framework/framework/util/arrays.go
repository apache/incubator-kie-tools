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

// Contains checks if the s string are within the array
func Contains(s string, array []string) bool {
	if len(s) == 0 {
		return false
	}
	for _, item := range array {
		if s == item {
			return true
		}
	}
	return false
}

// ArrayToSet converts an array of string to a set
func ArrayToSet(array []string) map[string]bool {
	set := make(map[string]bool, len(array))

	for _, e := range array {
		set[e] = true
	}

	return set
}

// ContainsAll checks if all the elements of the second are in the first array
func ContainsAll(array1 []string, array2 []string) bool {
	set1 := ArrayToSet(array1)

	for _, e := range array2 {
		if !set1[e] {
			return false
		}
	}

	return true
}
