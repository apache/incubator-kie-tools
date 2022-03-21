/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.palette;

import java.util.stream.Stream;

public class DefaultPaletteUtils {

    public static String getPaletteItemDefinitionId(final DefaultPaletteDefinition paletteDefinition,
                                                    final String itemId) {
        return paletteDefinition.getItems().stream()
                .flatMap(DefaultPaletteUtils::flattern)
                .filter(item -> itemId.equals(item.getId()))
                .findFirst()
                .get()
                .getDefinitionId();
    }

    @SuppressWarnings("unchecked")
    private static <T> Stream<T> flattern(final T item) {
        if (item instanceof AbstractPaletteItems) {
            return ((AbstractPaletteItems) item).getItems()
                    .stream()
                    .flatMap(DefaultPaletteUtils::flattern);
        } else {
            return Stream.of(item);
        }
    }
}
