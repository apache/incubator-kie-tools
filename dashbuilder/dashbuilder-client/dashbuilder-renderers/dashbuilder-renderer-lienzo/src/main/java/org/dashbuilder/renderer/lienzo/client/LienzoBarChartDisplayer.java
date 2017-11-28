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
package org.dashbuilder.renderer.lienzo.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.displayer.DisplayerSubType;

@Dependent
public class LienzoBarChartDisplayer extends LienzoXYChartDisplayer<LienzoBarChartDisplayer.View> {

    public interface View extends LienzoXYChartDisplayer.View<LienzoBarChartDisplayer> {

    }

    private View view;

    public LienzoBarChartDisplayer() {
        this(new LienzoBarChartDisplayerView());
    }

    @Inject
    public LienzoBarChartDisplayer(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    protected void createVisualization() {
        DisplayerSubType subType = displayerSettings.getSubtype();
        getView().setHorizontal(subType != null &&
                        (DisplayerSubType.BAR.equals(subType) ||
                        DisplayerSubType.BAR_STACKED.equals(subType)));

        super.createVisualization();
    }
}
