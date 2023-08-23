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

package org.kie.workbench.common.dmn.client.editors.types.common;

import elemental2.dom.Element;

public class HiddenHelper {

    public static final String HIDDEN_CSS_CLASS = "hidden";

    public static void hide(final Element element) {
        element.classList.add(HIDDEN_CSS_CLASS);
    }

    public static void show(final Element element) {
        element.classList.remove(HIDDEN_CSS_CLASS);
    }

    public static boolean isHidden(final Element element) {
        return element.classList.contains(HIDDEN_CSS_CLASS);
    }
}
