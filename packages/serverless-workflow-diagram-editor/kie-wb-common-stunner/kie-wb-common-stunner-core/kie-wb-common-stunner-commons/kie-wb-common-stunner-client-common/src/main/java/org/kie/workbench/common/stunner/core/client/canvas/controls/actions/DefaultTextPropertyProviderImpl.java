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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class DefaultTextPropertyProviderImpl implements TextPropertyProvider {

    private DefinitionUtils definitionUtils;
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    public DefaultTextPropertyProviderImpl() {
        //CDI proxy
    }

    @Inject
    public DefaultTextPropertyProviderImpl(final DefinitionUtils definitionUtils,
                                           final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.definitionUtils = definitionUtils;
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    public int getPriority() {
        return TextPropertyProviderFactory.CATCH_ALL_PRIORITY;
    }

    @Override
    public boolean supports(final Element<? extends Definition> element) {
        return true;
    }

    @Override
    public String getText(final Element<? extends Definition> element) {
        return definitionUtils.getName(element.getContent().getDefinition());
    }

    @Override
    public void setText(final AbstractCanvasHandler canvasHandler,
                        final CanvasCommandManager<AbstractCanvasHandler> commandManager,
                        final Element<? extends Definition> element,
                        final String text) {
        final Object def = element.getContent().getDefinition();
        final String nameField = definitionUtils.getNameIdentifier(def);
        if (null != nameField) {
            final CanvasCommand<AbstractCanvasHandler> command = canvasCommandFactory.updatePropertyValue(element,
                                                                                                          nameField,
                                                                                                          text);
            commandManager.execute(canvasHandler,
                                   command);
        }
    }
}
