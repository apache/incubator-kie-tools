package org.uberfire.client.workbench;

import javax.enterprise.event.Event;

import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.pmgr.nswe.NSWEExtendedBeanFactory;
import org.uberfire.client.workbench.pmgr.nswe.NSWEPanelManager;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;
import org.uberfire.workbench.model.PanelDefinition;

public class PanelManagerImplUnitTestWrapper extends NSWEPanelManager {

    private final WorkbenchPanelPresenter workbenchPanelPresenter;

    public PanelManagerImplUnitTestWrapper( final NSWEExtendedBeanFactory factory,
                                            final Event<PlaceGainFocusEvent> placeGainFocusEvent,
                                            final Event<PlaceLostFocusEvent> placeLostFocusEvent,
                                            final Event<SelectPlaceEvent> selectPlaceEvent,
                                            final WorkbenchStatusBarPresenter statusBar,
                                            WorkbenchPanelPresenter workbenchPanelPresenter ) {
        super( factory, placeGainFocusEvent, placeLostFocusEvent, selectPlaceEvent, statusBar );
        this.workbenchPanelPresenter = workbenchPanelPresenter;
    }

    @Override
    protected WorkbenchPanelPresenter getWorkbenchPanelPresenter( PanelDefinition panel ) {
        return workbenchPanelPresenter;
    }

}