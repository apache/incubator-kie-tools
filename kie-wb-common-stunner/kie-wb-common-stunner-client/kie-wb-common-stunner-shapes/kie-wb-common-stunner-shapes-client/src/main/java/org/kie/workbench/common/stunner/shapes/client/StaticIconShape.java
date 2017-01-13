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
import org.kie.workbench.common.stunner.shapes.client.view.icon.statics.StaticIconShapeView;
import org.kie.workbench.common.stunner.shapes.def.icon.statics.IconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.statics.Icons;

public class StaticIconShape<W> extends AbstractBasicShape<W, StaticIconShapeView, IconShapeDef<W>> {

    public StaticIconShape(final StaticIconShapeView view,
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
        if (null != definition) {
            return proxy.getIcon(definition);
        }
        return icon;
    }

    @Override
    public void applyProperties(final Node<View<W>, Edge> element,
                                final MutationContext mutationContext) {
        super.applyProperties(element,
                              mutationContext);
        // Icon.
        Icons icon = getIcon(getDefinition(element),
                             proxy);
        getShapeView().setIcon(icon);
    }

    @Override
    protected String getBackgroundColor(final Node<View<W>, Edge> element) {
        // Background color does not changes as model updates.
        return null;
    }

    @Override
    protected Double getBackgroundAlpha(final Node<View<W>, Edge> element) {
        // Background alpha does not changes as model updates.
        return null;
    }

    @Override
    protected String getBorderColor(final Node<View<W>, Edge> element) {
        // Border color does not changes as model updates.
        return null;
    }

    @Override
    protected Double getBorderSize(final Node<View<W>, Edge> element) {
        // Border size does not changes as model updates.
        return null;
    }

    @Override
    protected Double getBorderAlpha(final Node<View<W>, Edge> element) {
        // Border alpha does not changes as model updates.
        return null;
    }

    @Override
    public String toString() {
        return "StaticIconShape{}";
    }
}
