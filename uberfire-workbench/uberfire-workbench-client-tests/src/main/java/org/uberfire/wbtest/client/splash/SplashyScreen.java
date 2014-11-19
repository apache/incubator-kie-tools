package org.uberfire.wbtest.client.splash;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.menu.SplashScreenMenuPresenter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named("org.uberfire.wbtest.client.splash.SplashyScreen")
public class SplashyScreen extends AbstractTestScreenActivity {

    private final FlowPanel panel = new FlowPanel();
    private final Label label = new Label();
    private String debugId;

    @Inject
    private SplashScreenMenuPresenter splashList;

    @Inject
    public SplashyScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        debugId = place.getParameter( "debugId", "default" );
        splashList.asWidget().getElement().setId( "SplashyScreen-" + debugId + "-SplashList" );
        panel.getElement().setId( "SplashyScreen-" + debugId );

        label.setText( "Splashy screen " + debugId );

        panel.add( splashList );
        panel.add( label );
    }

    @Override
    public String getTitle() {
        return "Splashy-" + debugId;
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
