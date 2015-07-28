/**
 * Copyright 2012 JBoss Inc
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.superselector;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.uberfire.commons.data.Pair;

import static org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils.*;

public class SuperclassSelector extends Composite {

    interface SuperclassSelectorUIBinder
            extends UiBinder<Widget, SuperclassSelector> {

    }

    private static SuperclassSelectorUIBinder uiBinder = GWT.create( SuperclassSelectorUIBinder.class );

    @UiField
    Select superclassList;

    public SuperclassSelector() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public Select getSuperclassList() {
        return superclassList;
    }

    public void setEnabled( boolean enabled ) {
        superclassList.setEnabled( enabled );
        refreshSelect( superclassList );
    }

    public void clean() {
        superclassList.clear();
        superclassList.add( emptyOption() );
        setSelectedValue( superclassList, NOT_SELECTED );
    }

    public void initList( final List<Pair<String, String>> values, final String selectedValue ) {
        superclassList.clear();
        superclassList.add( emptyOption() );

        if ( values != null ) {
            for ( Pair<String, String> value : values ) {
                superclassList.add( newOption( value.getK1(), value.getK1() ) );
            }
        }
        setSelectedValue( superclassList, selectedValue != null ? selectedValue : NOT_SELECTED );
    }

}