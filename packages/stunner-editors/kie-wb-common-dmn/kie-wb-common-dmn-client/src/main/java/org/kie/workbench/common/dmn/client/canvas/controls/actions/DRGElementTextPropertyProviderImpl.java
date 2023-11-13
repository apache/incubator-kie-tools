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

package org.kie.workbench.common.dmn.client.canvas.controls.actions;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.util.NameUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class DRGElementTextPropertyProviderImpl implements TextPropertyProvider {

    private DefaultCanvasCommandFactory canvasCommandFactory;
    private DefinitionUtils definitionUtils;

    public DRGElementTextPropertyProviderImpl() {
        //CDI proxy
    }

    @Inject
    public DRGElementTextPropertyProviderImpl(final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                              final DefinitionUtils definitionUtils) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean supports(final Element<? extends Definition> element) {
        return DefinitionUtils.getElementDefinition(element) instanceof DRGElement;
    }

    @Override
    public String getText(final Element<? extends Definition> element) {
        final DRGElement drgElement = (DRGElement) DefinitionUtils.getElementDefinition(element);
        return drgElement.getNameHolder().getValue().getValue();
    }

    @Override
    public void setText(final AbstractCanvasHandler canvasHandler,
                        final CanvasCommandManager<AbstractCanvasHandler> commandManager,
                        final Element<? extends Definition> element,
                        final String text) {
        if (!Objects.equals(text, getText(element))) {
            final Object definition = DefinitionUtils.getElementDefinition(element);
            final CanvasCommand<AbstractCanvasHandler> command = canvasCommandFactory.updatePropertyValue(
                    element,
                    definitionUtils.getNameIdentifier(definition),
                    new Name(NameUtils.normaliseName(text)));

            commandManager.execute(canvasHandler, command);
        }
    }
}
