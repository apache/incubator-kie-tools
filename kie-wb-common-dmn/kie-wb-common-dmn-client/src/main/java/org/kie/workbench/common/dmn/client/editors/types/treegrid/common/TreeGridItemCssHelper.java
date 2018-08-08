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

package org.kie.workbench.common.dmn.client.editors.types.treegrid.common;

import elemental2.dom.Element;

public class TreeGridItemCssHelper {

    private static final String RIGHT_ARROW_CSS_CLASS = "fa-angle-right";
    private static final String DOWN_ARROW_CSS_CLASS = "fa-angle-down";

    public static void asRightArrow(final Element element) {
        element.classList.add(RIGHT_ARROW_CSS_CLASS);
        element.classList.remove(DOWN_ARROW_CSS_CLASS);
    }

    public static void asDownArrow(final Element element) {
        element.classList.remove(RIGHT_ARROW_CSS_CLASS);
        element.classList.add(DOWN_ARROW_CSS_CLASS);
    }

    public static boolean isRightArrow(final Element element) {
        return element.classList.contains(RIGHT_ARROW_CSS_CLASS);
    }
}
