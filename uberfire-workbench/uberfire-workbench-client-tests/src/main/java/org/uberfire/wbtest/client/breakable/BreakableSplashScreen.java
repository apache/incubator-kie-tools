package org.uberfire.wbtest.client.breakable;

import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestSplashScreenActivity;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named("org.uberfire.wbtest.client.breakable.BreakableSplashScreen")
public class BreakableSplashScreen extends AbstractTestSplashScreenActivity {

    private LifecyclePhase brokenLifecycle;
    private final Label widget = new Label( "Not started" );

    @Inject
    public BreakableSplashScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        String brokenParam = place.getParameter( "splash.broken", null );
        if ( brokenParam != null && brokenParam.length() > 0 ) {
            brokenLifecycle = LifecyclePhase.valueOf( brokenParam );
        }

        if ( brokenParam == null ) {
            widget.setText( "Splash Screen with no broken methods" );
        } else {
            widget.setText( "Splash Screen with broken " + brokenLifecycle + " method" );
        }

        if ( brokenLifecycle == LifecyclePhase.STARTUP ) {
            throw new RuntimeException( "This screen has a broken startup callback" );
        }
    }

    @Override
    public void onOpen() {
        super.onOpen();
        if ( brokenLifecycle == LifecyclePhase.OPEN ) {
            throw new RuntimeException( "This splash screen has a broken open callback" );
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if ( brokenLifecycle == LifecyclePhase.CLOSE ) {
            throw new RuntimeException( "This splash screen has a broken close callback" );
        }
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        if ( brokenLifecycle == LifecyclePhase.SHUTDOWN ) {
            throw new RuntimeException( "This splash screen has a broken shutdown callback" );
        }
    }

    @Override
    public IsWidget getWidget() {
        return widget;
    }

    @Override
    public Boolean intercept( PlaceRequest intercepted ) {
        return intercepted.getParameter( "breakable.splash", null ) != null;
    }

    @Override
    public SplashScreenFilter getFilter() {
        return new SplashScreenFilterImpl( "ThisFilterNotUsed", true, Collections.<String>emptyList() );
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
