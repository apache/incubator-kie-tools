/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.layout.editor.api.css;

/**
 * Enumeration of the different value types supported by a {@link org.uberfire.ext.layout.editor.api.css.CssProperty}.
 */
public enum CssValueType {

    TEXT,
    LENGTH,     // e.g: "100px", "50em", "30in"
    PERCENTAGE, // e.g: "50%" ,
    NUMBER,     // e.g: "1.2"
    COLOR,     // e.g: "#FFFFFF", "white"
    URI;        // e.g: "http://host/image.png"
}
