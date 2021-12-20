/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.menu.dev.impl;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.client.widgets.menu.dev.AbstractMenuDevCommand;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class AbstractSelectionDevCommand extends AbstractMenuDevCommand {

    private static Logger LOGGER = Logger.getLogger(AbstractSelectionDevCommand.class.getName());

    protected AbstractSelectionDevCommand(final SessionManager sessionManager) {
        super(sessionManager);
    }

    protected abstract void execute(Collection<Element<? extends View<?>>> items);

    @Override
    @SuppressWarnings("unchecked")
    public void execute() {
        try {
            boolean found = false;
            final EditorSession session = (EditorSession) getSession();
            final Collection<String> selectedItems = session.getSelectionControl().getSelectedItems();
            if (null != selectedItems) {

                final String uuid = selectedItems.stream().findFirst().orElse(null);
                if (null != uuid) {
                    execute(selectedItems.stream()
                                    .map(this::getViewElement)
                                    .collect(Collectors.toList()));
                    found = true;
                }
            }
            if (!found) {
                LOGGER.log(Level.WARNING,
                           "No items selected.");
            }
        } catch (final ClassCastException e) {
            LOGGER.log(Level.WARNING,
                       "Session is not an instance of ClientFullSession");
        }
    }

    @SuppressWarnings("unchecked")
    private Element<? extends View<?>> getViewElement(final String uuid) {
        return getCanvasHandler().getGraphIndex().get(uuid);
    }
}
