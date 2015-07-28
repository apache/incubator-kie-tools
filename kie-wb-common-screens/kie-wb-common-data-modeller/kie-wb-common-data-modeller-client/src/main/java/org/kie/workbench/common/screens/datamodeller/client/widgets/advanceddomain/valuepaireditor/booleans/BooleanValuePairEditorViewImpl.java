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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.booleans;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.uberfire.commons.data.Pair;

public class BooleanValuePairEditorViewImpl
        extends Composite
        implements BooleanValuePairEditorView {

    interface BooleanValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, BooleanValuePairEditorViewImpl> {

    }

    private static BooleanValuePairEditorViewImplUiBinder uiBinder = GWT.create( BooleanValuePairEditorViewImplUiBinder.class );

    @UiField
    FormLabel valuePairLabel;

    @UiField
    Select listBox;

    private static List<Pair<String, String>> items = new ArrayList<Pair<String, String>>();

    static {
        items.add( new Pair<String, String>( "", BooleanValuePairEditorView.NOT_SELECTED ) );
        items.add( new Pair<String, String>( "true", "true" ) );
        items.add( new Pair<String, String>( "false", "false" ) );
    }

    private Presenter presenter;

    public BooleanValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        initItems( items );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setSelectedValue( String value ) {
        DataModelerUtils.setSelectedValue( listBox, value );
    }

    @Override
    public String getSelectedValue() {
        return listBox.getValue();
    }

    public void setValuePairLabel( String valuePairLabel ) {
        this.valuePairLabel.setText( valuePairLabel );
    }

    @Override
    public void showValuePairName( boolean show ) {
        this.valuePairLabel.setVisible( show );
    }

    private void initItems( List<Pair<String, String>> options ) {
        for ( Pair<String, String> option : options ) {
            listBox.add( DataModelerUtils.newOption( option.getK1(), option.getK2() ) );
        }
    }

    @UiHandler( "listBox" )
    void onValueChanged( ChangeEvent event ) {
        presenter.onValueChanged();
    }
}
