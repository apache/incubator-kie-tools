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


package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import static org.junit.Assert.assertTrue;

public class GeneratorAssertions {

    public static void assertDraggable(final String raw) {
        assertTrue(raw.contains("setDraggable(false)"));
    }

    public static void assertID(final String raw,
                                final String id) {
        assertTrue(raw.contains("setID(" + formatString(id) + ")"));
    }

    public static void assertAlpha(final String raw,
                                   final double value) {
        assertTrue(raw.contains("setAlpha(" + formatDouble(value) + ")"));
    }

    public static void assertX(final String raw,
                               final double value) {
        assertTrue(raw.contains("setX(" + formatDouble(value) + ")"));
    }

    public static void assertY(final String raw,
                               final double value) {
        assertTrue(raw.contains("setY(" + formatDouble(value) + ")"));
    }

    public static void assertScale(final String raw,
                                   final double sx,
                                   final double sy) {
        assertTrue(raw.contains("setScale(" + formatDouble(sx) + "," + formatDouble(sy) + ")"));
    }

    public static void assertFillColor(final String raw,
                                       final String color) {
        assertTrue(raw.contains("setFillColor(" + formatString(color) + ")"));
    }

    public static void assertFillAlpha(final String raw,
                                       final double value) {
        assertTrue(raw.contains("setFillAlpha(" + formatDouble(value) + ")"));
    }

    public static void assertStrokeColor(final String raw,
                                         final String color) {
        assertTrue(raw.contains("setStrokeColor(" + formatString(color) + ")"));
    }

    public static void assertStrokeAlpha(final String raw,
                                         final double value) {
        assertTrue(raw.contains("setStrokeAlpha(" + formatDouble(value) + ")"));
    }

    public static void assertStrokeWidth(final String raw,
                                         final double value) {
        assertTrue(raw.contains("setStrokeWidth(" + formatDouble(value) + ")"));
    }

    public static void assertListening(final String raw,
                                       final boolean listening) {
        assertTrue(raw.contains("setListening(" + formatBoolean(listening) + ")"));
    }

    public static void assertStrokeDashArray(final String raw,
                                             final String value) {
        assertTrue(raw.contains("setDashArray(" + value + ")"));
    }

    public static String formatDouble(final double value) {
        return SVGGeneratorFormatUtils.format(value);
    }

    public static String formatString(final String value) {
        return "\"" + value + "\"";
    }

    public static String formatBoolean(final boolean value) {
        return Boolean.toString(value);
    }
}
