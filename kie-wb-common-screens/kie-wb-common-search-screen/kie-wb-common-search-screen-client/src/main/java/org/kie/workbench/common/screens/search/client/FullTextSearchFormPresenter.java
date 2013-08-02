package org.kie.workbench.common.screens.search.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.ChangeTitleWidgetEvent;
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
        title = "Search Result [ " + term + " ]";
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
            title = "Search Result [ " + term + " ]";
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest, title, null ) );
        }
    }

}
