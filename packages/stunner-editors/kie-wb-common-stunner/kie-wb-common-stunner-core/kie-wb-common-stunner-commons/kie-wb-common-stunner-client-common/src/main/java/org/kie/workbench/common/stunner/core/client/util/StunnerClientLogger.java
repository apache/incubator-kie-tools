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


package org.kie.workbench.common.stunner.core.client.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class StunnerClientLogger {

    private static Logger LOGGER = Logger.getLogger(StunnerClientLogger.class.getName());

    public static String getErrorMessage(final ClientRuntimeError error) {
        final String message = error.getErrorMessage();
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
        final String id = defAdapter.getId(def).value();
        final String category = defAdapter.getCategory(def);
        final String description = defAdapter.getDescription(def);
        final String title = defAdapter.getTitle(def);
        final String[] labels = defAdapter.getLabels(def);
        final Set<Object> propertiesVisited = new HashSet<>();
        GWT.log("");
        GWT.log("********************************************************");
        GWT.log("ID = " + id);
        GWT.log("CATEGORY = " + category);
        GWT.log("DESC = " + description);
        GWT.log("TITLE = " + title);
        GWT.log("LABELS = " + labels);

        Arrays.stream(defAdapter.getPropertyFields(def))
                .map(field -> defAdapter.getProperty(def, field))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(prop -> !propertiesVisited.contains(prop))
                .forEach(prop -> logProperty(definitionManager,
                                             prop,
                                             new HashSet<>()));
        GWT.log("********************************************************");
        GWT.log("");
    }

    public static void logProperty(final DefinitionManager definitionManager,
                                   final Object prop,
                                   final Set<Object> propertiesVisited) {
        final PropertyAdapter<Object, ?> adapter =
                definitionManager.adapters().registry().getPropertyAdapter(prop.getClass());
        final String id = adapter.getId(prop);
        final String caption = adapter.getCaption(prop);
        final Object value = adapter.getValue(prop);
        GWT.log("    -------------------------------------------------");
        GWT.log("    ID = " + id);
        GWT.log("    CAPTION = " + caption);
        GWT.log("    VALUE = " + value);
        GWT.log("    -------------------------------------------------");
        propertiesVisited.add(prop);
    }

    public static void logBounds(final Element<? extends View<?>> item) {
        final Bounds bounds = item.getContent().getBounds();
        final Bound ul = bounds.getUpperLeft();
        final Bound lr = bounds.getLowerRight();
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

    public static void logSessionInfo(final AbstractSession session) {
        log("************ Session Info ****************");
        if (null != session) {
            log("Session = " + session.toString());
            log("Session id = " + session.getSessionUUID());
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
                        log("Metadata profileId = " + metadata.getProfileId());
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
            if (session instanceof EditorSession) {
                logFullSessionInfo((EditorSession) session);
            }
        } else {
            log("Session is null");
        }
        log("******************************************");
    }

    private static void logFullSessionInfo(final EditorSession session) {
        final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager =
                session.getCommandManager();
        log("Canvas command mgr = " + (null != canvasCommandManager ? canvasCommandManager.toString() : "null"));
    }

    @SuppressWarnings("unchecked")
    public static void logCommandHistory(final EditorSession session) {
        if (null != session) {
            final List<Command<AbstractCanvasHandler, CanvasViolation>> history =
                    session.getCommandRegistry().getHistory();
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

    public static void logTask(Level toLevel,
                               Runnable task) {
        Level level = getStunnerLogger().getLevel();
        getStunnerLogger().setLevel(toLevel);
        task.run();
        getStunnerLogger().setLevel(level);
    }

    public static int switchToLogLevel(Level toLevel) {
        final int idx = getCurrentLoggerLevelIndex();
        getStunnerLogger().setLevel(toLevel);
        return idx;
    }

    public static void switchLogLevel() {
        final int idx = getCurrentLoggerLevelIndex();
        final Level newLevel = (idx > -1 && ((idx + 1) < LOG_LEVELS.length)) ? LOG_LEVELS[idx + 1] : LOG_LEVELS[0];
        GWT.log("*** Switching to log level: " + newLevel.toString());
        getStunnerLogger().setLevel(newLevel);
    }

    private static int getCurrentLoggerLevelIndex() {
        final Level level = getStunnerLogger().getLevel();
        return getLevelIndex(level);
    }

    private static Logger getStunnerLogger() {
        return Logger.getLogger("org.kie.workbench.common.stunner");
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
