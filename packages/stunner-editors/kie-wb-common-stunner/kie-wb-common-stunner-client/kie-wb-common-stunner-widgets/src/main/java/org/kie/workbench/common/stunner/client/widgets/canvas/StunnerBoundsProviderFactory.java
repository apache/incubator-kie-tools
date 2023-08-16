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


package org.kie.workbench.common.stunner.client.widgets.canvas;

import static com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory.WiresBoundsProvider;
import static com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory.computeBoundsAspectRatio;

public class StunnerBoundsProviderFactory {

    public static final float ASPECT_RATIO = 300 / 150;
    public static final double PADDING = 50d;

    public static WiresBoundsProvider newProvider() {
        return new WiresBoundsProvider()
                .setPadding(PADDING)
                .setBoundsBuilder(boundingBox -> computeBoundsAspectRatio(ASPECT_RATIO, boundingBox));
    }

    public static double computeWidth(final double height) {
        return height * ASPECT_RATIO;
    }

    public static int computeWidth(final int height) {
        return (int) computeWidth((double) height);
    }

    public static double computeHeight(final double width) {
        return width * (1 / ASPECT_RATIO);
    }

    public static int computeHeight(final int width) {
        return (int) computeHeight((double) width);
    }
}
