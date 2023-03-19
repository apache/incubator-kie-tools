/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.echarts.client;

import java.util.Objects;

import org.dashbuilder.displayer.Mode;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Renderer;

public class ChartBootstrapParams {

    int width;
    int height;
    boolean resizable;
    Mode mode;
    Renderer renderer;

    public ChartBootstrapParams(int width, int height, boolean resizable, Mode mode, Renderer renderer) {
        this.width = width;
        this.height = height;
        this.resizable = resizable;
        this.mode = mode;
        this.renderer = renderer;
    }

    public static ChartBootstrapParams of(int width,
                                          int height,
                                          boolean resizable,
                                          Mode mode,
                                          Renderer renderer) {
        return new ChartBootstrapParams(width, height, resizable, mode, renderer);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResizable() {
        return resizable;
    }

    public Mode getMode() {
        return mode;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, mode, renderer, resizable, width);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        var other = (ChartBootstrapParams) obj;
        return height == other.height && mode == other.mode && renderer == other.renderer &&
               resizable == other.resizable && width == other.width;
    }

}
