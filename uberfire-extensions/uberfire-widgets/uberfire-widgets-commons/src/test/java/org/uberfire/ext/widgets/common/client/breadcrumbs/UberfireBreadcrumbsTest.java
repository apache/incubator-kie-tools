package org.uberfire.ext.widgets.common.client.breadcrumbs;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbsPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class UberfireBreadcrumbsTest {

    @Mock
    private UberfireDocksContainer uberfireDocksContainer;

    @Mock
    private ManagedInstance<BreadcrumbsPresenter> breadcrumbsPresenters;

    @Mock
    private PlaceManager placeManager;

    private UberfireBreadcrumbs uberfireBreadcrumbs;
    private UberfireBreadcrumbs.View view;


    @Before
    public void setup() {

        when( breadcrumbsPresenters.get() ).thenReturn( mock( BreadcrumbsPresenter.class ) )
                .thenReturn( mock( BreadcrumbsPresenter.class ) ).thenReturn( mock( BreadcrumbsPresenter.class ) );

        view = mock( UberfireBreadcrumbs.View.class );
        uberfireBreadcrumbs = new UberfireBreadcrumbs( uberfireDocksContainer,
                                                       breadcrumbsPresenters,
                                                       placeManager,
                                                       view ) {
        };
    }

    @Test
    public void setupTest() {
        uberfireBreadcrumbs.setup( new UberfireDockContainerReadyEvent() );

        assertNotNull( uberfireBreadcrumbs );
        verify( uberfireDocksContainer ).addBreadcrumbs( any( IsElement.class ), eq( UberfireBreadcrumbs.SIZE ) );
        verify( uberfireDocksContainer ).hide( any( IsElement.class ) );
    }

    @Test
    public void addRootBreadCrumb() {
        assertTrue( uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty() );

        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.createRoot( "myperspective", "label", new DefaultPlaceRequest( "screen" ) );
        uberfireBreadcrumbs
                .navigateTo( "label2", new DefaultPlaceRequest( "screen2" ), mock( HasWidgets.class ) );
        uberfireBreadcrumbs.navigateTo( "label3", new DefaultPlaceRequest( "screen3" ) );
        uberfireBreadcrumbs.createRoot( "myperspective2", "label4", new DefaultPlaceRequest( "screen4" ) );


        assertTrue( !uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty() );
        assertEquals( 2, uberfireBreadcrumbs.breadcrumbsPerPerspective.size() );
        assertEquals( 3, uberfireBreadcrumbs.breadcrumbsPerPerspective.get( "myperspective" ).size() );
    }

    @Test
    public void addRootBreadCrumbWithGoTo() {
        assertTrue( uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty() );

        uberfireBreadcrumbs.currentPerspective = "myperspective";
        DefaultPlaceRequest placeRequest = new DefaultPlaceRequest( "screen" );
        uberfireBreadcrumbs.createRoot( "myperspective", "label", placeRequest, true );

        verify( placeManager ).goTo( placeRequest );

    }

    @Test
    public void perspectiveChangeEventShouldHideWhenThereIsNoBreadCrumb() {
        uberfireBreadcrumbs.createRoot( "myperspective", "label", new DefaultPlaceRequest( "screen" ) );

        PerspectiveChange perspectiveChange = new PerspectiveChange( null, null, null, "nop" );

        uberfireBreadcrumbs.perspectiveChangeEvent( perspectiveChange );

        verify( uberfireDocksContainer ).hide( any( IsElement.class ) );
    }

    @Test
    public void perspectiveChangeEventShouldShowWhenThereIsBreadCrumb() {
        uberfireBreadcrumbs.createRoot( "myperspective", "label", new DefaultPlaceRequest( "screen" ) );

        PerspectiveChange perspectiveChange = new PerspectiveChange( null, null, null, "myperspective" );

        uberfireBreadcrumbs.perspectiveChangeEvent( perspectiveChange );

        verify( uberfireDocksContainer ).show( any( IsElement.class ) );
    }

    @Test
    public void removeDeepLevelBreadcrumbsTest() {
        uberfireBreadcrumbs.createRoot( "myperspective", "label", new DefaultPlaceRequest( "screen" ) );
        uberfireBreadcrumbs
                .navigateTo( "label2", new DefaultPlaceRequest( "screen2" ), mock( HasWidgets.class ) );
        uberfireBreadcrumbs.navigateTo( "label3", new DefaultPlaceRequest( "screen3" ) );

        List<BreadcrumbsPresenter> breadcrumbs = uberfireBreadcrumbs.getBreadcrumbsPresenters( "myperspective" );

        uberfireBreadcrumbs.removeDeepLevelBreadcrumbs( "myperspective", breadcrumbs.get( 0 ) );

        assertEquals( 1, uberfireBreadcrumbs.getBreadcrumbsPresenters( "myperspective" ).size() );
    }

    @Test
    public void generateBreadCrumbSelectCommandTest() {
        DefaultPlaceRequest placeRequest = new DefaultPlaceRequest( "screen" );
        uberfireBreadcrumbs.createRoot( "myperspective", "label", placeRequest );

        List<BreadcrumbsPresenter> breadcrumbs = uberfireBreadcrumbs.getBreadcrumbsPresenters( "myperspective" );

        BreadcrumbsPresenter breadcrumb = breadcrumbs.get( 0 );


        uberfireBreadcrumbs.generateBreadCrumbSelectCommand( "myperspective",
                                                             breadcrumb,
                                                             placeRequest,
                                                             Optional.empty() ).execute();

        verify( placeManager ).goTo( placeRequest );
        verify( placeManager, never() ).goTo( eq( placeRequest ), any( HasWidgets.class ) );
    }

    @Test
    public void generateBreadCrumbSelectCommandWithTargetPanelTest() {
        DefaultPlaceRequest placeRequest = new DefaultPlaceRequest( "screen" );
        uberfireBreadcrumbs.createRoot( "myperspective", "label", placeRequest );

        List<BreadcrumbsPresenter> breadcrumbs = uberfireBreadcrumbs.getBreadcrumbsPresenters( "myperspective" );

        BreadcrumbsPresenter breadcrumb = breadcrumbs.get( 0 );


        uberfireBreadcrumbs.generateBreadCrumbSelectCommand( "myperspective",
                                                             breadcrumb,
                                                             placeRequest,
                                                             Optional.of( mock( HasWidgets.class ) ) ).execute();

        verify( placeManager ).goTo( eq( placeRequest ), any( HasWidgets.class ) );
        verify( placeManager, never() ).goTo( placeRequest );
    }

    @Test
    public void getViewShouldAddInnerBreadCrumbsTest() {

        List<BreadcrumbsPresenter> breadcrumbs = Arrays
                .asList( mock( BreadcrumbsPresenter.class ), mock( BreadcrumbsPresenter.class ) );
        uberfireBreadcrumbs.breadcrumbsPerPerspective.put( "myperspective", breadcrumbs );

        uberfireBreadcrumbs.getView();

        verify( view ).clear();
        verify( view, never() ).add( any( UberElement.class ) );

        uberfireBreadcrumbs.currentPerspective = "myperspective";

        uberfireBreadcrumbs.getView();

        verify( view, times( 2 ) ).add( any( UberElement.class ) );
    }

}