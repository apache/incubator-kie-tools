/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandler;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;

public abstract class BaseDomainEditor
        extends Composite
        implements DomainEditor {

    protected static int OBJECT_EDITOR = 0;

    protected static int FIELD_EDITOR = 1;

    protected static int INFO_EDITOR = 2;

    protected SimplePanel mainPanel = new SimplePanel();

    protected DeckPanel editorsDeck = new DeckPanel();

    protected ObjectEditor objectEditor;

    protected FieldEditor fieldEditor;

    protected InfoEditor infoEditor = new InfoEditor();

    protected DataModelerContext context;

    protected DomainHandler handler;

    public BaseDomainEditor() {
        initWidget( mainPanel );
    }

    public BaseDomainEditor( ObjectEditor objectEditor,
                             FieldEditor fieldEditor ) {
        this();
        this.objectEditor = objectEditor;
        this.fieldEditor = fieldEditor;
    }

    @PostConstruct
    private void init() {
        editorsDeck.add( objectEditor );
        editorsDeck.add( fieldEditor );
        editorsDeck.add( infoEditor );
        mainPanel.add( editorsDeck );
        infoEditor.setInfo( "No data object has been opened." );
        showInfoEditor();
    }

    public DataModelerContext getContext() {
        return context;
    }

    protected String getContextId() {
        return context != null ? context.getContextId() : null;
    }

    public void showObjectEditor() {
        editorsDeck.showWidget( OBJECT_EDITOR );
    }

    public void showFieldEditor() {
        editorsDeck.showWidget( FIELD_EDITOR );
    }

    public void showInfoEditor() {
        editorsDeck.showWidget( INFO_EDITOR );
    }

    @Override
    public Widget getWidget() {
        return this.asWidget();
    }

    @Override
    public DomainHandler getHandler() {
        return handler;
    }

    public void setHandler( DomainHandler handler ) {
        this.handler = handler;
    }

    protected void onContextChange( DataModelerContext context ) {
        this.context = context;
        if ( context == null ) {
            infoEditor.setInfo( "No data object has been opened." );
            showInfoEditor();
        } else if ( context.getEditionMode() == DataModelerContext.EditionMode.SOURCE_MODE ) {
            infoEditor.setInfo( "Data object is being edited at this moment." );
            showInfoEditor();
        } else if ( context.getEditionMode() == DataModelerContext.EditionMode.GRAPHICAL_MODE ) {
            if ( context.getDataObject() != null && context.getObjectProperty() != null ) {
                showFieldEditor();
            } else {
                showObjectEditor();
            }
        }
        objectEditor.onContextChange( context );
        fieldEditor.onContextChange( context );

    }

    protected void onDataObjectDeleted( @Observes DataObjectDeletedEvent event ) {
        if ( event.isFromContext( getContextId() ) ) {
            showInfoEditor();
        }
    }

}