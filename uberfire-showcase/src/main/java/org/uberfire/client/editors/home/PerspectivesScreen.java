package org.uberfire.client.editors.home;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.AbstractPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

@Dependent
@WorkbenchScreen(identifier = "perspectives")
public class PerspectivesScreen {

    private final Set<AbstractPerspectiveActivity> perspectives;
    private final PlaceManager                     placeManager;

    @Inject
    public PerspectivesScreen(ActivityManager activityManager,
                              PlaceManager placeManager) {
        this.perspectives = activityManager.getActivities( AbstractPerspectiveActivity.class );
        this.placeManager = placeManager;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Perspectives";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        VerticalPanel widgets = new VerticalPanel();

        for ( final AbstractPerspectiveActivity perspective : perspectives ) {
            InfoCube infoCube = new InfoCube();
            infoCube.setTitle( perspective.getIdentifier() );

            infoCube.addClickHandler( new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    placeManager.goTo( new DefaultPlaceRequest( perspective.getIdentifier() ) );
                }
            } );

            widgets.add( infoCube );
        }

        return widgets;
    }
}
