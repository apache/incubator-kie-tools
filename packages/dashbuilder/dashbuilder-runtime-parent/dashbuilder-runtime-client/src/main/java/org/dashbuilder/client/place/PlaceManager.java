package org.dashbuilder.client.place;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.dashbuilder.client.screens.ContentErrorScreen;
import org.dashbuilder.client.screens.DashboardsListScreen;
import org.dashbuilder.client.screens.EmptyScreen;
import org.dashbuilder.client.screens.NotFoundScreen;
import org.dashbuilder.client.screens.Router;
import org.dashbuilder.client.screens.RuntimeScreen;
import org.dashbuilder.client.screens.SamplesScreen;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;

/**
 * 
 * Class responsible to load a specific place
 *
 */
@ApplicationScoped
public class PlaceManager {

    Map<String, Place> screens;

    @Inject
    EmptyScreen emptyScreen;

    @Inject
    ContentErrorScreen contentErrorScreen;

    @Inject
    DashboardsListScreen dashboardsListScreen;

    @Inject
    NotFoundScreen notFoundScreen;

    @Inject
    RuntimeScreen runtimeScreen;

    @Inject
    SamplesScreen samplesScreen;

    @Inject
    Router routerScreen;

    private Place currentPlace;

    @Inject
    Elemental2DomUtil domUtil;

    Place defaultPlace;    

    private Element root;

    @PostConstruct
    void init() {
        screens = new HashMap<>();

        Stream.of(emptyScreen,
                contentErrorScreen,
                dashboardsListScreen,
                notFoundScreen,
                runtimeScreen,
                samplesScreen)
                .forEach(place -> screens.put(place.getId(), place));

    }

    public void goTo(String id) {
        if (root == null) {
            throw new IllegalStateException("Place Manager is missing setup.");
        }
        var screen = screens.get(id);

        if (screen != null) {
            screen.onOpen();
            // go to Screen
            domUtil.removeAllElementChildren(root);
            root.appendChild(screen.getElement());
            if (this.currentPlace != null) {
                this.currentPlace.onClose();
            }
            this.currentPlace = screen;
        }
    }

    public void setup(Element root) {
        this.root = root;
    }

}
