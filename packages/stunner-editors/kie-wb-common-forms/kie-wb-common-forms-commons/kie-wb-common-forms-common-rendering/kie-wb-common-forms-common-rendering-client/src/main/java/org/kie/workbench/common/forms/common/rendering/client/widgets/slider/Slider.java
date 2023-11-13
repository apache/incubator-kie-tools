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


package org.kie.workbench.common.forms.common.rendering.client.widgets.slider;

import com.google.gwt.dom.client.Element;
import org.gwtbootstrap3.extras.slider.client.ui.base.constants.HandleType;

/*
    Patch class to avoid error descrived on: https://github.com/gwtproject/gwt/issues/9242
    TODO: remove it when fixed
    TODO: there's an issue when the witget is binded, the value is update but it doesn't refresh the selected value.
 */
public class Slider extends org.gwtbootstrap3.extras.slider.client.ui.Slider {

    public Slider(double min,
                  double max,
                  double precision,
                  double step) {
        super(min,
              max,
              0.0);
        setPrecision(precision);
        setStep(step);
        setHandle(HandleType.TRIANGLE);
        setWidth("100%");
    }

    @Override
    protected native void setValue(Element e,
                                   Double value) /*-{
        $wnd.jQuery(e).slider(@org.gwtbootstrap3.extras.slider.client.ui.base.SliderCommand::SET_VALUE, value);
    }-*/;
}
