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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

/**
 * A provider of a {@link String} property displayed as the caption for all {@link Shape}'s handled
 * by the {@link BaseCanvasHandler} and edited "in-place" by the {@link CanvasInPlaceTextEditorControl}
 */
public interface TextPropertyProvider {

    /**
     * Returns the priority of the {@link TextPropertyProvider}. {@link Integer#MIN_VALUE} is considered the
     * highest priority, whereas {@link Integer#MAX_VALUE} is considered the lowest priority and is used by
     * the default implementation as a "catch all".
     * @return
     */
    int getPriority();

    /**
     * Returns whether the {@link TextPropertyProvider} supports a given {@link Element}.
     * @param element The element to check.
     * @return true if supported otherwise false.
     */
    boolean supports(final Element<? extends Definition> element);

    /**
     * Gets the text on the initialised {@link Element}. {@see TextPropertyProvider.initialiseForReading} which should be called first.
     * @param element The element for which to get the caption.
     * @return The text to be shown as the caption.
     */
    String getText(final Element<? extends Definition> element);

    /**
     * Sets the text on the initialised {@link Element}. {@see TextPropertyProvider.initialiseForWriting} which should be called first.
     * @param canvasHandler Required by {@link CommandManager} when executing {@link Command}'s to update the {@link Element}'s property.
     * @param commandManager A {@link CommandManager} with which to execute update commands.
     * @param element The element for which to update the property represented by the caption.
     * @param text The text shown as the caption.
     */
    void setText(final AbstractCanvasHandler canvasHandler,
                 final CanvasCommandManager<AbstractCanvasHandler> commandManager,
                 final Element<? extends Definition> element,
                 final String text);
}
