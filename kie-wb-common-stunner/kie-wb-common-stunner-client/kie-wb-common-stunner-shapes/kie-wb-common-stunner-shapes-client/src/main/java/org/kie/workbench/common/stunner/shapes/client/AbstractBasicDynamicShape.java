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

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.client.view.BasicShapeView;
import org.kie.workbench.common.stunner.shapes.def.BasicShapeDef;

public abstract class AbstractBasicDynamicShape<W, V extends BasicShapeView, P extends BasicShapeDef<W>>
        extends BasicShape<W, V> {

    protected final transient P proxy;

    public AbstractBasicDynamicShape(final V view,
                                     final P proxy) {
        super(view);
        this.proxy = proxy;
    }

    @Override
    protected String getBackgroundColor(final Node<View<W>, Edge> element) {
        return proxy.getBackgroundColor(getDefinition(element));
    }

    @Override
    protected Double getBackgroundAlpha(final Node<View<W>, Edge> element) {
        return proxy.getBackgroundAlpha(getDefinition(element));
    }

    @Override
    protected String getBorderColor(final Node<View<W>, Edge> element) {
        return proxy.getBorderColor(getDefinition(element));
    }

    @Override
    protected Double getBorderSize(final Node<View<W>, Edge> element) {
        return proxy.getBorderSize(getDefinition(element));
    }

    @Override
    protected Double getBorderAlpha(final Node<View<W>, Edge> element) {
        return proxy.getBackgroundAlpha(getDefinition(element));
    }
}
