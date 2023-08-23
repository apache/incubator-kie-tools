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

package org.kie.workbench.common.dmn.client.editors.expressions.commands;

import java.util.Objects;

import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class UpdateCanvasNodeNameCommand {

    private final SessionManager sessionManager;
    private final DefinitionUtils definitionUtils;
    private final DefaultCanvasCommandFactory canvasCommandFactory;

    public UpdateCanvasNodeNameCommand(final SessionManager sessionManager,
                                       final DefinitionUtils definitionUtils,
                                       final DefaultCanvasCommandFactory canvasCommandFactory) {
        this.sessionManager = sessionManager;
        this.definitionUtils = definitionUtils;
        this.canvasCommandFactory = canvasCommandFactory;
    }

    public void execute(final String nodeUUID,
                        final HasName hasName) {

        final AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
        final Element element = canvasHandler.getGraphIndex().get(nodeUUID);

        if (element.getContent() instanceof Definition) {
            final Definition definition = (Definition) element.getContent();
            final String nameId = definitionUtils.getNameIdentifier(definition.getDefinition());

            final CanvasCommand<AbstractCanvasHandler> command = getCommand(hasName, element, nameId);

            command.execute(canvasHandler);
        }
    }

    CanvasCommand<AbstractCanvasHandler> getCommand(final HasName hasName,
                                                    final Element element,
                                                    final String nameId) {
        final HasName value;
        if (Objects.isNull(hasName)) {
            value = HasName.NOP;
        } else {
            value = hasName;
        }
        return canvasCommandFactory.updatePropertyValue(element,
                                                        nameId,
                                                        value.getValue());
    }
}
