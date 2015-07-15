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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums;

import java.util.List;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.commons.data.Pair;

public class EnumValuePairEditorViewImpl
        extends Composite
        implements EnumValuePairEditorView {

    interface EnumValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, EnumValuePairEditorViewImpl> {

    }

    private static EnumValuePairEditorViewImplUiBinder uiBinder = GWT.create( EnumValuePairEditorViewImplUiBinder.class );

    @UiField
    Label valuePairLabel;

    @UiField
    ListBox listBox;

    private EnumValuePairEditorView.Presenter presenter;

    public EnumValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void initItems( List<Pair<String, String>> options ) {
        for ( Pair<String, String> option : options ) {
            listBox.addItem( option.getK1(), option.getK2() );
        }
    }

    @Override
    public void setSelectedValue( String value ) {
        listBox.setSelectedValue( value );
    }

    @Override
    public String getSelectedValue() {
        return listBox.getSelectedValue();
    }

    public void setValuePairLabel( String valuePairLabel ) {
        this.valuePairLabel.setText( valuePairLabel );
    }

    @UiHandler( "listBox" )
    void onValueChanged( ChangeEvent event ) {
        presenter.onValueChanged();
    }
}
