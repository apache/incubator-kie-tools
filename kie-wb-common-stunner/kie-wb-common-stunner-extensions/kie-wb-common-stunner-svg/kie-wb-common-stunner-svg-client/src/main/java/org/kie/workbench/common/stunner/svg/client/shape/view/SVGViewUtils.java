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

package org.kie.workbench.common.stunner.svg.client.shape.view;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class SVGViewUtils {

    public static void switchVisibility(final SVGShapeView<?> view,
                                        final String visibleId,
                                        final String nonVisibleId) {
        getPrimitive(view,
                     visibleId)
                .ifPresent(prim -> prim.get().setAlpha(1));
        getPrimitive(view,
                     nonVisibleId)
                .ifPresent(prim -> prim.get().setAlpha(0));
    }

    @SuppressWarnings("unchecked")
    public static <V extends ShapeView<?>> V getVisibleShape(final ShapeView<?>... views) {
        for (ShapeView<?> view : views) {
            if (view.getAlpha() > 0) {
                return (V) view;
            }
        }
        return null;
    }

    public static Optional<SVGPrimitive> getPrimitive(final SVGShapeView<?> view,
                                                      final String id) {
        return Optional.ofNullable(getPrimitive(view.getChildren(),
                                                id));
    }

    public static SVGPrimitive getPrimitive(final SVGContainer container,
                                            final String id) {
        return getPrimitive(container.getChildren(),
                            id);
    }

    public static SVGPrimitive getPrimitive(final Collection<SVGPrimitive<?>> primitives,
                                            final String id) {
        return primitives.stream()
                .map(p -> getPrimitive(p,
                                       id))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public static SVGPrimitive getPrimitive(final SVGPrimitive<?> primitive,
                                            final String id) {
        if (primitive instanceof SVGContainer) {
            return getPrimitive((SVGContainer) primitive,
                                id);
        }
        return id.equals(primitive.getId()) ?
                primitive :
                null;
    }
}
