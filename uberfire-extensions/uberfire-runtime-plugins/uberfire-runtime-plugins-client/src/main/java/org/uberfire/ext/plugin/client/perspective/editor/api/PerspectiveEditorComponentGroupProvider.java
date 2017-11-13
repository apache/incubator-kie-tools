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

import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;

/**
 * Any class implementing this interface class is used to add an instance of {@link LayoutDragComponentGroup} to
 * the Perspective Editor's component palette.
 */
public interface PerspectiveEditorComponentGroupProvider {

    /**
     * Return the name of the component group displayed in the component palette.
     */
    String getName();

    /**
     * Get the {@link LayoutDragComponentGroup} containing the {@link LayoutDragComponent} instances
     * listed under the group's category in the component palette.
     */
    LayoutDragComponentGroup getInstance();
}
