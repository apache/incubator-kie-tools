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

package org.kie.workbench.common.dmn.client.marshaller.converters.dd;

import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIColor;

public class ColorUtils {

    public static JSIColor dmnFromWB(final String colorString) {
        final JSIColor result = JSIColor.newInstance();

        try {
            final int i = Integer.decode(colorString);
            result.setRed((i >> 16) & 0xFF);
            result.setGreen((i >> 8) & 0xFF);
            result.setBlue(i & 0xFF);
        } catch (final NumberFormatException nfe) {
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
