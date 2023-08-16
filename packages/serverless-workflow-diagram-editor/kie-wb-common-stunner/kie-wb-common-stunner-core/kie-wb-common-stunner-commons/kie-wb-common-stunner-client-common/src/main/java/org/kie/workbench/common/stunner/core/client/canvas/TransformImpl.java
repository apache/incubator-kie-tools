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

package org.kie.workbench.common.stunner.core.client.canvas;

import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class TransformImpl implements Transform {

    private final Point2D translate;
    private final Point2D scale;

    public static final TransformImpl NO_TRANSFORM = new TransformImpl(new Point2D(0, 0), new Point2D(1, 1));

    public TransformImpl(final Point2D translate, final Point2D scale) {
        this.translate = translate;
        this.scale = scale;
    }

    @Override
    public Point2D getTranslate() {
        return translate;
    }

    @Override
    public Point2D getScale() {
        return scale;
    }

    @Override
    public Point2D transform(final double x, final double y) {
        return new Point2D((x * scale.getX()) + translate.getX(), (y * scale.getY()) + translate.getY());
    }

    @Override
    public Point2D inverse(final double x, final double y) {
        return new Point2D((x - translate.getX()) / scale.getX(), (y - translate.getY()) / scale.getY());
    }
}
