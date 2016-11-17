package org.uberfire.ext.widgets.common.client.breadcrumbs;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
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
    }

    @Test
    public void addToolbar() {
        assertTrue( uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty() );

        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.addToolbar( "myperspective", mock( Element.class ) );

        assertFalse( uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty() );
    }

    @Test
    public void addBreadCrumbs() {
        assertTrue( uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty() );

        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.addBreadCrumb( "myperspective", "label", new DefaultPlaceRequest( "screen" ) );
        uberfireBreadcrumbs
                .addBreadCrumb( "myperspective", "label2", new DefaultPlaceRequest( "screen2" ), Optional.empty() );
        uberfireBreadcrumbs.addBreadCrumb( "myperspective2", "label4", new DefaultPlaceRequest( "screen4" ) );


        assertFalse( uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty() );
        assertEquals( 2, uberfireBreadcrumbs.breadcrumbsPerPerspective.size() );
        assertEquals( 2, uberfireBreadcrumbs.breadcrumbsPerPerspective.get( "myperspective" ).size() );
    }


    @Test
    public void clearBreadCrumbs() {

        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.addToolbar( "myperspective", mock( Element.class ) );
        uberfireBreadcrumbs.addBreadCrumb( "myperspective", "label", new DefaultPlaceRequest( "screen" ) );

        assertFalse( uberfireBreadcrumbs.breadcrumbsPerPerspective.get( "myperspective" ).isEmpty() );
        assertFalse( uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty() );

        uberfireBreadcrumbs.clearBreadCrumbsAndToolBars( "myperspective" );

        assertTrue( uberfireBreadcrumbs.breadcrumbsPerPerspective.get( "myperspective" ).isEmpty() );
        assertTrue( uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty() );

    }

    @Test
    public void removeDeepLevelBreadcrumbsTest() {
        uberfireBreadcrumbs.addBreadCrumb( "myperspective", "label", new DefaultPlaceRequest( "screen" ) );
        uberfireBreadcrumbs
                .addBreadCrumb( "myperspective", "label2", new DefaultPlaceRequest( "screen2" ) );
        uberfireBreadcrumbs.addBreadCrumb( "myperspective", "label3", new DefaultPlaceRequest( "screen3" ) );

        List<BreadcrumbsPresenter> breadcrumbs = uberfireBreadcrumbs.breadcrumbsPerPerspective.get( "myperspective" );

        uberfireBreadcrumbs.removeDeepLevelBreadcrumbs( "myperspective", breadcrumbs.get( 0 ) );

        assertEquals( 1, uberfireBreadcrumbs.breadcrumbsPerPerspective.get( "myperspective" ).size() );
    }

    @Test
    public void generateBreadCrumbSelectCommandTest() {
        DefaultPlaceRequest placeRequest = new DefaultPlaceRequest( "screen" );
        uberfireBreadcrumbs.addBreadCrumb( "myperspective", "label", placeRequest );

        List<BreadcrumbsPresenter> breadcrumbs = uberfireBreadcrumbs.breadcrumbsPerPerspective.get( "myperspective" );

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
        uberfireBreadcrumbs.addBreadCrumb( "myperspective", "label", placeRequest );

        List<BreadcrumbsPresenter> breadcrumbs = uberfireBreadcrumbs.breadcrumbsPerPerspective.get( "myperspective" );

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
        uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.put( "myperspective", mock( Element.class ) );

        uberfireBreadcrumbs.getView();

        verify( view ).clear();
        verify( view, never() ).addBreadcrumb( any( UberElement.class ) );

        uberfireBreadcrumbs.currentPerspective = "myperspective";

        uberfireBreadcrumbs.getView();

        verify( view, times( 2 ) ).addBreadcrumb( any( UberElement.class ) );
        verify( view, times( 1 ) ).addBreadcrumbToolbar( any( Element.class ) );
    }

}