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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common;

public class DurationHelper {

    private static final String PREFIX = "duration";

    private static final String OPEN_BRACKET = "(";

    private static final String CLOSE_BRACKET = ")";

    private static final String QUOTE = "\"";

    public static String addFunctionCall(final String value) {
        return PREFIX + OPEN_BRACKET + QUOTE + value + QUOTE + CLOSE_BRACKET;
    }

    public static String getFunctionParameter(final String rawValue) {
        return rawValue.replace(PREFIX, "")
                   .replace(CLOSE_BRACKET, "")
                   .replace(OPEN_BRACKET, "")
                   .replace(" ", "")
                   .replace(QUOTE, "");
    }
}
