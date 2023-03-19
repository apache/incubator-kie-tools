/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.c3.client.charts.meter;

import org.dashbuilder.renderer.c3.client.C3DisplayerView;

public class C3MeterView 
      extends C3DisplayerView<C3MeterChartDisplayer> 
      implements C3MeterChartDisplayer.View {

    private static final String RED = "#FF0000";
    private static final String ORANGE = "#F97600";
    private static final String GREEN = "#60B044";
    String[] colors = {
            GREEN,
            ORANGE,
            RED
    };
    
    @Override
    public String getType() {
        return "gauge";
    }

    @Override
    public String[] getColorPattern() {
        return colors;
    }

}