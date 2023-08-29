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
package org.kie.workbench.common.dmn.client.session;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.AbstractCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;

/**
 * Acts as a single implementation of {@link AbstractCanvasShortcutsControlImpl} for use
 * in {@link DMNEditorSession} that delegates everything to the correct {@link Alternative} implementation
 * (for Business Central or Kogito environments). {@link ManagedSession} uses {@link ManagedInstance.select()} that does
 * not honour {@link Alternative} annotations. Consequentially attempts to register {@link AbstractCanvasShortcutsControlImpl}
 * directly leads to multiple bean implementation CDI exceptions at runtime (when the lookup is performed).
 */
@Dependent
public class DMNCanvasShortcutsControlProxy implements DMNCanvasShortcutsControl {

    private final AbstractCanvasShortcutsControlImpl delegate;

    @Inject
    public DMNCanvasShortcutsControlProxy(final @DMNEditor AbstractCanvasShortcutsControlImpl delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public void init(final AbstractCanvasHandler context) {
        delegate.init(context);
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

    @Override
    public void bind(final EditorSession session) {
        delegate.bind(session);
    }

    @Override
    public void onKeyShortcut(final KeyboardEvent.Key... keys) {
        delegate.onKeyShortcut(keys);
    }

    @Override
    public void onKeyUp(final KeyboardEvent.Key key) {
        delegate.onKeyUp(key);
    }
}
