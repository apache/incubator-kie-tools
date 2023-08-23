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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common;

import elemental2.dom.HTMLInputElement;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Integer.parseInt;

/**
 * The HTML 5 input `type` "number" does not work as expected with the `max` and `min` attributes.
 * This class provides a workaround for handling maximum and minimum values.
 */
public class MinMaxValueHelper {

    static final String OLD_ATTR = "data-old";

    public static void setupMinMaxHandlers(final HTMLInputElement input) {
        input.onfocusout = (e) -> onFocusOut(input);
        input.onkeydown = (e) -> onKeyDown(input);
        input.onkeyup = (e) -> onKeyUp(input);
    }

    private static boolean onKeyDown(final HTMLInputElement input) {

        final String inputValue = input.value;

        if (isValidValue(input, toNonNullInt(inputValue))) {
            setOldAttribute(input, inputValue);
        }

        return true;
    }

    private static boolean onKeyUp(final HTMLInputElement input) {

        final String oldValue = getOldAttribute(input);
        final int newValue = toNonNullInt(input.value);

        if (!isValidValue(input, newValue)) {
            input.value = oldValue;
        }

        return true;
    }

    private static boolean onFocusOut(final HTMLInputElement input) {
        return onKeyUp(input);
    }

    static boolean isValidValue(final HTMLInputElement input,
                                final int inputValue) {

        final int max = toInteger(input.max, MAX_VALUE);
        final int min = toInteger(input.min, MIN_VALUE);

        return inputValue >= min && inputValue <= max;
    }

    private static String getOldAttribute(final HTMLInputElement input) {
        return input.getAttribute(OLD_ATTR);
    }

    private static void setOldAttribute(final HTMLInputElement input,
                                        final String value) {
        input.setAttribute(OLD_ATTR, value);
    }

    public static Integer toInteger(final String value,
                                    final Integer defaultValue) {
        try {
            return parseInt(value);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int toNonNullInt(final String value) {
        return toInteger(value, 0);
    }
}
