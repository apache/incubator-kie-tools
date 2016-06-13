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

package org.kie.workbench.common.widgets.metadata.client;

import java.util.List;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.popups.alert.AlertPopupView;
import org.kie.workbench.common.widgets.metadata.client.popups.SelectDocumentPopupPresenter;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.KieMultipleDocumentEditorConstants;
import org.uberfire.backend.vfs.Path;

@KieMultipleDocumentEditorQualifier
public class KieMultipleDocumentEditorWrapperViewImpl
        extends KieEditorWrapperViewImpl
        implements KieMultipleDocumentEditorWrapperView {

    protected KieMultipleDocumentEditorPresenter presenter;

    protected AlertPopupView noDocumentsPopup;
    protected SelectDocumentPopupPresenter selectDocumentPopup;
    protected TranslationService translationService;

    @Inject
    void setTranslationService( final TranslationService translationService ) {
        this.translationService = translationService;
    }

    @Inject
    void setNoDocumentsPopup( final AlertPopupView noDocumentsPopup ) {
        this.noDocumentsPopup = noDocumentsPopup;
    }

    @Inject
    void setSelectDocumentPopup( final SelectDocumentPopupPresenter selectDocumentPopup ) {
        this.selectDocumentPopup = selectDocumentPopup;
    }

    @Override
    public void init( final KieMultipleDocumentEditorPresenter presenter ) {
        this.presenter = presenter;
        this.selectDocumentPopup.setEditorPresenter( presenter );
    }

    @Override
    public void showNoAdditionalDocuments() {
        noDocumentsPopup.alert( translationService.getTranslation( KieMultipleDocumentEditorConstants.NoDocuments_Title ),
                                translationService.getTranslation( KieMultipleDocumentEditorConstants.NoDocuments_Message ) );
    }

    @Override
    public void showAdditionalDocuments( final List<Path> paths ) {
        selectDocumentPopup.setDocuments( paths );
        selectDocumentPopup.show();
    }

}
