/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.session;

import javax.enterprise.inject.Instance;

import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.AbstractCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.KeyboardShortcut;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;

import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyPress;

public class KogitoAbstractCanvasShortcutsControlImpl extends AbstractCanvasShortcutsControlImpl {

    public KogitoAbstractCanvasShortcutsControlImpl(Instance<KeyboardShortcut> keyboardShortcutActions) {
        super(keyboardShortcutActions);
    }

    @Override
    public void bind(EditorSession session) {
        this.editorSession = session;
        for (final KeyboardShortcut action : keyboardShortcutActions) {
            session.getKeyboardControl().addKeyShortcutCallback(new KogitoKeyPress(
                    action.getKeyCombination(),
                    "Append Node | " + action.getLabel(),
                    () -> executeAction(action),
                    action.getOpts()));
        }
    }

    private void executeAction(final KeyboardShortcut action) {
        if (selectedNodeId() != null) {
            if (action.matchesSelectedElement(selectedNodeElement())) {
                action.executeAction(canvasHandler, selectedNodeId());
            }
        }
    }
}
