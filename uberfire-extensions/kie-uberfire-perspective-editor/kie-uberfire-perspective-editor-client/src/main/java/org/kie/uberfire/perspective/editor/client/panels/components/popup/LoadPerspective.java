/*
* Copyright 2013 JBoss Inc
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
package org.kie.uberfire.perspective.editor.client.panels.components.popup;

import java.util.Collection;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.perspective.editor.client.panels.perspective.PerspectivePresenter;

public class LoadPerspective
        extends PopupPanel {

    private final PerspectivePresenter perspectivePresenter;
    @UiField
    Modal popup;

    @UiField
    FlowPanel suggestion;

    private SuggestBox perspectiveSuggestion;

    interface Binder
            extends
            UiBinder<Widget, LoadPerspective> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public LoadPerspective( PerspectivePresenter perspectivePresenter ) {
        this.perspectivePresenter = perspectivePresenter;
        setWidget( uiBinder.createAndBindUi( this ) );
    }

    public void show( final Collection<String> perspectiveNames ) {
        perspectiveSuggestion = new SuggestBox( new MultiWordSuggestOracle() {{
            addAll( perspectiveNames );
        }} );
        perspectiveSuggestion.getElement().setAttribute( Constants.PLACEHOLDER, "" );
        perspectiveSuggestion.addSelectionHandler( new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection( SelectionEvent<SuggestOracle.Suggestion> event ) {
                System.out.println( event.getSelectedItem().getReplacementString() );
            }
        } );
        suggestion.add( perspectiveSuggestion );
        popup.show();
    }

    @UiHandler("load")
    void save( final ClickEvent event ) {
        perspectivePresenter.load( perspectiveSuggestion.getText() );
        popup.hide();
    }

}
