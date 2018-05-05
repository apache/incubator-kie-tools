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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@Dependent
public class LogSelectedItemsDevCommand extends AbstractSelectionDevCommand {

    private static Logger LOGGER = Logger.getLogger(LogSelectedItemsDevCommand.class.getName());

    protected LogSelectedItemsDevCommand() {
        this(null);
    }

    @Inject
    public LogSelectedItemsDevCommand(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    protected void execute(final Collection<Element<? extends View<?>>> items) {
        if (items.isEmpty()) {
            log("No items selected");
        } else {
            log("***************** Selected items *****************");
            int i = 0;
            for (Element<? extends View<?>> e : items) {
                log("[" + i++ + "] " + e.getUUID());
            }
            log("**************************************************");
        }
    }

    @Override
    public String getText() {
        return "Log selected items";
    }

    private static void log(final String message) {
        LOGGER.log(Level.INFO,
                   message);
    }
}
