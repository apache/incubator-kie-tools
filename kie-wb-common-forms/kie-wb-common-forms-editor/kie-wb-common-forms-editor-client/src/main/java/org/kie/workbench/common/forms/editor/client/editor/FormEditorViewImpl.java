/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.editor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;

@Dependent
@Templated
public class FormEditorViewImpl extends KieEditorViewImpl implements FormEditorPresenter.FormEditorView, RequiresResize {

    @DataField
    private Element container = DOM.createDiv();

    @Inject
    @DataField
    private FlowPanel editorContent;

    private TranslationService translationService;

    private FormEditorPresenter presenter;

    @Inject
    public FormEditorViewImpl( TranslationService translationService ) {
        this.translationService = translationService;
    }

    @Override
    public void init( FormEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupLayoutEditor( LayoutEditor layoutEditor ) {
        editorContent.clear();
        editorContent.add( layoutEditor.asWidget() );
    }

    @Override
    public void onResize() {
        if ( getParent() == null ) {
            return;
        }
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();

        container.getStyle().setWidth( width, Style.Unit.PX );
        container.getStyle().setHeight( height, Style.Unit.PX );
    }
}
