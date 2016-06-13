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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.NodeList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.KieMultipleDocumentEditorConstants;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
@Templated
public class SelectDocumentPopupViewImpl extends Composite
        implements SelectDocumentPopupView {

    @DataField("documents-container")
    Div documentsContainer;

    private final BaseModal modal;
    private final TranslationService translationService;

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( this::onOK,
                                                                                      this::onCancel );

    private SelectDocumentPopupPresenter presenter;

    @Inject
    public SelectDocumentPopupViewImpl( final Div documentsContainer,
                                        final TranslationService translationService ) {
        super();
        this.documentsContainer = documentsContainer;
        this.translationService = translationService;
        this.modal = new BaseModal();
    }

    @PostConstruct
    void init() {
        this.modal.setTitle( getSelectDocumentViewTitle() );
        this.modal.setBody( this );
        this.modal.add( footer );

        this.modal.addHiddenHandler( ( e ) -> onHidden() );
    }

    @Override
    public void init( final SelectDocumentPopupPresenter presenter ) {
        this.presenter = presenter;
    }

    private String getSelectDocumentViewTitle() {
        return translationService.format( KieMultipleDocumentEditorConstants.SelectDocumentPopupViewImpl_Title );
    }

    @Override
    public void clear() {
        final NodeList documents = documentsContainer.getChildNodes();
        for ( int i = 0; i < documents.getLength(); i++ ) {
            final Node document = documents.item( i );
            documentsContainer.removeChild( document );
        }
    }

    @Override
    public void addDocument( final SelectableDocumentView document ) {
        PortablePreconditions.checkNotNull( "document",
                                            document );
        documentsContainer.appendChild( document.getElement() );
    }

    @Override
    public void enableOKButton( final boolean enabled ) {
        footer.enableOkButton( enabled );
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    private void onOK() {
        presenter.onOK();
    }

    private void onCancel() {
        presenter.onCancel();
    }

    private void onHidden() {
        presenter.dispose();
    }

}
