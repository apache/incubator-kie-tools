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

package gherkin

import "github.com/cucumber/godog"

// MatchingFeatureWithTags checks whether any scenario in the feature has the given tags
func MatchingFeatureWithTags(filterTags string, features []*Feature) bool {
	for _, ft := range features {
		if MatchesScenariosWithTags(filterTags, ft.Scenarios) {
			return true
		}
	}
	return false
}

// MatchesScenariosWithTags checks whether the given scenarioin has the given tags
func MatchesScenariosWithTags(filterTags string, scenarios []*godog.Scenario) bool {
	for _, scenario := range scenarios {
		if matchesTags(filterTags, scenario.Tags) {
			return true
		}
	}
	return false
}
