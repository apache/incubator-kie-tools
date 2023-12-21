/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package utils

import (
	"github.com/magiconair/properties"
)

type ApplicationPropertiesBuilder interface {
	WithInitialProperties(initialProperties *properties.Properties) ApplicationPropertiesBuilder
	WithImmutableProperties(immutableProperties *properties.Properties) ApplicationPropertiesBuilder
	WithDefaultMutableProperties(defaultMutableProperties *properties.Properties) ApplicationPropertiesBuilder
	BuildAsString() string
	Build() *properties.Properties
}

type applicationPropertiesBuilder struct {
	initialProperties        *properties.Properties
	immutableProperties      *properties.Properties
	defaultMutableProperties *properties.Properties
}

func (a *applicationPropertiesBuilder) WithInitialProperties(initialProperties *properties.Properties) ApplicationPropertiesBuilder {
	a.initialProperties = initialProperties
	return a
}

func (a *applicationPropertiesBuilder) WithImmutableProperties(immutableProperties *properties.Properties) ApplicationPropertiesBuilder {
	a.immutableProperties = immutableProperties
	return a
}

func (a *applicationPropertiesBuilder) WithDefaultMutableProperties(defaultMutableProperties *properties.Properties) ApplicationPropertiesBuilder {
	a.defaultMutableProperties = defaultMutableProperties
	return a
}

func (a *applicationPropertiesBuilder) BuildAsString() string {
	return a.Build().String()
}

func (a *applicationPropertiesBuilder) Build() *properties.Properties {
	var props *properties.Properties
	if a.initialProperties != nil {
		props = a.initialProperties
	} else {
		props = properties.NewProperties()
	}
	// Disable expansions since it's not our responsibility
	// Property expansion means resolving ${} within the properties and environment context. Quarkus will do that in runtime.
	props.DisableExpansion = true

	if a.defaultMutableProperties != nil {
		defaultMutableProps := a.defaultMutableProperties
		for _, k := range defaultMutableProps.Keys() {
			if _, ok := props.Get(k); ok {
				defaultMutableProps.Delete(k)
			}
		}
		props.Merge(defaultMutableProps)
	}

	if a.immutableProperties != nil {
		// finally overwrite with the defaults immutable properties.
		props.Merge(a.immutableProperties)
	}
	return props
}

func NewApplicationPropertiesBuilder() ApplicationPropertiesBuilder {
	return &applicationPropertiesBuilder{}
}
