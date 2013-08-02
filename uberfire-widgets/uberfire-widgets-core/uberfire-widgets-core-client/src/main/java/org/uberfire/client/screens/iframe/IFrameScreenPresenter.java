package org.uberfire.client.screens.iframe;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "IFrameScreen")
public class IFrameScreenPresenter {

    private String title;

    public interface View extends IsWidget {

        void setURL( final String url );
    }

    @Inject
    public IFrameScreenPresenter.View view;

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.view.setURL( placeRequest.getParameter( "url", "none" ) );
        this.title = placeRequest.getParameter( "title", "iframe" );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}
