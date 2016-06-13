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

package org.kie.workbench.common.widgets.metadata.client.popups;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditorPresenter;
import org.kie.workbench.common.widgets.metadata.client.popups.SelectDocumentPopupView.SelectableDocumentView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

@Dependent
public class SelectDocumentPopup implements SelectDocumentPopupPresenter {

    private SelectDocumentPopupView view;
    private SyncBeanManager beanManager;

    private KieMultipleDocumentEditorPresenter presenter;

    private SelectableDocumentView selectedDocument = null;
    private final List<SelectableDocumentView> selectableDocuments = new ArrayList<>();

    @Inject
    public SelectDocumentPopup( final SelectDocumentPopupView view,
                                final SyncBeanManager beanManager ) {
        this.view = view;
        this.beanManager = beanManager;
    }

    @PostConstruct
    void init() {
        view.init( this );
    }

    @Override
    public void setEditorPresenter( final KieMultipleDocumentEditorPresenter presenter ) {
        this.presenter = PortablePreconditions.checkNotNull( "presenter",
                                                             presenter );
    }

    @Override
    public void setDocuments( final List<Path> paths ) {
        PortablePreconditions.checkNotNull( "paths",
                                            paths );
        dispose();
        for ( Path path : paths ) {
            final SelectableDocumentView document = makeSelectableDocument( path );
            selectableDocuments.add( document );
            view.addDocument( document );
        }
        view.enableOKButton( false );
    }

    @Override
    public void show() {
        view.show();
    }

    @Override
    public void onOK() {
        if ( selectedDocument != null ) {
            final Path path = selectedDocument.getPath();
            presenter.onOpenDocumentInEditor( path );
        }
        view.hide();
        dispose();
    }

    @Override
    public void onCancel() {
        view.hide();
        dispose();
    }

    @Override
    public void dispose() {
        view.clear();
        selectedDocument = null;
        for ( SelectableDocumentView selectableDocument : selectableDocuments ) {
            beanManager.destroyBean( selectableDocument );
        }
        selectableDocuments.clear();
    }

    SelectableDocumentView makeSelectableDocument( final Path path ) {
        final SelectableDocumentView selectableDocument = beanManager.lookupBean( SelectableDocumentView.class ).newInstance();
        selectableDocument.setPath( path );
        selectableDocument.setSelectDocumentCommand( () -> selectDocument( selectableDocument ) );
        return selectableDocument;
    }

    void selectDocument( final SelectableDocumentView document ) {
        this.selectedDocument = document;
        if ( document == null ) {
            view.enableOKButton( false );
        } else {
            view.enableOKButton( true );
            for ( SelectableDocumentView d : selectableDocuments ) {
                d.setSelected( d.getPath().equals( document.getPath() ) );
            }
        }
    }

}
