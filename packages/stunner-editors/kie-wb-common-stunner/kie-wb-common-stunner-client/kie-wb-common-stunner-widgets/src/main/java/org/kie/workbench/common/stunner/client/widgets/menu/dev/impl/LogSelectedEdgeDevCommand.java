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

package org.kie.workbench.common.stunner.client.widgets.menu.dev.impl;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Point2DArray;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StunnerLogger;

@Dependent
public class LogSelectedEdgeDevCommand extends AbstractSelectionDevCommand {

    private static Logger LOGGER = Logger.getLogger(LogSelectedEdgeDevCommand.class.getName());

    protected LogSelectedEdgeDevCommand() {
        this(null);
    }

    @Inject
    public LogSelectedEdgeDevCommand(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public String getText() {
        return "Log Selected Edge";
    }

    @Override
    protected void execute(final Collection<Element<? extends View<?>>> items) {
        logTask(() -> logEdges(items));
    }

    private void logEdges(final Collection<Element<? extends View<?>>> items) {
        if (1 == items.size()) {
            final Element<? extends View<?>> e = items.iterator().next();
            if (null != e.asEdge()) {
                logEdge(e.asEdge());
            }
        }
    }

    private void logEdge(final Edge<? extends View<?>, Node> edge) {
        StunnerLogger.log(edge);
        if (edge.getContent() instanceof HasControlPoints) {

            final ControlPoint[] controlPoints = ((HasControlPoints) edge.getContent()).getControlPoints();
            String s = "*** CPS = ";
            if (null != controlPoints) {
                for (ControlPoint cp : controlPoints) {
                    s += " [" + format(cp.getLocation()) + "] ";
                }
                s += " *** ";
            } else {
                s += " [Empty] ";
            }
            log(s);

            String s1 = "*** CPS[Lienzo] = ";
            final Shape shape = getCanvasHandler().getCanvas().getShape(edge.getUUID());
            final WiresConnectorView connectorView = (WiresConnectorView) shape.getShapeView();
            final Point2DArray controlPoints1 = connectorView.getControlPoints();
            if (null != controlPoints1) {
                for (int i = 0; i < controlPoints1.size(); i++) {
                    com.ait.lienzo.client.core.types.Point2D point2D = controlPoints1.get(i);
                    s1 += " [" + point2D.getX() + "," + point2D.getY() + "] ";
                }
                s1 += " *** ";
            } else {
                s1 += " [Empty] ";
            }
            log(s1);
        }
    }

    private static String format(final Point2D location) {
        if (null != location) {
            return location.getX() + "," + location.getY();
        } else {
            return "null";
        }
    }

    private static void log(final String message) {
        LOGGER.log(Level.INFO,
                   message);
    }
}
