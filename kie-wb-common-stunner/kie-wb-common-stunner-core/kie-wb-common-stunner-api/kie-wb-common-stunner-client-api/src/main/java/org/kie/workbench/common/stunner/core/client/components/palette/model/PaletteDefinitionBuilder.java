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

package org.kie.workbench.common.stunner.core.client.components.palette.model;

import java.util.Set;

public interface PaletteDefinitionBuilder<T, P, E> {

    interface Callback<P, E> {

        void onSuccess(final P paletteDefinition);

        void onError(final E error);
    }

    interface Configuration {

        String getDefinitionSetId();

        Set<String> getDefinitionIds();

    }

    /**
     * Exclude the given Definition identifier from appearing on the palette.
     * @param definitionId The Definition identifier to exclude.
     * @return The builder instance.
     */
    PaletteDefinitionBuilder<T, P, E> excludeDefinition(final String definitionId);

    /**
     * Exclude the given category identifier from appearing on the palette.
     * @param categoryId The category identifier to exclude.
     * @return The builder instance.
     */
    PaletteDefinitionBuilder<T, P, E> excludeCategory(final String categoryId);

    /**
     * Build the palette from source. Results present on the callback argument, as palette definition could be
     * build on server side.
     */
    void build(final T source,
               final Callback<P, E> callback);
}
