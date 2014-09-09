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

import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.Legend;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Well;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.social.hp.client.homepage.header.HeaderPresenter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public class SearchWidget extends Composite {

    @UiField
    Fieldset fieldset;

    interface Mybinder
            extends
            UiBinder<Widget, SearchWidget> {

    }

    private static Mybinder uiBinder = GWT.create( Mybinder.class );

    protected void init(final List<String> users,
                        final ParameterizedCommand<String> onSelect,
                        final String suggestText) {
        initWidget( uiBinder.createAndBindUi( this ) );
        SuggestBox suggestBox = new SuggestBox( new MultiWordSuggestOracle() {{
            addAll( users );
        }} );
        suggestBox.getElement().setAttribute( Constants.PLACEHOLDER, suggestText );
        suggestBox.addSelectionHandler( new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection( SelectionEvent<SuggestOracle.Suggestion> event ) {
                onSelect.execute( event.getSelectedItem().getReplacementString() );
            }
        } );
        fieldset.add( suggestBox );
    }
}