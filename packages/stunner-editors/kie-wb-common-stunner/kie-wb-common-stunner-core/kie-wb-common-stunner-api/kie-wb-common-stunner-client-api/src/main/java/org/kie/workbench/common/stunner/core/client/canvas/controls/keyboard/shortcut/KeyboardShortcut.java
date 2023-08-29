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


package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut;

import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * Interface representing an action that is invoked by pressing some Keys Combination on the keyboard
 * @param <H>
 */
public interface KeyboardShortcut<H extends CanvasHandler> {

    /**
     * @param pressedKeys
     * @return true if pressed keys should invoke the action
     */
    boolean matchesPressedKeys(final KeyboardEvent.Key... pressedKeys);

    /**
     * @param selectedElement
     * @return true if action can be executed for the selected element
     */
    boolean matchesSelectedElement(final Element selectedElement);

    void executeAction(final H canvasHandler,
                       final String selectedNodeId);

    KeyboardEvent.Key[] getKeyCombination();

    String getLabel();

    default KeyboardShortcutsApiOpts getOpts() {
        return KeyboardShortcutsApiOpts.DEFAULT;
    }
}
