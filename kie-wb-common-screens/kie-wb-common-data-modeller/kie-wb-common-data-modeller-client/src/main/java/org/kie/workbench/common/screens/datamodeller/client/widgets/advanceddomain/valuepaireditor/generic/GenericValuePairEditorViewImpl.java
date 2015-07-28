/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.generic;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.kie.workbench.common.screens.javaeditor.client.widget.EditJavaSourceWidget;

public class GenericValuePairEditorViewImpl
        extends Composite
        implements GenericValuePairEditorView {

    interface ComplexValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, GenericValuePairEditorViewImpl> {

    }

    private static ComplexValuePairEditorViewImplUiBinder uiBinder = GWT.create( ComplexValuePairEditorViewImplUiBinder.class );

    private Presenter presenter;

    @UiField
    HelpBlock valuePairValueInline;

    @UiField
    FlowPanel editorContainer;

    @UiField
    FormLabel valuePairLabel;

    @UiField
    Button validateButton;

    @Inject
    private EditJavaSourceWidget javaSourceEditor;

    public GenericValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    private void init() {
        editorContainer.add( javaSourceEditor );
        javaSourceEditor.setReadonly( false );
        javaSourceEditor.setWidth( "500px" );
        javaSourceEditor.setHeight( "100px" );
        javaSourceEditor.addChangeHandler( new EditJavaSourceWidget.TextChangeHandler() {
            @Override
            public void onTextChange() {
                presenter.onValueChanged();
            }
        } );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setValue( String value ) {
        javaSourceEditor.setContent( value );
    }

    @Override
    public String getValue() {
        return javaSourceEditor.getContent();
    }

    @Override
    public void setValuePairLabel( String nameLabel ) {
        valuePairLabel.setText( nameLabel );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        valuePairValueInline.setText( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        valuePairValueInline.setText( null );
    }

    @Override
    public void showValidateButton( boolean show ) {
        validateButton.setVisible( show );
    }

    @Override
    public void clear() {
        javaSourceEditor.clear();
        clearErrorMessage();
    }

    @Override
    public void refresh() {
        javaSourceEditor.refresh();
    }

    @Override
    public void addEditor( IsWidget editor ) {
        editorContainer.add( editor );
    }

    @UiHandler("validateButton")
    void onValidateClicked( ClickEvent event ) {
        presenter.onValidate();
    }
}