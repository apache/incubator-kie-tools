/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.actions;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateCanvasElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.components.actions.AbstractNameEditBox;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class NameEditBoxWidget extends AbstractNameEditBox<Element> {

    public interface View extends UberView<NameEditBoxWidget> {

        View show( String name );

        View hide();

    }

    View view;
    DefinitionUtils definitionUtils;
    CanvasCommandFactory canvasCommandFactory;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    GraphUtils graphUtils;
    private Element<? extends Definition> element;
    private String nameValue;

    @Inject
    public NameEditBoxWidget( final DefinitionUtils definitionUtils,
                              final CanvasCommandFactory canvasCommandFactory,
                              final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                              final GraphUtils graphUtils,
                              final View view ) {
        this.definitionUtils = definitionUtils;
        this.canvasCommandFactory = canvasCommandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.graphUtils = graphUtils;
        this.view = view;
        this.element = null;
        this.nameValue = null;
    }

    @PostConstruct
    public void setup() {
        view.init( this );
    }

    @Override
    public void show( final Element element ) {
        this.element = element;
        final String name = definitionUtils.getName( this.element.getContent().getDefinition() );
        view.show( name );
    }

    @Override
    public void hide() {
        view.hide();
        this.nameValue = null;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    void onChangeName( final String name ) {
        this.nameValue = name;
    }

    // TODO: Check command result.
    void onSave() {
        if ( null != this.nameValue ) {
            final Object def = element.getContent().getDefinition();
            final String nameId = definitionUtils.getNameIdentifier( def );
            if ( null != nameId ) {
                UpdateElementPropertyCommand command = canvasCommandFactory.UPDATE_PROPERTY( element, nameId, this.nameValue );
                canvasCommandManager.execute( canvasHandler, command );

            }

        }
        view.hide();
        fireCloseCallback();

    }

    void onKeyPress( final int keyCode,
                     final String value ) {
        processKey( keyCode, value );

    }

    void onKeyDown( final int keyCode,
                    final String value ) {
        processKey( keyCode, value );

    }

    void onClose() {
        this.hide();
        fireCloseCallback();

    }

    private void processKey( final int keyCode,
                             final String value ) {
        this.nameValue = value;
        // Enter key produces save.
        if ( 13 == keyCode ) {
            onSave();

        }

    }

    private void fireCloseCallback() {
        if ( null != closeCallback ) {
            closeCallback.execute();

        }

    }

}
