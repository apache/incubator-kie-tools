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

package org.uberfire.wbtest.client.resize;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * A widget that reports its current size as its textual content. The text is updated on every call to
 * {@link #onResize()}.
 */
public class ResizeTestWidget extends Label implements RequiresResize,
                                                       ProvidesResize {

    public static String DEBUG_ID_PREFIX = "ResizeTestWidget-";

    public ResizeTestWidget(String id) {
        ensureDebugId(DEBUG_ID_PREFIX + id);
        setText("no onResize yet");
    }

    @Override
    public void onResize() {
        setText(getOffsetWidth() + "x" + getOffsetHeight());
    }
}