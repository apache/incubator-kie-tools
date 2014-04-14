package org.uberfire.security.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.uberfire.security.server.FormAuthenticationScheme.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.errai.security.shared.api.UserCookieEncoder;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.picketlink.idm.model.basic.User;

public class FormBasedLoginTest extends BaseSecurityFilterTest {

    /**
     * Turns the request field into a login request as recognized by our form-based login scheme.
     * 
     * @param username the username the auth scheme should see
     * @param password the password the auth scheme should see
     */
    private void setRequestAsLogin( String username, String password ) {
        request.setServletPath( HTTP_FORM_SECURITY_CHECK_URI );
        request.setRequestURI( contextPath + HTTP_FORM_SECURITY_CHECK_URI );
        request.setParameter( HTTP_FORM_USERNAME_PARAM, username );
        request.setParameter( HTTP_FORM_PASSWORD_PARAM, password );
    }

    /**
     * The client-side of the security framework watches for 4xx errors on ErraiBus communication attempts, and it
     * does a redirect to the login page when that happens. This test ensures unauthenticated ErraiBus requests
     * result in a 403 error.
     */
    @Test
    public void test403WhenNotAuthenticated() throws Exception {

        request.setServletPath("/in.erraiBus");
        request.setRequestURI("/test-context/in.erraiBus");

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
        verify( response ).sendError( eq( 403 ) );
    }

    @Test
    public void shouldPassAuthenticatedRequestsThrough() throws Exception {

        // given: user is logged in
        identity.setLoggedInUser(new User("previously_logged_in"));

        request.setServletPath("/in.erraiBus");
        request.setRequestURI("/test-context/in.erraiBus");

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

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

        final String hostPageUri = "/MyGwtModule/MyGwtHostPage.html";
        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );

        setRequestAsLogin( "username", "password" );

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response ).sendRedirect( contextPath + hostPageUri );

        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    @Test
    public void successfulFormBasedLoginShouldGetUserCookieInResponse() throws Exception {
        final UserImpl user = new UserImpl( "this is the username we logged in with" );

        setRequestAsLogin( user.getIdentifier(), "password" );

        when( authService.getUser() ).thenReturn( user );
        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendError( anyInt(), anyString() );

        ArgumentCaptor<Cookie> cookieMonster = ArgumentCaptor.forClass( Cookie.class );
        verify( response ).addCookie( cookieMonster.capture() );
        Cookie cookie = cookieMonster.getValue();
        assertEquals( UserCookieEncoder.USER_COOKIE_NAME, cookie.getName() );
        assertTrue("Cookie value is missing username: " + cookie.getValue(),
                cookie.getValue().contains( user.getIdentifier() ) );
    }

    @Test
    public void newLoginAttemptShouldTakePrecedenceOverExistingSessionData() throws Exception {

        final String hostPageUri = "/MyGwtModule/MyGwtHostPage.html";
        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );

        // given: user is logged in
        identity.setLoggedInUser(new User("previously_logged_in"));

        setRequestAsLogin( "logged_in_via_form", "logged_in_via_form" );

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

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
        final String hostPageUri = "/host-page.html";

        // given: user is logged in
        identity.setLoggedInUser(new User("previously_logged_in"));

        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );

        request.setServletPath("");
        request.setRequestURI(contextPath + hostPageUri);

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

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
        final String hostPageUri = "/HostPage-uri.html";

        // given: user is logged in
        identity.setLoggedInUser(new User("previously_logged_in"));

        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );

        request.setServletPath("");
        request.setRequestURI(contextPath + "/foo.css");

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        // the host page redirect must not have happened (it would be a loop)
        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response, never() ).sendRedirect( contextPath + hostPageUri ); // redundant but has a better failure message
        verify( response, never() ).sendRedirect( anyString() );
        verify( filterChain ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );

    }

    @Test
    public void shouldRedirectToLoginPageUponUnauthenticatedRequestToHostPage() throws Exception {
        final String hostPageUri = "/HostPage-uri.html";
        final String loginPageUri = "/login.jsp";

        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );
        filterConfig.initParams.put( LOGIN_PAGE_INIT_PARAM, loginPageUri );

        request.setRequestURI(contextPath + hostPageUri);

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        verify( response ).sendRedirect( contextPath + loginPageUri );
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    @Test
    public void shouldRedirectToBackToLoginPageUponFailedLogin() throws Exception {
        final String loginPageUri = "/login.jsp";

        filterConfig.initParams.put( LOGIN_PAGE_INIT_PARAM, loginPageUri );

        setRequestAsLogin( "username", "password" );

        identity.setAllowsLogins( false );

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        verify( response ).sendRedirect( contextPath + loginPageUri + "?" + LOGIN_ERROR_QUERY_PARAM + "=true" );
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    @Test
    public void shouldEchoBackQueryParametersOnSuccessfulLogin() throws Exception {
        final String loginPageUri = "/login.jsp";

        filterConfig.initParams.put( LOGIN_PAGE_INIT_PARAM, loginPageUri );

        setRequestAsLogin( "username", "password" );
        request.setParameter( "extra=Param", "extraParam&Value" );
        request.setParameter( "extra?Param2", "extraParam<Value2" );

        final ArgumentCaptor<String> responseStringCaptor = ArgumentCaptor.forClass( String.class );

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        verify( response ).sendRedirect( responseStringCaptor.capture() );
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );

        assertTrue( responseStringCaptor.getValue().contains("extra%3DParam=extraParam%26Value") );
        assertTrue( responseStringCaptor.getValue().contains("extra%3FParam2=extraParam%3CValue2") );
    }

    @Test
    public void shouldEchoBackQueryParametersOnFailedLogin() throws Exception {
        final String loginPageUri = "/login.jsp";

        filterConfig.initParams.put( LOGIN_PAGE_INIT_PARAM, loginPageUri );

        setRequestAsLogin( "username", "password" );
        request.setParameter( "extra=Param", "extraParam&Value" );
        request.setParameter( "extra?Param2", "extraParam<Value2" );

        identity.setAllowsLogins( false );

        final ArgumentCaptor<String> responseStringCaptor = ArgumentCaptor.forClass( String.class );

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        verify( response ).sendRedirect( responseStringCaptor.capture() );
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );

        assertTrue( responseStringCaptor.getValue().contains("extra%3DParam=extraParam%26Value") );
        assertTrue( responseStringCaptor.getValue().contains("extra%3FParam2=extraParam%3CValue2") );
    }

    @Test
    public void shouldEchoBackQueryParametersOnRedirectFromHostPageToLoginPage() throws Exception {
        final String hostPageUri = "/gwt_host_page.html";
        final String loginPageUri = "/login.jsp";

        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, hostPageUri );
        filterConfig.initParams.put( LOGIN_PAGE_INIT_PARAM, loginPageUri );

        request.setRequestURI( contextPath + hostPageUri );
        request.setParameter( "extra=Param", "extraParam&Value" );
        request.setParameter( "extra?Param2", "extraParam<Value2" );

        final ArgumentCaptor<String> responseStringCaptor = ArgumentCaptor.forClass( String.class );

        authFilter.init( filterConfig );
        authFilter.doFilter( request, response, filterChain );

        verify( response ).sendRedirect( responseStringCaptor.capture() );
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );

        assertTrue( responseStringCaptor.getValue().contains("extra%3DParam=extraParam%26Value") );
        assertTrue( responseStringCaptor.getValue().contains("extra%3FParam2=extraParam%3CValue2") );

        // this is not a login error, so ensure the error param is not present
        assertFalse( responseStringCaptor.getValue().contains( LOGIN_ERROR_QUERY_PARAM ) );
    }

}
