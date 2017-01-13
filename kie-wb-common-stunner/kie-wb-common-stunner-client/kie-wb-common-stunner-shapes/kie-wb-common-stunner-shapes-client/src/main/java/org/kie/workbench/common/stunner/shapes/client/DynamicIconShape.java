/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.client.view.icon.dynamics.DynamicIconShapeView;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.DynamicIconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.IconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.Icons;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.MinusIconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.PlusIconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.XORIconShapeDef;

public class DynamicIconShape<W> extends AbstractBasicDynamicShape<W, DynamicIconShapeView, IconShapeDef<W>> {

    public DynamicIconShape(final DynamicIconShapeView view,
                            final IconShapeDef<W> proxy) {
        super(view,
              proxy);
    }

    public static <T> Icons getIcon(final IconShapeDef<T> proxy) {
        return getIcon(null,
                       proxy);
    }

    public static <T> Icons getIcon(final T definition,
                                    final IconShapeDef<T> proxy) {
        Icons icon = null;
        if (null != definition && proxy instanceof DynamicIconShapeDef) {
            final DynamicIconShapeDef<T> dynamicIconProxy = (DynamicIconShapeDef<T>) proxy;
            icon = dynamicIconProxy.getIcon(definition);
        } else if (proxy instanceof MinusIconShapeDef) {
            icon = Icons.MINUS;
        } else if (proxy instanceof PlusIconShapeDef) {
            icon = Icons.PLUS;
        } else if (proxy instanceof XORIconShapeDef) {
            icon = Icons.XOR;
        }
        return icon;
    }

    @Override
    public void applyProperties(final Node<View<W>, Edge> element,
                                final MutationContext mutationContext) {
        super.applyProperties(element,
                              mutationContext);
        // Width/Height.
        final Double w = proxy.getWidth(getDefinition(element));
        final Double h = proxy.getHeight(getDefinition(element));
        _applyWidthAndHeight(element,
                             w,
                             h,
                             mutationContext);
        // Icon.
        Icons icon = getIcon(getDefinition(element),
                             proxy);
        getShapeView().setIcon(icon);
    }

    @Override
    public String toString() {
        return "DynamicIconShape{}";
    }
}
