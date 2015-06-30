/**
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.superselector;

import java.util.List;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.commons.data.Pair;

public class SuperclassSelector extends Composite {

    public static final String NOT_SELECTED = "NOT_SELECTED";

    public static final String NOT_SELECTED_DESC = "";

    interface SuperclassSelectorUIBinder
            extends UiBinder<Widget, SuperclassSelector> {

    }

    private static SuperclassSelectorUIBinder uiBinder = GWT.create(SuperclassSelectorUIBinder.class);

    @UiField
    ListBox superclassList;

    public SuperclassSelector() {
        initWidget( uiBinder.createAndBindUi( this ) );
        clean();
    }

    public ListBox getSuperclassList() {
        return superclassList;
    }

    public void setEnabled(boolean enabled) {
        this.superclassList.setEnabled( enabled );
    }

    public void clean() {
        superclassList.clear();
        superclassList.addItem( NOT_SELECTED_DESC, NOT_SELECTED );
    }

    public void initList( List<Pair<String, String>> values, String selectedValue ) {
        superclassList.clear();
        superclassList.addItem( NOT_SELECTED_DESC, NOT_SELECTED );

        if ( values != null ) {
            for ( Pair<String, String> value : values ) {
                superclassList.addItem( value.getK1(), value.getK2() );
            }
        }
        superclassList.setSelectedValue( selectedValue != null ? selectedValue : NOT_SELECTED );
    }

    public void refreshList( List<Pair<String, String>> values, boolean keepSelection ) {
        String selectedValue = superclassList.getValue();
        initList( values, selectedValue );
        if ( keepSelection && selectedValue != null ) {
            superclassList.setSelectedValue( selectedValue );
        }
    }

}