/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.kogito.client.editor;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.kogito.client.resources.i18n.KogitoClientConstants;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;

public class MultiPageEditorContainerViewImpl
        extends MultiPageEditorImpl
        implements MultiPageEditorContainerView {

    protected static final int EDITOR_TAB_INDEX = 0;

    private TranslationService translationService;
    private Presenter presenter;

    public MultiPageEditorContainerViewImpl() {
        //CDI proxy
    }

    @Inject
    public MultiPageEditorContainerViewImpl(final TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setEditorWidget(final IsWidget editorView) {
        addPage(new PageImpl(editorView,
                             translationService.format(KogitoClientConstants.KieEditorWrapperView_EditTabTitle)) {
            @Override
            public void onFocus() {
                presenter.onEditTabSelected();
            }

            @Override
            public void onLostFocus() {
                presenter.onEditTabUnselected();
            }
        });
    }

    @Override
    public MultiPageEditor getMultiPage() {
        return this;
    }

    @Override
    public void selectEditorTab() {
        setSelectedTab(EDITOR_TAB_INDEX);
    }

    @Override
    public boolean isEditorTabSelected() {
        return selectedPage() == EDITOR_TAB_INDEX;
    }

    @Override
    public void setSelectedTab(final int tabIndex) {
        selectPage(tabIndex);
    }

    @Override
    public int getSelectedTabIndex() {
        return selectedPage();
    }
}
