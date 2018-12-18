/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.selectpicker;

import elemental2.dom.Node;

public class ElementHelper {

    public static void insertAfter(final Node newNode,
                                   final Node referenceNode) {
        referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
    }

    public static void insertBefore(final Node newNode,
                                    final Node referenceNode) {
        referenceNode.parentNode.insertBefore(newNode, referenceNode);
    }

    public static void remove(final Node element) {
        element.parentNode.removeChild(element);
    }
}
