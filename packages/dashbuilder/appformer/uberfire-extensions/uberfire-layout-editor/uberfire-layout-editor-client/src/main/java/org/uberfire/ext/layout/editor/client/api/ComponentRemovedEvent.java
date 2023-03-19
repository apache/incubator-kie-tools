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

package org.uberfire.ext.layout.editor.client.api;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public class ComponentRemovedEvent {

    private LayoutComponent layoutComponent;
    private Boolean fromMove;

    public ComponentRemovedEvent(LayoutComponent layoutComponent) {
        this.layoutComponent = layoutComponent;
    }

    public ComponentRemovedEvent(LayoutComponent layoutComponent,
                                 Boolean fromMove) {

        this.layoutComponent = layoutComponent;
        this.fromMove = fromMove;
    }

    public LayoutComponent getLayoutComponent() {
        return layoutComponent;
    }

    public Boolean getFromMove() {
        return fromMove;
    }
}
