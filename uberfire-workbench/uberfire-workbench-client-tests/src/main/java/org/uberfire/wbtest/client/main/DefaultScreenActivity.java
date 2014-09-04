package org.uberfire.wbtest.client.main;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named( "org.uberfire.wbtest.client.main.DefaultScreenActivity" )
public class DefaultScreenActivity extends AbstractTestScreenActivity {

    public static final String DEBUG_ID = "DefaultScreenActivity";

    private final Label widget = new Label( "Welcome to the default perspective!" );

    @Inject
    public DefaultScreenActivity( PlaceManager pm ) {
        super( pm );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        widget.ensureDebugId( DEBUG_ID );
    }

    @Override
    public IsWidget getWidget() {
        return widget;
    }

}
