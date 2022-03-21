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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresShapeViewExt;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

public class WiresContainerShapeView<T extends WiresContainerShapeView>
        extends WiresShapeViewExt<T>
        implements
        HasChildren<T> {

    private final List<T> children = new ArrayList<>();

    public WiresContainerShapeView(final ViewEventType[] supportedEventTypes,
                                   final MultiPath path) {
        super(supportedEventTypes,
              path);
    }

    @Override
    public void addChild(final T child,
                         final Layout layout) {
        children.add(child);
        super.addChild((IPrimitive<?>) child.getContainer(),
                       LienzoShapeUtils.getWiresLayout(layout));
    }

    @Override
    public void removeChild(final T child) {
        children.remove(child);
        super.removeChild((IPrimitive<?>) child.getContainer());
    }

    @Override
    public Iterable<T> getChildren() {
        return children;
    }

    @Override
    public void destroy() {
        this.getChildren().forEach(WiresContainerShapeView::destroy);
        super.destroy();
    }
}
