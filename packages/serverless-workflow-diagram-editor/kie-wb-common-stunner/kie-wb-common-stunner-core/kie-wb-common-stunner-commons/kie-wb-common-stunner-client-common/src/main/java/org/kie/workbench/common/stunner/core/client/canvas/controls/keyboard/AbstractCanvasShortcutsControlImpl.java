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


package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.Iterator;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.KeyboardShortcut;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractCanvasShortcutsControlImpl extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements CanvasControl.SessionAware<EditorSession>,
                   KeyboardControl.KeyShortcutCallback {

    protected final Instance<KeyboardShortcut> keyboardShortcutActions;

    protected EditorSession editorSession;

    @Inject
    public AbstractCanvasShortcutsControlImpl(final Instance<KeyboardShortcut> keyboardShortcutActions) {
        this.keyboardShortcutActions = keyboardShortcutActions;
    }

    @Override
    public void register(Element element) {
    }

    @Override
    public void bind(final EditorSession session) {
        this.editorSession = session;
        session.getKeyboardControl().addKeyShortcutCallback(this);
    }

    @Override
    public void onKeyShortcut(final KeyboardEvent.Key... keys) {
        if (selectedNodeId() != null) {
            final Iterator<KeyboardShortcut> keyboardShortcutActionsIterator = keyboardShortcutActions.iterator();
            while (keyboardShortcutActionsIterator.hasNext()) {
                final KeyboardShortcut action = keyboardShortcutActionsIterator.next();
                if (action.matchesPressedKeys(keys) && action.matchesSelectedElement(selectedNodeElement())) {
                    action.executeAction(canvasHandler, selectedNodeId());
                }
            }
        }
    }

    public String selectedNodeId() {
        if (editorSession != null && editorSession.getSelectionControl().getSelectedItems().size() == 1) {
            return editorSession.getSelectionControl().getSelectedItems().iterator().next();
        } else {
            return null;
        }
    }

    public Element selectedNodeElement() {
        if (selectedNodeId() != null) {
            return CanvasLayoutUtils.getElement(canvasHandler, selectedNodeId());
        } else {
            return null;
        }
    }
}
