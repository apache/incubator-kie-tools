package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.UserCookieEncoder;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.uberfire.wbtest.client.security.InjectedUserScreen;
import org.uberfire.wbtest.client.security.SecurityContextInfoScreen;


public class UserCookieTest extends AbstractSeleniumTest {

    @Before
    public void setup() {
        MappingContextSingleton.loadDynamicMarshallers();
    }

    @Test
    public void testGetInfoFromSecurityContext() throws Exception {
        User user = makeTestUser();
        String jsonUser = UserCookieEncoder.toCookieValue( user );

        // go to the site and set the user cookie (the security filter would normally do this upon login)
        driver.get( baseUrl + "blank.html" );
        driver.manage().addCookie( new Cookie( UserCookieEncoder.USER_COOKIE_NAME,
                                               jsonUser,
                                               "/",
                                               new Date( System.currentTimeMillis() + 1000 * 60 * 24 ) ) );

        // now go to the app with the cookie in place
        driver.get( baseUrl );
        driver.get( baseUrl + "#" + SecurityContextInfoScreen.class.getName() );

        WebElement userLabel = driver.findElement( By.id( "gwt-debug-SecurityStatusScreen-userLabel" ) );
        assertEquals( user.getIdentifier(), userLabel.getText() );

        WebElement rolesLabel = driver.findElement( By.id( "gwt-debug-SecurityStatusScreen-rolesLabel" ) );
        assertEquals( user.getRoles().toString(), rolesLabel.getText() );

        WebElement groupsLabel = driver.findElement( By.id( "gwt-debug-SecurityStatusScreen-groupsLabel" ) );
        assertEquals( user.getGroups().toString(), groupsLabel.getText() );
    }

    @Test
    public void testGetInfoFromInjectedUser() throws Exception {
        User user = makeTestUser();
        String jsonUser = UserCookieEncoder.toCookieValue( user );

        // go to the site and set the user cookie (the security filter would normally do this upon login)
        driver.get( baseUrl + "blank.html" );
        driver.manage().addCookie( new Cookie( UserCookieEncoder.USER_COOKIE_NAME,
                                               jsonUser,
                                               "/",
                                               new Date( System.currentTimeMillis() + 1000 * 60 * 24 ) ) );

        // now go to the app with the cookie in place
        driver.get( baseUrl );
        driver.get( baseUrl + "#" + InjectedUserScreen.class.getName() );

        WebElement userLabel = driver.findElement( By.id( "gwt-debug-SecurityStatusScreen-userLabel" ) );
        assertEquals( user.getIdentifier(), userLabel.getText() );

        WebElement rolesLabel = driver.findElement( By.id( "gwt-debug-SecurityStatusScreen-rolesLabel" ) );
        assertEquals( user.getRoles().toString(), rolesLabel.getText() );

        WebElement groupsLabel = driver.findElement( By.id( "gwt-debug-SecurityStatusScreen-groupsLabel" ) );
        assertEquals( user.getGroups().toString(), groupsLabel.getText() );
    }

    private User makeTestUser() {
        Collection<Role> roles = new ArrayList<Role>();
        roles.add( new RoleImpl( "role-1" ) );

        Collection<Group> groups = new ArrayList<Group>();
        groups.add( new GroupImpl( "group" ) );

        User user = new UserImpl( "testing-user", roles, groups );
        return user;
    }
}
