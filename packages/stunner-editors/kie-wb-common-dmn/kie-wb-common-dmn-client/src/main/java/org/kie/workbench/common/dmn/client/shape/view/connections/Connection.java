/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.shape.view.connections;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.types.Point2D;

public class Connection {

    static final double SELECTION_OFFSET = 30;

    private MultiPathDecorator head;
    private MultiPathDecorator tail;
    private PolyLine line;

    public Connection(final double x1,
                      final double y1,
                      final double x2,
                      final double y2) {
        buildConnection(x1, y1, x2, y2);
    }

    void buildConnection(final double x1,
                         final double y1,
                         final double x2,
                         final double y2) {
        head = createHead();
        tail = createTail();
        line = createLine(x1, y1, x2, y2);
    }

    public MultiPathDecorator getHead() {
        return head;
    }

    public MultiPathDecorator getTail() {
        return tail;
    }

    public PolyLine getLine() {
        return line;
    }

    protected PolyLine createLine(final double x1,
                                  final double y1,
                                  final double x2,
                                  final double y2) {
        final PolyLine line = new PolyLine(new Point2D(x1,
                                                       y1),
                                           new Point2D(x2,
                                                       y2));
        setDashArray(line);
        line.setDraggable(true);
        line.setSelectionStrokeOffset(SELECTION_OFFSET);
        line.setHeadOffset(getHead().getPath().getBoundingBox().getHeight());
        line.setTailOffset(getTail().getPath().getBoundingBox().getHeight());

        return line;
    }

    protected MultiPathDecorator createHead() {
        return new MultiPathDecorator(new MultiPath());
    }

    protected MultiPathDecorator createTail() {
        return new MultiPathDecorator(new MultiPath());
    }

    protected void setDashArray(final PolyLine line) {
        // It doesn't have dash array
    }
}
