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
import java.util.Optional;

import com.ait.lienzo.client.core.shape.IPrimitive;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class SVGViewUtils {

    public static void switchVisibility(final SVGShapeView<?> view,
                                        final String visibleId,
                                        final String nonVisibleId) {
        getPrimitive(view,
                     visibleId)
                .ifPresent(prim -> prim.setAlpha(1));
        getPrimitive(view,
                     nonVisibleId)
                .ifPresent(prim -> prim.setAlpha(0));
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

    public static Optional<IPrimitive> getPrimitive(final SVGShapeView<?> view,
                                                    final String id) {

        final Collection<SVGPrimitive<?>> children = view.getChildren();

        for (final SVGPrimitive<?> child : children) {
            if (child instanceof SVGContainer) {
                final IPrimitive primitive = ((SVGContainer) child).getPrimitive(id);
                if (null != primitive) {
                    return Optional.of(primitive);
                }
            } else {
                if (child.getId().equals(id)) {
                    return Optional.of(child.get());
                }
            }
        }
        return Optional.empty();
    }
}
