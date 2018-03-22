/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OryxIdMappings {

    void init(final List<Class<?>> definitions);

    Map<Class<?>, String> getGlobalMappings();

    Map<Class<?>, String> getCustomMappings();

    Map<Class<?>, Set<String>> getSkippedProperties();

    Map<Class<?>, Map<Class<?>, String>> getDefinitionMappings();

    String getOryxDefinitionId(final Object definition);

    String getOryxPropertyId(final Class<?> clazz);

    String getOryxPropertyId(final Class<?> definitionClass,
                             final Class<?> clazz);

    boolean isSkipProperty(final Class<?> definitionClass,
                           final String oryxPropertyId);

    <T> Class<?> getProperty(final T definition,
                             final String oryxId);

    Class<?> getDefinition(final String oryxId);

    <T> String getPropertyId(final T definition,
                             final String oryxId);

    String getDefinitionId(final String oryxId);

    String getPropertyId(final Class<?> clazz);

    String getDefinitionId(final Class<?> clazz);
}
