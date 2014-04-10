package org.uberfire.security.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.uberfire.security.server.FormAuthenticationScheme.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.picketlink.idm.model.basic.User;

public class FormBasedLoginTest extends BaseSecurityFilterTest {

    /**
     * The client-side of the security framework watches for 4xx errors on ErraiBus communication attempts, and it
     * does a redirect to the login page when that happens. This test ensures unauthenticated ErraiBus requests
     * result in a 403 error.
     */
    @Test
    public void test403WhenNotAuthenticated() throws Exception {

        when( request.getServletPath() ).thenReturn( "/in.erraiBus" );
        when( request.getRequestURI() ).thenReturn( "/test-context/in.erraiBus" );

        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
        verify( response ).sendError( eq( 403 ) );
    }

    @Test
    public void shouldPassAuthenticatedRequestsThrough() throws Exception {

        // given: user is logged in
        identity.setLoggedInUser(new User("previously_logged_in"));

        when( request.getServletPath() ).thenReturn( "/in.erraiBus" );
        when( request.getRequestURI() ).thenReturn( "/test-context/in.erraiBus" );

        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // make sure the filter didn't commit the response, with specific checks for the most likely reasons it might have
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendRedirect( anyString() );
        assertFalse( response.isCommitted() );

        // and most importantly, it passed the request up the filter chain!
        verify( filterChain ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    /**
     * This test protects the redirect-after-login behaviour that the uberfire-tutorial project relies on.
     */
    @Test
    public void successfulFormBasedLoginShouldRedirectToHostPageUrl() throws Exception {

        final String contextPath = "/test-context";
        final String hostPageUri = "/MyGwtModule/MyGwtHostPage.html";
        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );

        filterConfig.setContextPath( contextPath );
        when( request.getServletPath() ).thenReturn( HTTP_FORM_SECURITY_CHECK_URI );
        when( request.getRequestURI() ).thenReturn( contextPath + HTTP_FORM_SECURITY_CHECK_URI );
        when( request.getParameter( HTTP_FORM_USERNAME_PARAM )).thenReturn( "username" );
        when( request.getParameter( HTTP_FORM_PASSWORD_PARAM )).thenReturn( "password" );

        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response ).sendRedirect( contextPath + hostPageUri );

        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    @Test
    public void newLoginAttemptShouldTakePrecedenceOverExistingSessionData() throws Exception {

        final String contextPath = "/test-context";
        final String hostPageUri = "/MyGwtModule/MyGwtHostPage.html";
        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );
        filterConfig.setContextPath( contextPath );

        // given: user is logged in
        identity.setLoggedInUser(new User("previously_logged_in"));

        when( request.getServletPath() ).thenReturn( HTTP_FORM_SECURITY_CHECK_URI );
        when( request.getRequestURI() ).thenReturn( contextPath + HTTP_FORM_SECURITY_CHECK_URI );
        when( request.getParameter( HTTP_FORM_USERNAME_PARAM )).thenReturn( "logged_in_via_form" );
        when( request.getParameter( HTTP_FORM_PASSWORD_PARAM )).thenReturn( "logged_in_via_form" );

        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // the new form-based login attempt must take precedence over the existing session info
        assertEquals( "logged_in_via_form", ((User) identity.getAccount()).getLoginName() );

        // and the host page redirect should have happened too
        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response ).sendRedirect( contextPath + hostPageUri );
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    /**
     * The request in this test is not a login request; it's for an ongoing session. It targets the configured GWT host
     * page, which is actually a common scenario. This request must get through the filter without a redirect.
     */
    @Test
    public void authenticatedRequestToHostPageUrlShouldNotRedirectBackToItself() throws Exception {
        final String contextPath = "/test-context";
        final String hostPageUri = "/host-page.html";

        // given: user is logged in
        identity.setLoggedInUser(new User("previously_logged_in"));

        filterConfig.setContextPath( contextPath );
        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );

        when( request.getServletPath() ).thenReturn( "" );
        when( request.getRequestURI() ).thenReturn( contextPath + hostPageUri );

        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // the host page redirect must not have happened (it would be a loop)
        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response, never() ).sendRedirect( contextPath + hostPageUri ); // redundant but has a better failure message
        verify( response, never() ).sendRedirect( anyString() );
        verify( filterChain ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    /**
     * The request in this test is not a login request; it's for an ongoing session. It should not get redirected to the
     * GWT host page.
     */
    @Test
    public void authenticatedRequestToAnyUrlShouldNotRedirectToHostPageUrl() throws Exception {
        final String contextPath = "/test-context";
        final String HostPageUri = "/HostPage-uri.html";

        // given: user is logged in
        identity.setLoggedInUser(new User("previously_logged_in"));

        filterConfig.setContextPath( contextPath );
        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, HostPageUri );

        when( request.getServletPath() ).thenReturn( "" );
        when( request.getRequestURI() ).thenReturn( contextPath + "/foo.css" );

        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // the host page redirect must not have happened (it would be a loop)
        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response, never() ).sendRedirect( contextPath + HostPageUri ); // redundant but has a better failure message
        verify( response, never() ).sendRedirect( anyString() );
        verify( filterChain ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );

    }

}
