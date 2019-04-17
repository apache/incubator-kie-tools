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

package org.kie.workbench.common.forms.jbpm.client.rendering.util;

import com.google.gwt.i18n.client.NumberFormat;

public class DocumentSizeHelper {

    private static final String SIZE_UNITS[] = new String[]{"bytes", "Kb", "Mb", "Gb", "Tb"};

    public static String getFormattedDocumentSize(double size) {
        String result = "";

        int position;

        for (position = 0; position < SIZE_UNITS.length && size > 1024; position++) {
            size = size / 1024;
        }

        result = NumberFormat.getDecimalFormat().format(size) + " " + SIZE_UNITS[position];

        return result;
    }
}
