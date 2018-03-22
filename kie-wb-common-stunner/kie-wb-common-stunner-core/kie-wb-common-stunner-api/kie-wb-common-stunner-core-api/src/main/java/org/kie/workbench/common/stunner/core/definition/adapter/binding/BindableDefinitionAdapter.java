/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

public interface BindableDefinitionAdapter<T> extends DefinitionAdapter<T>,
                                                      HasInheritance {

    void setBindings(Map<PropertyMetaTypes, Class> metaPropertyTypeClasses,
                     Map<Class, Class> baseTypes,
                     Map<Class, Set<String>> propertySetsFieldNames,
                     Map<Class, Set<String>> propertiesFieldNames,
                     Map<Class, Class> propertyGraphFactoryFieldNames,
                     Map<Class, String> propertyIdFieldNames,
                     Map<Class, String> propertyLabelsFieldNames,
                     Map<Class, String> propertyTitleFieldNames,
                     Map<Class, String> propertyCategoryFieldNames,
                     Map<Class, String> propertyDescriptionFieldNames);

    Class<? extends ElementFactory> getGraphFactory(final Class<?> type);
}
