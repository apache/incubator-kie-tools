/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.breadcrumbs;

import com.google.gwt.user.client.ui.HasWidgets;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbsPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;

/**
 * A container for breadcrumbs and a toolbar area.
 * Allows the applications to add breadcrumbs to
 * any displayable thing: a {@link WorkbenchPerspective},
 * a {@link WorkbenchScreen} a {@link WorkbenchEditor},
 * or the editor associated with a VFS file located at a
 * particular {@link Path} through a {@link PathPlaceRequest}.
 * Also has a toolbar area, placed in the right side of bread, that allows
 * applications to add {@link Element} to Uberfire Breadcrumbs.
 */

@EntryPoint
public class UberfireBreadcrumbs {

    public interface View extends UberElement<UberfireBreadcrumbs> {

        void clear();

        void addBreadcrumb( UberElement<BreadcrumbsPresenter> view );

        void addBreadcrumbToolbar( Element uberElement );
    }

    public static final double SIZE = 35.0;

    String currentPerspective;

    final Map<String, List<BreadcrumbsPresenter>> breadcrumbsPerPerspective = new HashMap<>();

    final Map<String, Element> breadcrumbsToolBarPerPerspective = new HashMap<>();

    private final UberfireDocksContainer uberfireDocksContainer;

    private ManagedInstance<BreadcrumbsPresenter> breadcrumbsPresenters;

    private PlaceManager placeManager;

    private final View view;

    @Inject
    public UberfireBreadcrumbs( UberfireDocksContainer uberfireDocksContainer,
                                ManagedInstance<BreadcrumbsPresenter> breadcrumbsPresenters,
                                PlaceManager placeManager,
                                View view ) {
        this.uberfireDocksContainer = uberfireDocksContainer;
        this.breadcrumbsPresenters = breadcrumbsPresenters;
        this.placeManager = placeManager;
        this.view = view;
    }

    void perspectiveChangeEvent( @Observes PerspectiveChange perspectiveChange ) {
        currentPerspective = perspectiveChange.getIdentifier();
        updateView();
    }

    void setup( @Observes UberfireDockContainerReadyEvent event ) {
        createBreadCrumbs();
    }

    private void createBreadCrumbs() {
        uberfireDocksContainer.addBreadcrumbs( getView(), SIZE );
    }


    /**
     * Clear the breadcrumbs and toolbars
     * associated with a perspective.
     *
     * @param associatedPerspective perspective associated with the breadcrumb
     */
    public void clearBreadCrumbsAndToolBars( String associatedPerspective ) {
        breadcrumbsPerPerspective.put( associatedPerspective, new ArrayList<>() );
        breadcrumbsToolBarPerPerspective.remove( associatedPerspective );
    }

    /**
     * Creates a breadcrumb associated with a perspective
     * and Place Request.
     *
     * @param associatedPerspective  perspective associated with the breadcrumb
     * @param breadCrumbLabel        label of the breadcrumb
     * @param associatedPlaceRequest place request associated with the breadcrumb
     */
    public void addBreadCrumb( String associatedPerspective, String breadCrumbLabel,
                               DefaultPlaceRequest associatedPlaceRequest ) {
        addBreadCrumb( associatedPerspective, breadCrumbLabel, associatedPlaceRequest, Optional.empty() );
    }

    /**
     * Creates a breadcrumb associated with a perspective
     * a Place Request and a target panel.
     *
     * @param associatedPerspective  perspective associated with the breadcrumb
     * @param breadCrumbLabel        label of the breadcrumb
     * @param associatedPlaceRequest place request associated with the breadcrumb
     * @param addTo                  target content panel of the place request
     */
    public void addBreadCrumb( String associatedPerspective, String breadCrumbLabel,
                               DefaultPlaceRequest associatedPlaceRequest,
                               Optional<HasWidgets> addTo ) {
        List<BreadcrumbsPresenter> breadCrumbs = getBreadcrumbs( associatedPerspective );
        deactivateLastBreadcrumb( breadCrumbs );
        breadCrumbs.add( createBreadCrumb( associatedPerspective, breadCrumbLabel, associatedPlaceRequest, addTo ) );
        breadcrumbsPerPerspective.put( associatedPerspective, breadCrumbs );
        if ( currentPerspective == associatedPerspective ) {
            updateView();
        }
    }

