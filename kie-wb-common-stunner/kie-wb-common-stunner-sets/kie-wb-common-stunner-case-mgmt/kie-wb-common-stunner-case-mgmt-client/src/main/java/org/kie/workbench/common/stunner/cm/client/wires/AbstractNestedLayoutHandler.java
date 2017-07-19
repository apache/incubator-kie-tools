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
package org.kie.workbench.common.stunner.cm.client.wires;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;

public abstract class AbstractNestedLayoutHandler implements ILayoutHandler {

    private ReverseBreadthFirstTreeWalker walker = new ReverseBreadthFirstTreeWalker();

    @Override
    public void add(final WiresShape shape,
                    final WiresContainer container,
                    final Point2D mouseRelativeLoc) {
        orderChildren(shape,
                      container,
                      mouseRelativeLoc);
    }

    protected abstract void orderChildren(final WiresShape shape,
                                          final WiresContainer container,
                                          final Point2D mouseRelativeLoc);

    @Override
    public void remove(final WiresShape shape,
                       final WiresContainer container) {
        container.remove(shape);
    }

    @Override
    public void requestLayout(final WiresContainer container) {
        final WiresContainer root = findRoot(container);
        final List<WiresContainer> children = walker.getChildren(root);
        for (WiresContainer child : children) {
            child.getLayoutHandler().layout(child);
        }
    }

    protected WiresContainer findRoot(final WiresContainer parent) {
        WiresContainer _parent = parent;
        while (_parent.getParent() != null) {
            _parent = _parent.getParent();
        }
        return _parent;
    }

    static class ReverseBreadthFirstTreeWalker {

        List<WiresContainer> getChildren(final WiresContainer root) {
            final Queue<WiresContainer> q = new LinkedList<>();
            final Stack<WiresContainer> s = new Stack<>();
            q.add(root);

            while (!q.isEmpty()) {
                final WiresContainer container = q.remove();
                for (WiresShape child : container.getChildShapes()) {
                    q.add(child);
                }
                s.add(container);
            }

            final List<WiresContainer> ws = new ArrayList<>();
            while (!s.isEmpty()) {
                ws.add(s.pop());
            }
            return ws;
        }
    }
}
