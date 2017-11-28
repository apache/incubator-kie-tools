/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.impl;

import org.dashbuilder.displayer.ChartSettingsBuilder;
import org.dashbuilder.displayer.Position;

public abstract class AbstractChartSettingsBuilder<T extends ChartSettingsBuilder> extends AbstractDisplayerSettingsBuilder<T> implements ChartSettingsBuilder<T> {

    public T width(int width) {
        displayerSettings.setChartWidth( width );
        return (T) this;
    }

    public T height(int height) {
        displayerSettings.setChartHeight( height );
        return (T) this;
    }

    public T margins(int top, int bottom, int left, int right) {
        displayerSettings.setChartMarginTop(top);
        displayerSettings.setChartMarginBottom(bottom);
        displayerSettings.setChartMarginLeft(left);
        displayerSettings.setChartMarginRight(right);
        return (T) this;
    }

    public T legendOff() {
        displayerSettings.setChartShowLegend(false);
        return (T) this;
    }

    public T legendOn(Position position) {
        return legendOn(position.name());
    }

    public T legendOn(String position) {
        displayerSettings.setChartShowLegend(true);
        displayerSettings.setChartLegendPosition(Position.getByName(position));
        return (T) this;
    }

    public T resizableOn(int maxWidth, int maxHeight) {
        displayerSettings.setResizable(true);
        displayerSettings.setChartMaxWidth(maxWidth);
        displayerSettings.setChartMaxHeight(maxHeight);
        return (T) this;
    }

    public T resizableOff() {
        displayerSettings.setResizable(false);
        return (T) this;
    }
}
