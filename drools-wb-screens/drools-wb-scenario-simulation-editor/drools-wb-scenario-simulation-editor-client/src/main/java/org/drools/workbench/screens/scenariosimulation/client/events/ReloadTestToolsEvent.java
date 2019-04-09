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

import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ReloadTestToolsEventHandler;

/**
 * <code>GwtEvent</code> to <b>reload</b> the <code>TestToolsView</code>
 */
public class ReloadTestToolsEvent extends GwtEvent<ReloadTestToolsEventHandler> {

    public static Type<ReloadTestToolsEventHandler> TYPE = new Type<>();

    private final boolean disable;

    private final boolean openDock;

    /**
     * Fire this event to reload the right panel content. With this constructor the dock <b>does not</b> open if it is closed
     *
     * @param disable set this to <code>true</code> to <b>also</b> disable the panel.
     */
    public ReloadTestToolsEvent(boolean disable) {
        this.disable = disable;
        openDock = false;
    }

    /**
     * Fire this event to reload the right panel content.
     *
     * @param disable set this to <code>true</code> to <b>also</b> disable the panel.
     * @param openDock set this to <code>true</code> to <b>also</b> open the dock in case it is closed
     */
    public ReloadTestToolsEvent(boolean disable, boolean openDock) {
        this.disable = disable;
        this.openDock = openDock;
    }


    @Override
    public Type<ReloadTestToolsEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isDisable() {
        return disable;
    }

    public boolean isOpenDock() {
        return openDock;
    }

    @Override
    protected void dispatch(ReloadTestToolsEventHandler handler) {
        handler.onEvent(this);
    }
}
