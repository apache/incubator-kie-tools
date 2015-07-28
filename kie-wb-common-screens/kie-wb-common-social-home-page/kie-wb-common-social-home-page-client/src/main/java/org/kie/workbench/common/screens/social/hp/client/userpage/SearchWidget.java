/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.social.hp.client.userpage;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.typeahead.client.base.StringDataset;
import org.gwtbootstrap3.extras.typeahead.client.events.TypeaheadSelectedEvent;
import org.gwtbootstrap3.extras.typeahead.client.events.TypeaheadSelectedHandler;
import org.gwtbootstrap3.extras.typeahead.client.ui.Typeahead;
import org.uberfire.mvp.ParameterizedCommand;

public class SearchWidget extends Composite {

    @UiField
    Typeahead typeaheadUsers;

    interface Mybinder
            extends
            UiBinder<Widget, SearchWidget> {

    }

    public SearchWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private static Mybinder uiBinder = GWT.create( Mybinder.class );

    protected void init( final Set<String> users,
                         final ParameterizedCommand<String> onSelect,
                         final String suggestText ) {

        typeaheadUsers.setDatasets( new StringDataset( users ) );
        typeaheadUsers.setPlaceholder( suggestText );
        typeaheadUsers.addTypeaheadSelectedHandler( new TypeaheadSelectedHandler() {
            @Override
            public void onSelected( final TypeaheadSelectedEvent event ) {
                onSelect.execute( event.getTypeahead().getValue() );
            }
        } );
    }

    public void clear() {
        typeaheadUsers.clear();
    }
}