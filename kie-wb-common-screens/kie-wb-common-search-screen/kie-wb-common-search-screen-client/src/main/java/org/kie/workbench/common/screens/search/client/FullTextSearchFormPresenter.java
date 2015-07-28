/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.search.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.screens.search.client.resources.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "FullTextSearchForm")
public class FullTextSearchFormPresenter {

    public interface View
            extends
            UberView<FullTextSearchFormPresenter> {

        void setSearchTerm( final String term );
    }

    @Inject
    private View view;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceRequest placeRequest;

    private String title = null;

    private String term = null;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        this.term = placeRequest.getParameter( "term", null );

        view.setSearchTerm( term );
        title = Constants.INSTANCE.SearchResultTitle() + " [ " + term + " ]";
    }

    @WorkbenchPartView
    public UberView<FullTextSearchFormPresenter> getWidget() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    public void setTitle( final String term ) {
        if ( !this.term.equals( term ) ) {
            this.term = term;
            title = Constants.INSTANCE.SearchResultTitle() + " [ " + term + " ]";
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest, title ) );
        }
    }

}
