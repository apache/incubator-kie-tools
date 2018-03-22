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

package org.kie.workbench.common.stunner.core.client.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class StunnerClientLogger {

    private static Logger LOGGER = Logger.getLogger(StunnerClientLogger.class.getName());

    public static String getErrorMessage(final ClientRuntimeError error) {
        final String message = error.getMessage();
        final Throwable t1 = error.getThrowable();
        final Throwable t2 = t1 != null ? t1.getCause() : null;
        if (null != t2) {
            return t2.getMessage();
        } else if (null != t1) {
            return t1.getMessage();
        }
        return null != message ? message : " -- No message -- ";
    }

    public static void logDefinition(final DefinitionManager definitionManager,
                                     final Object def) {
        final DefinitionAdapter<Object> defAdapter =
                definitionManager.adapters().registry().getDefinitionAdapter(def.getClass());
        final String id = defAdapter.getId(def);
        final String category = defAdapter.getCategory(def);
        final String description = defAdapter.getDescription(def);
        final String title = defAdapter.getTitle(def);
        final Set<String> labels = defAdapter.getLabels(def);
        final Set<Object> propertiesVisited = new HashSet<>();
        GWT.log("");
        GWT.log("********************************************************");
        GWT.log("ID = " + id);
        GWT.log("CATEGORY = " + category);
        GWT.log("DESC = " + description);
        GWT.log("TITLE = " + title);
        GWT.log("LABELS = " + labels);
        defAdapter.getPropertySets(def)
                .forEach(propSet -> logPropertySet(definitionManager,
                                                   propSet,
                                                   propertiesVisited));
        defAdapter.getProperties(def)
                .stream()
                .filter(prop -> !propertiesVisited.contains(prop))
                .forEach(prop -> logProperty(definitionManager,
                                             prop,
                                             new HashSet<>()));
        GWT.log("********************************************************");
        GWT.log("");
    }

    public static void logPropertySet(final DefinitionManager definitionManager,
                                      final Object propSet,
                                      final Set<Object> propertiesVisited) {
        final PropertySetAdapter<Object> adapter =
                definitionManager.adapters().registry().getPropertySetAdapter(propSet.getClass());
        final String id = adapter.getId(propSet);
        final String name = adapter.getName(propSet);
        GWT.log("    ==================================================");
        GWT.log("    ID = " + id);
        GWT.log("    NAME = " + name);
        final Set<?> properties = adapter.getProperties(propSet);
        properties.forEach(prop -> logProperty(definitionManager,
                                               prop,
                                               propertiesVisited));
        GWT.log("    ==================================================");
    }

    public static void logProperty(final DefinitionManager definitionManager,
                                   final Object prop,
                                   final Set<Object> propertiesVisited) {
        final PropertyAdapter<Object, ?> adapter =
                definitionManager.adapters().registry().getPropertyAdapter(prop.getClass());
        final String id = adapter.getId(prop);
        final String caption = adapter.getCaption(prop);
        final String description = adapter.getDescription(prop);
        final PropertyType type = adapter.getType(prop);
        final Object value = adapter.getValue(prop);
        GWT.log("    -------------------------------------------------");
        GWT.log("    ID = " + id);
        GWT.log("    CAPTION = " + caption);
        GWT.log("    DESC = " + description);
        GWT.log("    TYPE = " + type);
        GWT.log("    VALUE = " + value);
        GWT.log("    -------------------------------------------------");
        propertiesVisited.add(prop);
    }

    public static void logBounds(final Element<? extends View<?>> item) {
        final Bounds bounds = item.getContent().getBounds();
        final Bounds.Bound ul = bounds.getUpperLeft();
        final Bounds.Bound lr = bounds.getLowerRight();
        LOGGER.log(Level.FINE,
                   "Bounds for [" + item.getUUID() + "] ARE " +
                           "{ UL=[" + ul.getX() + ", " + ul.getY() + "] " +
                           "LR=[ " + lr.getX() + ", " + lr.getY() + "] }");
        LOGGER.log(Level.FINE,
                   "Bound attributes for [" + item.getUUID() + "] ARE " +
                           "[X=" + ul.getX() + ", Y=" + ul.getY() + "] " +
                           "[W=[ " + (lr.getX() - ul.getX()) +
                           ", H=" + (lr.getY() - ul.getY()) + "] }");
    }

    public static void logSessionInfo(final AbstractClientSession session) {
        log("************ Session Info ****************");
        if (null != session) {
            log("Session = " + session.toString());
            log("Canvas = " + session.getCanvas().toString());
            if (null != session.getCanvasHandler()) {
                final CanvasHandler canvasHandler = session.getCanvasHandler();
                log("CanvasHandler = " + canvasHandler.toString());
                final Diagram diagram = canvasHandler.getDiagram();
                if (null != diagram) {
                    log("Diagram name = " + diagram.getName());
                    log("Graph uuid = " + (null != diagram.getGraph() ? diagram.getGraph().getUUID() : "null"));
                    final Metadata metadata = diagram.getMetadata();
                    if (null != metadata) {
                        log("Metadata defSetId = " + metadata.getDefinitionSetId());
                        log("Metadata shapeSetId = " + metadata.getShapeSetId());
                        log("Metadata canvas root = " + metadata.getCanvasRootUUID());
                        log("Metadata title = " + metadata.getTitle());
                    } else {
                        log("Metadata = null");
                    }
                } else {
                    log("Diagram = null");
                }
            } else {
                log("CanvasHandler = null");
            }
            if (session instanceof ClientFullSession) {
                logFullSessionInfo((ClientFullSession) session);
            }
        } else {
            log("Session is null");
        }
        log("******************************************");
    }

    private static void logFullSessionInfo(final ClientFullSession session) {
        final ElementBuilderControl<AbstractCanvasHandler> builderControl = session.getBuilderControl();
        final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager =
                session.getCommandManager();
        log("Builder control = " + (null != builderControl ? builderControl.toString() : "null"));
        log("Canvas command mgr = " + (null != canvasCommandManager ? canvasCommandManager.toString() : "null"));
    }

    @SuppressWarnings("unchecked")
    public static void logCommandHistory(final ClientFullSession session) {
        if (null != session) {
            final List<Command<AbstractCanvasHandler, CanvasViolation>> history =
                    session.getCommandRegistry().getCommandHistory();
            logCommandHistory(history);
        }
    }

    private static void logCommandHistory(final List<Command<AbstractCanvasHandler, CanvasViolation>> history) {
        log("**** COMMAND HISTORY START *********");
        if (null == history) {
            log("History is null");
        } else {
            final int[] x = {0};
            history.stream().forEach(command -> {
                logCommand(x[0],
                           command);
                x[0]++;
            });
            log(" ( FOUND " + x[0] + " ENTRIES )");
        }
        log("**** COMMAND HISTORY END *********");
    }

    private static void logCommand(final int count,
                                   final Command<AbstractCanvasHandler, CanvasViolation> command) {
        if (null == command) {
            log("Command is null");
        } else {
            log("Command [" + count + "] => " + command.toString());
        }
    }

    public static void switchLogLevel() {
        final Level level = Logger.getLogger("org.kie.workbench.common.stunner").getLevel();
        final int idx = getLevelIndex(level);
        final Level newLevel = (idx > -1 && ((idx + 1) < LOG_LEVELS.length)) ? LOG_LEVELS[idx + 1] : LOG_LEVELS[0];
        GWT.log("*** Switching to log level: " + newLevel.toString());
        Logger.getLogger("org.kie.workbench.common.stunner").setLevel(newLevel);
    }

    private static final Level[] LOG_LEVELS = new Level[]{
            Level.FINEST, Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE
    };

    private static int getLevelIndex(final Level level) {
        int idx = -1;
        if (null != level) {
            for (final Level l : LOG_LEVELS) {
                if (level.equals(l)) {
                    return idx + 1;
                }
                idx++;
            }
        }
        return idx;
    }

    public static void log(final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(Level.INFO,
                       message);
        }
    }

    public static void log(final Level level,
                           final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
