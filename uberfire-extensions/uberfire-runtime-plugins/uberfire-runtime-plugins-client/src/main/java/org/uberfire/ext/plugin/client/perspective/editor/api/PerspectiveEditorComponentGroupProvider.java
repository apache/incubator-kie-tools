/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.api;

import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteGroupProvider;

/**
 * Any class implementing this interface class is used to add an instance of {@link LayoutDragComponentGroup} to
 * the Perspective Editor's component palette.
 */
public interface PerspectiveEditorComponentGroupProvider extends LayoutComponentPaletteGroupProvider,
                                                                 Comparable {

    /**
     * How important is this group in relation to other groups available. For example, more relevant groups
     * are displayed first in the component palette.
     */
    default Integer getOrder() {
        return 0;
    }

    @Override
    default int compareTo(Object o) {
        if (this == o) {
            return 0;
        }
        if (o == null) {
            return -1;
        }
        try {
            PerspectiveEditorComponentGroupProvider other = (PerspectiveEditorComponentGroupProvider) o;
            if (other.getOrder() == this.getOrder()) {
                return this.getName().compareTo(other.getName());
            }
            return this.getOrder().compareTo(other.getOrder()) * -1;
        } catch (ClassCastException e) {
            return -1;
        }
    }
}
