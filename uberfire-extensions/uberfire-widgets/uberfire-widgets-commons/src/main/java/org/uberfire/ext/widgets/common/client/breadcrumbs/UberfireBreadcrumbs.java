package org.uberfire.ext.widgets.common.client.breadcrumbs;

import com.google.gwt.user.client.ui.HasWidgets;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchScreen;
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
 * A breadcrumb navigation system. Allows the application to navigate into
 * any displayable thing: a {@link WorkbenchPerspective}, a {@link WorkbenchScreen} a
 * {@link WorkbenchEditor}, or the editor associated with a VFS file
 * located at a particular {@link Path} through a {@link PathPlaceRequest}.
 */
@EntryPoint
public class UberfireBreadcrumbs {

    private static final Logger LOG = LoggerFactory.getLogger( UberfireBreadcrumbs.class );

    public interface View extends UberElement<UberfireBreadcrumbs> {

        void clear();

        void add( UberElement<BreadcrumbsPresenter> view );
    }

    public static final double SIZE = 35.0;

    String currentPerspective;

    final Map<String, List<BreadcrumbsPresenter>> breadcrumbsPerPerspective = new HashMap<>();

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
        if ( breadcrumbsPerPerspective.containsKey( currentPerspective ) ) {
            uberfireDocksContainer.show( getView() );
        } else {
            uberfireDocksContainer.hide( getView() );
        }
    }

    void setup( @Observes UberfireDockContainerReadyEvent event ) {
        createBreadCrumbs();
    }

    private void createBreadCrumbs() {
        uberfireDocksContainer.addBreadcrumbs( getView(), SIZE );
        uberfireDocksContainer.hide( getView() );
    }

    /**
     * Creates a root breadcrumb associated with a perspective.
     *
     * @param associatedPerspective
     *              perspective associated with the root breadcrumb
     * @param breadCrumbLabel
     *              label of the root breadcrumb
     * @param associatedPlaceRequest
     *              place request associated with the root breadcrumb
     */
    public void createRoot( String associatedPerspective,
                            String breadCrumbLabel,
                            DefaultPlaceRequest associatedPlaceRequest ) {
        createRoot( associatedPerspective, breadCrumbLabel, associatedPlaceRequest, false );
    }

    /**
     * Creates a root breadcrumb associated with a perspective
     * and makes a placeManager.goTo to the associated Place Request.
     *
     * @param associatedPerspective
     *             perspective associated with the root breadcrumb
     * @param breadCrumbLabel
     *             label of the root breadcrumb
     * @param associatedPlaceRequest
     *             place request associated with the root breadcrumb
     * @param goToPlaceRequest
     *             should I do make a goto call after creating the root breacrumb?
     */
    public void createRoot( String associatedPerspective,
                            String breadCrumbLabel,
                            DefaultPlaceRequest associatedPlaceRequest,
                            boolean goToPlaceRequest ) {
        addBreadCrumb( associatedPerspective, breadCrumbLabel, associatedPlaceRequest, Optional.empty() );
        if ( goToPlaceRequest ) {
            goToBreadCrumb( associatedPlaceRequest, Optional.empty() );
        }
    }

    /**
     * Navigates to an associated place request.
     * Also, if it isn't the most recent breadcrumbs,
     * automatically remove the most recent and try to
     * close the place request associated with it.
     *
     * @param breadCrumbLabel
     *             label of the root breadcrumb
     * @param associatedPlaceRequest
     *             place request associated with the breadcrumb
     */
    public void navigateTo( String breadCrumbLabel, DefaultPlaceRequest associatedPlaceRequest ) {
        if ( thereIsBreadcrumbForCurrentPerspective() ) {
            addBreadCrumb( currentPerspective, breadCrumbLabel, associatedPlaceRequest, Optional.empty() );
            goToBreadCrumb( associatedPlaceRequest, Optional.empty() );
        } else {
            LOG.error( "There is no root breadcrumb associated with current perspective" );
        }
    }

    /**
     * Navigates to an associated place request and add it
     * to a specific content area.
     * Also, if it isn't the most recent breadcrumbs,
     * automatically remove the most recent and close the place request
     * associated with it.
     *
     * @param breadCrumbLabel
     *              label of the root breadcrumb
     * @param associatedPlaceRequest
     *              place request associated with the breadcrumb
     * @param addTo
     *              target content panel of the place request
     */
    public void navigateTo( String breadCrumbLabel, DefaultPlaceRequest associatedPlaceRequest, HasWidgets addTo ) {
        if ( thereIsBreadcrumbForCurrentPerspective() ) {
            addBreadCrumb( currentPerspective, breadCrumbLabel, associatedPlaceRequest, Optional.of( addTo ) );
            goToBreadCrumb( associatedPlaceRequest, Optional.of( addTo ) );
        } else {
            LOG.error( "There is no root breadcrumb associated with current perspective" );
        }
    }

    private boolean thereIsBreadcrumbForCurrentPerspective() {
        return currentPerspective != null & breadcrumbsPerPerspective.containsKey( currentPerspective );
    }

    private void addBreadCrumb( String perspective, String label, DefaultPlaceRequest placeRequest,
                                Optional<HasWidgets> addTo ) {
        List<BreadcrumbsPresenter> breadCrumbs = getBreadcrumbsPresenters( perspective );
        deactivateLastBreadcrumb( breadCrumbs );
        breadCrumbs.add( createBreadCrumb( perspective, label, placeRequest, addTo ) );
        breadcrumbsPerPerspective.put( perspective, breadCrumbs );
        updateView();
    }

    private void deactivateLastBreadcrumb( List<BreadcrumbsPresenter> breadCrumbs ) {
        if ( !breadCrumbs.isEmpty() ) {
            breadCrumbs.get( breadCrumbs.size() - 1 ).deactivate();
        }
    }

    List<BreadcrumbsPresenter> getBreadcrumbsPresenters( String perspective ) {
        List<BreadcrumbsPresenter> breadCrumbs = breadcrumbsPerPerspective.get( perspective );
        if ( breadCrumbs == null ) {
            breadCrumbs = new ArrayList<>();
        }
        return breadCrumbs;
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
            breadCrumbs.stream()
                    .filter( toRemovePredicate )
                    .forEach( b -> tryToClosePlace( b ) );
            breadCrumbs.removeIf( toRemovePredicate );
        }
    }

    private void tryToClosePlace( BreadcrumbsPresenter b ) {
        placeManager.tryClosePlace(
                b.getPlaceRequest(),
                () -> { } );
    }

    private void updateView() {
        getView();
    }

    View getView() {
        view.clear();
        if ( thereIsBreadcrumbForCurrentPerspective() ) {
            breadcrumbsPerPerspective.get( currentPerspective )
                    .stream()
                    .forEach( p -> view.add( p.getView() ) );
        }
        return view;
    }

}
