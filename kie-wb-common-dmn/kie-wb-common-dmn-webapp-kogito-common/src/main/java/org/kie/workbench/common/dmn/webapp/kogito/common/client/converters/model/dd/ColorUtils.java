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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.dd;

import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIColor;

public class ColorUtils {

    public static JSIColor dmnFromWB(final String colorString) {
        final JSIColor result = new JSIColor();

        try {
            final Integer intval = Integer.decode(colorString);
            final int i = intval.intValue();
            result.setRed((i >> 16) & 0xFF);
            result.setGreen((i >> 8) & 0xFF);
            result.setBlue(i & 0xFF);
        } catch (NumberFormatException nfe) {
            //Return default
        }

        return result;
    }

    public static String wbFromDMN(final JSIColor color) {
        return "#" + toHexString(color.getRed()) + toHexString(color.getGreen()) + toHexString(color.getBlue());
    }

    private static String toHexString(final int value) {
        final String hex = Integer.toHexString(value & 0xFF);
        return hex.length() > 1 ? hex : "0" + hex;
    }
}