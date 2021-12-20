/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.dom;

/**
 * Interface declaring the need for additional resources. This interface is used during the grid rendering phase.
 * Columns may need additional resources to render their content. For example the DOM element overlays need to
 * initialise a DOM element and be able to release these when the column is no longer rendered.
 */
public interface HasDOMElementResources {

    /**
     * Destroys all additional resources created during initialisation. This is
     * invoked at the end of the render phase if the column is not visible.
     */
    void destroyResources();
}
