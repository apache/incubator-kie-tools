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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.javaeditor.client.widget.EditJavaSourceWidget;

public class ValuePairEditorViewImpl
        extends Composite
        implements ValuePairEditorView {

    interface ValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, ValuePairEditorViewImpl> {

    }

    private static ValuePairEditorViewImplUiBinder uiBinder = GWT.create( ValuePairEditorViewImplUiBinder.class );

    private Presenter presenter;

    @UiField
    HelpInline valuePairValueInline;

    @UiField
    DivWidget editorContainer;

    @UiField
    Label valuePairValueLabel;

    @UiField
    Button valuePairValidateButton;

    @Inject
    private EditJavaSourceWidget javaSourceEditor;

    public ValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    private void init() {
        editorContainer.add( javaSourceEditor );
        javaSourceEditor.setReadonly( false );
        javaSourceEditor.setWidth( "500px" );
        javaSourceEditor.setHeight( "100px" );
        javaSourceEditor.addChangeHandler( new EditJavaSourceWidget.TextChangeHandler() {
            @Override public void onTextChange() {
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
    public void setNameLabel( String nameLabel ) {
        valuePairValueLabel.setText(  nameLabel );
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
        valuePairValidateButton.setVisible( show );
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

    @UiHandler( "valuePairValidateButton" )
    void onValidateClicked( ClickEvent event ) {
        presenter.onValidate();
    }
}
