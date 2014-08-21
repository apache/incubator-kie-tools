package org.uberfire.wbtest.client.resize;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@Named( "org.uberfire.wbtest.client.resize.ResizeTestScreenActivity" )
public class ResizeTestScreenActivity extends AbstractTestScreenActivity {

    private ResizeTestWidget widget;

    @Inject
    public ResizeTestScreenActivity( PlaceManager pm ) {
        super( pm );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        String id = place.getParameter( "debugId", "" );
        widget = new ResizeTestWidget( id );
        Layouts.setToFillParent( widget );
    }

    @Override
    public IsWidget getWidget() {
        return widget;
    }

}