    /**
     * Add a toolbar to a a perspective.
     * Toolbar is placed in the right side of
     * breadcrumbs area.
     *
     * @param associatedPerspective perspective associated with the toolbar
     * @param toolbar               toolbar that will be added
     */
    public void addToolbar( String associatedPerspective, Element toolbar ) {
        breadcrumbsToolBarPerPerspective.put( associatedPerspective, toolbar );
        if ( currentPerspective == associatedPerspective ) {
            updateView();
        }
    }

    private List<BreadcrumbsPresenter> getBreadcrumbs( String perspective ) {
        List<BreadcrumbsPresenter> breadCrumbs = breadcrumbsPerPerspective.get( perspective );
        if ( breadCrumbs == null ) {
            breadCrumbs = new ArrayList<>();
        }
        return breadCrumbs;
    }

    private void deactivateLastBreadcrumb( List<BreadcrumbsPresenter> breadCrumbs ) {
        if ( !breadCrumbs.isEmpty() ) {
            breadCrumbs.get( breadCrumbs.size() - 1 ).deactivate();
        }
    }

    private BreadcrumbsPresenter createBreadCrumb( String perspective,
                                                   String label,
                                                   DefaultPlaceRequest placeRequest,
                                                   Optional<HasWidgets> addTo ) {

        BreadcrumbsPresenter breadCrumb = breadcrumbsPresenters.get();
        breadCrumb.setup( label, placeRequest,
                          generateBreadCrumbSelectCommand( perspective, breadCrumb, placeRequest, addTo ) );
        breadCrumb.activate();
        return breadCrumb;
    }

    Command generateBreadCrumbSelectCommand( String perspective,
                                             BreadcrumbsPresenter breadCrumb,
                                             DefaultPlaceRequest placeRequest, Optional<HasWidgets> addTo ) {
        return () -> {
            removeDeepLevelBreadcrumbs( perspective, breadCrumb );
            breadCrumb.activate();
            goToBreadCrumb( placeRequest, addTo );
            updateView();
        };
    }

    private void goToBreadCrumb( DefaultPlaceRequest placeRequest, Optional<HasWidgets> addTo ) {
        if ( addTo.isPresent() ) {
            placeManager.goTo( placeRequest, addTo.get() );
        } else {
            placeManager.goTo( placeRequest );
        }
    }

    void removeDeepLevelBreadcrumbs( String perspective, BreadcrumbsPresenter breadCrumb ) {
        List<BreadcrumbsPresenter> breadCrumbs = breadcrumbsPerPerspective.get( perspective );
        if ( breadCrumbs != null ) {
            Predicate<BreadcrumbsPresenter> toRemovePredicate = current ->
                    breadCrumbs.indexOf( current ) > breadCrumbs.indexOf( breadCrumb );
            breadCrumbs.removeIf( toRemovePredicate );
        }
    }

    private void updateView() {
        getView();
    }

    View getView() {
        view.clear();
        if ( thereIsBreadcrumbsFor( currentPerspective ) ) {
            breadcrumbsPerPerspective.get( currentPerspective )
                    .stream()
                    .forEach( p -> view.addBreadcrumb( p.getView() ) );
        }
        if ( thereIsBreadcrumbToolbarFor( currentPerspective ) ) {
            view.addBreadcrumbToolbar( breadcrumbsToolBarPerPerspective.get( currentPerspective ) );
        }
        return view;
    }

    private boolean thereIsBreadcrumbsFor( String perspective ) {
        return breadcrumbsPerPerspective.containsKey( perspective );
    }

    private boolean thereIsBreadcrumbToolbarFor( String perspective ) {
        return breadcrumbsToolBarPerPerspective.containsKey( perspective );
    }

}
