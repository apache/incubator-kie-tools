/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@Dependent
public class LogMagnetsDevCommand extends AbstractSelectionDevCommand {

    private static Logger LOGGER = Logger.getLogger(LogMagnetsDevCommand.class.getName());

    @Inject
    public LogMagnetsDevCommand(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public String getText() {
        return "Log magnets";
    }

    @Override
    protected void execute(final Collection<Element<? extends View<?>>> items) {
        for (final Element<? extends View<?>> item : items) {
            logTask(() -> logMagnets(item));
        }
    }

    private void logMagnets(Element<? extends View<?>> element) {
        if (null != element.asNode()) {
            final Shape shape = getCanvasHandler().getCanvas().getShape(element.getUUID());
            final WiresShape wiresShape = (WiresShape) shape.getShapeView();
            final MagnetManager.Magnets magnets = wiresShape.getMagnets();
            if (null != magnets) {
                log("---- Magnets [" + element.getUUID() + "] ------");
                for (int i = 0; i < magnets.size(); i++) {
                    WiresMagnet magnet = magnets.getMagnet(i);
                    NFastArrayList<WiresConnection> connections = magnet.getConnections();
                    WiresConnector connector = null;
                    if (null != connections && !connections.isEmpty()) {
                        WiresConnection connection = connections.iterator().next();
                        connector = connection.getConnector();
                    }
                    log("[" + i + "] - " + connector);
                }
                log("-------------------------------------------------------");
            } else {
                log("No magnets are set.");
            }
        }
    }

    private static void log(final String message) {
        LOGGER.log(Level.INFO, message);
    }
}
