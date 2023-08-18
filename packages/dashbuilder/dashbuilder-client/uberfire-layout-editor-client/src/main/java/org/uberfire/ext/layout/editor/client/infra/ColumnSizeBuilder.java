/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.infra;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ColumnSizeBuilder {

    private ColumnSizeBuilder() {
        // noop
    }

    public static String buildPFGridClasses(int span) {
        return Arrays.asList(
                buildMdClass(span),
                buildLgClass(span),
                buildXsClass(span),
                buildSmClass(span)).stream().collect(Collectors.joining(" "));
    }

    private static String buildSmClass(int span) {
        int smSize = 12;
        if (span <= 6) {
            smSize = 6;
        }
        return "pf-m-" + smSize + "-col-on-sm";
    }

    private static String buildLgClass(int span) {
        return "pf-m-" + span + "-col-on-xl";
    }

    private static String buildMdClass(int span) {
        return "pf-m-" + span + "-col-on-md";
    }

    private static String buildXsClass(int span) {
        return "pf-m-12-col-on-xs";
    }

}
