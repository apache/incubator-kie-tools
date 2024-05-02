// Copyright 2024 Apache Software Foundation (ASF)
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

package properties

import (
	"testing"

	"github.com/magiconair/properties"
	"github.com/stretchr/testify/assert"
)

func assertHasProperty(t *testing.T, props *properties.Properties, expectedProperty string, expectedValue string) {
	value, ok := props.Get(expectedProperty)
	assert.True(t, ok, "Property %s, is not present as expected.", expectedProperty)
	assert.Equal(t, expectedValue, value, "Expected value for property: %s, is: %s but current value is: %s", expectedProperty, expectedValue, value)
}
