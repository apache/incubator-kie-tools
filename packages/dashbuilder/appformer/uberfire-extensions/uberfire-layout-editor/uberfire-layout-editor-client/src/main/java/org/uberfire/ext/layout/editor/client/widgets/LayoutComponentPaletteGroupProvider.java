/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.layout.editor.client.widgets;

import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;

/**
 * Provides the {@link LayoutDragComponentGroup} to be displayed on the {@link LayoutDragComponentPalette}
 */
public interface LayoutComponentPaletteGroupProvider {

    /**
     * Return the name of the component group displayed in the component palette.
     */
    String getName();

    /**
     * Get the {@link LayoutDragComponentGroup} containing the {@link LayoutDragComponent} instances
     * listed under the group's category in the component palette.
     */
    LayoutDragComponentGroup getComponentGroup();

}
