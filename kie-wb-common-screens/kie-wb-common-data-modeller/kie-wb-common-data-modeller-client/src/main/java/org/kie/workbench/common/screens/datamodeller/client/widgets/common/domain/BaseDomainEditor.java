/*
 * Copyright 2015 JBoss Inc
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
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.services.datamodeller.core.DataModel;

public abstract class BaseDomainEditor
        extends Composite
        implements DomainEditor {

    protected static int OBJECT_EDITOR = 0;

    protected static int FIELD_EDITOR = 1;

    protected SimplePanel mainPanel = new SimplePanel(  );

    protected DeckPanel editorsDeck = new DeckPanel();

    protected ObjectEditor objectEditor;

    protected FieldEditor fieldEditor;

    protected DataModelerContext context;

    public BaseDomainEditor() {
        initWidget( mainPanel );
    }

    public BaseDomainEditor( ObjectEditor objectEditor, FieldEditor fieldEditor ) {
        this();
        this.objectEditor = objectEditor;
        this.fieldEditor = fieldEditor;
    }

    @PostConstruct
    private void init() {
        editorsDeck.add( objectEditor );
        editorsDeck.add( fieldEditor );
        mainPanel.add( editorsDeck );
        editorsDeck.showWidget( OBJECT_EDITOR );
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
        objectEditor.setContext( context );
        fieldEditor.setContext( context );
    }

    protected String getContextId() {
        return context != null ? context.getContextId() : null;
    }

    public DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void showObjectEditor() {
        editorsDeck.showWidget( OBJECT_EDITOR );
    }

    public void showFieldEditor() {
        editorsDeck.showWidget( FIELD_EDITOR );
    }

    @Override
    public Widget getWidget() {
        return this.asWidget();
    }

    protected void showFieldEditor( DataModelerEvent event ) {
        if ( getDataModel() != null &&
                getDataModel().getDataObjects().size() > 0 &&
                event.getCurrentDataObject() != null &&
                event.getCurrentDataObject().getProperties() != null &&
                event.getCurrentDataObject().getProperties().size() > 0 ) {
                showFieldEditor();
        } else {
            showObjectEditor();
        }
    }

    //event observers

    protected void onDataObjectSelected( @Observes DataObjectSelectedEvent event ) {
        if ( event.isFromContext( getContextId() ) ) {
            showObjectEditor();
        }
    }

    protected void onDataObjectDeleted( @Observes DataObjectDeletedEvent event ) {
        if ( event.isFromContext( getContextId() ) ) {
            //TODO check if we wants to do something special here
            showObjectEditor();
        }
    }

    protected void onDataObjectFieldCreated( @Observes DataObjectFieldCreatedEvent event ) {
        if ( event.isFromContext( getContextId() ) ) {
            showFieldEditor( event );
        }
    }

    protected void onDataObjectFieldDeleted( @Observes DataObjectFieldDeletedEvent event ) {
        if ( event.isFromContext( getContextId() ) ) {
            showFieldEditor( event );
        }
    }

    protected void onDataObjectFieldSelected( @Observes DataObjectFieldSelectedEvent event ) {
        if ( event.isFromContext( getContextId() ) ) {
            showFieldEditor( event );
        }
    }
}