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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.item;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;

@Dependent
public class AnnotationValuePairListItemViewImpl
        extends Composite
        implements AnnotationValuePairListItemView {

    interface AnnotationValuePairListItemViewImplUIBinder
            extends UiBinder< Widget, AnnotationValuePairListItemViewImpl > {

    }

    private static AnnotationValuePairListItemViewImplUIBinder uiBinder =
            GWT.create( AnnotationValuePairListItemViewImplUIBinder.class );

    @UiField
    FormLabel valueLabel;

    @UiField
    TextBox valueTextBox;

    @UiField
    Button editButton;

    @UiField
    Button clearButton;

    private Presenter presenter;

    @Inject
    public AnnotationValuePairListItemViewImpl( ) {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    private void init( ) {
        valueTextBox.setReadOnly( true );
        editButton.addClickHandler( new ClickHandler( ) {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onEdit( );
            }
        } );
        editButton.setIcon( IconType.EDIT );

        clearButton.addClickHandler( new ClickHandler( ) {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onClear( );
            }
        } );
        clearButton.setIcon( IconType.ERASER );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setReadonly( boolean readonly ) {
        editButton.setEnabled( !readonly );
        clearButton.setEnabled( !readonly );
    }

    @Override
    public void showRequiredIndicator( boolean show ) {
        valueLabel.setShowRequiredIndicator( show );
    }

    @Override
    public void setValuePairStringValue( String valuePairStringValue ) {
        valueTextBox.setText( valuePairStringValue );
        valueTextBox.setTitle( valuePairStringValue );
    }

    @Override
    public void setValuePairName( String valuePairName ) {
        valueLabel.setText( valuePairName );
    }
}