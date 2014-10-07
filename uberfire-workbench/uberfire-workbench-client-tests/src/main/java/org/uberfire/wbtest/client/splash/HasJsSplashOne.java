package org.uberfire.wbtest.client.splash;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named("org.uberfire.wbtest.client.splash.HasJsSplashOne")
public class HasJsSplashOne extends AbstractTestScreenActivity {

    private final FlowPanel panel = new FlowPanel();
    private final Label label = new Label();

    @Inject
    public HasJsSplashOne( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        panel.getElement().setId( "HasJsSplashOne" );
        label.setText( "Splashy screen JS One" );
        panel.add( label );
    }

    @Override
    public String getTitle() {
        return "HasJsSplashOne";
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
