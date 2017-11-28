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

import com.googlecode.gwt.charts.client.options.Legend;
import com.googlecode.gwt.charts.client.options.LegendAlignment;
import com.googlecode.gwt.charts.client.options.LegendPosition;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.Position;

/**
 * Base class for all the Google chart like displayers
 */
public abstract class GoogleChartDisplayerView<P extends GoogleChartDisplayer>
        extends GoogleDisplayerView<P>
        implements GoogleChartDisplayer.View<P> {

    protected int width = 500;
    protected int height= 300;
    protected int marginTop = 10;
    protected int marginBottom = 10;
    protected int marginRight = 10;
    protected int marginLeft = 10;
    protected Position legendPosition = null;
    protected DisplayerSubType subType = null;

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    @Override
    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    @Override
    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    @Override
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    @Override
    public void setLegendPosition(Position legendPosition) {
        this.legendPosition = legendPosition;
    }

    @Override
    public void setSubType(DisplayerSubType subType) {
        this.subType = subType;
    }

    protected Legend createChartLegend() {
        GoogleLegendWrapper legend = GoogleLegendWrapper.create();
        legend.setLegendPosition(getLegendPosition());
        legend.setAligment(LegendAlignment.CENTER);
        return legend;
    }

    protected String getLegendPosition() {
        if (legendPosition != null) {
            switch (legendPosition) {
                case TOP:
                    return LegendPosition.TOP.toString().toLowerCase();
                case BOTTOM:
                    return LegendPosition.BOTTOM.toString().toLowerCase();
                case RIGHT:
                    return LegendPosition.RIGHT.toString().toLowerCase();
                case IN:
                    return LegendPosition.IN.toString().toLowerCase();
                case LEFT:
                    return "left";
                default:
                    return LegendPosition.RIGHT.toString().toLowerCase();
            }
        }
        return LegendPosition.NONE.toString().toLowerCase();
    }
}
