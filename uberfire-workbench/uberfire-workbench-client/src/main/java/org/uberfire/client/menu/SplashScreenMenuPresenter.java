package org.uberfire.client.menu;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;
import org.uberfire.mvp.Command;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A drop-down style widget that contains an up-to-date list of the available splash screens. Each currently-displayed
 * part, plus the current perspective itself, may have a splash screen associated with it. Splash screens show up in
 * this widget's drop-down list even if they are not currently visible. The only requirement is that the part they are
 * associated with is currently in the workbench.
 * <p>
 * When the user clicks on one of the items in the drop down list, the corresponding splash screen will be displayed via
 * its {@link SplashScreenActivity#forceShow()} method.
 */
@ApplicationScoped
public class SplashScreenMenuPresenter implements IsWidget {

    public static class SplashScreenListEntry {

        private final String screenName;
        private final Command showCommand;

        public SplashScreenListEntry( String screenName,
                                      Command showCommand ) {
            this.screenName = checkNotNull( "screenName", screenName );
            this.showCommand = checkNotNull( "showCommand", showCommand );
        }

        public String getScreenName() {
            return screenName;
        }

        public Command getShowCommand() {
            return showCommand;
        }

    }

    public interface View extends UberView<SplashScreenMenuPresenter> {

        /**
         * Replaces the current list of splash screens with the given list.
         */
        void setSplashScreenList(List<SplashScreenListEntry> splashScreens);
    }

    private final PlaceManager placeManager;
    private final View view;

    @Inject
    public SplashScreenMenuPresenter(PlaceManager placeManager, View view) {
        this.placeManager = checkNotNull( "placeManager", placeManager );
        this.view = checkNotNull( "view", view );
        view.init( this );
    }

    void onNewSplashScreen( @Observes NewSplashScreenActiveEvent event ) {
        List<SplashScreenListEntry> splashScreens = new ArrayList<SplashScreenListEntry>();
        for ( final SplashScreenActivity activity : placeManager.getActiveSplashScreens() ) {
            splashScreens.add( new SplashScreenListEntry( activity.getTitle(),
                                                          new Command() {
                @Override
                public void execute() {
                    activity.forceShow();
                }
            } ) );
        }
        view.setSplashScreenList( splashScreens );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

}
