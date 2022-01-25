/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.preferences;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.client.shape.factory.BPMNShapeFactory;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerTextPreferences;

/**
 * Centralize the Text properties used on BPMN Diagram.
 * SVG shapes are getting these properties from CSS {@link BPMNSVGViewFactory#PATH_CSS}.
 * <p>
 * {@link BPMNShapeFactory} injects these properties when creating the shapes.
 */
@Dependent
@Specializes
public class BPMNTextPreferences extends StunnerTextPreferences {

    //Constant values from BPMNSVGViewFactory.PATH_CSS
    public static final int TEXT_ALPHA = 1;
    public static final String TEXT_FONT_FAMILY = "Open Sans";
    public static final int TEXT_FONT_SIZE = 10;
    public static final String TEXT_FILL_COLOR = "#000000";
    public static final String TEXT_STROKE_COLOR = "#393f44";
    public static final int TEXT_STROKE_WIDTH = 0;

    public BPMNTextPreferences() {
        setTextAlpha(TEXT_ALPHA);
        setTextFontFamily(TEXT_FONT_FAMILY);
        setTextFontSize(TEXT_FONT_SIZE);
        setTextFillColor(TEXT_FILL_COLOR);
        setTextStrokeColor(TEXT_STROKE_COLOR);
        setTextStrokeWidth(TEXT_STROKE_WIDTH);
    }
}