package org.uberfire.wbtest.client.panels.custom;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named( "org.uberfire.wbtest.client.panels.custom.CustomPanelContentScreen" )
public class CustomPanelContentScreen extends AbstractTestScreenActivity {

    private Label widget;

    @Inject CustomPanelInstanceCounter instanceCounter;

    @Inject
    public CustomPanelContentScreen( PlaceManager pm ) {
        super( pm );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        String id = place.getParameter( "debugId", "" );
        widget = new Label( "I'm in the custom widget!" );
        widget.ensureDebugId( "CustomPanelContentScreen-" + id );
        instanceCounter.instanceCreated();
    }

    @Override
    public void onShutdown() {
        instanceCounter.instanceDestroyed();
    }

    @Override
    public IsWidget getWidget() {
        return widget;
    }
}
