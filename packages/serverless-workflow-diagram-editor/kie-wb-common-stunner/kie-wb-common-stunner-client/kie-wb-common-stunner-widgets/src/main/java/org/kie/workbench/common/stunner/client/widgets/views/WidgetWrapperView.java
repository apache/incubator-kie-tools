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


package org.kie.workbench.common.stunner.client.widgets.views;

import io.crysknife.client.IsElement;

/**
 * Just a simple view that wraps any widget.
 */
public interface WidgetWrapperView extends IsElement {

    /**
     * Clears the current widget, if any, and sets the new widget to wrap.
     */
    WidgetWrapperView setWidget(final IsElement widget);

    /**
     * Clears the wrapped widget.
     */
    WidgetWrapperView clear();
}
