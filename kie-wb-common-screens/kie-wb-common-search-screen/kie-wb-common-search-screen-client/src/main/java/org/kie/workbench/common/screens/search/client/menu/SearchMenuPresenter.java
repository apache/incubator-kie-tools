/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.search.client.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.search.ClearSearchEvent;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.kie.workbench.common.widgets.client.search.SetSearchTextEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class SearchMenuPresenter implements IsWidget {

    public interface View extends IsWidget, UberView<SearchMenuPresenter> {

        void setText( String text );

    }

    @Inject
    public View view;

    @Inject
    private ContextualSearch contextualSearch;

    @Inject
    private PlaceManager placeManager;

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @PostConstruct
    public void init() {
        view.init( this );
        contextualSearch.setDefaultSearchBehavior( new SearchBehavior() {
            @Override
            public void execute( final String term ) {
                placeManager.goTo( new DefaultPlaceRequest( "FullTextSearchForm" ).addParameter( "term", term ) );
            }
        } );
    }

    public void onClearSearchBox( @Observes ClearSearchEvent clearSearch ) {
        view.setText( "" );
    }

    public void onSetSearchText( @Observes SetSearchTextEvent setSearchText ) {
        view.setText( setSearchText.getSearchText() );
    }

    public void search( final String searchText ) {
        contextualSearch.getSearchBehavior().execute( searchText );
    }
}