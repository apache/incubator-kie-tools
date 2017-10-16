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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditorPresenter;
import org.kie.workbench.common.widgets.metadata.client.popups.SelectDocumentPopupView.SelectableDocumentView;
import org.uberfire.backend.vfs.Path;

@Dependent
public class SelectDocumentPopup implements SelectDocumentPopupPresenter {

    private SelectDocumentPopupView view;
    private ManagedInstance<SelectableDocumentView> selectableDocumentProvider;

    private KieMultipleDocumentEditorPresenter presenter;

    private final Set<SelectableDocumentView> selectedDocuments = new HashSet<>();
    private final List<SelectableDocumentView> selectableDocuments = new ArrayList<>();

    @Inject
    public SelectDocumentPopup(final SelectDocumentPopupView view,
                               final ManagedInstance<SelectableDocumentView> selectableDocumentProvider) {
        this.view = view;
        this.selectableDocumentProvider = selectableDocumentProvider;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    @Override
    @PreDestroy
    public void dispose() {
        view.clear();
        selectedDocuments.clear();
        selectableDocuments.clear();
    }

    @Override
    public void setEditorPresenter(final KieMultipleDocumentEditorPresenter presenter) {
        this.presenter = PortablePreconditions.checkNotNull("presenter",
                                                            presenter);
    }

    @Override
    public void setDocuments(final List<Path> paths) {
        PortablePreconditions.checkNotNull("paths",
                                           paths);
        dispose();
        for (Path path : paths) {
            final SelectableDocumentView document = makeSelectableDocument(path);
            selectableDocuments.add(document);
            view.addDocument(document);
        }
        view.enableOKButton(false);
    }

    @Override
    public void show() {
        view.show();
    }

    @Override
    public void onOK() {
        if (!selectedDocuments.isEmpty()) {
            final List<Path> selectedDocumentPaths = new ArrayList<>();
            for (SelectableDocumentView selectableDocument : selectableDocuments) {
                if (selectedDocuments.contains(selectableDocument)) {
                    final Path path = selectableDocument.getPath();
                    selectedDocumentPaths.add(path);
                }
            }
            presenter.onOpenDocumentsInEditor(selectedDocumentPaths);
        }
        view.hide();
        dispose();
    }

    @Override
    public void onCancel() {
        view.hide();
        dispose();
    }

    SelectableDocumentView makeSelectableDocument(final Path path) {
        final SelectableDocumentView selectableDocument = selectableDocumentProvider.get();
        selectableDocument.setPath(path);
        selectableDocument.setDocumentSelectedCommand((Boolean selected) -> selectDocument(selectableDocument,
                                                                                           selected));
        return selectableDocument;
    }

    void selectDocument(final SelectableDocumentView document,
                        final boolean selected) {
        if (selected) {
            selectedDocuments.add(document);
        } else {
            selectedDocuments.remove(document);
        }

        view.enableOKButton(!selectedDocuments.isEmpty());
        for (SelectableDocumentView d : selectableDocuments) {
            d.setSelected(selectedDocuments.contains(d));
        }
    }
}
