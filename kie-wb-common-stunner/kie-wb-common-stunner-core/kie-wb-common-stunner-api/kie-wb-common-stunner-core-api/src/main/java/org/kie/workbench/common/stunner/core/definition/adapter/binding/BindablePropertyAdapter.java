/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;

import java.util.Map;

public interface BindablePropertyAdapter<T, V> extends PropertyAdapter<T, V> {

    void setBindings( Map<Class, String> propertyTypeFieldNames,
                      Map<Class, String> propertyCaptionFieldNames,
                      Map<Class, String> propertyDescriptionFieldNames,
                      Map<Class, String> propertyReadOnlyFieldNames,
                      Map<Class, String> propertyOptionalFieldNames,
                      Map<Class, String> propertyValueFieldNames,
                      Map<Class, String> propertyDefaultValueFieldNames,
                      Map<Class, String> propertyAllowedValuesFieldNames );

}
