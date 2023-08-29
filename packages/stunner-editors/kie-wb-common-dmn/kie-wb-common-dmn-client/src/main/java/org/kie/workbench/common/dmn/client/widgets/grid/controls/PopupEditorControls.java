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

package org.kie.workbench.common.dmn.client.widgets.grid.controls;

import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;

public interface PopupEditorControls extends IsElement,
                                             CanBeClosedByKeyboard {

    /**
     * Returns the {@link String} for the {@link PopupEditorControls} title used to to edit properties.
     * @return null if no title is to be shown.
     */
    default String getPopoverTitle() {
        return null;
    }

    /**
     * Shows the {@link PopupEditorControls}.
     */
    void show();

    /**
     * Hides the {@link PopupEditorControls}.
     */
    void hide();
}
