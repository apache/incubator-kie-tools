package org.uberfire.wbtest.gwttest;

import static org.uberfire.debug.Debug.*;
import static org.uberfire.wbtest.gwttest.TestingPredicates.*;

import java.util.Arrays;
import java.util.Collection;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.marshalling.client.api.MarshallerFramework;
import org.jboss.errai.security.client.local.api.SecurityContext;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.UserCookieEncoder;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.wbtest.client.menus.MenuBarTestScreen;

import com.google.common.base.Predicate;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Tests for the visibility of menus based on the current user's role.
 */
public class MenuAuthorizationTest extends AbstractUberFireGwtTest {

    private PlaceManager placeManager;
    private SecurityContext securityContext;

    private static final Predicate<Void> MENU_SCREEN_NOT_VISIBLE = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            return DOM.getElementById( shortName( MenuBarTestScreen.class ) ) == null;
        }
    };
    private User admin;

    @Override
    protected void gwtSetUp() throws Exception {
        MarshallerFramework.initializeDefaultSessionProvider();

        // because UberFire uses @Inject User, the only way we can set the current user is by putting this
        // cookie in place before the GWT modules get bootstrapped (so before super.gwtSetUp())
        Collection<? extends Role> roles = Arrays.asList( new RoleImpl( "admin" ) );
        admin = new UserImpl( "admin", roles );
        Cookies.setCookie( UserCookieEncoder.USER_COOKIE_NAME, UserCookieEncoder.toCookieValue( admin ) );

        super.gwtSetUp();
        placeManager = IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance();
        securityContext = IOC.getBeanManager().lookupBean( SecurityContext.class ).getInstance();
    }

    /**
     * Tests that we remain on the current perspective when the requested one can't be started.
     */
    public void testAdminSeesCorrectMenus() throws Exception {
        debugAsyncTesting = true;

        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertNotNull( securityContext.getCachedUser() );
                assertEquals( admin, securityContext.getCachedUser() );
                placeManager.goTo( MenuBarTestScreen.class.getName() );
            }
        } )
        .thenPollWhile( MENU_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertMenuVisible( MenuBarTestScreen.ADMIN_MENU_LABEL );
                assertMenuVisible( MenuBarTestScreen.STAFF_AND_ADMIN_MENU_LABEL );
                assertMenuVisible( MenuBarTestScreen.UNRESTRICTED_MENU_LABEL );
                assertMenuNotVisible( MenuBarTestScreen.STAFF_MENU_LABEL );
            }
        } );
    }

    static void assertMenuVisible( String label ) {
        if ( !menuVisible( DOM.getElementById( shortName( MenuBarTestScreen.class ) ), label ) ) {
            fail( "Menu with label \"" + label + "\" is not visible" );
        }
    }

    static void assertMenuNotVisible( String label ) {
        if ( menuVisible( DOM.getElementById( shortName( MenuBarTestScreen.class ) ), label ) ) {
            fail( "Menu with label \"" + label + "\" is visible" );
        }
    }

    private static boolean menuVisible( Element searchFrom, String label ) {
        System.out.println("Looking for an <li> with text " + label);
        NodeList<com.google.gwt.dom.client.Element> liElems = searchFrom.getElementsByTagName( "li" );
        for ( int i = 0, n = liElems.getLength(); i < n; i++ ) {
            com.google.gwt.dom.client.Element item = liElems.getItem( i );
            if ( item.getInnerText().contains( label ) ) {
                System.out.println("Found: " + item);
                return true;
            } else {
                System.out.println("Not this one!");
            }
        }
        System.out.println("Not Found!");
        return false;
    }

}
