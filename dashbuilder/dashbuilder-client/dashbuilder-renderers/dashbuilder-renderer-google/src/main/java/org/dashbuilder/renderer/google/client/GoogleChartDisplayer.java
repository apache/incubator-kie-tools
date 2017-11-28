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
package org.dashbuilder.renderer.google.client;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.Position;

/**
 * Base class for all the Google chart displayers
 */
public abstract class GoogleChartDisplayer<V extends GoogleChartDisplayer.View> extends GoogleDisplayer<V> {

    public interface View<P extends GoogleChartDisplayer> extends GoogleDisplayer.View<P> {

        void setWidth(int width);

        void setHeight(int height);

        void setMarginTop(int marginTop);

        void setMarginBottom(int marginBottom);

        void setMarginRight(int marginRight);

        void setMarginLeft(int marginLeft);

        void setLegendPosition(Position position);

        void setSubType(DisplayerSubType subType);
    }

    public GoogleChartDisplayer(FilterLabelSet filterLabelSet) {
        super(filterLabelSet);
    }

    @Override
    protected void createVisualization() {
        super.createVisualization();

        getView().setWidth(displayerSettings.getChartWidth());
        getView().setHeight(displayerSettings.getChartHeight());
        getView().setMarginTop(displayerSettings.getChartMarginTop());
        getView().setMarginBottom(displayerSettings.getChartMarginBottom());
        getView().setMarginRight(displayerSettings.getChartMarginRight());
        getView().setMarginLeft(displayerSettings.getChartMarginLeft());
        getView().setLegendPosition(displayerSettings.isChartShowLegend() ? displayerSettings.getChartLegendPosition() : null);
        getView().setSubType(displayerSettings.getSubtype());
    }
}
