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

package org.kie.workbench.common.stunner.core.client.shape;

public enum TextWrapperStrategy {
    /**
     * Wraps when a line exceeds the width of the provided boundary.
     */
    BOUNDS_AND_LINE_BREAKS,

    /**
     * Wraps when a line exceeds the width of the provided boundary. This is the default behaviour.
     */
    BOUNDS,

    /**
     * Wraps on line breaks.
     */
    LINE_BREAK,

    /**
     * No wrap.
     */
    NO_WRAP,

    /**
     * Wraps when exceeds the width. Truncates when the text exceeds the width and height appending "...".
     */
    TRUNCATE
}
