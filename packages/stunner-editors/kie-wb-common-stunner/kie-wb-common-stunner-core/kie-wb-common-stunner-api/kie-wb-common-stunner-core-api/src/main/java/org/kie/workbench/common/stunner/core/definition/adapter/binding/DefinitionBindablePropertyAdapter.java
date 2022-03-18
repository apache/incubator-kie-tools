/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;

public interface DefinitionBindablePropertyAdapter<T, V> extends PropertyAdapter<DefinitionBindableProperty<T>, V> {

    @Override
    default String getId(DefinitionBindableProperty<T> property) {
        return BindableAdapterUtils.getPropertyId(property.getPojo().getClass()) + "." + property.getField();
    }

    @Override
    default int getPriority() {
        return 0;
    }

    @Override
    default boolean accepts(Class<?> type) {
        return DefinitionBindableProperty.class.equals(type);
    }
}
