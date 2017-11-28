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

/**
 * Extension of the google Legend in order to allow for positioning the legend to the left of the chart, which is not
 * possible with the original class, because the LegendPosition enum does not include the 'left' value.
 */
public class GoogleLegendWrapper extends Legend {

    public static GoogleLegendWrapper create() {
        return createObject().cast();
    }

    protected GoogleLegendWrapper() {
    }

    public final void setLegendPosition(String alignment) {
        setPosition(alignment);
    }
}
