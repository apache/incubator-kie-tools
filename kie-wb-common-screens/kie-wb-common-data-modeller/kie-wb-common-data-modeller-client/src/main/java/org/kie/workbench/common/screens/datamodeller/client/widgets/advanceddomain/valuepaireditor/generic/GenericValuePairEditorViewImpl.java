/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.kie.workbench.common.screens.javaeditor.client.widget.EditJavaSourceWidget;

@Dependent
public class GenericValuePairEditorViewImpl
        extends Composite
        implements GenericValuePairEditorView {

    interface GenericValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, GenericValuePairEditorViewImpl> {

    }

    private static GenericValuePairEditorViewImplUiBinder uiBinder = GWT.create( GenericValuePairEditorViewImplUiBinder.class );

    private Presenter presenter;

    @UiField
    HelpBlock valuePairHelpBlock;

    @UiField
    FlowPanel editorContainer;

    @UiField
    FormLabel valuePairLabel;

    @UiField
    Button validateButton;

    private EditJavaSourceWidget javaSourceEditor;

    @Inject
    public GenericValuePairEditorViewImpl( EditJavaSourceWidget javaSourceEditor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.javaSourceEditor = javaSourceEditor;
    }

    @PostConstruct
    protected void init() {
        editorContainer.add( javaSourceEditor );
        javaSourceEditor.setReadonly( false );
        javaSourceEditor.setWidth( "400px" );
        javaSourceEditor.setHeight( "100px" );
        javaSourceEditor.addChangeHandler( new EditJavaSourceWidget.TextChangeHandler() {
            @Override
            public void onTextChange() {
                presenter.onValueChange();
            }
        } );
    }

    @Override
    public void init( Presenter presenter ) {
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
    public void showValuePairName( boolean show ) {
        valuePairLabel.setVisible( show );
    }

    @Override
    public void showValuePairRequiredIndicator( boolean required ) {
        valuePairLabel.setShowRequiredIndicator( required );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        valuePairHelpBlock.setText( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        valuePairHelpBlock.setText( null );
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

    @UiHandler("validateButton")
    void onValidateClicked( ClickEvent event ) {
        presenter.onValidate();
    }
}