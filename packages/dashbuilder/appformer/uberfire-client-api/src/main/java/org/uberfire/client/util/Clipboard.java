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

package org.uberfire.client.util;

import javax.enterprise.context.Dependent;

import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTextAreaElement;

@Dependent
public class Clipboard {

    public boolean copy(final HTMLInputElement element) {
        element.select();
        return copy();
    }

    public boolean copy(final HTMLTextAreaElement element) {
        element.select();
        return copy();
    }

    public native boolean copy() /*-{
        return $doc.execCommand("Copy");
    }-*/;
}
