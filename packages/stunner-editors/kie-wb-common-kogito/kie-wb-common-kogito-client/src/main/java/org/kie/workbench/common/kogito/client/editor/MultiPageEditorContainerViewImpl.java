/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
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

            }

            @Override
            public void onLostFocus() {

            }
        });
    }

    @Override
    public MultiPageEditor getMultiPage() {
        return this;
    }

    @Override
    public int getSelectedTabIndex() {
        return selectedPage();
    }
}
