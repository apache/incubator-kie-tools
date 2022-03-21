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
package org.drools.workbench.screens.scenariosimulation.client.events;

import com.google.gwt.event.dom.client.DomEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SaveEditorEventHandler;

/**
 * <code>GwtEvent</code> to <b>save</b> the current status of a <code>CollectionEditorDOMElement</code> to grid
 */
public class SaveEditorEvent extends DomEvent<SaveEditorEventHandler> {

    /**
     * Event type for closeComposite events. Represents the meta-data associated with this
     * event.
     */
    private static final Type<SaveEditorEventHandler> TYPE = new Type<>("save_editor", new SaveEditorEvent());

    /**
     * Gets the event type associated with closeComposite events.
     * @return the handler type
     */
    public static Type<SaveEditorEventHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<SaveEditorEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SaveEditorEventHandler handler) {
        handler.onEvent(this);
    }
}
