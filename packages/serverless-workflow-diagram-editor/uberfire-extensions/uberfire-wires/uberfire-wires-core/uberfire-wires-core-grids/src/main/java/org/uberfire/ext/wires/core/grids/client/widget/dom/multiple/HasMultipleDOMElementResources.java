/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.dom.multiple;

import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;

/**
 * Extension for Factories and Columns that display multiple DOMElements to edit the content of cells.
 */
public interface HasMultipleDOMElementResources extends HasDOMElementResources {

    /**
     * Initialises additional resources. This is invoked at the
     * start of the render phase if a column is visible.
     */
    void initialiseResources();

    /**
     * Destroys additional resources that are not required. This is invoked at the
     * end of the render phase on columns that are partially visible. Some of the
     * resources acquired in initialisation may not have been used for rendering
     * and can be released.
     */
    void freeUnusedResources();
}
