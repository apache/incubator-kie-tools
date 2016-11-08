/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.menu;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.widgets.metadata.client.KieDocument;
import org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuView.DocumentMenuItem;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
public class RegisteredDocumentsMenuBuilder implements MenuFactory.CustomMenuBuilder,
                                                       RegisteredDocumentsMenuView.Presenter {

    private boolean isReadOnly;
    private Command newDocumentCommand;
    private Command openDocumentCommand;
    private ParameterizedCommand<KieDocument> activateDocumentCommand;
    private ParameterizedCommand<KieDocument> removeDocumentCommand;

    private RegisteredDocumentsMenuView view;
    private ManagedInstance<DocumentMenuItem> documentMenuItemProvider;

    private Map<KieDocument, DocumentMenuItem> registeredDocuments = new HashMap<>();

    @Inject
    public RegisteredDocumentsMenuBuilder( final RegisteredDocumentsMenuView view,
                                           final ManagedInstance<DocumentMenuItem> documentMenuItemProvider ) {
        this.view = view;
        this.documentMenuItemProvider = documentMenuItemProvider;
    }

    @PostConstruct
    void setup() {
        view.init( this );
        view.enableNewDocumentButton( false );
        view.enableOpenDocumentButton( false );
        this.isReadOnly = false;
    }

    @Override
    public void setReadOnly( final boolean isReadOnly ) {
        this.isReadOnly = isReadOnly;
        this.view.setReadOnly( isReadOnly );
        this.registeredDocuments.values().stream().forEach( ( e ) -> e.setReadOnly( isReadOnly ) );
    }

    @Override
    @PreDestroy
    public void dispose() {
        view.clear();
        registeredDocuments.clear();
    }

    @Override
    public void push( final MenuFactory.CustomMenuBuilder element ) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return ElementWrapperWidget.getWidget( view.getElement() );
            }

            @Override
            public boolean isEnabled() {
                return view.isEnabled();
            }

            @Override
            public void setEnabled( final boolean enabled ) {
                view.setEnabled( enabled );
            }
        };
    }

    @Override
    public void onOpenDocument() {
        if ( isReadOnly ) {
            return;
        }
        if ( openDocumentCommand != null ) {
            openDocumentCommand.execute();
        }
    }

    @Override
    public void onNewDocument() {
        if ( isReadOnly ) {
            return;
        }
        if ( newDocumentCommand != null ) {
            newDocumentCommand.execute();
        }
    }

    @Override
    public void registerDocument( final KieDocument document ) {
        final DocumentMenuItem documentMenuItem = makeDocumentMenuItem( document );
        registeredDocuments.put( document,
                                 documentMenuItem );
        view.addDocument( documentMenuItem );
    }

    @Override
    public void deregisterDocument( final KieDocument document ) {
        final DocumentMenuItem documentMenuItem = registeredDocuments.remove( document );
        documentMenuItemProvider.destroy( documentMenuItem );
        view.deleteDocument( documentMenuItem );
    }

    @Override
    public void onActivateDocument( final KieDocument document ) {
        if ( activateDocumentCommand != null ) {
            activateDocumentCommand.execute( document );
        }
    }

    @Override
    public void onRemoveDocument( final KieDocument document ) {
        if ( isReadOnly ) {
            return;
        }
        if ( removeDocumentCommand != null ) {
            removeDocumentCommand.execute( document );
        }
    }

    @Override
    public void setNewDocumentCommand( final Command newDocumentCommand ) {
        this.newDocumentCommand = PortablePreconditions.checkNotNull( "newDocumentCommand",
                                                                      newDocumentCommand );
        view.enableNewDocumentButton( true );
    }

    @Override
    public void setOpenDocumentCommand( final Command openDocumentCommand ) {
        this.openDocumentCommand = PortablePreconditions.checkNotNull( "openDocumentCommand",
                                                                       openDocumentCommand );
        view.enableOpenDocumentButton( true );
    }

    @Override
    public void setActivateDocumentCommand( final ParameterizedCommand<KieDocument> activateDocumentCommand ) {
        this.activateDocumentCommand = PortablePreconditions.checkNotNull( "activateDocumentCommand",
                                                                           activateDocumentCommand );
    }

    @Override
    public void setRemoveDocumentCommand( final ParameterizedCommand<KieDocument> removeDocumentCommand ) {
        this.removeDocumentCommand = PortablePreconditions.checkNotNull( "removeDocumentCommand",
                                                                         removeDocumentCommand );
    }

    @Override
    public void activateDocument( final KieDocument document ) {
        for ( Map.Entry<KieDocument, DocumentMenuItem> e : registeredDocuments.entrySet() ) {
            e.getValue().setActive( e.getKey().equals( document ) );
        }
    }

    DocumentMenuItem makeDocumentMenuItem( final KieDocument document ) {
        final DocumentMenuItem documentMenuItem = documentMenuItemProvider.get();
        documentMenuItem.setName( document.getCurrentPath().getFileName() );
        documentMenuItem.setRemoveDocumentCommand( () -> onRemoveDocument( document ) );
        documentMenuItem.setActivateDocumentCommand( () -> onActivateDocument( document ) );
        documentMenuItem.setReadOnly( document.isReadOnly() );
        return documentMenuItem;
    }

}
