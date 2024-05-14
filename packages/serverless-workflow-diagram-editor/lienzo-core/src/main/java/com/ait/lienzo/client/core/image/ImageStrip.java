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

package com.ait.lienzo.client.core.image;

import org.kie.j2cl.tools.processors.common.resources.ImageResource;

public class ImageStrip {

    private final String name;
    private final String url;
    private final int wide;
    private final int high;
    private final int padding;
    private final Orientation orientation;

    public ImageStrip(final ImageResource resource,
                      final int wide,
                      final int high,
                      final int padding,
                      final Orientation orientation) {
        this(resource.getName(),
             resource.getSrc(),
             wide,
             high,
             padding,
             orientation);
    }

    public ImageStrip(final String name,
                      final String url,
                      final int wide,
                      final int high,
                      final int padding,
                      final Orientation orientation) {
        this.name = name;
        this.url = url;
        this.wide = wide;
        this.high = high;
        this.padding = padding;
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getWide() {
        return wide;
    }

    public int getHigh() {
        return high;
    }

    public int getPadding() {
        return padding;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL;
    }
}
