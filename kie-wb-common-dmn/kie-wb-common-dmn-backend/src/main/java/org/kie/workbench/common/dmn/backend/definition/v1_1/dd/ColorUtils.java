/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import org.kie.dmn.model.api.dmndi.Color;

public class ColorUtils {

    public static Color dmnFromWB(String colorString) {
        final Color result = new org.kie.dmn.model.v1_2.dmndi.Color();

        final java.awt.Color decode = java.awt.Color.decode(colorString);

        result.setRed(decode.getRed());
        result.setBlue(decode.getBlue());
        result.setGreen(decode.getGreen());

        return result;
    }

    public static String wbFromDMN(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}