/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorTitle;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorPresenter;

public class KieTextEditorViewImpl
        extends TextEditorPresenter
        implements KieTextEditorView {

    private KieTextEditorPresenter presenter;

    private KieEditorTitle kieEditorTitle = new KieEditorTitle();

    @Override
    public void init( final KieTextEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void showLoading() {
        showBusyIndicator( CommonConstants.INSTANCE.Loading() );
    }

    @Override
    public void showSaving() {
        showBusyIndicator( CommonConstants.INSTANCE.Saving() );
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public KieEditorTitle getTitleWidget() {
        return kieEditorTitle;
    }

    @Override
    public void refreshTitle( final String value ) {
        kieEditorTitle.setText( value );
    }

    @Override
    public void refreshTitle( final String fileName,
                              final String description ) {
        kieEditorTitle.setText( fileName,
                                description );
    }

    @Override
    public Widget asWidget() {
        return super.getWidget().asWidget();
    }

    @Override
    public void setReadOnly( final boolean isReadOnly ) {
        super.view.setReadOnly( isReadOnly );
    }

    @Override
    protected void onAfterViewLoaded() {
        presenter.onAfterViewLoaded();
    }

    @Override
    public String getContent() {
        return super.view.getContent();
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public AceEditorMode getAceEditorMode() {
        return presenter.getAceEditorMode();
    }
}
