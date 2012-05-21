package org.drools.guvnor.client.mvp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.ScrollTabLayoutPanel;
import org.drools.guvnor.client.common.content.multi.ClosableLabel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


/**
 * This is the tab panel manager.
 */
@Dependent
public class ExplorerViewCenterPanel extends Composite
    /*implements
    TabbedPanel*/ {
/*    @Inject
    private static PlaceManager placeManager;*/
    private final ScrollTabLayoutPanel       tabLayoutPanel;

    private PanelMap                         openedTabs          = new PanelMap();
    @Inject private Event<ClosePlaceEvent> event;

    public ExplorerViewCenterPanel() {
        //this.eventBus = eventBus;
        tabLayoutPanel = new ScrollTabLayoutPanel(2,
                Unit.EM);

        //addBeforeSelectionHandler();

        initWidget(tabLayoutPanel);
    }
/*
    private void addBeforeSelectionHandler() {
        tabLayoutPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> integerBeforeSelectionEvent) {
                if ( !tabLayoutPanel.isCanSelectTabToggle() ) {
                    integerBeforeSelectionEvent.cancel();
                    clientFactory.getPlaceController().goTo( openedTabs.getKey( integerBeforeSelectionEvent.getItem() ) );
                }
            }
        } );
    }*/

    public boolean contains(PlaceRequest key) {
        return openedTabs.contains( key );
    }

    public void show(PlaceRequest key) {
        if ( openedTabs.contains( key ) ) {
            LoadingPopup.close();
            tabLayoutPanel.selectTab( openedTabs.get( key ) );
        }
    }

    /**
     * Add a new tab. Should only do this if have checked showIfOpen to avoid
     * dupes being opened.
     * 
     * @param tabname
     *            The displayed tab name.
     * @param widget
     *            The contents.
     * @param place
     *            A place which is unique.
     */
    public void addTab(final String tabname,
                       IsWidget widget,
                       final PlaceRequest place) {

        ScrollPanel localTP = new ScrollPanel();
        localTP.add( widget );
        tabLayoutPanel.add( localTP,
                            newClosableLabel(
                                              tabname,
                                              place
                            ) );
        tabLayoutPanel.selectTab( localTP );

        openedTabs.put( place,
                        localTP );
    }

    private Widget newClosableLabel(final String title,
                                    final PlaceRequest place) {
        ClosableLabel closableLabel = new ClosableLabel( title );

        closableLabel.addCloseHandler( new CloseHandler<ClosableLabel>() {
            public void onClose(CloseEvent<ClosableLabel> e) {
                //TODO
                event.fire(new ClosePlaceEvent(place));
                close(place);
            }

        } );

        return closableLabel;
    }

    public void close(PlaceRequest key) {

        int widgetIndex = openedTabs.getIndex( key );

        PlaceRequest nextPlace = getPlace( widgetIndex );

        tabLayoutPanel.remove( openedTabs.get( key ) );
        openedTabs.remove( key );

        if ( nextPlace != null ) {
            goTo( nextPlace );
        } else {
            //TODO
            //goTo( Place.NOWHERE );
        }
    }

    private PlaceRequest getPlace(int widgetIndex) {
        if ( isOnlyOneTabLeft() ) {
            //TODO
            return null;
            //return Place.NOWHERE;
        } else if ( isSelectedTabIndex( widgetIndex ) ) {
            return getNeighbour( widgetIndex );
        } else {
            return null;
        }
    }

    private void goTo(PlaceRequest place) {
        //placeManager.goTo(place);        
    }

    private PlaceRequest getNeighbour(int widgetIndex) {
        if ( isLeftMost( widgetIndex ) ) {
            return getNextPlace();
        } else {
            return getPreviousPlace();
        }
    }

    private boolean isLeftMost(int widgetIndex) {
        return widgetIndex == 0;
    }

    private boolean isSelectedTabIndex(int widgetIndex) {
        return tabLayoutPanel.getSelectedIndex() == widgetIndex;
    }

    private PlaceRequest getPreviousPlace() {
        if ( tabLayoutPanel.getSelectedIndex() > 0 ) {
            return openedTabs.getKey( tabLayoutPanel.getSelectedIndex() - 1 );
        }
        return null;
    }

    private PlaceRequest getNextPlace() {
        return openedTabs.getKey( tabLayoutPanel.getSelectedIndex() + 1 );
    }

    private boolean isOnlyOneTabLeft() {
        return tabLayoutPanel.getWidgetCount() == 1;
    }

    private class PanelMap {

        private final Map<PlaceRequest, Panel> keysToPanel = new HashMap<PlaceRequest, Panel>();
        private final List<PlaceRequest>       keys        = new ArrayList<PlaceRequest>();

        Panel get(PlaceRequest key) {
            return keysToPanel.get( key );
        }

        PlaceRequest getKey(int index) {
            return keys.get( index );
        }

        void remove(PlaceRequest key) {
            keys.remove( key );
            keysToPanel.remove( key );
        }

        public boolean contains(PlaceRequest key) {
            return keysToPanel.containsKey( key );
        }

        public void put(PlaceRequest key,
                        Panel panel) {
            keys.add( key );
            keysToPanel.put( key,
                             panel );
        }

        public int getIndex(PlaceRequest key) {
            return keys.indexOf( key );
        }
    }
}
