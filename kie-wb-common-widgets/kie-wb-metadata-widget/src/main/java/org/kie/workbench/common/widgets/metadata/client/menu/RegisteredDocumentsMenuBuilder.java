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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
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

    private Command saveDocumentsCommand;
    private Command openDocumentCommand;
    private ParameterizedCommand<KieDocument> activateDocumentCommand;
    private ParameterizedCommand<KieDocument> removeDocumentCommand;

    private RegisteredDocumentsMenuView view;
    private SyncBeanManager beanManager;

    private Map<KieDocument, DocumentMenuItem> registeredDocuments = new HashMap<>();

    @Inject
    public RegisteredDocumentsMenuBuilder( final RegisteredDocumentsMenuView view,
                                           final SyncBeanManager beanManager ) {
        this.view = view;
        this.beanManager = beanManager;
    }

    @PostConstruct
    void setup() {
        view.init( this );
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

            @Override
            public String getSignatureId() {
                return "org.kie.workbench.common.widgets.metadata.client.menu.RegisteredDocumentsMenuBuilder";
            }

        };
    }

    @Override
    public void onOpenDocument() {
        if ( openDocumentCommand != null ) {
            openDocumentCommand.execute();
        }
    }

    @Override
    public void onSaveDocuments() {
        if ( saveDocumentsCommand != null ) {
            saveDocumentsCommand.execute();
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
        beanManager.destroyBean( documentMenuItem );
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
        if ( removeDocumentCommand != null ) {
            removeDocumentCommand.execute( document );
        }
    }

    @Override
    public void setOpenDocumentCommand( final Command openDocumentCommand ) {
        this.openDocumentCommand = PortablePreconditions.checkNotNull( "openDocumentCommand",
                                                                       openDocumentCommand );
    }

    @Override
    public void setSaveDocumentsCommand( final Command saveDocumentsCommand ) {
        this.saveDocumentsCommand = PortablePreconditions.checkNotNull( "saveDocumentsCommand",
                                                                        saveDocumentsCommand );
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

    @Override
    public void dispose() {
        view.clear();
        for ( DocumentMenuItem documentMenuItem : registeredDocuments.values() ) {
            beanManager.destroyBean( documentMenuItem );
        }
        registeredDocuments.clear();
    }

    DocumentMenuItem makeDocumentMenuItem( final KieDocument document ) {
        final DocumentMenuItem documentMenuItem = beanManager.lookupBean( DocumentMenuItem.class ).newInstance();
        documentMenuItem.setName( document.getCurrentPath().getFileName() );
        documentMenuItem.setRemoveDocumentCommand( () -> onRemoveDocument( document ) );
        documentMenuItem.setActivateDocumentCommand( () -> onActivateDocument( document ) );
        return documentMenuItem;
    }

}
