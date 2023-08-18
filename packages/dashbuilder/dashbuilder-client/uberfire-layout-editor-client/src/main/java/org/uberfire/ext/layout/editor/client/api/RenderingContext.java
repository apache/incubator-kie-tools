/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.layout.editor.client.api;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

/**
 * This class provides the context required to render drag components within the layout.
 */
public class RenderingContext {

    private LayoutComponent component;
    private Widget container;

    public RenderingContext(LayoutComponent component,
                            Widget container) {
        this.component = component;
        this.container = container;
    }

    public RenderingContext(LayoutComponent component) {
        this.component = component;
    }

    public LayoutComponent getComponent() {
        return component;
    }

    public Widget getContainer() {
        return container;
    }
}
